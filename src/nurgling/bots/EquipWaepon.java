package nurgling.bots;

import nurgling.NGameUI;

public class EquipWaepon extends Bot {

    public EquipWaepon(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Equip weapons";
        win_sz.y = 100;
 
        runActions.add ( new nurgling.bots.actions.EquipWaepon () );
        
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
