package nurgling.bots.actions;

import haven.Coord;
import haven.WItem;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;

import static haven.OCache.posres;

public class CheckWater implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        WItem item = gui.getInventory ().getItem ( new NAlias( "woodencup" ) );
        new TakeToHand ( item ).run ( gui );
        gui.map.wdgmsg ( "itemact", Coord.z, gui.getMap ().player ().rc.floor ( posres ), 0 );
        Thread.sleep ( 500 );

        NUtils.transferToInventory ();
        gui.msg ( "Water q =" +String.valueOf ( NUtils.getContentQuality ( gui.getInventory ().getItem ( new NAlias ( "woodencup" )) ) ) );
        gui.map.wdgmsg ( "click", Coord.z, gui.getMap ().player ().rc.floor ( posres ), 3, 0, 0);
        NUtils.waitEvent ( () -> gui.getInventory ().getItem ( new NAlias ( "woodencup" ) ) != null, 60 );
        new SelectFlowerAction ( gui.getInventory ().getItem ( new NAlias ( "woodencup" )), "Empty",
                SelectFlowerAction.Types.Inventory ).run ( gui );
        NUtils.waitEvent ( () -> NUtils.getContent (gui.getInventory ().getItem ( new NAlias ( "woodencup" )).item ) == null, 10 );
        return new Results ( Results.Types.SUCCESS );
    }
}
