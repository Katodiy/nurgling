package nurgling.bots.actions;

import haven.Coord;
import haven.Gob;
import haven.Window;
import nurgling.NGameUI;
import nurgling.NUtils;

import static haven.OCache.posres;

public class OpenTargetContainer implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        Window wnd;
        if ( gob != null ) {
            gui.map.wdgmsg ( "click", Coord.z, gob.rc.floor ( posres ), 3, 0, 0, ( int ) gob.id,
                    gob.rc.floor ( posres ), 0, -1 );
            NUtils.waitEvent ( ()->gui.getWindow ( cap )!=null,300 );
            if(cap.contains("Stockpile"))
            {
                NUtils.waitEvent (gui::isStockpile,500 );
            }
            else if(cap.contains("Barter Stand"))
            {
                NUtils.waitEvent ( gui::isBarter,500 );
            }
            else if(cap.contains("Barrel"))
            {
                NUtils.waitEvent ( gui::isBarrel,500 );
            }
            else
            {
                if(!NUtils.isIt(gob, "consobj"))
                    NUtils.waitEvent  ( ()->gui.getInventory ( cap )!=null,500 );
            }
            wnd = gui.getWindow ( cap );
            if ( wnd == null ) {
                return new Results ( Results.Types.OPEN_FAIL );
            }
            else {
                /// Задержка на подгрузку данных
                Thread.sleep ( 300 );
                return new Results ( Results.Types.SUCCESS );
            }
        }
        return new Results ( Results.Types.FAIL );
    }
    
    public OpenTargetContainer(
            Gob gob,
            String cap
    ) {
        this.gob = gob;
        this.cap = cap;
    }
    
    Gob gob;
    String cap;
}
