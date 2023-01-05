package nurgling.bots;


import nurgling.NGameUI;
import nurgling.bots.actions.TransferCheeseAction;
import nurgling.tools.AreasID;


public class TransferCheeseCellar extends Bot {

    public TransferCheeseCellar(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "TransferCheeseCellar";
        win_sz.y = 100;
        
        runActions.add ( new TransferCheeseAction(AreasID.c_cellar));
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
