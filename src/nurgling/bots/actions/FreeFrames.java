package nurgling.bots.actions;

import haven.Gob;
import haven.Resource;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.PathFinder;
import nurgling.tools.Finder;


import java.util.ArrayList;
import java.util.Comparator;

public class FreeFrames implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        ArrayList<Gob> in = Finder.findObjects ( new NAlias("dframe") );
        in.sort ( new Comparator<Gob> () {
            @Override
            public int compare (
                    Gob lhs,
                    Gob rhs
            ) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return ( lhs.rc.y > rhs.rc.y ) ? -1 : ( ( lhs.rc.y < rhs.rc.y ) ? 1 : ( lhs.rc.x > rhs.rc.x ) ? -1 : (
                        lhs.rc.x < rhs.rc.x ) ? 1 : 0 );
            }
        } );
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
                    new OpenTargetContainer ( gob, "Frame" ).run ( gui );
                    res = new TakeMaxFromContainer ( "Frame", items ).run ( gui );
                    if ( res.type == Results.Types.FULL ) {
                        new TransferHides ().run ( gui );
                    }
                }
                while ( res.type == Results.Types.FULL );
            }
        }
        if ( !gui.getInventory ().getItems ( items ).isEmpty () ) {
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
