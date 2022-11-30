package nurgling;

import haven.Coord;
import haven.GItem;
import haven.ItemInfo;
import haven.WItem;
import haven.res.ui.tt.q.quality.Quality;

import java.util.Optional;

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

    public double quality () {
        Optional<ItemInfo> qInfo = this.info ().stream ().filter ( (info ) -> info instanceof Quality).findFirst ();
        return qInfo.map(itemInfo -> ((Quality) itemInfo).q).orElse(-1.0);
    }
}
