package nurgling.bots;

import nurgling.NGameUI;
import nurgling.bots.actions.DanserAction;


public class Dancer extends Bot {

    public Dancer(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Dance at dawn";
        win_sz.y = 100;
        
        runActions.add ( new DanserAction() );
    }
    
    
    @Override
    public void initAction () {
    }
    
    @Override
    public void endAction () {
        super.endAction ();
    }
}
