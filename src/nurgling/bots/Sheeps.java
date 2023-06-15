package nurgling.bots;


import nurgling.NGameUI;


public class Sheeps extends Bot {


    public Sheeps(NGameUI gameUI ) {
        super(gameUI);
        win_title = "sheeps";
        win_sz.y = 100;

        runActions.add(new nurgling.bots.actions.Sheeps());
    }
    
    
    @Override
    public void initAction () {
    }
    
    @Override
    public void endAction () {
        super.endAction ();
    }
}
