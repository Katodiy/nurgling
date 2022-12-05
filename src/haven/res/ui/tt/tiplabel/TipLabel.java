package haven.res.ui.tt.tiplabel;/* Preprocessed source code */
import haven.*;
import java.awt.image.BufferedImage;
import java.util.*;

/* >wdg: haven.res.ui.tt.lbl.TipLabel */
@haven.FromResource(name = "ui/tt-lbl", version = 1)
public class TipLabel extends Widget implements ItemInfo.Owner {
    public final boolean shortvar;
    public ItemInfo.Raw rawinfo = null;
    public List<ItemInfo> info = Collections.emptyList();
    private Tex rendered;

    public TipLabel(boolean shortvar) {
	super(Coord.z);
	this.shortvar = shortvar;
    }

    public static TipLabel mkwidget(UI ui, Object... args) {
	boolean shortvar = (args.length > 0) && (((Integer)args[0]) != 0);
	return(new TipLabel(shortvar));
    }

    private static final OwnerContext.ClassResolver<TipLabel> ctxr = new OwnerContext.ClassResolver<TipLabel>()
	.add(Glob.class, wdg -> wdg.ui.sess.glob)
	.add(Session.class, wdg -> wdg.ui.sess);
    public <T> T context(Class<T> cl) {return(ctxr.context(cl, this));}

    public List<ItemInfo> info() {
	if(info == null)
	    info = ItemInfo.buildinfo(this, rawinfo);
	return(info);
    }

    public void tick(double dt) {
	if((rendered == null) && (rawinfo != null)) {
	    try {
		BufferedImage img;
		if(shortvar)
		    img = ItemInfo.shorttip(info());
		else
		    img = ItemInfo.longtip(info());
		rendered = new TexI(img);
		resize(rendered.sz());
	    } catch(Loading l) {
	    }
	}
    }

    public void draw(GOut g) {
	if(rendered != null)
	    g.image(rendered, Coord.z);
	super.draw(g);
    }

    public void uimsg(String name, Object... args) {
	if(name == "set") {
	    rawinfo = new ItemInfo.Raw(args);
	    info = null;
	    rendered = null;
	    tick(0);
	} else {
	    super.uimsg(name, args);
	}
    }
}
