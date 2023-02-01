package nurgling.bots;


import nurgling.NGameUI;

import nurgling.bots.actions.MonitorGob;
import nurgling.bots.actions.NomadFinder;
import nurgling.bots.actions.Returner;


public class NomadFinderBot extends Bot {

    
    public NomadFinderBot(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "GobFinder";
        win_sz.y = 100;
        runActions.add ( new NomadFinder(  ) );
        runActions.add ( new MonitorGob(  ) );
        runActions.add ( new Returner( true ) );

    }
    
    
    @Override
    public void initAction ()
            throws InterruptedException {
        super.initAction ();

    }

    @Override
    public void endAction () {

        super.endAction ();
    }
}
