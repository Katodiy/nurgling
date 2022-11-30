/* Preprocessed source code */
package haven.res.ui.tt.wear;

import haven.*;
import nurgling.NGItem;

import java.awt.image.BufferedImage;

/* >tt: Wear */
@FromResource(name = "ui/tt/wear", version = 3)
public class Wear extends ItemInfo.Tip {
    public final int d, m;

    public Wear(Owner owner, int d, int m) {
	super(owner);
	this.d = d;
	this.m = m;
    if(owner instanceof GItem) {
        ((NGItem) owner).wear = ((double)d) /m;
        ((NGItem) owner).d = d;
        ((NGItem) owner).m = m;
    }
    }

    public static ItemInfo mkinfo(Owner owner, Object... args) {
	return(new Wear(owner, (Integer)args[1], (Integer)args[2]));
    }

    public BufferedImage tipimg() {
	return(Text.render(String.format("Wear: %,d/%,d", d, m)).img);
    }
}
