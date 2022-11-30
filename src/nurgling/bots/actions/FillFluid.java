package nurgling.bots.actions;

import haven.Gob;

import nurgling.*;

import nurgling.tools.AreasID;
import nurgling.tools.Finder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class FillFluid implements Action {
    @Override
    public Results run (NGameUI gui )
            throws InterruptedException {
        Gob barrel = Finder.findObjectInArea ( new NAlias( "barrel" ), 1000, Finder.findNearestMark ( input ) );
        ArrayList<Gob> tunner;
        if ( withPaving ) {
            tunner = Finder.findObjectsInArea ( target, Finder.findNearestMark ( output, paving ) );
        }
        else {
            if(output!=null)
            tunner = Finder.findObjectsInArea ( target, Finder.findNearestMark ( output ) );
            else
                tunner = Finder.findObjects ( target );
        }
        boolean isFound = false;
        for ( Gob gob : tunner ) {
            if ( (  gob.getModelAttribute() & flag ) == 0 ) {
                isFound = true;
                break;
            }
        }
        if ( !isFound ) {
            return new Results ( Results.Types.SUCCESS );
        }
        if ( new LiftObject( new NAlias ( "barrel" ), input ).run ( gui ).type != Results.Types.SUCCESS ) {
            return new Results ( Results.Types.NO_WORKSTATION );
        }
        for ( Gob gob : tunner ) {
            while ( (  gob.getModelAttribute() & flag ) == 0 || forced ) {
                if ( !NUtils.isOverlay ( Objects.requireNonNull ( Finder.findObject ( barrel.id ) ), content ) ) {
                    if ( new ReFillInCistern ( content, input, barrel ).run ( gui ).type != Results.Types.SUCCESS ) {
                        return new Results ( Results.Types.NO_FUEL );
                    }
                }
                PathFinder pathFinder = new PathFinder ( gui, gob );
                pathFinder.ignoreGob ( barrel );
                pathFinder.setHardMode ( true );
                pathFinder.run ();
                NUtils.activate ( gob );
                NUtils.waitEvent ( ()->(  gob.getModelAttribute() & flag ) != 0, 60 );
                
                if ( forced ) {
                    forced = false;
                }
            }
            Thread.sleep ( 200 );
        }
        if ( NUtils.isOverlay ( Objects.requireNonNull ( Finder.findObject ( barrel.id ) ), content ) ) {
            Gob cistern = Finder.findObjectInArea ( new NAlias ( new ArrayList<> ( Arrays.asList ( "cistern" ))), 1000,
                    Finder.findNearestMark ( input ) );
            if (cistern!=null) {
                PathFinder pathFinder = new PathFinder ( gui, cistern );
                pathFinder.ignoreGob ( barrel );
                pathFinder.setHardMode ( true );
                pathFinder.run ();
                NUtils.activate ( cistern );
                int counter = 0;
                while ( ( NUtils.isOverlay ( Objects.requireNonNull ( Finder.findObject ( barrel.id ) ), content ) &&
                        counter < 20 ) || NUtils.getProg() >= 0 ) {
                    counter++;
                    Thread.sleep ( 50 );
                }
            }
        }
        if ( new PlaceLifted( input, NHitBox.getByName ( "barrel" ), new NAlias ( "barrel" ) ).run ( gui ).type !=
                Results.Types.SUCCESS ) {
            return new Results ( Results.Types.NO_FREE_SPACE );
        }
        Thread.sleep ( 500 );
        return new Results ( Results.Types.SUCCESS );
    }
    
    
    public FillFluid(
            AreasID input,
            AreasID output,
            int flag,
            NAlias target,
            NAlias content
    ) {
        this.input = input;
        this.output = output;
        this.flag = flag;
        this.target = target;
        this.content = content;
    }

    public FillFluid(
            AreasID input,
            int flag,
            NAlias target,
            NAlias content
    ) {
        this.input = input;
        this.output = null;
        this.flag = flag;
        this.target = target;
        this.content = content;
    }
    
    public FillFluid(
            AreasID input,
            AreasID output,
            int flag,
            NAlias target,
            NAlias content,
            String paving
    ) {
        this.input = input;
        this.output = output;
        this.flag = flag;
        this.target = target;
        this.content = content;
        this.paving = paving;
        this.withPaving = true;
    }
    
    public FillFluid(
            AreasID input,
            AreasID output,
            NAlias target,
            NAlias content,
            boolean forced
    ) {
        this.input = input;
        this.output = output;
        this.target = target;
        this.content = content;
        this.forced = forced;
    }
    
    AreasID input;
    AreasID output;
    int flag = 1024;
    NAlias target;
    NAlias content;
    boolean forced = false;
    boolean withPaving = false;
    String paving;
}
