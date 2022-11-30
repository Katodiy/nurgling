package nurgling.bots.actions;

import haven.WItem;
import haven.Window;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;

import java.util.ArrayList;

public class TakeFromContainer implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        Window spwnd = gui.getWindow ( cap );
        if ( gui.getInventory ().getFreeSpace () == 0 ) {
            return new Results ( Results.Types.FULL );
        }
        if ( spwnd != null ) {
            if ( cap.equals ( "Stockpile" ) ) {
                while ( count > 0 ) {
                    if ( !NUtils.takeItemFromPile () ) {
                        return new Results ( Results.Types.NO_ITEMS );
                    }
                    else {
                        count--;
                    }
                }
            }
            else {
                /// Находим все необходимые предметы в контейнере
                ArrayList<WItem> items = gui.getInventory ( cap ).getItems ( names );
                if(items.isEmpty ())
                    items = gui.getInventory ( cap ).getItemsWithInfo ( names );
                /// Переносим предметы в инвентарь
                
                for ( WItem item : items ) {
                    if ( !NUtils.transferItem ( gui.getInventory ( cap ), item, gui.getInventory() ) ) {
                        return new Results ( Results.Types.FULL );
                    }
                    count--;
                    if ( count == 0 ) {
                        return new Results ( Results.Types.SUCCESS );
                    }
                }
            }
            return new Results ( Results.Types.SUCCESS );
        }
        return new Results ( Results.Types.FAIL );
    }
    
    public TakeFromContainer(
            String cap,
            NAlias names,
            int count
    ) {
        this.count = count;
        this.cap = cap;
        this.names = names;
    }
    
    int count;
    String cap;
    NAlias names;
}
