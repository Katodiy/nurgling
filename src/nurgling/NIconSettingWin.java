package nurgling;

import haven.*;

public class NIconSettingWin extends GobIcon.SettingsWindow {
    public NIconSettingWin(GobIcon.Settings conf, Runnable save) {
        super(conf, save);
        list = cont.last(new NIconList(UI.scale(250, 500)), 0);
    }

    public class NIconList extends IconList {

        public class IconLine extends ItemWidget<Icon> {
            public IconLine(Coord sz, Icon icon) {
                super(NIconList.this, sz, icon);
                Widget prev;
                prev = adda(new CheckBox("").state(() -> icon.conf.notify).set(andsave(val -> icon.conf.notify = val)).settip("Notify"),
                        sz.x - UI.scale(2) - (sz.y / 2), sz.y / 2, 0.5, 0.5);
                prev = adda(new CheckBox("").state(() -> icon.conf.show).set(andsave(val -> icon.conf.show = val)).settip("Display"),
                        prev.c.x - UI.scale(2) - (sz.y / 2), sz.y / 2, 0.5, 0.5);

                add(IconText.of(Coord.of(prev.c.x - UI.scale(2), sz.y), () -> item.conf.res.loadsaved(Resource.remote())), Coord.z);
            }
        }

        protected NIconList(Coord sz) {
            super(sz);
        }
    }




}
