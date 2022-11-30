package nurgling.bots.actions;

import haven.Gob;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NHitBox;
import nurgling.NUtils;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;
import java.util.Objects;

public class LightGob implements Action {
    public LightGob (
            NAlias name,
            AreasID id,
            int flame_flag
    ) {
        this.name = name;
        this.id = id;
        this.flame_flag = flame_flag;
    }
    
    public LightGob (
            NAlias name,
            int flame_flag,
            boolean isAll
    ) {
        this.name = name;
        this.id = null;
        this.flame_flag = flame_flag;
    }
    
    public LightGob (
            NAlias name,
            int flame_flag
    ) {
        this.name = name;
        this.flame_flag = flame_flag;
        findSingle = true;
    }
    
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        ArrayList<Gob> gobs = new ArrayList<> ();
        if ( tgob == null ) {
            if ( !findSingle ) {
                if(id!=null) {
                    gobs = Finder.findObjectsInArea ( name, Finder.findNearestMark ( id ) );
                }else{
                    gobs = Finder.findObjects ( name );
                }
            }
            else {
                Gob gob = Finder.findObject ( name);
                if ( gob != null ) {
                    gobs.add ( gob );
                }
            }
        }
        else {
            gobs.add ( tgob );
        }
        Gob candelabrum = Finder.findObject ( new NAlias ( "candelabrum" ));
//        candelabrum = cand.get ( 0 );
        boolean fireNeeded = false;
        for ( Gob gob : gobs ) {
            if ( ( gob.getModelAttribute() & flame_flag ) == 0 ) {
                fireNeeded = true;
                break;
            }
        }
        if ( fireNeeded ) {
            if ( candelabrum != null ) {
                if ( candelabrum.getModelAttribute() == 0 ) {
                    new LightCandelabrun ( candelabrum ).run ( gui );
                }
                if ( candelabrum.getModelAttribute() == 0 ) {
                    candelabrum = null;
                }
                else {
                    new LiftObject ( candelabrum ).run ( gui );
                }
            }
            for ( Gob gob : gobs ) {
                if ( ( gob.getModelAttribute() & flame_flag ) == 0 ) {
                    new LightFire ( gob, candelabrum ).run ( gui );
                    if (  ((Finder.findObject ( gob.id )).getModelAttribute() &
                            flame_flag ) == 0 ) {
                        return new Results ( Results.Types.FAIL );
                    }
                    
                }
            }
            if ( candelabrum != null ) {
                new PlaceLifted ( AreasID.candelabrun, NHitBox.getByName ( "barrel" ), new NAlias ( "candelabrum" ) )
                        .run ( gui );
            }
        }
        return new Results ( Results.Types.SUCCESS );
    }
    
    public LightGob (
            int flame_flag,
            Gob gob
    ) {
        this.flame_flag = flame_flag;
        this.tgob = gob;
    }
    
    NAlias name;
    AreasID id;
    int flame_flag;
    boolean findSingle;
    Gob tgob = null;
}
