package nurgling.bots;

import nurgling.NGameUI;
import nurgling.bots.actions.PleaseLeaveMeAlone;

public class PleaseLeaveMe extends Bot {

    public PleaseLeaveMe(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Check Clay";
        win_sz.y = 100;
        /// Ждем пока килны не потухнут
 
        runActions.add ( new PleaseLeaveMeAlone() );
        
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
