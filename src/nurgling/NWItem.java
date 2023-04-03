package nurgling;

import haven.*;
import haven.res.ui.tt.defn.DefName;
import haven.res.ui.tt.highlighting.Highlighting;

public class NWItem extends WItem {

    public NWItem(GItem item) {
        super(item);
    }

    @Override
    public void tick(double dt) {
        super.tick(dt);
        if (((NGItem) item).needrender()) {
            for (ItemInfo inf : item.info()) {
                GItem.InfoOverlay<?>[] ols = itemols.get();
                if (ols != null) {
                    for (GItem.InfoOverlay<Tex> ol : (GItem.InfoOverlay<Tex>[]) ols) {
                        if (ol.inf instanceof NFoodInfo) {
                            ol.data = ((NFoodInfo) ol.inf).overlay();
                        }
                    }
                }
            }
        }
        try {
            String name = ((NGItem) item).name();
            if (name != null) {
                if (NUtils.getGameUI().itemsForSearch != null && !NUtils.getGameUI().itemsForSearch.isEmpty()) {
                    if (name.contains(NUtils.getGameUI().itemsForSearch)) {
                        for (ItemInfo inf : item.info()) {
                            if (inf instanceof Highlighting)
                                return;
                        }
                        item.info = null;
                        return;
                    }
                }

                for (ItemInfo inf : item.info()) {
                    if (inf instanceof Highlighting) {
                        item.info = null;
                        return;
                    }
                }
            }

        } catch (Loading e) {
        }
    }


}
