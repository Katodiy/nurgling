package nurgling.bots.actions;

import haven.Coord;
import haven.Gob;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.tools.Finder;

import static haven.OCache.posres;

public class Destroy implements Action {
    public Destroy(NAlias name ) {
        this.name = name;
    }
    
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        if ( gob == null ) {
            gob = Finder.findObject ( name );
        }
        gui.ui.rcvr.rcvmsg ( gui.getMenuGridId (), "act", "destroy" );
        Thread.sleep ( 100 );
        gui.map.wdgmsg ( "click", Coord.z, gob.rc.floor ( posres ), 1, 0, 0, ( int ) gob.id, gob.rc.floor ( posres ), 0,
                -1 );
        NUtils.stopWithClick();
        NUtils.waitEvent(NUtils::isIdleCurs,200);
        return new Results ( Results.Types.SUCCESS );
    }
    
    public Destroy(Gob gob ) {
        this.gob = gob;
    }
    
    NAlias name;
    Gob gob;
}
