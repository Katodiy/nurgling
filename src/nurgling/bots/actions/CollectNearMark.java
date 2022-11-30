package nurgling.bots.actions;

import haven.Coord2d;
import haven.Gob;

import haven.WItem;
import haven.res.ui.tt.leashed.Leashed;
import nurgling.*;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;

public class CollectNearMark implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        ArrayList<Gob> candidates = Finder.findObjectsInArea ( name, new NArea( mark, 1000 ) );
        if(candidates.isEmpty ())
            return new Results ( Results.Types.SUCCESS );
        for ( Gob gob : candidates ) {
            if ( gui.getInventory ().getFreeSpace () != 0 ) {
                if(leashed){
                    if(gob.rc.dist(leashed_gob.rc)<20){
                        WItem urope = gui.getInventory().getItem(new NAlias("rope"), Leashed.class);
                        if(urope!=null){
                            new SelectFlowerAction(urope,"Pull", SelectFlowerAction.Types.Inventory).run(gui);
                        }
                        NUtils.waitEvent(()->gob.rc.dist(leashed_gob.rc)>20,50);
                    }
                }
                PathFinder pf = new PathFinder ( gui, gob.rc );
                if(!leashed)
                    pf.setWithAlarm ( true );
                pf.run ();

                if(!gob.isTag(NGob.Tags.truffle))
                    new SelectFlowerAction ( gob, "Pick" , SelectFlowerAction.Types.Gob).run ( gui );
                else
                    NUtils.takeFromEarth ( gob );
                NUtils.waitEvent(()->NUtils.getGob(gob.id)==null, 500);
            }
            else {
                return new Results ( Results.Types.NO_FREE_SPACE );
            }
        }
        PathFinder pf = new PathFinder ( gui, mark );
        pf.setWithAlarm ( true );
        pf.run ();
        return new Results ( Results.Types.SUCCESS );
    }
    
    public CollectNearMark(
            Coord2d mark,
            NAlias name,
            boolean leashed,
            Gob gob
    ) {
        this.mark = mark;
        this.name = name;
        this.leashed_gob = gob;
        this.leashed = leashed;
    }
    
    Coord2d mark;
    
    NAlias name;

    boolean leashed = false;
    Gob leashed_gob = null;
}
