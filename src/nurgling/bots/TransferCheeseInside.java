package nurgling.bots;


import nurgling.NGameUI;
import nurgling.bots.actions.CurdTubeAction;
import nurgling.bots.actions.TransferCheeseAction;
import nurgling.tools.AreasID;


public class TransferCheeseInside extends Bot {

    public TransferCheeseInside(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "TransferCheeseInside";
        win_sz.y = 100;
        
        runActions.add ( new TransferCheeseAction(AreasID.c_inside));
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
