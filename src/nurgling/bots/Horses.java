package nurgling.bots;


import haven.Gob;
import haven.res.gfx.hud.rosters.cow.Ochs;
import haven.res.gfx.hud.rosters.horse.Horse;
import haven.res.ui.croster.CattleId;
import nurgling.NAlias;
import nurgling.NConfiguration;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.bots.actions.AnimalAction;
import nurgling.tools.AreasID;

import java.util.Comparator;
import java.util.function.Predicate;


public class Horses extends Bot {


    public Horses(NGameUI gameUI ) {
        super(gameUI);
        win_title = "horses";
        win_sz.y = 100;

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
                Horse p1 = (Horse) (NUtils.getAnimalEntity(gob, Horse.class));
                return !p1.stallion && !p1.dead;
            }
        };
        Predicate<Gob> mpred = new Predicate<Gob>() {
            @Override
            public boolean test(Gob gob) {
                Horse p1 = (Horse) (NUtils.getAnimalEntity(gob, Horse.class));
                return p1.stallion  && !p1.dead;
            }
        };
        Predicate<Gob> wlpred = new Predicate<Gob>() {
            @Override
            public boolean test(Gob gob) {
                Horse p1 = (Horse) (NUtils.getAnimalEntity(gob, Horse.class));
                return !p1.stallion && !p1.dead && p1.lactate && !p1.foal;
            }
        };

        Predicate<Gob> mlpred = new Predicate<Gob>() {
            @Override
            public boolean test(Gob gob) {
                Horse p1 = (Horse) (NUtils.getAnimalEntity(gob,Horse.class));;
                return p1.stallion && !p1.dead && !p1.foal;
            }
        };
        runActions.add(new AnimalAction<Horse>(new NAlias("horse"), AreasID.horses, comparator, Horse.class, wpred, NConfiguration.getInstance().horsesHerd.totalMares, wlpred, 1));
        runActions.add(new AnimalAction<Horse>(new NAlias("horse"), AreasID.horses, comparator, Horse.class, mpred, 1, mlpred, 1));

    }
    
    
    @Override
    public void initAction () {
    }
    
    @Override
    public void endAction () {
        super.endAction ();
    }
}
