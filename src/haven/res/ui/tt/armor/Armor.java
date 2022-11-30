package haven.res.ui.tt.armor;/* Preprocessed source code */

/* $use: ui/tt/armor */

import haven.*;
import nurgling.NGItem;

import java.awt.*;
import java.awt.image.BufferedImage;

/* >tt: haven.res.ui.tt.armor.Armor */
@FromResource(name = "ui/tt/armor", version = 4 , override = true)
public class Armor extends ItemInfo.Tip implements GItem.OverlayInfo<Tex> {
    public final int hard, soft;

    public Armor(Owner owner, int hard, int soft) {
	super(owner);
	this.hard = hard;
	this.soft = soft;
    }

    public static ItemInfo mkinfo(Owner owner, Object... args) {
	return(new Armor(owner, (Integer)args[1], (Integer)args[2]));
    }

    public BufferedImage tipimg() {
	return(Text.render(String.format("Armor class: %,d/%,d", hard, soft)).img);
    }
    public Tex overlay() {
        return(new TexI(NGItem.NumberInfo.numrender((int)Math.round(((NGItem)owner).m-((NGItem)owner).d), new Color(255, 255, 255, 255))));
    }

    public void drawoverlay(GOut g, Tex data) {
        if(data!=null) {
            g.chcolor(new Color(198, 0, 0, (int) Math.round(190*((NGItem)owner).wear)));
            g.frect(new Coord(0,  0), new Coord(g.sz().x,(int)Math.round(g.sz().y*(((NGItem)owner).wear))));
            g.chcolor();
//            g.aimage(data, new Coord(g.sz().x, g.sz().y), 1.0, 1.0);
        }
    }
}
