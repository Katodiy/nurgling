/* Preprocessed source code */
package haven.res.gfx.fx.fishline;

import haven.*;
import haven.render.*;
import java.util.*;
import java.util.function.*;
import java.nio.*;
import java.awt.Color;

@FromResource(name = "gfx/fx/fishline", version = 3)
public class FishLine extends Sprite {
    public final Pipe.Op linemat;
    public final long floatid;
    public final Gob plob;
    public final Line fx;
    private Coord3f gnd, ppos, fpos;
    private final List<RenderTree.Slot> slots = new ArrayList<>(1);

    public FishLine(Owner owner, Resource res, Color color, boolean stretch, long floatid) {
	super(owner, res);
	this.plob = (Gob)owner;
	this.floatid = floatid;
	this.linemat = new BaseColor(color);
	fx = new Line(stretch);
    }

    public static FishLine mksprite(Owner owner, Resource res, Message sdt) {
	Color color = sdt.color();
	long floatid = sdt.uint32();
	boolean stretch = sdt.uint8() != 0;
	return(new FishLine(owner, res, color, stretch, floatid));
    }

    private Gob flob = null;
    private Supplier<Pipe.Op> off = null;
    public Coord3f floatpos() {
	Gob flob = plob.glob.oc.getgob(floatid);
	if(flob == null)
	    return(null);
	if(flob != this.flob) {
	    ResDrawable draw = flob.getattr(ResDrawable.class);
	    if((draw == null) || !(draw.spr instanceof SkelSprite))
		return(null);
	    SkelSprite spr = (SkelSprite)draw.spr;
	    Skeleton.BoneOffset bo = spr.res.layer(Skeleton.BoneOffset.class, "l");
	    if(bo == null)
		throw(new RuntimeException("No line-offset (\"l\") in fish-float sprite"));
	    off = bo.forpose(spr.pose);
	    this.flob = flob;
	}
	Location.Chain loc = new BufPipe().prep(flob.placed.placement()).prep(off.get()).get(Homo3D.loc);
	return(loc.fin(Matrix4f.id).mul4(Coord3f.o));
    }

    public Coord3f linepos() {
	Composite cmp = plob.getattr(Composite.class);
	if(cmp == null)
	    return(null);
	for(Composited.Equ eq : cmp.comp.equ) {
	    if(!(eq instanceof Composited.SpriteEqu))
		continue;
	    Composited.SpriteEqu seq = (Composited.SpriteEqu)eq;
	    if(!(seq.r instanceof Pole))
		continue;
	    return(((Pole)seq.r).linepos());
	}
	return(null);
    }

    public Coord3f mypos() {
	RenderTree.Slot slot;
	synchronized(slots) {
	    if(slots.isEmpty())
		return(null);
	    slot = slots.get(0);
	}
	Location.Chain loc = Location.back(slot.state(), "gobx");
	if(loc == null)
	    return(null);
	return(loc.fin(Matrix4f.id).mul4(Coord3f.o));
    }

    public void gtick(Render g) {
	if((gnd != null) && (ppos != null) && (fpos != null))
	    fx.update(g, ppos.sub(gnd), fpos.sub(gnd));
    }

    public boolean tick(double dt) {
	gnd = mypos();
	ppos = linepos();
	try {
	    fpos = floatpos();
	} catch(Loading l) {}
	return(false);
    }

    public void added(RenderTree.Slot slot) {
	slot.ostate(Pipe.Op.compose(Rendered.postpfx, linemat, Location.goback("gobx")));
	slot.add(fx);
	synchronized(slots) {
	    slots.add(slot);
	}
    }

    public void removed(RenderTree.Slot slot) {
	synchronized(slots) {
	    slots.remove(slot);
	}
    }

    public void dispose() {
	fx.dispose();
    }
}
