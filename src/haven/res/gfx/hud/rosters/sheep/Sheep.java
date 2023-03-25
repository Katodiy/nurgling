/* Preprocessed source code */
/* $use: ui/croster */

package haven.res.gfx.hud.rosters.sheep;

import haven.*;
import haven.res.ui.croster.*;
import nurgling.NConfiguration;

import java.util.*;

@haven.FromResource(name = "gfx/hud/rosters/sheep", version = 64)
public class Sheep extends Entry {
    public int meat, milk, wool;
    public int meatq, milkq, woolq, hideq;
    public int seedq;
    public boolean ram, lamb, dead, pregnant, lactate, owned, mine;

    public Sheep(long id, String name) {
	super(SIZE, id, name);
    }

    public void draw(GOut g) {
	drawbg(g);
	int i = 0;
	drawcol(g, SheepRoster.cols.get(i), 0, this, namerend, i++);
	drawcol(g, SheepRoster.cols.get(i), 0.5, ram,      sex, i++);
	drawcol(g, SheepRoster.cols.get(i), 0.5, lamb,     growth, i++);
	drawcol(g, SheepRoster.cols.get(i), 0.5, dead,     deadrend, i++);
	drawcol(g, SheepRoster.cols.get(i), 0.5, pregnant, pregrend, i++);
	drawcol(g, SheepRoster.cols.get(i), 0.5, lactate,  lactrend, i++);
	drawcol(g, SheepRoster.cols.get(i), 0.5, (owned ? 1 : 0) | (mine ? 2 : 0), ownrend, i++);
	drawcol(g, SheepRoster.cols.get(i), 1, q, quality, i++);
	drawcol(g, SheepRoster.cols.get(i), 1, meat, null, i++);
	drawcol(g, SheepRoster.cols.get(i), 1, milk, null, i++);
	drawcol(g, SheepRoster.cols.get(i), 1, wool, null, i++);
	drawcol(g, SheepRoster.cols.get(i), 1, meatq, percent, i++);
	drawcol(g, SheepRoster.cols.get(i), 1, milkq, percent, i++);
	drawcol(g, SheepRoster.cols.get(i), 1, woolq, percent, i++);
	drawcol(g, SheepRoster.cols.get(i), 1, hideq, percent, i++);
	drawcol(g, SheepRoster.cols.get(i), 1, seedq, null, i++);
	drawcol(g, SheepRoster.cols.get(i), 1, rang(), null, i++);
	super.draw(g);
    }

    public boolean mousedown(Coord c, int button) {
	if(SheepRoster.cols.get(1).hasx(c.x)) {
	    markall(Sheep.class, o -> (o.ram == this.ram));
	    return(true);
	}
	if(SheepRoster.cols.get(2).hasx(c.x)) {
	    markall(Sheep.class, o -> (o.lamb == this.lamb));
	    return(true);
	}
	if(SheepRoster.cols.get(3).hasx(c.x)) {
	    markall(Sheep.class, o -> (o.dead == this.dead));
	    return(true);
	}
	if(SheepRoster.cols.get(4).hasx(c.x)) {
	    markall(Sheep.class, o -> (o.pregnant == this.pregnant));
	    return(true);
	}
	if(SheepRoster.cols.get(5).hasx(c.x)) {
	    markall(Sheep.class, o -> (o.lactate == this.lactate));
	    return(true);
	}
	if(SheepRoster.cols.get(6).hasx(c.x)) {
	    markall(Sheep.class, o -> ((o.owned == this.owned) && (o.mine == this.mine)));
	    return(true);
	}
	return(super.mousedown(c, button));
    }

	public double rang() {
		NConfiguration.SheepsHerd herd = NConfiguration.getInstance().sheepsHerd;
		double q1 = q;
		q1 = ((q + herd.breedingGap) > seedq) ? seedq - herd.breedingGap : q;
		return Math.round(herd.milkq * q1 * milkq / 100 + herd.milkquan * milk + herd.meatq * q1 * meatq / 100 + herd.meatquan * meat + herd.woolq * q1 * woolq / 100 + herd.woolquan * wool + (seedq - q - herd.breedingGap));
	}
}

/* >wdg: SheepRoster */
