/* Preprocessed source code */
package haven.res.lib.svaj;

import haven.*;
import haven.render.*;
import haven.render.sl.*;
import static haven.render.sl.Cons.*;
import static haven.render.sl.Type.*;
import static haven.render.sl.Function.PDir.*;

@FromResource(name = "lib/svaj", version = 24)
public class SvajOl extends Sprite implements Gob.SetupMod {
    public static final float v1 = 0.5f, v2 = 0.5f;
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
	if(!(owner instanceof Gob))
	    return(null);
	Gob gob = (Gob)owner;
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

    public boolean tick(double dt) {
	return(false);
    }
}

/* >objdelta: GobSvaj */
