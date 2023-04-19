package nurgling.bots.actions;

import haven.Coord;
import haven.GItem;
import haven.Gob;
import haven.WItem;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NInventory;
import nurgling.NUtils;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;


public class WaterSkinWaterIn implements Action {
    private NArea water_in;

    private boolean allFull(NGameUI gui) throws InterruptedException {
        WItem wbelt = Finder.findDressedItem ( new NAlias ("belt") );
        if(wbelt!=null) {
            NInventory belt = ((NInventory) wbelt.item.contents);
            ArrayList<GItem> wskins = belt.getWItems(new NAlias("waterskin"));
            for (GItem witem : wskins) {
                if (!NUtils.isContentWater(witem)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        while (!allFull(gui)) {
            Gob gob = Finder.findObjectInArea(new NAlias("barrel", "cistern"), 1000, water_in);

            if (gob == null) {
                return new Results(Results.Types.NO_CONTAINER);
            }

            WItem wbelt = Finder.findDressedItem(new NAlias("belt"));
            if (wbelt != null) {
                NInventory belt = ((NInventory) wbelt.item.contents);
                ArrayList<GItem> wskins = belt.getWItems(new NAlias("waterskin"));
                for (GItem witem : wskins) {
                    if (NUtils.getContent(witem) == null) {
                        if (!gui.hand.isEmpty()) {
                            if(NUtils.isIt(gui.vhand, "waterskin")) {
                                NUtils.transferToInventory();
                                NUtils.waitEvent(() -> gui.hand.isEmpty() && NUtils.getGameUI().getInventory().getItem(new NAlias("waterskin"))!=null, 50);
                                GItem in_inventory = NUtils.getGameUI().getInventory().getItem(new NAlias("waterskin"));
                                in_inventory.wdgmsg("transfer", Coord.z, 1);
                                NUtils.waitEvent(() -> NUtils.getGameUI().getInventory().getItem(in_inventory) == null, 50);
                            }
                        }
                        if (NUtils.isIt(gob, new NAlias("barrel")) && !NUtils.isOverlay(gob, new NAlias("water")))
                            return new Results(Results.Types.NO_WATER);
                        new TakeToHand(witem).run(gui);
                        NUtils.waitEvent(() -> !gui.hand.isEmpty(), 200);
                        NUtils.activateItem(gob);
                        NUtils.waitEvent(() -> NUtils.isContentWater(gui.vhand.item) || (NUtils.isIt(gob, new NAlias("barrel")) &&!NUtils.isOverlay(gob, new NAlias("water"))), 2000);
                    }
                    if (!gui.hand.isEmpty()) {
                        NUtils.transferToInventory();
                        NUtils.waitEvent(() -> gui.hand.isEmpty() && NUtils.getGameUI().getInventory().getItem(new NAlias("waterskin"))!=null, 50);
                        GItem in_inventory = NUtils.getGameUI().getInventory().getItem(new NAlias("waterskin"));
                        in_inventory.wdgmsg("transfer", Coord.z, 1);
                        NUtils.waitEvent(() -> NUtils.getGameUI().getInventory().getItem(in_inventory) == null, 50);
                    }
                }
            }
        }
        return new Results(Results.Types.SUCCESS);
    }

    public WaterSkinWaterIn(NArea water_in) {
        this.water_in = water_in;
    }
}
