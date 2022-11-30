package nurgling;

import haven.*;

import java.awt.*;
import java.awt.event.MouseEvent;

import static haven.Equipory.ebgs;
import static haven.Equipory.etts;
import static haven.Inventory.invsq;


public class NEquipProxy extends NDraggableWidget implements NDTarget2 {
    public static final Color BG_COLOR = new Color(91, 128, 51, 202);
    private WItem[] quickslots;
	NEquipory.Slots []slots;
    public NEquipProxy(NEquipory.Slots...slots) {
	super("EquipProxy");
	setSlots(slots);
    }

    public void setSlots(NEquipory.Slots...slots) {
	this.slots = slots;
	sz = invsz(new Coord(slots.length, 1));
    }

    private NEquipory.Slots slot(Coord c) {
	int slot = sqroff(c).x;
	if(slot < 0) {slot = 0;}
	if(slot >= slots.length) {slot = slots.length - 1;}
	return slots[slot];
    }

    @Override
    public boolean mousedown(Coord c, int button) {
	Equipory e = NUtils.getEquipment();
	if(e != null) {
	    WItem w = NUtils.getEquipment().quickslots[slot(c).idx];
	    if(w != null) {
		w.mousedown(Coord.z, button);
		return true;
	    }
	}
	return super.mousedown(c, button);
    }

    @Override
    public void draw(GOut g) {
	Equipory equipory = NUtils.getEquipment();
	if(equipory != null) {
	    int k = 0;
	    g.chcolor(BG_COLOR);
	    g.frect(Coord.z, sz);
	    g.chcolor();
	    Coord c0 = new Coord(0, 0);
	    for (NEquipory.Slots slot : slots) {
		c0.x = k;
		Coord c1 = sqoff(c0);
		g.image(invsq, c1);
		WItem w = NUtils.getEquipment().quickslots[slot.idx];
		if(w != null) {
		    w.draw(g.reclipl(c1, invsq.sz()));
		} else if(ebgs[slot.idx] != null) {
		    g.image(ebgs[slot.idx], c1);
		}
		k++;
	    }
	}
    }

    @Override
    public Object tooltip(Coord c, Widget prev) {
	Equipory e = NUtils.getEquipment();
	if(e != null) {
	    NEquipory.Slots slot = slot(c);
	    WItem w = NUtils.getEquipment().quickslots[slot.idx];
	    if(w != null) {
		return w.tooltip(c, (prev == this) ? w : prev);
	    } else {
		return etts[slot.idx];
	    }
	}
	return super.tooltip(c, prev);
    }

    @Override
    public boolean drop(WItem target, Coord cc, Coord ul) {
	Equipory e = NUtils.getEquipment();
	if(e != null) {
	    e.wdgmsg("drop", slot(cc).idx);
	    return true;
	}
	return false;
    }

    @Override
    public boolean iteminteract(WItem target, Coord cc, Coord ul) {
	Equipory e = NUtils.getEquipment();
	if(e != null) {
	    WItem w = NUtils.getEquipment().quickslots[slot(cc).idx];
	    if(w != null) {
		return w.iteminteract( cc, ul);
	    }
	}
	return false;
    }

    public void activate(NEquipory.Slots slot, int button) {
	for (int i = 0; i < slots.length; i++) {
	    if(slots[i] == slot) {
		activate(i, button);
		return;
	    }
	}
    }

    private void activate(int i, int button) {
        Coord mc = ui.mc;
	Coord c = sqoff(new Coord(i, 0)).add(rootpos());
//	ui.mousedown(new MouseEvent(), c, button);
//	ui.mouseup(c, button);
//	ui.mousemove(mc);
    }

	public static Coord sqoff(Coord c){
		return c.mul(invsq.sz());
	}

	public static Coord sqroff(Coord c){
		return c.div(invsq.sz());
	}

	public static Coord invsz(Coord sz) {
		return invsq.sz().add(new Coord(-1, -1)).mul(sz).add(new Coord(1, 1));
	}

}
