/* Preprocessed source code */
package haven.res.ui.tt.attrmod;

import haven.*;
import haven.res.lib.tspec.Spec;
import nurgling.NEquipory;

import static haven.PUtils.*;
import java.util.*;
import java.awt.image.BufferedImage;

/* >tt: AttrMod$haven.res.ui.tt.slots.Fac */
@FromResource(name = "ui/tt/attrmod", version = 6)
public class AttrMod extends ItemInfo.Tip {
    public final Collection<Mod> mods;

    public static class Mod {
	public final Resource attr;
	public int mod;

	public Mod(Resource attr, int mod) {this.attr = attr; this.mod = mod;}
    }

    public AttrMod(Owner owner, Collection<Mod> mods) {
	super(owner);
	this.mods = mods;
		if(owner instanceof Spec) {
			if(((Spec) owner).ctx instanceof GItem && ((GItem)((Spec) owner).ctx).parent instanceof Equipory) {
				((NEquipory)((GItem)((Spec) owner).ctx).parent).updBuffs = true;
			}
		}
    }

    public static class Fac implements InfoFactory {
	public ItemInfo build(Owner owner, Object... args) {
	    Resource.Resolver rr = owner.context(Resource.Resolver.class);
	    Collection<Mod> mods = new ArrayList<Mod>();
	    for(int a = 1; a < args.length; a += 2)
		mods.add(new Mod(rr.getres((Integer)args[a]).get(), (Integer)args[a + 1]));
	    return(new AttrMod(owner, mods));
	}
    }

    public static String buff = "128,255,128";
	public static String debuff = "255,128,128";
    public static BufferedImage modimg(Collection<Mod> mods) {
	Collection<BufferedImage> lines = new ArrayList<BufferedImage>(mods.size());
	for(Mod mod : mods) {
	    BufferedImage line = RichText.render(String.format("%s $col[%s]{%s%d}", mod.attr.layer(Resource.tooltip).t,
							       (mod.mod < 0)?debuff:buff, (mod.mod < 0)?'-':'+', Math.abs(mod.mod)),
						 0).img;
	    BufferedImage icon = convolvedown(mod.attr.layer(Resource.imgc).img,
					      new Coord(line.getHeight(), line.getHeight()),
					      CharWnd.iconfilter);
	    lines.add(catimgsh(0, icon, line));
	}
	return(catimgs(0, lines.toArray(new BufferedImage[0])));
    }

    public BufferedImage tipimg() {
	return(modimg(mods));
    }
}
