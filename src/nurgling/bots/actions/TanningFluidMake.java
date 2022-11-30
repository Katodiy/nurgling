package nurgling.bots.actions;

import haven.Gob;
import haven.WItem;
import nurgling.*;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

import java.util.ArrayList;
import java.util.Arrays;

public class TanningFluidMake implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        Gob gob = Finder.findObjectWithCoontent(new NAlias("barrel"), new NAlias("water"), 3000);
        new PathFinder( gui, gob ).run ();
        if(gui.getInventory ().getItems ( new NAlias("bark")).size()<10) {
            return new Results(Results.Types.NO_ITEMS);
        }
        for (int i = 0 ; i<10; i++) {
            if (gui.hand.isEmpty()) {
                new TakeToHand(new NAlias("bark")).run(gui);
            }
            NUtils.activateItem(gob);
            NUtils.waitEvent(() -> gui.hand.isEmpty(), 20);
        }
        return new Results ( Results.Types.SUCCESS );
    }

    public TanningFluidMake(
    ) {
    }

}
