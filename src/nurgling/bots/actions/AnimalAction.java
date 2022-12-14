package nurgling.bots.actions;

import haven.*;
import haven.res.gfx.hud.rosters.pig.Pig;
import haven.res.gfx.hud.rosters.pig.PigRoster;
import haven.res.ui.croster.CattleId;
import haven.res.ui.croster.CattleRoster;
import haven.res.ui.croster.Entry;
import haven.res.ui.croster.RosterWindow;
import haven.res.ui.tt.leashed.Leashed;
import nurgling.*;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Predicate;

public class AnimalAction <C extends Entry> implements Action {

    @Override
    public Results run(NGameUI gui)
            throws InterruptedException {
        RosterWindow w = NUtils.getRosterWindow(cattleRoster);
        ArrayList<Gob> gobs = Finder.findObjects(animal,current);
        ArrayList<Gob> targets = new ArrayList<>();

        for (Gob gob : gobs) {
            if (gob.getattr(CattleId.class) == null && !gob.isTag(NGob.Tags.knocked)) {
                new PathFinder(gui, gob, PathFinder.Type.dyn).run();
                new SelectFlowerAction(gob, "Memorize", SelectFlowerAction.Types.Gob).run(gui);
                NUtils.waitEvent(() -> gob.getattr(CattleId.class) != null, 5000);
                NUtils.waitEvent(() -> w.roster(cattleRoster).entries.get(gob.getattr(CattleId.class).id) != null, 5000);
                NUtils.waitEvent(() -> w.roster(cattleRoster).entries.get(gob.getattr(CattleId.class).id).getClass() == cattleRoster, 5000);
            }
            if (gob.getattr(CattleId.class) != null) {
                if (pred.test(gob)) {
                    targets.add(gob);
                }
            }
        }
        if (targets.size() > count) {
            targets.sort(comp);
            ArrayList<Gob> forlife = new ArrayList<>();
            ArrayList<Gob> forkill = new ArrayList<>();
            for (int i = 0; i < targets.size(); i++) {
                if (i < count) {
                    forlife.add(targets.get(i));
                } else {
                    forkill.add(targets.get(i));
                }
            }
            if (forpred != null) {
                int c = 0;
                for (Gob gob : forlife) {
                    if (forpred.test(gob)) {
                        c++;
                        if (c >=flcount) {
                            break;
                        }
                    }
                }
                if (c<flcount) {
                    ArrayList<Gob> forsave = new ArrayList<>();
                    for (Gob gob : forkill) {
                        if (forpred.test(gob)) {
                            c++;
                            forsave.add(gob);
                            if(c >=flcount)
                                break;
                        }
                    }
                    forkill.removeAll(forsave);
                }
            }
            for (Gob target : forkill) {
                new PathFinder(gui, target, PathFinder.Type.dyn).run();
                new SelectFlowerAction(target, "Slaughter", SelectFlowerAction.Types.Gob).run(gui);
                NUtils.waitEvent(() -> target.isTag(NGob.Tags.knocked), 20000);
                new LiftObject(target).run(gui);
                new PlaceLifted(AreasID.kritter, target.getHitBox(), target).run(gui);
                Collection<Object> args = new ArrayList<>();
                args.add(Integer.valueOf((int) (((CattleId) target.getattr(CattleId.class)).entry().id & 0x00000000ffffffffl)));
                args.add(Integer.valueOf((int) ((((CattleId) target.getattr(CattleId.class)).entry().id & 0xffffffff00000000l) >> 32)));
                w.roster(cattleRoster).wdgmsg("rm", args.toArray(new Object[0]));
            }
        }
        return new Results(Results.Types.SUCCESS);
    }

    public <C extends Entry> AnimalAction(NAlias animal, AreasID current, Comparator<Gob> wc, Class<C> c, Predicate<Gob> pred, int count) {
        this.animal = animal;
        this.current = current;
        sub = "sodalite";
        cattleRoster = c;
        comp = wc;
        this.pred = pred;
        this.count = count;
    }
    public <C extends Entry> AnimalAction(NAlias animal, AreasID current, Comparator<Gob> wc, Class<C> c, Predicate<Gob> pred, int count, Predicate<Gob> forpred, int flcount) {
        this.animal = animal;
        this.current = current;
        sub = "sodalite";
        cattleRoster = c;
        comp = wc;
        this.pred = pred;
        this.count = count;
        this.forpred = forpred;
        this.flcount = flcount;
    }


    NAlias animal;
    AreasID current;
    String sub;

    Class<? extends Entry> cattleRoster;
    Comparator<Gob> comp = null;
    Predicate<Gob> pred = null;
    Predicate<Gob> forpred = null;
    int count = 0;
    int flcount = 0;
}
