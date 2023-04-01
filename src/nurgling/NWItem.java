package nurgling;

import haven.*;
import haven.res.ui.tt.defn.DefName;

public class NWItem extends WItem {

    public NWItem(GItem item) {
        super(item);
    }

    @Override
    public void tick(double dt) {
        super.tick(dt);
        if(((NGItem)item).needrender()) {
            for (ItemInfo inf : item.info()) {
                GItem.InfoOverlay<?>[] ols = itemols.get();
                if (ols != null) {
                    for (GItem.InfoOverlay<Tex> ol : (GItem.InfoOverlay<Tex>[])ols) {
                        if (ol.inf instanceof NFoodInfo) {
                            ol.data = ((NFoodInfo)ol.inf).overlay();
                        }
                    }
                }
            }
        }
    }


}
