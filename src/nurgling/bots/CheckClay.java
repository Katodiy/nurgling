package nurgling.bots;

import nurgling.NGameUI;

public class CheckClay extends Bot {
    
    public CheckClay ( NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Check Clay";
        win_sz.y = 100;
        /// Ждем пока килны не потухнут
 
        runActions.add ( new nurgling.bots.actions.CheckClay () );
        
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
