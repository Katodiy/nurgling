package nurgling.bots.actions;

import haven.Gob;
import haven.res.gfx.hud.rosters.pig.Pig;
import haven.res.ui.croster.CattleId;
import nurgling.NAlias;
import nurgling.NConfiguration;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.tools.AreasID;

import java.util.Comparator;
import java.util.function.Predicate;

public class Pigs implements Action{
    @Override
    public Results run(NGameUI gui) throws InterruptedException {
        Comparator<Gob> comparator = new Comparator<Gob>() {
            @Override
            public int compare(Gob o1, Gob o2) {
                if (o1.getattr(CattleId.class) != null && o2.getattr(CattleId.class) != null) {
                    Pig p1 = (Pig) (NUtils.getAnimalEntity(o1,Pig.class));
                    Pig p2 = (Pig) (NUtils.getAnimalEntity(o2,Pig.class));
                    return -Double.compare(p1.rang(), p2.rang());
                }
                return 0;
            }
        };

        Predicate<Gob> wpred = new Predicate<Gob>() {
            @Override
            public boolean test(Gob gob) {
                if(NConfiguration.getInstance().pigsHerd.get(NConfiguration.getInstance().selected_pigsHerd).disable_killing)
                    return false;
                Pig p1 = (Pig) (NUtils.getAnimalEntity(gob,Pig.class));;
                return !p1.hog && !p1.dead && (!p1.piglet || !NConfiguration.getInstance().pigsHerd.get(NConfiguration.getInstance().selected_pigsHerd).ignoreChildren);
            }
        };
        Predicate<Gob> mpred = new Predicate<Gob>() {
            @Override
            public boolean test(Gob gob) {
                if(NConfiguration.getInstance().pigsHerd.get(NConfiguration.getInstance().selected_pigsHerd).disable_killing)
                    return false;
                Pig p1 = (Pig) (NUtils.getAnimalEntity(gob,Pig.class));;
                return p1.hog && !p1.dead && (!p1.piglet || !NConfiguration.getInstance().pigsHerd.get(NConfiguration.getInstance().selected_pigsHerd).ignoreChildren);
            }
        };

        Predicate<Gob> mlpred = new Predicate<Gob>() {
            @Override
            public boolean test(Gob gob) {
                Pig p1 = (Pig) (NUtils.getAnimalEntity(gob,Pig.class));;
                return p1.hog && !p1.dead && !p1.piglet;
            }
        };

        Predicate<Gob> wlpred = new Predicate<Gob>() {
            @Override
            public boolean test(Gob gob) {
                Pig p1 = (Pig) (NUtils.getAnimalEntity(gob,Pig.class));;
                return !p1.hog && !p1.dead && p1.lactate;
            }
        };
        new AnimalAction<Pig>(new NAlias("pig"), AreasID.pigs, comparator, Pig.class, wpred, wlpred, NConfiguration.getInstance().pigsHerd.get(NConfiguration.getInstance().selected_pigsHerd).adultPigs.get()).run(gui);
        new AnimalAction<Pig>(new NAlias("pig"), AreasID.pigs, comparator, Pig.class, mpred, mlpred,1).run(gui);

        return new Results(Results.Types.SUCCESS);
    }
}
