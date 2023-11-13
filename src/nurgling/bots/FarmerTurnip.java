package nurgling.bots;


import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.bots.actions.HarvestSeedAction;
import nurgling.bots.actions.SeederSeed;
import nurgling.bots.tools.HarvestOut;
import nurgling.tools.AreasID;

import java.util.ArrayList;


public class FarmerTurnip extends Bot {


    public FarmerTurnip(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "FarmerTurnip";
        win_sz.y = 100;
        /// доливаем воды
        
        
        //            runActions.add ( new Harvester ( harwest_id, type, paving ) );
        runActions.add ( new HarvestSeedAction(new NAlias("turnip", "Turnip"), AreasID.turnip, false ));
        runActions.add ( new SeederSeed(new HarvestOut( new NAlias( "turnip" ), AreasID.turnip )) );
        
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
