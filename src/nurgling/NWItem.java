package nurgling;

import haven.*;

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
        search();
    }

    private void search() {
        try {
            if (NUtils.getGameUI().itemsForSearch != null && !NUtils.getGameUI().itemsForSearch.isEmpty()) {
                String name = ((NGItem) item).name();
                if (name != null) {
                    if (NUtils.getGameUI().itemsForSearch.onlyName()) {
                        if (name.toLowerCase().contains(NUtils.getGameUI().itemsForSearch.name)) {
                            if (!((NGItem) item).isSeached) {
                                ((NGItem) item).isSeached = true;
                                item.info = null;
                            }
                            return;
                        }
                    }
                }
                for (ItemInfo inf : item.info()) {
                    if (inf instanceof NSearchable) {
                        if(((NSearchable)inf).search())
                        {
                            if (!((NGItem) item).isSeached) {
                                ((NGItem) item).isSeached = true;
                                item.info = null;
                            }
                            return;
                        }
                    }
                }
            }
        } catch (Loading e) {
        }
        if (((NGItem) item).isSeached) {
            ((NGItem) item).isSeached = false;
            item.info = null;
        }
    }


}
