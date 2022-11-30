package nurgling.bots.actions;

import haven.Composite;
import haven.Coord;
import haven.Gob;
import haven.Skeleton;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.tools.Finder;

import static haven.OCache.posres;

public class TakeVehicle implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        if(vehicle==null)
        vehicle = Finder.findObject ( name );
        
        /// Используем тачку
        gui.map.wdgmsg ( "click", Coord.z, vehicle.rc.floor ( posres ), 3, 0, 0, ( int ) vehicle.id,
                vehicle.rc.floor ( posres ), 0, -1 );
        Thread.sleep ( 500 );
        int counter = 0;
        boolean isAnim = gui.map.player ().getattr ( Composite.class ).comp.poses.mods.length > 1;
        Skeleton.PoseMod state = null;
        if ( isAnim ) {
            state = gui.map.player ().getattr ( Composite.class ).comp.poses.mods[1];
        }
        do {
            
            Thread.sleep ( 50 );
            counter++;
            if ( isAnim ) {
                if ( state != ( gui.map.player ().getattr ( Composite.class ).comp.poses.mods[1] ) ) {
                    break;
                }
            }
            else {
                if ( gui.map.player ().getattr ( Composite.class ).comp.poses.mods.length > 1 ) {
                    break;
                }
            }
            
        }
        while ( counter < 20 );
        return new Results ( Results.Types.SUCCESS );
    }
    
    public TakeVehicle(NAlias name ) {
        this.name = name;
    }
    
    public TakeVehicle(Gob vehicle ) {
        this.vehicle = vehicle;
    }
    
    NAlias name;
    Gob vehicle = null;
}
