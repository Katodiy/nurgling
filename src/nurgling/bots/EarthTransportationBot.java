package nurgling.bots;


import nurgling.NGameUI;
import nurgling.bots.actions.DryerAction;
import nurgling.bots.actions.EarthTransportBot;


public class EarthTransportationBot extends Bot {


    public EarthTransportationBot(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "EarthTransportationBot";
        win_sz.y = 100;
        

        /// Забираем шкуры и переносим их в пайлы
        runActions.add (new EarthTransportBot());
        
    }
    
    
    @Override
    public void initAction () {
    }
    
    @Override
    public void endAction () {
        super.endAction ();
    }
}
