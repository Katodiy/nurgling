/* Preprocessed source code */
/* $use: ui/croster */

package haven.res.gfx.hud.rosters.goat;

import haven.*;
import haven.res.gfx.hud.rosters.sheep.SheepRoster;
import haven.res.ui.croster.*;
import nurgling.NConfiguration;

import java.util.*;

@FromResource(name = "gfx/hud/rosters/goat", version = 62)
public class Goat extends Entry {
    public int meat, milk, wool;
    public int meatq, milkq, woolq, hideq;
    public int seedq;
    public boolean billy, kid, dead, pregnant, lactate, owned, mine;

    public Goat(long id, String name) {
	super(SIZE, id, name);
    }

    public void draw(GOut g) {
	drawbg(g);
	int i = 0;
	drawcol(g, GoatRoster.cols.get(i), 0, this, namerend, i++);
	drawcol(g, GoatRoster.cols.get(i), 0.5, billy,    sex, i++);
	drawcol(g, GoatRoster.cols.get(i), 0.5, kid,      growth, i++);
	drawcol(g, GoatRoster.cols.get(i), 0.5, dead,     deadrend, i++);
	drawcol(g, GoatRoster.cols.get(i), 0.5, pregnant, pregrend, i++);
	drawcol(g, GoatRoster.cols.get(i), 0.5, lactate,  lactrend, i++);
	drawcol(g, GoatRoster.cols.get(i), 0.5, (owned ? 1 : 0) | (mine ? 2 : 0), ownrend, i++);
	drawcol(g, GoatRoster.cols.get(i), 1, q, quality, i++);
	drawcol(g, GoatRoster.cols.get(i), 1, meat, null, i++);
	drawcol(g, GoatRoster.cols.get(i), 1, milk, null, i++);
	drawcol(g, GoatRoster.cols.get(i), 1, wool, null, i++);
	drawcol(g, GoatRoster.cols.get(i), 1, meatq, percent, i++);
	drawcol(g, GoatRoster.cols.get(i), 1, milkq, percent, i++);
	drawcol(g, GoatRoster.cols.get(i), 1, woolq, percent, i++);
	drawcol(g, GoatRoster.cols.get(i), 1, hideq, percent, i++);
	drawcol(g, GoatRoster.cols.get(i), 1, seedq, null, i++);
	drawcol(g, GoatRoster.cols.get(i), 1, rang(), null, i++);
	super.draw(g);
    }

    public boolean mousedown(Coord c, int button) {
	if(GoatRoster.cols.get(1).hasx(c.x)) {
	    markall(Goat.class, o -> (o.billy == this.billy));
	    return(true);
	}
	if(GoatRoster.cols.get(2).hasx(c.x)) {
	    markall(Goat.class, o -> (o.kid == this.kid));
	    return(true);
	}
	if(GoatRoster.cols.get(3).hasx(c.x)) {
	    markall(Goat.class, o -> (o.dead == this.dead));
	    return(true);
	}
	if(GoatRoster.cols.get(4).hasx(c.x)) {
	    markall(Goat.class, o -> (o.pregnant == this.pregnant));
	    return(true);
	}
	if(GoatRoster.cols.get(5).hasx(c.x)) {
	    markall(Goat.class, o -> (o.lactate == this.lactate));
	    return(true);
	}
	if(GoatRoster.cols.get(6).hasx(c.x)) {
	    markall(Goat.class, o -> ((o.owned == this.owned) && (o.mine == this.mine)));
	    return(true);
	}
	return(super.mousedown(c, button));
    }

	public double rang() {
		NConfiguration.GoatsHerd herd = NConfiguration.getInstance().goatsHerd;
		double q1 = q;
		if (billy) {
			q1 = ((q + herd.breedingGap) > seedq) ? seedq - herd.breedingGap : q;
		}
		return Math.round(herd.milkq * q1 * milkq / 100 + herd.milkquan * milk + herd.meatq * q1 * meatq / 100 + herd.meatquan * meat + herd.woolq * q1 * woolq / 100 + herd.woolquan * wool);
	}
	double rang;
}

/* >wdg: GoatRoster */
