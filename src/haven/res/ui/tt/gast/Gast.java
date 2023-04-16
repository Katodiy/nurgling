package haven.res.ui.tt.gast;/* Preprocessed source code */
/* $use: ui/tt/wear */

import haven.*;
import haven.res.ui.tt.wear.Wear;

import java.awt.image.BufferedImage;

/* >tt: Gast */
@FromResource(name = "ui/tt/gast", version = 10)
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
	if(glut != 0.0)
	    buf.append(String.format("Hunger reduction: %s%%\n", Utils.odformat2(100 * glut, 1)));
	if(fev != 0.0)
	    buf.append(String.format("Food event bonus: %s%%\n", Utils.odformat2(100 * fev, 1)));
	return(RichText.render(buf.toString(), 0).img);
    }

    public int itemnum() {
	Wear wear = find(Wear.class, owner.info());
	if(wear == null)
	    return(0);
	return(wear.m - wear.d);
    }
}
