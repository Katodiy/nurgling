package haven.res.ui.tt.wellmined;

/* $use: ui/tt/wellMined */

import haven.*;
import haven.res.ui.tt.q.qbuff.QBuff;
import nurgling.NCharacterInfo;
import nurgling.NGItem;
import nurgling.NUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/* >tt: WellMined */
public class WellMined extends ItemInfo.Tip implements GItem.OverlayInfo<Tex> {
    public WellMined(Owner owner) {
        super(owner);
    }

    public static ItemInfo mkinfo(Owner owner, Object... args) {
	return(new WellMined(owner));
    }

    static ArrayList<Tex> frames = new ArrayList<>();
    public static void init(){
        for(int i = 0;i<45 ; i++){
            frames.add(Resource.loadtex("overlays/items/frame/frame" + String.valueOf(i)));
        }
    }

    public static Tex wm_img = Resource.loadtex("overlays/items/wellmined");

    @Override
    public Tex overlay() {
        return wm_img;
    }

    @Override
    public void drawoverlay(GOut g, Tex data)
    {
        if(data!=null) {
            g.aimage(frames.get(((int)NUtils.getTickId()/2)%45), data.sz(), 1, 1);
        }
    }
}
