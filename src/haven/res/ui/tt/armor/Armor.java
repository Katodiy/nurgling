package haven.res.ui.tt.armor;/* Preprocessed source code */

/* $use: ui/tt/armor */

import haven.*;
import nurgling.NGItem;

import java.awt.*;
import java.awt.image.BufferedImage;

/* >tt: haven.res.ui.tt.armor.Armor */
@FromResource(name = "ui/tt/armor", version = 4 , override = true)
public class Armor extends ItemInfo.Tip{
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
}
