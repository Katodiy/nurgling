package nurgling;

import haven.*;
import haven.res.ui.tt.cn.CustomName;
import nurgling.bots.actions.SelectFlowerAction;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class NFlowerMenu extends FlowerMenu {
    @Override
    public void draw(GOut g) {
        super.draw(g);
    }

    public void select(String text) {
        for (Petal p : opts) {
            if (p.name.contains(text)) {
                choose(p);
            }
        }
        cancel();
    }

    public void select(NAlias text) {
        for (Petal p : opts) {
            if (NUtils.checkName(p.name, text)) {
                choose(p);
            }
        }
        cancel();
    }

    public boolean find(String text) {
        for (Petal p : opts) {
            if (p.name.contains(text)) {
                return true;
            }
        }
        cancel();
        return false;
    }

    public boolean find(NAlias text) {
        for (Petal p : opts) {
            if (NUtils.checkName(p.name, text)) {
                return true;
            }
        }
        cancel();
        return false;
    }

    public NFlowerMenu(String... options) {
        super(options);
    }

    @Override
    public void tick(double dt) {
        super.tick(dt);
        if(NUtils.getUI()!=null && !NUtils.getUI().botMode.get())
        {
            if(!ui.modshift) {
                for (NConfiguration.PickingAction pa : NConfiguration.getInstance().pickingActions) {
                    if(pa.isEnable) {
                        for (Petal p : opts) {
                            if (NUtils.checkName(p.name, pa.action)) {
                                choose(p);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public void cancel(){
        NUtils.getUI().wdgmsg(this,"cl", -1);
    }

    @Override
    public void choose(Petal option) {
        super.choose(option);
        if (!NUtils.getUI().botMode.get() && option!=null && NConfiguration.getInstance().autoFlower && NUtils.getUI().sessInfo.characterInfo.flowerCand != null) {
            NInventory inv;
            NGItem fc = NUtils.getUI().sessInfo.characterInfo.flowerCand;
            if(fc.parent instanceof NInventory) {
                Window parent = NUtils.findWinParent(fc);
                inv = (NInventory) fc.parent;
                ArrayList<GItem> targets = new ArrayList<>();
                try {
                if (fc.isSeached) {
                        for(GItem item : inv.getWItems())
                            if(((NGItem)item).isSeached)
                                targets.add(item);
                }
                else
                {
                    targets = inv.getGItems(new NAlias(fc.name()));
                }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                NUtils.getUI().botMode.set(true);
                new Thread(new AutoChooser(parent, targets, option.name)).start();
            }
        }
    }

    class AutoChooser implements Runnable{

        Window win;
        ArrayList<GItem> items;
        String action;
        @Override
        public void run() {
            try {
            win.winDisabler = true;
            win.dwdg.show();
            win.dwdg.resize(win.deco.sz);
            win.dwdg.raise();
            NUtils.waitEvent(() -> NUtils.getFlowerMenu() == null, 500);
            for(GItem item : items)
            {
                    if(!win.winDisabler)
                        break;
                    if(item.parent!=null) {
                        new SelectFlowerAction((NGItem) item, action, SelectFlowerAction.Types.Item).run(NUtils.getGameUI());
//                        NUtils.waitEvent(() -> item.parent == null || !((NInventory) item.parent).findItem(item), 500);
                    }

            }
            } catch (InterruptedException e) {
            }finally {
                win.winDisabler = false;
                win.dwdg.hide();
                NUtils.getUI().botMode.set(false);
            }
        }

        public AutoChooser(Window win, ArrayList<GItem> items, String action) {
            this.win = win;
            this.items = items;
            this.action = action;
        }
    }


}
