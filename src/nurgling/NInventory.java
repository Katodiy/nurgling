package nurgling;

import haven.*;
import haven.res.ui.tt.slot.Slotted;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NInventory extends Inventory {
    public NSearchWidget searchwdg;
    public NPopUpWidget toggles;
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


        movePopup(parent.c);
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
        if(toggles !=null)
        {
           toggles.move(new Coord(c.x - toggles.atl.x - UI.scale(5),c.y + UI.scale(25)));
        }
        if(searchwdg.history!=null) {
            searchwdg.history.move(new Coord(c.x  + ((Window)parent).atl.x + UI.scale(7), c.y + parent.sz.y- UI.scale(37)));
        }
        super.mousemove(c);
    }


    @Override
    public void tick(double dt) {
        super.tick(dt);
        if(toggles !=null)
            toggles.visible = parent.visible && showPopup;
    }

    private static final TexI[] collapsei = new TexI[]{
            new TexI(Resource.loadsimg("nurgling/hud/buttons/itogglecu")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/itogglecd")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/itogglech")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/itogglecdh"))};

    private static final TexI[] gildingi = new TexI[]{
            new TexI(Resource.loadsimg("nurgling/hud/buttons/gilding/u")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/gilding/d")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/gilding/h")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/gilding/dh"))};

    private static final TexI[] ssearchbi = new TexI[]{
            new TexI(Resource.loadsimg("nurgling/hud/buttons/ssearchu")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/ssearchd")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/ssearchh"))};


    public void installMainInv()
    {
        searchwdg = new NSearchWidget(new Coord(sz));
        searchwdg.resize(sz);
        parent.add(searchwdg, (new Coord(0, sz.y + UI.scale(10))));
        parent.add(new ICheckBox(collapsei[0], collapsei[1], collapsei[2], collapsei[3]) {
                              @Override
                              public void changed(boolean val) {
                                  super.changed(val);
                                  showPopup = val;
                              }
                          }
                , new Coord(-gildingi[0].sz().x + UI.scale(2), UI.scale(27)));

        parent.pack();
        toggles = NUtils.getGameUI().add(new NPopUpWidget(new Coord(UI.scale(50), UI.scale(80)), NPopUpWidget.Type.RIGHT));


        Widget pw = toggles.add(new ICheckBox(gildingi[0], gildingi[1], gildingi[2], gildingi[3]) {
                              @Override
                              public void changed(boolean val) {
                                  super.changed(val);
                                  Slotted.show = val;
                              }
                          }
                , toggles.atl);
        pw = toggles.add(new IButton(ssearchbi[0].back, ssearchbi[1].back, ssearchbi[2].back), pw.pos("bl").add(UI.scale(new Coord(0, 5))));
        pw = toggles.add(new IButton(ssearchbi[0].back, ssearchbi[1].back, ssearchbi[2].back), pw.pos("bl").add(UI.scale(new Coord(0, 5))));
        pw = toggles.add(new IButton(ssearchbi[0].back, ssearchbi[1].back, ssearchbi[2].back), pw.pos("bl").add(UI.scale(new Coord(0, 5))));
        pw = toggles.add(new IButton(ssearchbi[0].back, ssearchbi[1].back, ssearchbi[2].back), pw.pos("bl").add(UI.scale(new Coord(0, 5))));
        pw = toggles.add(new IButton(ssearchbi[0].back, ssearchbi[1].back, ssearchbi[2].back), pw.pos("bl").add(UI.scale(new Coord(0, 5))));
        toggles.pack();
        movePopup(parent.c);
        toggles.pack();

    }
}
