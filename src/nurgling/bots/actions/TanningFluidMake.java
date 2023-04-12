package nurgling.bots.actions;

import haven.Gob;
import nurgling.*;
import nurgling.tools.Finder;

public class TanningFluidMake implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        Gob gob = Finder.findObjectWithCoontent(new NAlias("barrel"), new NAlias("water"), 3000);
       // new PathFinder( gui, gob ).run ();
        if(gui.getInventory ().getWItems( new NAlias("bark")).size()<10) {
            return new Results(Results.Types.NO_ITEMS);
        }
        int count = 0;
        while (count<10) {
            if (gui.hand.isEmpty()) {
                new TakeToHand(new NAlias("bark")).run(gui);
            }
            if(!gui.hand.isEmpty()) {
                NUtils.activateItem(gob);
                NUtils.waitEvent(() -> gui.hand.isEmpty(), 500);
                if (gui.hand.isEmpty())
                    count++;
            }
        }
        return new Results ( Results.Types.SUCCESS );
    }

    public TanningFluidMake(
    ) {
    }

}
