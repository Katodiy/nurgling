package nurgling;

import haven.*;
import haven.res.ui.tt.q.quality.Quality;

import java.util.Optional;

import static haven.Inventory.sqsz;

public class NWItem extends WItem {
    public NWItem(GItem item) {
        super(item);
    }

    public static NWItem selectedItem = null;
    @Override
    public boolean mousedown(Coord c, int btn) {
        if (ui.modshift) {
            if (ui.modmeta) {
                wdgmsg("transfer-same", item, btn == 3);
                return true;
            }
        }
        if(btn == 1) {
            if(NUtils.isFeastCurs()){
                ui.sess.character.constipation.lastItem = this;
            }

            if(ui.modshift) {
                int n = ui.modctrl ? -1 : 1;
                item.wdgmsg("transfer", c, n);
            } else if(ui.modctrl) {
                int n = ui.modmeta ? -1 : 1;
                item.wdgmsg("drop", c, n);
            } else {
                item.wdgmsg("take", c);
            }
            return(true);
        } else if(btn == 3) {
            item.wdgmsg("iact", c, ui.modflags());
            selectedItem = this;
            return(true);
        }
        return(false);

    }
}
