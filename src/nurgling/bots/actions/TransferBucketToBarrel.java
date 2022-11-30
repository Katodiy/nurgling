package nurgling.bots.actions;

import haven.Gob;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;

public class TransferBucketToBarrel implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        if ( !NUtils.isIt ( gui.vhand, new NAlias( "bucket" ) ) ) {
            return new Results ( Results.Types.NO_CONTAINER );
        }
            ArrayList<Gob> gobs = Finder.findObjectsInArea ( new NAlias ( "barrel" ),
                    ( area == null ) ? Finder.findNearestMark ( id ) : area );
            Gob gob = null;
            for ( Gob candidate : gobs ) {
                if ( !full.contains ( gob ) ) {
                    if ( NUtils.isOverlay ( candidate, items ) ) {
                        gob = candidate;
                        break;
                    }
                }
            }
            if ( gob == null ) {
                for ( Gob candidate : gobs ) {
                    if ( !full.contains ( gob ) ) {
                        if ( !NUtils.isOverlay ( candidate ) ) {
                            gob = candidate;
                            break;
                        }
                    }
                }
            }
            if ( gob == null ) {
                return new Results ( Results.Types.NO_CONTAINER );
            }
            
            new PathFinder( gui, gob ).run ();
            NUtils.activateItem ( gob );
            int counter = 0;
            while ( NUtils.checkName ( NUtils.getContent ( gui.vhand ), items ) &&
                    counter < 20 ) {
                
                Thread.sleep ( 50 );
                counter++;
            }
            
        return new Results ( Results.Types.SUCCESS );
    }
    
    public TransferBucketToBarrel(
            AreasID id,
            NAlias items
    ) {
        this.id = id;
        this.items = items;
    }


    AreasID id;
    NArea area = null;
    int size;
    ArrayList<Gob> full = new ArrayList<> ();
    NAlias items;
}
