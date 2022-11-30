package nurgling.bots;

import nurgling.NGameUI;
import nurgling.bots.actions.Butching;


public class TruffleHunter extends Bot {

    public TruffleHunter(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Butcher";
        win_sz.y = 100;
 
        
        runActions.add ( new Butching() );
        
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
