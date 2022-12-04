package nurgling.bots;


import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.bots.actions.CollectAndReturn;

import java.util.ArrayList;
import java.util.Arrays;

public class CollectClay extends Bot {
    
    public CollectClay(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Check Clay";
        win_sz.y = 100;
        runActions.add ( new CollectAndReturn(new NAlias(new ArrayList<>(Arrays.asList("clay") ),new ArrayList<>(Arrays.asList("stockpile") ) )));
        
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
