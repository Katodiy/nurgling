/* Preprocessed source code */
/* $use: ui/croster */

package haven.res.gfx.hud.rosters.cow;

import haven.*;
import haven.res.ui.croster.*;
import nurgling.NConfiguration;

import java.util.*;

@haven.FromResource(name = "gfx/hud/rosters/cow", version = 74)
public class Ochs extends Entry {
    public int meat, milk;
    public int meatq, milkq, hideq;
    public int seedq;
    public boolean bull, calf, dead, pregnant, lactate, owned, mine;

    public Ochs(long id, String name) {
	super(SIZE, id, name);
    }

    public void draw(GOut g) {
	drawbg(g);
	int i = 0;
	drawcol(g, CowRoster.cols.get(i), 0, this, namerend, i++);
	drawcol(g, CowRoster.cols.get(i), 0.5, bull, sex, i++);
	drawcol(g, CowRoster.cols.get(i), 0.5, calf, growth, i++);
	drawcol(g, CowRoster.cols.get(i), 0.5, dead, deadrend, i++);
	drawcol(g, CowRoster.cols.get(i), 0.5, pregnant, pregrend, i++);
	drawcol(g, CowRoster.cols.get(i), 0.5, lactate, lactrend, i++);
	drawcol(g, CowRoster.cols.get(i), 0.5, (owned ? 1 : 0) | (mine ? 2 : 0), ownrend, i++);
	drawcol(g, CowRoster.cols.get(i), 1, q, quality, i++);
	drawcol(g, CowRoster.cols.get(i), 1, meat, null, i++);
	drawcol(g, CowRoster.cols.get(i), 1, milk, null, i++);
	drawcol(g, CowRoster.cols.get(i), 1, meatq, percent, i++);
	drawcol(g, CowRoster.cols.get(i), 1, milkq, percent, i++);
	drawcol(g, CowRoster.cols.get(i), 1, hideq, percent, i++);
	drawcol(g, CowRoster.cols.get(i), 1, seedq, null, i++);
	drawcol(g, CowRoster.cols.get(i), 1, rang(), null, i++);
	super.draw(g);
    }

    public boolean mousedown(Coord c, int button) {
	if(CowRoster.cols.get(1).hasx(c.x)) {
	    markall(Ochs.class, o -> (o.bull == this.bull));
	    return(true);
	}
	if(CowRoster.cols.get(2).hasx(c.x)) {
	    markall(Ochs.class, o -> (o.calf == this.calf));
	    return(true);
	}
	if(CowRoster.cols.get(3).hasx(c.x)) {
	    markall(Ochs.class, o -> (o.dead == this.dead));
	    return(true);
	}
	if(CowRoster.cols.get(4).hasx(c.x)) {
	    markall(Ochs.class, o -> (o.pregnant == this.pregnant));
	    return(true);
	}
	if(CowRoster.cols.get(5).hasx(c.x)) {
	    markall(Ochs.class, o -> (o.lactate == this.lactate));
	    return(true);
	}
	if(CowRoster.cols.get(6).hasx(c.x)) {
	    markall(Ochs.class, o -> ((o.owned == this.owned) && (o.mine == this.mine)));
	    return(true);
	}
	return(super.mousedown(c, button));
    }

	public double rang() {
		NConfiguration.CowsHerd herd = NConfiguration.getInstance().cowsHerd;
		double q1 = q;
		q1 = ((q + herd.breedingGap) > seedq) ? seedq - herd.breedingGap : q;
		return Math.round(herd.milkq * q1 * milkq / 100 + herd.milkquan * milk + herd.meatq * q1 * meatq / 100 + herd.meatquan * meat + (seedq - q - herd.breedingGap));
	}
}

/* >wdg: CowRoster */
