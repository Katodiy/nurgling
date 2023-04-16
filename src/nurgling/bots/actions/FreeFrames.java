package nurgling.bots.actions;

import haven.Gob;
import haven.Resource;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.tools.Finder;


import java.util.ArrayList;

public class FreeFrames implements Action {

    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        ArrayList<Gob> in = Finder.findObjects ( new NAlias("dframe") );

        for ( Gob gob : in ) {
            boolean empty = true;
            for ( Gob.Overlay ol : gob.ols ) {
                if ( ol.res != null ) {
                    Resource olres = ol.res.get ();
                    if ( olres.name.startsWith ( "gfx/terobjs/dframe-" ) ) {
                        empty = false;
                    }
                }
            }
            if(!empty) {
                Results res;
                do {
                    new PathFinder( gui, gob ).run ();
                    NUtils.waitEvent(()->gui.getWindow ( "Frame" )==null,300);
                    new OpenTargetContainer ( gob, "Frame" ).run ( gui );
                    NUtils.waitEvent(()->gui.getInventory( "Frame" )!=null && (!gob.ols.isEmpty() && gui.getInventory("Frame").getFreeSpace()<4),300);
                    res = new TakeMaxFromContainer ( "Frame", items ).run ( gui );
                    if ( res.type == Results.Types.FULL ) {
                        new TransferHides ().run ( gui );
                    }
                }
                while ( res.type == Results.Types.FULL );
            }
        }
        if ( !gui.getInventory ().getWItems( items ).isEmpty () ) {
            new TransferHides (  ).run ( gui );
        }
        return new Results ( Results.Types.SUCCESS );
    }
    
    public FreeFrames (
            NAlias items

    ) {
        this.items = items;
    }
    
    NAlias items;
}
