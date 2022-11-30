package nurgling.bots.actions;

import haven.Gob;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.tools.AreasID;

import java.util.ArrayList;

public class LightCandelabrun implements Action {
    
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        if ( ( candelabrum.getModelAttribute() ) == 0 ) {
            new TakeFromContainers ( null, new NAlias ( "candle" ), 1, AreasID.candle, "" ).run ( gui );
            new PathFinder( gui, candelabrum ).run ();
            new TakeToHand ( new NAlias( "candle" ) ).run ( gui );
            NUtils.activateItem ( candelabrum );
            int counter = 0;
            while ( ( candelabrum.getModelAttribute() ) == 0 && counter < 20 ) {
                Thread.sleep ( 50 );
                counter++;
            }
            if ( ( candelabrum.getModelAttribute() ) == 0 ) {
                return new Results ( Results.Types.FAIL );
            }
        }
        new LightFire ( candelabrum ).run ( gui );
        
        return new Results ( Results.Types.SUCCESS );
    }
    
    public LightCandelabrun (
            Gob gob
    ) {
        this.candelabrum = gob;
    }
    
    Gob candelabrum;
}
