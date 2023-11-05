package nurgling.bots;


import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.bots.actions.CollectItemsToPile;
import nurgling.bots.actions.HarvestSeedAction;
import nurgling.bots.actions.SeederSeed;
import nurgling.bots.tools.HarvestOut;
import nurgling.tools.AreasID;

import java.util.ArrayList;
import java.util.Arrays;


public class FarmerHemp extends Bot {


    public FarmerHemp(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "FarmerHemp";
        win_sz.y = 100;
        /// доливаем воды
        
        
        runActions.add ( new HarvestSeedAction(new NAlias(new ArrayList<String>(Arrays.asList("hemp", "Hemp")),new ArrayList<String>(Arrays.asList("fibre", "Fibre"))), AreasID.hemp , true));
        runActions.add ( new CollectItemsToPile(AreasID.hempFibre,AreasID.hemp,new NAlias("fibre")));
        runActions.add ( new SeederSeed(new HarvestOut( new NAlias( "hemp" ), AreasID.hemp )) );
        
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
