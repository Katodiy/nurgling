/* Preprocessed source code */
package haven.res.lib.svaj;

import haven.*;
import haven.render.*;
import haven.render.sl.*;
import static haven.render.sl.Cons.*;
import static haven.render.sl.Type.*;
import static haven.render.sl.Function.PDir.*;

@FromResource(name = "lib/svaj", version = 24)
public class GobSvaj extends GAttrib implements Gob.SetupMod {
    public static final float v1 = 0.5f, v2 = 0.25f;
    public final Coord3f zhvec, chvec;
    public final float zhfreq, chfreq;

    private static float r(float a, float b) {
	return(a + ((float)Math.random() * (b - a)));
    }

    public GobSvaj(Gob gob, float v1, float v2) {
	super(gob);
	this.zhvec = new Coord3f(r(-0.05f * v1, 0.05f * v1), r(-0.05f * v1, 0.05f * v1), r(-0.01f * v1, 0.01f * v1));
	this.zhfreq = r(0.05f, 0.2f);
	this.chvec = new Coord3f(r(-0.02f * v2, 0.02f * v2), r(-0.02f * v2, 0.02f * v2), r(-0.03f * v2, 0.03f * v2));
	this.chfreq = r(0.5f, 1.5f);
    }

    public GobSvaj(Gob gob) {
	this(gob, v1, v2);
    }

    public static void parse(Gob gob, Message sdt) {
	float V1 = v1, V2 = v2;
	if(!sdt.eom()) {
	    int fl = sdt.uint8();
	    V1 = sdt.float8();
	    V2 = sdt.float8();
	}
	gob.setattr(new GobSvaj(gob, V1, V2));
    }

    private Svaj cur = null;
    private State st() {
	Coord3f origin;
	try {
	    origin = gob.getc();
	} catch(Loading l) {
	    return(cur);
	}
	if((cur == null) || !Utils.eq(origin, cur.origin)) {
	    origin.y = -origin.y;
	    cur = new Svaj(zhvec, zhfreq, chvec, chfreq, origin);
	}
	return(cur);
    }

    public Pipe.Op placestate() {
	return(st());
    }
}
