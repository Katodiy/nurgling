/* Preprocessed source code */
/* $use: ui/croster */

package haven.res.gfx.hud.rosters.horse;

import haven.*;
import haven.res.ui.croster.*;
import java.util.*;

@FromResource(name = "gfx/hud/rosters/horse", version = 60)
public class HorseRoster extends CattleRoster<Horse> {
    public static List<Column> cols = initcols(
	new Column<Entry>("Name", Comparator.comparing((Entry e) -> e.name), 200),

	new Column<Horse>(Resource.local().load("hud/rosters/sex", 2),      Comparator.comparing((Horse e) -> e.stallion).reversed(), 20).runon(),
	new Column<Horse>(Resource.local().load("hud/rosters/growth", 2),   Comparator.comparing((Horse e) -> e.foal).reversed(), 20).runon(),
	new Column<Horse>(Resource.local().load("hud/rosters/deadp", 3),    Comparator.comparing((Horse e) -> e.dead).reversed(), 20).runon(),
	new Column<Horse>(Resource.local().load("hud/rosters/pregnant", 2), Comparator.comparing((Horse e) -> e.pregnant).reversed(), 20).runon(),
	new Column<Horse>(Resource.local().load("hud/rosters/lactate", 1),  Comparator.comparing((Horse e) -> e.lactate).reversed(), 20).runon(),
	new Column<Horse>(Resource.local().load("hud/rosters/owned", 1),    Comparator.comparing((Horse e) -> ((e.owned ? 1 : 0) | (e.mine ? 2 : 0))).reversed(), 20),

	new Column<Horse>(Resource.local().load("hud/rosters/quality", 2), Comparator.comparing((Horse e) -> e.q).reversed()),

	new Column<Horse>(Resource.local().load("hud/rosters/endurance", 1), Comparator.comparing((Horse e) -> e.end).reversed()),
	new Column<Horse>(Resource.local().load("hud/rosters/stamina", 1), Comparator.comparing((Horse e) -> e.stam).reversed()),
	new Column<Horse>(Resource.local().load("hud/rosters/metabolism", 1), Comparator.comparing((Horse e) -> e.mb).reversed()),

	new Column<Horse>(Resource.local().load("hud/rosters/meatquantity", 1), Comparator.comparing((Horse e) -> e.meat).reversed()),
	new Column<Horse>(Resource.local().load("hud/rosters/milkquantity", 1), Comparator.comparing((Horse e) -> e.milk).reversed()),

	new Column<Horse>(Resource.local().load("hud/rosters/meatquality", 1), Comparator.comparing((Horse e) -> e.meatq).reversed()),
	new Column<Horse>(Resource.local().load("hud/rosters/milkquality", 1), Comparator.comparing((Horse e) -> e.milkq).reversed()),
	new Column<Horse>(Resource.local().load("hud/rosters/hidequality", 1), Comparator.comparing((Horse e) -> e.hideq).reversed()),

	new Column<Horse>(Resource.local().load("hud/rosters/breedingquality", 1), Comparator.comparing((Horse e) -> e.seedq).reversed())
    );
    protected List<Column> cols() {return(cols);}

    public static CattleRoster mkwidget(UI ui, Object... args) {
	return(new HorseRoster());
    }

    public Horse parse(Object... args) {
	int n = 0;
	long id = (Long)args[n++];
	String name = (String)args[n++];
	Horse ret = new Horse(id, name);
	ret.grp = (Integer)args[n++];
	int fl = (Integer)args[n++];
	ret.stallion = (fl & 1) != 0;
	ret.foal = (fl & 2) != 0;
	ret.dead = (fl & 4) != 0;
	ret.pregnant = (fl & 8) != 0;
	ret.lactate = (fl & 16) != 0;
	ret.owned = (fl & 32) != 0;
	ret.mine = (fl & 64) != 0;
	ret.q = ((Number)args[n++]).doubleValue();
	ret.meat = (Integer)args[n++];
	ret.milk = (Integer)args[n++];
	ret.meatq = (Integer)args[n++];
	ret.milkq = (Integer)args[n++];
	ret.hideq = (Integer)args[n++];
	ret.seedq = (Integer)args[n++];
	ret.end = (Integer)args[n++];
	ret.stam = (Integer)args[n++];
	ret.mb = (Integer)args[n++];
	return(ret);
    }

    public TypeButton button() {
	return(typebtn(Resource.local().load("hud/rosters/btn-horse", 2),
		       Resource.local().load("hud/rosters/btn-horse-d", 2)));
    }
}
