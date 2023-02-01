package nurgling;

import haven.*;
import haven.render.Render;
import haven.render.RenderTree;

import java.awt.*;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class NPlayerArrow extends NSprite{

	public static ConcurrentHashMap<Long, NPlayerMarker> targets = new ConcurrentHashMap<>();
	public static HashMap<Long, Arrow> arrowHashMap = new HashMap();

	boolean needUpdate = true;
	class Arrow{
		NDirArrow arrow;
		NConfiguration.ArrowProp prop;

		public Arrow(NDirArrow arrow, NConfiguration.ArrowProp prop) {
			this.arrow = arrow;
			this.prop = prop;
		}
	}

	Gob player;
	final Collection<RenderTree.Slot> slots1 = new ArrayList<>(1);

	public NPlayerArrow(Owner owner, float r) {
		super(owner, null);
		this.player = (Gob)owner;
	}

	public void show() {
//		if (prop.ring) {
//			Loading.waitfor(() -> RUtils.multiadd(slots1, fx));
//		} else {
//			for (RenderTree.Slot slot : slots1)
//				slot.clear();
//		}
//		oldState = prop.ring;
	}

	public void update() {
		for (RenderTree.Slot slot : slots1)
			slot.clear();
		for(Arrow a : arrowHashMap.values()){
			if(a.prop!=null){
				Loading.waitfor(() -> RUtils.multiadd(slots1, a.arrow));
			}
		}
		needUpdate = false;
	}

	public void added(RenderTree.Slot slot) {
		slots1.add(slot);
	}

	@Override
	public void gtick(Render g) {
		for(Arrow a : arrowHashMap.values())
		{
			if(a.arrow!=null)
				a.arrow.gtick(g);
		}

	}

	@Override
	public boolean tick(double dt) {
		ArrayList<Long> forRemove = new ArrayList<>();
		for(Long target : targets.keySet()){
			if(NUtils.getGob(target)==null){
				Arrow a = arrowHashMap.get(target);
				if(a!=null && a.arrow!=null)
					a.arrow.forDelete = true;
				arrowHashMap.remove(target);
				forRemove.add(target);
				needUpdate = true;
			}else {
				if(arrowHashMap.get(target)==null || arrowHashMap.get(target).prop != targets.get(target).prop){
					NPlayerMarker mark = targets.get(target);
					if(mark.prop!=null) {
						arrowHashMap.put(target, new Arrow(new NDirArrow(player, mark.color, mark.r * 5, mark.gob, null, mark.prop), mark.prop));
					}else{
						arrowHashMap.put(target, new Arrow(null,null));
					}
					needUpdate = true;
				}
			}
		}
		for(Long id : forRemove){
			targets.remove(id);
		}
		if(needUpdate)
			update();
		return false;
	}

	public void removed(RenderTree.Slot slot) {
		slots1.remove(slot);
	}
}
