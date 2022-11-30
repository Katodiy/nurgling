package nurgling.bots.actions;

import haven.Gob;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.Objects;

public class LightFire implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {

        
        if ( candelabrum != null ) {

            PathFinder pathFinder = new PathFinder ( gui, fired_gob );
            pathFinder.ignoreGob ( candelabrum );
            pathFinder.setHardMode ( true );
            pathFinder.run ();
            NUtils.activate ( fired_gob );
            int counter = 0;
            while ( (  NUtils.getProg() >= 0 || counter < 20) ) {
                counter++;
                Thread.sleep ( 100 );
            }
            Thread.sleep ( 200 );
            
        }
        else {
            NArea pile_area = Finder.findNearestMark ( AreasID.branch );
            if ( !pile_area.check () ) {
                return new Results ( Results.Types.FAIL );
            }
            long state =  fired_gob.getModelAttribute();
            do {
                Gob gob = Finder
                        .findObjectInArea ( new NAlias( "branch" ), 1000, Finder.findNearestMark ( AreasID.branch ) );
                if ( gob != null ) {
                    PathFinder pf = new PathFinder ( gui,gob );
                    pf.setHardMode(true);
                    pf.run();
                    new OpenTargetContainer ( gob, "Stockpile" ).run ( gui );
                    /// Берем две веточки для поджигания
                    if ( new TakeFromContainer ( "Stockpile", new NAlias ( "branch" ), 2 ).run ( gui ).type ==
                            Results.Types.FULL ) {
                        return new Results ( Results.Types.NO_FREE_SPACE );
                    }
                }
                /// Идем к зажигаемому объекту
                PathFinder pf = new PathFinder ( gui, fired_gob );
                pf.run ();
                /// Пьем
                if ( new Drink ( 0.9, false ).run ( gui ).type != Results.Types.SUCCESS ) {
                    return new Results ( Results.Types.Drink_FAIL );
                }
                NUtils.craft ( new char[]{ 'a', 'g' }, "Light fire", true );
                NUtils.waitEvent(()->!gui.hand.isEmpty(),20000,10);
//                int counter = 0;
//                while ( gui.hand.isEmpty () && counter < 50) {
//                    Thread.sleep ( 50 );
//                    counter++;
//                }
                if(!gui.hand.isEmpty ()) {
                    NUtils.activateItem ( fired_gob );
                    NUtils.waitEvent(()->NUtils.getProg() >= 0 ,50);
                    NUtils.waitEvent(()->NUtils.getProg() <0 ,500);
//                    counter = 0;
//                    /// Ждем завершения розжига
//                    while ( NUtils.getProg() >= 0 || counter < 50 ) {
//                        Thread.sleep ( 50 );
//                        counter++;
//                    }
                    if ( !gui.hand.isEmpty () ) {
                        NUtils.drop ();
                    }
                    /// Задержка на обновление состояния
                    Thread.sleep ( 500 );
                }
            }
            while (  Finder.findObject ( fired_gob.id ).getModelAttribute() ==
                    state );
        }
        return new
                
                Results ( Results.Types.SUCCESS );
        
    }
    
    public LightFire ( Gob fired_gob ) {
        this.fired_gob = fired_gob;
    }
    
    public LightFire ( Gob fired_gob , Gob candelabrum) {
        this.fired_gob = fired_gob;
        this.candelabrum = candelabrum;
    }
    
    Gob fired_gob;
    Gob candelabrum;
}
