package nurgling.bots.actions;

import haven.Gob;
import haven.WItem;
import nurgling.*;
import nurgling.NExceptions.NoFreeSpace;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;
import nurgling.tools.NArea;
import nurgling.tools.PileMaker;

import java.util.ArrayList;

public class TransferToPile implements Action {


    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        if(NUtils.checkName(name.getDefault(),new NAlias("soil")))
        {
            name = new NAlias("gfx/terobjs/stockpile-soil");
        }else if(NUtils.checkName(name.getDefault(),new NAlias("board")))
        {
            name = new NAlias("gfx/terobjs/stockpile-board");
        }else if(NUtils.checkName(name.getDefault(),new NAlias("pumpkin")))
        {
            name = new NAlias("gfx/terobjs/stockpile-pumpkin");
        }else if(NUtils.checkName(name.getDefault(),new NAlias("metal")))
        {
            name = new NAlias("gfx/terobjs/stockpile-metal");
        }else if(NUtils.checkName(name.getDefault(),new NAlias("brick")))
        {
            name = new NAlias("gfx/terobjs/stockpile-brick");
        }else if(NUtils.checkName(name.getDefault(),new NAlias("leaf")))
        {
            name = new NAlias("gfx/terobjs/stockpile-leaf");
        }
        if (!gui.getInventory().getItems(items).isEmpty()) {
            if (area == null)
                area = Finder.findNearestMark(id);
            if (area != null) {
                Gob target = null;
                for (Gob gob : Finder.findObjectsInArea(name, area)) {
                    if (gob.getModelAttribute() != 31) {
                        target = gob;
                        PathFinder pf = new PathFinder(gui, target);
                        pf.setHardMode(true);
                        pf.run();
                        if (!gui.hand.isEmpty()) {
                            NUtils.activateItemToPile(gob);
                            NUtils.waitEvent(() -> NUtils.getGameUI().hand.isEmpty(), 50);
                            if (NOCache.getgob(gob).getModelAttribute() == 31)
                                continue;
                        }
                        break;
                    }
                }
                ArrayList<WItem> witems = gui.getInventory().getItems(items);
                boolean qIsFind = false;
                for (WItem witem : witems) {
                    if (NUtils.getWItemQuality(witem) >= q) {
                        qIsFind = true;
                        break;
                    }
                }
                if (!qIsFind) {
                    return new Results(Results.Types.SUCCESS);
                }
                if (target == null) {
                    PileMaker maker = new PileMaker(gui, area, hitBox, name);
                    maker.setItemName(items);
                    try {
                        maker.create();
                    } catch (NoFreeSpace noFreeSpace) {
                        return new Results(Results.Types.NO_FREE_SPACE);
                    }
                }

                if (new OpenTargetContainer(Finder.findObject(name), "Stockpile").run(gui).type != Results.Types.SUCCESS) {
                    return new Results(Results.Types.OPEN_FAIL);
                }
                NUtils.transferAlltoStockPile(items, q);
                if (!gui.getInventory().getItems(items, q).isEmpty()) {
                    run(gui);
                }
            }
        }
        return new Results(Results.Types.SUCCESS);
    }
    
    public TransferToPile(
            AreasID id,
            NHitBox hitBox,
            NAlias name,
            NAlias items
    ) {
        this.id = id;
        this.hitBox = hitBox;
        this.name = name;
        this.items = items;
    }
    
    public TransferToPile(
            NArea area,
            NHitBox hitBox,
            NAlias name,
            NAlias items
    ) {
        this.area =area;
        this.hitBox = hitBox;
        this.name = name;
        this.items = items;
    }

    public TransferToPile(
            AreasID id,
            NHitBox hitBox,
            NAlias name,
            NAlias items,
            double q
    ) {
        this.id = id;
        this.hitBox = hitBox;
        this.name = name;
        this.items = items;
        this.q = q;
    }

    public TransferToPile(
            NArea area,
            NHitBox hitBox,
            NAlias name,
            NAlias items,
            double q
    ) {
        this.area =area;
        this.hitBox = hitBox;
        this.name = name;
        this.items = items;
        this.q = q;
    }

    AreasID id;
    NHitBox hitBox;
    NAlias name;
    NAlias items;
    NArea area = null;

    private double q =-1;
}
