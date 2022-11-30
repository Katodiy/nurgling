package nurgling.bots.actions;

import haven.WItem;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;

public class TakeToHand implements Action {
    public TakeToHand(NAlias name ) {
        this.name = name;
    }
    
    public TakeToHand(WItem item ) {
        this.item = item;
    }
    
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        if ( item == null ) {
            item = gui.getInventory ().getItem ( name );
        }
        if ( item != null ) {
                NUtils.takeItemToHand ( item.item );
                NUtils.waitEvent(()->!gui.hand.isEmpty () , 20);
            if ( !gui.hand.isEmpty () ) {
                return new Results ( Results.Types.SUCCESS );
            }
        }
        return new Results ( Results.Types.FAIL );
    }
    
    NAlias name;
    WItem item = null;
}
