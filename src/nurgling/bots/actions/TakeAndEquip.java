package nurgling.bots.actions;

import haven.Coord;
import haven.Gob;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.tools.Finder;

import static haven.OCache.posres;

public class TakeAndEquip implements Action {
    public TakeAndEquip(
            NAlias name,
            boolean ignore_res
    ) {
        this.name = name;
        this.ignore_res = ignore_res;
    }
    
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        NUtils.freeHands ( name );
        Gob gob = Finder.findObject ( name );
        if ( gob != null ) {
            new PathFinder( gui, gob ).run();
            gui.map.wdgmsg ( "click", Coord.z, gob.rc.floor ( posres ), 3, 0, 0, ( int ) gob.id,
                    gob.rc.floor ( posres ), 0, -1 );
            
            int counter = 0;
            while ( gui.hand.isEmpty () && counter < 20 ) {
                Thread.sleep ( 100 );
                counter++;
            }
            if ( gui.hand.isEmpty () ) {
                new Results ( Results.Types.DROP_FAIL );
            }
            NUtils.transferToEquipmentHands ();
            Thread.sleep ( 200 );
        }
        return ( Finder.findDressedItem ( name ) != null ) || ignore_res ? new Results (
                Results.Types.SUCCESS ) : new Results ( Results.Types.DROP_FAIL );
    }
    
    NAlias name;
    boolean ignore_res;
}
