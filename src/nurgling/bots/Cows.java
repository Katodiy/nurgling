package nurgling.bots;


import haven.Gob;
import haven.res.gfx.hud.rosters.cow.Ochs;
import haven.res.gfx.hud.rosters.pig.Pig;
import haven.res.ui.croster.CattleId;
import nurgling.NAlias;
import nurgling.NConfiguration;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.bots.actions.AnimalAction;
import nurgling.bots.actions.AnimalMilk;
import nurgling.tools.AreasID;

import java.util.Comparator;
import java.util.function.Predicate;


public class Cows extends Bot {


    public Cows(NGameUI gameUI ) {
        super(gameUI);
        win_title = "cows";
        win_sz.y = 100;

        Comparator<Gob> comparator = new Comparator<Gob>() {
            @Override
            public int compare(Gob o1, Gob o2) {
                if (o1.getattr(CattleId.class) != null && o2.getattr(CattleId.class) != null) {
                    Ochs p1 = (Ochs) (NUtils.getAnimalEntity(o1,Ochs.class));;
                    Ochs p2 = (Ochs) (NUtils.getAnimalEntity(o2,Ochs.class));;
                    return -Double.compare(p1.rang(), p2.rang());
                }
                return 0;
            }
        };

        Predicate<Gob> wpred = new Predicate<Gob>() {
            @Override
            public boolean test(Gob gob) {
                Ochs p1 = (Ochs) (NUtils.getAnimalEntity(gob,Ochs.class));;
                return !p1.bull && !p1.dead;
            }
        };
        Predicate<Gob> mpred = new Predicate<Gob>() {
            @Override
            public boolean test(Gob gob) {
                Ochs p1 = (Ochs) (NUtils.getAnimalEntity(gob,Ochs.class));;
                return p1.bull && !p1.dead;
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
                return !p1.bull && !p1.dead && !p1.calf;
            }
        };
        runActions.add(new AnimalMilk<Ochs>(new NAlias("cattle"), AreasID.cows,AreasID.milk,Ochs.class,wlpred));
        runActions.add(new AnimalAction<Ochs>(new NAlias("cattle"), AreasID.cows, comparator, Ochs.class, wpred, NConfiguration.getInstance().cowsHerd.totalCows, wlpred,NConfiguration.getInstance().cowsHerd.adultCows));
        runActions.add(new AnimalAction<Ochs>(new NAlias("cattle"), AreasID.cows, comparator, Ochs.class, mpred, 1, mlpred,1));
    }
    
    
    @Override
    public void initAction () {
    }
    
    @Override
    public void endAction () {
        super.endAction ();
    }
}
