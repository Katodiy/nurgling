package nurgling.bots.actions;

import haven.Gob;

import nurgling.*;
import nurgling.tools.Finder;

import java.util.ArrayList;

public class CollectAllInArea implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        
        ArrayList<Gob> candidates = Finder.findAllinDistance ( src, 1000 );
        if ( candidates.isEmpty () ) {
            return new Results ( Results.Types.SUCCESS );
        }
        for ( Gob gob : candidates ) {
            if ( gui.getInventory ().getFreeSpace () < threshold ) {
                new TransferItemsToContainers (cont,items ).run ( gui );
            }
            
            if ( gui.getInventory ().getFreeSpace () > threshold ) {
                while ( NUtils.checkGobFlower ( action, gob, 0 )) {
                    new PathFinder( gui, gob ).run ();
                    NFlowerMenu.instance.selectInCurrent ( action );
                    NUtils.waitEvent(()->NUtils.getProg()>=0,20);
                    NUtils.waitEvent(()->NUtils.getProg()<0,500);
                }
            }
            else {
                return new Results ( Results.Types.NO_FREE_SPACE );
            }
        }
        new TransferItemsToContainers (  cont, items ).run ( gui );
        return new Results ( Results.Types.SUCCESS );
    }
    
    public CollectAllInArea(
            NAlias cont,
            String cap,
            NAlias src,
            NAlias items,
            String action,
            int threshold
    ) {
        this.cont = cont;
        this.cap = cap;
        this.src = src;
        this.items = items;
        this.action = action;
        this.threshold = threshold;
    }
    

    int threshold;
    
    NAlias src;
    NAlias items;
    NAlias cont;
    String cap;
    
    String action;
}
