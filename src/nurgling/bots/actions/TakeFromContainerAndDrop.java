package nurgling.bots.actions;

import haven.GItem;
import haven.Window;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;

import java.util.ArrayList;

public class TakeFromContainerAndDrop implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        
        Window spwnd = gui.getWindow ( cap );
        if ( gui.getInventory ().getFreeSpace () == 0 ) {
            return new Results ( Results.Types.FULL );
        }
        if ( spwnd != null ) {
            /// Находим все необходимые предметы в контейнере
            ArrayList<GItem> items = gui.getInventory ( cap ).getWItems( names );
            /// Переносим предметы в инвентарь
            
            for ( GItem item : items ) {
                NUtils.dropFrom(item,cap);
            }
            return new Results ( Results.Types.SUCCESS );
        }
        return new Results ( Results.Types.FAIL );
    }
    
    public TakeFromContainerAndDrop(
            String cap,
            NAlias names
    ) {
        this.cap = cap;
        this.names = names;
    }
    
    String cap;
    NAlias names;
}
