package nurgling.bots.actions;

import haven.Coord;
import haven.WItem;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;

import static haven.OCache.posres;

public class TruffleAction implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {

        return new Results ( Results.Types.SUCCESS );
    }
}
