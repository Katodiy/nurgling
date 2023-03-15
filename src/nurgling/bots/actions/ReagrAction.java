package nurgling.bots.actions;

import haven.*;
import nurgling.*;
import nurgling.tools.Finder;

import java.util.ArrayList;
import java.util.Arrays;

import static haven.OCache.posres;

public class ReagrAction implements Action {

    @Override
    public Results run(NGameUI gui)
            throws InterruptedException {
        Gob gob = null;
        while (true) {

            NFightView fightView = NUtils.getFightView();

            if (fightView != null && (gob = fightView.getCurrentGob()) != null && !gob.isTag(NGob.Tags.knocked)) {
                fight(gui,gob);
            }
            else {
                Thread.sleep(50);
            }
        }
    }


    void fight(NGameUI gui, Gob gob) throws InterruptedException {
        int count = 0;
        while ((gob = Finder.findObject(gob.id)) != null && !gob.isTag(NGob.Tags.knocked)) {
            if ((NUtils.getFightSess() != null && NUtils.getFightView().curdisp != null) && (NUtils.getFightView().curdisp.give.state == 0 || NUtils.getFightView().curdisp.give.state == 2)) {
                int oldstate = NUtils.getFightView().curdisp.give.state;
                NUtils.getFightView().give();
                NUtils.waitEvent(() -> NUtils.getFightView().curdisp == null || oldstate != NUtils.getFightView().curdisp.give.state, 100, 50);
            } else {
                count = 0;
            }
            if (NUtils.getFightView().curdisp == null || NUtils.getFightView().getCurrentGob()!=gob) {
                do {
                    count++;
                    NUtils.command(new char[]{'t'});
                    NUtils.getGameUI().map.wdgmsg("click", Coord.z, gob.rc.floor(posres), 1, 0, 1,
                            (int) gob.id, gob.rc.floor(posres), 0, -1);
                    NUtils.waitEvent(() -> NUtils.getFightSess() != null || NUtils.getFightView().curdisp != null, 100, 20);
                    if (count >= 6)
                        return;
                } while (NUtils.getFightView().curdisp == null && !gob.isTag(NGob.Tags.knocked));
            } else {
                Thread.sleep(50);
                if (!NUtils.isIdleCurs()) {
                    gui.map.wdgmsg("click", Coord.z, gui.map.player().rc.floor(posres), 3, 0);
                    NUtils.waitEvent(() -> !NUtils.isIdleCurs(), 500);
                }
            }
        }
        if (!NUtils.isIdleCurs()) {
            gui.map.wdgmsg("click", Coord.z, gui.map.player().rc.floor(posres), 3, 0);
            NUtils.waitEvent(() -> !NUtils.isIdleCurs(), 500);
        }
    }

    public ReagrAction(
    ) {
    }


}