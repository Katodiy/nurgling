/* Preprocessed source code */
package haven.res.lib.globfx;

import haven.*;
import haven.render.*;
import haven.render.RenderTree.Slot;
import java.util.*;
import java.lang.reflect.*;
import java.lang.ref.*;

@FromResource(name = "lib/globfx", version = 12)
public class GlobEffector extends Drawable {
    /* Keep weak references to the glob-effectors themselves, or
     * GlobEffector.glob (and GlobEffector.gob.glob) will keep the
     * globs alive through the strong value references forever. */
    static Map<Glob, Reference<GlobEffector>> cur = new WeakHashMap<Glob, Reference<GlobEffector>>();
    public final Glob glob;
    final Collection<Slot> slots = new ArrayList<>(1);
    Collection<Gob> holder = null;
    Map<Effect, Effect> effects = new HashMap<>();
    Map<Datum, Datum> data = new HashMap<>();
    Map<Slot, Map<Effect, Slot>> fxslots = new HashMap<>();
    
    private GlobEffector(Gob gob) {
	super(gob);
	this.glob = gob.glob;
    }
    
    public void added(Slot slot) {
	Collection<Pair<Effect, Slot>> added = new ArrayList<>(effects.size());
	for(Effect spr : effects.values()) {
	    added.add(new Pair<>(spr, slot.add(spr)));
	}
	slots.add(slot);
	for(Pair<Effect, Slot> fs : added)
	    fxslots.computeIfAbsent(slot, k -> new HashMap<>()).put(fs.a, fs.b);
    }

    public void removed(Slot slot) {
	slots.remove(slot);
	fxslots.remove(slot);
    }
    
    public void ctick(double ddt) {
	float dt = (float)ddt;
	for(Iterator<Effect> i = effects.values().iterator(); i.hasNext();) {
	    Effect spr = i.next();
	    if(spr.tick(dt)) {
		i.remove();
		for(Map<Effect, Slot> ms : fxslots.values()) {
		    Slot slot = ms.remove(spr);
		    if(slot == null)
			System.err.printf("warning: globfx effect-slot not present when auto-removing %s\n", spr);
		    else
			slot.remove();
		}
	    }
	}
	for(Iterator<Datum> i = data.values().iterator(); i.hasNext();) {
	    Datum d = i.next();
	    if(d.tick(dt))
		i.remove();
	}
	synchronized(cur) {
	    if((effects.size() == 0) && (data.size() == 0)) {
		glob.oc.lrem(holder);
		cur.remove(glob);
	    }
	}
    }

    public void gtick(Render out) {
	for(Effect spr : effects.values())
	    spr.gtick(out);
    }
    
    public Resource getres() {
	return(null);
    }
    
    private <T> T create(Class<T> fx) {
	Resource res = Resource.classres(fx);
	try {
	    try {
		Constructor<T> cons = fx.getConstructor(Sprite.Owner.class, Resource.class);
		return(cons.newInstance(gob, res));
	    } catch(NoSuchMethodException e) {}
	    throw(new RuntimeException("No valid constructor found for global effect " + fx));
	} catch(InstantiationException e) {
	    throw(new RuntimeException(e));
	} catch(IllegalAccessException e) {
	    throw(new RuntimeException(e));
	} catch(InvocationTargetException e) {
	    if(e.getCause() instanceof RuntimeException)
		throw((RuntimeException)e.getCause());
	    throw(new RuntimeException(e));
	}
    }

    public Object monitor() {
	return(this.gob);
    }

    @SuppressWarnings("unchecked")
    public <T extends Effect> T get(T fx) {
	synchronized(this.gob) {
	    T ret = (T)effects.get(fx);
	    if(ret == null) {
		Collection<Pair<Slot, Slot>> added = new ArrayList<>(slots.size());
		try {
		    for(Slot slot : slots)
			added.add(new Pair(slot, slot.add(fx)));
		} catch(RuntimeException e) {
		    for(Pair<Slot, Slot> slotm : added)
			slotm.b.remove();
		    throw(e);
		}
		effects.put(ret = fx, fx);
		for(Pair<Slot, Slot> slotm : added)
		    fxslots.computeIfAbsent(slotm.a, k -> new HashMap<>()).put(fx, slotm.b);
	    }
	    return(ret);
	}
    }
    
    @SuppressWarnings("unchecked")
    public <T extends Datum> T getdata(T fx) {
	synchronized(this.gob) {
	    T ret = (T)data.get(fx);
	    if(ret == null)
		data.put(ret = fx, fx);
	    return(ret);
	}
    }
    
    public static GlobEffector get(Glob glob) {
	Collection<Gob> add = null;
	GlobEffector ret;
	synchronized(cur) {
	    Reference<GlobEffector> ref = cur.get(glob);
	    ret = (ref == null) ? null : ref.get();
	    if(ret == null) {
		Gob hgob = new Gob(glob, Coord2d.z) {
			public Coord3f getc() {
			    return(Coord3f.o);
			}

			public Pipe.Op getmapstate(Coord3f pc) {
			    return(null);
			}
		    };
		GlobEffector ne = new GlobEffector(hgob);
		hgob.setattr(ne);
		add = ne.holder = Collections.singleton(hgob);
		cur.put(glob, new WeakReference<GlobEffector>(ret = ne));
	    }
	}
	if(add != null)
	    glob.oc.ladd(add);
	return(ret);
    }

    public static <T extends Effect> T get(Glob glob, T fx) {
	return(get(glob).get(fx));
    }

    public static <T extends Datum> T getdata(Glob glob, T fx) {
	return(get(glob).getdata(fx));
    }
}
