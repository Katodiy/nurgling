package nurgling;

import haven.*;
import haven.res.ui.tt.highlighting.Highlighting;
import haven.res.ui.tt.slot.Slotted;
import haven.res.ui.tt.wellmined.WellMined;

public class NUI extends UI {
    public NSessInfo sessInfo;
    public NDataTables dataTables;
    long tickId = 0;

    public NUI(Context uictx, Coord sz, Runner fun) {
        super(uictx, sz, fun);
        NUtils.setUI(this);
        dataTables = new NDataTables();
        Highlighting.init();
        Slotted.init();
        NFoodInfo.init();
    }

    @Override
    public void tick() {
        super.tick();
        tickId += 1;
        if (sessInfo == null && sess != null) {
            sessInfo = new NSessInfo(sess.username);
        }
        if (NUtils.getGameUI() == null && sessInfo != null) {
            for (Widget wdg : widgets.values()) {
                if (wdg instanceof Img) {
                    Img img = (Img) wdg;
                    if (img.tooltip instanceof Widget.KeyboundTip) {
                        if (!sessInfo.isVerified && ((Widget.KeyboundTip) img.tooltip).base.contains("Verif"))
                            sessInfo.isVerified = true;
                        else if (!sessInfo.isSubscribed && ((Widget.KeyboundTip) img.tooltip).base.contains("Subsc"))
                            sessInfo.isSubscribed = true;
                    }
                }
            }
        }
    }

    public long getTickId () {
        return tickId;
    }

    public Widget findInRoot(Class<?> c)
    {
        for(Widget wdg: root.children()){
            if(wdg.getClass()==c)
            {
                return wdg;
            }
        }
        return null;
    }

    public float getDeltaZ() {
        return (float)Math.sin(tickId/10.)*1;
    }

}
