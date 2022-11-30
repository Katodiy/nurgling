/* Preprocessed source code */
package haven.res.ui.croster;

import haven.*;
import haven.render.*;
import java.util.*;
import java.util.function.*;
import haven.MenuGrid.Pagina;
import java.awt.Color;
import java.awt.image.BufferedImage;

@FromResource(name = "ui/croster", version = 72)
public class Entry extends Widget {
    public static final int WIDTH = CattleRoster.WIDTH;
    public static final int HEIGHT = UI.scale(20);
    public static final Coord SIZE = new Coord(WIDTH, HEIGHT);
    public static final Color every = new Color(255, 255, 255, 16), other = new Color(255, 255, 255, 32);
    public static final Function<Integer, String> percent = v -> String.format("%d%%", v);
    public static final Function<Number, String> quality = v -> Long.toString(Math.round(v.doubleValue()));
    public static final Function<Entry, Tex> namerend = e -> {
	return(CharWnd.attrf2.render(e.name, BuddyWnd.gc[e.grp]).tex());
    };
    public static final Tex male = new TexI(Resource.loadsimg("hud/rosters/male"));
    public static final Tex female = new TexI(Resource.loadsimg("hud/rosters/female"));
    public static final Function<Boolean, Tex> sex = v -> (v ? male : female);
    public static final Tex adult = new TexI(Resource.loadsimg("hud/rosters/adult"));
    public static final Tex child = new TexI(Resource.loadsimg("hud/rosters/child"));
    public static final Function<Boolean, Tex> growth = v -> (v ? child : adult);
    public static final Tex dead  = new TexI(Resource.loadsimg("hud/rosters/dead"));
    public static final Tex alive  = new TexI(Resource.loadsimg("hud/rosters/alive"));

    public static final Function<Boolean, Tex> deadrend = v -> (v ? dead : alive);
    public static final Tex pregy  = new TexI(Resource.loadsimg("hud/rosters/pregnant-y"));
    public static final Tex pregn  = new TexI(Resource.loadsimg("hud/rosters/pregnant-n"));
    public static final Function<Boolean, Tex> pregrend = v -> (v ? pregy : pregn);
    public static final Tex lacty  = new TexI(Resource.loadsimg("hud/rosters/lactate-y"));
    public static final Tex lactn  = new TexI(Resource.loadsimg("hud/rosters/lactate-n"));
    public static final Function<Boolean, Tex> lactrend = v -> (v ? lacty : lactn);
    public static final Tex ownedn  = new TexI(Resource.loadsimg("hud/rosters/owned-n"));
    public static final Tex ownedo  = new TexI(Resource.loadsimg("hud/rosters/owned-o"));
    public static final Tex ownedm  = new TexI(Resource.loadsimg("hud/rosters/owned-m"));
    public static final Function<Integer, Tex> ownrend = v -> ((v == 3) ? ownedm : ((v == 1) ? ownedo : ownedn));
    public final long id;
    public String name;
    public int grp;
    public double q;
    public int idx;
    public CheckBox mark;

    public Entry(Coord sz, long id, String name) {
	super(sz);
	this.id = id;
	this.name = name;
	this.mark = adda(new CheckBox(""), UI.scale(5), sz.y / 2, 0, 0.5);
    }

    protected void drawbg(GOut g) {
	g.chcolor(((idx & 1) == 0) ? every : other);
	g.frect(Coord.z, sz);
	g.chcolor();
    }

    private Tex[] rend = {};
    private Object[] rendv = {};
    public <V> void drawcol(GOut g, Column<?> col, double a, V val, Function<? super V, ?> fmt, int idx) {
	if(fmt == null) fmt = Function.identity();
	if(rend.length <= idx) {
	    rend = Arrays.copyOf(rend, idx + 1);
	    rendv = Arrays.copyOf(rendv, idx + 1);
	}
	if(!Utils.eq(rendv[idx], val)) {
	    if(rend[idx] != null)
		rend[idx].dispose();
	    Object rval = fmt.apply(val);
	    if(rval instanceof Tex)
		rend[idx] = (Tex)rval;
	    else
		rend[idx] = CharWnd.attrf.render(String.valueOf(rval)).tex();
	    rendv[idx] = val;
	}
	Coord sz = rend[idx].sz();
	g.image(rend[idx], new Coord(col.x + (int)Math.round((col.w - sz.x) * a), (this.sz.y - sz.y) / 2));
    }

    public boolean mousedown(Coord c, int button) {
	if(super.mousedown(c, button))
	    return(true);
	getparent(CattleRoster.class).wdgmsg("click", (int)(id & 0x00000000ffffffffl), (int)((id & 0xffffffff00000000l) >> 32), button, ui.modflags(), ui.mc);
	return(true);
    }

    public <T extends Entry> void markall(Class<T> type, Predicate<? super T> p) {
	for(T ent : getparent(CattleRoster.class).children(type)) {
	    if(p.test(ent))
		ent.mark.click();
	}
    }
}
