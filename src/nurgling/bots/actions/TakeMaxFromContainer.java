package nurgling.bots.actions;

import haven.WItem;
import haven.Window;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;

import java.util.ArrayList;

public class TakeMaxFromContainer implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        Window spwnd = gui.getWindow ( cap );
        if ( gui.getInventory ().getFreeSpace () == 0 ) {
            return new Results ( Results.Types.FULL );
        }
        if ( spwnd != null ) {
            /// Находим все необходимые предметы в контейнере
            if(!NUtils.checkName(cap,"Stockpile")) {
                ArrayList<WItem> items;
                if (qMode) {
                    items = gui.getInventory(cap).getItems(names, q, isMore);
                } else {
                    items = gui.getInventory(cap).getItems(names);
                }

                /// Переносим предметы в инвентарь
                for (WItem item : items) {
                    if (!NUtils.transferItem(gui.getInventory(cap), item, gui.getInventory())) {
                        return new Results(Results.Types.FULL);
                    }

                }
            }else{
                while (NUtils.takeItemFromPile());
            }
            spwnd.destroy();
            NUtils.waitEvent(()->gui.getWindow ( cap )==null,50);
            return new Results ( Results.Types.SUCCESS );
        }
        return new Results ( Results.Types.FAIL );
    }
    
    public TakeMaxFromContainer(
            String cap,
            NAlias names
    ) {
        this.cap = cap;
        this.names = names;
    }
    
    public TakeMaxFromContainer(
            String cap,
            NAlias names,
            double q,
            boolean isMore
    ) {
        this.cap = cap;
        this.names = names;
        this.isMore = isMore;
        this.q = q;
        qMode = true;
    }
    
    String cap;
    NAlias names;
    double q;
    boolean qMode = false;
    boolean isMore = false;
}
