/* Preprocessed source code */
/* $use: ui/croster */

package haven.res.gfx.hud.rosters.sheep;

import haven.*;
import haven.res.gfx.hud.rosters.cow.Ochs;
import haven.res.ui.croster.*;
import java.util.*;

@FromResource(name = "gfx/hud/rosters/sheep", version = 61)
public class SheepRoster extends CattleRoster<Sheep> {
    public static List<Column> cols = initcols(
	new Column<Entry>("Name", Comparator.comparing((Entry e) -> e.name), 200),

	new Column<Sheep>(Resource.local().load("hud/rosters/sex", 2),      Comparator.comparing((Sheep e) -> e.ram).reversed(), 20).runon(),
	new Column<Sheep>(Resource.local().load("hud/rosters/growth", 2),   Comparator.comparing((Sheep e) -> e.lamb).reversed(), 20).runon(),
	new Column<Sheep>(Resource.local().load("hud/rosters/deadp", 3),    Comparator.comparing((Sheep e) -> e.dead).reversed(), 20).runon(),
	new Column<Sheep>(Resource.local().load("hud/rosters/pregnant", 2), Comparator.comparing((Sheep e) -> e.pregnant).reversed(), 20).runon(),
	new Column<Sheep>(Resource.local().load("hud/rosters/lactate", 1),  Comparator.comparing((Sheep e) -> e.lactate).reversed(), 20),
	new Column<Sheep>(Resource.local().load("hud/rosters/owned", 1),    Comparator.comparing((Sheep e) -> ((e.owned ? 1 : 0) | (e.mine ? 2 : 0))).reversed(), 20),

	new Column<Sheep>(Resource.local().load("hud/rosters/quality", 2), Comparator.comparing((Sheep e) -> e.q).reversed()),

	new Column<Sheep>(Resource.local().load("hud/rosters/meatquantity", 1), Comparator.comparing((Sheep e) -> e.meat).reversed()),
	new Column<Sheep>(Resource.local().load("hud/rosters/milkquantity", 1), Comparator.comparing((Sheep e) -> e.milk).reversed()),
	new Column<Sheep>(Resource.local().load("hud/rosters/woolquantity", 1), Comparator.comparing((Sheep e) -> e.wool).reversed()),

	new Column<Sheep>(Resource.local().load("hud/rosters/meatquality", 1), Comparator.comparing((Sheep e) -> e.meatq).reversed()),
	new Column<Sheep>(Resource.local().load("hud/rosters/milkquality", 1), Comparator.comparing((Sheep e) -> e.milkq).reversed()),
	new Column<Sheep>(Resource.local().load("hud/rosters/woolquality", 1), Comparator.comparing((Sheep e) -> e.woolq).reversed()),
	new Column<Sheep>(Resource.local().load("hud/rosters/hidequality", 1), Comparator.comparing((Sheep e) -> e.hideq).reversed()),

	new Column<Sheep>(Resource.local().load("hud/rosters/breedingquality", 1), Comparator.comparing((Sheep e) -> e.seedq).reversed()),
	new Column<Sheep>(Resource.local().load("hud/rosters/rang", 1), Comparator.comparing((Sheep e) -> e.rang).reversed())
    );
    protected List<Column> cols() {return(cols);}

    public static CattleRoster mkwidget(UI ui, Object... args) {
	return(new SheepRoster());
    }

    public Sheep parse(Object... args) {
	int n = 0;
	long id = (Long)args[n++];
	String name = (String)args[n++];
	Sheep ret = new Sheep(id, name);
	ret.grp = (Integer)args[n++];
	int fl = (Integer)args[n++];
	ret.ram = (fl & 1) != 0;
	ret.lamb = (fl & 2) != 0;
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
	return(typebtn(Resource.local().load("hud/rosters/btn-sheep", 2),
		       Resource.local().load("hud/rosters/btn-sheep-d", 2)));
    }
}
