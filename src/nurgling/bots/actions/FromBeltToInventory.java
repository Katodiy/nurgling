package nurgling.bots.actions;

import haven.Coord;
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
            wbelt.item.wdgmsg ( "iact", wbelt.sz, 0 );
            NUtils.waitEvent ( ()->gui.getWindow ( "elt" )!=null,300 );

            int count = 0;
            while(count < 5) {
                NInventory belt = gui.getInventory ( "elt" );
                if ( belt == null ) {
                    return new Results ( Results.Types.NO_ITEMS );
                }
                WItem item = belt.getItem ( name );
                if(item!=null) {
                    NUtils.getGameUI().setfocus(NUtils.getGameUI().getInventory());
                    item.item.wdgmsg("transfer", Coord.z, 1);
                    /// waiting for the completion of the transfer to the inventory
                    NUtils.waitEvent(() -> gui.getInventory().getItem(name) != null, 50);
                    NUtils.getGameUI().getInventory().lostfocus();
                    if (gui.getInventory().getItem(name) != null) {
                        break;
                    }
                    count++;
                }else {
                    break;
                }
            }
            gui.getWindow ( "elt" ).cbtn.wdgmsg ( "activate" );
            NUtils.waitEvent(() -> gui.getWindow("elt") == null, 300);
            if(gui.getInventory().getItem ( name ) != null)
                return new Results(Results.Types.SUCCESS);
        }
        return new Results(Results.Types.FAIL);
    }

    NAlias name;

}
