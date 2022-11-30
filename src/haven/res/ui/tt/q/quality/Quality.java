/* Preprocessed source code */
package haven.res.ui.tt.q.quality;

/* $use: ui/tt/q/qbuff */
import haven.*;
import haven.res.ui.tt.q.qbuff.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import haven.MenuGrid.Pagina;
import nurgling.NGItem;

/* >tt: Quality */
@FromResource(name = "ui/tt/q/quality", version = 23, override = true)
public class Quality extends QBuff implements GItem.OverlayInfo<Tex> {
    public static boolean show = false;
    private static BufferedImage icon = Resource.remote().loadwait("ui/tt/q/quality").layer(Resource.imgc, 0).scaled();

    public Quality(Owner owner, double q) {
	super(owner, icon, "Quality", q);
    }

    public static ItemInfo mkinfo(Owner owner, Object... args) {
	return(new Quality(owner, ((Number)args[1]).doubleValue()));
    }

    public Tex overlay() {
        ItemInfo cont = null;
        if((cont = ((NGItem)owner).getInfo(Contents.class)) != null){
            return(new TexI(GItem.NumberInfo.numrender((int)Math.round(((Contents)cont).getQuality()), new Color(165, 165, 255, 255))));
        }
	    return(new TexI(GItem.NumberInfo.numrender((int)Math.round(q), new Color(128, 255, 255, 255))));
    }

    public void drawoverlay(GOut g, Tex ol) {
        g.chcolor(new Color(0, 0, 0, 115));
        g.frect(new Coord(g.sz().x-ol.sz().x,  0), ol.sz());
        g.chcolor();
        g.aimage(ol, new Coord(g.sz().x-ol.sz().x,  ol.sz().y), 0, 1);
    }
}
