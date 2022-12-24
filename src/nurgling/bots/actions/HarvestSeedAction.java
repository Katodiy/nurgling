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

public class HarvestSeedAction implements Action {

    
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        NArea input = Finder.findNearestMark ( harvest_area );

        ArrayList<Gob> plants = Finder.findObjectsInArea ( crop, input );
        Gob barrel = Finder.findObjectInArea ( new NAlias("barrel"),2000,input );
        new OpenBarrelAndTransfer ( 9000,  crop, harvest_area, barrel ).run ( gui );
        boolean isFull=false;
        /// Выкапываем урожай
        while ( !Finder.findCropsInArea ( crop, input, isMaxStage ).isEmpty () ) {
            Gob plant = Finder.findCropInArea ( crop, 3000, input, isMaxStage );
            if ( plant != null ) {
                    if ( gui.getInventory ().getFreeSpace () < 2 ) {
                        if(!isFull) {
                            if ( new OpenBarrelAndTransfer ( 9000, crop,
                                    harvest_area,barrel )
                                    .run ( gui ).type == Results.Types.FULL ) {
                                isFull = true;
                            }
                        }
                        if ( !gui.getInventory ().getItems ( crop ).isEmpty () ) {
                            new TransferToTrough ( crop ).run ( gui );
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
        if ( !gui.getInventory ().getItems ( crop ).isEmpty () ) {
            if(!isFull) {
                if ( new OpenBarrelAndTransfer ( 9000, crop ,
                        harvest_area,barrel )
                        .run ( gui ).type == Results.Types.FULL ) {
                }
            }
            else
            {
                new TransferToTrough(crop).run(gui);
            }
        }
        return new Results ( Results.Types.SUCCESS );
    }

    public HarvestSeedAction(NAlias crop, AreasID harvest_area, boolean isMaxStage) {
        this.crop = crop;
        this.harvest_area = harvest_area;
        this.isMaxStage = isMaxStage;
    }

    AreasID harvest_area;
    NAlias crop;

    boolean isMaxStage = false;
}
