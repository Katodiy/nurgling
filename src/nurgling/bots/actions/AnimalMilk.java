package nurgling.bots.actions;

import haven.Gob;
import haven.res.ui.croster.CattleId;
import haven.res.ui.croster.Entry;
import haven.res.ui.croster.RosterWindow;
import nurgling.*;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

import java.util.ArrayList;
import java.util.function.Predicate;

public class AnimalMilk<C extends Entry> implements Action {

    @Override
    public Results run(NGameUI gui)
            throws InterruptedException {
        NUtils.command(new char[]{'a', 'c'});
        NUtils.waitEvent(() -> NUtils.getGameUI().getWindow("Cattle Roster") != null, 500);
        RosterWindow w = (RosterWindow) NUtils.getGameUI().getWindow("Cattle Roster");
        NUtils.waitEvent(w::allLoaded, 1000);
        ArrayList<Gob> gobs = Finder.findObjectsInArea(animal,Finder.findNearestMark(current));
        ArrayList<Gob> targets = new ArrayList<>();

        while(NUtils.memorize(gobs,gui,w,cattleRoster));

        for (Gob gob : gobs) {
            if (gob.getattr(CattleId.class) != null) {
                if (pred.test(gob)) {
                    targets.add(gob);
                }
            }
        }
        NConfiguration.NInteger count = new NConfiguration.NInteger(0);
        Gob barrel = Finder.findObjectInArea(new NAlias("barrel"),10000,Finder.findSubArea(current,sub));
        Gob cistern = Finder.findObjectInArea(new NAlias("cistern"),10000,Finder.findSubArea(current,sub));
        new LiftObject(barrel).run(gui);
        while(!targets.isEmpty()) {
            milk(targets, gui, cistern, barrel, count);
        }
        if(!barrel.isTag(NGob.Tags.free)) {
            new PathFinder(gui, cistern, PathFinder.Type.dyn).run();
            NUtils.activate(cistern);
            NUtils.waitEvent(() -> barrel.isTag(NGob.Tags.free), 50);
        }
        new PlaceLifted(Finder.findSubArea(current,sub),barrel.getHitBox(), new NAlias(barrel.getResName())).run(gui);

        return new Results(Results.Types.SUCCESS);
    }

    void milk(ArrayList<Gob> targets, NGameUI gui, Gob cistern, Gob barrel, NConfiguration.NInteger count) throws InterruptedException {
        targets.sort(NUtils.d_comp);
        Gob gob = targets.get(0);
        new PathFinder(gui, gob, PathFinder.Type.dyn).run();
        NUtils.activate(gob);
        Thread.sleep(1000);
        count.set(count.get()+1);
        if(count.get()==4){
            new PathFinder(gui, cistern, PathFinder.Type.dyn).run();
            NUtils.activate(cistern);
            NUtils.waitEvent(()->barrel.isTag(NGob.Tags.free),50);
        }
        targets.remove(gob);
    }

    public <C extends Entry> AnimalMilk(NAlias animal, AreasID current, AreasID sub, Class<C> c, Predicate<Gob> pred) {
        this.animal = animal;
        this.current = current;
        this.sub = sub;
        cattleRoster = c;
        this.pred = pred;
    }


    NAlias animal;
    AreasID current;
    AreasID sub;

    Class<? extends Entry> cattleRoster;
    Predicate<Gob> pred = null;
}
