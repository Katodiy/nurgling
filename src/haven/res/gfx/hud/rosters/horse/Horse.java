/* Preprocessed source code */
/* $use: ui/croster */

package haven.res.gfx.hud.rosters.horse;

import haven.Coord;
import haven.GOut;
import haven.res.ui.croster.Entry;
import nurgling.NConfiguration;

@haven.FromResource(name = "gfx/hud/rosters/horse", version = 62)
public class Horse extends Entry {
    public int meat, milk;
    public int meatq, milkq, hideq;
    public int seedq;
    public int end, stam, mb;
    public boolean stallion, foal, dead, pregnant, lactate, owned, mine;

    public Horse(long id, String name) {
	super(SIZE, id, name);
    }

    public void draw(GOut g) {
	drawbg(g);
	int i = 0;
	drawcol(g, HorseRoster.cols.get(i), 0, this, namerend, i++);
	drawcol(g, HorseRoster.cols.get(i), 0.5, stallion, sex, i++);
	drawcol(g, HorseRoster.cols.get(i), 0.5, foal,     growth, i++);
	drawcol(g, HorseRoster.cols.get(i), 0.5, dead,     deadrend, i++);
	drawcol(g, HorseRoster.cols.get(i), 0.5, pregnant, pregrend, i++);
	drawcol(g, HorseRoster.cols.get(i), 0.5, lactate,  lactrend, i++);
	drawcol(g, HorseRoster.cols.get(i), 0.5, (owned ? 1 : 0) | (mine ? 2 : 0), ownrend, i++);
	drawcol(g, HorseRoster.cols.get(i), 1, q, quality, i++);
	drawcol(g, HorseRoster.cols.get(i), 1, end, null, i++);
	drawcol(g, HorseRoster.cols.get(i), 1, stam, null, i++);
	drawcol(g, HorseRoster.cols.get(i), 1, mb, null, i++);
	drawcol(g, HorseRoster.cols.get(i), 1, meat, null, i++);
	drawcol(g, HorseRoster.cols.get(i), 1, milk, null, i++);
	drawcol(g, HorseRoster.cols.get(i), 1, meatq, percent, i++);
	drawcol(g, HorseRoster.cols.get(i), 1, milkq, percent, i++);
	drawcol(g, HorseRoster.cols.get(i), 1, hideq, percent, i++);
	drawcol(g, HorseRoster.cols.get(i), 1, seedq, null, i++);
	drawcol(g, HorseRoster.cols.get(i), 1, rang(), null, i++);
	super.draw(g);
    }

    public boolean mousedown(Coord c, int button) {
	if(HorseRoster.cols.get(1).hasx(c.x)) {
	    markall(Horse.class, o -> (o.stallion == this.stallion));
	    return(true);
	}
	if(HorseRoster.cols.get(2).hasx(c.x)) {
	    markall(Horse.class, o -> (o.foal == this.foal));
	    return(true);
	}
	if(HorseRoster.cols.get(3).hasx(c.x)) {
	    markall(Horse.class, o -> (o.dead == this.dead));
	    return(true);
	}
	if(HorseRoster.cols.get(4).hasx(c.x)) {
	    markall(Horse.class, o -> (o.pregnant == this.pregnant));
	    return(true);
	}
	if(HorseRoster.cols.get(5).hasx(c.x)) {
	    markall(Horse.class, o -> (o.lactate == this.lactate));
	    return(true);
	}
	if(HorseRoster.cols.get(6).hasx(c.x)) {
	    markall(Horse.class, o -> ((o.owned == this.owned) && (o.mine == this.mine)));
	    return(true);
	}
	return(super.mousedown(c, button));
    }
	public double rang() {
		if(NConfiguration.getInstance().selected_horsesHerd.isEmpty())
			return 0;

		NConfiguration.HorsesHerd herd = NConfiguration.getInstance().horsesHerd.get(NConfiguration.getInstance().selected_horsesHerd);

		double ql = (q > (seedq - herd.breedingGap.get())) ? (q + seedq - herd.breedingGap.get()) / 2. : q + ((seedq - herd.breedingGap.get()) - q) * herd.coverbreed.get();
		double m = ql * herd.meatq.get() * meatq / 100.;
		double qm = meat * herd.meatquan1.get() + ((meat > herd.meatquanth.get()) ? ((meat - herd.meatquanth.get()) * (herd.meatquan2.get() - herd.meatquan1.get())) : 0);
		double _stam = milk * herd.stam1.get() + ((milk > herd.stamth.get()) ? ((milk - herd.stamth.get()) * (herd.stam2.get() - herd.stam1.get())) : 0);
		double hide = ql * herd.hideq.get() * hideq / 100.;
		double _end = herd.enduran.get() * end;
		double _meta = herd.meta.get() * mb;

		return Math.round((m + qm + _stam + _meta + _end + hide) * 10) / 10.;
	}
}

/* >wdg: HorseRoster */
