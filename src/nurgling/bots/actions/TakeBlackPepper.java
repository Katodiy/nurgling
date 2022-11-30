package nurgling.bots.actions;


import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.tools.AreasID;

import java.util.ArrayList;
import java.util.Arrays;

public class TakeBlackPepper implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        
        if ( gui.getInventory ().getItems ( new NAlias ( new ArrayList<> ( Arrays.asList ( "pepper" ) ) ) )
                .isEmpty () ) {
            new TakeFromBarrels ( AreasID.barrels, 1, new NAlias( new ArrayList<> ( Arrays.asList ( "pepper" ) ) ) )
                    .run ( gui );
        }
        return new Results ( Results.Types.SUCCESS );
    }
}
