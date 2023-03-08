package nurgling.bots.actions;

import haven.GItem;
import haven.Gob;
import haven.Resource;
import haven.WItem;
import nurgling.*;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class CurdTubeAction implements Action {
    @Override
    public Results run(NGameUI gui)
            throws InterruptedException {
        ArrayList<Gob> in = Finder.findObjectsInArea(new NAlias("curdingtub"),Finder.findNearestMark(main));
        in.sort(new Comparator<Gob>() {
            @Override
            public int compare(
                    Gob lhs,
                    Gob rhs
            ) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return (lhs.rc.y > rhs.rc.y) ? -1 : ((lhs.rc.y < rhs.rc.y) ? 1 : (lhs.rc.x > rhs.rc.x) ? -1 : (
                        lhs.rc.x < rhs.rc.x) ? 1 : 0);
            }
        });

        for (Gob gob : in) {

            if (((gob.getModelAttribute() & 2) != 0)) {
                Results res;
                do {
                    if(gui.getInventory().getItem(new NAlias("cheesetray")) == null)
                        if(new TakeItemsFromBarter(new NAlias("cheesetray"), AreasID.curd_out,false,1).run(gui).type == Results.Types.NO_ITEMS)
                            return new Results(Results.Types.NO_ITEMS);
                    new PathFinder(gui, gob).run();
                    new OpenTargetContainer(gob, "Curding Tub").run(gui);
                    if(gui.getInventory("Curding Tub")!=null && gui.getInventory("Curding Tub").getItems(new NAlias("curd")).size()<4)
                        return new Results(Results.Types.NO_FUEL);

                    res = new TakeFromContainer("Curding Tub", new NAlias("curd"), 4- gui.getInventory().getItems(new NAlias("curd")).size()).run(gui);
                    GItem cheese_tray = NUtils.getGameUI().getInventory().getItem(new NAlias("cheesetray"));
                    NUtils.waitEvent(()->gui.getInventory().getItems(new NAlias("invobjs/curd-")).size()>=4,10);
                    if(gui.getInventory().getItems(new NAlias("invobjs/curd-")).size()<4)
                        return new Results(Results.Types.NO_FUEL);
                    int finalcount = gui.getInventory().getItems(new NAlias("invobjs/curd-")).size()-4;
                    while (gui.getInventory().getItems(new NAlias("invobjs/curd-")).size()!=finalcount)
                        new UseItemOnItem(new NAlias("invobjs/curd-"),cheese_tray).run(gui);
                    if(gui.getInventory("Curding Tub").getItems(new NAlias("invobjs/curd-")).size()<4)
                        break;
                    res = new TransferItemsToBarter(Finder.findSubArea(main, AreasID.curd_out), new NAlias("cheesetray"), false).run(gui);
                }
                while (res.type == Results.Types.SUCCESS && (gob.getModelAttribute() & 2) != 0);
                res = new TransferItemsToBarter(Finder.findSubArea(main, AreasID.curd_out), new NAlias("cheesetray"), false).run(gui);
            }
        }
        return new Results(Results.Types.SUCCESS);
    }

    AreasID main;

    public CurdTubeAction(AreasID main) {
        this.main = main;
    }
}
