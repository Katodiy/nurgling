package nurgling.bots.actions;


import haven.Gob;
import haven.res.gfx.hud.rosters.goat.Goat;
import haven.res.ui.croster.CattleId;
import nurgling.NAlias;
import nurgling.NConfiguration;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.tools.AreasID;

import java.util.Comparator;
import java.util.function.Predicate;

public class Goats implements Action {
    @Override
    public Results run(NGameUI gui) throws InterruptedException {
        Comparator<Gob> comparator = new Comparator<Gob>() {
            @Override
            public int compare(Gob o1, Gob o2) {
                if (o1.getattr(CattleId.class) != null && o2.getattr(CattleId.class) != null) {
                    Goat p1 = (Goat) (NUtils.getAnimalEntity(o1,Goat.class));;
                    Goat p2 = (Goat) (NUtils.getAnimalEntity(o2,Goat.class));;
                    return -Double.compare(p1.rang(), p2.rang());
                }
                return 0;
            }
        };

        Predicate<Gob> wpred = new Predicate<Gob>() {
            @Override
            public boolean test(Gob gob) {
                if(NConfiguration.getInstance().goatsHerd.get(NConfiguration.getInstance().selected_goatsHerd).disable_killing)
                    return false;
                Goat p1 = (Goat) (NUtils.getAnimalEntity(gob,Goat.class));;
                return !p1.billy && !p1.dead && (!p1.kid || !NConfiguration.getInstance().goatsHerd.get(NConfiguration.getInstance().selected_goatsHerd).ignoreChildren);
            }
        };
        Predicate<Gob> mpred = new Predicate<Gob>() {
            @Override
            public boolean test(Gob gob) {
                if(NConfiguration.getInstance().goatsHerd.get(NConfiguration.getInstance().selected_goatsHerd).disable_killing)
                    return false;
                Goat p1 = (Goat) (NUtils.getAnimalEntity(gob,Goat.class));;
                return p1.billy && !p1.dead && (!p1.kid || !NConfiguration.getInstance().goatsHerd.get(NConfiguration.getInstance().selected_goatsHerd).ignoreChildren);
            }
        };

        Predicate<Gob> mlpred = new Predicate<Gob>() {
            @Override
            public boolean test(Gob gob) {
                Goat p1 = (Goat) (NUtils.getAnimalEntity(gob,Goat.class));;
                return p1.billy && !p1.dead && !p1.kid;
            }
        };

        Predicate<Gob> wlpred = new Predicate<Gob>() {
            @Override
            public boolean test(Gob gob) {
                Goat p1 = (Goat) (NUtils.getAnimalEntity(gob,Goat.class));;
                return !p1.billy && !p1.dead && p1.lactate;
            }
        };
        new AnimalWool<Goat>(new NAlias("goat"), AreasID.goats,AreasID.wool,Goat.class,wlpred).run(gui);
        new AnimalMilk<Goat>(new NAlias("goat"), AreasID.goats,AreasID.milk,Goat.class,wlpred).run(gui);
        new AnimalAction<Goat>(new NAlias("goat"), AreasID.goats, comparator, Goat.class, wpred, wlpred,NConfiguration.getInstance().goatsHerd.get(NConfiguration.getInstance().selected_goatsHerd).adultGoats.get()).run(gui);
        new AnimalAction<Goat>(new NAlias("goat"), AreasID.goats, comparator, Goat.class, mpred, mlpred,1).run(gui);
        return new Results(Results.Types.SUCCESS);
    }
}
