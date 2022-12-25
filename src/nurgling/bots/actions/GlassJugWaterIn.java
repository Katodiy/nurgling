package nurgling.bots.actions;

import haven.Gob;
import haven.WItem;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.tools.Finder;


public class GlassJugWaterIn implements Action {
    private boolean allFull(NGameUI gui) throws InterruptedException {
        for(WItem item :  gui.getInventory().getItems(new NAlias("jug")))
            if(!NUtils.isContentWater(item.item))
                return false;
        return true;
    }

    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        while (!allFull(gui)) {
            Gob gob = Finder.findObject(new NAlias("barrel"));

            if (gob == null) {
                return new Results(Results.Types.NO_CONTAINER);
            }

//            new PathFinder(gui, gob).run();
            for (WItem item : gui.getInventory().getItems(new NAlias("jug"))) {
                if (!gui.hand.isEmpty()) {
                    NUtils.transferToInventory();
                    NUtils.waitEvent(() -> gui.hand.isEmpty(), 50);
                }
                if( !NUtils.isOverlay ( gob, new NAlias("water") ))
                    return new Results(Results.Types.NO_FUEL);
                if (!NUtils.isContentWater(item.item)) {
                    new TakeToHand(item).run(gui);
                    NUtils.waitEvent(() -> !gui.hand.isEmpty(), 200);
                    NUtils.activateItem(gob);
                    NUtils.waitEvent(() -> NUtils.isContentWater(gui.vhand.item) || !NUtils.isOverlay ( gob, new NAlias("water") ), 2000);
                }
            }
        }
        if (!gui.hand.isEmpty()) {
            NUtils.transferToInventory();
            NUtils.waitEvent(() -> gui.hand.isEmpty(), 50);
        }
        return new Results(Results.Types.SUCCESS);
    }
}
