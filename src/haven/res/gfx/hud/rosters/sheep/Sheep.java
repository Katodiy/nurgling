/* Preprocessed source code */
/* $use: ui/croster */

package haven.res.gfx.hud.rosters.sheep;

import haven.Coord;
import haven.GOut;
import haven.res.ui.croster.Entry;
import nurgling.NConfiguration;

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

		double ql = (q < (seedq - herd.breedingGap.get())) ? (q + seedq - herd.breedingGap.get()) / 2 : q + (q - seedq - herd.breedingGap.get())*herd.coverbreed.get();
		double m = ql * herd.meatq.get()* hideq/100.;
		double qm = meat * herd.meatquan1.get() + (( meat > herd.meatquanth.get()) ? ((meat - herd.meatquanth.get()) * (herd.meatquan2.get() - herd.meatquan1.get())) : 0);
		double _milk = ql * herd.milkq.get()* milkq/100.;
		double qmilk = milk * herd.milkquan1.get() + ((milk > herd.milkquanth.get()) ? ((milk - herd.milkquanth.get()) * (herd.milkquan2.get() - herd.milkquan1.get())) : 0);
		double _wool = ql * herd.woolq.get()* woolq/100.;
		double qwool = wool * herd.woolquan1.get() + ((wool > herd.woolquanth.get()) ? ((wool - herd.woolquanth.get()) * (herd.woolquan2.get() - herd.woolquan1.get())) : 0);
		double hide = ql * herd.hideq.get() * hideq/100.;

		return Math.round((m + qm + _milk + qmilk + _wool + qwool + hide)*10)/10.;
	}
}

/* >wdg: SheepRoster */
