/* Preprocessed source code */
/* $use: lib/globfx */
/* $use: lib/leaves */
/* $use: lib/svaj */

package haven.res.lib.tree;

import haven.*;
import haven.render.*;
import haven.res.lib.leaves.*;
import haven.res.lib.svaj.*;
import java.util.*;

@FromResource(name = "lib/tree", version = 14)
public class Tree extends Sprite {
    public final float fscale;
    public final RenderTree.Node[][] parts;
    public int stage, sel;
    public LeafSpec leaves = null;
    private final Collection<RenderTree.Slot> slots = new ArrayList<>();

    public static Location mkscale(float x, float y, float z) {
	return(new Location(new Matrix4f(x, 0, 0, 0,
					 0, y, 0, 0,
					 0, 0, z, 0,
					 0, 0, 0, 1)));
    }

    public static Location mkscale(float s) {
	return(mkscale(s, s, s));
    }

    public Collection<Pair<Integer, RenderTree.Node>> lsparts(Resource res, int matsel) {
	Collection<Pair<Integer, RenderTree.Node>> rl = new ArrayList<>(16);
	for(FastMesh.MeshRes mr : res.layers(FastMesh.MeshRes.class)) {
	    int id = (mr.id < 0) ? 0 : mr.id;
	    int dmat = (id & 0xf0) >> 4;
	    if((dmat == 0) || (matsel < 0)) {
		if(mr.mat != null)
		    rl.add(new Pair<>(id, mr.mat.get().apply(mr.m)));
	    } else {
		Material.Res mat = res.layer(Material.Res.class, (dmat * 4) + matsel);
		if(mat != null)
		    rl.add(new Pair<>(id, mat.get().apply(mr.m)));
	    }
	}
	for(RenderLink.Res lr : res.layers(RenderLink.Res.class)) {
	    rl.add(new Pair<>(lr.id, lr.l.make(owner)));
	}
	return(rl);
    }

    @SuppressWarnings("unchecked")
    public RenderTree.Node[][] mkparts(Resource res, int matsel, int fl) {
	Collection<RenderTree.Node> rl[] = new Collection[3];
	for(int i = 0; i < rl.length; rl[i++] = new ArrayList<>());
	int pmask = (1 << rl.length) - 1;
	for(Pair<Integer, RenderTree.Node> part : lsparts(res, matsel)) {
	    if(((1 << (part.a & 0xf)) & fl) != 0)
		continue;
	    int dmesh = (part.a & 0xf00) >> 8;
	    if((dmesh != 0) && (matsel >= 0) && ((dmesh & (1 << matsel)) == 0))
		continue;
	    int sel = ((part.a & 0xf000) >> 12) & pmask;
	    for(int i = 0; i < rl.length; i++)
		if((sel == 0) || ((sel & (1 << i)) != 0))
		    rl[i].add(part.b);
	}
	RenderTree.Node[][] ret = new RenderTree.Node[rl.length][];
	for(int i = 0; i < ret.length; i++)
	    ret[i] = rl[i].toArray(new RenderTree.Node[0]);
	return(ret);
    }

    public static Random randoom(Gob owner) {
	Coord tc = owner.rc.floor(MCache.tilesz);
	MCache.Grid grid = owner.glob.map.getgridt(tc);
	tc = tc.sub(grid.ul);
	Random rnd = new Random(grid.id);
	rnd.setSeed(rnd.nextLong() ^ tc.x);
	rnd.setSeed(rnd.nextLong() ^ tc.y);
	return(rnd);
    }

    public static Location rndrot(Random rnd) {
	double aa = rnd.nextDouble() * Math.PI * 2;
	double ra = rnd.nextGaussian() * Math.PI / 64;
	Coord3f axis = new Coord3f((float)Math.sin(aa), (float)Math.cos(aa), 0);
	return(Location.rot(axis, (float)ra));
    }

    public static Location rndrot(Owner owner) {
	if(owner instanceof Gob)
	    return(rndrot(randoom((Gob)owner)));
	return(null);
    }

    public Tree(Owner owner, Resource res, float scale, int s, int fl) {
	super(owner, res);
	this.fscale = scale;
	if(owner instanceof Gob) {
	    Gob gob = (Gob)owner;
	    gob.setattr(new TreeRotation(gob, rndrot(gob)));
	    gob.setattr(new GobSvaj(gob));
	    if(fscale != 1.0f)
		gob.setattr(new TreeScale(gob, fscale));
	}
	parts = mkparts(res, s, fl);
	sel = s;
    }

    private static final haven.res.lib.tree.Factory deffac = new haven.res.lib.tree.Factory();
    public static Tree mksprite(Owner owner, Resource res, Message sdt) {
	/* XXX: Remove me */
	return(deffac.create(owner, res, sdt));
    }

    public void added(RenderTree.Slot slot) {
	for(RenderTree.Node p : parts[stage])
	    slot.add(p);
	slots.add(slot);
    }

    public void removed(RenderTree.Slot slot) {
	slots.remove(slot);
    }

    private Random lrand;
    private double lrate;
    public boolean tick(double dt) {
	leaves: if(leaves != null) {
	    if(lrand == null) {
		lrand = new Random();
		if(lrand.nextInt(2) == 0) {
		    leaves = null;
		    break leaves;
		}
		Random rrand = lrand;
		if(owner instanceof Gob) {
		    try {
			rrand = randoom((Gob)owner);
		    } catch(Loading l) {
			break leaves;
		    }
		}
		lrate = 0.05 + (Math.pow(rrand.nextDouble(), 0.75) * 0.95);
	    }
	    if(fscale < 0.75)
		return(false);
	    try {
		if(!slots.isEmpty() && (lrand.nextDouble() > Math.pow(lrate, dt))) {
		    Location.Chain loc = Utils.el(slots).state().get(Homo3D.loc);
		    FallingLeaves fx = FallingLeaves.get(((Gob)owner).glob);
		    Material mat = leaves.mat[lrand.nextInt(leaves.mat.length)];
		    fx.addleaf(new StdLeaf(fx, fx.onevertex(loc, leaves.mesh), mat));
		}
	    } catch(Loading e) {}
	}
	return(false);
    }
}
