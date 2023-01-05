package nurgling.bots;


import nurgling.NGameUI;
import nurgling.bots.actions.BackerAction;
import nurgling.bots.actions.CurdTubeAction;
import nurgling.tools.AreasID;


public class CurdCows extends Bot {

    public CurdCows(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Cows Curd";
        win_sz.y = 100;
        
        runActions.add ( new CurdTubeAction(AreasID.cows) );
    }
    
    
    @Override
    public void initAction ()
            throws InterruptedException {
        super.initAction ();
    }
    
    @Override
    public void endAction () {
        super.endAction ();
    }
}
