package haven.res.ui.tt.gast;/* Preprocessed source code */
/* $use: ui/tt/wear */

import haven.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import haven.res.ui.tt.wear.Wear;
import nurgling.NGItem;

/* >tt: haven.res.ui.tt.gast.Gast */
@FromResource(name = "ui/tt/gast", version = 9)
public class Gast extends ItemInfo.Tip implements GItem.NumberInfo {
    public final double glut, fev;

    public Gast(Owner owner, double glut, double fev) {
	super(owner);
	this.glut = glut;
	this.fev = fev;
    }

    public static ItemInfo mkinfo(Owner owner, Object... args) {
	return(new Gast(owner, ((Number)args[1]).doubleValue(), ((Number)args[2]).doubleValue()));
    }

    public BufferedImage tipimg() {
	StringBuilder buf = new StringBuilder();
	if(glut != 1.0)
	    buf.append(String.format("Hunger reduction: %s%%\n", Utils.odformat2(100 * glut, 1)));
	if(fev != 1.0)
	    buf.append(String.format("Food event bonus: %s%%\n", Utils.odformat2(100 * fev, 1)));
	return(RichText.render(buf.toString(), 0).img);
    }

    public int itemnum() {
	Wear wear = find(Wear.class, owner.info());
	if(wear == null)
	    return(0);
	return(wear.m - wear.d);
    }

	public Tex overlay() {
		return(new TexI(NGItem.NumberInfo.numrender((int)Math.round(((NGItem)owner).m-((NGItem)owner).d), new Color(255, 255, 255, 255))));
	}

	public void drawoverlay(GOut g, Tex data) {
		if(data!=null) {
			g.chcolor(new Color(198, 0, 0, (int) Math.round(190*((NGItem)owner).wear)));
			g.frect(new Coord(0,  0), new Coord(g.sz().x,(int)Math.round(g.sz().y*(((NGItem)owner).wear))));
			g.chcolor();
			g.aimage(data, new Coord(g.sz().x, g.sz().y), 1.0, 1.0);
		}
	}
}
