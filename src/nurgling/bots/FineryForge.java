package nurgling.bots;

import nurgling.NGameUI;
import nurgling.bots.actions.FFAction;

public class FineryForge extends Bot{

    public FineryForge(NGameUI gameUI) {
        super(gameUI);
        win_title = "FineryForge";
        win_sz.y = 100;

        runActions.add ( new FFAction());

    }


    @Override
    public void initAction () {
    }

    @Override
    public void endAction () {
        super.endAction ();
    }
}
