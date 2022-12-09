package nurgling.bots.actions;

import haven.Gob;
import haven.WItem;
import haven.res.gfx.terobjs.roastspit.Roastspit;
import haven.res.ui.croster.CattleId;
import haven.res.ui.croster.Entry;
import haven.res.ui.croster.RosterWindow;
import nurgling.*;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Predicate;

public class BranderAction implements Action {

    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        // Equip the Kindled Brand
        if (gui.getInventory().getItem(new NAlias("brandingiron-r"))==null) {
            // otherwise we set fire
            // We transfer the brand to inventory for ignition
            Gob kiln = Finder.findObject(new NAlias("kiln"));
            if (kiln != null) {
                new FromBeltToInventory(new NAlias("branding")).run(gui);
                WItem branding = gui.getInventory().getItem(new NAlias("branding"));
                if (branding == null)
                    return new Results(Results.Types.NO_ITEMS);
                new FillContainers(new NAlias("branding"), new NAlias("kiln"), new ArrayList<>()).run(gui);
                new FillFuelFromPiles(2, new NAlias("branch"), new NAlias("kiln"),
                        new NAlias("branch"), AreasID.branch).run(gui);
                new LightGob(new NAlias("kiln"), 1).run(gui);

                new WaitAction(() -> {
                    for (Gob gob : Finder.findObjects(new NAlias("kiln"))) {
                        if ((gob.getModelAttribute() & 1) != 0) {
                            return true;
                        }
                    }
                    return false;
                }, 1000).run(gui);
                new PathFinder(gui, kiln).run();
                new OpenTargetContainer(kiln, "Kiln").run(gui);
                new TakeMaxFromContainer("Kiln", new NAlias("brandingiron-r")).run(gui);
            } else {
                return new Results(Results.Types.NO_WORKSTATION);
            }

        }
        RosterWindow w = NUtils.getRosterWindow(area.cattleRoster);
        ArrayList<Gob> gobs = Finder.findObjects(area.animal,area.id);
        ArrayList<Gob> targets = new ArrayList<>();

        for (Gob gob : gobs) {
            if (gob.getattr(CattleId.class) == null && !gob.isTag(NGob.Tags.knocked)) {
                new PathFinder(gui, gob, PathFinder.Type.dyn).run();
                new SelectFlowerAction(gob, "Memorize", SelectFlowerAction.Types.Gob).run(gui);
                NUtils.waitEvent(() -> gob.getattr(CattleId.class) != null, 5000);
                NUtils.waitEvent(() -> w.roster(area.cattleRoster).entries.get(gob.getattr(CattleId.class)) != null, 5000);
            }
            if (gob.getattr(CattleId.class) != null) {
                if (area.pred.test(gob)) {
                    targets.add(gob);
                }
            }
        }
        if (new TakeToHand(new NAlias("brandingiron-r")).run(gui).type != Results.Types.SUCCESS) {
            return new Results(Results.Types.FAIL);
        }
        for(Gob gob : targets){
            new PathFinder(gui, gob, PathFinder.Type.dyn).run();
            NUtils.activateItem(gob);
            NUtils.waitEvent(()->NUtils.isPose(gui.map.player(),new NAlias("brand")),500);
            NUtils.waitEvent(()->NUtils.isPose(gui.map.player(),new NAlias("idle") ),5000);
        }
        NUtils.transferToInventory();



        return new Results(Results.Types.SUCCESS);
    }
    AnimalArea area  = null;

    public static class AnimalArea {
        public AreasID id = null;
        public Predicate<Gob> pred = null;
        public Class<? extends Entry> cattleRoster;
        public NAlias animal = null;
    }

    public BranderAction(AnimalArea area) {
        this.area = area;
    }
}
