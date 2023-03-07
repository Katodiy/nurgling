package nurgling.bots;


import nurgling.NAlias;
import nurgling.NConfiguration;
import nurgling.NGameUI;
import nurgling.bots.actions.FillFuelFromPiles;
import nurgling.tools.AreasID;


public class FillSteel extends Bot {
    
    public FillSteel(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Steel Feel";
        win_sz.y = 100;
        
        runActions.add ( new FillFuelFromPiles( 17, new NAlias("branch"), new NAlias("steelcrucible"), new NAlias("branch"),
                AreasID.branch, "Steelbox"));
    }
    
    
    @Override
    public void initAction () {
    }
    
    @Override
    public void endAction () {
        super.endAction ();
    }
}
