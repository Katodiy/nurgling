package nurgling.bots;

import nurgling.NGameUI;
import nurgling.bots.actions.CattailAction;

public class CattailPicker extends Bot {
    
    public CattailPicker(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "CattailPicker";
        win_sz.y = 100;
        
        runActions.add ( new CattailAction( ) );
    }
    
    
    @Override
    public void initAction () {
    }
    
    @Override
    public void endAction () {
        super.endAction ();
    }
}
