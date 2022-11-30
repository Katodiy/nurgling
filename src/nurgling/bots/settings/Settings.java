package nurgling.bots.settings;

import haven.Widget;

public class Settings extends Widget {
    @Override
    public void show() {
        for(Widget w: children())
            w.show();
        super.show();
    }
}
