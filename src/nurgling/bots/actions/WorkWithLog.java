package nurgling.bots.actions;

import haven.Coord;
import haven.Gob;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;
import nurgling.tools.NArea;


import java.util.ArrayList;
import java.util.Arrays;


public class WorkWithLog implements Action {

    public static ArrayList<String> lumber_tools = new ArrayList<String> (
            Arrays.asList ( "woodsmansaxe", "axe-m", "stoneaxe" ) );
    public static ArrayList<String> saw_tools = new ArrayList<String> ( Arrays.asList ( "saw-m", "bonesaw" ) );
    
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        if ( new Equip ( new NAlias( ( isCopping ? lumber_tools : saw_tools ) ) ).run ( gui ).type !=
                Results.Types.SUCCESS ) {
            return new Results ( Results.Types.NO_WORKSTATION );
        }
        int need = count;
        while ( need > 0 ) {
            Gob gob;
            if ( area == null ) {
                gob = Finder.findObjectInArea ( name, 2000, Finder.findNearestMark ( AreasID.logs ) );
            }else{
                gob = Finder.findObjectInArea ( name, 2000, area );
            }
            if ( gob != null ) {
                PathFinder pf = new PathFinder( gui, gob );
                pf.setHardMode ( true );
                pf.run ();
                if ( isCopping ) {
                    new SelectFlowerAction ( gob, "Chop into blocks", SelectFlowerAction.Types.Gob ).run ( gui );
                }
                else {
                    new SelectFlowerAction ( gob, "Make boards", SelectFlowerAction.Types.Gob ).run ( gui );
                }
                int counter = 0;
                while ( ( NUtils.getProg() >= 0 && NUtils.getStamina() >= 0.3 &&
                        ( ( isCopping ) ? gui.getInventory ().getItems ( new NAlias ( "block" ) ).size () : gui
                                .getInventory ().getItems ( new NAlias ( "board" ) ).size () ) < count &&
                        ( ( isCopping ) ? gui.getInventory ().getNumberFreeCoord ( new Coord ( 2, 1 ) ) : gui
                                .getInventory ().getNumberFreeCoord ( new Coord ( 1, 4 ) ) ) > 0 &&
                        Finder.findObject ( gob.id ) != null ) || counter < 10 ) {
                    Thread.sleep ( 50 );
                    counter += 1;
                }
                if ( ( ( isCopping ) ? gui.getInventory ().getItems ( new NAlias ( "block" ) ).size () : gui
                        .getInventory ().getItems ( new NAlias ( "board" ) ).size () ) >= count ) {
                    NUtils.stopWithClick ();
                    return new Results ( Results.Types.SUCCESS );
                }
                if ( NUtils.getStamina() < 0.3 ) {
                    new Drink ( 0.9, false ).run ( gui );
                }
                need = count - ( ( isCopping ) ? gui.getInventory ().getItems ( new NAlias ( "block" ) ).size () : gui
                        .getInventory ().getItems ( new NAlias ( "board" ) ).size () );
                if ( ( isCopping ) ? ( gui.getInventory ().getNumberFreeCoord ( new Coord ( 2, 1 ) ) == 0 ) :
                        ( gui.getInventory ().getNumberFreeCoord ( new Coord ( 1, 4 ) ) ) == 0 ) {
                    return new Results ( Results.Types.SUCCESS );
                }
            }
            else {
                return new Results ( Results.Types.NO_ITEMS );
            }
        }
        return new Results ( Results.Types.FAIL );
        
    }
    
    public WorkWithLog(
            int count,
            NAlias name,
            boolean isCopping
    ) {
        this.count = count;
        this.name = name;
        this.isCopping = isCopping;
    }
    
    public WorkWithLog(
            int count,
            NAlias name,
            boolean isCopping,
            NArea area
    ) {
        this.count = count;
        this.name = name;
        this.isCopping = isCopping;
        this.area = area;
    }
    
    int count;
    NAlias name;
    boolean isCopping;
    NArea area = null;
}
