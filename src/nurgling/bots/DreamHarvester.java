package nurgling.bots;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.bots.actions.CollectAllInArea;


public class DreamHarvester extends Bot {
    
    public DreamHarvester(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Dream collector";
        win_sz.y = 100;
        
        runActions.add ( new CollectAllInArea( new NAlias ("cupboard"),"Cupboard", new NAlias("dreca"),new NAlias (
                "dream") , "Harvest", 3 ) );
    }
    
    
    @Override
    public void initAction () {
    }
    
    @Override
    public void endAction () {
        super.endAction ();
    }
}
