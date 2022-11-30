package nurgling.bots.actions;

import haven.Gob;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.tools.Finder;
import nurgling.tools.NArea;


import java.util.ArrayList;
import java.util.Arrays;

public class DestroyAll implements Action {
    
    
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        ArrayList<Gob> gobs = Finder.findObjectsInArea ( new NAlias( new ArrayList<> ( Arrays.asList ( "" ) ),
                new ArrayList<> ( Arrays.asList ( "trees", "bumlings", "kritter", "borka" ) ) ), area );
        ArrayList<Gob> bumlings = Finder.findObjectsInArea ( new NAlias( new ArrayList<> ( Arrays.asList ( "bumlings/ras" ) )), area );
        gobs.addAll(bumlings);
        for ( Gob gob : gobs ) {
            new PathFinder( gui, gob ).run ();
            if ( NUtils.getStamina() < 0.4 ) {
                new Drink ( 0.9, false ).run ( gui );
            }
            while ( Finder.findObject ( gob.id ) != null ) {
                new Destroy ( gob ).run ( gui );
                NUtils.waitEvent(()->NUtils.getProg() >= 0, 50);
                NUtils.waitEvent(()->NUtils.getProg() < 0 || NUtils.getStamina() < 0.3, 50000);

                if ( NUtils.getStamina() < 0.4 ) {
                    new Drink ( 0.9, false ).run ( gui );
                }
            }
        }
        
        return new Results ( Results.Types.SUCCESS );
    }
    
    
    public DestroyAll ( NArea area ) {
        this.area = area;
    }
    
    NArea area;
}
