/* Preprocessed source code */
package haven.res.gfx.fx.fishline;

import haven.*;
import haven.render.*;
import java.util.*;
import java.util.function.*;
import java.nio.*;
import java.awt.Color;

@FromResource(name = "gfx/fx/fishline", version = 3)
public class Pole extends StaticSprite {
    public final List<RenderTree.Slot> slots = new ArrayList<>(1);

    public Pole(Owner owner, Resource res, Message sdt) {
	super(owner, res, sdt);
    }

    public void added(RenderTree.Slot slot) {
	super.added(slot);
	synchronized(this.slots) {
	    this.slots.add(slot);
	}
    }

    public void removed(RenderTree.Slot slot) {
	super.removed(slot);
	synchronized(this.slots) {
	    this.slots.remove(slot);
	}
    }

    private Pipe.Op lineoff = null;
    public Pipe.Op lineoff() {
	if(lineoff == null) {
	    Skeleton.BoneOffset bo = res.layer(Skeleton.BoneOffset.class, "l");
	    if(bo == null)
		throw(new RuntimeException("No line-offset (\"l\") in fishing pole sprite"));
	    lineoff = bo.forpose(null).get();
	}
	return(lineoff);
    }

    public RenderTree.Slot lineslot() {
	synchronized(slots) {
	    if(slots.isEmpty())
		return(null);
	    return(slots.get(0));
	}
    }

    public Location.Chain lineloc() {
	RenderTree.Slot slot = lineslot();
	if(slot == null)
	    return(null);
	Pipe buf = slot.state().copy();
	buf.prep(lineoff());
	return(buf.get(Homo3D.loc));
    }

    public Coord3f linepos() {
	Location.Chain loc = lineloc();
	if(loc == null)
	    return(null);
	return(loc.fin(Matrix4f.id).mul4(Coord3f.o));
    }
}
