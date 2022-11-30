package nurgling.bots;

import nurgling.NGameUI;

public class CheckWater extends Bot {
    
    public CheckWater ( NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Check Water";
        win_sz.y = 100;
        /// Ждем пока килны не потухнут
 
        runActions.add ( new nurgling.bots.actions.CheckWater () );
        
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
