package nurgling.bots.actions;

import haven.*;
import haven.res.gfx.terobjs.roastspit.Roastspit;
import nurgling.*;
import nurgling.tools.Finder;

import static haven.OCache.posres;

public class SelectFlowerAction implements Action {
    public SelectFlowerAction(
            NGItem item,
            String action,
            Types type
    ) {
        this.item = item;
        this.action = action;
        this.type = type;
    }

    boolean skip = false;
    public SelectFlowerAction(
            NGItem item,
            String action,
            Types type,
            boolean skip
    ) {
        this(item, action, type);
        this.skip = skip;
    }

    public SelectFlowerAction(
            NAlias name,
            String action,
            Types type
    ) {
        this.name = name;
        this.action = action;
        this.type = type;
    }

    public SelectFlowerAction(
            Gob gob,
            String action,
            Types type
    ) {
        this.gob = gob;
        this.action = action;
        this.type = type;
    }

    public SelectFlowerAction(
            Gob gob,
            String action,
            Types type,
            boolean trellis
    ) {
        this.gob = gob;
        this.action = action;
        this.type = type;
        this.trellis = trellis;
    }


    public enum Types {
        Item, Gob, Roastspit, Equipment
    }

    @Override
    public Results run(NGameUI gui)
            throws InterruptedException {
        if(!skip)
            NUtils.waitEvent(()->NUtils.isPose(NUtils.getGameUI().getMap().player(),new NAlias("idle")),1000);
        Results res = null;
        switch (type) {
            case Item:
                res = selectInventory(gui);
                break;
            case Gob:
                res = selectGob(gui);
                break;
            case Roastspit:
                res = selectRoastspit(gui);
                break;
            case Equipment:
                res = selectEquipment(gui);
                break;
        }
        if (res != null && res.type != Results.Types.SUCCESS)
            return res;
        NUtils.waitEvent(() -> NUtils.getFlowerMenu() == null, 500);
        if (NUtils.getFlowerMenu() == null) {
            return new Results(Results.Types.SUCCESS);
        } else {
            return new Results(Results.Types.SELECT_FLOWER_FAIL);
        }

    }

    private Results selectRoastspit(NGameUI gui) throws InterruptedException {
        if (gob == null) {
            gob = Finder.findObject(name);
        }
        Gob.Overlay ol = gob.findol(Roastspit.class);
        if (ol != null) {
            gui.map.wdgmsg("click", Coord.z, gob.rc.floor(posres), 3, 0, 1, (int) gob.id,
                    gob.rc.floor(posres), ol.id, -1);
            if (NUtils.waitEvent(() -> NUtils.getFlowerMenu() != null, 50)) {
                NFlowerMenu fm = NUtils.getFlowerMenu();
                if (fm != null) {
                    if (fm.find(action)) {
                        fm.select(action);
                        NUtils.waitEvent(() -> NUtils.getFlowerMenu() == null, 50);
                        return new Results(Results.Types.SUCCESS);
                    }
                }
            }
        }
        return new Results(Results.Types.NO_FLOWER);
    }

    Results selectGob(NGameUI gui)
            throws InterruptedException {
        if (gob == null) {
            gob = Finder.findObject(name);
        }
        if (!trellis) {
            if (gob.rc.dist(NUtils.getGameUI().map.player().rc) > 15)
                new PathFinder(NUtils.getGameUI(), gob).run();
        }
        if (gob != null) {
            if(NUtils.checkGobFlower(new NAlias(action), gob)) {
                NFlowerMenu flowerMenu = NUtils.getFlowerMenu();
                if (flowerMenu == null)
                    return new Results(Results.Types.NO_FLOWER);
                flowerMenu.select(action);
            }
        }
        return new Results(Results.Types.SUCCESS);

    }

    Results selectInventory(NGameUI gui)
            throws InterruptedException {
        if(item==null) {
            item =(NGItem) NUtils.getGameUI().getInventory().getItem(name);
        }
        if (item != null) {
            item.wdgmsg("iact", item.sz, 0);
            NUtils.waitEvent(() -> NUtils.getFlowerMenu() != null, 500);
            NFlowerMenu flowerMenu = NUtils.getFlowerMenu();
            if (flowerMenu == null)
                return new Results(Results.Types.NO_FLOWER);
            flowerMenu.select(action);
        }
        return new Results(Results.Types.SUCCESS);
    }

    Results selectEquipment(NGameUI gui)
            throws InterruptedException {
        if(item==null) {
            WItem wItem = Finder.findDressedItem(name);
            if(wItem!=null)
                item = (NGItem) wItem.item;
        }
        if (item != null) {
            item.wdgmsg("iact", item.sz, 0);
            NUtils.waitEvent(() -> NUtils.getFlowerMenu() != null, 500);
            NFlowerMenu flowerMenu = NUtils.getFlowerMenu();
            if (flowerMenu == null)
                return new Results(Results.Types.NO_FLOWER);
            flowerMenu.select(action);
        }
        return new Results(Results.Types.SUCCESS);
    }



    NAlias name;
    NGItem item = null;
    Gob gob = null;
    String action;
    Types type;
    boolean trellis = false;
}
