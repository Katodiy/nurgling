package nurgling.bots.actions;

import haven.Coord2d;
import haven.Gob;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;

public class CollectAndReturn implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        Coord2d start = Finder.findObject(new NAlias("runestone")).rc;
        PathFinder pf = new PathFinder ( gui, start );
        pf.run ();
        ArrayList<Gob> gobs = Finder.findObjects ( name );
        for(Gob gob:gobs){
            if ( gui.getInventory ().getFreeSpace () != 0 ) {
                new PathFinder ( gui, gob ).run ();
                new SelectFlowerAction ( gob, "Pick" , SelectFlowerAction.Types.Gob).run ( gui );
                Thread.sleep ( 2000 );
            }
            else {
                return new Results ( Results.Types.NO_FREE_SPACE );
            }
        }
        new PathFinder ( gui, start ).run ();
        NUtils.logOut ();
        return new Results ( Results.Types.SUCCESS );
    }
    
    public CollectAndReturn(
            NAlias name
    ) {
        this.name = name;
    }
    
    NAlias name;
    
}
