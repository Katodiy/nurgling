/* Preprocessed source code */
/* $use: ui/croster */

package haven.res.gfx.hud.rosters.goat;

import haven.*;
import haven.res.gfx.hud.rosters.sheep.Sheep;
import haven.res.ui.croster.*;
import java.util.*;

@FromResource(name = "gfx/hud/rosters/goat", version = 61)
public class GoatRoster extends CattleRoster<Goat> {
    public static List<Column> cols = initcols(
	new Column<Entry>("Name", Comparator.comparing((Entry e) -> e.name), 200),

	new Column<Goat>(Resource.local().load("hud/rosters/sex", 2),      Comparator.comparing((Goat e) -> e.billy).reversed(), 20).runon(),
	new Column<Goat>(Resource.local().load("hud/rosters/growth", 2),   Comparator.comparing((Goat e) -> e.kid).reversed(), 20).runon(),
	new Column<Goat>(Resource.local().load("hud/rosters/deadp", 3),    Comparator.comparing((Goat e) -> e.dead).reversed(), 20).runon(),
	new Column<Goat>(Resource.local().load("hud/rosters/pregnant", 2), Comparator.comparing((Goat e) -> e.pregnant).reversed(), 20).runon(),
	new Column<Goat>(Resource.local().load("hud/rosters/lactate", 1),  Comparator.comparing((Goat e) -> e.lactate).reversed(), 20).runon(),
	new Column<Goat>(Resource.local().load("hud/rosters/owned", 1),    Comparator.comparing((Goat e) -> ((e.owned ? 1 : 0) | (e.mine ? 2 : 0))).reversed(), 20),

	new Column<Goat>(Resource.local().load("hud/rosters/quality", 2), Comparator.comparing((Goat e) -> e.q).reversed()),

	new Column<Goat>(Resource.local().load("hud/rosters/meatquantity", 1), Comparator.comparing((Goat e) -> e.meat).reversed()),
	new Column<Goat>(Resource.local().load("hud/rosters/milkquantity", 1), Comparator.comparing((Goat e) -> e.milk).reversed()),
	new Column<Goat>(Resource.local().load("hud/rosters/woolquantity", 1), Comparator.comparing((Goat e) -> e.milk).reversed()),

	new Column<Goat>(Resource.local().load("hud/rosters/meatquality", 1), Comparator.comparing((Goat e) -> e.meatq).reversed()),
	new Column<Goat>(Resource.local().load("hud/rosters/milkquality", 1), Comparator.comparing((Goat e) -> e.milkq).reversed()),
	new Column<Goat>(Resource.local().load("hud/rosters/woolquality", 1), Comparator.comparing((Goat e) -> e.milkq).reversed()),
	new Column<Goat>(Resource.local().load("hud/rosters/hidequality", 1), Comparator.comparing((Goat e) -> e.hideq).reversed()),

	new Column<Goat>(Resource.local().load("hud/rosters/breedingquality", 1), Comparator.comparing((Goat e) -> e.seedq).reversed()),
		new Column<Goat>(Resource.local().load("hud/rosters/rang", 1), Comparator.comparing((Goat e) -> e.rang).reversed())
    );
    protected List<Column> cols() {return(cols);}

    public static CattleRoster mkwidget(UI ui, Object... args) {
	return(new GoatRoster());
    }

    public Goat parse(Object... args) {
	int n = 0;
	long id = (Long)args[n++];
	String name = (String)args[n++];
	Goat ret = new Goat(id, name);
	ret.grp = (Integer)args[n++];
	int fl = (Integer)args[n++];
	ret.billy = (fl & 1) != 0;
	ret.kid = (fl & 2) != 0;
	ret.dead = (fl & 4) != 0;
	ret.pregnant = (fl & 8) != 0;
	ret.lactate = (fl & 16) != 0;
	ret.owned = (fl & 32) != 0;
	ret.mine = (fl & 64) != 0;
	ret.q = ((Number)args[n++]).doubleValue();
	ret.meat = (Integer)args[n++];
	ret.milk = (Integer)args[n++];
	ret.wool = (Integer)args[n++];
	ret.meatq = (Integer)args[n++];
	ret.milkq = (Integer)args[n++];
	ret.woolq = (Integer)args[n++];
	ret.hideq = (Integer)args[n++];
	ret.seedq = (Integer)args[n++];
	ret.rang = ret.rang();
	return(ret);
    }

    public TypeButton button() {
	return(typebtn(Resource.local().load("hud/rosters/btn-goat", 4),
		       Resource.local().load("hud/rosters/btn-goat-d", 3)));
    }
}
