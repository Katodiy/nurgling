package nurgling;

import haven.*;
import haven.Button;
import haven.Composite;
import haven.Label;
import haven.Window;
import haven.res.ui.barterbox.Shopbox;
import haven.res.ui.croster.CattleId;
import haven.res.ui.croster.Entry;
import haven.res.ui.croster.RosterWindow;
import haven.res.ui.tt.defn.DefName;
import haven.res.ui.tt.q.qbuff.QBuff;
import haven.res.ui.tt.q.quality.Quality;
import nurgling.bots.*;
import nurgling.bots.actions.WaitAction;
import nurgling.json.JSONObject;
import nurgling.json.parser.JSONParser;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import static haven.MCache.tilesz;
import static haven.OCache.posres;
import static nurgling.tools.Finder.findNearestObject;

public class NUtils {
    static HashMap<String,String> data_titles;



    private static NFightView fightView;
    public static void setFightView(NFightView fightView) {
        NUtils.fightView = fightView;
    }

    public static NFightView getFightView() {
        return fightView;
    }

    public static NFightSess getFightSess() {
        return NFightSess.instance;
    }

    static {
        try {
            data_titles = new HashMap<>();
            URL url = NUtils.class.getProtectionDomain().getCodeSource().getLocation();

            if (url != null) {
                String path = url.toURI().getPath().substring(0, url.toURI().getPath().lastIndexOf("/"));
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(path + "/tile_names.json"), "cp1251"));
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(reader);
                for(Object key:jsonObject.keySet())
                {
                    data_titles.put(key.toString(), (String) jsonObject.get(key));
                }
            }
        } catch (Exception ignored) {
        }
    }
    private static NGameUI gameUI;
    private static NUI nui;
    private static Speedget speedget;

    public static boolean checkName(
            final String name,
            final String... args
    ) {
        return checkName(name, new NAlias(args));
    }

    public static void sendToChat(String channel, String msg) throws InterruptedException {
        for ( ChatUI.Selector.DarkChannel chan : gameUI.chat.chat.chansel.chls ) {
            if ( chan.chan.name ().equals  ( channel ) ) {
                gameUI.chat.chat.select ( chan.chan );
                Thread.sleep ( 1000 );
                gameUI.chat.chat.sel.wdgmsg ( "msg", msg );
            }
        }
    }
    public static boolean checkName(
            final String name,
            final NAlias regEx
    ) { if (regEx!=null) {
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

    public static boolean craft(
            char[] command,
            String name,
            boolean noStop
    )
            throws InterruptedException {
        Widget spwnd = null;
        if(gameUI.craftwnd!=null) {
            spwnd = gameUI.craftwnd.makeWidget;
        }
        if (spwnd == null || !checkName(name, gameUI.craftwnd.makeWidget.rcpnm)) {
            ((NMenuGrid) gameUI.menu).globtype('q');
            for (Character ch : command) {
                ((NMenuGrid) gameUI.menu).globtype(ch);
            }
            waitEvent(() -> (gameUI.craftwnd!=null && gameUI.craftwnd.makeWidget!=null), 500);
            waitEvent(() -> gameUI.craftwnd.makeWidget!=null && checkName(name, gameUI.craftwnd.makeWidget.rcpnm), 50);
            spwnd = gameUI.craftwnd.makeWidget;

        }
        if (spwnd != null) {
            boolean makeFound = false;
            spwnd.wdgmsg("make", 1);
            waitEvent(() -> getProg() >= 0, 15);
            if (getProg() >= 0)
                makeFound = true;
            waitEvent(() -> getProg() < 0, 10000);


            /// Ждем завершения крафта
            while (true) {
                waitEvent(() -> getProg() >= 0, 15);
                if (getProg() < 0)
                    break;
                waitEvent(() -> getProg() < 0, 10000);
            }

            int size = gameUI.getInventory().getFreeSpace();
            if (checkName(name, new NAlias("Grind Pepper"))) {
                while (getProg() >= 0) {
                    Thread.sleep(100);
                }
                Thread.sleep(500);
            } else {
                while (getProg() >= 0) {
                    if (gameUI.getInventory().getFreeSpace() == 0)
                        break;
                    waitEvent(() -> getProg() >= 0, 20);
                    waitEvent(() -> getProg() < 0 && gameUI.getInventory().getFreeSpace() > 0, 2000);
                }
            }
            if (!noStop) {
                stopWithClick();
            }
            gameUI.craftwnd.hide();
            return makeFound || gameUI.getInventory().getFreeSpace() != size || !gameUI.hand.isEmpty();
        }
        return true;
    }

    public static boolean craft(
            char[] command,
            NAlias special_pag,
            String name
    )
            throws InterruptedException {
        Widget spwnd = null;
        if(gameUI.craftwnd!=null) {
            spwnd = gameUI.craftwnd.makeWidget;
        }
        if (spwnd == null || !checkName(name, gameUI.craftwnd.makeWidget.rcpnm)) {
            ((NMenuGrid) gameUI.menu).globtype('q');
            for (Character ch : command) {
                ((NMenuGrid) gameUI.menu).globtype(ch);
            }
            for (MenuGrid.PagButton btn : gameUI.menu.curbtns) {
                Resource res = btn.pag.res();
                if (res != null) {
                    if (checkName(res.name, special_pag)) {
                        gameUI.menu.use(btn, new MenuGrid.Interaction(), true);
                    }
                }
            }
            waitEvent(() -> (gameUI.craftwnd!=null && gameUI.craftwnd.makeWidget!=null), 500);
            waitEvent(() -> gameUI.craftwnd.makeWidget!=null && checkName(name, gameUI.craftwnd.makeWidget.rcpnm), 50);
            spwnd = gameUI.craftwnd.makeWidget;
        }
        if (spwnd != null) {
            spwnd.wdgmsg("make", 1);
            waitEvent(() -> getProg() >= 0, 30);
            waitEvent(() -> getProg() < 0, 1000);
            gameUI.craftwnd.hide();
            /// Ждем завершения крафта
            while (getProg() < 0 || gameUI.getInventory().getFreeSpace() > 0) {
                if (gameUI.getInventory().getFreeSpace() == 0)
                    break;
                waitEvent(() -> getProg() >= 0, 30);
                if (getProg() < 0)
                    break;
                if (gameUI.getInventory().getFreeSpace() == 0)
                    break;
                waitEvent(() -> getProg() < 0, 100);
            }
            stopWithClick();
            return true;
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

    public static void activate(Gob gob) {
        gameUI.map.wdgmsg("click", Coord.z, gob.rc.floor(posres), 3, 0, 0, (int) gob.id, gob.rc.floor(posres),
                0, -1);
    }

    public static void activate(Gob gob, int flag) {
        gameUI.map.wdgmsg("click", Coord.z, gob.rc.floor(posres), 3, 0, 0, (int) gob.id, gob.rc.floor(posres),
                0, flag);
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

    public static boolean isIt(
            final Shopbox shopbox,
            final NAlias regEx
    ) {
        if (shopbox != null) {
            try {
                /// Запрашиваем ресур
                Resource res = null;
                res = shopbox.getres();
                if (res != null) {
                    /// Проверяем имя на соответствие
                    return checkName(res.name, regEx);
                }
            } catch (Loading e) {
            }
        }
        return false;
    }

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
                    if(((NGItem)item.item).dfname!=null)
                        return checkName(res.name, regEx) || checkName(((NGItem)item.item).dfname, regEx);
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

    public static NGameUI getGameUI() {
        return gameUI;
    }

    public static NUI getUI() {
        return nui;
    }
    public static long getTickId() {
        return nui.tickId;
    }
    public static Gob getGob(long id){
        if(gameUI!=null)
            return gameUI.ui.sess.glob.oc.getgob(id);
        return null;
    }

    public static void setSpeedget(Speedget speed) {
        speedget = speed;
        speedValue.ifPresent(speed::set);
    }

    public static boolean dropFrom(
            GItem item,
            String cap
    )
            throws InterruptedException {
        int counter = 0;
        while (gameUI.getInventory(cap).isInInventory(item) && counter < 20) {
            item.wdgmsg("drop", item.sz, gameUI.map.player().rc, 0);
            Thread.sleep(50);
            counter++;
        }
        return !gameUI.getInventory().isInInventory(item);
    }

    public static double getContentNumber(WItem item)
            throws InterruptedException {
        String value = getContent(item.item);
        if (value != null)
            return Double.parseDouble(value.substring(0, value.indexOf('\040')));
        return 0;
    }

    public static boolean transferToInventory(
            String name,
            Coord coord
    )
            throws InterruptedException {
        if (gameUI.vhand != null) {
            NInventory inv = gameUI.getInventory(name);
            int fs = inv.getFreeSpace();
            if (coord.x != -1) {
                int counter = 0;
                while ((fs == inv.getFreeSpace() || gameUI.vhand != null) && counter != 20) {
                    inv.wdgmsg("drop", coord);
                    Thread.sleep(100);
                    counter++;
                }
                return fs != inv.getFreeSpace();
            }
            return false;
        }
        return true;
    }


    public static boolean transferToInventory()
            throws InterruptedException {
        if (gameUI.vhand != null) {
            NInventory inv = gameUI.getInventory();
            int fs = inv.getFreeSpace();
            Coord placePos = gameUI.getInventory().getFreeCoord(gameUI.vhand);
            if (placePos.x != -1) {
                int counter = 0;
                while ((fs == inv.getFreeSpace() || gameUI.vhand != null) && counter != 20) {
                    inv.wdgmsg("drop", placePos);
                    Thread.sleep(100);
                    counter++;
                }
                return fs != inv.getFreeSpace();
            }
            return false;
        }
        return true;
    }

    public static void activateRoastspit(Gob.Overlay ol) {
        gameUI.map.wdgmsg("itemact", Coord.z, ol.gob.rc.floor(posres), 0, 1, (int)  ol.gob.id,
                ol.gob.rc.floor(posres), ol.id, -1);
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



    public static void activateItem(GItem item) {
        item.wdgmsg("iact", Coord.z, 1);
    }

    public static void takeFromEarth(Gob gob) throws InterruptedException {
            gameUI.map.wdgmsg("click", Coord.z, gob.rc.floor(posres), 3, 0, 1, (int) gob.id,
                    gob.rc.floor(posres), 0, -1);
            waitEvent(()->getGob(gob.id)==null,500);
    }

    public static double getAmount(GItem item) {
        for(ItemInfo inf : item.info()){
            if(inf instanceof GItem.Amount){
                return ((GItem.Amount)inf).itemnum();
            }
        }
        return 1;
    }

    public static void digSnow(Coord2d pos)
            throws InterruptedException {
            if (!gameUI.hand.isEmpty()) {
                drop(gameUI.vhand.item);
            }
            NUtils.command(new char[]{'q', 'a', 'd'});
            Coord pltc = new Coord2d(pos.x / 11, pos.y / 11).floor();
            Coord2d pCoord = new Coord2d((pltc).x * tilesz.x, (pltc).y * tilesz.y);
            gameUI.map.wdgmsg("click", Coord.z, pCoord.floor(posres), 1, 0);
            NUtils.waitEvent (() -> !gameUI.ui.sess.glob.map.tilesetr(gameUI.ui.sess.glob.map.gettile(pltc)).name
                    .contains("snow") ,500);
    }

    public static double getFuelLvl(String cap,Color c) {
        Window inv;
        if ((inv = getGameUI().getWindow(cap)) != null) {
            for (Widget w = inv.lchild; w != null; w = w.prev) {
                if (w instanceof VMeter) {
                    for (LayerMeter.Meter meter : ((VMeter) w).meters) {
                        if (meter.c.getRed() == c.getRed() && meter.c.getBlue() == c.getBlue() && meter.c.getGreen() == c.getGreen())
                            return meter.a;
                    }
                }
            }
        }
        return 0;
    }

    public static boolean isLifted(Gob gob) {
        return (gob.getattr(Following.class)!=null && gob.getattr(Following.class).tgt==gameUI.map.player().id);
    }


    static class AutoBot implements Runnable{

        @Override
        public void run() {
            while (getGameUI().map==null || !getGameUI ().ui.sess.glob.map.isLoaded()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            switch (NConfiguration.botmod.bot){
                case "Dreamer":
                    new Thread(new DreamHarvester(gameUI)).start();
                    break;
                case "Steel":
                    new Thread(new FillSteel(gameUI)).start();
                    break;
                case "Truffle":
                    new Thread(new Truffle(gameUI)).start();
                    break;
                case "GobFinder":
                    new Thread(new NomadFinderBot(gameUI)).start();
                    break;
                case "Candleberry":
                    new Thread(new Candleberry(gameUI)).start();
                    break;
                case "Smelter":
                    new Thread(new Smelter(gameUI)).start();
                    break;
                case "Clay":
                    new Thread(new CollectClay(gameUI)).start();
                    break;
            }
        }
    }

    public static void setGameUI(NGameUI nGameUI) {
        gameUI = nGameUI;
        NTimer.start(gameUI);

        if(NConfiguration.botmod!=null){
            new Thread(new AutoBot()).start();
        }
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

    public static void setUI(NUI vnui) {
        nui = vnui;
    }

    public static String getPath() {
        File jarFile = null;
        try {
            CodeSource codeSource = NUtils.class.getProtectionDomain().getCodeSource();
            jarFile = new File(codeSource.getLocation().toURI().getPath());
            String jarDir = jarFile.getParentFile().getPath();
            return jarDir;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean checkContent(
            GItem item,
            NAlias candidates
    ) {
        boolean result = false;
        for (Object info : item.rawinfo.data) {
            if (info instanceof Object[]) {
                Object[] info_array = (Object[]) info;
                for (Object subinfo : info_array) {
                    if (subinfo instanceof Object[]) {
                        Object[] data = (Object[]) info;
                        for (Object unit : data) {
                            if (unit instanceof Object[]) {
                                Object[] subunits = (Object[]) unit;
                                for (Object subunit : subunits) {
                                    if (subunit instanceof Object[]) {
                                        Object[] subdatas = (Object[]) subunit;
                                        for (Object subdata : subdatas) {
                                            if (subdata instanceof String) {
                                                //                                                System.out.println ( ( String ) subdata );
                                                if (checkName((String) subdata, candidates)) {
                                                    result = true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public static void command(
            char[] command
    ) {
        ((NMenuGrid) gameUI.menu).globtype('q');
        for (Character ch : command) {
            ((NMenuGrid) gameUI.menu).globtype(ch);
        }
        ((NMenuGrid) gameUI.menu).globtype('q');
    }

    public static float getDeltaZ() {
        return nui.getDeltaZ();
    }

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

    public static Pair<Coord, Coord> clipLine(Coord a, Coord b, Coord ul, Coord br) {
        // Define the x/y clipping values for the border.
        double edgeLeft = ul.x;
        double edgeRight = br.x;
        double edgeBottom = ul.y;
        double edgeTop = br.y;

        // Define the start and end points of the line.
        double x0src = a.x;
        double y0src = a.y;
        double x1src = b.x;
        double y1src = b.y;

        double t0 = 0.0;
        double t1 = 1.0;
        double xdelta = x1src - x0src;
        double ydelta = y1src - y0src;
        double p = 0, q = 0, r;

        for (int edge = 0; edge < 4; edge++) {   // Traverse through left, right, bottom, top edges.
            if(edge == 0) {
                p = -xdelta;
                q = -(edgeLeft - x0src);
            }
            if(edge == 1) {
                p = xdelta;
                q = (edgeRight - x0src);
            }
            if(edge == 2) {
                p = -ydelta;
                q = -(edgeBottom - y0src);
            }
            if(edge == 3) {
                p = ydelta;
                q = (edgeTop - y0src);
            }
            if(p == 0 && q < 0) return null;   // Don't draw line at all. (parallel line outside)
            r = q / p;

            if(p < 0) {
                if(r > t1) return null;         // Don't draw line at all.
                else if(r > t0) t0 = r;         // Line is clipped!
            } else if(p > 0) {
                if(r < t0) return null;      // Don't draw line at all.
                else if(r < t1) t1 = r;      // Line is clipped!
            }
        }

        return new Pair<>(
                new Coord((int) (x0src + t0 * xdelta), (int) (y0src + t0 * ydelta)),
                new Coord((int) (x0src + t1 * xdelta), (int) (y0src + t1 * ydelta))
        );
    }

    public static String timestamp() {
        return new SimpleDateFormat("HH:mm").format(new Date());
    }
    public static String timestamp(String text) {
        return String.format("[%s] %s", timestamp(), text);
    }

    public static NEquipory getEquipment(){
            if ( gameUI.equwnd != null ) {
                for ( Widget w = gameUI.equwnd.lchild ; w != null ; w = w.prev ) {
                    if ( w instanceof Equipory ) {
                        return ( NEquipory ) w;
                    }
                }
            }
            return null;
    }

    public static String getInfo(
            final GItem item
    ) {
        if (item != null) {
            try {
                /// Запрашиваем информацию по предмету
                for (ItemInfo info : item.info()) {
                    if (info instanceof ItemInfo.Name) {
                        return ((ItemInfo.Name) info).str.text;
                    }
                }
            } catch (Loading e) {
            }
        }
        return null;
    }

    public static String getInfo(
            final WItem item
    ) {
        return getInfo(item.item);
    }

    public static double getTableFepModifier(){
        double res = 1;
        Window table;
        if((table = getGameUI().getWindow("Table"))!=null){
            for( Widget wdg = table.child; wdg!=null; wdg = wdg.next){
                if (wdg instanceof Label) {
                    Label text = (Label) wdg;
                    if (text.texts.contains("Food")) {
                        res = res + Double.parseDouble(text.texts.substring(text.texts.indexOf(":") + 1, text.texts.indexOf("%"))) / 100.;
                        break;
                    }
                }
            }
        }
        return res;
    }

    public static double getRealmFepModifier() {
        double realmBuff = 1;
        for (Widget wdg = getGameUI().child; wdg != null; wdg = wdg.next) {
            if (wdg instanceof GameUI.Hidepanel) {
                for (Widget wdg1 = wdg.child; wdg1 != null; wdg1 = wdg1.next) {
                    if (wdg1 instanceof Bufflist) {
                        for (Widget pbuff = wdg1.child; pbuff != null; pbuff = pbuff.next) {
                            if (pbuff instanceof Buff) {
                                if (checkName(((Buff) pbuff).res.get().name, new NAlias("realm"))) {
                                    ArrayList<ItemInfo> realm = new ArrayList<>(((Buff) pbuff).info());
                                    for (Object data : realm) {
                                        if (data instanceof ItemInfo.AdHoc) {
                                            ItemInfo.AdHoc ah = ((ItemInfo.AdHoc) data);
                                            if (checkName(ah.str.text, new NAlias("Food event"))) {
                                                realmBuff = realmBuff + Double.parseDouble(ah.str.text.substring(ah.str.text.indexOf("+") + 1, ah.str.text.indexOf("%"))) / 100.;
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return realmBuff;
    }

    public static int getMaxBase(){
        return gameUI.chrwdg.base.stream().max(new Comparator<CharWnd.Attr>() {
            @Override
            public int compare(CharWnd.Attr o1, CharWnd.Attr o2) {
                return Integer.compare(o1.attr.base,o2.attr.base);
            }
        }).get().attr.base;
    }

    public static boolean isContentWater(GItem item)
            throws InterruptedException {
        String value = getContent(item);
        if (value != null)
            if (value.toLowerCase().contains("water")) {
                return true;
            }
        return false;
    }

    public static String getContent(
            GItem item
    )  {
        for (Object o : item.rawinfo.data) {
            if (o instanceof Object[]) {
                Object[] a = (Object[]) o;
                if (a[0] instanceof Integer) {
                    if (NUtils.checkName("ui/tt/cont", ((Session.CachedRes) item.glob().sess.rescache.get((Integer) a[0])).resnm)) {
                        for(Object o1 : (Object[])a[1])
                        {
                            if ( o1 instanceof Object[]) {
                                Object[] b = (Object[]) o1;
                                if (b[0] instanceof Integer) {
                                    if (NUtils.checkName("ui/tt/cn", ((Session.CachedRes) item.glob().sess.rescache.get((Integer) b[0])).resnm)) {
                                        return (String) b[1];
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static double getContentQuality(
            GItem item
    )  {
        for (Object o : item.rawinfo.data) {
            if (o instanceof Object[]) {
                Object[] a = (Object[]) o;
                if (a[0] instanceof Integer) {
                    if (NUtils.checkName("ui/tt/cont", ((Session.CachedRes) item.glob().sess.rescache.get((Integer) a[0])).resnm)) {
                        for(Object o1 : (Object[])a[1]) {
                            if (o1 instanceof Object[]) {
                                Object[] b = (Object[]) o1;
                                if (b[0] instanceof Integer) {
                                    if (NUtils.checkName("ui/tt/q/quality", ((Session.CachedRes) item.glob().sess.rescache.get((Integer) b[0])).resnm)) {
                                        return (double) (float) b[1];
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return -1;
    }

    public static float getQuality(
            GItem item
    )  {
        for (Object o : item.rawinfo.data) {
            if (o instanceof Object[]) {
                Object[] a = (Object[]) o;
                if (a[0] instanceof Integer) {
                    if (NUtils.checkName("ui/tt/q/quality", ((Session.CachedRes) item.glob().sess.rescache.get((Integer) a[0])).resnm)) {
                        return (float)a[1];
                    }
                }
            }
        }
        return -1.f;
    }

    public static String getName(NGItem item) {
        for (Object o : item.rawinfo.data) {
            if (o instanceof Object[]) {
                Object[] a = (Object[]) o;
                if (a[0] instanceof Integer) {
                    if (NUtils.checkName("ui/tt/defn", ((Session.CachedRes) item.glob().sess.rescache.get((Integer) a[0])).resnm)) {
                        return DefName.getname(item);
                    }
                }
            }
        }
        return null;
    }



    public static ItemInfo.Contents getContent(
            List<ItemInfo> iteminfo
    )  {
        while (true) {
            try {
                for (ItemInfo info : iteminfo) {
                    if (info instanceof ItemInfo.Contents) {
                        return (ItemInfo.Contents) info;
                    }
                }
                break;
            } catch (Loading ignored) {
            }
        }
        return null;
    }

    public static String getContentName(List<ItemInfo> item)
            throws InterruptedException {
        ItemInfo.Contents content = getContent(item);

        if (content != null) {
            for (ItemInfo info : content.sub) {
                if (info instanceof ItemInfo.Name) {
                    return ((ItemInfo.Name) info).str.text;
                }
            }
        }
        return null;
    }

    public static boolean isItInfo(
            final GItem item,
            final NAlias regEx
    ) {
        if (item != null) {
                return checkName(((NGItem)item).dfname, regEx);
        }
        return false;
    }

    public static boolean isIdleCurs() {
        if(getGameUI().ui.getcurs(Coord.z)!=null && (getGameUI().ui.getcurs(Coord.z).name!=null))
            return getGameUI().ui.getcurs(Coord.z).name.contains("arw");
        return false;
    }

    public static boolean isFeastCurs() {
        if(getGameUI().ui.getcurs(Coord.z)!=null && (getGameUI().ui.getcurs(Coord.z).name!=null))
            return getGameUI().ui.getcurs(Coord.z).name.contains("eat");
        return false;
    }


    public static Coord2d getTrellisCoord(Coord2d coord2d) {
        Coord sfcoord = coord2d.floor(MCache.tilesz);
        return new Coord2d((sfcoord).x * tilesz.x + tilesz.x / 2, (sfcoord).y * tilesz.y + tilesz.y / 2);
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

    public static boolean isOverlay(
            Gob gob
    ) {
        ConcurrentLinkedDeque<Gob.Overlay> core = (ConcurrentLinkedDeque<Gob.Overlay>) gob.ols;
        for (Gob.Overlay ov : core) {
            if (ov.res != null) {
                Resource res_ov = ov.res.get();
                if (res_ov != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isGobInArea(
            Gob gob,
            NArea area
    ) {
        return area.begin.x <= gob.rc.x && area.begin.y <= gob.rc.y && gob.rc.x <= area.end.x && gob.rc.y <= area.end.y;
    }

    public static boolean isCropInArea(
            Gob gob,
            NArea area
    ) {
        return area.begin.x+area.center.x <= gob.rc.x && area.begin.y+area.center.y <= gob.rc.y && gob.rc.x <= area.end.x+area.center.x && gob.rc.y <= area.end.y+area.center.y;
    }

    public static boolean isCropstgmaxval(Gob gob) {
        NProperties.Crop crop = ((NProperties.Crop)gob.getProperties(NProperties.Crop.class));
        if(crop!=null)
            return crop.maxstage==gob.modelAttribute;
        return false;
    }

    public static boolean isSpecialStageCrop(Gob gob) {
        return gob.getCrop().specstage == gob.modelAttribute;
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

    public static Coord getDirection_shift(long ignored) {
        Gob gob = findNearestObject(ignored);
        if (gob == null) {
            System.out.println("No nearest GOB");
        } else {
            //            System.out.println ( gob.getres ().name );
            double dx = Math.abs(gameUI.map.player().rc.x - gob.rc.x);
            double dy = Math.abs(gameUI.map.player().rc.y - gob.rc.y);
            if (dx > dy) {
                return new Coord(gameUI.map.player().rc.x < gob.rc.x ? -1 : 1, 0);
            } else {
                return new Coord(0, gameUI.map.player().rc.y < gob.rc.y ? -1 : 1);
            }
        }
        return Coord.z;
    }

    public static NAlias getMenuOpt(
            final Gob gob,
            final ArrayList<Pair<String, String>> regEx
    ) {
        NAlias resa = new NAlias();
        if (gob != null) {
            try {
                /// Запрашиваем ресур
                Resource res = null;
                res = gob.getres();
                if (res != null) {
                    /// Проверяем имя на соответствие
                    for (Pair<String, String> pair : regEx) {
                        if (res.name.contains(pair.a)) {
                            resa.keys.add(pair.b);
                        }
                    }
                }
            } catch (Loading e) {
            }
        }
        return resa;
    }

    public static void freeHands(
            NAlias exceptions
    )
            throws InterruptedException {
        /// Освобождаем слоты рук
        if(NUtils.getEquipment()!= null && (NUtils.getEquipment().quickslots[6]!=null || NUtils.getEquipment().quickslots[7]!=null)){
            {
                WItem wbelt = Finder.findDressedItem(new NAlias("belt"));

                if (wbelt == null) {
                    //NUtils.getGameUI().setfocus(NUtils.getGameUI().getInventory());
                    freeSlotById(6, exceptions);
                    freeSlotById(7, exceptions);
                } else {
                    for (int i = 6; i <= 7; i++) {
                        if (((NInventory)wbelt.item.contents).getFreeSpace() != 0) {
                            if (gameUI.getEquipment().quickslots[i] != null) {
                                ArrayList<GItem> items = gameUI.getInventory().getItems();
                                freeSlotById(i, exceptions);
                                for(GItem item : gameUI.getInventory().getItems()){
                                    if(!items.contains(item)){
                                        item.wdgmsg("transfer", Coord.z, 1);
                                        NUtils.waitEvent(() -> NUtils.getGameUI().getInventory().getItem(item) == null, 50);
                                        break;
                                    }
                                }
                            }
                        } else {
                            freeSlotById(6, exceptions);
                            freeSlotById(7, exceptions);
                            return;
                        }
                    }
                }
            }
        }
    }

    public static double getProg(){
        return gameUI.getProg();
    }

    public static double getStamina() {
        IMeter.Meter stam = getGameUI().getmeter ( "stam", 0 );
        return stam.a;
    }

    public static int getEqupmentId () {
        int id = 0;
        /// Проверяем все зарегистрированные виджеты
        for ( Map.Entry<Widget, Integer> widget : getUI().rwidgets.entrySet () ) {
            if ( widget.getKey () instanceof NEquipory ) {
                /// Если проверяемый виджет - Экипировка возвращаем id
                id = widget.getValue ();
            }
        }
        return id;
    }

    public static void stopWithClick()
            throws InterruptedException {

        gameUI.map.wdgmsg("click", Coord.z, gameUI.map.player().rc.floor(posres), 1, 0);
        gameUI.map.wdgmsg("cancel");
        waitEvent(() -> getProg() < 0, 20);
        if (!gameUI.hand.isEmpty()) {
            gameUI.map.wdgmsg("drop", Coord.z, gameUI.map.player().rc.floor(posres), 0);
        }
        waitEvent(() -> gameUI.hand.isEmpty(), 20);
        gameUI.map.wdgmsg("click", Coord.z, gameUI.map.player().rc.floor(posres), 3, 0);
    }

    public static boolean transferToEquipment(int id)
            throws InterruptedException {
        /// Проверяем что запрашиваемый слот пуст
        if (gameUI.getEquipment().quickslots[id] == null) {
            /// Маскируемся что конечный виджет сброса предмета это экипировка
            gameUI.ui.rcvr.rcvmsg(getEqupmentId(), "drop", id);
            /// Ожидаем появления предмета в запрашиваемом слоте
            while (gameUI.getEquipment().quickslots[id] == null) {
                Thread.sleep(20);
            }
            return true;
        }
        /// Если слот изначально занят то возвращаем false
        return false;
    }

    public static void transferToEquipmentHands()
            throws InterruptedException {
        /// Проверяем есть ли в курсоре пердмет
        if (!gameUI.hand.isEmpty()) {
            /// Пытаемся одеть предмет в слоты рук
            if (!transferToEquipment(6)) {
                transferToEquipment(7);
            }
        }
    }

    public static boolean freeSlotById(
            int id,
            NAlias exceptions
    )
            throws InterruptedException {
        NEquipory equipory = gameUI.getEquipment();
        if (equipory.quickslots[id] != null) {
            /// Проверяем отсутствие соответствия заданным ключам исключениями
            if (!isIt(equipory.quickslots[id].item, exceptions)) {
                int size = NUtils.getGameUI().getInventory().getFreeSpace();
                GItem item = equipory.quickslots[id].item;
                equipory.quickslots[id].item.wdgmsg("transfer", Coord.z, 0);
                /// Пока предмет не снят - спим
                NUtils.waitEvent(()->size != NUtils.getGameUI().getInventory().getFreeSpace(),20);
                return true;
            }
        }
        return false;
    }

    public static boolean waitEvent(
            WaitAction.Expression exp,
            int delay
    )
            throws InterruptedException {
        return waitEvent(exp,delay,50);
    }

    public static boolean waitEvent(
            WaitAction.Expression exp,
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

    public static void takeItemToHand(GItem item) {
        item.wdgmsg("take", Coord.z);
    }

    public static boolean transferAlltoStockPile(
            NAlias names
    )
            throws InterruptedException {
        return transferAlltoStockPile(names, -1);
    }
    public static boolean transferAlltoStockPile(
            NAlias names,
            double q
    )
            throws InterruptedException {
        /// Дожидаемся открытия окна
        Window spwnd = gameUI.getWindow("Stockpile");
        for (Widget sp = spwnd.lchild; sp != null; sp = sp.prev) {
            /// Выбираем внутренний контейнер
            if (sp instanceof NISBox) {
                /// Для каждого элемента из списка кандидатов выполняем процедуру переноса
                /// Находим предмет в инвентаре
                ArrayList<GItem> wItems = gameUI.getInventory().getItems(names);
                /// Вычисляем оставшееся свободное место в пайле
                for (GItem wItem : wItems) {
                    if(((NGItem)wItem).quality()>=q) {
                        int freeSpace = ((NISBox) sp).getFreeSpace();
                        /// Передаем предмет и дожидаемся изменения пайла
                        int count = 0;
                        wItem.wdgmsg("transfer", wItem.sz, 1);
                        while (freeSpace == ((NISBox) sp).getFreeSpace() && count < 50) {
                            Thread.sleep(10);
                            count += 1;
                        }
                        if (((NISBox) sp).getFreeSpace() == 0) {
                            break;
                        }
                    }
                }
                /// Возвращаем осталось ли место после переноса
                return ((NISBox) sp).getFreeSpace() != 0;
            }
        }
        /// Если окно так и не появилось бросаем исключение об отсутствии пайла
        return false;
    }

    public static boolean checkGobFlower(
            NAlias name,
            Gob gob
    )
            throws InterruptedException {
        while (NFlowerMenu.instance != null) {
            NFlowerMenu.stop();
            Thread.sleep(30);
        }
        gameUI.map.wdgmsg("click", Coord.z, gob.rc.floor(posres), 3, 0, 1, (int) gob.id, gob.rc.floor(posres),
                0, -1);
        waitEvent( ()->NFlowerMenu.instance != null,20);
        if (NFlowerMenu.instance != null) {
            if (NFlowerMenu.instance.findInCurrentFlower(name)) {
                return true;
            } else {
                NFlowerMenu.stop();
            }
        }
        return false;
    }

    public static boolean drop(GItem item)
            throws InterruptedException {
        int counter = 0;
        while (gameUI.getInventory().isInInventory(item) && counter < 20) {
            item.wdgmsg("drop", item.sz, gameUI.map.player().rc, 0);
            Thread.sleep(50);
            counter++;
        }
        return !gameUI.getInventory().isInInventory(item);
    }

    public static void destroyFCNbndl(GItem item){
        item.wdgmsg("iact", item.sz, 3);
    }

    public static boolean transferItem(
            NInventory inv,
            GItem item
    )
            throws InterruptedException {
        int space = inv.getFreeSpace();
        if (item != null) {
            item.wdgmsg("transfer", item.sz, 1);
            NUtils.waitEvent(() -> space != inv.getFreeSpace(), 200);
            return space != inv.getFreeSpace();
        }
        return false;
    }


    public static boolean transferItem(
            NInventory inv,
            GItem item,
            NInventory targetinv
    )
            throws InterruptedException {
        int space = inv.getFreeSpace();
        int oldspace = targetinv.getFreeSpace();
        if (item != null) {
            if(targetinv.getNumberFreeCoord(item)==0)
                return false;
            item.wdgmsg("transfer", item.sz, 1);
            waitEvent(() -> space != inv.getFreeSpace() && targetinv.getFreeSpace()!=oldspace, 200);
            return oldspace != targetinv.getFreeSpace();
        }
        return false;
    }

    public static boolean takeItemFromPile()
            throws InterruptedException {
        Window spwnd = gameUI.getWindow("Stockpile");
        if (spwnd != null) {
            for (Widget sp = spwnd.lchild; sp != null; sp = sp.prev) {
                /// Выбираем внутренний контейнер
                if (sp instanceof NISBox) {
                    final NISBox nis = (NISBox) sp;
                    /// Вычисляем свободное место в пайле
                    int freeSpace = nis.getFreeSpace();
                    int minv_freeSpace = gameUI.getInventory().getFreeSpace();

                    if (gameUI.getInventory().getFreeSpace() > 0) {
                        /// Берем один предмет
                        sp.wdgmsg("xfer");
                        NUtils.waitEvent(() -> (freeSpace != nis.getFreeSpace()), 50, 10);
                        if(freeSpace != nis.getFreeSpace())
                        {
                            NUtils.waitEvent(() -> (minv_freeSpace != gameUI.getInventory().getFreeSpace() || findBundle()), 100, 10);
                        }
                        destroyAllBundle();
                        return !(freeSpace == nis.getFreeSpace());
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    static boolean findBundle() throws InterruptedException {
        for(GItem item : gameUI.getInventory().getItems()) {
            if (item.contents != null) {
                return true;
            }
        }
        return false;
    }

    static void destroyAllBundle() throws InterruptedException {
        for(GItem item : gameUI.getInventory().getItems()) {
            if (item.contents != null) {
                item.wdgmsg("iact", item.sz, 3);
                NUtils.waitEvent(()->NUtils.getGameUI().getInventory().wmap.get(item)==null,50,10);
            }
        }
    }

    public static String prettyResName(String biome) {
        String res = data_titles.get(biome);
        if (res != null) {
            return res;
        }
        else return "???";
    }

    public static void drop() {
        gameUI.map.wdgmsg("drop", Coord.z, gameUI.map.player().rc.floor(posres), 0);
    }

    public static boolean lift(
            Gob gob
    )
            throws InterruptedException {
        gameUI.ui.rcvr.rcvmsg(gameUI.getMenuGridId(), "act", "carry");
        gameUI.map.wdgmsg("click", Coord.z, gob.rc.floor(posres), 1, 0, 0, (int) gob.id, gob.rc.floor(posres),
                0, -1);
        waitEvent(()->NUtils.isPose(gameUI.map.player(),new NAlias("banzai")),200);
        return false;
    }



    public static class ContainerProp {
        public int fullMark;
        public String cap ="";
        public NAlias name;
    }

    public static ContainerProp getContainerType(
            NArea area,
            Gob target
    ) {
        ContainerProp result = new ContainerProp();
        if (Finder.findObjectsInArea(new NAlias("crate"), area).size() > 0 ||
                (target != null && isIt(target, new NAlias("crate")))) {
            result.fullMark = 16;
            result.cap = "Crate";
            result.name = new NAlias("crate");
        } else if (Finder.findObjectsInArea(new NAlias("largechest"), area).size() > 0 ||
                (target != null && isIt(target, new NAlias("largechest")))) {
            result.fullMark = 16;
            result.cap = "Large Chest";
            result.name = new NAlias("largechest");
        } else if (Finder.findObjectsInArea(new NAlias("oven"), area).size() > 0 ||
                (target != null && isIt(target, new NAlias("oven")))) {
            result.fullMark = 1024;
            result.cap = "Oven";
            result.name = new NAlias("oven");
        } else if (Finder.findObjectsInArea(new NAlias("cheeserack"), area).size() > 0 ||
                (target != null && isIt(target, new NAlias("cheeserack")))) {
            result.fullMark = 1024;
            result.cap = "Rack";
            result.name = new NAlias("cheeserack");
        } else if (Finder.findObjectsInArea(new NAlias("cupboard"), area).size() > 0 ||
                (target != null && isIt(target, new NAlias("cupboard")))) {
            result.fullMark = 16;
            result.cap = "Cupboard";
            result.name = new NAlias("cupboard");
        } else if (Finder.findObjectsInArea(new NAlias("chest"), area).size() > 0 ||
                (target != null && isIt(target, new NAlias("chest")))) {
            result.fullMark = 16;
            result.cap = "Chest";
            result.name = new NAlias("chest");
        } else if (Finder.findObjectsInArea(new NAlias("metalcabinet"), area).size() > 0 ||
                (target != null && isIt(target, new NAlias("metalcabinet")))) {
            result.fullMark = 64;
            result.cap = "Metal Cabinet";
            result.name = new NAlias("metalcabinet");
        } else if (Finder.findObjectsInArea(new NAlias("frame"), area).size() > 0 ||
                (target != null && isIt(target, new NAlias("frame")))) {
            result.fullMark = 700;
            result.cap = "Frame";
            result.name = new NAlias("frame");
        } else if (Finder.findObjectsInArea(new NAlias("kiln"), area).size() > 0 ||
                (target != null && isIt(target, new NAlias("kiln")))) {
            result.fullMark = 1024;
            result.cap = "Kiln";
            result.name = new NAlias("kiln");
        } else if (Finder.findObjectsInArea(new NAlias("primsmelter"), area).size() > 0 ||
                (target != null && isIt(target, new NAlias("primsmelter")))) {
            result.fullMark = 1024;
            result.cap = "Furnace";
            result.name = new NAlias("primsmelter");
        } else if (Finder.findObjectsInArea(new NAlias("smelter"), area).size() > 0 ||
                (target != null && isIt(target, new NAlias("smelter")))) {
            result.fullMark = 8192;
            result.cap = "Smelter";
            result.name = new NAlias("smelter");
        } else if (Finder.findObjectsInArea(new NAlias("fineryforge"), area).size() > 0 ||
                (target != null && isIt(target, new NAlias("fineryforge")))) {
            result.fullMark = 1024;
            result.cap = "Finery Forge";
            result.name = new NAlias("fineryforge");
        } else if (Finder.findObjectsInArea(new NAlias("stockpile"), area).size() > 0 ||
                (target != null && isIt(target, new NAlias("stockpile")))) {
            result.fullMark = 1024;
            result.cap = "Stockpile";
            result.name = new NAlias("stockpile");
        } else if (Finder.findObjectsInArea(new NAlias("chickencoop"), area).size() > 0 ||
                (target != null && isIt(target, new NAlias("chickencoop")))) {
            result.fullMark = 1024;
            result.cap = "Chicken Coop";
            result.name = new NAlias("chickencoop");
        } else if (Finder.findObjectsInArea(new NAlias("rabbithutch"), area).size() > 0 ||
                (target != null && isIt(target, new NAlias("rabbithutch")))) {
            result.fullMark = 1024;
            result.cap = "Rabbit Hutch";
            result.name = new NAlias("rabbithutch");
        } else if (Finder.findObjectsInArea(new NAlias("smokeshed"), area).size() > 0 ||
                (target != null && isIt(target, new NAlias("smokeshed")))) {
            result.fullMark = 1024;
            result.cap = "Smoke shed";
            result.name = new NAlias("smokeshed");
        } else if (Finder.findObjectsInArea(new NAlias("htable"), area).size() > 0 ||
                (target != null && isIt(target, new NAlias("htable")))) {
            result.fullMark = 0;
            result.cap = "Herbalist Table";
            result.name = new NAlias("htable");
        } else if (Finder.findObjectsInArea(new NAlias("oldtrunk"), area).size() > 0 ||
                (target != null && isIt(target, new NAlias("oldtrunk")))) {
            result.fullMark = 1024;
            result.cap = "Old Trunk";
            result.name = new NAlias("oldtrunk");
        }else if (Finder.findObjectsInArea(new NAlias("table"), area).size() > 0 ||
                (target != null && isIt(target, new NAlias("table")))) {
            result.fullMark = 1024;
            result.cap = "Table";
            result.name = new NAlias("table");
        } else if (Finder.findObjectsInArea(new NAlias("ttub"), area).size() > 0 ||
                (target != null && isIt(target, new NAlias("ttub")))) {
            result.fullMark = 1024;
            result.cap = "Tub";
            result.name = new NAlias("ttub");
        }
        return result;
    }

    public static ContainerProp getContainerType(Gob gob) {
        return getContainerType(new NArea(), gob);
    }


    public static ContainerProp getContainerType(NArea area) {
        if (area != null) {
            return getContainerType(area, null);
        } else {
            return new ContainerProp();
        }
    }

    public static void activateItem(Gob gob) {
        gameUI.map
                .wdgmsg("itemact", Coord.z, gob.rc.floor(posres), 0, 0, (int) gob.id, gob.rc.floor(posres), 0,
                        -1);
    }

    public static void activateItemToPile(Gob gob) {
        gameUI.map
                .wdgmsg("itemact", Coord.z, gob.rc.floor(posres), 0, 0, (int) gob.id, gob.rc.floor(posres), 0,
                        0);
    }

    public static ContainerProp getContainerType(AreasID mark) {
        return getContainerType(Finder.findNearestMark(mark));
    }

    public static boolean build(
            char[] command,
            NAlias specisal,
            Coord2d coord,
            double rotation,
            String name,
            NArea area
    )
            throws InterruptedException {
        Window spwnd = gameUI.getWindow(name);

        ((NMenuGrid) gameUI.menu).globtype('q');
        for (Character ch : command) {
            ((NMenuGrid) gameUI.menu).globtype(ch);
        }
        for (MenuGrid.PagButton btn : gameUI.menu.curbtns) {
            Resource res = btn.pag.res();
            if (res != null) {
                if (checkName(res.name, specisal)) {
                    gameUI.menu.use(btn, new MenuGrid.Interaction(), true);
                }
            }
        }
        do {
            gameUI.getMap()
                    .wdgmsg("place", coord.floor(posres), (int) Math.round(rotation * 32768 / Math.PI), 1, 0);
            Thread.sleep(50);
        }
        while (Finder.findObjectInArea(new NAlias("consobj"), 1000, area) == null);

        waitEvent(()->gameUI.getWindow(name)!=null,50);
        spwnd = gameUI.getWindow(name);


        if (spwnd != null) {
            for (Widget sp = spwnd.lchild; sp != null; sp = sp.prev) {
                /// Выбираем внутренний контейнер
                if (sp instanceof Button) {
                    ((Button) sp).click();
                    Thread.sleep(100);
                }
            }
            waitEvent(()->getProg()>=0, 20);
            /// Ждем завершения крафта
            waitEvent(()->getProg()<0, 1000);
            return true;
        }
        return false;
    }

    public static int checkHerbCount(
            final Gob gob
    ) {
        int count = 0;
        if (isIt(gob, new NAlias("htable"))) {
            {
                for (Gob.Overlay ov : gob.ols) {
                    if (ov.spr.getClass().getSimpleName().equals("Equed")) {
                        count += 1;
                    }
                }
            }
        }
        return count;
    }

    public static boolean buildCurrent(
            String name
    )
            throws InterruptedException {
        Window spwnd = gameUI.getWindow(name);

        if (spwnd != null) {
            for (Widget sp = spwnd.lchild; sp != null; sp = sp.prev) {
                /// Выбираем внутренний контейнер
                if (sp instanceof Button) {
                    ((Button) sp).click();
                    break;
                }
            }
            waitEvent(()->getProg()>=0, 20);
            /// Ждем завершения крафта
            waitEvent(()->getProg()<0, 1000);
            return true;
        }
        return false;
    }

    public static void place(Coord2d coord2d) {
        gameUI.map.wdgmsg("click", Coord.z, coord2d.floor(posres), 3, 0);
    }

    public static boolean checkGobFlower(
            String name,
            Gob gob,
            int id
    )
            throws InterruptedException {
        while (NFlowerMenu.instance != null) {
            NFlowerMenu.stop();
            Thread.sleep(30);
        }
        gameUI.map.wdgmsg("click", Coord.z, gob.rc.floor(posres), 3, 0, 1, (int) gob.id, gob.rc.floor(posres),
                id, -1);
        waitEvent(()->NFlowerMenu.instance!=null,50);
        if (NFlowerMenu.instance != null) {
            if (NFlowerMenu.instance.findInCurrentFlower(name)) {
                return true;
            } else {
                NFlowerMenu.stop();
            }
        }
        return false;
    }

    public static boolean build(
            char[] command,
            Coord2d coord,
            double rotation,
            String name,
            NArea area
    )
            throws InterruptedException {
        Window spwnd = gameUI.getWindow(name);
//        gameUI.msg("Build coord: " + coord.toString());
        ((NMenuGrid) gameUI.menu).globtype('q');
        for (Character ch : command) {
            ((NMenuGrid) gameUI.menu).globtype(ch);
        }
        do {
            gameUI.getMap()
                    .wdgmsg("place", coord.floor(posres), (int) Math.round(rotation * 32768 / Math.PI), 1, 0);
            Thread.sleep(50);
        }
        while (Finder.findObjectInArea(new NAlias("consobj"), 1000, area) == null);

        //        gameUI.hand
        waitEvent(()->gameUI.getWindow(name)!=null,50);
        spwnd = gameUI.getWindow(name);

        if (spwnd != null) {
            for (Widget sp = spwnd.lchild; sp != null; sp = sp.prev) {
                /// Выбираем внутренний контейнер
                if (sp instanceof Button) {
                    ((Button) sp).click();
                    break;
                }
            }
            waitEvent(()->getProg()>=0, 20);
            /// Ждем завершения крафта
            waitEvent(()->getProg()<0, 1000);
            return true;
        }
        return false;
    }


    public static RosterWindow getRosterWindow(Class<? extends Entry> cattleRoster) throws InterruptedException {
        RosterWindow w;
        if((w = (RosterWindow)NUtils.getGameUI().getWindow("Cattle Roster")) == null) {
            NUtils.command(new char[]{'a', 'c'});
            NUtils.waitEvent(() -> NUtils.getGameUI().getWindow("Cattle Roster") != null, 500);

            w = (RosterWindow) NUtils.getGameUI().getWindow("Cattle Roster");
            NUtils.waitEvent(w::allLoaded, 1000);
        }
        w.show(cattleRoster);
        return w;
    }

    public static Entry getAnimalEntity(Gob gob, Class<? extends Entry> cattleRoster ){
        try {
            return (Entry) ((RosterWindow) NUtils.getGameUI().getWindow("Cattle Roster")).roster(cattleRoster).entries.get(((CattleId) gob.getattr(CattleId.class)).id);
        }catch (NullPointerException e){
            return null;
        }
    }

    public static Optional<Coord2d> intersect(Pair<Coord2d, Coord2d> lineA, Pair<Coord2d, Coord2d> lineB) {
        double a1 = lineA.b.y - lineA.a.y;
        double b1 = lineA.a.x - lineA.b.x;
        double c1 = a1 * lineA.a.x + b1 * lineA.a.y;

        double a2 = lineB.b.y - lineB.a.y;
        double b2 = lineB.a.x - lineB.b.x;
        double c2 = a2 * lineB.a.x + b2 * lineB.a.y;

        double delta = a1 * b2 - a2 * b1;
        if(delta == 0) {
            return Optional.empty();
        }
        return Optional.of(new Coord2d((float) ((b2 * c1 - b1 * c2) / delta), (float) ((a1 * c2 - a2 * c1) / delta)));
    }
}
