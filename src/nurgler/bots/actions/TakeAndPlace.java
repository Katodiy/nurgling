package nurgler.bots.actions;

import haven.Gob;

import nurgling.*;
import nurgling.bots.actions.*;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;

public class TakeAndPlace implements Action {
    @Override
    public Results run (NGameUI gui )
            throws InterruptedException {
        ArrayList<Gob> in = Finder.findObjectsInArea ( output, inArea );

        for ( Gob gob : in ) {
            Results res = new Results ( Results.Types.SUCCESS );
            do {
                new PathFinder( gui, gob ).run ();
                new OpenTargetContainer( gob, cap ).run ( gui );

                res = new TakeAndLift( cap, item ).run ( gui );
                if ( res.type == Results.Types.NO_ITEMS ) {
                    break;
                }
                NUtils.waitEvent(()-> Finder.findLifted ()!=null, 1000);
                Gob lifted = Finder.findLifted ();
                NUtils.waitEvent(()-> lifted.getResName()!=null, 200);
                if ( new PlaceLifted( outArea, NHitBox.get ( lifted.getResName() ), lifted )
                        .run ( gui ).type != Results.Types.SUCCESS ) {
                    return new Results ( Results.Types.NO_PLACE );
                }
            }
            while ( res.type == Results.Types.SUCCESS );
        }
        return new Results ( Results.Types.SUCCESS );
    }

    public TakeAndPlace (
            NAlias output,
            String cap,
            NAlias item,
            NArea outArea,
            NArea inArea
    ) {
        this.output = output;
        this.cap = cap;
        this.item = item;
        this.outArea = outArea;
        this.inArea = inArea;
    }

    NAlias output;
    String cap;
    NAlias item;
    NArea outArea;
    NArea inArea;
}
