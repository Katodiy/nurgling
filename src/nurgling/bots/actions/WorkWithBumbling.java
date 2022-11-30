package nurgling.bots.actions;

import haven.Gob;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

public class WorkWithBumbling implements Action {
    
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {

        int need = count;
        while ( need > 0 ) {
            Gob gob = Finder.findObjectInArea ( name, 2000, area );
            
            if ( gob != null ) {
                PathFinder pf = new PathFinder( gui, gob );
                pf.setHardMode ( true );
                pf.run ();
                int currentCount = gui.getInventory ().getFreeSpace ();
                new SelectFlowerAction ( gob, "Chip stone", SelectFlowerAction.Types.Gob ).run ( gui );
                int counter = 0;
                while ( ( NUtils.getProg() >= 0 &&  NUtils.getStamina()  >= 0.3 &&
                        currentCount - gui.getInventory ().getFreeSpace () < count && ( gui.getInventory ().getFreeSpace () ) >
                        0 && Finder.findObject ( gob.id ) != null ) || counter < 10 ) {
                    Thread.sleep ( 50 );
                    counter += 1;
                }
                if ( currentCount - gui.getInventory ().getFreeSpace () >= count ) {
                    NUtils.stopWithClick ();
                    return new Results ( Results.Types.SUCCESS );
                }
                if ( NUtils.getStamina() < 0.3 ) {
                    new Drink ( 0.9, false ).run ( gui );
                }
                need = count - (currentCount - gui.getInventory ().getFreeSpace ());
                if ( gui.getInventory ().getFreeSpace () == currentCount ) {
                    return new Results ( Results.Types.FAIL );
                }
            }
            else {
                return new Results ( Results.Types.NO_ITEMS );
            }
        }
        return new Results ( Results.Types.FAIL );
        
    }
    

    public WorkWithBumbling(
            int count,
            NAlias name,
            NArea area
    ) {
        this.count = count;
        this.name = name;
        this.area = area;
    }
    
    int count;
    NAlias name;
    NArea area = null;
}
