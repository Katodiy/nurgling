package nurgling.bots;


import nurgling.NGameUI;


public class Goats extends Bot {


    public Goats(NGameUI gameUI ) {
        super(gameUI);
        win_title = "goats";
        win_sz.y = 100;

        runActions.add(new nurgling.bots.actions.Goats());
    }
    
    
    @Override
    public void initAction () {
    }
    
    @Override
    public void endAction () {
        super.endAction ();
    }
}
