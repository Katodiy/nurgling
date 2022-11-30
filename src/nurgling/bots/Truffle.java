package nurgling.bots;


import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.bots.actions.AnimalAction;
import nurgling.bots.actions.TruffleCollect;
import nurgling.tools.AreasID;

import java.util.ArrayList;
import java.util.Arrays;


public class Truffle extends Bot {


    public Truffle(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Truffle";
        win_sz.y = 100;
        

        /// Забираем шкуры и переносим их в пайлы
        runActions.add (new TruffleCollect());
        
    }
    
    
    @Override
    public void initAction () {
    }
    
    @Override
    public void endAction () {
        super.endAction ();
    }
}
