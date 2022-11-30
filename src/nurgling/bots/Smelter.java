package nurgling.bots;

import nurgling.NGameUI;
import nurgling.bots.actions.SmelterAction;


public class Smelter extends Bot {

    
    public Smelter(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "SmeltingBot";
        win_sz.y = 100;
        
        runActions.add (new SmelterAction());
    }
    
    
    @Override
    public void initAction () {
    }
    
    @Override
    public void endAction () {
        super.endAction ();
    }
    
    boolean iron_smelter = true;
    
}
