/* Preprocessed source code */
/* $use: ui/croster */

package haven.res.gfx.hud.rosters.cow;

import haven.Coord;
import haven.GOut;
import haven.res.ui.croster.Entry;
import nurgling.NConfiguration;

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
		if (CowRoster.cols.get(1).hasx(c.x)) {
			markall(Ochs.class, o -> (o.bull == this.bull));
			return (true);
		}
		if (CowRoster.cols.get(2).hasx(c.x)) {
			markall(Ochs.class, o -> (o.calf == this.calf));
			return (true);
		}
		if (CowRoster.cols.get(3).hasx(c.x)) {
			markall(Ochs.class, o -> (o.dead == this.dead));
			return (true);
		}
		if (CowRoster.cols.get(4).hasx(c.x)) {
			markall(Ochs.class, o -> (o.pregnant == this.pregnant));
			return (true);
		}
		if (CowRoster.cols.get(5).hasx(c.x)) {
			markall(Ochs.class, o -> (o.lactate == this.lactate));
			return (true);
		}
		if (CowRoster.cols.get(6).hasx(c.x)) {
			markall(Ochs.class, o -> ((o.owned == this.owned) && (o.mine == this.mine)));
			return (true);
		}
		return (super.mousedown(c, button));
	}

	public double rang() {
		if(NConfiguration.getInstance().selected_cowsHerd.isEmpty())
			return 0;

		NConfiguration.CowsHerd herd = NConfiguration.getInstance().cowsHerd.get(NConfiguration.getInstance().selected_cowsHerd);

		double ql = (!herd.ignoreBD || bull)?(q > (seedq - herd.breedingGap.get())) ? (q + seedq - herd.breedingGap.get()) / 2. : q + ((seedq - herd.breedingGap.get()) - q) * herd.coverbreed.get():q;
		double m = ql * herd.meatq.get() * meatq / 100.;
		double qm = meat * herd.meatquan1.get() + ((meat > herd.meatquanth.get()) ? ((meat - herd.meatquanth.get()) * (herd.meatquan2.get() - herd.meatquan1.get())) : 0);
		double _milk = ql * herd.milkq.get() * milkq / 100.;
		double qmilk = milk * herd.milkquan1.get() + ((milk > herd.milkquanth.get()) ? ((milk - herd.milkquanth.get()) * (herd.milkquan2.get() - herd.milkquan1.get())) : 0);
		double hide = ql * herd.hideq.get() * hideq / 100.;
		double k_res = (m + qm + _milk + qmilk + hide);
		return k_res == 0 ? ql : Math.round(k_res * 10) / 10.;
	}
}

/* >wdg: CowRoster */
