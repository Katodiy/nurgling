package nurgling;

import haven.Glob;
import haven.Gob;
import haven.OCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class NOCache extends OCache {


    public final HashMap<Long, HashSet<NGob.Tags>> bounds = new HashMap<>();
    public final NPathVisualizer paths = new NPathVisualizer();
    public static void addBoon(Long id, NGob.Tags tag){
        synchronized (((NOCache)NUtils.getGameUI().ui.sess.glob.oc).bounds){
            if(!((NOCache)NUtils.getGameUI().ui.sess.glob.oc).bounds.containsKey(id)) {
                ((NOCache)NUtils.getGameUI().ui.sess.glob.oc).bounds.put(id, new HashSet<>());
            }
            ((NOCache)NUtils.getGameUI().ui.sess.glob.oc).bounds.get(id).add(tag);
        }
    }

    public static boolean isBoon(Long id, NGob.Tags tag){
        synchronized (((NOCache)NUtils.getGameUI().ui.sess.glob.oc).bounds){
            if(((NOCache)NUtils.getGameUI().ui.sess.glob.oc).bounds.containsKey(id)) {
                return ((NOCache)NUtils.getGameUI().ui.sess.glob.oc).bounds.get(id).contains(tag);
            }
        }
        return false;
    }

    public static void removeBoon(Long id, NGob.Tags tag){
        synchronized (((NOCache)NUtils.getGameUI().ui.sess.glob.oc).bounds) {
            if (((NOCache) NUtils.getGameUI().ui.sess.glob.oc).bounds.get(id) != null) {
                ((NOCache) NUtils.getGameUI().ui.sess.glob.oc).bounds.get(id).remove(tag);
                if (((NOCache) NUtils.getGameUI().ui.sess.glob.oc).bounds.get(id).isEmpty())
                    ((NOCache) NUtils.getGameUI().ui.sess.glob.oc).bounds.remove(id);
            }
        }
    }

    public static HashSet<NGob.Tags> getBounds(Long id){
        synchronized (((NOCache)NUtils.getGameUI().ui.sess.glob.oc).bounds) {
            return ((NOCache)NUtils.getGameUI().ui.sess.glob.oc).bounds.get(id);
        }
    }

    public NOCache(Glob glob) {
        super(glob);
        callback(NGob.CHANGED);
    }

    public static ArrayList<Gob> getObjects(NGob.Tags... tags) {
        ArrayList<Gob> results = new ArrayList<>();
        synchronized (NUtils.getGameUI().ui.sess.glob.oc) {
            for (Gob gob : NUtils.getGameUI().ui.sess.glob.oc){
                boolean isFound = true;
                for(NGob.Tags tag: tags){
                    if(!gob.tags.contains(tag)){
                        isFound = false;
                        break;
                    }
                }
                if(isFound)
                    results.add(gob);
            }
        }
        return results;
    }

    public static Gob getgob(NGob.Tags... tags) {
        synchronized (NUtils.getGameUI().ui.sess.glob.oc) {
            for (Gob gob : NUtils.getGameUI().ui.sess.glob.oc){
                boolean isFounded = true;
                for(NGob.Tags tag: tags){
                    if(!gob.tags.contains(tag)){
                        isFounded = false;
                        break;
                    }
                }
                if(isFounded)
                    return gob;
            }
        }
        return null;
    }

    public static Gob getgob(Gob gob) {
        if(gob != null) {
            return NUtils.getGameUI().ui.sess.glob.oc.getgob(gob.id);
        }
        return null;
    }

    @Override
    public void ctick(double dt) {
        super.ctick(dt);
        paths.tick(dt);
    }
}
