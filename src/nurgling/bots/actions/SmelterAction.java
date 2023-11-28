package nurgling.bots.actions;

import haven.Coord;
import haven.Gob;
import nurgling.*;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

import java.util.ArrayList;
import java.util.Arrays;

import static haven.OCache.posres;

/// TODO флаг 16 для мехов
public class SmelterAction implements Action {
    static NAlias ores = new NAlias ( new ArrayList<> (
            Arrays.asList ( "cassiterite", "hematite", "peacockore", "chalcopyrite", "malachite", "leadglance",
                    "cinnabar", "galena", "ilmenite", "hornsilver", "argentite", "sylvanite" , "magnetite", "nagyagite", "petzite", "cuprite","limonite") ) );
    private final NAlias smelter_name = new NAlias ( new ArrayList<> ( Arrays.asList ( "terobjs/primsmelter","terobjs/smelter" ) ),
            new ArrayList<> () );

    boolean isPrim = false;
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        if(Finder.findObjectsInArea ( smelter_name, Finder.findNearestMark(AreasID.smelter) ).isEmpty())
            return new Results(Results.Types.NO_WORKSTATION);
        while ( !Finder.findObjectsInArea ( new NAlias ( "ore" ), Finder.findNearestMark ( AreasID.ore ) )
                       .isEmpty () ) {

            while(true)
            {
                boolean needBreak = false;
                for ( Gob gob : Finder.findObjectsInArea ( smelter_name, Finder.findNearestMark(AreasID.smelter) ) ) {
                    if (NUtils.isIt(gob, "primsmelter")) {
                        isPrim = true;
                        if (NConfiguration.getInstance().smelterbellows && (gob.getModelAttribute() & 16) == 0 && (gob.getModelAttribute() & 2) != 0) {
                            new PathFinder(gui, gob).run();
                            new Drink(0.9, false).run(gui);
                            gui.map.wdgmsg("click", Coord.z, gob.rc.floor(posres), 3, 0, 0, (int) gob.id,
                                    gob.rc.floor(posres), 0, 16);
                            NUtils.waitEvent(() -> NUtils.isPose(NUtils.getGameUI().map.player(), new NAlias("primsmelter")), 500);
                            NUtils.waitEvent(() -> NUtils.isPose(NUtils.getGameUI().map.player(), new NAlias("idle")), 5000);
                        }
                        if ((gob.getModelAttribute() & 1) == 0) {
                            needBreak = true;
                        }
                    } else {
                        if ((gob.getModelAttribute() & 2) == 0) {
                            needBreak = true;
                        }
                    }
                }
                if(!needBreak)
                    break;
                Thread.sleep(1000);
            }
            /// Wait until the smelters go out


            /// We pick up the ingots and transfer them to the chests
            Results.Types res;
            do {
                res = new TakeMaxFromContainers (
                        new NAlias( new ArrayList<> ( Arrays.asList ( "bar", "nugget", "pebble-gold" ) ),
                                new ArrayList<> ( Arrays.asList ( "cinna" ) ) ),
                        AreasID.smelter, new ArrayList<>() ).run (
                        gui ).type;
                new TransferBars ().run ( gui );
            }
            while ( res != Results.Types.FAIL );
            new TransferBars ().run ( gui );

            /// We throw out slag
            if ( Finder.findNearestMark ( AreasID.slag ) == null ) {
                new ClearContainers ( smelter_name, isPrim?"Furnace":"Smelter", new NAlias ( "slag" ), AreasID.smelter ).run ( gui );
            }
            else {
                new TransferToPileFromContainer ( smelter_name, new NAlias ( "stockpile-stone" ), new NAlias ( "slag" ),
                        AreasID.slag, AreasID.smelter, isPrim?"Furnace":"Smelter" ).run ( gui );
            }
            //Filling Smelters with Stockpiles
            new FillContainers(ores, AreasID.smelter, new ArrayList<>(), new TakeMaxFromContainers(ores,AreasID.ore,new ArrayList<>())).run(gui);
            new TransferToPile(AreasID.ore, NHitBox.getByName("stockpile"), new NAlias("stockpile"), ores).run(gui);

            //Fill the smelter with fuel from the piles
            new FillFuelSmelter ( smelter_name ).run ( gui );
            /// Light fire
            new LightGob ( new NAlias ( "smelter" ), AreasID.smelter, 2 ).run ( gui );
        }
        return new Results ( Results.Types.SUCCESS );
    }
}
