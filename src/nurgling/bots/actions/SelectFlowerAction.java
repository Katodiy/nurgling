package nurgling.bots.actions;

import haven.*;
import nurgling.*;

import static haven.OCache.posres;

public class SelectFlowerAction implements Action {
    public SelectFlowerAction (
            NGItem item,
            String action,
            Types type
    ) {
        this.item = item;
        this.action = action;
        this.type = type;
    }

    public SelectFlowerAction (
            Gob gob,
            String action,
            Types type
    ) {
        this.gob = gob;
        this.action = action;
        this.type = type;
    }

    public SelectFlowerAction (
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
        Item, Gob, Roastspit
    }

    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {

        Results res = null;
        switch (type) {
            case Item:
                res = selectInventory ( gui );
                break;
            case Gob:
                selectGob ( gui );
                break;
//            case Roastspit:
//                selectRoastspit(gui);
//                break;
        }
        if(res != null && res.type!= Results.Types.SUCCESS)
            return res;
        NUtils.waitEvent(()->NUtils.getUI().findInRoot(FlowerMenu.class)==null,500);
        if (NUtils.getUI().findInRoot(FlowerMenu.class)==null) {
          return new Results ( Results.Types.SUCCESS );
        }
        else {
            return new Results ( Results.Types.SELECT_FLOWER_FAIL );
        }

    }

//    private void selectRoastspit(NGameUI gui) throws InterruptedException {
//        if ( gob == null ) {
//            gob = Finder.findObject ( _name );
//        }
//        Gob.Overlay ol = gob.findol(Roastspit.class);
//        if(ol!=null) {
//            for (int i = 0; i < 5; i++) {
//                gui.map.wdgmsg("click", Coord.z, gob.rc.floor(posres), 3, 0, 1, (int) gob.id,
//                        gob.rc.floor(posres), ol.id, -1);
//                if (NUtils.waitEvent(() -> NFlowerMenu.instance != null, 5)) {
//                    if (NFlowerMenu.instance.isContain(_action)) {
//                        NFlowerMenu.instance.selectInCurrent(_action);
//                        if (NUtils.waitEvent(() -> !NFlowerMenu.selectNext, 5)) {
//                            NFlowerMenu.stop();
//                            return;
//                        }
//                    }
//                }
//            }
//        }
//    }
//
    Results selectGob ( NGameUI gui )
            throws InterruptedException {
//        if (gob == null) {
//            gob = Finder.findObject(_name);
//        }
        if (!trellis) {
            if (gob.rc.dist(NUtils.getGameUI().map.player().rc) > 15)
                new PathFinder(NUtils.getGameUI(), gob).run();
        }
        if (gob != null) {
            gui.map.wdgmsg("click", Coord.z, gob.rc.floor(posres), 3, 0, 0, (int) gob.id,
                    gob.rc.floor(posres), 0, -1);
            NUtils.waitEvent(() -> NUtils.getUI().findInRoot(FlowerMenu.class) != null, 500);
            NFlowerMenu flowerMenu = (NFlowerMenu) NUtils.getUI().findInRoot(FlowerMenu.class);
            if (flowerMenu == null)
                return new Results(Results.Types.NO_FLOWER);
            flowerMenu.select(action);
        }
        return new Results(Results.Types.SUCCESS);

    }

    Results selectInventory ( NGameUI gui )
            throws InterruptedException {
        if ( item != null ) {
            item.wdgmsg ( "iact", item.sz, 0 );
            NUtils.waitEvent(()->NUtils.getUI().findInRoot(FlowerMenu.class)!=null,500);
            NFlowerMenu flowerMenu = (NFlowerMenu) NUtils.getUI().findInRoot(FlowerMenu.class);
            if(flowerMenu == null)
                return new Results(Results.Types.NO_FLOWER);
            flowerMenu.select(action);
        }
        return new Results(Results.Types.SUCCESS);
    }

    NAlias _name;
    NGItem item = null;
    Gob gob = null;
    String action;
    Types type;
    boolean trellis = false;
}
