package nurgling.bots.actions;

import haven.Gob;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;


public class LiftObject implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        if(gob==null)
            gob = Finder.findObjectInArea ( name, 1000, Finder.findNearestMark ( input ) );
        if ( gob != null ) {
            PathFinder pf = new PathFinder ( gui, gob );
            pf.run ();
            NUtils.lift (gob);
            return new Results ( Results.Types.SUCCESS );
        }
        return new Results ( Results.Types.NO_FUEL );
    }
    
    public LiftObject (
            NAlias name,
            AreasID input
    ) {
        this.name = name;
        this.input = input;
    }
    
    public LiftObject (
            Gob gob
            
    ) {
        this.gob = gob;
    }
    
    Gob gob = null;
    NAlias name;
    AreasID input;
}
