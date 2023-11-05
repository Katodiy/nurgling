package nurgling.bots.actions;

import haven.Coord;
import haven.GItem;
import nurgling.NAlias;
import nurgling.NGItem;
import nurgling.NGameUI;
import nurgling.NUtils;

import java.util.ArrayList;
import java.util.Arrays;

import static haven.OCache.posres;

public class DanserAction implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        NUtils.waitEvent(() -> NUtils.getGameUI().cal.ui.sess.glob.ast.hh >= 5 && NUtils.getGameUI().cal.ui.sess.glob.ast.mm >= 1, 10000000);
        NUtils.command(new char[]{'e', 'o'});
        NUtils.waitEvent(() -> NUtils.isCursor("gfx/hud/curs/hand"), 500);
        NUtils.getGameUI().map.wdgmsg("click", Coord.z, NUtils.getGameUI().map.player().rc.floor(posres), 1, 0);
        return new Results(Results.Types.SUCCESS);
    }
}
