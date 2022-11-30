package nurgling.bots.actions;

import haven.Coord;
import haven.WItem;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;

import java.util.ArrayList;
import java.util.Arrays;

import static haven.OCache.posres;

public class CheckClay implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        NUtils.transferToInventory ();
        NUtils.command ( new char[]{ 'a', 'd' } );
        int size = gui.getInventory ().getFreeSpace ();
        gui.map.wdgmsg ( "click", Coord.z, gui.map.player ().rc.floor ( posres ), 1, 0 );
        NUtils.waitEvent ( () -> gui.getInventory ().getFreeSpace () != size, 2000 );
        gui.map.wdgmsg("click", Coord.z, gui.map.player().rc.floor(posres), 3, 0);
        NUtils.waitEvent(NUtils::isIdleCurs,20);
        NUtils.stopWithClick ();
        ArrayList<WItem> items = gui.getInventory ().getItems (
                new NAlias( new ArrayList<> ( Arrays.asList ( "clay", "sand", "soil", "worm" ) ),
                        new ArrayList<> ( Arrays.asList ( "pit", "cave" ) ) ) );
        
        if ( !items.isEmpty () ) {
            for ( WItem item : items ) {
                double q = NUtils.getWItemQuality ( item );
                gui.msg ( item.item.getres ().name + " quality = " + q );
                NUtils.drop ( item );
                Thread.sleep ( 100 );
            }
        }
        return new Results ( Results.Types.SUCCESS );
    }
}
