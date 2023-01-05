package nurgling.bots;


import nurgling.NGameUI;
import nurgling.bots.actions.TransferCheeseAction;
import nurgling.tools.AreasID;


public class TransferCheeseOutside extends Bot {

    public TransferCheeseOutside(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "TransferCheeseOutside";
        win_sz.y = 100;
        
        runActions.add ( new TransferCheeseAction(AreasID.c_outside));
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
