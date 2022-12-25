package nurgling.bots;

import haven.Button;
import haven.Coord;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.bots.actions.GlassJugWaterIn;
import nurgling.bots.actions.GlassJugWaterOut;
import nurgling.tools.AreaSelecter;
import nurgling.tools.NArea;

import java.util.concurrent.atomic.AtomicBoolean;


public class JugWaterIn extends Bot {


    public JugWaterIn(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "GlassJugWaterIn";
        win_sz.y = 100;
        
        runActions.add ( new GlassJugWaterIn() );
    }
    

    @Override
    public void endAction () {
        super.endAction ();
    }
}
