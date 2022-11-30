package nurgling.bots;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.bots.actions.NomadCollector;

public class Candleberry extends Bot {

    public Candleberry(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Candleberry";
        win_sz.y = 100;
        
        runActions.add ( new NomadCollector( new NAlias("candleberry"), "./nomad.dat") );
    }
    
    
    @Override
    public void initAction () {
    }
    
    @Override
    public void endAction () {
        super.endAction ();
        NUtils.logOut();
    }
}
