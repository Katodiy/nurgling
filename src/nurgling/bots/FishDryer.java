package nurgling.bots;


import nurgling.NGameUI;
import nurgling.bots.actions.DryerFishAction;
import nurgling.bots.actions.DryerSeedAction;

public class FishDryer extends Bot {


    public FishDryer(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Fish Dryer";
        win_sz.y = 100;
        
        
        runActions.add(new DryerFishAction());
        
    }
    
    
    @Override
    public void initAction () {
    }
    
    @Override
    public void endAction () {
        super.endAction ();
    }
}
