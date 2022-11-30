/* Preprocessed source code */
/* $use: ui/tt/defn */

package haven.res.lib.layspr;

import haven.*;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.*;

@FromResource(name = "lib/layspr", version = 14)
class Animation extends Layer {
    Resource.Anim a;
    double te, dur;
    int cf;

    static Coord sz(Resource.Anim a) {
	Coord ret = new Coord();
	for(int i = 0; i < a.f.length; i++) {
	    for(int o = 0; o < a.f[i].length; o++) {
		ret.x = Math.max(ret.x, a.f[i][o].ssz.x);
		ret.y = Math.max(ret.y, a.f[i][o].ssz.y);
	    }
	}
	return(ret);
    }

    Animation(Resource.Anim anim) {
	super(anim.f[0][0].z, sz(anim));
	this.a = anim;
	this.dur = anim.d / 1000.0;
	te = 0;
    }

    void tick(double dt) {
	te += dt;
	while(te > dur) {
	    te -= dur;
	    cf = (cf + 1) % a.f.length;
	}
    }

    void draw(GOut g) {
	for(int i = 0; i < a.f[cf].length; i++)
	    g.image(a.f[cf][i], Coord.z);
    }
}
