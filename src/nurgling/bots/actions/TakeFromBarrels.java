package nurgling.bots.actions;

import haven.Coord;
import haven.Gob;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;
import java.util.Objects;

import static haven.OCache.posres;

public class TakeFromBarrels implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        
        ArrayList<Gob> gobs = Finder
                .findObjectsInArea ( new NAlias( "barrel" ), ( area == null ) ? Finder.findNearestMark ( id ) : area );
        int need = size;
        for ( Gob gob : gobs ) {
            if ( NUtils.isOverlay ( Objects.requireNonNull ( Finder.findObject ( gob.id ) ), items ) ) {
                new PathFinder( gui, gob ).run ();
                while ( need > 0 ) {
                    int current = gui.getInventory ().getItems ( items ).size ();
                    gui.map.wdgmsg ( "click", Coord.z, gob.rc.floor ( posres ), 3, 1, 0, ( int ) gob.id,
                            gob.rc.floor ( posres ), 0, -1 );
                    int counter = 0;
                    while ( counter < 20 && current == gui.getInventory ().getItems ( items ).size () ) {
                        Thread.sleep ( 200 );
                        counter++;
                    }
                    int newcount = gui.getInventory ().getItems ( items ).size ();
                    if ( current != newcount ) {
                        need = size - newcount;
                    }
                    if ( need <= 0 ||
                            !NUtils.isOverlay ( Objects.requireNonNull ( Finder.findObject ( gob.id ) ), items ) ) {
                        break;
                    }
                }
                if ( need <= 0 ) {
                    return new Results ( Results.Types.SUCCESS );
                }
            }
        }
        return new Results ( Results.Types.NO_ITEMS );
    }
    
    public TakeFromBarrels(
            AreasID id,
            int count,
            NAlias items
    ) {
        this.id = id;
        this.size = count;
        this.items = items;
    }
    
    public TakeFromBarrels(
            NArea area,
            int count,
            NAlias items
    ) {
        this.area = area;
        this.size = count;
        this.items = items;
    }

    AreasID id;
    NArea area = null;
    int size;
    
    NAlias items;
}
