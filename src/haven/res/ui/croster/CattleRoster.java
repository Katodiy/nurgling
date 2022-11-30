/* Preprocessed source code */
package haven.res.ui.croster;

import haven.*;
import haven.render.*;

import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.*;
import haven.MenuGrid.Pagina;
import java.awt.Color;
import java.awt.image.BufferedImage;

@FromResource(name = "ui/croster", version = 72)
public abstract class CattleRoster <T extends Entry> extends Widget {
    public static final int WIDTH = UI.scale(900);
    public static final Comparator<Entry> namecmp = (a, b) -> a.name.compareTo(b.name);
    public static final int HEADH = UI.scale(40);
    public final Map<Long, T> entries = new HashMap<>();
    public final Scrollbar sb;
    public final Widget entrycont;
    public int entryseq = 0;
    public List<T> display = Collections.emptyList();
    public boolean dirty = true;
    public Comparator<? super T> order = namecmp;
    public Column mousecol, ordercol;
    public boolean revorder;

    public CattleRoster() {
	super(new Coord(WIDTH, UI.scale(400)));
	this.type = (Class<T>) ((ParameterizedType) getClass()
			.getGenericSuperclass()).getActualTypeArguments()[0];
	entrycont = add(new Widget(sz), 0, HEADH);
	sb = add(new Scrollbar(sz.y, 0, 0) {
		public void changed() {redisplay(display);}
	    }, sz.x, HEADH);
	Widget prev;
	prev = add(new Button(UI.scale(100), "Select all", false).action(() -> {
		    for(Entry entry : this.entries.values())
			entry.mark.set(true);
		}), entrycont.pos("bl").adds(0, 5));
	prev = add(new Button(UI.scale(100), "Select none", false).action(() -> {
		    for(Entry entry : this.entries.values())
			entry.mark.set(false);
		}), prev.pos("ur").adds(5, 0));
	adda(new Button(UI.scale(150), "Remove selected", false).action(() -> {
	    Collection<Object> args = new ArrayList<>();
	    for(Entry entry : this.entries.values()) {
		if(entry.mark.a) {
		    args.add(Integer.valueOf((int)(entry.id & 0x00000000ffffffffl)));
		    args.add(Integer.valueOf((int)((entry.id & 0xffffffff00000000l) >> 32)));
		}
	    }
	    wdgmsg("rm", args.toArray(new Object[0]));
	}), entrycont.pos("br").adds(0, 5), 1, 0);
	pack();
    }

    public static <E extends Entry>  List<Column> initcols(Column... attrs) {
	for(int i = 0, x = CheckBox.sbox.sz().x + UI.scale(10); i < attrs.length; i++) {
	    Column attr = attrs[i];
	    attr.x = x;
	    x += attr.w;
	    x += UI.scale(attr.r ? 5 : 1);
	}
	return(Arrays.asList(attrs));
    }

    public void redisplay(List<T> display) {
	Set<T> hide = new HashSet<>(entries.values());
	int h = 0, th = entrycont.sz.y;
	for(T entry : display)
	    h += entry.sz.y;
	sb.max = h - th;
	int y = -sb.val, idx = 0;
	for(T entry : display) {
	    entry.idx = idx++;
	    if((y + entry.sz.y > 0) && (y < th)) {
		entry.move(new Coord(0, y));
		entry.show();
	    } else {
		entry.hide();
	    }
	    hide.remove(entry);
	    y += entry.sz.y;
	}
	for(T entry : hide)
	    entry.hide();
	this.display = display;
    }

    public void tick(double dt) {
	if(dirty) {
	    List<T> ndisp = new ArrayList<>(entries.values());
	    ndisp.sort(order);
	    redisplay(ndisp);
	    dirty = false;
	}
	super.tick(dt);
    }

    protected abstract List<Column> cols();

    public void drawcols(GOut g) {
	Column prev = null;
	for(Column col : cols()) {
	    if((prev != null) && !prev.r) {
		g.chcolor(255, 255, 0, 64);
		int x = (prev.x + prev.w + col.x) / 2;
		g.line(new Coord(x, 0), new Coord(x, sz.y), 1);
		g.chcolor();
	    }
	    if((col == mousecol) && (col.order != null)) {
		g.chcolor(255, 255, 0, 16);
		g.frect2(new Coord(col.x, 0), new Coord(col.x + col.w, sz.y));
		g.chcolor();
	    }
	    if(col == ordercol) {
		g.chcolor(255, 255, 0, 16);
		g.frect2(new Coord(col.x, 0), new Coord(col.x + col.w, sz.y));
		g.chcolor();
	    }
	    Tex head = col.head();
	    g.aimage(head, new Coord(col.x + (col.w / 2), HEADH / 2), 0.5, 0.5);
	    prev = col;
	}
    }

    public void draw(GOut g) {
	drawcols(g);
	super.draw(g);
    }

    public Column onhead(Coord c) {
	if((c.y < 0) || (c.y >= HEADH))
	    return(null);
	for(Column col : cols()) {
	    if((c.x >= col.x) && (c.x < col.x + col.w))
		return(col);
	}
	return(null);
    }

    public void mousemove(Coord c) {
	super.mousemove(c);
	mousecol = onhead(c);
    }

    public boolean mousedown(Coord c, int button) {
	Column col = onhead(c);
	if(button == 1) {
	    if((col != null) && (col.order != null)) {
		revorder = (col == ordercol) ? !revorder : false;
		this.order = col.order;
		if(revorder)
		    this.order = this.order.reversed();
		ordercol = col;
		dirty = true;
		return(true);
	    }
	}
	return(super.mousedown(c, button));
    }

    public boolean mousewheel(Coord c, int amount) {
	sb.ch(amount * UI.scale(15));
	return(true);
    }

    public Object tooltip(Coord c, Widget prev) {
	if(mousecol != null)
	    return(mousecol.tip);
	return(super.tooltip(c, prev));
    }

    public void addentry(T entry) {
	entries.put(entry.id, entry);
	entrycont.add(entry, Coord.z);
	dirty = true;
	entryseq++;
    }

    public void delentry(long id) {
	T entry = entries.remove(id);
	entry.destroy();
	dirty = true;
	entryseq++;
    }

    public void delentry(T entry) {
	delentry(entry.id);
    }

    public abstract T parse(Object... args);

    public void uimsg(String msg, Object... args) {
	if(msg == "add") {
	    addentry(parse(args));
	} else if(msg == "upd") {
	    T entry = parse(args);
	    delentry(entry.id);
	    addentry(entry);
	} else if(msg == "rm") {
	    delentry((Long)args[0]);
	} else if(msg == "addto") {
	    GameUI gui = (GameUI)ui.getwidget((Integer)args[0]);
	    Pagina pag = gui.menu.paginafor(ui.sess.getres((Integer)args[1]));
	    RosterButton btn = (RosterButton)Loading.waitfor(pag::button);
	    btn.add(this);
	} else {
	    super.uimsg(msg, args);
	}
    }

    public abstract TypeButton button();

    public static TypeButton typebtn(Indir<Resource> up, Indir<Resource> dn) {
	Resource ur = Loading.waitfor(() -> up.get());
	Resource.Image ui = ur.layer(Resource.imgc);
	Resource.Image di = Loading.waitfor(() -> dn.get()).layer(Resource.imgc);
	TypeButton ret = new TypeButton(ui.scaled(), di.scaled(), ui.z);
	Resource.Tooltip tip = ur.layer(Resource.tooltip);
	if(tip != null)
	    ret.settip(tip.t);
	return(ret);
    }


	private final Class<T> type;

	public Class<T> getGenType() {
		return this.type;
	}
}
