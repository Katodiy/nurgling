package nurgling.bots.actions;

import haven.Gob;
import haven.WItem;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.tools.Finder;

public class FeedCloverAction implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        WItem item = gui.getInventory ().getItem ( new NAlias( "clover" ) );
        new TakeToHand ( item ).run ( gui );
        Gob finded = Finder.findObject(new NAlias("horse", "cattle", "boar", "goat", "sheep"));
        if(finded!=null){
            NUtils.activateItem(finded);
        }
        NUtils.waitEvent ( () -> NUtils.getGameUI().hand.isEmpty(), 50000 );
        return new Results ( Results.Types.SUCCESS );
    }
}
