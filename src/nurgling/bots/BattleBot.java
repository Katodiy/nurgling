package nurgling.bots;

import nurgling.NGameUI;
import nurgling.bots.actions.BattleAction;
import nurgling.bots.actions.ReagrAction;

public class BattleBot extends Bot {

    public BattleBot(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "BattleAction";
        win_sz.y = 100;

        runActions.add ( new ReagrAction() );
        
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
