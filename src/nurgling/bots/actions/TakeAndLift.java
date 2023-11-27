package nurgling.bots.actions;

import haven.Coord;
import haven.GItem;
import haven.WItem;
import haven.Window;

import nurgling.NAlias;
import nurgling.NGItem;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.tools.Finder;

public class TakeAndLift implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        Window spwnd = gui.getWindow ( cap );
        if ( spwnd != null ) {
            /// Находим предмет в контейнере

            GItem item = gui.getInventory ( cap ).getItem ( items );
            
            /// Предметов нет - сообщаем
            if ( item == null ) {
                return new Results ( Results.Types.NO_ITEMS );
            }
            
            /// Поднимаем предмет
            item.wdgmsg ( "take", new Coord ( item.sz.x / 2, item.sz.y / 2 ) );

            NUtils.waitEvent(()->NUtils.isPose(gui.map.player(),new NAlias("banzai")),200);

            return NUtils.isPose(gui.map.player(),new NAlias("banzai")) ? new Results ( Results.Types.SUCCESS ) : new Results (
                    Results.Types.FAIL );
        }
        return new Results ( Results.Types.FAIL );
    }
    
    public TakeAndLift(
            String cap,
            NAlias items
    ) {
        this.cap = cap;
        this.items = items;
    }
    
    String cap;
    NAlias items;
}
