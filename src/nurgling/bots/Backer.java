package nurgling.bots;


import nurgling.NGameUI;
import nurgling.bots.actions.BackerAction;



public class Backer extends Bot {

    public Backer(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Backer";
        win_sz.y = 100;
        
        runActions.add ( new BackerAction(  ) );
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
