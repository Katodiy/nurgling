package nurgling.bots;


import haven.Gob;
import haven.res.gfx.hud.rosters.sheep.Sheep;
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


public class Sheeps extends Bot {


    public Sheeps(NGameUI gameUI ) {
        super(gameUI);
        win_title = "sheeps";
        win_sz.y = 100;

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
                Sheep p1 = (Sheep) (NUtils.getAnimalEntity(gob,Sheep.class));;
                return !p1.ram && !p1.dead;
            }
        };
        Predicate<Gob> mpred = new Predicate<Gob>() {
            @Override
            public boolean test(Gob gob) {
                Sheep p1 = (Sheep) (NUtils.getAnimalEntity(gob,Sheep.class));;
                return p1.ram && !p1.dead;
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
                return !p1.ram && !p1.dead && !p1.lamb;
            }
        };
        runActions.add(new AnimalWool<Sheep>(new NAlias("sheep"), AreasID.sheeps,AreasID.wool,Sheep.class,wlpred));
        runActions.add(new AnimalMilk<Sheep>(new NAlias("sheep"), AreasID.sheeps,AreasID.milk,Sheep.class,wlpred));
        runActions.add(new AnimalAction<Sheep>(new NAlias("sheep"), AreasID.sheeps, comparator, Sheep.class, wpred, NConfiguration.getInstance().sheepsHerd.totalSheeps, wlpred,NConfiguration.getInstance().sheepsHerd.adultSheeps));
        runActions.add(new AnimalAction<Sheep>(new NAlias("sheep"), AreasID.sheeps, comparator, Sheep.class, mpred, 1, mlpred,1));
    }
    
    
    @Override
    public void initAction () {
    }
    
    @Override
    public void endAction () {
        super.endAction ();
    }
}
