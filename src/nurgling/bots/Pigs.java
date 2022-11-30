package nurgling.bots;


import haven.Gob;
import haven.res.gfx.hud.rosters.cow.Ochs;
import haven.res.gfx.hud.rosters.pig.Pig;
import haven.res.ui.croster.CattleId;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.bots.actions.AnimalAction;
import nurgling.bots.actions.DryerAction;
import nurgling.tools.AreasID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Predicate;


public class Pigs extends Bot {


    public Pigs(NGameUI gameUI ) {
        super(gameUI);
        win_title = "pigs";
        win_sz.y = 100;

        Comparator<Gob> comparator = new Comparator<Gob>() {
            @Override
            public int compare(Gob o1, Gob o2) {
                if (o1.getattr(CattleId.class) != null && o2.getattr(CattleId.class) != null) {
                    Pig p1 = (Pig) ((CattleId) o1.getattr(CattleId.class)).entry();
                    Pig p2 = (Pig) ((CattleId) o2.getattr(CattleId.class)).entry();
                    return -Double.compare(p1.rang(), p2.rang());
                }
                return 0;
            }
        };

        Predicate<Gob> wpred = new Predicate<Gob>() {
            @Override
            public boolean test(Gob gob) {
                Pig p1 = (Pig) ((CattleId) gob.getattr(CattleId.class)).entry();
                return !p1.hog && !p1.piglet && !p1.dead;
            }
        };
        Predicate<Gob> mpred = new Predicate<Gob>() {
            @Override
            public boolean test(Gob gob) {
                Pig p1 = (Pig) ((CattleId) gob.getattr(CattleId.class)).entry();
                return p1.hog && !p1.piglet && !p1.dead;
            }
        };
        Predicate<Gob> wlpred = new Predicate<Gob>() {
            @Override
            public boolean test(Gob gob) {
                Pig p1 = (Pig) ((CattleId) gob.getattr(CattleId.class)).entry();
                return !p1.hog && !p1.dead && p1.lactate && !p1.piglet;
            }
        };
        runActions.add(new AnimalAction<Pig>(new NAlias("pig"), AreasID.pigs, comparator, Pig.class, wpred, 4,wlpred));
        runActions.add(new AnimalAction<Pig>(new NAlias("pig"), AreasID.pigs, comparator, Pig.class, mpred, 1));

    }
    
    
    @Override
    public void initAction () {
    }
    
    @Override
    public void endAction () {
        super.endAction ();
    }
}
