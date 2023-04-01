package haven.res.ui.inputch;/* Preprocessed source code */
/* $use: lib/tspec */

import haven.*;
import java.util.*;
import haven.res.lib.tspec.Spec;
import static haven.Inventory.invsq;

/* >wdg: haven.res.ui.inputch.InputChooser */
@haven.FromResource(name = "ui/inputch", version = 64)
public class InputChooser extends Window {
    public static int itemh = invsq.sz().y + UI.scale(5);
    public final Spec[] inputs;
    private final Text[] rnm;
    public int sel = 0;

    public InputChooser(Spec[] inputs) {
	super(Coord.z, "Choose type");
	this.rnm = new Text[inputs.length];
	this.inputs = inputs;
	adda(new Button(UI.scale(75), "Choose") {
		public void click() {
		    InputChooser.this.wdgmsg("ch", sel);
		}
	    }, new Coord(UI.scale(250), place(inputs.length).y), 1.0, 0.0);
	pack();
    }

    public static Widget mkwidget(UI ui, Object... args) {
	Glob glob = ui.sess.glob;
	int a = 0;
	List<Spec> buf = new ArrayList<Spec>(args.length);
	while(a < args.length) {
	    Indir<Resource> res = ui.sess.getres((Integer)args[a++]);
	    Message sdt = Message.nil;
	    if((a < args.length) && (args[a] instanceof byte[]))
		sdt = new MessageBuf((byte[])args[a++]);
	    Object[] info = null;
	    if((a < args.length) && (args[a] instanceof Object[])) {
		info = new Object[0][];
		while((a < args.length) && (args[a] instanceof Object[]))
		    info = Utils.extend(info, args[a++]);
	    }
	    buf.add(new Spec(new ResData(res, sdt), Spec.uictx(ui), info));
	}
	return(new InputChooser(buf.toArray(new Spec[0])));
    }

    private Coord place(int i) {
	return(new Coord(0, i * itemh));
    }

    public void cdraw(GOut g) {
	for(int i = 0; i < inputs.length; i++) {
	    Spec input = inputs[i];
	    Coord c = place(i);
	    if(sel == i)
		g.chcolor(128, 128, 255, 255);
	    else
		g.chcolor();
	    g.image(invsq, c);
	    try {
		input.spr().draw(g.reclip(c, invsq.sz()));
	    } catch(Loading l) {
		g.image(WItem.missing.layer(Resource.imgc).tex(), c, invsq.sz());
	    }
	    try {
		if(rnm[i] == null)
		    rnm[i] = Text.render(input.name());
		g.aimage(rnm[i].tex(), c.add(invsq.sz().x + UI.scale(5), invsq.sz().y / 2), 0.0, 0.5);
	    } catch(Loading l) {}
	}
    }

    public boolean mousedown(Coord c, int btn) {
	Coord ic = xlate(c, false);
	for(int i = 0; i < inputs.length; i++) {
	    if(ic.isect(place(i), invsq.sz())) {
		sel = i;
		return(true);
	    }
	}
	return(super.mousedown(c, btn));
    }
}
