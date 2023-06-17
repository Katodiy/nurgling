package nurgling.bots.actions;

import haven.Gob;
import haven.res.gfx.hud.rosters.cow.Ochs;
import haven.res.ui.croster.CattleId;
import nurgling.NAlias;
import nurgling.NConfiguration;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.tools.AreasID;

import java.util.Comparator;
import java.util.function.Predicate;

public class Cows implements Action {
    @Override
    public Results run(NGameUI gui) throws InterruptedException {
        Comparator<Gob> comparator = new Comparator<Gob>() {
            @Override
            public int compare(Gob o1, Gob o2) {
                if (o1.getattr(CattleId.class) != null && o2.getattr(CattleId.class) != null) {
                    Ochs p1 = (Ochs) (NUtils.getAnimalEntity(o1,Ochs.class));;
                    Ochs p2 = (Ochs) (NUtils.getAnimalEntity(o2,Ochs.class));;
                    return Double.compare(p2.rang(), p1.rang());
                }
                return 0;
            }
        };

        Predicate<Gob> wpred = new Predicate<Gob>() {
            @Override
            public boolean test(Gob gob) {
                if(NConfiguration.getInstance().cowsHerd.get(NConfiguration.getInstance().selected_cowsHerd).disable_killing)
                    return false;
                Ochs p1 = (Ochs) (NUtils.getAnimalEntity(gob,Ochs.class));;
                return !p1.bull && !p1.dead && (!p1.calf || !NConfiguration.getInstance().cowsHerd.get(NConfiguration.getInstance().selected_cowsHerd).ignoreChildren);
            }
        };
        Predicate<Gob> mpred = new Predicate<Gob>() {
            @Override
            public boolean test(Gob gob) {
                if(NConfiguration.getInstance().cowsHerd.get(NConfiguration.getInstance().selected_cowsHerd).disable_killing)
                    return false;
                Ochs p1 = (Ochs) (NUtils.getAnimalEntity(gob,Ochs.class));;
                return p1.bull && !p1.dead && (!p1.calf || !NConfiguration.getInstance().cowsHerd.get(NConfiguration.getInstance().selected_cowsHerd).ignoreChildren);
            }
        };

        Predicate<Gob> mlpred = new Predicate<Gob>() {
            @Override
            public boolean test(Gob gob) {
                Ochs p1 = (Ochs) (NUtils.getAnimalEntity(gob,Ochs.class));;
                return p1.bull && !p1.dead && !p1.calf;
            }
        };

        Predicate<Gob> wlpred = new Predicate<Gob>() {
            @Override
            public boolean test(Gob gob) {
                Ochs p1 = (Ochs) (NUtils.getAnimalEntity(gob,Ochs.class));;
                return !p1.bull && !p1.dead && p1.lactate;
            }
        };
        new AnimalMilk<Ochs>(new NAlias("cattle"), AreasID.cows,AreasID.milk,Ochs.class,wlpred).run(gui);
        new AnimalAction<Ochs>(new NAlias("cattle"), AreasID.cows, comparator, Ochs.class, wpred, wlpred,NConfiguration.getInstance().cowsHerd.get(NConfiguration.getInstance().selected_cowsHerd).adultCows.get()).run(gui);
        new AnimalAction<Ochs>(new NAlias("cattle"), AreasID.cows, comparator, Ochs.class, mpred, mlpred,1).run(gui);
        return new Results(Results.Types.SUCCESS);
    }
}