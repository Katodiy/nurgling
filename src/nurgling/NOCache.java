package nurgling;

import haven.*;
import haven.render.BaseColor;
import haven.render.States;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class NOCache extends OCache {

    public final NPathVisualizer paths = new NPathVisualizer();


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

    static class OverlayInfo{
        MCache.AreaOverlay ol;
        AreasID id;

        public OverlayInfo(MCache.AreaOverlay ol, AreasID id) {
            this.ol = ol;
            this.id = id;
        }
    }

    public static final ArrayList<MCache.OverlayInfo> ols= new ArrayList<>();
    static final AtomicInteger currentOls = new AtomicInteger(0);

    public static void initOls(){
        synchronized (ols) {
            ols.add(new MCache.OverlayInfo() {
                final Material mat = new Material(new BaseColor(255, 255, 0, 64), States.maskdepth);

                public Collection<String> tags() {
                    return (Arrays.asList("show"));
                }

                public Material mat() {
                    return (mat);
                }
            });

            ols.add(new MCache.OverlayInfo() {
                final Material mat = new Material(new BaseColor(0, 0, 255, 64), States.maskdepth);

                public Collection<String> tags() {
                    return (Arrays.asList("show"));
                }

                public Material mat() {
                    return (mat);
                }
            });

            ols.add(new MCache.OverlayInfo() {
                final Material mat = new Material(new BaseColor(0, 255, 255, 64), States.maskdepth);

                public Collection<String> tags() {
                    return (Arrays.asList("show"));
                }

                public Material mat() {
                    return (mat);
                }
            });

            ols.add(new MCache.OverlayInfo() {
                final Material mat = new Material(new BaseColor(255, 0, 0, 64), States.maskdepth);

                public Collection<String> tags() {
                    return (Arrays.asList("show"));
                }

                public Material mat() {
                    return (mat);
                }
            });
            ols.add(new MCache.OverlayInfo() {
                final Material mat = new Material(new BaseColor(128, 0, 255, 64), States.maskdepth);

                public Collection<String> tags() {
                    return (Arrays.asList("show"));
                }

                public Material mat() {
                    return (mat);
                }
            });

            ols.add(new MCache.OverlayInfo() {
                final Material mat = new Material(new BaseColor(0, 255, 0, 64), States.maskdepth);

                public Collection<String> tags() {
                    return (Arrays.asList("show"));
                }

                public Material mat() {
                    return (mat);
                }
            });
            ols.add(new MCache.OverlayInfo() {
                final Material mat = new Material(new BaseColor(0, 255, 128, 64), States.maskdepth);

                public Collection<String> tags() {
                    return (Arrays.asList("show"));
                }

                public Material mat() {
                    return (mat);
                }
            });
        }
    }



    ArrayList<OverlayInfo> overlays = new ArrayList<>();
    public static void constructOverlay(AreasID id){
        if(id!=null) {
            for (OverlayInfo oi : ((NOCache) NUtils.getGameUI().ui.sess.glob.oc).overlays) {
                if (oi.id == id)
                    return;
            }
            if (ols.isEmpty())
                initOls();
            NArea area = Finder.findNearestMark(id);
            if(area!=null) {
                Coord begin_sc = area.begin.div(MCache.tilesz).floor();
                Coord end_sc = area.end.div(MCache.tilesz).floor();
                synchronized (NUtils.getGameUI().ui.sess.glob.map) {
                    int num = 0;
                    synchronized (currentOls) {
                        currentOls.set((currentOls.get() + 1 < ols.size()) ? currentOls.get() + 1 : 0);
                        num = currentOls.get();
                    }
                    ((NOCache) NUtils.getGameUI().ui.sess.glob.oc).overlays.add(new OverlayInfo(NUtils.getGameUI().ui.sess.glob.map.new AreaOverlay(new haven.Area(begin_sc, end_sc), ols.get(num), id), id));
                }
            }
        }
    }
}
