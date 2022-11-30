package nurgling.bots.actions;

import haven.WItem;
import nurgling.NAlias;
import nurgling.NGameUI;

public class UseItemOnItem implements Action {

    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        new TakeToHand ( src_item ).run ( gui );
        target_item.item.wdgmsg ( "itemact",0 );
        int counter = 0;
        while ( gui.vhand!=null && counter < 20){
            Thread.sleep ( 50 );
            counter++;
        }
        return new Results ( Results.Types.SUCCESS );
    }
    
    public UseItemOnItem(
            NAlias src_item,
            WItem target_item
    ) {
        this.src_item = src_item;
        this.target_item = target_item;
    }
    
    NAlias src_item;
    WItem target_item;
}
