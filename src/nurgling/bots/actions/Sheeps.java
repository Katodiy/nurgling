package nurgling.bots.actions;

import haven.Gob;
import haven.res.gfx.hud.rosters.sheep.Sheep;
import haven.res.ui.croster.CattleId;
import nurgling.NAlias;
import nurgling.NConfiguration;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.tools.AreasID;

import java.util.Comparator;
import java.util.function.Predicate;

public class Sheeps implements Action {
    @Override
    public Results run(NGameUI gui) throws InterruptedException {
        Comparator<Gob> comparator = new Comparator<Gob>() {
            @Override
            public int compare(Gob o1, Gob o2) {
                if (o1.getattr(CattleId.class) != null && o2.getattr(CattleId.class) != null) {
                    Sheep p1 = (Sheep) (NUtils.getAnimalEntity(o1,Sheep.class));;
                    Sheep p2 = (Sheep) (NUtils.getAnimalEntity(o2,Sheep.class));;
                    return -Double.compare(p1.rang(), p2.rang());
                }
                return 0;
            }
        };

        Predicate<Gob> wpred = new Predicate<Gob>() {
            @Override
            public boolean test(Gob gob) {
                if(NConfiguration.getInstance().sheepsHerd.get(NConfiguration.getInstance().selected_sheepsHerd).disable_killing)
                    return false;
                Sheep p1 = (Sheep) (NUtils.getAnimalEntity(gob,Sheep.class));;
                return !p1.ram && !p1.dead && (!p1.lamb || !NConfiguration.getInstance().sheepsHerd.get(NConfiguration.getInstance().selected_sheepsHerd).ignoreChildren);
            }
        };
        Predicate<Gob> mpred = new Predicate<Gob>() {
            @Override
            public boolean test(Gob gob) {
                if(NConfiguration.getInstance().sheepsHerd.get(NConfiguration.getInstance().selected_sheepsHerd).disable_killing)
                    return false;
                Sheep p1 = (Sheep) (NUtils.getAnimalEntity(gob,Sheep.class));;
                return p1.ram && !p1.dead && (!p1.lamb || !NConfiguration.getInstance().sheepsHerd.get(NConfiguration.getInstance().selected_sheepsHerd).ignoreChildren);
            }
        };

        Predicate<Gob> mlpred = new Predicate<Gob>() {
            @Override
            public boolean test(Gob gob) {
                Sheep p1 = (Sheep) (NUtils.getAnimalEntity(gob,Sheep.class));;
                return p1.ram && !p1.dead && !p1.lamb;
            }
        };

        Predicate<Gob> wlpred = new Predicate<Gob>() {
            @Override
            public boolean test(Gob gob) {
                Sheep p1 = (Sheep) (NUtils.getAnimalEntity(gob,Sheep.class));;
                return !p1.ram && !p1.dead && p1.lactate;
            }
        };
        new AnimalWool<Sheep>(new NAlias("sheep"), AreasID.sheeps,AreasID.wool,Sheep.class,wlpred).run(gui);
        new AnimalMilk<Sheep>(new NAlias("sheep"), AreasID.sheeps,AreasID.milk,Sheep.class,wlpred).run(gui);
        new AnimalAction<Sheep>(new NAlias("sheep"), AreasID.sheeps, comparator, Sheep.class, wpred, wlpred,NConfiguration.getInstance().sheepsHerd.get(NConfiguration.getInstance().selected_sheepsHerd).adultSheeps.get()).run(gui);
        new AnimalAction<Sheep>(new NAlias("sheep"), AreasID.sheeps, comparator, Sheep.class, mpred,mlpred,1).run(gui);
        return new Results(Results.Types.SUCCESS);
    }
}
