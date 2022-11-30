/* Preprocessed source code */
package haven.res.gfx.terobjs.consobj;

import haven.*;
import haven.render.*;
import static haven.MCache.tilesz;

/* >spr: Consobj */
@FromResource(name = "gfx/terobjs/consobj", version = 32)
public class Consobj extends Sprite implements Sprite.CUpd {
    public static Resource signres = null;
    public static Resource poleres = null;
    private static Material bmat = null;
    public final static float bscale = 1f / 11;
    public final ResData built;
    public float done;
    final Coord3f cc;
    final Sprite sign, pole;
    final Location[] poles;
    final MCache map;
    final RenderTree.Node bound;

    Coord3f gnd(float rx, float ry) {
	double a = -((Gob)owner).a;
	float s = (float)Math.sin(a), c = (float)Math.cos(a);
	float gx = rx * c + ry * s, gy = ry * c - rx * s;
	return(new Coord3f(rx, -ry, map.getcz(gx + cc.x, gy + cc.y) - cc.z));
    }

    public Consobj(Owner owner, Resource res, Message sdt) {
	super(owner, res);
	if(signres == null){
		signres= Resource.remote().loadwait("ngfx/terobjs/sign", 6);
	}
    if(poleres == null){
        poleres = Resource.remote().loadwait("ngfx/terobjs/arch/conspole", 2);
    }
	this.map = owner.context(Glob.class).map;
	if(bmat == null)
	{
		Resource cor = Resource.remote().loadwait("ngfx/terobjs/consobj");
		for(Resource.Layer layer: cor.layers) {
			if(layer instanceof Material.Res){
				bmat = ((Material.Res)layer).get();
			}
		}
	}
	Coord3f[] vert;
	int[] face;
	{
	    int nf = sdt.uint8();
	    if(nf >= 128) {
		Coord ul = new Coord((byte)nf, sdt.int8());
		Coord br = new Coord(sdt.int8(), sdt.int8());
		vert = new Coord3f[] {new Coord3f(ul.x, ul.y, 0), new Coord3f(br.x, ul.y, 0),
				      new Coord3f(br.x, br.y, 0), new Coord3f(ul.x, br.y, 0)};
		face = new int[] {4};
	    } else {
		int ext = sdt.uint8() * 11;
		face = new int[nf];
		int nv = 0;
		for(int f = 0; f < nf; f++)
		    face[f] = nv = sdt.uint8() + nv;
		vert = new Coord3f[nv];
		for(int v = 0; v < nv; v++)
		    vert[v] = new Coord3f(sdt.snorm8() * ext, sdt.snorm8() * ext, 0);
	    }
	}
	done = sdt.uint8() / 255.0f;
	if(!sdt.eom()) {
	    int resid = sdt.uint16();
	    built = new ResData(owner.context(Resource.Resolver.class).getres(resid), new MessageBuf(sdt.bytes()));
	} else {
	    built = null;
	}
	sign = Sprite.create(owner, signres, Message.nil);
	pole = Sprite.create(owner, poleres, Message.nil);
	this.cc = ((Gob)owner).getrc();
	poles = new Location[vert.length];
	if(vert.length > 0) {
	    float bu = vert[0].x, bl = vert[0].y, bb = vert[0].x, br = vert[0].y;
	    for(int i = 0; i < vert.length; i++) {
		poles[i] = Location.xlate(gnd(vert[i].x, vert[i].y));
		bu = Math.min(vert[i].y, bu); bl = Math.min(vert[i].x, bl);
		bb = Math.max(vert[i].y, bb); br = Math.max(vert[i].x, br);
	    }
	    if(((br - bl) > 22) || ((bb - bu) > 22))
		bound = mkbound(face, vert);
	    else
		bound = null;
	} else {
	    bound = null;
	}
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
	return(bmat.apply(mesh));
    }

    public void added(RenderTree.Slot slot) {
	slot.add(sign);
	if(bound != null) {
	    slot.add(bound);
	    for(Location loc : poles)
		slot.add(pole, loc);
	}
    }

    public void update(Message sdt) {
	for(int i = 0; i < 4; i++)
	    sdt.int8();
	done = sdt.uint8() / 255.0f;
    }
}
