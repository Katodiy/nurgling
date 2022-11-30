/* Preprocessed source code */
/* $use: lib/tspec */

package haven.res.ui.tt.drink;

import haven.*;
import haven.res.lib.tspec.*;
import java.awt.image.BufferedImage;

@FromResource(name = "ui/tt/drink", version = 1)
public class Drink extends ItemInfo.Tip {
    public final Type[] types;

    public Drink(Owner owner, Type[] types) {
	super(owner);
	this.types = types;
    }

    public static ItemInfo mkinfo(Owner owner, Object... args) {
	Resource.Resolver rr = owner.context(Resource.Resolver.class);
	int a = 1;
	int nt = (args.length - a) / 2;
	Type[] types = new Type[nt];
	for(int i = 0; a < args.length; i++, a += 2) {
	    ResData sdt = new ResData(rr.getres((Integer)args[a + 0]), Message.nil);
	    double m = ((Number)args[a + 1]).doubleValue();
	    types[i] = Type.make(owner, sdt, m);
	}
	return(new Drink(owner, types));
    }

    public void layout(Layout l) {
	for(Type type : types) {
	    BufferedImage lbl = Text.render(String.format("%s: +%d%%", type.nm, Math.round(type.m * 100))).img;
	    BufferedImage icon = PUtils.convolvedown(type.img, new Coord(lbl.getHeight(), lbl.getHeight()), CharWnd.iconfilter);
	    int y = l.cmp.sz.y;
	    l.cmp.add(icon, new Coord(0, y));
	    l.cmp.add(lbl, new Coord(icon.getWidth() + 3, y));
	}
    }
}
