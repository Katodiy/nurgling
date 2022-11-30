package nurgling.bots;

import nurgling.NGameUI;
import nurgling.bots.actions.FeedCloverAction;

public class FeedClover extends Bot {

    public FeedClover(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "FeedClover";
        win_sz.y = 100;
        /// Ждем пока килны не потухнут
 
        runActions.add ( new FeedCloverAction() );
        
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
