package nurgling.bots.actions;

import haven.Gob;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class ReFillInCistern implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        if(barrel==null) {
            barrel = Finder.findObject ( new NAlias( "barrel" ) );
        }
        Gob cistern = Finder.findObjectInArea ( new NAlias ( new ArrayList<> ( Arrays.asList ( "cistern", "well")) ),
                1000,
                Finder.findNearestMark ( input ) );
        PathFinder pf = new PathFinder ( gui, cistern );
        pf.ignoreGob ( barrel );
        pf.run ();
        NUtils.activate ( cistern );
        int counter = 0;
        while ( (!NUtils
                .isOverlay ( Objects.requireNonNull ( Finder.findObject ( barrel.id ) ), name ) &&
                counter < 20)   ||
                (!NUtils
                .isOverlay ( Objects.requireNonNull ( Finder.findObject ( barrel.id ) ), name ) && NUtils.getProg()>=0)) {
            counter++;
            Thread.sleep ( 50 );
        }
        return  NUtils.isOverlay ( Objects.requireNonNull ( Finder.findObject ( barrel.id ) ),
                name) ? new Results ( Results.Types.SUCCESS ) : new Results (
                Results.Types.NO_FUEL );
        
    }
    
    public ReFillInCistern(
            NAlias name,
            AreasID input
    ) {
        this.name = name;
        this.input = input;
    }
    
    public ReFillInCistern(
            NAlias name,
            AreasID input,
            Gob barrel
    ) {
        this.name = name;
        this.input = input;
        this.barrel = barrel;
    }
    
    NAlias name;
    AreasID input;
    Gob barrel = null;
}
