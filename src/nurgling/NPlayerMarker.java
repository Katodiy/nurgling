package nurgling;

import haven.*;
import haven.render.Render;
import haven.render.RenderTree;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public class NPlayerMarker extends NSprite{
	NTargetRing fx = null;
	NDirArrow arrow = null;
	NTargetRing ring = null;
	final Collection<RenderTree.Slot> slots1 = new ArrayList<>(1);
	Tex marker;
	NConfiguration.ArrowProp prop;
	Gob gob;
	Color color;

	int group;
	boolean oldState = false;
	float r;
	public NPlayerMarker(Owner owner, float r) {
		super(owner, null);
		this.gob = (Gob)owner;
		group = -1;
		this.r = r;


	}

	public void show() {
		if (prop.ring) {
			Loading.waitfor(() -> RUtils.multiadd(slots1, fx));
//			NAlarmManager.play(NGob.Tags.unknown);
		} else {
			for (RenderTree.Slot slot : slots1)
				slot.clear();
		}
		oldState = prop.ring;
	}

	public void update() {
		NPlayerArrow.targets.put(gob.id,this);
		group = KinInfo.getGroup(gob);
		if (group == 0 || group == -1 && gob.getattr(NGobHealth.class) == null) {
			color = Color.WHITE;
			prop = NConfiguration.getInstance().players.get("white");
			fx = new NTargetRing(gob, color, r);
			for (RenderTree.Slot slot : slots1)
				slot.clear();
			show();
		} else if (KinInfo.getGroup(gob) == 2) {
			color = Color.RED;
			prop = NConfiguration.getInstance().players.get("red");
			fx = new NTargetRing(gob, color, r);
			for (RenderTree.Slot slot : slots1)
				slot.clear();
			show();
		} else {
			prop = null;
			for (RenderTree.Slot slot : slots1)
				slot.clear();
		}
	}

	public void added(RenderTree.Slot slot) {
		slots1.add(slot);
	}

	@Override
	public void gtick(Render g) {
		if(fx!=null)
			fx.gtick(g);
	}

	@Override
	public boolean tick(double dt) {
		if(prop!=null && prop.ring!=oldState)
			show();
		if(group!=KinInfo.getGroup(gob))
			update();
		return gob.isTag(NGob.Tags.knocked);
	}

	public void removed(RenderTree.Slot slot) {
		slots1.remove(slot);
	}
}
