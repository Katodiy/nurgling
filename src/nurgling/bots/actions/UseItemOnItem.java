package nurgling.bots.actions;

import haven.GItem;
import haven.WItem;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;

public class UseItemOnItem implements Action {

    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        if(src==null)
            new TakeToHand ( src_item ).run ( gui );
        else
            new TakeToHand ( src ).run ( gui );
        target_item.wdgmsg ( "itemact",0 );
        NUtils.waitEvent(()->gui.vhand==null,50);
        return new Results ( Results.Types.SUCCESS );
    }
    
    public UseItemOnItem(
            NAlias src_item,
            GItem target_item
    ) {
        this.src_item = src_item;
        this.target_item = target_item;
    }

    public UseItemOnItem(
            GItem src_item,
            GItem target_item
    ) {
        this.src = src_item;
        this.target_item = target_item;
    }


    NAlias src_item;
    GItem src;
    GItem target_item;
}
