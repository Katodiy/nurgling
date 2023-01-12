package nurgling.bots.actions;

import haven.Coord;
import haven.Coord2d;
import haven.MCache;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.tools.NArea;

import static haven.OCache.posres;

public class ClearSnowInArea implements Action {
    
    
    public enum Type {
        Center, Back
    }
    
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        Coord2d pos = new Coord2d ( area.begin.x + MCache.tilesz.x / 2, area.begin.y + MCache.tilesz.x / 2 );
        
        if( NUtils.getStamina() <= 0.3) {
            new Drink ( 0.9, false ).run ( gui );
        }
        new PathFinder( gui,pos ).run ();
        Thread.sleep ( 200 );


        NUtils.digSnow ( pos );
        while ( pos.x <= area.end.x ) {
            while ( pos.y < area.end.y - MCache.tilesz.x / 2 ) {
                pos.y += MCache.tilesz.y;
                NUtils.digSnow ( pos );
                if( NUtils.getStamina() <= 0.3) {
                    new Drink ( 0.9, false ).run ( gui );
                }
            }
            pos.x += MCache.tilesz.x;
            if ( pos.x < area.end.x ) {
                NUtils.digSnow ( pos );
                if( NUtils.getStamina() <= 0.3) {
                    new Drink ( 0.9, false ).run ( gui );
                }
            }
            else {
                break;
            }
            while ( pos.y > area.begin.y + MCache.tilesz.y / 2 ) {
                pos.y -= MCache.tilesz.y;
                NUtils.digSnow ( pos );
                if( NUtils.getStamina() <= 0.3) {
                    new Drink ( 0.9, false ).run ( gui );
                }
            }
            pos.x += MCache.tilesz.x;
            if ( pos.x < area.end.x ) {
                gui.map.wdgmsg ( "click", Coord.z, pos.floor ( posres ), 1, 0 );
                NUtils.digSnow ( pos );
                if( NUtils.getStamina() <= 0.3) {
                    new Drink ( 0.9, false ).run ( gui );
                }
            }
            else {
                break;
            }
        }
        NUtils.stopWithClick ();
        return new Results ( Results.Types.SUCCESS );
    }
    
    public ClearSnowInArea(
            NArea area
    ) {
        this.area = area;
    }
    
    NAlias name;
    NArea area;
}
