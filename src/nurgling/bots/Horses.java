package nurgling.bots;


import nurgling.NGameUI;


public class Horses extends Bot {


    public Horses(NGameUI gameUI ) {
        super(gameUI);
        win_title = "horses";
        win_sz.y = 100;

        runActions.add(new nurgling.bots.actions.Horses());
    }
    
    
    @Override
    public void initAction () {
    }
    
    @Override
    public void endAction () {
        super.endAction ();
    }
}
