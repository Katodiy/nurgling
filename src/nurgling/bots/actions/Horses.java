package nurgling.bots.actions;

import haven.Gob;
import haven.res.gfx.hud.rosters.horse.Horse;
import haven.res.ui.croster.CattleId;
import nurgling.NAlias;
import nurgling.NConfiguration;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.tools.AreasID;

import java.util.Comparator;
import java.util.function.Predicate;

public class Horses implements Action{
    @Override
    public Results run(NGameUI gui) throws InterruptedException {
        Comparator<Gob> comparator = new Comparator<Gob>() {
            @Override
            public int compare(Gob o1, Gob o2) {
                if (o1.getattr(CattleId.class) != null && o2.getattr(CattleId.class) != null) {
                    Horse p1 = (Horse) (NUtils.getAnimalEntity(o1, Horse.class));
                    Horse p2 = (Horse) (NUtils.getAnimalEntity(o2, Horse.class));
                    return -Double.compare(p1.rang(), p2.rang());
                }
                return 0;
            }
        };

        Predicate<Gob> wpred = new Predicate<Gob>() {
            @Override
            public boolean test(Gob gob) {
                if(NConfiguration.getInstance().horsesHerd.get(NConfiguration.getInstance().selected_horsesHerd).disable_killing)
                    return false;
                Horse p1 = (Horse) (NUtils.getAnimalEntity(gob, Horse.class));
                return !p1.stallion && !p1.dead && (!p1.foal || !NConfiguration.getInstance().horsesHerd.get(NConfiguration.getInstance().selected_horsesHerd).ignoreChildren);
            }
        };
        Predicate<Gob> mpred = new Predicate<Gob>() {
            @Override
            public boolean test(Gob gob) {
                if(NConfiguration.getInstance().horsesHerd.get(NConfiguration.getInstance().selected_horsesHerd).disable_killing)
                    return false;
                Horse p1 = (Horse) (NUtils.getAnimalEntity(gob, Horse.class));
                return p1.stallion && !p1.dead && (!p1.foal || !NConfiguration.getInstance().horsesHerd.get(NConfiguration.getInstance().selected_horsesHerd).ignoreChildren);
            }
        };
        Predicate<Gob> wlpred = new Predicate<Gob>() {
            @Override
            public boolean test(Gob gob) {
                Horse p1 = (Horse) (NUtils.getAnimalEntity(gob, Horse.class));
                return !p1.stallion && !p1.dead && p1.lactate;
            }
        };

        Predicate<Gob> mlpred = new Predicate<Gob>() {
            @Override
            public boolean test(Gob gob) {
                Horse p1 = (Horse) (NUtils.getAnimalEntity(gob,Horse.class));;
                return p1.stallion && !p1.dead && !p1.foal;
            }
        };
        new AnimalAction<Horse>(new NAlias("horse"), AreasID.horses, comparator, Horse.class, wpred, wlpred, NConfiguration.getInstance().horsesHerd.get(NConfiguration.getInstance().selected_horsesHerd).adultHorse.get()).run(gui);
        new AnimalAction<Horse>(new NAlias("horse"), AreasID.horses, comparator, Horse.class, mpred, mlpred, 1).run(gui);

        return new Results(Results.Types.SUCCESS);
    }
}
