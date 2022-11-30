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
import java.util.Arrays;

public class HarvestCarrotAction implements Action {

    
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        NArea input = Finder.findNearestMark ( AreasID.carrot );

        ArrayList<Gob> plants = Finder.findObjectsInArea ( new NAlias ("carrot"), input );
        Gob barrel = Finder.findObjectInArea ( new NAlias("barrel"),2000,input );
        new OpenBarrelAndTransfer ( 10000,  new NAlias ("carrot"), harvest_area, barrel ).run ( gui );
        boolean isFull=false;
        /// Выкапываем урожай
        while ( !Finder.findCropsInArea ( new NAlias ("carrot"), input, false ).isEmpty () ) {
            Gob plant = Finder.findCropInArea ( new NAlias ("carrot"), 3000, input, false );
            if ( plant != null ) {
                    if ( gui.getInventory ().getFreeSpace () < 2 ) {
                        if(!isFull) {
                            if ( new OpenBarrelAndTransfer ( 5000, new NAlias ( new ArrayList<> (Arrays.asList (
                                    "carrot" , "Carrot"))),
                                    AreasID.carrot,barrel )
                                    .run ( gui ).type == Results.Types.FULL ) {
                                isFull = true;
                            }
                        }
                        if ( !gui.getInventory ().getItems ( new NAlias ( "carrot" ) ).isEmpty () ) {
                            new TransferToTrough ( new NAlias ( "carrot" ) ).run ( gui );
                        }
                    }
                    if ( NUtils.getStamina() <= 0.3 ) {
                        new Drink ( 0.9, false ).run ( gui );
                    }
        
                    new PathFinder( gui, plant ).run ();
                    int size = gui.getInventory ().getFreeSpace ();
                    new SelectFlowerAction ( plant, "Harvest", SelectFlowerAction.Types.Gob ).run ( gui );
                    NUtils.waitEvent ( () -> NUtils.getProg() < 0 && size != gui.getInventory ().getFreeSpace (), 60 );
                }
            
        }
        if ( !gui.getInventory ().getItems ( new NAlias ( "carrot" ) ).isEmpty () ) {
            new TransferToTrough ( new NAlias ( "carrot" ) ).run ( gui );
        }
        return new Results ( Results.Types.SUCCESS );
    }
    
    AreasID harvest_area;
    NAlias crop;
}
