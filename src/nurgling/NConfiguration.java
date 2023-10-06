package nurgling;

import haven.*;
import haven.render.MixColor;
import haven.res.ui.tt.slot.Slotted;
import haven.res.ui.tt.stackn.Stack;
import nurgling.bots.settings.IngredientSettings;
import nurgling.bots.tools.Ingredient;
import nurgling.json.JSONArray;
import nurgling.json.JSONObject;
import nurgling.json.parser.JSONParser;
import nurgling.json.parser.ParseException;
import nurgling.tools.AreasID;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;

public class NConfiguration {
    public boolean showDebugInfo = false;
    public static final Text.Foundry fnd = new Text.Foundry(Text.sans, 14);
    public boolean isVerified = false;
    public boolean isSubscribed = false;
    public boolean isMinerCredo = false;
    public HashMap<String, MixColor> colors = new HashMap<>();
    private static final NConfiguration instance = new NConfiguration();
    public boolean disabledCheck = false;
    public boolean enablePfBoundingBoxes = false;
    public HashMap<String, Integer> playerSpeed_h = new HashMap<String, Integer>();
    public HashMap<String, Integer> horseSpeed_h = new HashMap<String, Integer>();
    public List<NLoginData> logins = new ArrayList<NLoginData>();
    public boolean autologin = false;
    public String autologin_user = "";
    public String autologin_character = "";
    public String checked = "";
    public String autobot = "";
    public String msg = "";
    public boolean render = true;
    public boolean restart = true;
    public String nomadPath = "";
    public String village;
    public boolean showCropStage = false;
    public boolean autoPicking = false;
    public boolean nightVision = false;

    public boolean showAreas = false;
    public double coefSubscribe = 0.5;
    public double coefVerif = 0.2;
    public double coefVar =0.3999;

    public int playerSpeed = 3;
    public int horseSpeed = 3;
    public int quickRange = 25;

    public boolean isEyed = false;
    public boolean isPaths = false;
    public boolean isGrid = false;

    public HashMap<String,HashMap<String, HashMap<Integer, String>>> allKeys = new HashMap<String,HashMap<String, HashMap<Integer, String>>>();

    public ArrayList<String> fishCandidates = new ArrayList<>();
    public boolean isInfo = true;
    public boolean collectFoodInfo = false;
    public boolean lockStudy = false;
    public boolean alarmGreyseal = false;
    public boolean isQuestInfoVisible = true;
    public boolean hideNature = true;
    public boolean showBB = false;

    public boolean autoFlower = false;
    public boolean autoSplitter = false;
    public boolean autoDropper = false;
    public boolean invert_hor = false;
    public boolean invert_ver = false;
    public String baseurl =" https://raw.githubusercontent.com/Katodiy/nurgling-release/master/ver";
    public boolean flatsurface = false;
    public boolean nextflatsurface = false;
    public boolean showCSprite = false;

    public boolean nextshowCSprite = false;
    public boolean minesup = false;

    public static void saveButtons(String name, NGameUI.NButtonBeltSlot[] custom) {
        String key = NUtils.getUI().sessInfo.username +"/" + NUtils.getUI().sessInfo.characterInfo.chrid;
        instance.allKeys.computeIfAbsent(key, k -> new HashMap<>());
        HashMap<String, HashMap<Integer, String>> customKeys  = instance.allKeys.get(key);

        if(customKeys!=null) {
            customKeys.computeIfAbsent(name, k -> new HashMap<Integer, String>());
            HashMap<Integer, String> keys = customKeys.get(name);
            if(keys!=null) {
                for (NGameUI.NButtonBeltSlot slot : custom) {
                    if(slot!=null) {
                        instance.allKeys.get(key).get(name).put(slot.idx, slot.button != null ? slot.button.name : null);
                    }
                }
            }
        }
        instance.write();
    }

    public void setPickActions(LinkedList<NAutoPickMenu.PickItem> pickList) {
        pickingActions.clear();
        for(NAutoPickMenu.PickItem item: pickList)
        {
            pickingActions.add(new PickingAction(item.text.texts,item.select.a));
        }
    }

    public void setQuickActions(LinkedList<NAutoActionMenu.PatternItem> pickList) {
        quickActions.clear();
        for(NAutoActionMenu.PatternItem item: pickList)
        {
            quickActions.add(item.text.texts);
        }
    }

    public Tiler getRidge() {
        if(ridge==null)
            ridge = customTileRes.get("ridge").tfac().create(7001, customTileRes.get("ridge"));
        return ridge;
    }

    public static class PickingAction{
        public String action;
        public boolean isEnable;

        public PickingAction(String action, boolean isEnable) {
            this.action = action;
            this.isEnable = isEnable;
        }
    }

    public static class GoatsHerd{
        public boolean ignoreChildren = false;
        public boolean disable_killing = false;
        public NInteger adultGoats = new NInteger(4);
        public NInteger breedingGap = new NInteger(10);
        public NDouble milkq = new NDouble(1.5);
        public NDouble woolq = new NDouble(0.33);
        public NDouble meatq = new NDouble(0.);
        public NDouble hideq = new NDouble(0.);
        public NDouble meatquanth =new NDouble(0.);
        public NDouble milkquanth =new NDouble(0.);
        public NDouble woolquanth =new NDouble(0.);
        public NDouble meatquan1 = new NDouble(0.);
        public NDouble meatquan2 = new NDouble(0.);
        public NDouble milkquan1 = new NDouble(0.);
        public NDouble milkquan2 = new NDouble(0.);
        public NDouble woolquan1 = new NDouble(0.);
        public NDouble woolquan2 = new NDouble(0.);
        public NDouble coverbreed = new NDouble(0.);
    }

    public static class HorsesHerd{
        public boolean ignoreChildren = false;
        public boolean disable_killing = false;
        public NInteger adultHorse = new NInteger(4);
        public NInteger breedingGap = new NInteger(10);
        public NInteger enduran = new NInteger(1);
        public NInteger meta = new NInteger(1);
        public NDouble meatq = new NDouble(0.);
        public NDouble hideq = new NDouble(0.);
        public NDouble meatquanth =new NDouble(0.);
        public NDouble stamth =new NDouble(0.);
        public NDouble meatquan1 = new NDouble(0.);
        public NDouble meatquan2 = new NDouble(0.);
        public NDouble stam1 = new NDouble(0.);
        public NDouble stam2 = new NDouble(0.);
        public NDouble coverbreed = new NDouble(0.);
    }

    public static class NInteger
    {
        Integer val;

        public NInteger(Integer val) {
            this.val = val;
        }

        public void set(Integer val)
        {
            this.val = val;
        }

        public Integer get() {
            return val;
        }

        @Override
        public String toString() {
            return val.toString();
        }
    }

    public static class NDouble
    {
        Double val;

        public NDouble(Double val) {
            this.val = val;
        }

        void set(Double val)
        {
            this.val = val;
        }

        public Double get() {
            return val;
        }

        @Override
        public String toString() {
            return val.toString();
        }
    }

    public static class SheepsHerd{
        public boolean ignoreChildren = false;
        public boolean disable_killing = false;
        public NInteger adultSheeps = new NInteger(4);
        public NInteger breedingGap = new NInteger(10);
        public NDouble milkq = new NDouble(1.5);
        public NDouble woolq = new NDouble(0.33);
        public NDouble meatq = new NDouble(0.);
        public NDouble hideq = new NDouble(0.);
        public NDouble meatquanth =new NDouble(0.);
        public NDouble milkquanth =new NDouble(0.);
        public NDouble woolquanth =new NDouble(0.);
        public NDouble meatquan1 = new NDouble(0.);
        public NDouble meatquan2 = new NDouble(0.);
        public NDouble milkquan1 = new NDouble(0.);
        public NDouble milkquan2 = new NDouble(0.);
        public NDouble woolquan1 = new NDouble(0.);
        public NDouble woolquan2 = new NDouble(0.);
        public NDouble coverbreed = new NDouble(0.);
    }

    public static class PigsHerd{
        public boolean ignoreChildren = false;
        public boolean disable_killing = false;
        public NInteger adultPigs = new NInteger(4);
        public NInteger breedingGap = new NInteger(10);
        public NDouble meatq = new NDouble(0.);
        public NDouble hideq = new NDouble(0.);
        public NDouble meatquanth =new NDouble(0.);
        public NDouble trufquanth =new NDouble(0.);
        public NDouble meatquan1 = new NDouble(0.);
        public NDouble meatquan2 = new NDouble(0.);
        public NDouble trufquan1 = new NDouble(0.);
        public NDouble trufquan2 = new NDouble(0.);
        public NDouble coverbreed = new NDouble(0.);
    }


    public static class CowsHerd{
        public boolean ignoreChildren = false;
        public boolean disable_killing = false;
        public NInteger adultCows = new NInteger(4);
        public NInteger breedingGap = new NInteger(10);
        public NDouble milkq = new NDouble(1.5);
        public NDouble meatq = new NDouble(0.);
        public NDouble hideq = new NDouble(0.);
        public NDouble meatquanth =new NDouble(0.);
        public NDouble milkquanth =new NDouble(0.);
        public NDouble meatquan1 = new NDouble(0.);
        public NDouble meatquan2 = new NDouble(0.);
        public NDouble milkquan1 = new NDouble(0.);
        public NDouble milkquan2 = new NDouble(0.);
        public NDouble coverbreed = new NDouble(0.);
    }

    public HashMap<String,GoatsHerd> goatsHerd = new HashMap<>();
    public String selected_goatsHerd = "";
    public HashMap<String,HorsesHerd> horsesHerd = new HashMap<>();
    public String selected_horsesHerd = "";
    public HashMap<String,SheepsHerd> sheepsHerd = new HashMap<>();
    public String selected_sheepsHerd = "";
    public HashMap<String,PigsHerd> pigsHerd = new HashMap<>();
    public String selected_pigsHerd = "";
    public HashMap<String,CowsHerd> cowsHerd = new HashMap<>();
    public String selected_cowsHerd = "";


    public ArrayList<PickingAction> pickingActions = new ArrayList<>();
    public ArrayList<String> quickActions = new ArrayList<>();

    public HashMap<String, ArrayList<String>> data_food = new HashMap<>();
    public HashMap<String, ArrayList<String>> data_drinks = new HashMap<>();
    public HashMap<String, String> data_vessel = new HashMap<>();
    public HashMap<String, GobIcon.Setting> iconsettings = new HashMap<>();

    public HashMap<String, ToolBelt> toolBelts = new HashMap<>();



    public Set<NPathVisualizer.PathCategory> pathCategories = new HashSet<>();
    public boolean isRealTime = true;

    public static void resetToolBelts() {
        NUtils.getGameUI().updateButtons();
    }

    public static BotMod botmod = null;
    public static class BotMod{
        String user;
        String password;
        String character;
        String bot;
        public String nomad;

        public BotMod(String user, String password, String character, String bot, String nomad) {
            this.user = user;
            this.password = password;
            this.character = character;
            this.bot = bot;
            this.nomad = nomad;
        }
    }
    public static void enableBotMod(String path) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(path), "UTF-8"));

            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            botmod = new BotMod((String) jsonObject.get("user"), (String) jsonObject.get("password"), (String) jsonObject.get("character"), (String) jsonObject.get("bot"), (String) jsonObject.get("nomad"));
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

    }

    public class ToolBelt{
        public boolean isVertical = false;
        public boolean isLocked = false;
        public KeyBinding [] toolKeys;
        public boolean isEnable = true;

        public ToolBelt(boolean isVertical, boolean isLocked, KeyBinding[] toolKeys) {
            this.isVertical = isVertical;
            this.isLocked = isLocked;
            this.toolKeys = toolKeys;
        }
    }

    public class Ring{
        public boolean isEnable;
        public double size;

        public Ring(boolean isEnable, double size) {
            this.isEnable = isEnable;
            this.size = size;
        }
    }
    public HashMap<String, Ring> rings = new HashMap<>();

    public class ArrowProp {
        public ArrowProp(boolean arrow, boolean ring, boolean mark, boolean mark_target) {
            this.arrow = arrow;
            this.ring = ring;
            this.mark = mark;
            this.mark_target = mark;
        }

        public boolean arrow;
        public boolean ring;
        public boolean mark;
        public boolean mark_target;
    }
    public HashMap<String, ArrowProp> players = new HashMap<>();
    public HashMap<String, DragWdg> dragWidgets = new HashMap<>();
    public HashMap<String, Coord>  resizeWidgets = new HashMap<>();
    public class DragWdg{
        public boolean locked = false;
        public Coord coord;

        public DragWdg(boolean locked, Coord coord) {
            this.locked = locked;
            this.coord = coord;
        }
    }

    public class Farmer{
        public String crop;
        public String paving;
    }
    public Farmer farmer = null;

    public HashMap<String, Tileset> customTileRes = new HashMap<String, Tileset>();
    private Tiler ridge;
    void initCustomTileRes()
    {
        Resource.local().loadwait("tiles/ridge-tex");
        customTileRes.put("ridge", Resource.local().loadwait("tiles/ridge").layer(Tileset.class));
    }


    NConfiguration () {
        AreasID.init ();
        NHitBox.init();
        initCustomTileRes();
        iconsettings.put("mm/wheelbarrow",new GobIcon.Setting(new Resource.Spec(Resource.local(),"mm/wheelbarrow"),true,true,false,false));
        iconsettings.put("mm/anvil",new GobIcon.Setting(new Resource.Spec(Resource.local(),"mm/anvil"),true,true,false,false));
        iconsettings.put("mm/dugout",new GobIcon.Setting(new Resource.Spec(Resource.local(),"mm/dugout"),true,true,false,false));
        iconsettings.put("mm/knarr",new GobIcon.Setting(new Resource.Spec(Resource.local(),"mm/knarr"),true,true,false,false));
        iconsettings.put("mm/snekkja",new GobIcon.Setting(new Resource.Spec(Resource.local(),"mm/snekkja"),true,true,false,false));
        iconsettings.put("mm/rowboat",new GobIcon.Setting(new Resource.Spec(Resource.local(),"mm/rowboat"),true,true,false,false));
        iconsettings.put("mm/horse",new GobIcon.Setting(new Resource.Spec(Resource.local(),"mm/horse"),true,true,false,false));
        iconsettings.put("mm/milestones",new GobIcon.Setting(new Resource.Spec(Resource.local(),"mm/milestones"),true,true,false,false));
        iconsettings.put("mm/milestonese",new GobIcon.Setting(new Resource.Spec(Resource.local(),"mm/milestonese"),true,true,false,false));
        iconsettings.put("mm/milestonew",new GobIcon.Setting(new Resource.Spec(Resource.local(),"mm/milestonew"),true,true,false,false));
        iconsettings.put("mm/milestonewe",new GobIcon.Setting(new Resource.Spec(Resource.local(),"mm/milestonewe"),true,true,false,false));
        iconsettings.put("mm/truffle",new GobIcon.Setting(new Resource.Spec(Resource.local(),"mm/truffle"),true,true,false,false));
        iconsettings.put("mm/claim",new GobIcon.Setting(new Resource.Spec(Resource.local(),"mm/claim"),true,true,false,false));
        iconsettings.put("mm/gem",new GobIcon.Setting(new Resource.Spec(Resource.local(),"mm/gem"),true,true,false,false));
        iconsettings.put("mm/stalagoomba",new GobIcon.Setting(new Resource.Spec(Resource.local(),"mm/stalagoomba"),true,true,false,false));
        iconsettings.put("mm/candelabrum",new GobIcon.Setting(new Resource.Spec(Resource.local(),"mm/candelabrum"),true,true,false,false));
        iconsettings.put("mm/cauldron",new GobIcon.Setting(new Resource.Spec(Resource.local(),"mm/cauldron"),true,true,false,false));
        iconsettings.put("mm/cart",new GobIcon.Setting(new Resource.Spec(Resource.local(),"mm/cart"),true,true,false,false));
        iconsettings.put("mm/plow",new GobIcon.Setting(new Resource.Spec(Resource.local(),"mm/plow"),true,true,false,false));
        iconsettings.put("mm/clay-cave",new GobIcon.Setting(new Resource.Spec(Resource.local(),"mm/clay-cave"),true,true,false,false));


        fishCandidates.add("Salmon");
        fishCandidates.add("Pike");


        pickingActions.add(new PickingAction("Giddyup!", false));
        pickingActions.add(new PickingAction("Pick", false));
        pickingActions.add(new PickingAction("Skin", false));
        pickingActions.add(new PickingAction("Light My Fire", false));
        pickingActions.add(new PickingAction("Open", false));
        pickingActions.add(new PickingAction("Carve", false));
        pickingActions.add(new PickingAction("Turn", false));
        pickingActions.add(new PickingAction("Flay", false));
        pickingActions.add(new PickingAction("Clean", false));
        pickingActions.add(new PickingAction("Butcher", false));
        pickingActions.add(new PickingAction("Collect bones", false));
        pickingActions.add(new PickingAction("Eat", false));
        pickingActions.add(new PickingAction("Shear wool", false));
        quickActions.add("cart");

        final KeyBinding[] toolKeys1 = {
                KeyBinding.get ("f1", KeyMatch.forcode(KeyEvent.VK_F1,0)),
                KeyBinding.get ("f2",KeyMatch.forcode(KeyEvent.VK_F2,0)),
                KeyBinding.get ("f3",KeyMatch.forcode(KeyEvent.VK_F3,0)),
                KeyBinding.get ("f4",KeyMatch.forcode(KeyEvent.VK_F4,0)),
                KeyBinding.get ("f5",KeyMatch.forcode(KeyEvent.VK_F5,0)),
                KeyBinding.get ("f6",KeyMatch.forcode(KeyEvent.VK_F6,0)),
                KeyBinding.get ("f7",KeyMatch.forcode(KeyEvent.VK_F7,0)),
                KeyBinding.get ("f8",KeyMatch.forcode(KeyEvent.VK_F8,0)),
                KeyBinding.get ("f9",KeyMatch.forcode(KeyEvent.VK_F9,0)),
                KeyBinding.get ("f10",KeyMatch.forcode(KeyEvent.VK_F10,0)),
                KeyBinding.get ("f11",KeyMatch.forcode(KeyEvent.VK_F11,0)),
                KeyBinding.get ("f12",KeyMatch.forcode(KeyEvent.VK_F12,0))
        };

        final KeyBinding[] toolKeys2 = {
                KeyBinding.get ("m1",KeyMatch.forcode(KeyEvent.VK_1,0)),
                KeyBinding.get ("m2",KeyMatch.forcode(KeyEvent.VK_2,0)),
                KeyBinding.get ("m3",KeyMatch.forcode(KeyEvent.VK_3,0)),
                KeyBinding.get ("m4",KeyMatch.forcode(KeyEvent.VK_4,0)),
                KeyBinding.get ("m5",KeyMatch.forcode(KeyEvent.VK_5,0)),
                KeyBinding.get ("m6",KeyMatch.forcode(KeyEvent.VK_6,0)),
                KeyBinding.get ("m7",KeyMatch.forcode(KeyEvent.VK_7,0)),
                KeyBinding.get ("m8",KeyMatch.forcode(KeyEvent.VK_8,0)),
                KeyBinding.get ("m9",KeyMatch.forcode(KeyEvent.VK_9,0)),
                KeyBinding.get ("m10",KeyMatch.forcode(KeyEvent.VK_0,0)),
                KeyBinding.get ("m11",KeyMatch.forcode(KeyEvent.VK_MINUS,0)),
                KeyBinding.get ("m12",KeyMatch.forcode(KeyEvent.VK_EQUALS,0))
        };
        final KeyBinding[] toolKeys3 = {
                KeyBinding.get ("cm1",KeyMatch.forcode(KeyEvent.VK_1,KeyMatch.C)),
                KeyBinding.get ("cm2",KeyMatch.forcode(KeyEvent.VK_2,KeyMatch.C)),
                KeyBinding.get ("cm3",KeyMatch.forcode(KeyEvent.VK_3,KeyMatch.C)),
                KeyBinding.get ("cm4",KeyMatch.forcode(KeyEvent.VK_4,KeyMatch.C)),
                KeyBinding.get ("cm5",KeyMatch.forcode(KeyEvent.VK_5,KeyMatch.C)),
                KeyBinding.get ("cm6",KeyMatch.forcode(KeyEvent.VK_6,KeyMatch.C)),
                KeyBinding.get ("cm7",KeyMatch.forcode(KeyEvent.VK_7,KeyMatch.C)),
                KeyBinding.get ("cm8",KeyMatch.forcode(KeyEvent.VK_8,KeyMatch.C)),
                KeyBinding.get ("cm9",KeyMatch.forcode(KeyEvent.VK_9,KeyMatch.C)),
                KeyBinding.get ("cm10",KeyMatch.forcode(KeyEvent.VK_0,KeyMatch.C)),
                KeyBinding.get ("cm11",KeyMatch.forcode(KeyEvent.VK_MINUS,KeyMatch.C)),
                KeyBinding.get ("cm12",KeyMatch.forcode(KeyEvent.VK_EQUALS,KeyMatch.C))
        };

        colors.put("no_color",new MixColor(new Color(0, 0, 0,0)));
        colors.put("free",new MixColor(new Color(121, 151, 219,244)));
        colors.put("ready",new MixColor(new Color(229, 27, 81, 195)));
        colors.put("inwork",new MixColor(new Color(229, 81, 29,195)));
        colors.put("warning",new MixColor(new Color(224, 204, 36, 139)));
        colors.put("no_soil",new MixColor(new Color(224, 114, 36, 187)));
        colors.put("no_water",new MixColor(new Color(36, 202, 224, 139)));
        colors.put("full",new MixColor(new Color(224, 36, 114, 240)));
        colors.put("not_full",new MixColor(new Color(224, 186, 36, 174)));

        playerSpeed_h.put("Crawl On", 0);
        playerSpeed_h.put("Walk On", 1);
        playerSpeed_h.put("Run On", 2);
        playerSpeed_h.put("Sprint On", 3);

        horseSpeed_h.put("Crawl On", 0);
        horseSpeed_h.put("Walk On", 1);
        horseSpeed_h.put("Run On", 2);
        horseSpeed_h.put("Sprint On", 3);

        dragWidgets.put("EquipProxy", new DragWdg(false,new Coord(500,30)));
        dragWidgets.put("ChatUI", new DragWdg(false,new Coord(500,30)));
        dragWidgets.put("MiniMap", new DragWdg(false,new Coord(400,400)));
        dragWidgets.put("belt0", new DragWdg(false,new Coord(50,200)));
        dragWidgets.put("belt1", new DragWdg(false,new Coord(50,230)));
        dragWidgets.put("belt2", new DragWdg(false,new Coord(50,250)));
        dragWidgets.put("NQuestInfo", new DragWdg(false,new Coord(200,250)));
        dragWidgets.put("NBotsInfo", new DragWdg(false,new Coord ( 200, 250 )));
        resizeWidgets.put("ChatUI", new Coord(700,300));
        resizeWidgets.put("MiniMap", new Coord(133,133));

        toolBelts.put("belt0", new ToolBelt(false,false,toolKeys1));
        toolBelts.put("belt1", new ToolBelt(false,false,toolKeys2));
        toolBelts.put("belt2", new ToolBelt(false,false,toolKeys3));

        rings.put("beeskep",new Ring(false,150));
        rings.put("brazier",new Ring(false,77));
        rings.put("barterhand",new Ring(false,55));
        rings.put("minesup",new Ring(false,-1));
        rings.put("trough",new Ring(false,200));
        rings.put("bat",new Ring(false,50));
        rings.put("boar",new Ring(false,100));
        rings.put("bear",new Ring(false,100));
        rings.put("adder",new Ring(false,100));
        rings.put("wildgoat",new Ring(false,100));
        rings.put("badger",new Ring(false,100));
        rings.put("lynx",new Ring(false,100));
        rings.put("mammoth",new Ring(false,100));
        rings.put("moose",new Ring(false,100));
        rings.put("wolf",new Ring(false,100));
        rings.put("walrus",new Ring(false,100));
        rings.put("orca",new Ring(false,100));
        rings.put("wolverine",new Ring(false,100));
        rings.put("troll",new Ring(false,100));

        players.put("red", new ArrowProp(false,false,false,false));
        players.put("white", new ArrowProp(false,false,false,false));
        players.put("green", new ArrowProp(true,false,true,false));
    }

    public static double getRad(String key){
        for(String k: getInstance().rings.keySet ()){
            if(key.contains ( k ))
                if(getInstance().rings.get ( k ).isEnable)
                    return getInstance().rings.get ( k ).size;
        }
        return -1;
    }

    public static NConfiguration getInstance () {
        return instance;
    }

    public void install() {
        if (NUtils.getGameUI() != null && NUtils.getGameUI().ui != null && NUtils.getGameUI().ui.sess != null)
            synchronized (NUtils.getGameUI().ui.sess.glob.oc) {
                for (Gob gob : NUtils.getGameUI().ui.sess.glob.oc) {
                    gob.status = NGob.Status.ready_for_update;
                }
            }
    }

    public void write () {

        JSONObject obj = new JSONObject ();
        JSONArray jgoatsHerds = new JSONArray();
        for(String val: goatsHerd.keySet()) {
            JSONObject jGoatHerd = new JSONObject();
            GoatsHerd gh = goatsHerd.get(val);
            jGoatHerd.put("adult_count", gh.adultGoats);
            jGoatHerd.put("breading_gap", gh.breedingGap);
            jGoatHerd.put("mq", gh.milkq);
            jGoatHerd.put("wq", gh.woolq);
            jGoatHerd.put("meatq", gh.meatq);
            jGoatHerd.put("hideq", gh.hideq);
            jGoatHerd.put("milkquan1", gh.milkquan1);
            jGoatHerd.put("milkquan2", gh.milkquan2);
            jGoatHerd.put("milkquanth", gh.milkquanth);
            jGoatHerd.put("woolquan1", gh.woolquan1);
            jGoatHerd.put("woolquan2", gh.woolquan2);
            jGoatHerd.put("woolquanth", gh.woolquanth);
            jGoatHerd.put("meatquan1", gh.meatquan1);
            jGoatHerd.put("meatquan2", gh.meatquan2);
            jGoatHerd.put("meatquanth", gh.meatquanth);
            jGoatHerd.put("ic", gh.ignoreChildren);
            jGoatHerd.put("dk", gh.disable_killing);
            jGoatHerd.put("name", val);
            jgoatsHerds.add(jGoatHerd);
        }
        obj.put("goatsHerds", jgoatsHerds);

        JSONArray jsheepHerds = new JSONArray();
        for(String val: sheepsHerd.keySet()) {
            JSONObject jShepsHerd = new JSONObject();
            SheepsHerd sh = sheepsHerd.get(val);
            jShepsHerd.put("adult_count", sh.adultSheeps);
            jShepsHerd.put("breading_gap", sh.breedingGap);
            jShepsHerd.put("mq", sh.milkq);
            jShepsHerd.put("wq", sh.woolq);
            jShepsHerd.put("meatq", sh.meatq);
            jShepsHerd.put("hideq", sh.hideq);
            jShepsHerd.put("milkquan1", sh.milkquan1);
            jShepsHerd.put("milkquan2", sh.milkquan2);
            jShepsHerd.put("milkquanth", sh.milkquanth);
            jShepsHerd.put("woolquan1", sh.woolquan1);
            jShepsHerd.put("woolquan2", sh.woolquan2);
            jShepsHerd.put("woolquanth", sh.woolquanth);
            jShepsHerd.put("meatquan1", sh.meatquan1);
            jShepsHerd.put("meatquan2", sh.meatquan2);
            jShepsHerd.put("meatquanth", sh.meatquanth);
            jShepsHerd.put("ic", sh.ignoreChildren);
            jShepsHerd.put("dk", sh.disable_killing);
            jShepsHerd.put("name", val);
            jsheepHerds.add(jShepsHerd);
        }
        obj.put("sheepsHerds", jsheepHerds);

        JSONArray jcowsHerds = new JSONArray();
        for(String val: cowsHerd.keySet()) {
            JSONObject jCowsHerd = new JSONObject();
            CowsHerd ch = cowsHerd.get(val);
            jCowsHerd.put("adult_count", ch.adultCows);
            jCowsHerd.put("breading_gap", ch.breedingGap);
            jCowsHerd.put("mq", ch.milkq);
            jCowsHerd.put("meatq", ch.meatq);
            jCowsHerd.put("hideq", ch.hideq);
            jCowsHerd.put("milkquan1", ch.milkquan1);
            jCowsHerd.put("milkquan2", ch.milkquan2);
            jCowsHerd.put("milkquanth", ch.milkquanth);
            jCowsHerd.put("meatquan1", ch.meatquan1);
            jCowsHerd.put("meatquan2", ch.meatquan2);
            jCowsHerd.put("meatquanth", ch.meatquanth);
            jCowsHerd.put("ic", ch.ignoreChildren);
            jCowsHerd.put("dk", ch.disable_killing);
            jCowsHerd.put("name", val);
            jcowsHerds.add(jCowsHerd);
        }
        obj.put("cowsHerds", jcowsHerds);

        JSONArray jpigsHerds = new JSONArray();
        for(String val: pigsHerd.keySet()) {
            JSONObject jPigsHerd = new JSONObject();
            PigsHerd ph = pigsHerd.get(val);

            jPigsHerd.put("adult_count", ph.adultPigs);
            jPigsHerd.put("breading_gap", ph.breedingGap);
            jPigsHerd.put("trufquan1", ph.trufquan1);
            jPigsHerd.put("trufquan2", ph.trufquan2);
            jPigsHerd.put("trufquanth", ph.trufquanth);
            jPigsHerd.put("meatq", ph.meatq);
            jPigsHerd.put("hideq", ph.hideq);
            jPigsHerd.put("meatquan1", ph.meatquan1);
            jPigsHerd.put("meatquan2", ph.meatquan2);
            jPigsHerd.put("meatquanth", ph.meatquanth);
            jPigsHerd.put("ic", ph.ignoreChildren);
            jPigsHerd.put("dk", ph.disable_killing);
            jPigsHerd.put("name", val);
            jpigsHerds.add(jPigsHerd);
        }
        obj.put("pigsHerds", jpigsHerds);
        JSONArray jhorsesHerds = new JSONArray();
        for(String val: horsesHerd.keySet()) {
            JSONObject jhorsesHerd = new JSONObject();
            HorsesHerd hh = horsesHerd.get(val);

            jhorsesHerd.put("adult_count", hh.adultHorse);
            jhorsesHerd.put("breading_gap", hh.breedingGap);
            jhorsesHerd.put("stam1", hh.stam1);
            jhorsesHerd.put("stam2", hh.stam2);
            jhorsesHerd.put("stamth", hh.stamth);
            jhorsesHerd.put("meatq", hh.meatq);
            jhorsesHerd.put("hideq", hh.hideq);
            jhorsesHerd.put("enduran", hh.enduran);
            jhorsesHerd.put("meta", hh.meta);
            jhorsesHerd.put("meatquan1", hh.meatquan1);
            jhorsesHerd.put("meatquan2", hh.meatquan2);
            jhorsesHerd.put("meatquanth", hh.meatquanth);
            jhorsesHerd.put("ic", hh.ignoreChildren);
            jhorsesHerd.put("dk", hh.disable_killing);
            jhorsesHerd.put("name", val);
            jhorsesHerds.add(jhorsesHerd);
        }
        obj.put("horsesHerds", jhorsesHerds);


        JSONArray keys = new JSONArray();
        {
            for (String charkey : allKeys.keySet()) {
                JSONObject jcharkeys = new JSONObject();
                jcharkeys.put("key", charkey);
                JSONArray jpanels = new JSONArray();
                for (String panel : allKeys.get(charkey).keySet()) {
                    JSONObject jpanel = new JSONObject();
                    jpanel.put("name", panel);
                    JSONArray jbuttons = new JSONArray();
                    for (Integer button : allKeys.get(charkey).get(panel).keySet()) {
                        JSONObject jbutton = new JSONObject();
                        jbutton.put("id", button);
                        jbutton.put("res", allKeys.get(charkey).get(panel).get(button));
                        jbuttons.add(jbutton);
                    }
                    jpanel.put("buttons", jbuttons);
                    jpanels.add(jpanel);
                }
                jcharkeys.put("panels", jpanels);
                keys.add(jcharkeys);
            }
        }
        obj.put("button_keys" ,keys);

        obj.put("ingredients" ,getIngredientsArray());
        obj.put("isMinerCredo" ,isMinerCredo);
        obj.put("lockStudy" ,lockStudy);
        obj.put("alarmGreyseal" ,alarmGreyseal);
        obj.put("isQuestInfoVisible" ,isQuestInfoVisible);

        JSONArray users = new JSONArray ();
        for ( NLoginData user : logins ) {
            JSONObject userobj = new JSONObject ();
            userobj.put ( "name", user.name );
            userobj.put ( "pass", user.pass );
            users.add ( userobj );
        }
        obj.put("users",users);

        JSONArray is = new JSONArray ();
        for ( String setting : iconsettings.keySet() ) {
            JSONObject is_obj = new JSONObject ();
            is_obj.put ( "name", setting );
            GobIcon.Setting setting1 = iconsettings.get(setting);
            is_obj.put ( "show", setting1.show );
            is_obj.put ( "defshow", setting1.defshow );
            is_obj.put ( "notify", setting1.notify );
            is_obj.put ( "ring", setting1.ring );
            is.add(is_obj);
        }
        obj.put("iconsettings",is);

        JSONArray widgetsPos = new JSONArray ();
        for ( String name : dragWidgets.keySet() ) {
            JSONObject coord = new JSONObject();
            coord.put("name", name);
            coord.put("x", dragWidgets.get(name).coord.x);
            coord.put("y", dragWidgets.get(name).coord.y);
            coord.put("locked", dragWidgets.get(name).locked);
            widgetsPos.add(coord);
        }
        obj.put("widgetsPos",widgetsPos);
        JSONArray paarray = new JSONArray ();
        for ( PickingAction pa : pickingActions ) {
            JSONObject pa_obj = new JSONObject();
            pa_obj.put("name", pa.action);
            pa_obj.put("isEnable", pa.isEnable);
            paarray.add(pa_obj);
        }
        obj.put("picking_actions",paarray);

        JSONArray qaarray = new JSONArray ();
        for ( String quick_action : quickActions ) {
            JSONObject pa_obj = new JSONObject();
            pa_obj.put("name", quick_action);
            qaarray.add(pa_obj);
        }
        obj.put("quick_actions",qaarray);

        JSONArray resizePos = new JSONArray ();
        for ( String name : resizeWidgets.keySet() ) {
            JSONObject coord = new JSONObject();
            coord.put("name", name);
            coord.put("x", resizeWidgets.get(name).x);
            coord.put("y", resizeWidgets.get(name).y);

            resizePos.add(coord);
        }
        obj.put("resizePos",resizePos);
        JSONArray ringsarr = new JSONArray ();
        for(String key: rings.keySet()){
            JSONObject ringobj = new JSONObject ();
            Ring r = rings.get(key);
            ringobj.put("name", key);
            ringobj.put("isEnable", r.isEnable);
            ringobj.put("value", r.size);
            ringsarr.add(ringobj);
        }
        JSONArray colorsarr = new JSONArray ();
        for(String key: colors.keySet()){
            JSONObject colorobj = new JSONObject ();
            Color clr = colors.get(key).color();
            colorobj.put("name", key);
            colorobj.put("r", clr.getRed());
            colorobj.put("g", clr.getGreen());
            colorobj.put("b", clr.getBlue());
            colorobj.put("a", clr.getAlpha());
            colorsarr.add(colorobj);
        }
        obj.put("colors",colorsarr);
        obj.put("isGrid",isGrid);
        obj.put("minesup",minesup);
        obj.put("isShowGild", Slotted.show);
        obj.put("isShowVar",NFoodInfo.show);
        obj.put("isShowStackQ", Stack.show);
        obj.put("isEye",isEyed);
        obj.put("enablePfBoundingBoxes",enablePfBoundingBoxes);
        obj.put("showBB",showBB);
        obj.put("hideNature",hideNature);
        obj.put("flatsurface",nextflatsurface);
        obj.put("showCSprite",nextshowCSprite);
        obj.put("invert_ver",invert_ver);
        obj.put("invert_hor",invert_hor);
        obj.put("enableCollectFoodInfo",collectFoodInfo);
        obj.put("isPaths",isPaths);
        JSONArray pathCandidates = new JSONArray ();
        for(NPathVisualizer.PathCategory cat: pathCategories){
            pathCandidates.add(cat.toString());
        }
        obj.put("pathCategories",pathCandidates);
        JSONArray toolbeltsarr = new JSONArray ();
        for(String key: toolBelts.keySet()){
            JSONObject toolbeltobj = new JSONObject ();
            ToolBelt t = toolBelts.get(key);
            toolbeltobj.put("name", key);
            toolbeltobj.put("isVertical", t.isVertical);
            toolbeltobj.put("isLocked", t.isLocked);
            toolbeltobj.put("isEnable", t.isEnable);
            if(t.toolKeys!=null) {
                JSONArray hotkeyssarr = new JSONArray();
                for (KeyBinding hotkey : t.toolKeys) {
                    JSONObject keyObj = new JSONObject();
                    if (hotkey.key == null) {
                        keyObj.put("code", hotkey.defkey.code);
                        keyObj.put("mod", hotkey.defkey.modmatch);
                    } else {
                        keyObj.put("code", hotkey.key.code);
                        keyObj.put("mod", hotkey.key.modmatch);
                    }
                    hotkeyssarr.add(keyObj);
                }
                toolbeltobj.put("hotkeys", hotkeyssarr);
            }
            toolbeltsarr.add(toolbeltobj);
        }
        obj.put("toolbelts", toolbeltsarr);

        obj.put ( "autologin", autologin );
        obj.put ( "autologin_user", autologin_user );
        obj.put ( "autologin_character", autologin_character );
        obj.put ( "checked", checked );
        obj.put ( "autobot", autobot );
        obj.put ( "msg", msg );
        /// TODO Users
//        obj.put ( "users", users );
        obj.put ( "rings", ringsarr );
        obj.put ( "nomad", nomadPath );
        obj.put ( "village", village);
        obj.put ( "showCropStage", showCropStage);
        obj.put ( "autoPicking", autoPicking);
        obj.put ( "nightVision", nightVision);
        obj.put ( "showAreas", showAreas);
        obj.put ( "playerSpeed", playerSpeed);
        obj.put ( "horseSpeed", horseSpeed);
        obj.put ( "quickRange", quickRange);

        JSONObject redPlayers = new JSONObject ();
        redPlayers.put("mark",players.get("red").mark);
        redPlayers.put("mark_target",players.get("red").mark_target);
        redPlayers.put("ring",players.get("red").ring);
        redPlayers.put("arrow",players.get("red").arrow);
        obj.put("red_players",redPlayers);
        JSONObject whitePlayers = new JSONObject ();
        whitePlayers.put("mark",players.get("white").mark);
        whitePlayers.put("mark_target",players.get("white").mark_target);
        whitePlayers.put("ring",players.get("white").ring);
        whitePlayers.put("arrow",players.get("white").arrow);
        obj.put("white_players",whitePlayers);
        OutputStreamWriter file = null;

        try  {
            String path = ((HashDirCache) ResCache.global).base + "\\..\\" + "config.nurgling.json";
            file = new OutputStreamWriter(Files.newOutputStream(Paths.get(path)), StandardCharsets.UTF_8);
            file.write ( obj.toJSONString () );
            file.close();
        }  catch (IOException e) {
            System.out.println("No config. config.nurgling.json not found");
        }
    }

    public JSONArray getIngredientsArray() {
        JSONArray ingredients = new JSONArray();
        {
            for (String ingredientKey : IngredientSettings.data.keySet()) {
                JSONObject jingredientKey = new JSONObject();
                jingredientKey.put("name", ingredientKey);
                jingredientKey.put("barter_in", IngredientSettings.data.get(ingredientKey).barter_in.toString());
                jingredientKey.put("barter_out", IngredientSettings.data.get(ingredientKey).barter_out.toString());
                jingredientKey.put("area_in", IngredientSettings.data.get(ingredientKey).area_in.toString());
                jingredientKey.put("area_out", IngredientSettings.data.get(ingredientKey).area_out.toString());
                jingredientKey.put("th",IngredientSettings.data.get(ingredientKey).th);
                jingredientKey.put("isGroup",IngredientSettings.data.get(ingredientKey).isGroup);
                ingredients.add(jingredientKey);
            }
        }
        return ingredients;
    }

    public static void initDefault () {
            getInstance ().read ( ((HashDirCache)ResCache.global).base +"\\..\\" + "config.nurgling.json" );
    }

    public void read ( String path ) {
        read_drink_data();
        try {
            BufferedReader reader = new BufferedReader (
                    new InputStreamReader( new FileInputStream( path ), "UTF-8" ) );
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = ( JSONObject ) parser.parse ( reader );

            JSONArray jgoatsHerds = ( JSONArray ) jsonObject.get ( "goatsHerds" );
            if(jgoatsHerds!=null) {
                Iterator<JSONObject> jigoatsHerd = jgoatsHerds.iterator();
                while (jigoatsHerd.hasNext()) {
                    JSONObject jGoatHerd = jigoatsHerd.next();
                    GoatsHerd gh = new GoatsHerd();
                    if (jGoatHerd.get("adult_count") != null)
                        gh.adultGoats.set(Integer.valueOf(jGoatHerd.get("adult_count").toString()));
                    gh.breedingGap.set(Integer.valueOf(jGoatHerd.get("breading_gap").toString()));
                    gh.milkq.set((jGoatHerd.get("mq") != null ? Double.parseDouble(jGoatHerd.get("mq").toString()) : 0.));
                    gh.woolq.set((jGoatHerd.get("wq") != null ? Double.parseDouble(jGoatHerd.get("wq").toString()) : 0.));
                    gh.meatq.set((jGoatHerd.get("meatq") != null ? Double.parseDouble(jGoatHerd.get("meatq").toString()) : 0.));
                    gh.hideq.set((jGoatHerd.get("hideq") != null ? Double.parseDouble(jGoatHerd.get("hideq").toString()) : 0.));
                    gh.milkquan1.set((jGoatHerd.get("milkquan1") != null ? Double.parseDouble(jGoatHerd.get("milkquan1").toString()) : 0.));
                    gh.milkquan2.set((jGoatHerd.get("milkquan2") != null ? Double.parseDouble(jGoatHerd.get("milkquan2").toString()) : 0.));
                    gh.milkquanth.set((jGoatHerd.get("milkquanth") != null ? Double.parseDouble(jGoatHerd.get("milkquanth").toString()) : 0.));
                    gh.woolquan1.set((jGoatHerd.get("woolquan1") != null ? Double.parseDouble(jGoatHerd.get("woolquan1").toString()) : 0.));
                    gh.woolquan2.set((jGoatHerd.get("woolquan2") != null ? Double.parseDouble(jGoatHerd.get("woolquan2").toString()) : 0.));
                    gh.woolquanth.set((jGoatHerd.get("woolquanth") != null ? Double.parseDouble(jGoatHerd.get("woolquanth").toString()) : 0.));
                    gh.meatquan1.set((jGoatHerd.get("meatquan1") != null ? Double.parseDouble(jGoatHerd.get("meatquan1").toString()) : 0.));
                    gh.meatquan2.set((jGoatHerd.get("meatquan2") != null ? Double.parseDouble(jGoatHerd.get("meatquan2").toString()) : 0.));
                    gh.meatquanth.set((jGoatHerd.get("meatquanth") != null ? Double.parseDouble(jGoatHerd.get("meatquanth").toString()) : 0.));
                    gh.ignoreChildren = jGoatHerd.get("ic") != null && (boolean) jGoatHerd.get("ic");
                    gh.disable_killing = jGoatHerd.get("dk") != null && (boolean) jGoatHerd.get("dk");
                    if (jGoatHerd.get("name") != null)
                    {
                        goatsHerd.put(jGoatHerd.get("name").toString(),gh);
                        if(selected_goatsHerd.isEmpty())
                        {
                            selected_goatsHerd = jGoatHerd.get("name").toString();
                        }
                    }
                }
            }

            JSONArray jsheepsHerds = ( JSONArray ) jsonObject.get ( "sheepsHerds" );
            if(jsheepsHerds!=null) {
                Iterator<JSONObject> jisheepsHerd = jsheepsHerds.iterator();
                while (jisheepsHerd.hasNext()) {
                    JSONObject jShepsHerd = jisheepsHerd.next();
                    SheepsHerd sh = new SheepsHerd();
                    if (jShepsHerd.get("adult_count") != null)
                        sh.adultSheeps.set(Integer.valueOf(jShepsHerd.get("adult_count").toString()));
                    sh.breedingGap.set(Integer.valueOf(jShepsHerd.get("breading_gap").toString()));
                    sh.milkq.set((jShepsHerd.get("mq") != null ? Double.parseDouble(jShepsHerd.get("mq").toString()) : 0.));
                    sh.woolq.set((jShepsHerd.get("wq") != null ? Double.parseDouble(jShepsHerd.get("wq").toString()) : 0.));
                    sh.meatq.set((jShepsHerd.get("meatq") != null ? Double.parseDouble(jShepsHerd.get("meatq").toString()) : 0.));
                    sh.hideq.set((jShepsHerd.get("hideq") != null ? Double.parseDouble(jShepsHerd.get("hideq").toString()) : 0.));
                    sh.milkquan1.set((jShepsHerd.get("milkquan1") != null ? Double.parseDouble(jShepsHerd.get("milkquan1").toString()) : 0.));
                    sh.milkquan2.set((jShepsHerd.get("milkquan2") != null ? Double.parseDouble(jShepsHerd.get("milkquan2").toString()) : 0.));
                    sh.milkquanth.set((jShepsHerd.get("milkquanth") != null ? Double.parseDouble(jShepsHerd.get("milkquanth").toString()) : 0.));
                    sh.woolquan1.set((jShepsHerd.get("woolquan1") != null ? Double.parseDouble(jShepsHerd.get("woolquan1").toString()) : 0.));
                    sh.woolquan2.set((jShepsHerd.get("woolquan2") != null ? Double.parseDouble(jShepsHerd.get("woolquan2").toString()) : 0.));
                    sh.woolquanth.set((jShepsHerd.get("woolquanth") != null ? Double.parseDouble(jShepsHerd.get("woolquanth").toString()) : 0.));
                    sh.meatquan1.set((jShepsHerd.get("meatquan1") != null ? Double.parseDouble(jShepsHerd.get("meatquan1").toString()) : 0.));
                    sh.meatquan2.set((jShepsHerd.get("meatquan2") != null ? Double.parseDouble(jShepsHerd.get("meatquan2").toString()) : 0.));
                    sh.meatquanth.set((jShepsHerd.get("meatquanth") != null ? Double.parseDouble(jShepsHerd.get("meatquanth").toString()) : 0.));
                    sh.ignoreChildren = jShepsHerd.get("ic") != null && (boolean) jShepsHerd.get("ic");
                    sh.disable_killing = jShepsHerd.get("dk") != null && (boolean) jShepsHerd.get("dk");
                    if (jShepsHerd.get("name") != null) {
                        sheepsHerd.put(jShepsHerd.get("name").toString(), sh);
                        if(selected_sheepsHerd.isEmpty())
                        {
                            selected_sheepsHerd = jShepsHerd.get("name").toString();
                        }
                    }
                }
            }
            JSONArray jcowsHerds = ( JSONArray ) jsonObject.get ( "cowsHerds" );
            if(jcowsHerds!=null) {
                Iterator<JSONObject> jicowsHerd = jcowsHerds.iterator();
                while (jicowsHerd.hasNext()) {
                    JSONObject jCowsHerd = jicowsHerd.next();
                    CowsHerd ch = new CowsHerd();
                    if (jCowsHerd.get("adult_count") != null)
                        ch.adultCows.set(Integer.valueOf(jCowsHerd.get("adult_count").toString()));
                    ch.breedingGap.set(Integer.valueOf(jCowsHerd.get("breading_gap").toString()));
                    ch.milkq.set((jCowsHerd.get("mq") != null ? Double.parseDouble(jCowsHerd.get("mq").toString()) : 0.));
                    ch.meatq.set((jCowsHerd.get("meatq") != null ? Double.parseDouble(jCowsHerd.get("meatq").toString()) : 0.));
                    ch.hideq.set((jCowsHerd.get("hideq") != null ? Double.parseDouble(jCowsHerd.get("hideq").toString()) : 0.));
                    ch.milkquan1.set((jCowsHerd.get("milkquan1") != null ? Double.parseDouble(jCowsHerd.get("milkquan1").toString()) : 0.));
                    ch.milkquan2.set((jCowsHerd.get("milkquan2") != null ? Double.parseDouble(jCowsHerd.get("milkquan2").toString()) : 0.));
                    ch.milkquanth.set((jCowsHerd.get("milkquanth") != null ? Double.parseDouble(jCowsHerd.get("milkquanth").toString()) : 0.));
                    ch.meatquan1.set((jCowsHerd.get("meatquan1") != null ? Double.parseDouble(jCowsHerd.get("meatquan1").toString()) : 0.));
                    ch.meatquan2.set((jCowsHerd.get("meatquan2") != null ? Double.parseDouble(jCowsHerd.get("meatquan2").toString()) : 0.));
                    ch.meatquanth.set((jCowsHerd.get("meatquanth") != null ? Double.parseDouble(jCowsHerd.get("meatquanth").toString()) : 0.));
                    ch.ignoreChildren = jCowsHerd.get("ic") != null && (boolean) jCowsHerd.get("ic");
                    ch.disable_killing = jCowsHerd.get("dk") != null && (boolean) jCowsHerd.get("dk");
                    if (jCowsHerd.get("name") != null) {
                        cowsHerd.put(jCowsHerd.get("name").toString(), ch);
                        if(selected_cowsHerd.isEmpty())
                        {
                            selected_cowsHerd = jCowsHerd.get("name").toString();
                        }
                    }
                }
            }

            JSONArray jpigsHerds = ( JSONArray ) jsonObject.get ( "pigsHerds" );
            if(jpigsHerds!=null) {
                Iterator<JSONObject> jipigsHerds = jpigsHerds.iterator();
                while (jipigsHerds.hasNext()) {
                    JSONObject jPigsHerd = jipigsHerds.next();
                    PigsHerd ph = new PigsHerd();
                    if (jPigsHerd.get("adult_count") != null)
                        ph.adultPigs.set(Integer.valueOf(jPigsHerd.get("adult_count").toString()));
                    ph.breedingGap.set(Integer.valueOf(jPigsHerd.get("breading_gap").toString()));
                    ph.meatq.set((jPigsHerd.get("meatq") != null ? Double.parseDouble(jPigsHerd.get("meatq").toString()) : 0.));
                    ph.hideq.set((jPigsHerd.get("hideq") != null ? Double.parseDouble(jPigsHerd.get("hideq").toString()) : 0.));
                    ph.trufquan1.set((jPigsHerd.get("trufquan1") != null ? Double.parseDouble(jPigsHerd.get("trufquan1").toString()) : 0.));
                    ph.trufquan2.set((jPigsHerd.get("trufquan2") != null ? Double.parseDouble(jPigsHerd.get("trufquan2").toString()) : 0.));
                    ph.trufquanth.set((jPigsHerd.get("trufquanth") != null ? Double.parseDouble(jPigsHerd.get("trufquanth").toString()) : 0.));
                    ph.meatquan1.set((jPigsHerd.get("meatquan1") != null ? Double.parseDouble(jPigsHerd.get("meatquan1").toString()) : 0.));
                    ph.meatquan2.set((jPigsHerd.get("meatquan2") != null ? Double.parseDouble(jPigsHerd.get("meatquan2").toString()) : 0.));
                    ph.meatquanth.set((jPigsHerd.get("meatquanth") != null ? Double.parseDouble(jPigsHerd.get("meatquanth").toString()) : 0.));
                    ph.ignoreChildren = jPigsHerd.get("ic") != null && (boolean) jPigsHerd.get("ic");
                    ph.disable_killing = jPigsHerd.get("dk") != null && (boolean) jPigsHerd.get("dk");
                    if (jPigsHerd.get("name") != null) {
                        pigsHerd.put(jPigsHerd.get("name").toString(), ph);
                        if(selected_pigsHerd.isEmpty())
                        {
                            selected_pigsHerd = jPigsHerd.get("name").toString();
                        }
                    }
                }
            }

            JSONArray jhorsesHerds = ( JSONArray ) jsonObject.get ( "horsesHerds" );
            if(jhorsesHerds!=null) {
                Iterator<JSONObject> jihorsesHerds = jhorsesHerds.iterator();
                while (jihorsesHerds.hasNext()) {
                    JSONObject jHorsesHerd = jihorsesHerds.next();
                    HorsesHerd hh = new HorsesHerd();
                    if (jHorsesHerd.get("adult_count") != null)
                        hh.adultHorse.set(Integer.valueOf(jHorsesHerd.get("adult_count").toString()));
                    if (jHorsesHerd.get("breading_gap") != null)
                        hh.breedingGap.set(Integer.valueOf(jHorsesHerd.get("breading_gap").toString()));
                    hh.meatq.set((jHorsesHerd.get("meatq") != null ? Double.parseDouble(jHorsesHerd.get("meatq").toString()) : 0.));
                    hh.meta.set((jHorsesHerd.get("meta") != null ? (Integer) ((int) Double.parseDouble(jHorsesHerd.get("meta").toString())) : 0));
                    hh.enduran.set((jHorsesHerd.get("enduran") != null ? Integer.parseInt(jHorsesHerd.get("enduran").toString()) : 0));
                    hh.hideq.set((jHorsesHerd.get("hideq") != null ? Double.parseDouble(jHorsesHerd.get("hideq").toString()) : 0.));
                    hh.stam1.set((jHorsesHerd.get("stam1") != null ? Double.parseDouble(jHorsesHerd.get("stam1").toString()) : 0.));
                    hh.stam2.set((jHorsesHerd.get("stam2") != null ? Double.parseDouble(jHorsesHerd.get("stam2").toString()) : 0.));
                    hh.stamth.set((jHorsesHerd.get("stamth") != null ? Double.parseDouble(jHorsesHerd.get("stamth").toString()) : 0.));
                    hh.meatquan1.set((jHorsesHerd.get("meatquan1") != null ? Double.parseDouble(jHorsesHerd.get("meatquan1").toString()) : 0.));
                    hh.meatquan2.set((jHorsesHerd.get("meatquan2") != null ? Double.parseDouble(jHorsesHerd.get("meatquan2").toString()) : 0.));
                    hh.meatquanth.set((jHorsesHerd.get("meatquanth") != null ? Double.parseDouble(jHorsesHerd.get("meatquanth").toString()) : 0.));
                    hh.ignoreChildren = jHorsesHerd.get("ic") != null && (boolean) jHorsesHerd.get("ic");
                    hh.disable_killing = jHorsesHerd.get("dk") != null && (boolean) jHorsesHerd.get("dk");
                    if (jHorsesHerd.get("name") != null) {
                        horsesHerd.put(jHorsesHerd.get("name").toString(), hh);
                    }
                }
            }

            parseIngredients(( JSONArray ) jsonObject.get ( "ingredients" ));
            JSONArray jkeys =  ( JSONArray ) jsonObject.get ( "button_keys" );
            if(jkeys!=null) {
                Iterator<JSONObject> jkey = jkeys.iterator();
                while (jkey.hasNext()) {
                    JSONObject item = jkey.next();
                    String key =item.get("key").toString();
                    allKeys.put(key, new HashMap<>());
                    HashMap<String, HashMap<Integer, String>> panels = allKeys.get(key);
                            JSONArray jpanels = (JSONArray) item.get("panels");
                    if (jpanels != null) {
                        Iterator<JSONObject> jpanel = jpanels.iterator();
                        while (jpanel.hasNext()) {
                            JSONObject unit = jpanel.next();
                            panels.put(unit.get("name").toString(), new HashMap<>());
                            HashMap<Integer, String> panel = panels.get(unit.get("name").toString());
                            JSONArray jbuttons = (JSONArray) unit.get("buttons");
                            if (jbuttons != null) {
                                Iterator<JSONObject> jbutton = jbuttons.iterator();
                                while (jbutton.hasNext()) {
                                    JSONObject button = jbutton.next();
                                    panel.put(Integer.valueOf(button.get("id").toString()), button.get("res").toString());
                                }
                            }
                        }
                    }
                }
            }

            JSONArray msg = ( JSONArray ) jsonObject.get ( "users" );
            if(msg!=null) {
                logins.clear();
                Iterator<JSONObject> iterator = msg.iterator();
                while (iterator.hasNext()) {
                    JSONObject item = iterator.next();
                    NLoginData logData = new NLoginData(item.get("name").toString(), item.get("pass").toString());
                    logins.add(logData);
                }
            }
            JSONArray widgetsPos = ( JSONArray ) jsonObject.get ( "widgetsPos" );
            if(widgetsPos!=null) {
                Iterator<JSONObject> iterator2 = widgetsPos.iterator();
                while (iterator2.hasNext()) {
                    JSONObject item = iterator2.next();
                    dragWidgets.put(item.get("name").toString(), new DragWdg(item.get("locked") == null || (boolean) item.get("locked"),new Coord((int)((long) item.get("x")), (int)((long) item.get("y")))));
                }
            }

            JSONArray paarray = ( JSONArray ) jsonObject.get ( "picking_actions" );
            if(paarray!=null) {
                pickingActions.clear();
                Iterator<JSONObject> iterator2 = paarray.iterator();
                while (iterator2.hasNext()) {
                    JSONObject item = iterator2.next();
                    pickingActions.add( new PickingAction((String)item.get("name"),(boolean)item.get("isEnable")));
                }
            }

            JSONArray qaarray = ( JSONArray ) jsonObject.get ( "quick_actions" );
            if(qaarray!=null) {
                quickActions.clear();
                Iterator<JSONObject> iterator2 = qaarray.iterator();
                while (iterator2.hasNext()) {
                    JSONObject item = iterator2.next();
                    quickActions.add( (String)item.get("name"));
                }
            }


            JSONArray resizePos = ( JSONArray ) jsonObject.get ( "resizePos" );
            if(resizePos!=null) {
                if (widgetsPos != null) {
                    Iterator<JSONObject> iterator2 = resizePos.iterator();
                    while (iterator2.hasNext()) {
                        JSONObject item = iterator2.next();
                        resizeWidgets.put(item.get("name").toString(), new Coord((int) ((long) item.get("x")), (int) ((long) item.get("y"))));
                    }
                }
            }

            JSONArray is_arr = ( JSONArray ) jsonObject.get ( "iconsettings" );
            if(is_arr!=null) {
                Iterator<JSONObject> iterator2 = is_arr.iterator();
                while (iterator2.hasNext()) {
                    JSONObject item = iterator2.next();
                    iconsettings.put(item.get("name").toString(), new GobIcon.Setting(new Resource.Spec(Resource.local(),item.get("name").toString()),(Boolean)item.get("show"),(Boolean)item.get("defshow"),(Boolean)item.get("notify"),(Boolean)item.get("ring")));
                }
            }

            JSONArray toolsbeltarr = ( JSONArray ) jsonObject.get ( "toolbelts" );
            if(toolsbeltarr!=null) {
                Iterator<JSONObject> iterator2 = toolsbeltarr.iterator();
                while (iterator2.hasNext()) {
                    JSONObject item = iterator2.next();
                    int[] array = new int[12];
                    String key = item.get("name").toString();
                    if(item.get("hotkeys")!=null) {
                        Iterator<JSONObject> keyiterator = ((JSONArray) item.get("hotkeys")).iterator();
                        int counter = 0;

                        while (keyiterator.hasNext()) {
                            JSONObject unit = keyiterator.next();

                            toolBelts.get(key).toolKeys[counter++].set(KeyMatch.forcode((int) ((long) unit.get("code")), (int) ((long) unit.get("mod"))));
                        }
                    }
                    toolBelts.get(key).isVertical = (boolean) item.get("isVertical");
                    toolBelts.get(key).isLocked = (boolean) item.get("isLocked");
                    toolBelts.get(key).isEnable = (boolean) item.get("isEnable");
                }
            }

            if ( jsonObject.get ( "nomad" ) != null ) {
                nomadPath = ( String ) jsonObject.get ( "nomad" );
            }
            if ( jsonObject.get ( "playerSpeed" ) != null ) {
                playerSpeed = ( int ) (long)jsonObject.get ( "playerSpeed" );
            }
            if ( jsonObject.get ( "enablePfBoundingBoxes" ) != null ) {
                enablePfBoundingBoxes = (boolean)jsonObject.get ( "enablePfBoundingBoxes" );
            }
            if ( jsonObject.get ( "showBB" ) != null ) {
                showBB = (boolean)jsonObject.get ( "showBB" );
            }
            if ( jsonObject.get ( "hideNature" ) != null ) {
                hideNature = (boolean)jsonObject.get ( "hideNature" );
            }
            if ( jsonObject.get ( "flatsurface" ) != null ) {
                flatsurface = (boolean)jsonObject.get ( "flatsurface" );
                nextflatsurface = flatsurface;
            }
            if ( jsonObject.get ( "showCSprite" ) != null ) {
                showCSprite = (boolean)jsonObject.get ( "showCSprite" );
                nextshowCSprite = showCSprite;
            }
            if ( jsonObject.get ( "invert_ver" ) != null ) {
                invert_ver = (boolean)jsonObject.get ( "invert_ver" );
            }
            if ( jsonObject.get ( "invert_hor" ) != null ) {
                invert_hor = (boolean)jsonObject.get ( "invert_hor" );
            }
            if ( jsonObject.get ( "enableCollectFoodInfo" ) != null ) {
                collectFoodInfo = (boolean)jsonObject.get ( "enableCollectFoodInfo" );
            }
            if ( jsonObject.get ( "horseSpeed" ) != null ) {
                horseSpeed = ( int ) (long) jsonObject.get ( "horseSpeed" );
            }
            if ( jsonObject.get ( "quickRange" ) != null ) {
                quickRange = ( int ) (long) jsonObject.get ( "quickRange" );
            }
            if(jsonObject.get("red_players")!=null){
                JSONObject red = (JSONObject)jsonObject.get("red_players");
                players.put("red", new ArrowProp((boolean) red.get("arrow"),(boolean) red.get("ring"),(boolean) red.get("mark"),(boolean) red.get("mark_target")));
            }
            if(jsonObject.get("isMinerCredo")!=null){
                isMinerCredo = (boolean) jsonObject.get("isMinerCredo");
            }
            if(jsonObject.get("lockStudy")!=null){
                lockStudy = (boolean) jsonObject.get("lockStudy");
            }
            if(jsonObject.get("alarmGreyseal")!=null){
                alarmGreyseal = (boolean) jsonObject.get("alarmGreyseal");
            }
            if(jsonObject.get("isQuestInfoVisible")!=null){
                isQuestInfoVisible = (boolean) jsonObject.get("isQuestInfoVisible");
            }
            if(jsonObject.get("white_players")!=null){
                JSONObject white = (JSONObject)jsonObject.get("white_players");
                players.put("white", new ArrowProp((boolean) white.get("arrow"),(boolean) white.get("ring"),(boolean) white.get("mark"),(boolean) white.get("mark_target")));
            }
            if ( jsonObject.get ( "autologin" ) != null ) {
                autologin = ( boolean ) jsonObject.get ( "autologin" );
                autologin_user = ( String ) jsonObject.get ( "autologin_user" );
                autologin_character = ( String ) jsonObject.get ( "autologin_character" );
                checked = ( String ) jsonObject.get ( "checked" );
                autobot = ( String ) jsonObject.get ( "autobot" );

                JSONObject farm = ( JSONObject ) jsonObject.get ( "farmer" );
                if(farm!=null){
                    farmer = new Farmer ();
                    farmer.crop = ( String ) farm.get ( "crop" );
                    farmer.paving = ( String ) farm.get ( "paving" );
                }
                this.msg = ( String ) jsonObject.get ( "msg" );
            }
            village = ( String ) jsonObject.get ( "village" );
            JSONArray rings_arr = ( JSONArray ) jsonObject.get ( "rings" );
            if(rings_arr!=null) {
                Iterator<JSONObject> rings_it = rings_arr.iterator();
                while (rings_it.hasNext()) {
                    JSONObject item = rings_it.next();
                    rings.put(item.get("name").toString(), new Ring((Boolean) item.get("isEnable"), (Double) item.get("value")));
                }
            }
            JSONArray colors_arr = ( JSONArray ) jsonObject.get ( "colors" );
            if(colors_arr!=null) {
                Iterator<JSONObject> color_it = colors_arr.iterator();
                while (color_it.hasNext()) {
                    JSONObject item = color_it.next();
                    colors.put(item.get("name").toString(), new MixColor(new Color((int)((long)item.get("r")),(int)((long)item.get("g")),(int)((long)item.get("b")),(int)((long)item.get("a")))));
                }
            }
            if(jsonObject.get ( "showCropStage" )!=null)
                showCropStage = ( Boolean ) jsonObject.get ( "showCropStage" );
            if(jsonObject.get ( "autoPicking" )!=null)
                autoPicking = ( Boolean ) jsonObject.get ( "autoPicking" );
            if(jsonObject.get ( "nightVision" )!=null)
                nightVision = ( Boolean ) jsonObject.get ( "nightVision" );
            if(jsonObject.get ( "showAreas" )!=null)
                showAreas = ( Boolean ) jsonObject.get ( "showAreas" );
            if(jsonObject.get ( "isEye" )!=null)
                isEyed = ( Boolean ) jsonObject.get ( "isEye" );
            if(jsonObject.get ( "isPaths" )!=null)
                isPaths = ( Boolean ) jsonObject.get ( "isPaths" );
            if(jsonObject.get ( "isShowGild" )!=null)
                Slotted.show = ( Boolean ) jsonObject.get ( "isShowGild" );
            if(jsonObject.get ( "isShowVar" )!=null)
                NFoodInfo.show = ( Boolean ) jsonObject.get ( "isShowVar" );
            if(jsonObject.get ( "isShowStackQ" )!=null)
                Stack.show = ( Boolean ) jsonObject.get ( "isShowStackQ" );
            if(jsonObject.get ( "minesup" )!=null) {
                minesup = (Boolean) jsonObject.get("minesup");
            }
            if(jsonObject.get ( "isGrid" )!=null)
                isGrid = ( Boolean ) jsonObject.get ( "isGrid" );
            JSONArray pathCategories = ( JSONArray ) jsonObject.get ( "pathCategories" );
            if(pathCategories!=null){
                Iterator<String> path_it = pathCategories.iterator();
                while (path_it.hasNext()) {
                    this.pathCategories.add(NPathVisualizer.PathCategory.valueOf(path_it.next()));
                }
            }

        }
        catch ( IOException | ParseException e ) {
            System.out.println("No config. config.nurgling.json not found");
        }
        /// TODO light
//        Light.isEnable = Configuration.getInstance().nightVision;
    }

    public void parseIngredients( JSONArray jingredients) {
        if(jingredients!=null) {
            Iterator<JSONObject> jingredient = jingredients.iterator();
            while (jingredient.hasNext()) {
                JSONObject ingredient = jingredient.next();
                String ingName = ingredient.get("name").toString();
                IngredientSettings.data.put(ingName,
                        new Ingredient(AreasID.valueOf(ingredient.get("area_out").toString()),
                                AreasID.valueOf(ingredient.get("barter_out").toString()),
                                AreasID.valueOf(ingredient.get("area_in").toString()),
                                AreasID.valueOf(ingredient.get("barter_in").toString()), new NAlias(ingName), ((ingredient.containsKey("th")) ? Double.parseDouble(ingredient.get("th").toString()) : 0.), (ingredient.containsKey("isGroup") && (boolean) (ingredient.get("isGroup")))));
            }
        }
    }

    private void read_drink_data() {
        try {
            URL url = NUtils.class.getProtectionDomain().getCodeSource().getLocation();

            if (url != null) {
                String path = url.toURI().getPath().substring(0, url.toURI().getPath().lastIndexOf("/"));
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(path + "/drink_data.json"), "UTF-8"));
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(reader);

                // loop array
                JSONArray msg = (JSONArray) jsonObject.get("data_food");
                Iterator<JSONObject> iterator = msg.iterator();
                while (iterator.hasNext()) {
                    JSONObject item = iterator.next();
                    ArrayList<String> types = new ArrayList<>((JSONArray) item.get("types"));
                    data_food.put((String)item.get("name"),types);
                }

                JSONArray msg1 = (JSONArray) jsonObject.get("data_drinks");
                Iterator<JSONObject> iterator1 = msg1.iterator();
                while (iterator1.hasNext()) {
                    JSONObject item = iterator1.next();
                    ArrayList<String> types = new ArrayList<>((JSONArray) item.get("drink"));
                    data_drinks.put((String)item.get("types"),types);
                }

                JSONArray msg2 = (JSONArray) jsonObject.get("data_vessel");
                Iterator<JSONObject> iterator2 = msg2.iterator();
                while (iterator2.hasNext()) {
                    JSONObject item = iterator2.next();
                    data_vessel.put((String)item.get("drink"),(String)item.get("vessel"));
                }
            }
        }catch (IOException | ParseException | URISyntaxException e ) {
            e.printStackTrace ();
        }

    }
}
