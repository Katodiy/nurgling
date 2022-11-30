package nurgling.bots;


import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.bots.actions.HarvestCarrotAction;
import nurgling.bots.actions.SeederSeed;
import nurgling.bots.tools.HarvestOut;
import nurgling.tools.AreasID;

import java.util.ArrayList;


public class FarmerCarrot extends Bot {


    public FarmerCarrot(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "FarmerCarrot";
        win_sz.y = 100;
        /// доливаем воды
        
        
        //            runActions.add ( new Harvester ( harwest_id, type, paving ) );
        runActions.add ( new HarvestCarrotAction() );
        runActions.add ( new SeederSeed(new HarvestOut( new NAlias( "carrot" ), AreasID.carrot )) );
        
    }
    
    
    @Override
    public void initAction () {
    }
    
    @Override
    public void endAction () {
        super.endAction ();
    }
    
    HarvestOut seed;
    ArrayList<HarvestOut> harvestOuts = new ArrayList<> ();
}
