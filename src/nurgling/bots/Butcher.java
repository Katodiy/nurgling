package nurgling.bots;

import nurgling.NGameUI;
import nurgling.bots.actions.Butching;


public class Butcher extends Bot {
    
    public Butcher(NGameUI gameUI ) {
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
