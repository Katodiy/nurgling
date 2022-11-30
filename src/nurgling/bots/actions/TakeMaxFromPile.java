package nurgling.bots.actions;


import haven.Gob;

import haven.Window;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

public class TakeMaxFromPile implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        if ( inPile == null ) {
            if ( area == null ) {
                inPile = Finder.findObjectInArea ( new NAlias( "stockpile" ), 1000, Finder.findNearestMark ( input ) );
            }
            else {
                inPile = Finder.findObjectInArea ( new NAlias ( "stockpile" ), 1000, area );
            }
        }
        
        Results res;
        do {
            if ( inPile == null ) {
                return new Results ( Results.Types.SUCCESS );
            }
            new PathFinder( gui, inPile ).run ();
            new OpenTargetContainer ( inPile, "Stockpile" ).run ( gui );
            while ( NUtils.takeItemFromPile () ) {
                ;
            }
            
            if ( Finder.findObject ( inPile.id ) == null ) {
                inPile = Finder.findObjectInArea ( new NAlias ( "stockpile" ), 1000, Finder.findNearestMark ( input ) );
            }
            else {
                Window wpile = gui.getWindow ("Stockpile"  );
                if(wpile!=null)
                {
                    wpile.destroy();
                }
                return new Results ( Results.Types.SUCCESS );
            }
        }
        while ( inPile != null );
        return new Results ( Results.Types.NO_ITEMS );
    }
    
    public TakeMaxFromPile(AreasID input ) {
        this.input = input;
    }
    
    public TakeMaxFromPile(Gob gob ) {
        this.inPile = gob;
    }
    
    public TakeMaxFromPile(NArea area ) {
        this.area = area;
    }
    
    AreasID input;
    Gob inPile = null;
    NArea area = null;
}
