package nurgling.bots.actions;

import haven.WItem;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.tools.Finder;


public class OpenBelt implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        if(gui.getInventory ( "elt",false )==null) {
            WItem item = Finder.findDressedItem(new NAlias("belt"));
            if (item != null) {
                item.item.wdgmsg("iact", item.sz, 0);
                if (!NUtils.waitEvent(() -> gui.getInventory("elt") != null, 100)) {
                    return new Results(Results.Types.BELT_FAIL);
                }
                gui.getInventory("elt").getFreeSpace();
                return new Results(Results.Types.SUCCESS);
            } else {
                return new Results(Results.Types.NO_BELT);
            }
        }else{
            return new Results(Results.Types.SUCCESS);
        }
    }
}
