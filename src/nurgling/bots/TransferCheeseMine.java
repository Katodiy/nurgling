package nurgling.bots;


import nurgling.NGameUI;
import nurgling.bots.actions.TransferCheeseAction;
import nurgling.tools.AreasID;


public class TransferCheeseMine extends Bot {

    public TransferCheeseMine(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "TransferCheeseMine";
        win_sz.y = 100;
        
        runActions.add ( new TransferCheeseAction(AreasID.c_mine));
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
