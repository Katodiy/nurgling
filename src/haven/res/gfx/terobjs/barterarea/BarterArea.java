/* Preprocessed source code */
package haven.res.gfx.terobjs.barterarea;

import haven.*;
import haven.render.*;
import nurgling.NConfiguration;

import static haven.MCache.tilesz;

/* >spr: BarterArea */
@haven.FromResource(name = "gfx/terobjs/barterarea", version = 2)
public class BarterArea extends Sprite {
    public final static Indir<Resource> poleres = Resource.remote().load("gfx/terobjs/arch/marketpole", 1);
    public final static Indir<Resource> roperes = Resource.remote().load("gfx/terobjs/barterarea-string", 1);
    public final static float bscale = 1f / 11;
    final Gob gob = owner.context(Gob.class);
    final Material extramat;
    final Coord3f cc;
    final Sprite pole;
    final Location[] poles;
    final MCache map;
    final RenderTree.Node bound;

    Coord3f gnd(float rx, float ry) {
	double a = -gob.a;
	float s = (float)Math.sin(a), c = (float)Math.cos(a);
	float gx = rx * c + ry * s, gy = ry * c - rx * s;
	if(!NConfiguration.getInstance().flatsurface)
		return(new Coord3f(rx, -ry, map.getcz(gx + cc.x, gy + cc.y) - cc.z));
	else
		return(new Coord3f(rx, -ry, 0));
    }

    public BarterArea(Owner owner, Resource res, Message sdt) {
	super(owner, res);
	this.map = owner.context(Glob.class).map;
	Material bmat = roperes.get().layer(Material.Res.class).get();
	Coord3f[] vert;
	int[] face;
	int fl = 0;
	if(sdt.eom()) {
	    vert = new Coord3f[] {new Coord3f(-11, -11, 0), new Coord3f( 11, -11, 0),
				  new Coord3f( 11,  11, 0), new Coord3f(-11,  11, 0)};
	    face = new int[] {4};
	} else {
	    fl = sdt.uint8();
	    float ext = sdt.uint8() * 11;
	    float l = sdt.snorm8() * ext, u = sdt.snorm8() * ext;
	    float r = sdt.snorm8() * ext, b = sdt.snorm8() * ext;
	    vert = new Coord3f[] {new Coord3f(l, u, 0), new Coord3f(r, u, 0),
				  new Coord3f(r, b, 0), new Coord3f(l, b, 0)};
	    face = new int[] {4};
	}
	pole = Sprite.create(owner, poleres.get(), Message.nil);
	this.cc = gob.getrc();
	poles = new Location[vert.length];
	float bu = vert[0].x, bl = vert[0].y, bb = vert[0].x, br = vert[0].y;
	for(int i = 0; i < vert.length; i++) {
	    poles[i] = Location.xlate(gnd(vert[i].x, vert[i].y));
	    bu = Math.min(vert[i].y, bu); bl = Math.min(vert[i].x, bl);
	    bb = Math.max(vert[i].y, bb); br = Math.max(vert[i].x, br);
	}
	extramat = Resource.classres(BarterArea.class).layer(Material.Res.class, fl & 3).get();
	bound = bmat.apply(mkbound(face, vert));
    }

    void trace(MeshBuf buf, float x1, float y1, float x2, float y2) {
	float dx = x2 - x1, dy = y2 - y1, ed = (float)Math.sqrt(dx * dx + dy * dy);
	float lx = x1, ly = y1;
	Coord3f nrm = new Coord3f(dy / ed, dx / ed, 0);
	MeshBuf.Tex tex = buf.layer(MeshBuf.tex);
	MeshBuf.Vertex ll = buf.new Vertex(gnd(lx, ly), nrm);
	MeshBuf.Vertex lh = buf.new Vertex(gnd(lx, ly).add(0, 0, 3), nrm);
	tex.set(ll, new Coord3f(0, 1, 0));
	tex.set(lh, new Coord3f(0, 0, 0));
	int lim = 0;
	while(true) {
	    boolean end = true;
	    float ma = 1.0f, a;
	    float nx = x2, ny = y2;
	    if(dx != 0) {
		float ex;
		if(dx > 0) {
		    a = ((ex = (float)((Math.floor(lx / tilesz.x) + 1) * tilesz.x)) - x1) / dx;
		} else {
		    a = ((ex = (float)((Math.ceil (lx / tilesz.x) - 1) * tilesz.x)) - x1) / dx;
		}
		if(a < ma) {
		    nx = ex; ny = y1 + dy * a;
		    ma = a;
		    end = false;
		}
	    }
	    if(dy != 0) {
		float ey;
		if(dy > 0)
		    a = ((ey = (float)((Math.floor(ly / tilesz.y) + 1) * tilesz.y)) - y1) / dy;
		else
		    a = ((ey = (float)((Math.ceil (ly / tilesz.y) - 1) * tilesz.y)) - y1) / dy;
		if(a < ma) {
		    nx = x1 + dx * a; ny = ey;
		    ma = a;
		    end = false;
		}
	    }
	    MeshBuf.Vertex nl = buf.new Vertex(gnd(nx, ny), nrm);
	    MeshBuf.Vertex nh = buf.new Vertex(gnd(nx, ny).add(0, 0, 3), nrm);
	    tex.set(nl, new Coord3f(ma * ed * bscale, 1, 0));
	    tex.set(nh, new Coord3f(ma * ed * bscale, 0, 0));
	    buf.new Face(lh, ll, nh); buf.new Face(ll, nl, nh);
	    ll = nl; lh = nh;
	    lx = nx; ly = ny;
	    if(end)
		return;
	    if(lim++ > 100)
		throw(new RuntimeException("stuck in trace"));
	}
    }

    RenderTree.Node mkbound(int[] face, Coord3f[] vert) {
	MeshBuf buf = new MeshBuf();
	for(int f = 0, b = 0; f < face.length; b = face[f++]) {
	    for(int v = 0, n = face[f] - b; v < n; v++) {
		int j = v + b, k = ((v + 1) % n) + b;
		trace(buf, vert[j].x, vert[j].y, vert[k].x, vert[k].y);
	    }
	}
	FastMesh mesh = buf.mkmesh();
	return(mesh);
    }

    public void added(RenderTree.Slot slot) {
	if(extramat != null)
	    slot.ostate(extramat);
	slot.lockstate();
	if(bound != null) {
	    slot.add(bound);
	    for(Location loc : poles)
		slot.add(pole, loc);
	}
    }
}
