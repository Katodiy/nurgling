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
    }



}
