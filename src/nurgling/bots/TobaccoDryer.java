package nurgling.bots;


import nurgling.NGameUI;
import nurgling.bots.actions.DryerTobaccoAction;

public class TobaccoDryer extends Bot {

    public TobaccoDryer(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Tobacco Dryer";
        win_sz.y = 100;
        
        
        runActions.add(new DryerTobaccoAction());
        
    }
    
    @Override
    public void initAction () {
    }
    
    @Override
    public void endAction () {
        super.endAction ();
    }
}
