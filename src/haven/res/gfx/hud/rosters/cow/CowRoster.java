/* Preprocessed source code */
/* $use: ui/croster */

package haven.res.gfx.hud.rosters.cow;

import haven.*;
import haven.res.ui.croster.*;
import java.util.*;

@FromResource(name = "gfx/hud/rosters/cow", version = 72)
public class CowRoster extends CattleRoster<Ochs> {
    public static List<Column> cols = initcols(
	new Column<Entry>("Name", Comparator.comparing((Entry e) -> e.name), 200),

	new Column<Ochs>(Resource.local().load("hud/rosters/sex", 2),      Comparator.comparing((Ochs e) -> e.bull).reversed(), 20).runon(),
	new Column<Ochs>(Resource.local().load("hud/rosters/growth", 2),   Comparator.comparing((Ochs e) -> e.calf).reversed(), 20).runon(),
	new Column<Ochs>(Resource.local().load("hud/rosters/deadp", 3),    Comparator.comparing((Ochs e) -> e.dead).reversed(), 20).runon(),
	new Column<Ochs>(Resource.local().load("hud/rosters/pregnant", 2), Comparator.comparing((Ochs e) -> e.pregnant).reversed(), 20).runon(),
	new Column<Ochs>(Resource.local().load("hud/rosters/lactate", 1),  Comparator.comparing((Ochs e) -> e.lactate).reversed(), 20).runon(),
	new Column<Ochs>(Resource.local().load("hud/rosters/owned", 1),    Comparator.comparing((Ochs e) -> ((e.owned ? 1 : 0) | (e.mine ? 2 : 0))).reversed(), 20),

	new Column<Ochs>(Resource.local().load("hud/rosters/quality", 2), Comparator.comparing((Ochs e) -> e.q).reversed()),

	new Column<Ochs>(Resource.local().load("hud/rosters/meatquantity", 1), Comparator.comparing((Ochs e) -> e.meat).reversed()),
	new Column<Ochs>(Resource.local().load("hud/rosters/milkquantity", 1), Comparator.comparing((Ochs e) -> e.milk).reversed()),

	new Column<Ochs>(Resource.local().load("hud/rosters/meatquality", 1), Comparator.comparing((Ochs e) -> e.meatq).reversed()),
	new Column<Ochs>(Resource.local().load("hud/rosters/milkquality", 1), Comparator.comparing((Ochs e) -> e.milkq).reversed()),
	new Column<Ochs>(Resource.local().load("hud/rosters/hidequality", 1), Comparator.comparing((Ochs e) -> e.hideq).reversed()),

	new Column<Ochs>(Resource.local().load("hud/rosters/breedingquality", 1), Comparator.comparing((Ochs e) -> e.seedq).reversed()),
	new Column<Ochs>(Resource.local().load("hud/rosters/rang", 1), Comparator.comparing((Ochs e) -> e.rang).reversed())
    );
    protected List<Column> cols() {return(cols);}

    public static CattleRoster mkwidget(UI ui, Object... args) {
	return(new CowRoster());
    }

    public Ochs parse(Object... args) {
	int n = 0;
	long id = (Long)args[n++];
	String name = (String)args[n++];
	Ochs ret = new Ochs(id, name);
	ret.grp = (Integer)args[n++];
	int fl = (Integer)args[n++];
	ret.bull = (fl & 1) != 0;
	ret.calf = (fl & 2) != 0;
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
	ret.rang = ret.rang();
	return(ret);
    }

    public TypeButton button() {
	return(typebtn(Resource.local().load("hud/rosters/btn-cow", 2),
		       Resource.local().load("hud/rosters/btn-cow-d", 2)));
    }
}
