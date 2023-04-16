package nurgling.bots.actions;


import haven.Coord;
import haven.Gob;
import haven.Resource;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.bots.tools.OutContainer;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

import java.util.ArrayList;

public class DryerTobaccoAction implements Action {

    @Override
    public Results run(NGameUI gui)
            throws InterruptedException {
        if (Finder.findObjectInArea(new NAlias("barter"), 3000, Finder.findNearestMark(AreasID.tobacco_out)) == null) {
            /// Забираем высушенный табак и переносим его в ящики
            new FillContainers(new NAlias("tobacco-cured"), AreasID.tobacco_out, new ArrayList<>(), new TakeMaxFromContainers(new NAlias("tobacco-cured"), AreasID.tobacco_work, new ArrayList<>())).run(gui);
        } else {
            ArrayList<Gob> in = Finder.findObjects ( new NAlias("htable"), AreasID.tobacco_work );

            for ( Gob gob : in ) {
                boolean empty = true;
                for ( Gob.Overlay ol : gob.ols ) {
                    if ( ol.res != null ) {
                        Resource olres = ol.res.get ();
                        if ( olres.name.startsWith ( "gfx/fx/eq" ) ) {
                            empty = false;
                        }
                    }
                }
                if(!empty) {
                    Results res;
                    do {
                        new PathFinder( gui, gob ).run ();
                        NUtils.waitEvent(()->gui.getWindow ( "Herbalist Table" )==null,300);
                        new OpenTargetContainer ( gob, "Herbalist Table" ).run ( gui );
                        NUtils.waitEvent(()->gui.getInventory( "Herbalist Table" )!=null && (!gob.ols.isEmpty() && gui.getInventory("Herbalist Table").getFreeSpace()<16),300);
                        res = new TakeMaxFromContainer ( "Herbalist Table", new NAlias("tobacco-cured") ).run ( gui );
                        if ( res.type == Results.Types.FULL ) {
                            new TransferItemsToBarter(AreasID.tobacco_out, new NAlias("tobacco-cured"), false).run(gui);
                        }
                    }
                    while ( res.type == Results.Types.FULL );
                }
            }
            if ( !gui.getInventory ().getWItems( new NAlias("tobacco-cured") ).isEmpty () ) {
                new TransferItemsToBarter(AreasID.tobacco_out, new NAlias("tobacco-cured"), false).run(gui);
            }
        }
        /// Заполняем новыми свежими листьями
        if (Finder.findObjectInArea(new NAlias("barter"), 3000, Finder.findNearestMark(AreasID.tobacco_in)) == null) {
            new FillContainers(new NAlias("tobacco-fresh"), AreasID.tobacco_work, new ArrayList<>(), new TakeMaxFromContainers(new NAlias("tobacco-fresh"), AreasID.tobacco_in, new ArrayList<>())).run(gui);
        } else {
            int current_size;
            ArrayList<OutContainer> outContainers = new ArrayList<>();
            do {
                if (gui.getInventory().getNumberFreeCoord(new Coord(1, 2)) > 1) {
                    new TakeItemsFromBarter(new NAlias("tobacco-fresh"), AreasID.tobacco_in, false, gui.getInventory().getNumberFreeCoord(new Coord(1, 2)) - 1).run(gui);
                }
                current_size = gui.getInventory().getFreeSpace();
                new FillContainers(new NAlias("tobacco-fresh"), AreasID.tobacco_work, outContainers).run(gui);
                boolean isFull = true;
                for (OutContainer gob : outContainers) {
                    if (!gob.isFull) {
                        isFull = false;
                        break;
                    }
                }
                if (isFull)
                    break;
            }
            while (current_size != gui.getInventory().getFreeSpace());
            new TransferItemsToBarter(AreasID.tobacco_in, new NAlias("tobacco-fresh"), false).run(gui);
        }

        /// Сбрасываем лишнее в изначальные стокпайлы
        new FillContainers(new NAlias("tobacco-fresh"), AreasID.tobacco_in, new ArrayList<>()).run(gui);

        return new Results(Results.Types.SUCCESS);
    }
}
