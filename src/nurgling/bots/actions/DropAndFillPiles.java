package nurgling.bots.actions;

import haven.Gob;
import haven.WItem;

import nurgling.*;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;


public class DropAndFillPiles implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        
        for( Gob in: Finder.findObjectsInArea ( droped,in ) ){
            while(Finder.findObject ( in.id )!=null){
                new PathFinder ( gui, in ).run ();
                new TakeMaxFromPile ( in ).run ( gui );
                new PathFinder( gui, darea.center ).run ();
                ArrayList<WItem> ditems = gui.getInventory (  ).getItems ( droped );
                for ( WItem item : ditems ) {
                    NUtils.drop(item);
                }
                new TransferToPile ( piles, NHitBox.getByName ( droped.keys.get ( 0 ) ),droped,items ).run ( gui );
            }
        }
        
        
        return new Results ( Results.Types.SUCCESS );
    }
    
    public DropAndFillPiles(
            NAlias droped,
            NAlias items,
            NArea piles,
            NArea darea,
            NArea in
    ) {
        this.droped = droped;
        this.items = items;
        this.piles = piles;
        this.darea = darea;
        this.in = in;
    }
    
    NAlias droped;
    NAlias items;
    NArea piles;
    NArea darea;
    NArea in;
}
