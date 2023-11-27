package nurgling.bots;

import haven.Gob;
import nurgling.NGameUI;
import nurgling.bots.actions.GardenPotMakerAction;

import java.util.ArrayList;
import java.util.Arrays;

public class GardenPotMaker extends Bot {
    
    public GardenPotMaker ( NGameUI gameUI ) {
        super ( gameUI );
        win_title = "GardenPotMaker";
        win_sz.y = 100;
        


        runActions.add ( new GardenPotMakerAction());
    }
    
    
    @Override
    public void initAction () {
    }
    
    @Override
    public void endAction () {
        super.endAction ();
    }
}
