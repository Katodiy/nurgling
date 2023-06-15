package nurgling.bots;


import nurgling.NGameUI;


public class Pigs extends Bot {


    public Pigs(NGameUI gameUI ) {
        super(gameUI);
        win_title = "pigs";
        win_sz.y = 100;

        runActions.add(new nurgling.bots.actions.Pigs());
    }
    
    
    @Override
    public void initAction () {
    }
    
    @Override
    public void endAction () {
        super.endAction ();
    }
}
