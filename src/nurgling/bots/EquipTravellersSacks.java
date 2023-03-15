package nurgling.bots;

import nurgling.NGameUI;

public class EquipTravellersSacks extends Bot {

    public EquipTravellersSacks(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Equip Traveller's Sacks";
        win_sz.y = 100;
 
        runActions.add ( new nurgling.bots.actions.EquipTSacks () );
        
    }
    
    
    @Override
    public void initAction ()
            throws InterruptedException { super.initAction();
    }
    
    @Override
    public void endAction () {
        super.endAction ();
    }

}
