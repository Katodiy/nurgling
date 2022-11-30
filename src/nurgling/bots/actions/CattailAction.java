package nurgling.bots.actions;

import haven.Coord;
import haven.Gob;
import haven.WItem;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.tools.Finder;

import java.util.ArrayList;
import java.util.Arrays;

import static haven.OCache.posres;

public class CattailAction implements Action {
    @Override
    public Results run(NGameUI gui)
            throws InterruptedException {
        while (gui.getInventory().getFreeSpace() > 0) {
            Thread.sleep(50);
            for (WItem fordrop : gui.getInventory().getItems(
                    new NAlias(new ArrayList<String>(Arrays.asList("cattail")),
                            new ArrayList<>(Arrays.asList("fibre"))))) {
                NUtils.drop(fordrop);
            }
            if(gui.getInventory("Basket")!=null) {
                for (WItem fordrop : gui.getInventory("Basket").getItems(
                        new NAlias(new ArrayList<String>(Arrays.asList("cattail")),
                                new ArrayList<>(Arrays.asList("fibre"))))) {
                    NUtils.drop(fordrop);
                }
            }
                //Gob candidate = Finder.findObject(new NAlias("cattail"));
            //if (candidate != null) {
//
            //    if (gui.getInventory().getFreeSpace() > 0) {
            //        gui.map.wdgmsg ( "click", Coord.z, candidate.rc.floor ( posres ), 1, 0 );
            //        NUtils.waitEvent(() -> candidate.rc.dist(gui.map.player().rc) < 10, 20);
            //        int size = gui.getInventory().getFreeSpace();
            //        if(Finder.findObject(candidate.id)!=null) {
            //            new SelectFlowerAction(candidate, "Pick", SelectFlowerAction.Types.Gob).run(gui);
            //            NUtils.waitEvent(() -> size != gui.getInventory().getFreeSpace(), 100);
            //            for (WItem fordrop : gui.getInventory().getItems(
            //                    new NAlias(new ArrayList<String>(Arrays.asList("cattail")),
            //                            new ArrayList<>(Arrays.asList("fibre"))))) {
            //                NUtils.drop(fordrop);
            //            }
            //        }
            //    } else {
            //        return new Results(Results.Types.NO_FREE_SPACE);
            //    }
            //}
        }
        return new Results(Results.Types.SUCCESS);
    }

    public CattailAction(
    ) {

    }

}
