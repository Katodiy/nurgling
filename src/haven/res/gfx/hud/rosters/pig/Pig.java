/* Preprocessed source code */
/* $use: ui/croster */

package haven.res.gfx.hud.rosters.pig;

import haven.*;
import haven.res.ui.croster.*;
import java.util.*;

@FromResource(name = "gfx/hud/rosters/pig", version = 60)
public class Pig extends Entry {
    public int meat, milk;
    public int meatq, milkq, hideq;
    public int seedq;
    public int prc;
    public boolean hog, piglet, dead, pregnant, lactate, owned, mine;

    public Pig(long id, String name) {
	super(SIZE, id, name);
    }

    public void draw(GOut g) {
	drawbg(g);
	int i = 0;
	drawcol(g, PigRoster.cols.get(i), 0, this, namerend, i++);
	drawcol(g, PigRoster.cols.get(i), 0.5, hog,      sex, i++);
	drawcol(g, PigRoster.cols.get(i), 0.5, piglet,   growth, i++);
	drawcol(g, PigRoster.cols.get(i), 0.5, dead,     deadrend, i++);
	drawcol(g, PigRoster.cols.get(i), 0.5, pregnant, pregrend, i++);
	drawcol(g, PigRoster.cols.get(i), 0.5, lactate,  lactrend, i++);
	drawcol(g, PigRoster.cols.get(i), 0.5, (owned ? 1 : 0) | (mine ? 2 : 0), ownrend, i++);
	drawcol(g, PigRoster.cols.get(i), 1, q, quality, i++);
	drawcol(g, PigRoster.cols.get(i), 1, prc, null, i++);
	drawcol(g, PigRoster.cols.get(i), 1, meat, null, i++);
	drawcol(g, PigRoster.cols.get(i), 1, milk, null, i++);
	drawcol(g, PigRoster.cols.get(i), 1, meatq, percent, i++);
	drawcol(g, PigRoster.cols.get(i), 1, milkq, percent, i++);
	drawcol(g, PigRoster.cols.get(i), 1, hideq, percent, i++);
	drawcol(g, PigRoster.cols.get(i), 1, seedq, null, i++);
	drawcol(g, PigRoster.cols.get(i), 1, rang, null, i++);
	super.draw(g);
    }

    public boolean mousedown(Coord c, int button) {
	if(PigRoster.cols.get(1).hasx(c.x)) {
	    markall(Pig.class, o -> (o.hog == this.hog));
	    return(true);
	}
	if(PigRoster.cols.get(2).hasx(c.x)) {
	    markall(Pig.class, o -> (o.piglet == this.piglet));
	    return(true);
	}
	if(PigRoster.cols.get(3).hasx(c.x)) {
	    markall(Pig.class, o -> (o.dead == this.dead));
	    return(true);
	}
	if(PigRoster.cols.get(4).hasx(c.x)) {
	    markall(Pig.class, o -> (o.pregnant == this.pregnant));
	    return(true);
	}
	if(PigRoster.cols.get(5).hasx(c.x)) {
	    markall(Pig.class, o -> (o.lactate == this.lactate));
	    return(true);
	}
	if(PigRoster.cols.get(6).hasx(c.x)) {
	    markall(Pig.class, o -> ((o.owned == this.owned) && (o.mine == this.mine)));
	    return(true);
	}
	return(super.mousedown(c, button));
    }

	public double rang() {
		double q1 = q;
		if (hog) {
			q1 = ((q + 10) > seedq) ? seedq - 10 : q;
		}
		return Math.round(1.5 * q1 * meatq / 100 + meat);
	}
	double rang;
}

/* >wdg: PigRoster */
