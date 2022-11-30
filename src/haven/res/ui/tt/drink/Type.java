/* Preprocessed source code */
/* $use: lib/tspec */

package haven.res.ui.tt.drink;

import haven.*;
import haven.res.lib.tspec.*;
import java.awt.image.BufferedImage;

@FromResource(name = "ui/tt/drink", version = 1)
public class Type {
    public final BufferedImage img;
    public final String nm;
    public final double m;

    public Type(BufferedImage img, String nm, double m) {
	this.img = img;
	this.nm = nm;
	this.m = m;
    }

    public static Type make(ItemInfo.Owner owner, ResData sdt, double m) {
	Spec spec = new Spec(sdt, owner, null);
	GSprite spr = spec.spr();
	BufferedImage img;
	if(spr instanceof GSprite.ImageSprite)
	    img = ((GSprite.ImageSprite)spr).image();
	else
	    img = spec.res.res.get().layer(Resource.imgc).img;
	return(new Type(img, spec.name(), m));
    }
}

/* >tt: Drink */
