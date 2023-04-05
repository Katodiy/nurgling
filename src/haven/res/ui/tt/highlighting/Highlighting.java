package haven.res.ui.tt.highlighting;

/* $use: ui/tt/wellMined */

import haven.*;
import nurgling.NGItem;
import nurgling.NUtils;

import java.util.ArrayList;

/* >tt: WellMined */
public class Highlighting extends ItemInfo.Tip implements GItem.OverlayInfo<Tex> {
    NGItem higlighted;
    public Highlighting(Owner owner) {
        super(owner);
        higlighted = (NGItem) owner;
    }

    public static ItemInfo mkinfo(Owner owner, Object... args) {
	return(new Highlighting(owner));
    }

    static ArrayList<Tex> frames = new ArrayList<>();
    public static void init(){
        for(int i = 0;i<45 ; i++){
            frames.add(Resource.loadtex("overlays/items/frame/frame" + String.valueOf(i)));
        }
    }


    @Override
    public Tex overlay() {
        return frames.get(0);
    }

    @Override
    public void drawoverlay(GOut g, Tex data)
    {
        if(higlighted.isSeached)
            g.aimage(frames.get(((int)NUtils.getTickId()/2)%45),Coord.z, 0, 0,g.sz());
    }
}
