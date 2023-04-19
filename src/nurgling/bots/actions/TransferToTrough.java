package nurgling.bots.actions;

import haven.GItem;
import haven.Gob;

import nurgling.*;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

import java.util.ArrayList;
import java.util.Arrays;

public class TransferToTrough implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        
        while ( !gui.getInventory ().getWItems( items ).isEmpty () ) {
            
            Gob gob = Finder
                    .findObjectInArea ( new NAlias( "trough" ), 2000, Finder.findNearestMark ( AreasID.swill) );
            
            
            new PathFinder( gui, gob ).run ();
            for ( GItem item : gui.getInventory ().getWItems( items ) ) {
                do {
                    if ( gob.getModelAttribute() != 7 ) {
                        if ( gui.hand.isEmpty () ) {
                            new TakeToHand ( item ).run ( gui );
                        }
                        int counter = 0;
                        while ( !gui.hand.isEmpty () && counter < 20 ) {
                            NUtils.activateItem ( gob );
                            Thread.sleep ( 50 );
                            counter++;
                        }
                    }
                    else {
                        if(!gui.hand.isEmpty ())
                            NUtils.transferToInventory ();
                        NUtils.waitEvent ( ()->gui.hand.isEmpty (),60 );
                        new LiftObject ( gob ).run ( gui );
                        Gob cistern = Finder.findObjectInArea ( new NAlias ( new ArrayList<> ( Arrays
                                        .asList ( "cistern")) ),
                                1000,
                                Finder.findNearestMark ( AreasID.swill) );
                        PathFinder pf = new PathFinder ( gui, cistern );
                        pf.ignoreGob ( gob );
                        pf.run ();
                        NUtils.activate ( cistern );
                        int counter = 0;
                        while ( Finder.findObject ( gob.id ).getModelAttribute() == 7  &&
                                counter < 20) {
                            counter++;
                            Thread.sleep ( 50 );
                        }
                        new PlaceLifted ( AreasID.swill, gob.getHitBox(),new NAlias ("trough") ).run ( gui );
                    }
                }
                while ( !gui.hand.isEmpty () );
            }
        }
        return new Results ( Results.Types.SUCCESS );
    }
    
    public TransferToTrough(
            NAlias items
    ) {
        this.items = items;
    }
    
    NAlias items;
}
