package nurgling.bots;


import nurgling.NGameUI;
import nurgling.bots.actions.DryerSeedAction;

public class SeedDryer extends Bot {

    
    public SeedDryer ( NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Seed Dryer";
        win_sz.y = 100;
        
        
        runActions.add(new DryerSeedAction());
        
    }
    
    
    @Override
    public void initAction () {
    }
    
    @Override
    public void endAction () {
        super.endAction ();
    }
}
