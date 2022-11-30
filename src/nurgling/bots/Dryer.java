package nurgling.bots;


import nurgling.NGameUI;
import nurgling.bots.actions.DryerAction;


public class Dryer extends Bot {

    
    public Dryer ( NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Dryer";
        win_sz.y = 100;
        

        /// Забираем шкуры и переносим их в пайлы
        runActions.add (new DryerAction());
        
    }
    
    
    @Override
    public void initAction () {
    }
    
    @Override
    public void endAction () {
        super.endAction ();
    }
}
