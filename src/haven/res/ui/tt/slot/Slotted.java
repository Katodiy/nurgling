/* Preprocessed source code */
package haven.res.ui.tt.slot;

import haven.*;
import haven.res.ui.tt.attrmod.AttrMod;

import static haven.PUtils.*;

import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.util.List;

/* >tt: Slotted */
@haven.FromResource(name = "ui/tt/slot", version = 18)
public class Slotted extends ItemInfo.Tip implements GItem.OverlayInfo<Tex> {
	public static final Coord size = UI.scale(new Coord(33,33));
    public static final Text.Line ch = Text.render("As gilding:");
    public final double pmin, pmax;
    public final Resource[] attrs;
    public final List<ItemInfo> sub;

    public Slotted(Owner owner, double pmin, double pmax, Resource[] attrs, List<ItemInfo> sub) {
	super(owner);
	this.pmin = pmin;
	this.pmax = pmax;
	this.attrs = attrs;
	this.sub = sub;
    }

    public static ItemInfo mkinfo(Owner owner, Object... args) {
	Resource.Resolver rr = owner.context(Resource.Resolver.class);
	int a = 1;
	double pmin = ((Number)args[a++]).doubleValue();
	double pmax = ((Number)args[a++]).doubleValue();
	List<Resource> attrs = new LinkedList<Resource>();
	while(args[a] instanceof Integer)
	    attrs.add(rr.getres((Integer)args[a++]).get());
	Object[] raw = (Object[])args[a++];
	return(new Slotted(owner, pmin, pmax, attrs.toArray(new Resource[0]), buildinfo(owner, raw)));
    }

    public static final String chc = "192,192,255";
    public void layout(Layout l) {
	l.cmp.add(ch.img, new Coord(0, l.cmp.sz.y));
	if(attrs.length > 0) {
	    BufferedImage head = RichText.render(String.format("Chance: $col[%s]{%d%%} to $col[%s]{%d%%}", chc, Math.round(100 * pmin), chc, Math.round(100 * pmax)), 0).img;
	    int h = head.getHeight();
	    int x = 10, y = l.cmp.sz.y;
	    l.cmp.add(head, new Coord(x, y));
	    x += head.getWidth() + 10;
	    for(int i = 0; i < attrs.length; i++) {
		BufferedImage icon = convolvedown(attrs[i].layer(Resource.imgc).img, new Coord(h, h), CharWnd.iconfilter);
		l.cmp.add(icon, new Coord(x, y));
		x += icon.getWidth() + 2;
	    }
	} else {
	    BufferedImage head = RichText.render(String.format("Chance: $col[%s]{%d%%}", chc, (int)Math.round(100 * pmin)), 0).img;
	    l.cmp.add(head, new Coord(10, l.cmp.sz.y));
	}

	BufferedImage stip = longtip(sub);
	if(stip != null)
	    l.cmp.add(stip, new Coord(10, l.cmp.sz.y));
    }

    public int order() {
	return(200);
    }

	@Override
	public Tex overlay() {
		Collection<BufferedImage> imgs = new LinkedList<BufferedImage>();
		for(ItemInfo info: sub)
		{
			if (info instanceof AttrMod)
			{
				AttrMod mod = (AttrMod) info;
				for ( AttrMod.Mod m: mod.mods)
				{
					imgs.add(convolvedown(m.attr.layer(Resource.imgc).img, size.div(2 *  (1 + (mod.mods.size()-1)/4.)), CharWnd.iconfilter));
				}
			}
		}

		if(imgs.size()>3)
		{
			ArrayList<BufferedImage> for_connect = new ArrayList<>(imgs);
			imgs.clear();
			for(int i = 0; i < for_connect.size(); i+=2) {
				if (i + 1 < for_connect.size()) {
					imgs.add(catimgsh(0, for_connect.get(i), for_connect.get(i+1)));
				} else
					imgs.add(for_connect.get(i));
			}
		}
		BufferedImage lay = catimgs(0, imgs.toArray(new BufferedImage[0]));
		BufferedImage bi = new BufferedImage(lay.getWidth(), lay.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = bi.createGraphics();
//		Color rgb =  new Color(0,0,0, 161);
//		graphics.setColor (rgb);
//		graphics.fillRect ( 0, 0, bi.getWidth(), bi.getHeight());
		graphics.drawImage(lay, 0, 0, null);
		return(new TexI(bi));
	}

	@Override
	public void drawoverlay(GOut g, Tex data) {
		g.aimage(data, new Coord(data.sz().x, g.sz().y - data.sz().y), 1, 0);
	}
}
