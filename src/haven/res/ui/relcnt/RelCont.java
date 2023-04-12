package haven.res.ui.relcnt;/* Preprocessed source code */
import haven.*;
import java.util.*;
import java.util.function.*;

/* >wdg: haven.res.ui.tt.relcnt.RelCont */
@haven.FromResource(name = "ui/relcnt", version = 1)
public class RelCont extends Widget {
    public final List<Pair<Widget, Supplier<Coord>>> childpos = new ArrayList<>();
    public final boolean packpar;
    private boolean packed = false;

    public RelCont(boolean packpar) {
	super(Coord.z);
	this.packpar = packpar;
    }

    public static RelCont mkwidget(UI ui, Object... args) {
	boolean packpar = (args.length > 0) && (((Integer)args[0]) != 0);
	return(new RelCont(packpar));
    }

    public void addchild(Widget child, Object... args) {
	Supplier<Coord> pos;
	if(args[0] instanceof Coord) {
	    pos = () -> (Coord)args[0];
	} else if(args[0] instanceof Coord2d) {
	    pos = () -> ((Coord2d)args[0]).mul(new Coord2d(this.sz.sub(child.sz))).round();
	} else if(args[0] instanceof String) {
	    pos = () -> relpos((String)args[0], child, args, 1);
	} else {
	    throw(new UI.UIException("unknown child widget position specification", null, args));
	}
	add(child, pos.get());
	childpos.add(new Pair<>(child, pos));
	if(packed)
	    pack();
    }

    public void pack() {
	super.pack();
	if(packpar && (parent != null))
	    parent.pack();
    }

    public void uimsg(String msg, Object... args) {
	if(msg == "pack") {
	    pack();
	    packed = true;
	} else {
	    super.uimsg(msg, args);
	}
    }

    public void cdestroy(Widget ch) {
	childpos.removeIf(rp -> rp.a == ch);
	if(packed)
	    pack();
    }

    public void cresize(Widget ch) {
	if(packed) {
	    for(Pair<Widget, Supplier<Coord>> rp : childpos)
		rp.a.move(rp.b.get());
	    pack();
	}
    }
}
