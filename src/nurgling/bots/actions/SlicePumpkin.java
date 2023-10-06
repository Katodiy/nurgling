package nurgling.bots.actions;

import haven.Coord;
import haven.GItem;
import haven.Gob;
import haven.MCache;
import nurgling.*;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

import java.util.ArrayList;
import java.util.Arrays;

public class SlicePumpkin implements Action{
    static NAlias pumpkin_name = new NAlias(new ArrayList<>(Arrays.asList("Pumpkin")),new ArrayList<>(Arrays.asList("Flesh", "seed", "Seed", "flesh", "plant")));
    @Override
    public Results run(NGameUI gui) throws InterruptedException {
        while ( !Finder.findObjectsInArea ( new NAlias("pumpkin"), Finder.findNearestMark(AreasID.pumpkin)).isEmpty () ){

            if ( gui.getInventory ().getNumberFreeCoord (new Coord(3,3)) == 0 && !gui.getInventory ().getWItems().isEmpty () ) {
                while (gui.getInventory ().getItem ( pumpkin_name)!=null) {
                    GItem pumpkin = gui.getInventory().getItem(pumpkin_name);
                    if (pumpkin == null) {
                        return new Results(Results.Types.NO_ITEMS);
                    }
                    int oldSize = gui.getInventory().getWItems( new NAlias("pumpkinflesh", "Pumpkin Flesh") ).size();
                    new SelectFlowerAction((NGItem) pumpkin, "Slice", SelectFlowerAction.Types.Item).run(gui);
                    NUtils.waitEvent(() -> gui.getInventory().getWItems(new NAlias("pumpkinflesh", "Pumpkin Flesh") ).size() != oldSize, 50);
                }
                new OpenBarrelAndTransfer ( 9000,  new NAlias(new ArrayList<>(Arrays.asList("pumpkin", "Pumpkin")),new ArrayList<>(Arrays.asList("flesh", "Flesh"))), AreasID.pumpkin, Finder.findObjectInArea ( new NAlias("barrel"),2000,Finder.findNearestMark(AreasID.pumpkin) ) ).run ( gui );
                new TransferToTrough ( new NAlias("pumpkinflesh", "Pumpkin Flesh") ).run ( gui );


            }

            Gob item = Finder.findObjectInArea ( new NAlias(new ArrayList<>(Arrays.asList("pumpkin")),new ArrayList<>(Arrays.asList("plant"))), 3000, Finder.findNearestMark(AreasID.pumpkin) );
            if(item == null)
                break;
            /// Если предмет далеко, идем к нему с помощью ПФ
            if(item.rc.dist(gui.map.player().rc)> MCache.tilesz2.x) {
                PathFinder pf = new PathFinder(gui, item);
                pf.run();
            }
            /// Подбираем предмет
            int size = NUtils.getGameUI().getInventory().getFreeSpace();
            NUtils.takeFromEarth ( item );
            NUtils.waitEvent(()->size!=NUtils.getGameUI().getInventory().getFreeSpace(),50);
        }
        while (gui.getInventory ().getItem ( pumpkin_name)!=null) {
            GItem pumpkin = gui.getInventory().getItem(pumpkin_name);
            if (pumpkin == null) {
                return new Results(Results.Types.NO_ITEMS);
            }
            int oldSize = gui.getInventory().getWItems( new NAlias("pumpkinflesh", "Pumpkin Flesh") ).size();
            new SelectFlowerAction((NGItem) pumpkin, "Slice", SelectFlowerAction.Types.Item).run(gui);
            NUtils.waitEvent(() -> gui.getInventory().getWItems(new NAlias("pumpkinflesh", "Pumpkin Flesh") ).size() != oldSize, 50);
        }
        new OpenBarrelAndTransfer ( 9000,  new NAlias(new ArrayList<>(Arrays.asList("pumpkin", "Pumpkin")),new ArrayList<>(Arrays.asList("flesh", "Flesh"))), AreasID.pumpkin, Finder.findObjectInArea ( new NAlias("barrel"),2000,Finder.findNearestMark(AreasID.pumpkin) ) ).run ( gui );
        new TransferToTrough ( new NAlias("pumpkinflesh", "Pumpkin Flesh") ).run ( gui );
        return new Results(Results.Types.SUCCESS);
    }
}
