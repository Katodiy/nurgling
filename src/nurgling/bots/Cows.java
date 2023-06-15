package nurgling.bots;


import nurgling.NGameUI;


public class Cows extends Bot {


    public Cows(NGameUI gameUI ) {
        super(gameUI);
        win_title = "cows";
        win_sz.y = 100;

        runActions.add(new nurgling.bots.actions.Cows());
    }
    
    
    @Override
    public void initAction () {
    }
    
    @Override
    public void endAction () {
        super.endAction ();
    }
}
