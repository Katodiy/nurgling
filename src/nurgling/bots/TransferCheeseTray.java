package nurgling.bots;


import nurgling.NGameUI;
import nurgling.bots.actions.CurdTubeAction;
import nurgling.bots.actions.TransferTrayAction;
import nurgling.tools.AreasID;


public class TransferCheeseTray extends Bot {

    public TransferCheeseTray(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "TransferTray";
        win_sz.y = 100;
        
        runActions.add ( new TransferTrayAction() );
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
