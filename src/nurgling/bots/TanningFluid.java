package nurgling.bots;

import nurgling.NGameUI;
import nurgling.bots.actions.TanningFluidMake;

public class TanningFluid extends Bot {

    public TanningFluid(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Tanning Fluid";
        win_sz.y = 100;
        /// Ждем пока килны не потухнут
 
        runActions.add ( new TanningFluidMake() );
        
    }
    
    
    @Override
    public void initAction ()
            throws InterruptedException { super.initAction();
    }
    
    @Override
    public void endAction () {
        super.endAction ();
    }

}
