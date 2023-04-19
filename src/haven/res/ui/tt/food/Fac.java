package haven.res.ui.tt.food;/* Preprocessed source code */

import haven.*;
import nurgling.NFoodInfo;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import static haven.resutil.FoodInfo.Effect;
import static haven.resutil.FoodInfo.Event;
import static haven.resutil.FoodInfo.Event.imgsz;

/* >tt: haven.res.ui.tt.food.Fac */
@FromResource(name = "ui/tt/food", version = 13)
public class Fac implements ItemInfo.InfoFactory {

	class FepCache
	{
		public FepCache(CharWnd.FoodMeter.Event ev, BufferedImage img) {
			this.ev = ev;
			this.img = img;
		}

		public final CharWnd.FoodMeter.Event ev;
		public final BufferedImage img;
	}

	static final ConcurrentHashMap<Integer, FepCache > fepResCache = new ConcurrentHashMap<>();
    public ItemInfo build(ItemInfo.Owner owner, ItemInfo.Raw raw, Object... args) {
	int c = 1;
	double end = ((Number)args[c++]).doubleValue();
	double glut = ((Number)args[c++]).doubleValue();
	double cons = 0;
	if(args[c] instanceof Number)
	    cons = ((Number)args[c++]).doubleValue();
	Object[] evd = (Object[])args[c++];
	Object[] efd = (Object[])args[c++];
	Object[] tpd = (Object[])args[c++];

	Collection<Event> evs = new LinkedList<Event>();
	Collection<Effect> efs = new LinkedList<Effect>();
	for(int a = 0; a < evd.length; a += 2) {
		if (!fepResCache.containsKey((Integer) evd[a])) {
			Resource res = owner.context(Resource.Resolver.class).getres((Integer) evd[a]).get();
			fepResCache.put((Integer) evd[a], new FepCache(
					res.flayer(CharWnd.FoodMeter.Event.class)
					, PUtils.convolve(res.flayer(Resource.imgc).img, imgsz, CharWnd.iconfilter)
			));
		}
		FepCache fepCache = fepResCache.get((Integer) evd[a]);
		evs.add(new Event(fepCache.ev, fepCache.img,
				((Number) evd[a + 1]).doubleValue()));
	}

	for(int a = 0; a < efd.length; a += 2)
	    efs.add(new Effect(ItemInfo.buildinfo(owner, new Object[] {(Object[])efd[a]}),
			       ((Number)efd[a + 1]).doubleValue()));

	int[] types;
	{
	    int[] buf = new int[tpd.length * 32];
	    int n = 0, t = 0;
	    for(int i = 0; i < tpd.length; i++) {
		for(int b = 0, m = 1; b < 32; b++, m <<= 1, t++) {
		    if(((Integer)tpd[i] & m) != 0)
			buf[n++] = t;
		}
	    }
	    types = new int[n];
	    for(int i = 0; i < n; i++)
		types[i] = buf[i];
	}

	try {
	    return(new NFoodInfo(owner, end, glut, cons, evs.toArray(new Event[0]), efs.toArray(new Effect[0]), types));
	} catch(NoSuchMethodError e) {
	    return(new NFoodInfo(owner, end, glut, evs.toArray(new Event[0]), efs.toArray(new Effect[0]), types));
	}
    }
}
