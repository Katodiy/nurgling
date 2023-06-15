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

public class AnimalWool<C extends Entry> implements Action {

    @Override
    public Results run(NGameUI gui)
            throws InterruptedException {
        new Equip(new NAlias("shears")).run(gui);
        NUtils.command(new char[]{'a', 'c'});
        NUtils.waitEvent(() -> NUtils.getGameUI().getWindow("Cattle Roster") != null, 500);
        RosterWindow w = (RosterWindow) NUtils.getGameUI().getWindow("Cattle Roster");
        NUtils.waitEvent(w::allLoaded, 1000);
        ArrayList<Gob> gobs = Finder.findObjectsInArea(animal,Finder.findNearestMark(current));
        ArrayList<Gob> targets = new ArrayList<>();

        while(NUtils.memorize(gobs,gui,w,cattleRoster));

        for (Gob gob : gobs) {
            if (gob.getattr(CattleId.class) != null) {
                if (gob.isTag(NGob.Tags.wool)) {
                    targets.add(gob);
                }
            }
        }

        while (!targets.isEmpty())
            wool(targets,gui);
        new TransferToPile(Finder.findSubArea(current,sub),NHitBox.get(),new NAlias("wool"),new NAlias("wool")).run(gui);
        return new Results(Results.Types.SUCCESS);
    }

    void wool(ArrayList<Gob> targets, NGameUI gui) throws InterruptedException {
        targets.sort(NUtils.d_comp);
        Gob gob = targets.get(0);
        if (gui.getInventory().getFreeSpace() < 5)
            new TransferToPile(Finder.findSubArea(current, sub), NHitBox.get(), new NAlias("wool"), new NAlias("wool")).run(gui);
        new PathFinder(gui, gob, PathFinder.Type.dyn).run();
        new SelectFlowerAction(gob, "Shear wool", SelectFlowerAction.Types.Gob).run(gui);
        NUtils.waitEvent(() -> NUtils.isPose(gui.map.player(), new NAlias("carving")), 500);
        NUtils.waitEvent(() -> !NUtils.isPose(gui.map.player(), new NAlias("carving")), 10000);
        targets.remove(gob);
    }

    public <C extends Entry> AnimalWool(NAlias animal, AreasID current, AreasID sub, Class<C> c, Predicate<Gob> pred) {
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
