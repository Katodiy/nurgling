package nurgling.bots.actions;

import haven.*;
import nurgling.*;
import nurgling.tools.Finder;

import java.util.ArrayList;
import java.util.Arrays;

import static haven.OCache.posres;

public class ReagrAction implements Action {

    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        Gob gob;
        while (true) {

            NFightView fightView = NUtils.getFightView();

            if (fightView != null && (gob = fightView.getCurrentGob()) != null && !gob.isTag(NGob.Tags.knocked)) {
                while ((gob = Finder.findObject(gob.id)) != null && !gob.isTag(NGob.Tags.knocked)) {
                    if ((NUtils.getFightSess() != null && NUtils.getFightView().curdisp != null) && (NUtils.getFightView().curdisp.give.state == 0 || NUtils.getFightView().curdisp.give.state == 2)) {
                        int oldstate = NUtils.getFightView().curdisp.give.state;
                        NUtils.getFightView().give();
                        NUtils.waitEvent(() ->  NUtils.getFightView().curdisp == null || oldstate!= NUtils.getFightView().curdisp.give.state, 10, 50);
                    }
                    if (NUtils.getFightView().curdisp == null) {
                        do {
                            NUtils.command(new char[]{'t'});
                            NUtils.getGameUI().map.wdgmsg("click", Coord.z, gob.rc.floor(posres), 1, 0, 1,
                                    (int) gob.id, gob.rc.floor(posres), 0, -1);
                            NUtils.waitEvent(() ->  NUtils.getFightSess() != null||  NUtils.getFightView().curdisp != null,10,20);
                        } while ( NUtils.getFightView().curdisp == null && !gob.isTag(NGob.Tags.knocked));
                    }else{
                        Thread.sleep(20);
                        if(!   NUtils.isIdleCurs()) {
                            gui.map.wdgmsg("click", Coord.z, gui.map.player().rc.floor(posres), 3, 0);
                            NUtils.waitEvent(()->!NUtils.isIdleCurs(), 50);
                        }
                    }
                }
                if(!   NUtils.isIdleCurs()) {
                    gui.map.wdgmsg("click", Coord.z, gui.map.player().rc.floor(posres), 3, 0);
                    NUtils.waitEvent(()->!NUtils.isIdleCurs(), 50);
                }
            } else {

                Thread.sleep(20);
            }
        }
    }

    public ReagrAction(
    ) {
    }
    
    
}