/* Preprocessed source code */
/*
  $use: lib/globfx
  $use: lib/env
*/

package haven.res.lib.leaves;

import haven.*;
import haven.render.*;
import java.util.*;
import java.nio.*;
import haven.res.lib.globfx.*;
import haven.res.lib.env.*;

@FromResource(name = "lib/leaves", version = 18)
public class FallingLeaves extends GlobEffect {
    public static final int maxleaves = 10000;
    static final VertexArray.Layout fmt =
	new VertexArray.Layout(new VertexArray.Layout.Input(Homo3D.vertex, new VectorFormat(3, NumberFormat.FLOAT32), 0,  0, 20),
			       new VertexArray.Layout.Input(Homo3D.normal, new VectorFormat(3, NumberFormat.SNORM8),  0, 12, 20),
			       new VertexArray.Layout.Input(Tex2D.texc,    new VectorFormat(2, NumberFormat.UNORM8),  0, 16, 20));

    public final Random rnd = new Random();
    public final Object monitor;
    VertexArray va = null;
    final Leaf leaves[] = new Leaf[maxleaves];
    final Map<Material, MSlot> matmap = new HashMap<Material, MSlot>();
    final Glob glob;
    final Collection<RenderTree.Slot> slots = new ArrayList<>(1);
    int nl;
    float ckt = 0;

    private class MSlot implements Rendered, RenderTree.Node {
	final Material m;
	final Collection<RenderTree.Slot> slots = new ArrayList<>(1);
	Leaf leaves[] = new Leaf[128];
	Model model = null;
	Model.Indices ind = null;
	boolean added = false, update = true;
	int nl;

	MSlot(Material m) {
	    this.m = m;
	}

	public void draw(Pipe state, Render out) {
	    if(model != null)
		out.draw(state, model);
	}

	FillBuffer fillind(Model.Indices dst, Environment env) {
	    FillBuffer ret = env.fillbuf(dst);
	    ByteBuffer buf = ret.push();
	    for(int i = 0; i < nl; i++) {
		Leaf l = leaves[i];
		int vi = l.vidx * 4;
		buf.putShort((short)(vi + 0));
		buf.putShort((short)(vi + 1));
		buf.putShort((short)(vi + 3));
		buf.putShort((short)(vi + 1));
		buf.putShort((short)(vi + 2));
		buf.putShort((short)(vi + 3));
	    }
	    return(ret);
	}

	void update(Render d) {
	    if((model == null) || (model.n != nl * 6)) {
		if(model != null)
		    model.dispose();
		int indsz = (ind == null) ? 0 : ind.n;
		if((indsz < nl * 6) || (indsz > nl * 24))
		    indsz = Math.max(Integer.highestOneBit(nl * 6), 64) << 1;
		if((ind == null) || (indsz != ind.n)) {
		    if(ind != null)
			ind.dispose();
		    ind = new Model.Indices(indsz, NumberFormat.UINT16, DataBuffer.Usage.STREAM, null).shared();
		}
		model = new Model(Model.Mode.TRIANGLES, va, ind, 0, nl * 6);
		for(RenderTree.Slot slot : this.slots)
		    slot.update();
	    }
	    d.update(model.ind, this::fillind);
	}

	void add(Leaf l) {
	    if(nl >= leaves.length)
		leaves = Arrays.copyOf(leaves, leaves.length * 2);
	    (leaves[nl] = l).midx = nl;
	    nl++;
	    update = true;
	}

	void remove(Leaf l) {
	    (leaves[l.midx] = leaves[--nl]).midx = l.midx;
	    leaves[nl] = null;
	    update = true;
	}

	public void added(RenderTree.Slot slot) {
	    slot.ostate(m);
	    slots.add(slot);
	}

	public void removed(RenderTree.Slot slot) {
	    slots.remove(slot);
	}
    }

    public FallingLeaves(Glob glob, Object monitor) {
	this.glob = glob;
	this.monitor = monitor;
    }

    public abstract class Leaf {
	float x, y, z;
	float xv, yv, zv;
	float nx, ny, nz;
	float nxv, nyv, nzv;
	float ar = (0.5f + rnd.nextFloat()) / 50;
	MSlot m;
	int vidx, midx;

	public Leaf(float x, float y, float z) {
	    this.x = x; this.y = y; this.z = z;
	    nx = rnd.nextFloat();
	    ny = rnd.nextFloat();
	    nz = rnd.nextFloat();
	    if(nx < 0.5f) nx -= 1.0f;
	    if(ny < 0.5f) ny -= 1.0f;
	    if(nz < 0.5f) nz -= 1.0f;
	    float nf = 1.0f / (float)Math.sqrt((nx * nx) + (ny * ny) + (nz * nz));
	    nx *= nf;
	    ny *= nf;
	    nz *= nf;
	}

	public Leaf() {
	    this(0, 0, 0);
	}

	public Leaf(Coord3f c) {
	    this(c.x, c.y, c.z);
	}

	public abstract Material mat();
	public float size() {return(1);}
    }

    public static FallingLeaves get(Glob glob) {
	GlobEffector eff = GlobEffector.get(glob);
	return(eff.get(new FallingLeaves(glob, eff.monitor())));
    }

    public void added(RenderTree.Slot slot) {
	for(MSlot mat : matmap.values()) {
	    if(mat.added)
		slot.add(mat);
	}
	slots.add(slot);
    }

    public void removed(RenderTree.Slot slot) {
	slots.remove(slot);
    }

    FillBuffer fillvert(VertexArray.Buffer dst, Environment env) {
	FillBuffer ret = env.fillbuf(dst);
	ByteBuffer buf = ret.push();
	for(int i = 0; i < nl; i++) {
	    try {
		Leaf l = leaves[i];
		byte nx = (byte)(Utils.clip(l.nx, -1, 1) * 127);
		byte ny = (byte)(Utils.clip(l.ny, -1, 1) * 127);
		byte nz = (byte)(Utils.clip(l.nz, -1, 1) * 127);
		float sz = l.size();
		buf.putFloat(l.x + sz * l.nz);
		buf.putFloat(l.y - sz * l.nz);
		buf.putFloat(l.z + sz * (l.ny - l.nx));
		buf.put(nx).put(ny).put(nz).put((byte)0);
		buf.put((byte)0).put((byte)0).put((byte)0).put((byte)0);
		buf.putFloat(l.x + sz * l.nz);
		buf.putFloat(l.y + sz * l.nz);
		buf.putFloat(l.z - sz * (l.nx - l.ny));
		buf.put(nx).put(ny).put(nz).put((byte)0);
		buf.put((byte)0).put((byte)255).put((byte)0).put((byte)0);
		buf.putFloat(l.x - sz * l.nz);
		buf.putFloat(l.y + sz * l.nz);
		buf.putFloat(l.z + sz * (l.nx - l.ny));
		buf.put(nx).put(ny).put(nz).put((byte)0);
		buf.put((byte)255).put((byte)255).put((byte)0).put((byte)0);
		buf.putFloat(l.x - sz * l.nz);
		buf.putFloat(l.y - sz * l.ny);
		buf.putFloat(l.z + sz * (l.nx + l.ny));
		buf.put(nx).put(ny).put(nz).put((byte)0);
		buf.put((byte)255).put((byte)0).put((byte)0).put((byte)0);
	    } catch(RuntimeException exc) {
		throw(new RuntimeException(String.format("%d %d %d", i, buf.position(), buf.capacity()), exc));
	    }
	}
	return(ret);
    }

    void move(float dt) {
	Coord3f av = Environ.get(glob).wind();
	for(int i = 0; i < nl; i++) {
	    Leaf l = leaves[i];
	    float xvd = l.xv - av.x, yvd = l.yv - av.y, zvd = l.zv - av.z;
	    float vel = (float)Math.sqrt((xvd * xvd) + (yvd * yvd) + (zvd * zvd));

	    /* Rotate the normal around the normal velocity vector. */
	    float nvl = (float)Math.sqrt((l.nxv * l.nxv) + (l.nyv * l.nyv) + (l.nzv * l.nzv));
	    if(nvl > 0) {
		float s = (float)Math.sin(nvl * dt);
		float c = (float)Math.cos(nvl * dt);
		nvl = 1.0f / nvl;
		float nxvn = l.nxv * nvl, nyvn = l.nyv * nvl, nzvn = l.nzv * nvl;
		float nx = l.nx, ny = l.ny, nz = l.nz;
		l.nx = (nx * (nxvn * nxvn * (1 - c) + c)) + (ny * (nxvn * nyvn * (1 - c) - nzvn * s)) + (nz * (nxvn * nzvn * (1 - c) + nyvn * s));
		l.ny = (nx * (nyvn * nxvn * (1 - c) + nzvn * s)) + (ny * (nyvn * nyvn * (1 - c) + c)) + (nz * (nyvn * nzvn * (1 - c) - nxvn * s));
		l.nz = (nx * (nzvn * nxvn * (1 - c) - nyvn * s)) + (ny * (nzvn * nyvn * (1 - c) + nxvn * s)) + (nz * (nzvn * nzvn * (1 - c) + c));

		float df = (float)Math.pow(0.7, dt);
		l.nxv *= df;
		l.nyv *= df;
		l.nzv *= df;
	    }

	    /* Add the cross-product of the airspeed and the normal to the normal velocity. */
	    float vr = (vel * vel) / 5.0f, ar = 0.5f;
	    float rxvd = xvd + ((rnd.nextFloat() - 0.5f) * vr), ryvd = yvd + ((rnd.nextFloat() - 0.5f) * vr), rzvd = zvd + ((rnd.nextFloat() - 0.5f) * vr);
	    float nxv = l.nxv, nyv = l.nyv, nzv = l.nzv;
	    l.nxv += (l.ny * rzvd - l.nz * ryvd) * dt * ar;
	    l.nyv += (l.nz * rxvd - l.nx * rzvd) * dt * ar;
	    l.nzv += (l.nx * ryvd - l.ny * rxvd) * dt * ar;

	    float ae = Math.abs((l.nx * xvd) + (l.ny * yvd) + (l.nz * zvd));
	    float xa = (l.nx * ae - xvd), ya = (l.ny * ae - yvd), za = (l.nz * ae - zvd);
	    l.xv += xa * Math.abs(xa) * l.ar * dt;
	    l.yv += ya * Math.abs(ya) * l.ar * dt;
	    l.zv += za * Math.abs(za) * l.ar * dt;
	    l.x += l.xv * dt;
	    l.y += l.yv * dt;
	    l.z += l.zv * dt;
	    l.zv -= 9.81f * dt;
	}
    }

    void ckstop(Glob glob) {
	for(int i = 0; i < nl; i++) {
	    if(leaves[i].vidx != i)
		throw(new AssertionError());
	    boolean drop = false;
	    try {
		drop = leaves[i].z < glob.map.getcz(leaves[i].x, -leaves[i].y) - 1;
	    } catch(Loading e) {
		drop = true;
	    }
	    if(drop) {
		leaves[i].m.remove(leaves[i]);
		(leaves[i] = leaves[--nl]).vidx = i;
		leaves[nl] = null;
		i--;
	    }
	}
    }

    public void gtick(Render d) {
	if(va == null)
	    va = new VertexArray(fmt, new VertexArray.Buffer(maxleaves * 4 * fmt.inputs[0].stride, DataBuffer.Usage.STREAM, null)).shared();
	for(MSlot m : matmap.values()) {
	    if(m.update)
		m.update(d);
	}
	d.update(va.bufs[0], this::fillvert);
    }

    public boolean tick(float dt) {
	for(MSlot m : matmap.values()) {
	    if(!m.added) {
		try {
		    RUtils.multiadd(this.slots, m);
		    m.added = true;
		} catch(Loading l) {
		}
	    }
	}
	if((ckt += dt) > 10) {
	    ckstop(glob);
	    ckt = 0;
	}
	if(nl == 0)
	    return(true);
	move(dt);
	return(false);
    }

    public Coord3f onevertex(Location.Chain loc, FastMesh m) {
	int vi = m.indb.get(rnd.nextInt(m.num));
	VertexBuf.VertexData va = m.vert.buf(VertexBuf.VertexData.class);
	Coord3f vc = new Coord3f(va.data.get(vi * 3),
				 va.data.get(vi * 3 + 1),
				 va.data.get(vi * 3 + 2));
	return(loc.fin(Matrix4f.id).mul4(vc));
    }

    public void addleaf(Leaf leaf) {
	synchronized(monitor) {
	    if(nl >= maxleaves)
		return;
	    (leaves[nl] = leaf).vidx = nl;
	    Material m = leaf.mat();
	    if((leaf.m = matmap.get(m)) == null)
		matmap.put(m, leaf.m = new MSlot(m));
	    leaf.m.add(leaf);
	    nl++;
	}
    }
}
