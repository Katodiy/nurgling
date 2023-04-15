package nurgling;

import haven.FlowerMenu;
import haven.GOut;

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

    public void cancel(){
        NUtils.getUI().wdgmsg(this,"cl", -1);
    }
}
