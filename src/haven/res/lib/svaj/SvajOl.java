/* Preprocessed source code */
package haven.res.lib.svaj;

import haven.*;
import haven.render.*;
import haven.render.sl.*;
import static haven.render.sl.Cons.*;
import static haven.render.sl.Type.*;
import static haven.render.sl.Function.PDir.*;

@haven.FromResource(name = "lib/svaj", version = 25)
public class SvajOl extends Sprite implements Gob.SetupMod {
    public static final float v1 = 0.5f, v2 = 0.5f;
    public final Gob gob = (owner instanceof Gob) ? (Gob)owner : owner.fcontext(Gob.class, false);
    public final Coord3f zhvec, chvec;
    public final float zhfreq, chfreq;

    private static float r(float a, float b) {
	return(a + ((float)Math.random() * (b - a)));
    }

    public SvajOl(Owner owner) {
	super(owner, Resource.classres(SvajOl.class));
	this.zhvec = new Coord3f(r(-0.05f * v1, 0.05f * v1), r(-0.05f * v1, 0.05f * v1), r(-0.01f * v1, 0.01f * v1));
	this.zhfreq = r(0.05f, 0.2f);
	this.chvec = new Coord3f(r(-0.03f * v2, 0.03f * v2), r(-0.03f * v2, 0.03f * v2), r(-0.03f * v2, 0.03f * v2));
	this.chfreq = r(0.5f, 1.5f);
    }

    public static SvajOl mksprite(Owner owner, Resource res, Message sdt) {
	return(new SvajOl(owner));
    }

    private Svaj cur = null;
    private State st() {
	Coord3f origin;
	try {
	    origin = gob.getc();
	} catch(Loading l) {
	    return(cur);
	}
	origin.y = -origin.y;
	if((cur == null) || !Utils.eq(origin, cur.origin)) {
	    cur = new Svaj(zhvec, zhfreq, chvec, chfreq, origin);
	}
	return(cur);
    }

    public Pipe.Op placestate() {
	return(st());
    }

    public boolean tick(double dt) {
	return(false);
    }
}

/* >objdelta: GobSvaj */
