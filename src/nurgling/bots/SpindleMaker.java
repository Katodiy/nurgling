package nurgling.bots;


import nurgling.NGameUI;
import nurgling.bots.actions.SpindleMaking;


public class SpindleMaker extends Bot {
    
    
    public SpindleMaker(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "SpindleMaker";
        win_sz.y = 100;
        runActions.add ( new SpindleMaking() );
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
