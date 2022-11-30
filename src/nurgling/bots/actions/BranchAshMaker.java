package nurgling.bots.actions;

import haven.Coord;
import haven.Gob;
import haven.WItem;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.tools.Finder;
import nurgling.tools.NArea;


public class BranchAshMaker implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        Gob barrel = Finder.findObjectInArea(new NAlias("barrel"),1000,area_barrel);
        Gob brazier = Finder.findObject(new NAlias("brazier"));
        if(brazier==null){
            return new Results(Results.Types.NO_BRAZIER);
        }
        Gob inPile = null;

        new Equip(new NAlias("stoneaxe")).run(gui);
        while ( Finder.findObjectInArea ( new NAlias("stockpile"), 1000, area_blocks )!=null) {
            if (Finder.findObjectInArea(new NAlias("block"), 1000, area_blocks) != null) {
                inPile = Finder.findObjectInArea(new NAlias("block"), 1000, area_blocks);
                if (inPile == null) {
                    return new Results(Results.Types.SUCCESS);
                }
                new PathFinder(gui, inPile).run();
                new OpenTargetContainer(inPile, "Stockpile").run(gui);
                while (gui.getInventory().getNumberFreeCoord(new Coord(2, 1)) > 2) {
                    NUtils.takeItemFromPile();
                    if (Finder.findObject(inPile.id) == null) {
                        inPile = Finder.findObjectInArea(new NAlias("block"), 1000, area_blocks);
                        if (inPile == null) {
                            break;
                        }
                        new PathFinder(gui, inPile).run();
                        new OpenTargetContainer(inPile, "Stockpile").run(gui);
                    }
                }
                new PathFinder(gui, brazier).run();
                WItem block = gui.getInventory().getItem(new NAlias("block"));
                while (block != null) {
                    new SelectFlowerAction(block, "Split", SelectFlowerAction.Types.Inventory).run(gui);
                    NUtils.waitEvent(() -> !gui.getInventory().getItems(new NAlias("branch")).isEmpty(), 20);
                    while (!gui.getInventory().getItems(new NAlias("branch")).isEmpty()) {
                        NUtils.takeItemToHand(gui.getInventory().getItem(new NAlias("branch")).item);
                        NUtils.activateItem(brazier);
                        NUtils.waitEvent(() -> NUtils.getProg() >= 0, 100);
                        NUtils.waitEvent(() -> !gui.hand.isEmpty() && !NUtils.isIt(gui.vhand, new NAlias("branch")), 200);
                        NUtils.transferToInventory();
                        NUtils.waitEvent(() -> gui.hand.isEmpty(), 100);
                    }
                    new TransferToBarrel(barrel, new NAlias("ash")).run(gui);
                    block = gui.getInventory().getItem(new NAlias("block"));
                }
            } else if (Finder.findObjectInArea(new NAlias("branch"), 1000, area_blocks) != null)  {
                new TakeMaxFromPile(area_blocks).run(gui);
                NUtils.waitEvent(() -> !gui.getInventory().getItems(new NAlias("branch")).isEmpty(), 20);
                while (!gui.getInventory().getItems(new NAlias("branch")).isEmpty()) {
                    NUtils.takeItemToHand(gui.getInventory().getItem(new NAlias("branch")).item);
                    NUtils.activateItem(brazier);
                    NUtils.waitEvent(() -> NUtils.getProg() >= 0, 100);
                    NUtils.waitEvent(() -> !gui.hand.isEmpty() && !NUtils.isIt(gui.vhand, new NAlias("branch")), 200);
                    NUtils.transferToInventory();
                }
                new TransferToBarrel(barrel, new NAlias("ash")).run(gui);
            }
        }
        while ( Finder.findObjectInArea ( new NAlias("log"), 1000, area_blocks )!=null ) {
            if (Finder.findObjectInArea(new NAlias("log"), 1000, area_blocks) != null) {
                new WorkWithLog(gui.getInventory().getNumberFreeCoord(new Coord(2, 1))-2,new NAlias("log"),true,area_blocks).run(gui);
                new PathFinder(gui, brazier).run();
                WItem block = gui.getInventory().getItem(new NAlias("block"));
                while (block != null) {
                    new SelectFlowerAction(block, "Split", SelectFlowerAction.Types.Inventory).run(gui);
                    NUtils.waitEvent(() -> !gui.getInventory().getItems(new NAlias("branch")).isEmpty(), 20);
                    while (!gui.getInventory().getItems(new NAlias("branch")).isEmpty()) {
                        NUtils.takeItemToHand(gui.getInventory().getItem(new NAlias("branch")).item);
                        NUtils.activateItem(brazier);
                        NUtils.waitEvent(() -> NUtils.getProg() >= 0, 100);
                        NUtils.waitEvent(() -> !gui.hand.isEmpty() && !NUtils.isIt(gui.vhand, new NAlias("branch")), 200);
                        NUtils.transferToInventory();
                    }
                    new TransferToBarrel(barrel, new NAlias("ash")).run(gui);
                    block = gui.getInventory().getItem(new NAlias("block"));
                }
            } else if (Finder.findObjectInArea(new NAlias("branch"), 1000, area_blocks) != null)  {
                new TakeMaxFromPile(area_blocks).run(gui);
                NUtils.waitEvent(() -> !gui.getInventory().getItems(new NAlias("branch")).isEmpty(), 20);
                while (!gui.getInventory().getItems(new NAlias("branch")).isEmpty()) {
                    NUtils.takeItemToHand(gui.getInventory().getItem(new NAlias("branch")).item);
                    NUtils.activateItem(brazier);
                    NUtils.waitEvent(() -> NUtils.getProg() >= 0, 100);
                    NUtils.waitEvent(() -> !gui.hand.isEmpty() && !NUtils.isIt(gui.vhand, new NAlias("branch")), 200);
                    NUtils.transferToInventory();
                }
                new TransferToBarrel(barrel, new NAlias("ash")).run(gui);
            }
        }

        return new Results(Results.Types.SUCCESS);
    }

    public BranchAshMaker(NArea area_barrel, NArea area_blocks) {
        this.area_barrel = area_barrel;
        this.area_blocks = area_blocks;
    }

    NArea area_barrel;
    NArea area_blocks;
}
