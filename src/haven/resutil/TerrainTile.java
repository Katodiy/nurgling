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

package haven.resutil;

import haven.*;
import java.util.*;
import java.awt.Color;

public class TerrainTile extends Tiler {
    public final GLState base;
    public final SNoise3 noise;
    public final Var[] var;

    public static class Var {
	public GLState mat;
	public double thr;
	public double nz;

	public Var(GLState mat, double thr, double nz) {
	    this.mat = mat; this.thr = thr; this.nz = nz;
	}
    }

    public static class Scan {
        public final Coord ul, sz, br;
        public final int l;

        public Scan(Coord ul, Coord sz) {
            this.ul = ul;
            this.sz = sz;
            this.br = sz.add(ul);
            this.l = sz.x * sz.y;
        }

        public int o(int x, int y) {
            return((x - ul.x) + ((y - ul.y) * sz.x));
        }

        public int o(Coord in) {
            return(o(in.x, in.y));
        }
    }

    private static final int sr = 12;
    public class Blend {
	final MapMesh m;
	final Scan vs, es;
	final float[][] bv;
	final boolean[][] en;

	private Blend(MapMesh m) {
	    this.m = m;
	    vs = new Scan(Coord.z.sub(sr, sr), m.sz.add(sr * 2 + 1, sr * 2 + 1));
	    float[][] buf1 = new float[var.length + 1][vs.l];
	    setbase(buf1);
	    for(int i = 0; i < sr; i++) {
		float[][] buf2 = new float[var.length + 1][vs.l];
		for(int y = vs.ul.y; y < vs.br.y; y++) {
		    for(int x = vs.ul.x; x < vs.br.x; x++) {
			for(int o = 0; o < var.length + 1; o++) {
			    float s = buf1[o][vs.o(x, y)] * 4;
			    float w = 4;
			    float lw = (float)noise.getr(0.5, 1.5, 32, x + m.ul.x, y + m.ul.y, o * 23);
			    if(lw < 0)
				lw = lw * lw * lw;
			    else
				lw = lw * lw;
			    if(x > vs.ul.x) {
				s += buf1[o][vs.o(x - 1, y)] * lw;
				w += lw;
			    }
			    if(y > vs.ul.y) {
				s += buf1[o][vs.o(x, y - 1)] * lw;
				w += lw;
			    }
			    if(x < vs.br.x - 1) {
				s += buf1[o][vs.o(x + 1, y)] * lw;
				w += lw;
			    }
			    if(y < vs.br.y - 1) {
				s += buf1[o][vs.o(x, y + 1)] * lw;
				w += lw;
			    }
			    buf2[o][vs.o(x, y)] = s / w;
			}
		    }
		}
		buf1 = buf2;
	    }
	    bv = buf1;
	    for(int y = vs.ul.y; y < vs.br.y; y++) {
		for(int x = vs.ul.x; x < vs.br.x; x++) {
		    for(int i = 0; i < var.length + 1; i++) {
			float v = bv[i][vs.o(x, y)];
			v = v * 1.2f - 0.1f;
			if(v < 0)
			    v = 0;
			else if(v > 1)
			    v = 1;
			else
			    v = 0.25f + (0.75f * v);
			bv[i][vs.o(x, y)] = v;
		    }
		}
	    }
	    es = new Scan(Coord.z, m.sz);
	    en = new boolean[var.length + 1][es.l];
	    for(int y = es.ul.y; y < es.br.y; y++) {
		for(int x = es.ul.x; x < es.br.x; x++) {
		    boolean fall = false;
		    for(int i = var.length; i >= 0; i--) {
			if(fall) {
			    en[i][es.o(x, y)] = false;
			} else if((bv[i][vs.o(x    , y    )] < 0.001f) && (bv[i][vs.o(x + 1, y    )] < 0.001f) &&
				  (bv[i][vs.o(x    , y + 1)] < 0.001f) && (bv[i][vs.o(x + 1, y + 1)] < 0.001f)) {
			    en[i][es.o(x, y)] = false;
			} else {
			    en[i][es.o(x, y)] = true;
			    if((bv[i][vs.o(x    , y    )] > 0.99f) && (bv[i][vs.o(x + 1, y    )] > 0.99f) &&
			       (bv[i][vs.o(x    , y + 1)] > 0.99f) && (bv[i][vs.o(x + 1, y + 1)] > 0.99f)) {
				fall = true;
			    }
			}
		    }
		}
	    }
	}

	private void setbase(float[][] bv) {
	    for(int y = vs.ul.y; y < vs.br.y - 1; y++) {
		for(int x = vs.ul.x; x < vs.br.x - 1; x++) {
		    fall: {
			for(int i = var.length - 1; i >= 0; i--) {
			    Var v = var[i];
			    double n = 0;
			    for(double s = 64; s >= 8; s /= 2)
				n += noise.get(s, x + m.ul.x, y + m.ul.y, v.nz);
			    if((n / 2) >= v.thr) {
				bv[i + 1][vs.o(x, y)] = 1;
				bv[i + 1][vs.o(x + 1, y)] = 1;
				bv[i + 1][vs.o(x, y + 1)] = 1;
				bv[i + 1][vs.o(x + 1, y + 1)] = 1;
				break fall;
			    }
			}
			bv[0][vs.o(x, y)] = 1;
			bv[0][vs.o(x + 1, y)] = 1;
			bv[0][vs.o(x, y + 1)] = 1;
			bv[0][vs.o(x + 1, y + 1)] = 1;
		    }
		}
	    }
	}
    }
    private final MapMesh.DataID<Blend> blend = new MapMesh.DataID<Blend>() {
	public Blend make(MapMesh m) {
	    return(new Blend(m));
	}
    };

    @ResName("trn")
    public static class Factory implements Tiler.Factory {
	public Tiler create(int id, Resource.Tileset set) {
	    Resource res = set.getres();
	    Material base = null;
	    Collection<Var> var = new LinkedList<Var>();
	    for(Object rdesc : set.ta) {
		Object[] desc = (Object[])rdesc;
		String p = (String)desc[0];
		if(p.equals("base")) {
		    int mid = (Integer)desc[1];
		    base = res.layer(Material.Res.class, mid).get();
		} else if(p.equals("var")) {
		    int mid = (Integer)desc[1];
		    float thr = (Float)desc[2];
		    double nz = (res.name.hashCode() * mid * 8129) % 10000;
		    var.add(new Var(res.layer(Material.Res.class, mid).get(), thr, nz));
		}
	    }
	    return(new TerrainTile(id, res.name.hashCode(), base, var.toArray(new Var[0])));
	}
    }

    public TerrainTile(int id, long nseed, GLState base, Var[] var) {
	super(id);
	this.noise = new SNoise3(nseed);
	this.base = GLState.compose(base, States.vertexcolor);
	for(Var v : this.var = var)
	    v.mat = GLState.compose(v.mat, States.vertexcolor);
    }

    public class Plane extends MapMesh.Shape {
	public MapMesh.SPoint[] vrt;
	public Coord3f[] tc;
	public int[] alpha;

	public Plane(MapMesh m, MapMesh.Surface surf, Coord sc, int z, GLState mat, int[] alpha) {
	    m.super(z, mat);
	    vrt = surf.fortile(sc);
	    float fac = 25f / 4f;
	    tc = new Coord3f[] {
		new Coord3f((sc.x + 0) / fac, (sc.y + 0) / fac, 0),
		new Coord3f((sc.x + 0) / fac, (sc.y + 1) / fac, 0),
		new Coord3f((sc.x + 1) / fac, (sc.y + 1) / fac, 0),
		new Coord3f((sc.x + 1) / fac, (sc.y + 0) / fac, 0),
	    };
	    this.alpha = alpha;
	}

	public void build(MeshBuf buf) {
	    MeshBuf.Tex btex = buf.layer(MeshBuf.tex);
	    MeshBuf.Col bcol = buf.layer(MeshBuf.col);
	    MeshBuf.Vertex v1 = buf.new Vertex(vrt[0].pos, vrt[0].nrm);
	    MeshBuf.Vertex v2 = buf.new Vertex(vrt[1].pos, vrt[1].nrm);
	    MeshBuf.Vertex v3 = buf.new Vertex(vrt[2].pos, vrt[2].nrm);
	    MeshBuf.Vertex v4 = buf.new Vertex(vrt[3].pos, vrt[3].nrm);
	    btex.set(v1, tc[0]); bcol.set(v1, new Color(255, 255, 255, alpha[0]));
	    btex.set(v2, tc[1]); bcol.set(v2, new Color(255, 255, 255, alpha[1]));
	    btex.set(v3, tc[2]); bcol.set(v3, new Color(255, 255, 255, alpha[2]));
	    btex.set(v4, tc[3]); bcol.set(v4, new Color(255, 255, 255, alpha[3]));
	    MapMesh.splitquad(buf, v1, v2, v3, v4);
	}
    }

    public void lay(MapMesh m, Random rnd, Coord lc, Coord gc) {
	Blend b = m.data(blend);
	for(int i = 0; i < var.length + 1; i++) {
	    GLState mat = (i == 0)?base:(var[i - 1].mat);
	    if(b.en[i][b.es.o(lc)])
		new Plane(m, m.gnd(), lc, i, mat, new int[] {
			(int)(b.bv[i][b.vs.o(lc)] * 255),
			(int)(b.bv[i][b.vs.o(lc.add(0, 1))] * 255),
			(int)(b.bv[i][b.vs.o(lc.add(1, 1))] * 255),
			(int)(b.bv[i][b.vs.o(lc.add(1, 0))] * 255),
		    });
	}
    }

    public void trans(MapMesh m, Random rnd, Tiler gt, Coord lc, Coord gc, int z, int bmask, int cmask) {
    }
}