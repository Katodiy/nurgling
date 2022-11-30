package haven.res.ui.tt.leashed;/* Preprocessed source code */
import haven.*;

import java.awt.*;
import java.awt.image.BufferedImage;

/* >tt: Leashed */
@FromResource(name = "ui/tt/leashed", version = 3)
public class Leashed extends ItemInfo implements GItem.OverlayInfo<Tex> {
//    public static final Resource.Image img = Resource.classres(Leashed.class).layer(Resource.imgc);
    private static Tex iconLeshed = new TexI(Resource.loadsimg("icon/leashed"));

    public Leashed(Owner owner) {
	super(owner);
    }

    public static ItemInfo mkinfo(Owner owner, Object... args) {
	return(new Leashed(owner));
    }

    public Tex overlay() {
        return(iconLeshed);
    }

    public void drawoverlay(GOut g, Tex ol) {
//	g.image(ol, Coord.z);
//    g.chcolor(new Color(0, 0, 0, 115));
//    g.frect(new Coord(0,  ol.sz().y), ol.sz());
//    g.chcolor();
    g.aimage(iconLeshed,new Coord(0,  ol.sz().y), 0, 0);
    }
}
