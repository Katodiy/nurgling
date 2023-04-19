package nurgling.bots.actions;

import haven.GItem;
import haven.Gob;
import nurgling.*;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;


public class GlassJugWaterOut implements Action {
    private Gob targetBarrel;
    private NArea area;
    private AreasID id;
    private NAlias resource = null;

    private boolean allFree(NGameUI gui) throws InterruptedException {
        for(GItem item :  gui.getInventory().getWItems(new NAlias("jug")))
            if(NUtils.isContentWater(item))
                return false;
        return true;
    }

    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        ArrayList<Gob> full = new ArrayList<> ();
        while (!allFree(gui)) {
            ArrayList<Gob> gobs;
            if (targetBarrel == null) {
                gobs = Finder.findObjectsInArea(new NAlias("barrel"),
                        (area == null) ? Finder.findNearestMark(id) : area);
            } else {
                gobs = new ArrayList<>();
                gobs.add(targetBarrel);
            }
            Gob gob = null;
            for (Gob candidate : gobs) {
                boolean isFull = false;
                for(Gob f : full){
                    if (f.id == candidate.id){
                        isFull = true;
                    }
                }
                if (!isFull) {
                    if (NUtils.isOverlay(candidate, resource)) {
                        gob = candidate;
                        break;
                    }
                }
            }
            if (gob == null) {
                for (Gob candidate : gobs) {
                    boolean isFull = false;
                    for(Gob f : full){
                        if (f.id == candidate.id){
                            isFull = true;
                        }
                    }
                    if (!isFull) {
                        if (!NUtils.isOverlay(candidate)) {
                            gob = candidate;
                            break;
                        }
                    }
                }
            }
            if (gob == null) {
                return new Results(Results.Types.NO_CONTAINER);
            }

            new PathFinder(gui, gob).run();
            for (GItem item : gui.getInventory().getWItems(new NAlias("jug"))) {
                if (!gui.hand.isEmpty()) {
                    NUtils.transferToInventory();
                    NUtils.waitEvent(() -> gui.hand.isEmpty(), 50);
                }
                if (NUtils.isContentWater(item)) {
                    new TakeToHand(item).run(gui);
                    NUtils.waitEvent(() -> !gui.hand.isEmpty(), 200);
                    NUtils.activateItem(gob);
                    NUtils.waitEvent(() -> !NUtils.isContentWater(gui.vhand.item), 200);
                    if (NUtils.isContentWater(gui.vhand.item)) {
                        full.add(gob);
                        gob = null;
                        break;
                    }
                }


            }
        }
        if (!gui.hand.isEmpty()) {
            NUtils.transferToInventory();
            NUtils.waitEvent(() -> gui.hand.isEmpty(), 50);
        }
        return new Results(Results.Types.SUCCESS);
    }

    public GlassJugWaterOut(NArea area_barrel, NArea area_blocks) {
        this.area_barrel = area_barrel;
        this.area_blocks = area_blocks;
    }

    public GlassJugWaterOut(
            AreasID id,
            NAlias resource
    ) {
        this.id = id;
        this.resource = resource;
    }

    public GlassJugWaterOut(
            NArea area,
            NAlias resource
    ) {
        this.area = area;
        this.resource = resource;
    }

    public GlassJugWaterOut(
            Gob targetBarrel,
            NAlias resource
    ) {
        this.targetBarrel = targetBarrel;
        this.resource = resource;
    }


    NArea area_barrel;
    NArea area_blocks;
}
