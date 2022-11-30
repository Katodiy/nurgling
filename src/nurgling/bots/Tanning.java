package nurgling.bots;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NHitBox;
import nurgling.bots.actions.*;
import nurgling.tools.AreasID;

import java.util.ArrayList;
import java.util.Arrays;


public class Tanning extends Bot {

    
    public Tanning(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Tanning";
        win_sz.y = 100;
        /// Попополняем жидкость
       runActions.add(new TanningAction());
    }
    
    
    @Override
    public void initAction () {
    }
    
    @Override
    public void endAction () {
        super.endAction ();
    }
}
