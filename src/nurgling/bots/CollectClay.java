package nurgling.bots;


import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.bots.actions.CollectAndReturn;

public class CollectClay extends Bot {
    
    public CollectClay(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Check Clay";
        win_sz.y = 100;
        runActions.add ( new CollectAndReturn(new NAlias("clay") ) );
        
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
