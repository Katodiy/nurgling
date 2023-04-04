package nurgling;

import haven.*;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class NInventory extends Inventory {
    public NSearchWidget searchwdg;
    public NInventory(Coord sz) {
        super(sz);
    }

    @Override
    public void resize(Coord sz) {
        super.resize(new Coord(sz));
        searchwdg.resize(new Coord(sz.x , 0));
        searchwdg.move(new Coord(0,sz.y + UI.scale(5)));
        parent.pack();
    }

    public ArrayList<GItem> getItems(
            final NAlias names,
            double q
    ) {
        ArrayList<GItem> result = new ArrayList<>();
            for (Widget widget = child; widget != null; widget = widget.next) {
                if (widget instanceof WItem) {
                    NGItem wdg = (NGItem) ((WItem) widget).item;
                    if (NUtils.checkName(wdg.name(), names)) {
                        if ((wdg.quality()) >= q) {
                            result.add(wdg);
                        }
                    }
                }
            }
        return result;
    }

    public ArrayList<NGItem> getItems(
            Class<?> cl
    ) {
        ArrayList<NGItem> result = new ArrayList<>();
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                NGItem wdg = (NGItem) ((WItem) widget).item;
                if (wdg.isHaveInfo(cl)) {
                    result.add(wdg);
                }
            }
        }
        return result;
    }

    public ArrayList<NGItem> getItems(
            NAlias name
    ) {
        ArrayList<NGItem> result = new ArrayList<>();
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                NGItem wdg = (NGItem) ((WItem) widget).item;
                if (NUtils.checkName(wdg.name(), name)) {
                    result.add(wdg);
                }
            }
        }
        return result;
    }


    public boolean isLoaded(){
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                NGItem item = ((NGItem) ((WItem) widget).item);
                if ( (item.getStatus() & NGItem.NAME_IS_READY) == 0)
                    return false;
            }
        }
        return true;
    }
}
