package nurgling.bots.actions;

import haven.*;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NHitBox;
import nurgling.NUtils;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import static haven.OCache.posres;

public class PatrolArea implements Action {
    
    
    public enum Type {
        Center, Back
    }
    
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        Gob vehicle =  Finder.findObject ( name );
        if ( new LiftObject ( vehicle ).run ( gui ).type != Results.Types.SUCCESS ) {
            return new Results ( Results.Types.NO_WORKSTATION );
        }
        
        Coord2d pos = new Coord2d ( area.begin.x + MCache.tilesz.x / 2, area.begin.y + MCache.tilesz.x / 2 );

        new PlaceLifted ( pos , NHitBox.getByName ( "plow" ), new NAlias(  "plow" ) ).run ( gui );

        if( NUtils.getStamina() <= 0.3) {
            if(new Drink ( 0.9, false ).run ( gui ).type!= Results.Types.SUCCESS)
                return new Results ( Results.Types.Drink_FAIL );
        }


        NUtils.stopWithClick ();
        new TakeVehicle ( vehicle ).run ( gui );
        gui.map.wdgmsg ( "click", Coord.z, pos.floor ( posres ), 1, 0 );
        while ( Math.sqrt ( Math.pow ( vehicle.rc.x - pos.x, 2 ) +
                Math.pow ( vehicle.rc.y - pos.y, 2 ) ) > 1 ) {
            Thread.sleep ( 160 );
        }
        while ( pos.x <= area.end.x ) {
            while ( pos.y < area.end.y - MCache.tilesz.x / 2 ) {
                pos.y += MCache.tilesz.y;
                gui.map.wdgmsg ( "click", Coord.z, pos.floor ( posres ), 1, 0 );
                while ( Math.sqrt ( Math.pow ( vehicle.rc.x - pos.x, 2 ) +
                        Math.pow ( vehicle.rc.y - pos.y, 2 ) ) > 1 ) {
                    Thread.sleep ( 160 );
                }
                if( NUtils.getStamina() <= 0.3) {
                    if(new Drink ( 0.9, false ).run ( gui ).type!= Results.Types.SUCCESS)
                        return new Results ( Results.Types.Drink_FAIL );
                    new TakeVehicle ( vehicle ).run ( gui );
                }
            }
            pos.x += MCache.tilesz.x;
            if ( pos.x < area.end.x ) {
                gui.map.wdgmsg ( "click", Coord.z, pos.floor ( posres ), 1, 0 );
                while ( Math.sqrt ( Math.pow ( vehicle.rc.x - pos.x, 2 ) +
                        Math.pow ( vehicle.rc.y - pos.y, 2 ) ) > 1 ) {
                    Thread.sleep ( 160 );
                }
                if( NUtils.getStamina() <= 0.3) {
                    if(new Drink ( 0.9, false ).run ( gui ).type!= Results.Types.SUCCESS)
                        return new Results ( Results.Types.Drink_FAIL );
                    new TakeVehicle ( vehicle ).run ( gui );
                }
            }
            else {
                break;
            }
            while ( pos.y > area.begin.y + MCache.tilesz.y / 2 ) {
                pos.y -= MCache.tilesz.y;
                gui.map.wdgmsg ( "click", Coord.z, pos.floor ( posres ), 1, 0 );
                while ( Math.sqrt ( Math.pow ( vehicle.rc.x - pos.x, 2 ) +
                        Math.pow ( vehicle.rc.y - pos.y, 2 ) ) > 1 ) {
                    Thread.sleep ( 160 );
                }
                if( NUtils.getStamina() <= 0.3) {
                    if(new Drink ( 0.9, false ).run ( gui ).type!= Results.Types.SUCCESS)
                        return new Results ( Results.Types.Drink_FAIL );
                    new TakeVehicle ( vehicle ).run ( gui );
                }
            }
            pos.x += MCache.tilesz.x;
            if ( pos.x < area.end.x ) {
                gui.map.wdgmsg ( "click", Coord.z, pos.floor ( posres ), 1, 0 );
                while ( Math.sqrt ( Math.pow ( vehicle.rc.x - pos.x, 2 ) +
                        Math.pow ( vehicle.rc.y - pos.y, 2 ) ) > 1 ) {
                    Thread.sleep ( 160 );
                }
                if( NUtils.getStamina() <= 0.3) {
                    if(new Drink ( 0.9, false ).run ( gui ).type!= Results.Types.SUCCESS)
                        return new Results ( Results.Types.Drink_FAIL );
                    new TakeVehicle ( vehicle ).run ( gui );
                }
            }
            else {
                break;
            }
        }

        return new Results ( Results.Types.SUCCESS );
    }
    
    public PatrolArea(
            NAlias name,
            NArea area
    ) {
        this.name = name;
        this.area = area;
    }
    
    NAlias name;
    NArea area;
}
