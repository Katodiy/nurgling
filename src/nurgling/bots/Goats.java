package nurgling.bots;


import haven.Gob;
import haven.res.gfx.hud.rosters.goat.Goat;
import haven.res.ui.croster.CattleId;
import nurgling.NAlias;
import nurgling.NConfiguration;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.bots.actions.AnimalAction;
import nurgling.bots.actions.AnimalMilk;
import nurgling.bots.actions.AnimalWool;
import nurgling.tools.AreasID;

import java.util.Comparator;
import java.util.function.Predicate;


public class Goats extends Bot {


    public Goats(NGameUI gameUI ) {
        super(gameUI);
        win_title = "goats";
        win_sz.y = 100;

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
                Goat p1 = (Goat) (NUtils.getAnimalEntity(gob,Goat.class));;
                return !p1.billy && !p1.dead;
            }
        };
        Predicate<Gob> mpred = new Predicate<Gob>() {
            @Override
            public boolean test(Gob gob) {
                Goat p1 = (Goat) (NUtils.getAnimalEntity(gob,Goat.class));;
                return p1.billy && !p1.dead;
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
        runActions.add(new AnimalWool<Goat>(new NAlias("goat"), AreasID.goats,AreasID.wool,Goat.class,wlpred));
        runActions.add(new AnimalMilk<Goat>(new NAlias("goat"), AreasID.goats,AreasID.milk,Goat.class,wlpred));
        runActions.add(new AnimalAction<Goat>(new NAlias("goat"), AreasID.goats, comparator, Goat.class, wpred, NConfiguration.getInstance().goatsHerd.totalGoats, wlpred,NConfiguration.getInstance().goatsHerd.adultGoats));
        runActions.add(new AnimalAction<Goat>(new NAlias("goat"), AreasID.goats, comparator, Goat.class, mpred, 1, mlpred,1));
    }
    
    
    @Override
    public void initAction () {
    }
    
    @Override
    public void endAction () {
        super.endAction ();
    }
}
