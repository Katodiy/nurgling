/*
 *  This file is part of the Haven & Hearth game client.
 *  Copyright (C) 2009 Fredrik Tolf <fredrik@dolda2000.com>, and
 *                     Björn Johannessen <johannessen.bjorn@gmail.com>
 *
 *  Redistribution and/or modification of this file is subject to the
 *  terms of the GNU Lesser General Public License, version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Other parts of this source tree adhere to other copying
 *  rights. Please see the file `COPYING' in the root directory of the
 *  source tree for details.
 *
 *  A copy the GNU Lesser General Public License is distributed along
 *  with the source tree of which this file is a part in the file
 *  `doc/LPGL-3'. If it is missing for any reason, please see the Free
 *  Software Foundation's website at <http://www.fsf.org/>, or write
 *  to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *  Boston, MA 02111-1307 USA
 */

package haven;

import java.util.*;
import java.util.function.*;
import java.lang.ref.*;
import haven.render.*;
import nurgling.NAlias;
import nurgling.NUtils;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

/* XXX: This whole file is a bit of a mess and could use a bit of a
 * rewrite some rainy day. Synchronization especially is quite hairy. */
public class MCache implements MapSource {
    public static final Coord2d tilesz = Coord2d.of(11, 11);
    public static final Coord tilesz2 = tilesz.round(); /* XXX: Remove me in due time. */
    public static final Coord cmaps = Coord.of(100, 100);
    public static final Coord cutsz = Coord.of(25, 25);
    public static final Coord cutn = cmaps.div(cutsz);
    private final Object setmon = new Object();
    private Resource.Spec[] nsets = new Resource.Spec[16];
    @SuppressWarnings("unchecked")
    private Reference<Resource>[] sets = new Reference[16];
    @SuppressWarnings("unchecked")
    private Reference<Tileset>[] csets = new Reference[16];
    @SuppressWarnings("unchecked")
    private Reference<Tiler>[] tiles = new Reference[16];
    private final Waitable.Queue gridwait = new Waitable.Queue();
    Map<Coord, Request> req = new HashMap<Coord, Request>();
    Map<Coord, Grid> grids = new HashMap<Coord, Grid>();
    Session sess;
    Set<Overlay> ols = new HashSet<Overlay>();
    public int olseq = 0, chseq = 0;
    Map<Integer, Defrag> fragbufs = new TreeMap<Integer, Defrag>();

    public static class LoadingMap extends Loading {
	public final Coord gc;
	private transient final MCache map;

	public LoadingMap(MCache map, Coord gc) {
	    super("Waiting for map data...");
	    this.gc = gc;
	    this.map = map;
	}

	public void waitfor(Runnable callback, Consumer<Waitable.Waiting> reg) {
	    synchronized(map.grids) {
		if(map.grids.containsKey(gc)) {
		    reg.accept(Waitable.Waiting.dummy);
		    callback.run();
		} else {
		    reg.accept(new Waitable.Checker(callback) {
			    protected Object monitor() {return(map.grids);}
			    double st = Utils.rtime();
			    protected boolean check() {
				if((Utils.rtime() - st > 5)) {
				    st = Utils.rtime();
				    return(true);
				}
				return(map.grids.containsKey(gc));
			    }
			    protected Waitable.Waiting add() {return(map.gridwait.add(this));}
			}.addi());
		}
	    }
	}
    }

    private static class Request {
	private long lastreq = 0;
	private int reqs = 0;
    }

    public static interface ZSurface {
	public default double getz(Coord tc) {
	    return(getz(tc.mul(tilesz)));
	}

	public default double getz(Coord2d pc) {
	    double tw = tilesz.x, th = tilesz.y;
	    Coord ul = Coord.of(Utils.floordiv(pc.x, tw), Utils.floordiv(pc.y, th));
	    double sx = (pc.x - (ul.x * tw)) / tw, ix = 1.0 - sx;
	    double sy = (pc.y - (ul.y * th)) / th, iy = 1.0 - sy;
	    try {
		return((iy * ((ix * getz(ul          )) + (sx * getz(ul.add(1, 0))))) +
		       (sy * ((ix * getz(ul.add(0, 1))) + (sx * getz(ul.add(1, 1))))));
	    } catch(ArrayIndexOutOfBoundsException e) {
		Debug.dump(pc, ul, sx, sy);
		throw(e);
	    }
	}

	public default Coord3f getnorm(Coord2d pc) {
	    return(getnormt(pc));
	}

	public default Coord3f getnormt(Coord2d pc) {
	    double tw = tilesz.x, th = tilesz.y;
	    Coord ul = Coord.of(Utils.floordiv(pc.x, tw), Utils.floordiv(pc.y, th));
	    double sx = (pc.x - (ul.x * tw)) / tw, ix = 1.0 - sx;
	    double sy = (pc.y - (ul.y * th)) / th, iy = 1.0 - sy;
	    double z0 = getz(ul), z1 = getz(ul.add(1, 0)), z2 = getz(ul.add(1, 1)), z3 = getz(ul.add(0, 1));
	    double nx = ((z1 * iy) + (z2 * sy)) - ((z0 * iy) + (z3 * sy));
	    double ny = ((z3 * iy) + (z2 * sy)) - ((z0 * iy) + (z1 * sy));
	    return(Coord3f.of((float)tw, 0, (float)nx).cmul(0, (float)th, (float)ny).norm());
	}

	public default Coord3f getnormp(Coord2d pc) {
	    double D = 0.01;
	    Coord2d tul = pc.sub(pc.mod(tilesz)), tbr = tul.add(tilesz);
	    double l = Math.max(pc.x - D, tul.x), u = Math.max(pc.y - D, tul.y);
	    double r = Math.min(pc.x + D, tbr.x), b = Math.min(pc.y + D, tbr.y);
	    double z0 = getz(Coord2d.of(pc.x, u));
	    double z1 = getz(Coord2d.of(r, pc.y));
	    double z2 = getz(Coord2d.of(pc.x, b));
	    double z3 = getz(Coord2d.of(l, pc.y));
	    return(Coord3f.of((float)(r - l), 0, (float)(z1 - z3)).cmul(0, (float)(b - u), (float)(z2 - z0)).norm());
	}
    }

    public static class SurfaceID {
	public final SurfaceID parent;

	public SurfaceID(SurfaceID parent) {
	    this.parent = parent;
	}

	public boolean hasparent(SurfaceID p) {
	    for(SurfaceID id = this; id != null; id = id.parent) {
		if(id == p)
		    return(true);
	    }
	    return(false);
	}

	public static final SurfaceID map = new SurfaceID(null);
	public static final SurfaceID trn = new SurfaceID(map);
    }

    public final Gob.Placer mapplace = new Gob.DefaultPlace(this, SurfaceID.map);
    public final Gob.Placer trnplace = new Gob.DefaultPlace(this, SurfaceID.trn);

    public static interface OverlayInfo {
	public Collection<String> tags();
	public Material mat();
	public default Material omat() {return(null);}
    }

    @Resource.LayerName("overlay")
    public static class ResOverlay extends Resource.Layer implements OverlayInfo {
	public final Collection<String> tags;
	private final int matid, omatid;

	public ResOverlay(Resource res, Message buf) {
	    res.super();
	    int ver = buf.uint8();
	    if(ver == 1) {
		int matid = 0, omatid = -1;
		Collection<String> tags = Collections.emptyList();
		Object[] data = buf.list();
		for(Object argp : data) {
		    Object[] arg = (Object[])argp;
		    switch((String)arg[0]) {
		    case "tags": {
			ArrayList<String> tbuf = new ArrayList<>();
			for(int i = 1; i < arg.length; i++)
			    tbuf.add(((String)arg[i]).intern());
			tbuf.trimToSize();
			tags = tbuf;
			break;
		    }
		    case "mat": {
			matid = (Integer)arg[1];
			break;
		    }
		    case "omat": {
			omatid = (Integer)arg[1];
			break;
		    }
		    }
		}
		this.matid = matid;
		this.omatid = omatid;
		this.tags = tags;
	    } else {
		throw(new Resource.LoadException("unknown overlay version: " + ver, res));
	    }
	}

	public void init() {
	}

	public Collection<String> tags() {
	    return(tags);
	}

	public Material mat() {
	    return(getres().flayer(Material.Res.class, matid).get());
	}
	public Material omat() {
	    if(omatid < 0)
		return(null);
	    return(getres().flayer(Material.Res.class, omatid).get());
	}

	public String toString() {
	    return(String.format("#<res-overlay %s %d>", getres().name, matid));
	}
    }

    public class Overlay {
	private Area a;
	private OverlayInfo id;

	public Overlay(Area a, OverlayInfo id) {
	    this.a = a;
	    this.id = id;
	    ols.add(this);
	    olseq++;
	}

	public void destroy() {
	    ols.remove(this);
	    olseq++;
	}

	public void update(Area a) {
	    if(!a.equals(this.a)) {
		olseq++;
		this.a = a;
	    }
	}
    }

	public class AreaOverlay extends Overlay{

		public AreaOverlay(Area a, OverlayInfo id, AreasID a_id) {
			super(a, id);
			this.a_id = a_id;
			first = Finder.findSign ( new NAlias( "iconsign" ), 5000, a_id );
			second = Finder.findSignButNotThis ( new NAlias( "iconsign" ), 5000, a_id , first);
		}
		AreasID a_id;
		Gob first;
		Gob second;

		public boolean tick(){
			return NUtils.getGob(first.id) == null || NUtils.getGob(second.id) == null;
		}
	}

    private void cktileid(int id) {
	if(id >= nsets.length) {
	    synchronized(setmon) {
		if(id >= nsets.length) {
		    nsets = Utils.extend(nsets, Integer.highestOneBit(id) * 2);
		    sets  = Utils.extend(sets,  Integer.highestOneBit(id) * 2);
		    csets = Utils.extend(csets, Integer.highestOneBit(id) * 2);
		    tiles = Utils.extend(tiles, Integer.highestOneBit(id) * 2);
		}
	    }
	}
    }

    public class Grid {
	public final Coord gc, ul;
	public final int tiles[] = new int[cmaps.x * cmaps.y];
	public final float z[] = new float[cmaps.x * cmaps.y];
	public Indir<Resource> ols[];
	public boolean ol[][];
	public long id;
	public int seq = -1;
	private int olseq = -1;
	private final Cut cuts[];
	private Flavobjs[] fo = new Flavobjs[cutn.x * cutn.y];

	private class Cut {
	    MapMesh mesh;
	    Defer.Future<MapMesh> dmesh;
	    Map<OverlayInfo, RenderTree.Node> ols = new HashMap<>();
	    Map<OverlayInfo, RenderTree.Node> olols = new HashMap<>();
	}

	private class Flavobj extends Gob {
	    private Flavobj(Coord2d c, double a) {
		super(sess.glob, c);
		this.a = a;
	    }

	    public Random mkrandoom() {
		Random r = new Random(Grid.this.id);
		r.setSeed(r.nextLong() ^ Double.doubleToLongBits(rc.x));
		r.setSeed(r.nextLong() ^ Double.doubleToLongBits(rc.y));
		return(r);
	    }
	}

	public Grid(Coord gc) {
	    this.gc = gc;
	    this.ul = gc.mul(cmaps);
	    cuts = new Cut[cutn.x * cutn.y];
	    for(int i = 0; i < cuts.length; i++)
		cuts[i] = new Cut();
	}

	public int gettile(Coord tc) {
	    return(tiles[tc.x + (tc.y * cmaps.x)]);
	}

	public double getz(Coord tc) {
	    return(z[tc.x + (tc.y * cmaps.x)]);
	}

	public void getol(OverlayInfo id, Area a, boolean[] buf) {
	    for(int i = 0; i < ols.length; i++) {
		if(ols[i].get().layer(ResOverlay.class) == id) {
		    int o = 0;
		    for(Coord c : a)
			buf[o++] = ol[i][c.x + (c.y * cmaps.x)];
		    return;
		}
	    }
	    for(int o = 0; o < buf.length; o++)
		buf[o] = false;
	}

	private class Flavobjs implements RenderTree.Node {
	    final RenderTree.Node[] mats;
	    final Gob[] all;

	    Flavobjs(Map<NodeWrap, Collection<Gob>> flavobjs) {
		Collection<Gob> all = new ArrayList<>();
		RenderTree.Node[] mats = new RenderTree.Node[flavobjs.size()];
		int i = 0;
		for(Map.Entry<NodeWrap, Collection<Gob>> matent : flavobjs.entrySet()) {
		    final NodeWrap mat = matent.getKey();
		    Collection<Gob> fos = matent.getValue();
		    final Gob[] fol = fos.toArray(new Gob[0]);
		    all.addAll(fos);
		    mats[i] = new RenderTree.Node() {
			    public void added(RenderTree.Slot slot) {
				for(Gob fo : fol)
				    slot.add(fo.placed);
			    }
			};
		    if(mat != null)
			mats[i] = mat.apply(mats[i]);
		    i++;
		}
		this.mats = mats;
		this.all = all.toArray(new Gob[0]);
	    }

	    public void added(RenderTree.Slot slot) {
		for(RenderTree.Node mat : mats)
		    slot.add(mat);
	    }

	    void tick(double dt) {
		for(Gob fo : all)
		    fo.ctick(dt);
	    }

	    void gtick(Render g) {
		for(Gob fo : all)
		    fo.gtick(g);
	    }
	}

	private Flavobjs makeflavor(Coord cutc) {
	    Map<NodeWrap, Collection<Gob>> buf = new HashMap<>();
	    Coord o = new Coord(0, 0);
	    Coord ul = cutc.mul(cutsz);
	    Coord gul = ul.add(gc.mul(cmaps));
	    int i = ul.x + (ul.y * cmaps.x);
	    Random rnd = new Random(id + cutc.x + (cutc.y * cutn.x));
	    for(o.y = 0; o.y < cutsz.x; o.y++, i += (cmaps.x - cutsz.x)) {
		for(o.x = 0; o.x < cutsz.y; o.x++, i++) {
		    Tileset set = tileset(tiles[i]);
		    Collection<Gob> mbuf = buf.get(set.flavobjmat);
		    if(mbuf == null)
			buf.put(set.flavobjmat, mbuf = new ArrayList<>());
		    int fp = rnd.nextInt();
		    int rp = rnd.nextInt();
		    double a = rnd.nextDouble();
		    if(set.flavobjs.size() > 0) {
			if((fp % set.flavprob) == 0) {
			    Indir<Resource> r = set.flavobjs.pick(rp % set.flavobjs.tw);
			    Gob g = new Flavobj(o.add(gul).mul(tilesz).add(tilesz.div(2)), a * 2 * Math.PI);
			    g.setattr(new ResDrawable(g, r, Message.nil));
			    mbuf.add(g);
			}
		    }
		}
	    }
	    return(new Flavobjs(buf));
	}

	public RenderTree.Node getfo(Coord cc) {
	    int foo = cc.x + (cc.y * cutn.x);
	    if(fo[foo] == null)
		fo[foo] = makeflavor(cc);
	    return(fo[foo]);
	}
	
	private Cut geticut(Coord cc) {
	    return(cuts[cc.x + (cc.y * cutn.x)]);
	}

	public MapMesh getcut(Coord cc) {
	    Cut cut = geticut(cc);
	    MapMesh ret = cut.mesh;
	    if((ret == null) || (cut.dmesh != null)) {
		synchronized(cut) {
		    ret = cut.mesh;
		    if((ret == null) && (cut.dmesh == null)) {
			/* Grid has been disposed, so wait for new one to arrive. */
			throw(new LoadingMap(MCache.this, gc));
		    }
		    if((ret == null) || ((cut.dmesh != null) && cut.dmesh.done())) {
			MapMesh old = cut.mesh;
			cut.mesh = ret = cut.dmesh.get();
			cut.dmesh = null;
			cut.ols.clear();
			cut.olols.clear();
			if(old != null)
			    old.dispose();
		    }
		}
	    }
	    return(ret);
	}
	
	public RenderTree.Node getolcut(OverlayInfo id, Coord cc) {
	    int nseq = MCache.this.olseq;
	    if(this.olseq != nseq) {
		for(int i = 0; i < cutn.x * cutn.y; i++) {
		    for(RenderTree.Node r : cuts[i].ols.values()) {
			if(r instanceof Disposable)
			    ((Disposable)r).dispose();
		    }
		    for(RenderTree.Node r : cuts[i].olols.values()) {
			if(r instanceof Disposable)
			    ((Disposable)r).dispose();
		    }
		    cuts[i].ols.clear();
		    cuts[i].olols.clear();
		}
		this.olseq = nseq;
	    }
	    Cut cut = geticut(cc);
	    if(!cut.ols.containsKey(id)) {
		cut.ols.put(id, getcut(cc).makeol(id));
		cut.olols.put(id, getcut(cc).makeolol(id));
	    }
	    return(cut.ols.get(id));
	}
	
	public RenderTree.Node getololcut(OverlayInfo id, Coord cc) {
	    getolcut(id, cc);
	    return(geticut(cc).olols.get(id));
	}

	private void buildcut(final Coord cc) {
	    final Cut cut = geticut(cc);
	    synchronized(cut) {
		Defer.Future<?> prev = cut.dmesh;
		cut.dmesh = Defer.later(new Defer.Callable<MapMesh>() {
			public MapMesh call() {
			    Random rnd = new Random(id);
			    rnd.setSeed(rnd.nextInt() ^ cc.x);
			    rnd.setSeed(rnd.nextInt() ^ cc.y);
			    return(MapMesh.build(MCache.this, rnd, ul.add(cc.mul(cutsz)), cutsz));
			}

			public String toString() {
			    return("Building map...");
			}
		    });
		if(prev != null)
		    prev.cancel();
	    }
	}

	public void ivneigh(Coord nc) {
	    Coord cc = new Coord();
	    for(cc.y = 0; cc.y < cutn.y; cc.y++) {
		for(cc.x = 0; cc.x < cutn.x; cc.x++) {
		    if((((nc.x < 0) && (cc.x == 0)) || ((nc.x > 0) && (cc.x == cutn.x - 1)) || (nc.x == 0)) &&
		       (((nc.y < 0) && (cc.y == 0)) || ((nc.y > 0) && (cc.y == cutn.y - 1)) || (nc.y == 0))) {
			buildcut(Coord.of(cc));
		    }
		}
	    }
	}
	
	public void tick(double dt) {
	    for(Flavobjs fol : fo) {
		if(fol != null)
		    fol.tick(dt);
	    }
	}
	
	public void gtick(Render g) {
	    for(Flavobjs fol : fo) {
		if(fol != null)
		    fol.gtick(g);
	    }
	}
	
	private void invalidate() {
	    for(int y = 0; y < cutn.y; y++) {
		for(int x = 0; x < cutn.x; x++)
		    buildcut(Coord.of(x, y));
	    }
	    fo = new Flavobjs[cutn.x * cutn.y];
	    for(Coord ic : new Coord[] {
		    Coord.of(-1, -1), Coord.of( 0, -1), Coord.of( 1, -1),
		    Coord.of(-1,  0),                   Coord.of( 1,  0),
		    Coord.of(-1,  1), Coord.of( 0,  1), Coord.of( 1,  1)}) {
		Grid ng = grids.get(gc.add(ic));
		if(ng != null)
		    ng.ivneigh(ic.inv());
	    }
	}

	public void dispose() {
	    for(Cut cut : cuts) {
		synchronized(cut) {
		    if(cut.dmesh != null) {
			cut.dmesh.cancel();
			cut.dmesh = null;
		    }
		    if(cut.mesh != null) {
			cut.mesh.dispose();
			cut.mesh = null;
		    }
		    for(RenderTree.Node r : cut.ols.values()) {
			if(r instanceof Disposable)
			    ((Disposable)r).dispose();
		    }
		    for(RenderTree.Node r : cut.olols.values()) {
			if(r instanceof Disposable)
			    ((Disposable)r).dispose();
		    }
		}
	    }
	}

	private void filltiles(Message buf) {
	    while(true) {
		int tileid = buf.uint8();
		if(tileid == 255)
		    break;
		String resnm = buf.string();
		int resver = buf.uint16();
		cktileid(tileid);
		nsets[tileid] = new Resource.Spec(Resource.remote(), resnm, resver);
	    }
	    for(int i = 0; i < tiles.length; i++) {
		tiles[i] = buf.uint8();
		if(nsets[tiles[i]] == null)
		    throw(new Message.FormatError(String.format("Got undefined tile: " + tiles[i])));
	    }
	}

	private void filltiles2(Message buf) {
	    int[] tileids = new int[1];
	    int maxid = 0;
	    while(true) {
		int encid = buf.uint16();
		if(encid == 65535)
		    break;
		maxid = Math.max(maxid, encid);
		int tileid = buf.uint16();
		if(encid >= tileids.length)
		    tileids = Utils.extend(tileids, Integer.highestOneBit(encid) * 2);
		tileids[encid] = tileid;
		String resnm = buf.string();
		int resver = buf.uint16();
		cktileid(tileid);
		nsets[tileid] = new Resource.Spec(Resource.remote(), resnm, resver);
	    }
	    boolean lg = maxid >= 256;
	    for(int i = 0; i < tiles.length; i++) {
		tiles[i] = tileids[lg ? buf.uint16() : buf.uint8()];
		if(nsets[tiles[i]] == null)
		    throw(new Message.FormatError(String.format("Got undefined tile: " + tiles[i])));
	    }
	}

	private void fillz(Message buf) {
	    int fmt = buf.uint8();
	    if(fmt == 0) {
		float z = buf.float32() * 11;
		for(int i = 0; i < this.z.length; i++)
		    this.z[i] = z;
	    } else if(fmt == 1) {
		float min = buf.float32() * 11, q = buf.float32() * 11;
		for(int i = 0; i < z.length; i++)
		    z[i] = min + (buf.uint8() * q);
	    } else if(fmt == 2) {
		float min = buf.float32() * 11, q = buf.float32() * 11;
		for(int i = 0; i < z.length; i++)
		    z[i] = min + (buf.uint16() * q);
	    } else if(fmt == 3) {
		for(int i = 0; i < z.length; i++)
		    z[i] = buf.float32() * 11;
	    } else {
		throw(new Message.FormatError(String.format("Unknown z-map format: %d", fmt)));
	    }
	}

	private Indir<Resource>[] fill_plots;
	private void decplots(Message buf) {
	    @SuppressWarnings("unchecked") Indir<Resource>[] pt = new Indir[256];
	    while(!buf.eom()) {
		int pidx = buf.uint8();
		if(pidx == 255)
		    break;
		pt[pidx] = sess.getres(buf.uint16());
	    }
	    fill_plots = pt;
	}

	private void fillplots(Message buf) {
	    if(fill_plots == null)
		return;
	    @SuppressWarnings("unchecked") Indir<Resource>[] olids = new Indir[0];
	    boolean[][] ols = {};
	    while(!buf.eom()) {
		int pidx = buf.uint8();
		if(pidx == 255)
		    break;
		int fl = buf.uint8();
		Coord c1 = Coord.of(buf.uint8(), buf.uint8());
		Coord c2 = Coord.of(buf.uint8(), buf.uint8());
		boolean[] mask = new boolean[(c2.x - c1.x) * (c2.y - c1.y)];
		if((fl & 1) != 0) {
		    for(int i = 0, l = 0, m = buf.uint8(); i < mask.length; i++) {
			if(l >= 8) {
			    m = buf.uint8();
			    l = 0;
			}
			mask[i] = (m & 1) != 0;
			m >>= 1;
			l++;
		    }
		} else {
		    for(int i = 0; i < mask.length; i++)
			mask[i] = true;
		}
		Indir<Resource> olid = fill_plots[pidx];
		if(olid == null)
		    continue;
		int oi;
		find: {
		    for(oi = 0; oi < olids.length; oi++) {
			if(olids[oi] == olid)
			    break find;
		    }
		    olids = Arrays.copyOf(olids, oi + 1);
		    ols = Arrays.copyOf(ols, oi + 1);
		    olids[oi] = olid;
		}
		boolean[] ol = ols[oi];
		if(ol == null)
		    ols[oi] = ol = new boolean[cmaps.x * cmaps.y];
		for(int y = c1.y, mi = 0; y < c2.y; y++) {
		    for(int x = c1.x; x < c2.x; x++) {
			ol[x + (y * cmaps.x)] |= mask[mi++];
		    }
		}
	    }
	    this.ols = olids;
	    this.ol = ols;
	    fill_plots = null;
	}

	private void subfill(Message msg) {
	    while(!msg.eom()) {
		String lnm = msg.string();
		int len = msg.uint8();
		if((len & 0x80) != 0)
		    len = msg.int32();
		Message buf = new LimitMessage(msg, len);
		switch(lnm) {
		case "z":
		    subfill(new ZMessage(buf));
		    break;
		case "m":
		    id = buf.int64();
		    break;
		case "t":
		    filltiles(buf);
		    break;
		case "t2":
		    filltiles2(buf);
		    break;
		case "h":
		    fillz(buf);
		    break;
		case "pi":
		    decplots(buf);
		    break;
		case "p":
		    fillplots(buf);
		    break;
		}
		buf.skip();
	    }
	}

	public void fill(Message msg) {
	    int ver = msg.uint8();
	    if(ver == 1) {
		subfill(msg);
	    } else {
		throw(new RuntimeException("Unknown map data version " + ver));
	    }
	    invalidate();
	    seq++;
	}
    }

    public MCache(Session sess) {
	this.sess = sess;
    }

    public void ctick(double dt) {
	Collection<Grid> copy;
	synchronized(grids) {
	    copy = new ArrayList<>(grids.values());
	}
	for(Grid g : copy)
	    g.tick(dt);
    }

    public void gtick(Render g) {
	Collection<Grid> copy;
	synchronized(grids) {
	    copy = new ArrayList<>(grids.values());
	}
	for(Grid gr : copy)
	    gr.gtick(g);
    }

    public void invalidate(Coord cc) {
	synchronized(req) {
	    if(req.get(cc) == null)
		req.put(cc, new Request());
	}
    }

    public void invalblob(Message msg) {
	int type = msg.uint8();
	if(type == 0) {
	    invalidate(msg.coord());
	} else if(type == 1) {
	    Coord ul = msg.coord();
	    Coord lr = msg.coord();
	    synchronized(this) {
		trim(ul, lr);
	    }
	} else if(type == 2) {
	    synchronized(this) {
		trimall();
	    }
	}
    }

    private Grid cached = null;
    public Grid getgrid(Coord gc) {
	synchronized(grids) {
	    if((cached == null) || !cached.gc.equals(gc)) {
		cached = grids.get(gc);
		if(cached == null) {
		    request(gc);
		    throw(new LoadingMap(this, gc));
		}
	    }
	    return(cached);
	}
    }

    public Grid getgridt(Coord tc) {
	return(getgrid(tc.div(cmaps)));
    }

    public int gettile(Coord tc) {
	Grid g = getgridt(tc);
	return(g.gettile(tc.sub(g.ul)));
    }

    public double getfz(Coord tc) {
	Grid g = getgridt(tc);
	return(g.getz(tc.sub(g.ul)));
    }

    public double getcz(double px, double py) {
	double tw = tilesz.x, th = tilesz.y;
	Coord ul = Coord.of(Utils.floordiv(px, tw), Utils.floordiv(py, th));
	double sx = (px - (ul.x * tw)) / tw;
	double sy = (py - (ul.y * th)) / th;
	return(((1.0f - sy) * (((1.0f - sx) * getfz(ul)) + (sx * getfz(ul.add(1, 0))))) +
	       (sy * (((1.0f - sx) * getfz(ul.add(0, 1))) + (sx * getfz(ul.add(1, 1))))));
    }

    public double getcz(Coord2d pc) {
	return(getcz(pc.x, pc.y));
    }

    public float getcz(float px, float py) {
	return((float)getcz((double)px, (double)py));
    }

    public float getcz(Coord pc) {
	return(getcz(pc.x, pc.y));
    }

    public Coord3f getzp(Coord2d pc) {
	return(Coord3f.of((float)pc.x, (float)pc.y, (float)getcz(pc)));
    }

    public final ZSurface zsurf = new ZSurface() {
	    public double getz(Coord tc) {
		return(getfz(tc));
	    }
	};

    public double getz(SurfaceID id, Coord tc) {
	Grid g = getgridt(tc);
	MapMesh cut = g.getcut(tc.sub(g.ul).div(cutsz));
	Tiler t = tiler(g.gettile(tc.sub(g.ul)));
	return(cut.getsurf(id, t).getz(tc));
    }

    public double getz(SurfaceID id, Coord2d pc) {
	Coord tc = pc.floor(tilesz);
	Grid g = getgridt(tc);
	MapMesh cut = g.getcut(tc.sub(g.ul).div(cutsz));
	Tiler t = tiler(g.gettile(tc.sub(g.ul)));
	ZSurface surf = cut.getsurf(id, t);
	return(surf.getz(pc));
    }

    public Coord3f getzp(SurfaceID id, Coord2d pc) {
	return(Coord3f.of((float)pc.x, (float)pc.y, (float)getz(id, pc)));
    }

    public Coord3f getnorm(SurfaceID id, Coord2d pc) {
	Coord tc = pc.floor(tilesz);
	Grid g = getgridt(tc);
	MapMesh cut = g.getcut(tc.sub(g.ul).div(cutsz));
	Tiler t = tiler(g.gettile(tc.sub(g.ul)));
	return(cut.getsurf(id, t).getnorm(pc));
    }

    public Collection<OverlayInfo> getols(Area a) {
	Collection<OverlayInfo> ret = new ArrayList<>();
	for(Coord gc : a.div(cmaps)) {
	    Grid g = getgrid(gc);
	    if(g.ols == null)
		continue;
	    for(Indir<Resource> res : g.ols) {
		OverlayInfo id = res.get().flayer(ResOverlay.class);
		if(!ret.contains(id))
		    ret.add(id);
	    }
	}
	for(Overlay lol : ols) {
	    if((lol.a.overlap(a) != null) && !ret.contains(lol.id))
		ret.add(lol.id);
	}
	return(ret);
    }

    public void getol(OverlayInfo id, Area a, boolean[] buf) {
	Area ga = a.div(cmaps);
	if(ga.area() == 1) {
	    Grid g = getgrid(ga.ul);
	    g.getol(id, a.xl(g.ul.inv()), buf);
	} else {
	    boolean[] gbuf = new boolean[cmaps.x * cmaps.y];
	    for(Coord gc : ga) {
		Grid g = getgrid(gc);
		Area gt = Area.sized(g.ul, cmaps);
		g.getol(id, Area.sized(Coord.z, cmaps), gbuf);
		for(Coord tc : a.overlap(gt))
		    buf[a.ri(tc)] = gbuf[(tc.x - gt.ul.x) + ((tc.y - gt.ul.y) * cmaps.x)];
	    }
	}
	for(Overlay lol : ols) {
	    if(lol.id != id)
		continue;
	    Area la = lol.a.overlap(a);
	    if(la != null) {
		for(Coord lc : la)
		    buf[a.ri(lc)] = true;
	    }
	}
    }
    
    public MapMesh getcut(Coord cc) {
	return(getgrid(cc.div(cutn)).getcut(cc.mod(cutn)));
    }
    
    public RenderTree.Node getfo(Coord cc) {
	synchronized(grids) {
	    return(getgrid(cc.div(cutn)).getfo(cc.mod(cutn)));
	}
    }

    public RenderTree.Node getolcut(OverlayInfo id, Coord cc) {
	synchronized(grids) {
	    return(getgrid(cc.div(cutn)).getolcut(id, cc.mod(cutn)));
	}
    }

    public RenderTree.Node getololcut(OverlayInfo id, Coord cc) {
	synchronized(grids) {
	    return(getgrid(cc.div(cutn)).getololcut(id, cc.mod(cutn)));
	}
    }

    public void mapdata2(Message msg) {
	Coord c = msg.coord();
	synchronized(grids) {
	    synchronized(req) {
		if(req.containsKey(c)) {
		    Grid g = grids.get(c);
		    if(g == null) {
			grids.put(c, g = new Grid(c));
			cached = null;
		    }
		    g.fill(msg);
		    req.remove(c);
		    olseq++;
		    chseq++;
		    gridwait.wnotify();
		}
	    }
	}
    }

    public void mapdata(Message msg) {
	long now = System.currentTimeMillis();
	int pktid = msg.int32();
	int off = msg.uint16();
	int len = msg.uint16();
	Defrag fragbuf;
	synchronized(fragbufs) {
	    if((fragbuf = fragbufs.get(pktid)) == null) {
		fragbuf = new Defrag(len);
		fragbufs.put(pktid, fragbuf);
	    }
	    fragbuf.add(msg.bytes(), off);
	    fragbuf.last = now;
	    if(fragbuf.done()) {
		mapdata2(fragbuf.msg());
		fragbufs.remove(pktid);
	    }
	
	    /* Clean up old buffers */
	    for(Iterator<Map.Entry<Integer, Defrag>> i = fragbufs.entrySet().iterator(); i.hasNext();) {
		Map.Entry<Integer, Defrag> e = i.next();
		Defrag old = e.getValue();
		if(now - old.last > 10000)
		    i.remove();
	    }
	}
    }

    public Resource.Spec tilesetn(int i) {
	Resource.Spec[] nsets = this.nsets;
	if(i >= nsets.length)
	    return(null);
	return(nsets[i]);
    }

    public Resource tilesetr(int i) {
	Reference<Resource>[] sets = this.sets;
	if(i >= sets.length)
	    return(null);
	Resource res = (sets[i] == null) ? null : sets[i].get();
	if(res == null) {
	    Resource.Spec[] nsets = this.nsets;
	    if(nsets[i] == null)
		return(null);
	    sets[i] = new SoftReference<>(res = nsets[i].get());
	}
	return(res);
    }

    public Tileset tileset(int i) {
	Reference<Tileset>[] csets = this.csets;
	if(i >= csets.length)
	    return(null);
	Tileset cset = (csets[i] == null) ? null : csets[i].get();
	if(cset == null) {
	    Resource res = tilesetr(i);
	    if(res == null)
		return(null);
	    csets[i] = new SoftReference<>(cset = res.flayer(Tileset.class));
	}
	return(cset);
    }

    public Tiler tiler(int i) {
	Reference<Tiler>[] tiles = this.tiles;
	if(i >= tiles.length)
	    return(null);
	Tiler tile = (tiles[i] == null) ? null : tiles[i].get();
	if(tile == null) {
	    Tileset set = tileset(i);
	    if(set == null)
		return(null);
	    tiles[i] = new SoftReference<>(tile = set.tfac().create(i, set));
	}
	return(tile);
    }

    public void trimall() {
	synchronized(grids) {
	    synchronized(req) {
		for(Grid g : grids.values())
		    g.dispose();
		grids.clear();
		req.clear();
		cached = null;
	    }
	    gridwait.wnotify();
	}
    }

    public void trim(Coord ul, Coord lr) {
	synchronized(grids) {
	    synchronized(req) {
		for(Iterator<Map.Entry<Coord, Grid>> i = grids.entrySet().iterator(); i.hasNext();) {
		    Map.Entry<Coord, Grid> e = i.next();
		    Coord gc = e.getKey();
		    Grid g = e.getValue();
		    if((gc.x < ul.x) || (gc.y < ul.y) || (gc.x > lr.x) || (gc.y > lr.y)) {
			g.dispose();
			i.remove();
		    }
		}
		for(Iterator<Coord> i = req.keySet().iterator(); i.hasNext();) {
		    Coord gc = i.next();
		    if((gc.x < ul.x) || (gc.y < ul.y) || (gc.x > lr.x) || (gc.y > lr.y))
			i.remove();
		}
		cached = null;
	    }
	    gridwait.wnotify();
	}
    }

    public void request(Coord gc) {
	synchronized(req) {
	    if(!req.containsKey(gc))
		req.put(Coord.of(gc), new Request());
	}
    }

    public void reqarea(Coord ul, Coord br) {
	ul = ul.div(cutsz); br = br.div(cutsz);
	Coord rc = new Coord();
	for(rc.y = ul.y; rc.y <= br.y; rc.y++) {
	    for(rc.x = ul.x; rc.x <= br.x; rc.x++) {
		try {
		    getcut(Coord.of(rc));
		} catch(Loading e) {}
	    }
	}
    }

    public void sendreqs() {
	long now = System.currentTimeMillis();
	boolean updated = false;
	synchronized(req) {
	    for(Iterator<Map.Entry<Coord, Request>> i = req.entrySet().iterator(); i.hasNext();) {
		Map.Entry<Coord, Request> e = i.next();
		Coord c = e.getKey();
		Request r = e.getValue();
		if(now - r.lastreq > 1000) {
		    r.lastreq = now;
		    if(++r.reqs >= 5) {
			i.remove();
			updated = true;
		    } else {
			PMessage msg = new PMessage(Session.MSG_MAPREQ);
			msg.addcoord(c);
			sess.sendmsg(msg);
		    }
		}
	    }
	}
	if(updated) {
	    synchronized(grids) {
		gridwait.wnotify();
	    }
	}
    }

	public boolean isLoaded(){
		return cached !=null;
	}
}
