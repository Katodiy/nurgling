package nurgling.bots.actions;

import haven.*;
import haven.res.ui.tt.cn.CustomName;
import nurgling.NAlias;
import nurgling.NGItem;
import nurgling.NInventory;
import nurgling.NUtils;

import java.util.ArrayList;

public class AutoSplitter implements Runnable{

    Window win;
    ArrayList<GItem> items;
    String action = "Split";

    double count = 0;
    @Override
    public void run() {
        try {
            win.winDisabler = true;
            win.dwdg.show();
            win.dwdg.resize(win.deco.sz);
            win.dwdg.raise();
            NUtils.waitEvent(() -> NUtils.getFlowerMenu() == null, 500);
            NUtils.waitEvent(() -> !NUtils.getGameUI().hand.isEmpty(), 200);
            NUtils.transferToInventory(new Coord(1, 1));
            NUtils.waitEvent(() -> NUtils.getGameUI().hand.isEmpty(), 100);

            for(GItem item : items)
            {
                if(!win.winDisabler)
                    break;
                if(item.parent!=null) {
                    while(((NInventory) item.parent).getFreeSpace()>0 && (((NGItem)item).getInfo(CustomName.class)!=null || ((NGItem)item).getInfo(GItem.Amount.class)!=null)) {
                        if(!win.winDisabler)
                            break;
                        if(((((NGItem)item).getInfo(GItem.Amount.class)!=null)?((double)(((GItem.Amount)((NGItem)item).getInfo(GItem.Amount.class)).itemnum())):(((CustomName)((NGItem)item).getInfo(CustomName.class)).count))<=count)
                        {
                            if(NUtils.getSplitWnd()!=null) {
                                NUtils.getUI().destroy(NUtils.getSplitWnd());
                                NUtils.waitEvent(() -> NUtils.getSplitWnd() == null, 50);
                            }
                            break;
                        }
                        Window wnd = NUtils.getSplitWnd();

                        if(wnd==null) {
                            new SelectFlowerAction((NGItem) item, action, SelectFlowerAction.Types.Item).run(NUtils.getGameUI());
                            NUtils.waitEvent(() -> NUtils.getSplitWnd() != null, 50);
                            wnd = NUtils.getSplitWnd();
                        }

                        if (wnd != null) {
                            Window finalWnd = wnd;
                            NUtils.waitEvent(()->NUtils.getChild(finalWnd,TextEntry.class)!=null, 50);
                            for (Widget wdg = wnd.child; wdg != null; wdg = wdg.next) {
                                if (wdg instanceof TextEntry) {
                                    TextEntry te = (TextEntry) wdg;
                                    te.settext(String.valueOf(count));
                                    te.activate(String.valueOf(count));
                                    break;
                                }
                            }
                            NUtils.waitEvent(() -> !NUtils.getGameUI().hand.isEmpty(), 200);
                            NUtils.transferToInventory(new Coord(1, 1), (NInventory) item.parent);
                            NUtils.waitEvent(() -> NUtils.getGameUI().hand.isEmpty(), 100);
                        }
                    }
                }

            }
        } catch (InterruptedException ignored) {
        }finally {
            win.winDisabler = false;
            win.dwdg.hide();
            NUtils.getUI().botMode.set(false);
        }
    }

    public AutoSplitter(Window win, ArrayList<GItem> items, double count) {
        this.win = win;
        this.items = items;
        this.count = count;
    }
}