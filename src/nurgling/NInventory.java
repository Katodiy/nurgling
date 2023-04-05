package nurgling;

import haven.*;
import haven.render.Render;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class NInventory extends Inventory {
    public NSearchWidget searchwdg;
    public NPopUpWidget popup;
    public NInventory(Coord sz) {
        super(sz);
    }

    boolean showPopup = false;

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
                if (wdg.isSeached) {
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

    public void movePopup(Coord c) {
        if(popup!=null)
        {
           popup.move(new Coord(c.x -popup.sz.x +UI.scale(16),c.y - ((Window) parent).ctl.y + popup.atl.y));
        }
        super.mousemove(c);
    }

    @Override
    public void tick(double dt) {
        super.tick(dt);
        if(popup!=null)
            popup.visible = parent.visible && showPopup;
    }
}
