package nurgling;

import haven.*;
import haven.Composite;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

public class NUtils {
    static NGameUI gameUI;
    static NUI nui;

    static final HashMap<NGob.Tags,TexI> iHashMap = new HashMap<>();
    static final HashMap<String, String> iconMap = new HashMap<>();
    static final HashMap<Color,TexI> iCropMap = new HashMap<>();
    static final HashMap<Integer,TexI> iCropStageMap3 = new HashMap<>();
    static final HashMap<Integer,TexI> iCropStageMap4 = new HashMap<>();
    static final HashMap<Integer,TexI> iCropStageMap5 = new HashMap<>();
    static final HashMap<Integer,TexI> iCropStageMap6 = new HashMap<>();

    static
    {
        iHashMap.put(NGob.Tags.no_water,new TexI(Resource.loadsimg("icon/no_water")));
        iHashMap.put(NGob.Tags.no_silo,new TexI(Resource.loadsimg("icon/no_silo")));
        iHashMap.put(NGob.Tags.wax,new TexI(Resource.loadsimg("icon/wax")));
        iHashMap.put(NGob.Tags.tanning,new TexI(Resource.loadsimg("icon/tanning")));
        iHashMap.put(NGob.Tags.truffle,new TexI(Resource.loadsimg("icon/items/truffle")));
        iHashMap.put(NGob.Tags.gem,new TexI(Resource.loadsimg("icon/items/gem")));
        iHashMap.put(NGob.Tags.angryhorse,new TexI(Resource.loadsimg("icon/angryhorse")));
        iHashMap.put(NGob.Tags.wool,new TexI(Resource.loadsimg("icon/wool")));

        iconMap.put("gfx/terobjs/vehicle/wheelbarrow","mm/wheelbarrow");
        Resource.loadimg("mm/wheelbarrow");
        iconMap.put("gfx/terobjs/items/truffle","mm/truffle");
        Resource.loadimg("mm/truffle");
        iconMap.put("gfx/terobjs/cauldron","mm/cauldron");
        Resource.loadimg("mm/cauldron");
        iconMap.put("gfx/terobjs/anvil","mm/anvil");
        Resource.loadimg("mm/anvil");
        iconMap.put("gfx/terobjs/candelabrum","mm/candelabrum");
        Resource.loadimg("mm/candelabrum");
        iconMap.put("gfx/kritter/stalagoomba/stalagoomba","mm/stalagoomba");
        Resource.loadimg("mm/stalagoomba");
        iconMap.put("gfx/terobjs/claim","mm/claim");
        Resource.loadimg("mm/claim");
        iconMap.put("gfx/terobjs/items/gems/gemstone","mm/gem");
        Resource.loadimg("mm/gem");
        iconMap.put("gfx/terobjs/vehicle/cart","mm/cart");
        Resource.loadimg("mm/cart");
        iconMap.put("gfx/terobjs/map/cavepuddle","mm/clay-cave");
        Resource.loadimg("mm/clay-cave");

        iCropMap.put(Color.RED,new TexI(Resource.loadsimg("crop/red")));
        iCropMap.put(Color.ORANGE,new TexI(Resource.loadsimg("crop/orange")));
        iCropMap.put(Color.YELLOW,new TexI(Resource.loadsimg("crop/yellow")));
        iCropMap.put(Color.BLUE,new TexI(Resource.loadsimg("crop/blue")));
        iCropMap.put(Color.GRAY,new TexI(Resource.loadsimg("crop/gray")));
        iCropMap.put(Color.GREEN,new TexI(Resource.loadsimg("crop/green")));
        iCropStageMap3.put(1,new TexI(Resource.loadsimg("crop/yellow_1_3")));
        iCropStageMap3.put(2,new TexI(Resource.loadsimg("crop/yellow_2_3")));
        iCropStageMap4.put(1,new TexI(Resource.loadsimg("crop/yellow_1_4")));
        iCropStageMap4.put(2,new TexI(Resource.loadsimg("crop/yellow_2_4")));
        iCropStageMap4.put(3,new TexI(Resource.loadsimg("crop/yellow_3_4")));
        iCropStageMap5.put(1,new TexI(Resource.loadsimg("crop/yellow_1_5")));
        iCropStageMap5.put(2,new TexI(Resource.loadsimg("crop/yellow_2_5")));
        iCropStageMap5.put(3,new TexI(Resource.loadsimg("crop/yellow_3_5")));
        iCropStageMap5.put(4,new TexI(Resource.loadsimg("crop/yellow_4_5")));
        iCropStageMap6.put(1,new TexI(Resource.loadsimg("crop/yellow_1_6")));
        iCropStageMap6.put(2,new TexI(Resource.loadsimg("crop/yellow_2_6")));
        iCropStageMap6.put(3,new TexI(Resource.loadsimg("crop/yellow_3_6")));
        iCropStageMap6.put(4,new TexI(Resource.loadsimg("crop/yellow_4_6")));
        iCropStageMap6.put(5,new TexI(Resource.loadsimg("crop/yellow_5_6")));
    }
    public static TexI getTexI(NGob.Tags tag) {
        return iHashMap.get(tag);
    }
    public static TexI getCropTexI(Color clr) {
        return iCropMap.get(clr);
    }

    public static TexI getCropTexI(int curent, int max) {
        switch (max) {
            case 3:
                return iCropStageMap3.get(curent);
            case 4:
                return iCropStageMap4.get(curent);
            case 5:
                return iCropStageMap5.get(curent);
            case 6:
                return iCropStageMap6.get(curent);
        }return null;
    }

    static public NGameUI getGameUI() {
        return gameUI;
    }

    public static void setGameUI(NGameUI gameUI) {
        NUtils.gameUI = gameUI;
    }

    public static void setUI(NUI nui) {
        NUtils.nui = nui;
    }

    public static NUI getUI() {
        return nui;
    }

    public static long getTickId() {
        return nui.tickId;
    }

    public static boolean checkName(
            final String name,
            final String... args
    ) {
        return checkName(name, new NAlias(args));
    }

    public static boolean checkName(
            final String name,
            final NAlias regEx
    ) {
        if (regEx != null) {
            /// Проверяем имя на соответствие
            for (String key : regEx.keys) {
                if (name.contains(key)) {
                    for (String ex : regEx.exceptions) {
                        if (name.contains(ex)) {
                            return false;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean waitEvent(
            Expression exp,
            int delay
    )
            throws InterruptedException {
        return waitEvent(exp,delay,50);
    }

    public static boolean waitEvent(
            Expression exp,
            int delay,
            int tick
    )
            throws InterruptedException {
        long start_id = getUI().getTickId();
        while ((!exp.isTrue()) && getUI().getTickId() - start_id < delay) {
            Thread.sleep(tick);
        }
        return exp.isTrue();
    }


    public interface Expression {
        public boolean isTrue()
                throws InterruptedException;
    }
    public static boolean isIt(
            final Indir<Resource> res,
            final String... candidates
    ) {
        NAlias regEx = new NAlias(candidates);
        return isIt(res,regEx);
    }

    public static boolean isIt(
            final Indir<Resource> res,
            final NAlias regEx
    ) {
        if (res != null) {
            /// Запрашиваем ресур
            Resource resource = res.get();

            if (resource != null) {
                /// Проверяем имя на соответствие
                return checkName(resource.name, regEx);
            }
        }
        return false;
    }

    public static boolean isIt(
            final ResData res,
            final String... candidates
    ) {
        NAlias regEx = new NAlias(candidates);
        return isIt(res,regEx);
    }

    public static boolean isIt(
            final ResData res,
            final NAlias regEx
    ) {
        if (res != null) {
            /// Запрашиваем ресур
            Resource resource = res.res.get();
            if (resource != null) {
                /// Проверяем имя на соответствие
                return checkName(resource.name, regEx);
            }
        }
        return false;
    }

    public static boolean isIt(
            final Gob gob,
            final String... candidates
    ) {
        NAlias regEx = new NAlias(candidates);
        return isIt(gob,regEx);
    }

    public static boolean isIt(
            final Gob gob,
            final NAlias regEx
    ) {
        if (gob != null) {
            try {
                /// Запрашиваем ресур
                Resource res = null;
                res = gob.getres();
                if (res != null) {
                    /// Проверяем имя на соответствие
                    return checkName(res.name, regEx);
                }
            } catch (Loading e) {
            }
        }
        return false;
    }

    public static boolean isPose(Gob gob, NAlias name){
        if(gob!=null && gob.getattr(Drawable.class)!=null && gob.getattr(Drawable.class) instanceof Composite && ((Composite)gob.getattr(Drawable.class)).oldposes!=null)
            for(ResData data:((Composite)gob.getattr(Drawable.class)).oldposes)
                if(isIt(data, name))
                    return true;
        return false;
    }

    public static boolean isIt(
            final Gob.Overlay ol,
            final String... candidates
    ) {
        NAlias regEx = new NAlias(candidates);
        return isIt(ol,regEx);
    }
    public static boolean isIt(Gob.Overlay ol, NAlias regEx) {
        if (ol != null) {
            try {
                /// Запрашиваем ресур
                Resource res = null;
                if (ol.res != null) {
                    res = ol.res.get();
                    /// Проверяем имя на соответствие
                    return checkName(res.name, regEx);
                }
            } catch (Loading e) {
            }
        }
        return false;
    }

//    public static boolean isIt(
//            final Shopbox shopbox,
//            final NAlias regEx
//    ) {
//        if (shopbox != null) {
//            try {
//                /// Запрашиваем ресур
//                Resource res = null;
//                res = shopbox.getres();
//                if (res != null) {
//                    /// Проверяем имя на соответствие
//                    return checkName(res.name, regEx);
//                }
//            } catch (Loading e) {
//            }
//        }
//        return false;
//    }

    public static boolean isIt(
            final GItem item,
            final String... candidates
    ) {
        NAlias regEx = new NAlias(candidates);
        return isIt(item,regEx);
    }

    public static boolean isIt(
            final GItem item,
            final NAlias regEx
    ) {
        /// Проверяем что переданный предмет существует
        if (item != null) {
            try {
                /// Запрашиваем ресур
                Resource resource = item.resource();
                /// Проверяем что полученный ресур существует
                if (resource != null) {
                    /// Проверяем имя на соответствие
                    return checkName(resource.name, regEx);
                }
            } catch (Resource.Loading e) {
                /// Если при проверке произошло исключение загрузки ресурса возвращаем false
                return false;
            }
        }
        /// Если предмет не найден возвращаем false
        return false;
    }

    public static boolean isIt(
            final WItem item,
            final NAlias regEx
    ) {
        if (item != null) {
            try {
                /// Запрашиваем ресур
                Resource res = null;
                res = item.item.getres();
                if (res != null) {
                    /// Проверяем имя на соответствие
                    if(((NGItem)item.item).name()!=null)
                        return checkName(res.name, regEx) || checkName(((NGItem)item.item).name(), regEx);
                    else
                        return checkName(res.name, regEx);
                }
            } catch (Loading e) {
            }
        }
        return false;
    }

    public static boolean isIt(
            final WItem item,
            final String... candidates
    ) {
        NAlias regEx = new NAlias(candidates);
        return isIt(item,regEx);
    }

    public static boolean isIt(
            Coord tilecooord,
            NAlias name1
    ) {
        try {
            /// Запрашиваем ресур
            Resource res_beg = gameUI.ui.sess.glob.map.tilesetr(gameUI.ui.sess.glob.map.gettile(tilecooord));
            /// Проверяем что полученный ресур существует
            if (res_beg != null) {
                /// Проверяем имя на соответствие
                return checkName(res_beg.name, name1);
            }
        } catch (Resource.Loading e) {
            /// Если при проверке произошло исключение загрузки ресурса возвращаем false
        }
        /// Если не совпада
        return false;
    }

    public static Gob getGob(long id){
        if(gameUI!=null)
            return gameUI.ui.sess.glob.oc.getgob(id);
        return null;
    }
    private static Speedget speedget;

    public static void setSpeedget(Speedget speed) {
        speedget = speed;
        speedValue.ifPresent(speed::set);
    }

    static Optional<Integer> speedValue = Optional.empty();
    public static void setSpeed(int value) {
        if(speedget!=null && gameUI.ui!=null) {
            if (value >= 0 && value <= 3)
                speedget.set(value);
        }else{
            speedValue = Optional.of(value);
        }
    }

    public static GobIcon getIcon(NGob nGob) {
        Gob gob = (Gob)nGob;
        if(gob.getResName()!= null && checkName(gob.getResName(),"truffle")) {
            return new GobIcon(gob, Resource.remote().load(iconMap.get("gfx/terobjs/items/truffle")));
        }
        if(iconMap.get(gob.getResName()) != null) {
            return new GobIcon(gob, Resource.remote().load(iconMap.get(gob.getResName())));
        }
        return null;
    }

    public static float getDeltaZ() {
        return nui.getDeltaZ();
    }

    public static boolean isLifted(Gob gob) {
        return (gob.getattr(Following.class)!=null && gob.getattr(Following.class).tgt==gameUI.map.player().id);
    }

    public static boolean isGobInArea(
            Gob gob,
            NArea area
    ) {
        return area.begin.x <= gob.rc.x && area.begin.y <= gob.rc.y && gob.rc.x <= area.end.x && gob.rc.y <= area.end.y;
    }

    public static boolean isOverlay(
            Gob gob,
            NAlias name
    ) {
        for (Gob.Overlay ov : gob.ols) {
            if (ov.res != null) {
                Resource res_ov = ov.res.get();
                if (res_ov != null) {
                    if (checkName(res_ov.name, name)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean alarmOrcalot() {
        ArrayList<Gob> gobs;
        if(NConfiguration.getInstance().alarmGreyseal)
            gobs = Finder.findObjectsInArea(
                    new NAlias(new ArrayList<>(Arrays.asList("/orca", "/spermwhale", "/greyseal")),new ArrayList<>(Arrays.asList("beef", "skeleton"))),
                    new NArea(gameUI.map.player().rc, 3999));
        else
            gobs = Finder.findObjectsInArea(
                    new NAlias(new ArrayList<>(Arrays.asList("/orca", "/spermwhale")),new ArrayList<>(Arrays.asList("beef", "skeleton"))),
                    new NArea(gameUI.map.player().rc, 3999));
        for(Gob gob: gobs) {
            if (!gob.isTag(NGob.Tags.knocked) && gob.isTag(NGob.Tags.kritter_is_ready))
                return true;
        }
        return false;
    }

    public static void logOut() {
        gameUI.act("lo");
    }

    public static boolean alarm() {
        if (!Finder.findObjectsInArea(new NAlias(new ArrayList<String>(
                        Arrays.asList("/boar", "/badger", "/wolverine", "/adder", "/bat", "/moose"))),
                new NArea(gameUI.map.player().rc, 280)).isEmpty()) {
            return true;
        } else {
            return !Finder.findObjectsInArea(
                    new NAlias(new ArrayList<String>(Arrays.asList("/bear", "/wolf", "/lynx", "borka/body"))),
                    new NArea(gameUI.map.player().rc, 500)).isEmpty();
        }
    }

}
