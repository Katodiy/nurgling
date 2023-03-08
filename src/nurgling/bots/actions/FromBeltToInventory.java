package nurgling.bots.actions;

import haven.Coord;
import haven.GItem;
import haven.WItem;
import nurgling.*;
import nurgling.tools.Finder;

public class FromBeltToInventory implements Action{
    public FromBeltToInventory(NAlias name) {
        this.name = name;
    }

    ///I can't directly transfer items from my belt to containers. Therefore, this action allows you to transfer items from the belt to the inventory
    @Override
    public Results run(NGameUI gui) throws InterruptedException {

        /// If item in player inventory - success
        if (gui.getInventory().getItem ( name ) != null ) {
            return new Results(Results.Types.SUCCESS);
        }

        WItem wbelt = Finder.findDressedItem ( new NAlias ("belt") );
        if(wbelt!=null) {
            NInventory belt = (NInventory)wbelt.item.contents;
            if ( belt == null ) {
                return new Results ( Results.Types.NO_ITEMS );
            }
            GItem item = belt.getItem ( name );
            if(item!=null) {
                item.wdgmsg("transfer", Coord.z, 1);
                /// waiting for the completion of the transfer to the inventory
                NUtils.waitEvent(() -> gui.getInventory().getItem(name) != null, 50);
            }
            if(gui.getInventory().getItem ( name ) != null)
                return new Results(Results.Types.SUCCESS);
        }
        return new Results(Results.Types.FAIL);
    }

    NAlias name;

}
