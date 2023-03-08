package nurgling.bots.actions;

import haven.GItem;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;

public class TakeToHand implements Action {
    public TakeToHand(NAlias name ) {
        this.name = name;
    }
    
    public TakeToHand(GItem item ) {
        this.item = item;
    }
    
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        if ( item == null ) {
            item = gui.getInventory ().getItem ( name );
        }
        if ( item != null ) {
                NUtils.takeItemToHand ( item );
                NUtils.waitEvent(()->!gui.hand.isEmpty () , 500);
            if ( !gui.hand.isEmpty () ) {
                return new Results ( Results.Types.SUCCESS );
            }
        }
        return new Results ( Results.Types.FAIL );
    }
    
    NAlias name;
    GItem item = null;
}
