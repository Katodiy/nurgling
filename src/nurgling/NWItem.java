package nurgling;

import haven.*;
import haven.res.ui.tt.highlighting.Highlighting;
import haven.res.ui.tt.q.quality.Quality;

import java.util.Collection;

public class NWItem extends WItem {

    public NWItem(GItem item) {
        super(item);
    }

//    public static NWItem selectedItem = null;
    @Override
    public void tick(double dt) {
        super.tick(dt);
        if (((NGItem) item).needrender()) {
            for (ItemInfo inf : item.info()) {
                GItem.InfoOverlay<?>[] ols = itemols.get();
                if (ols != null) {
                    for (GItem.InfoOverlay<Tex> ol : (GItem.InfoOverlay<Tex>[]) ols) {
                        if (ol.inf instanceof NFoodInfo && ((NFoodInfo) ol.inf).check()) {
                            ol.data = ((NFoodInfo) ol.inf).overlay();
                        } else if (ol.inf instanceof Quality && ((Quality) ol.inf).check()) {
                            ol.data = ((Quality) ol.inf).overlay();
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
                                if (!NUtils.getGameUI().itemsForSearch.q.isEmpty() && !searchQuality())
                                    return;
                                ((NGItem) item).isSeached = true;
                            }
                            return;
                        }
                    }
                }
                if(!NUtils.getGameUI().itemsForSearch.q.isEmpty() && searchQuality())
                {
                    if (!((NGItem) item).isSeached) {
                        ((NGItem) item).isSeached = true;
                    }
                }
            }
        } catch (Loading e) {
        }
        if (((NGItem) item).isSeached) {
            if (NUtils.getGameUI().itemsForSearch != null && !NUtils.getGameUI().itemsForSearch.q.isEmpty() && searchQuality())
                return;
            ((NGItem) item).isSeached = false;
        }
    }

    private boolean searchQuality() {
        for(NGameUI.SearchItem.Quality q : NUtils.getGameUI().itemsForSearch.q)
        {
            switch (q.type) {
                case MORE:
                    if (((NGItem) item).quality() <= q.val)
                        return false;
                    break;
                case LOW:
                    if (((NGItem) item).quality() >= q.val)
                        return false;
                    break;
                case EQ:
                    if (((NGItem) item).quality() != q.val)
                        return false;
            }
        }

            if (!NUtils.getGameUI().itemsForSearch.name.isEmpty()) {
                String name = ((NGItem) item).name();
                if(name!=null) {
                    return name.toLowerCase().contains(NUtils.getGameUI().itemsForSearch.name);
                }
                else
                {
                    return false;
                }
            }
        return true;
    }


    @Override
    public boolean mousedown(Coord c, int btn) {
//        if(btn == 3) {
//            selectedItem = this;
//        }
        if (ui.modctrl && ui.modmeta) {
            if(item.parent instanceof NInventory) {
                Collection<GItem> items;
                if (((NGItem) item).isSeached) {
                    items = ((NInventory) item.parent).getWItems(Highlighting.class);
                } else {
                    items = ((NInventory) item.parent).getWItems(new NAlias(((NGItem) item).name()));
                }
                for(GItem item : items)
                {
                    item.wdgmsg("drop", c, 1);
                }
            }
            return (true);
        } else if(ui.modshift && ui.modmeta) {
            Collection<GItem> items;
            if (item.parent instanceof NInventory) {
                if (((NGItem) item).isSeached) {
                    items = ((NInventory) item.parent).getWItems(Highlighting.class);
                } else {
                    items = ((NInventory) item.parent).getWItems(new NAlias(((NGItem) item).name()));
                }
                for(GItem item : items)
                {
                    item.wdgmsg("transfer", c, 1);
                }
            }
        }
        return super.mousedown(c, btn);
    }
}
