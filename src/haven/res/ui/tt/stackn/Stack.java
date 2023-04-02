package haven.res.ui.tt.stackn;/* Preprocessed source code */
import haven.*;
import nurgling.NGItem;

import java.awt.*;
import java.awt.image.BufferedImage;

/* >tt: Stack */
@FromResource(name = "ui/tt/stackn", version = 4)
public class Stack extends ItemInfo.Name implements GItem.OverlayInfo<Tex> {

	public Stack(Owner owner, String str) {
		super(owner, str);

	}


	public Tex overlay() {
		int count = 0;
		double q = 0;
		NGItem item = (NGItem)owner;
		if(item.contents != null) {
			for (Widget ch : item.contents.children()) {
				if (ch instanceof NGItem) {
					q+=((NGItem)ch).quality();
					count++;
				}
			}
		}
		if(count>0) {
			q = q / count;
			BufferedImage text = GItem.NumberInfo.numrender((int) Math.round(q), new Color(245, 245, 65, 255));
			BufferedImage bi = new BufferedImage(text.getWidth(), text.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = bi.createGraphics();
			Color rgb = new Color(0, 0, 0, 115);
			graphics.setColor(rgb);
			graphics.fillRect(0, 0, bi.getWidth(), bi.getHeight());
			graphics.drawImage(text, 0, 0, null);
			return (new TexI(bi));
		}
		return null;
	}

	public void drawoverlay(GOut g, Tex ol) {
		if(ol!=null)
			g.aimage(ol, new Coord(g.sz().x - ol.sz().x, ol.sz().y), 0, 1);
	}
}
