/* Preprocessed source code */
package haven.res.ui.tt.wear;

import haven.*;
import nurgling.NGItem;

import java.awt.*;
import java.awt.image.BufferedImage;

/* >tt: Wear */
@haven.FromResource(name = "ui/tt/wear", version = 4)
public class Wear extends ItemInfo.Tip implements GItem.OverlayInfo<Tex> {
    public final int d, m;
    public final double wear;

    public Wear(Owner owner, int d, int m) {
	super(owner);
	this.d = d;
	this.m = m;
    this.wear = ((double)d) /m;
    }

    public static ItemInfo mkinfo(Owner owner, Object... args) {
	return(new Wear(owner, (Integer)args[1], (Integer)args[2]));
    }

    public BufferedImage tipimg() {
	if(d >= m)
	    return(RichText.render(String.format("Wear: $col[255,128,128]{%,d/%,d}", d, m), 0).img);
	return(RichText.render(String.format("Wear: %,d/%,d", d, m)).img);
    }


    @Override
    public Tex overlay() {
        return null;
    }

    public void drawoverlay(GOut g, Tex data) {
        if(d!=0) {
            g.chcolor(new Color(198, 0, 0, (int) Math.round(190*wear)));
            g.frect(new Coord(0,  0), new Coord(g.sz().x,(int)Math.round(g.sz().y*wear)));
            g.chcolor();
        }
    }
}
