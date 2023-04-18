package haven.res.ui.tt.highlighting;

/* $use: ui/tt/wellMined */

import haven.*;
import nurgling.NGItem;
import nurgling.NUtils;

import java.util.ArrayList;

/* >tt: WellMined */
public class ItemHigh extends ItemInfo.Tip implements GItem.OverlayInfo<Tex> {
    NGItem higlighted;

    public ItemHigh(Owner owner) {
        super(owner);
        higlighted = (NGItem) owner;
    }

    public static ItemInfo mkinfo(Owner owner, Object... args) {
	return(new ItemHigh(owner));
    }

    public static Tex frame = Resource.loadtex("overlays/quests/frame");
    public static Tex mark = Resource.loadtex("overlays/quests/mark");


    @Override
    public Tex overlay() {
        return frame;
    }

    @Override
    public void drawoverlay(GOut g, Tex data)
    {
        if(higlighted.isQuested) {
            g.aimage(frame, Coord.z, 0, 0, g.sz());
            g.aimage(mark, Coord.z, 0, 0);
        }
    }
}
