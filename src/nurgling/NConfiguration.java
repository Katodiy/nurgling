package nurgling;

import haven.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

import haven.render.MixColor;
import haven.res.ui.tt.highlighting.Highlighting;
import haven.res.ui.tt.slot.Slotted;
import haven.res.ui.tt.stackn.Stack;
import nurgling.bots.settings.IngredientSettings;
import nurgling.bots.tools.AItem;
import nurgling.bots.tools.Ingredient;
import nurgling.json.*;
import nurgling.json.parser.JSONParser;
import nurgling.json.parser.ParseException;
import nurgling.tools.AreasID;

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

    public static void saveButtons(String name, NGameUI.NButtonBeltSlot[] custom) {
        String key = NUtils.getUI().sessInfo.characterInfo.chrid;
        HashMap<String, HashMap<Integer, String>> customKeys  = (instance.allKeys.get(key)==null)?instance.allKeys.put(key, new HashMap<>()):instance.allKeys.get(key);

        if(customKeys!=null) {
            HashMap<Integer, String> keys = (customKeys.get(name) == null)?customKeys.put(name, new HashMap<Integer, String>()):customKeys.get(name);
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

    public static class PickingAction{
        public String action;
        public boolean isEnable;

        public PickingAction(String action, boolean isEnable) {
            this.action = action;
            this.isEnable = isEnable;
        }
    }

    public class GoatsHerd{
        public int totalGoats = 4;
        public int adultGoats = 4;

        public double milkq = 1.5;
        public double milkquan = 1;

        public double woolq = 0.33;
        public double woolquan = 0.33;

        public double meatq = 0;
        public double meatquan = 0;

        public int breedingGap = 10;
    }

    public class HorsesHerd{
        public int totalMares = 4;

        public double endurance = 1.5;
        public double metabolism = 1;

    }

    public class SheepsHerd{
        public int totalSheeps = 4;

        public int adultSheeps = 4;

        public double milkq = 1.5;
        public double milkquan = 1;

        public double woolq = 0.33;
        public double woolquan = 0.33;

        public double meatq = 0;
        public double meatquan = 0;

        public int breedingGap = 10;
    }

    public class PigsHerd{
        public int totalPigs = 4;

        public double trufSnout = 0;

        public double meatq = 1.5;
        public double meatquan = 1;

        public int breedingGap = 10;
    }


    public class CowsHerd{
        public int totalCows = 4;
        public int adultCows = 4;

        public double milkq = 1.5;
        public double milkquan = 1;

        public double meatq = 0;
        public double meatquan = 0;

        public int breedingGap = 10;
    }

    public GoatsHerd goatsHerd = new GoatsHerd();
    public HorsesHerd horsesHerd = new HorsesHerd();
    public SheepsHerd sheepsHerd = new SheepsHerd();
    public PigsHerd pigsHerd = new PigsHerd();
    public CowsHerd cowsHerd = new CowsHerd();

    public ArrayList<PickingAction> pickingActions = new ArrayList<>();

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


    NConfiguration () {
        AreasID.init ();
        NHitBox.init();
        iconsettings.put("mm/wheelbarrow",new GobIcon.Setting(new Resource.Spec(Resource.local(),"mm/wheelbarrow"),true,true,false,false));
        iconsettings.put("mm/anvil",new GobIcon.Setting(new Resource.Spec(Resource.local(),"mm/anvil"),true,true,false,false));
        iconsettings.put("mm/truffle",new GobIcon.Setting(new Resource.Spec(Resource.local(),"mm/truffle"),true,true,false,false));
        iconsettings.put("mm/claim",new GobIcon.Setting(new Resource.Spec(Resource.local(),"mm/claim"),true,true,false,false));
        iconsettings.put("mm/gem",new GobIcon.Setting(new Resource.Spec(Resource.local(),"mm/gem"),true,true,false,false));
        iconsettings.put("mm/stalagoomba",new GobIcon.Setting(new Resource.Spec(Resource.local(),"mm/stalagoomba"),true,true,false,false));
        iconsettings.put("mm/candelabrum",new GobIcon.Setting(new Resource.Spec(Resource.local(),"mm/candelabrum"),true,true,false,false));
        iconsettings.put("mm/cauldron",new GobIcon.Setting(new Resource.Spec(Resource.local(),"mm/cauldron"),true,true,false,false));
        iconsettings.put("mm/cart",new GobIcon.Setting(new Resource.Spec(Resource.local(),"mm/cart"),true,true,false,false));
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

        JSONObject jGoatHerd = new JSONObject();
        jGoatHerd.put("female_count",goatsHerd.totalGoats);
        jGoatHerd.put("adult_count",goatsHerd.adultGoats);
        jGoatHerd.put("breading_gap",goatsHerd.breedingGap);
        jGoatHerd.put("mq",goatsHerd.milkq);
        jGoatHerd.put("m",goatsHerd.milkquan);
        jGoatHerd.put("wq",goatsHerd.woolq);
        jGoatHerd.put("w",goatsHerd.woolquan);
        jGoatHerd.put("meatq",goatsHerd.meatq);
        jGoatHerd.put("meat",goatsHerd.meatquan);
        obj.put("goatsHerd",jGoatHerd);

        JSONObject jShepsHerd = new JSONObject();
        jShepsHerd.put("female_count",sheepsHerd.totalSheeps);
        jShepsHerd.put("adult_count",sheepsHerd.adultSheeps);
        jShepsHerd.put("breading_gap",sheepsHerd.breedingGap);
        jShepsHerd.put("mq",sheepsHerd.milkq);
        jShepsHerd.put("m",sheepsHerd.milkquan);
        jShepsHerd.put("wq",sheepsHerd.woolq);
        jShepsHerd.put("w",sheepsHerd.woolquan);
        jShepsHerd.put("meatq",sheepsHerd.meatq);
        jShepsHerd.put("meat",sheepsHerd.meatquan);
        obj.put("sheepsHerd",jShepsHerd);

        JSONObject jCowsHerd = new JSONObject();
        jCowsHerd.put("female_count",cowsHerd.totalCows);
        jCowsHerd.put("adult_count",cowsHerd.adultCows);
        jCowsHerd.put("breading_gap",cowsHerd.breedingGap);
        jCowsHerd.put("mq",cowsHerd.milkq);
        jCowsHerd.put("m",cowsHerd.milkquan);
        jCowsHerd.put("meatq",cowsHerd.meatq);
        jCowsHerd.put("meat",cowsHerd.meatquan);
        obj.put("cowsHerd",jCowsHerd);

        JSONObject jPigsHerd = new JSONObject();
        jPigsHerd.put("female_count",pigsHerd.totalPigs);
        jPigsHerd.put("breading_gap",pigsHerd.breedingGap);
        jPigsHerd.put("truf",pigsHerd.trufSnout);
        jPigsHerd.put("meatq",pigsHerd.meatq);
        jPigsHerd.put("meat",pigsHerd.meatquan);
        obj.put("pigsHerd",jPigsHerd);

        JSONObject jhorsesHerd = new JSONObject();
        jhorsesHerd.put("female_count",horsesHerd.totalMares);
        jhorsesHerd.put("endurance",horsesHerd.endurance);
        jhorsesHerd.put("meta",horsesHerd.metabolism);
        obj.put("horsesHerd",jhorsesHerd);


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
        obj.put("ingredients" ,ingredients);
        obj.put("isMinerCredo" ,isMinerCredo);
        obj.put("lockStudy" ,lockStudy);
        obj.put("alarmGreyseal" ,alarmGreyseal);
        obj.put("isQuestInfoVisible" ,isQuestInfoVisible);

        JSONArray users = new JSONArray ();
        for ( NLoginData user : logins ) {
            JSONObject userobj = new JSONObject ();
            userobj.put ( "name", user.name );
            userobj.put ( "pass", user.pass );
            userobj.put ( "isTokenUsed", user.isTokenUsed );
            userobj.put ( "token", user.token );
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
        obj.put("isShowGild", Slotted.show);
        obj.put("isShowVar",NFoodInfo.show);
        obj.put("isShowStackQ", Stack.show);
        obj.put("isEye",isEyed);
        obj.put("enablePfBoundingBoxes",enablePfBoundingBoxes);
        obj.put("showBB",showBB);
        obj.put("hideNature",hideNature);
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


            JSONObject jGoatHerd = ( JSONObject ) jsonObject.get("goatsHerd");
            if(jGoatHerd!=null) {
                goatsHerd.totalGoats = Integer.valueOf(jGoatHerd.get("female_count").toString());
                if(jGoatHerd.get("adult_count")!=null)
                    goatsHerd.adultGoats = Integer.valueOf(jGoatHerd.get("adult_count").toString());
                goatsHerd.breedingGap = Integer.valueOf(jGoatHerd.get("breading_gap").toString());
                goatsHerd.milkq = Double.valueOf(jGoatHerd.get("mq").toString());
                goatsHerd.milkquan = Double.valueOf(jGoatHerd.get("m").toString());
                goatsHerd.woolq = Double.valueOf(jGoatHerd.get("wq").toString());
                goatsHerd.woolquan = Double.valueOf(jGoatHerd.get("w").toString());
                goatsHerd.meatq = Double.valueOf(jGoatHerd.get("meatq").toString());
                goatsHerd.meatquan = Double.valueOf(jGoatHerd.get("meat").toString());
            }

            JSONObject jShepsHerd = ( JSONObject ) jsonObject.get("sheepsHerd");
            if(jShepsHerd!=null) {
                sheepsHerd.totalSheeps = Integer.valueOf(jShepsHerd.get("female_count").toString());
                if(jGoatHerd.get("adult_count")!=null)
                    sheepsHerd.adultSheeps = Integer.valueOf(jShepsHerd.get("adult_count").toString());
                sheepsHerd.breedingGap = Integer.valueOf(jShepsHerd.get("breading_gap").toString());
                sheepsHerd.milkq = Double.valueOf(jShepsHerd.get("mq").toString());
                sheepsHerd.milkquan = Double.valueOf(jShepsHerd.get("m").toString());
                sheepsHerd.woolq = Double.valueOf(jShepsHerd.get("wq").toString());
                sheepsHerd.woolquan = Double.valueOf(jShepsHerd.get("w").toString());
                sheepsHerd.meatq = Double.valueOf(jShepsHerd.get("meatq").toString());
                sheepsHerd.meatquan = Double.valueOf(jShepsHerd.get("meat").toString());
            }

            JSONObject jCowsHerd = ( JSONObject ) jsonObject.get("cowsHerd");
            if(jCowsHerd!=null) {
                cowsHerd.totalCows = Integer.valueOf(jCowsHerd.get("female_count").toString());
                if(jGoatHerd.get("adult_count")!=null)
                    cowsHerd.adultCows = Integer.valueOf(jCowsHerd.get("adult_count").toString());
                cowsHerd.breedingGap = Integer.valueOf(jCowsHerd.get("breading_gap").toString());
                cowsHerd.milkq = Double.valueOf(jCowsHerd.get("mq").toString());
                cowsHerd.milkquan = Double.valueOf(jCowsHerd.get("m").toString());
                cowsHerd.meatq = Double.valueOf(jCowsHerd.get("meatq").toString());
                cowsHerd.meatquan = Double.valueOf(jCowsHerd.get("meat").toString());
            }

            JSONObject jPigsHerd = ( JSONObject ) jsonObject.get("pigsHerd");
            if(jGoatHerd!=null) {
                pigsHerd.totalPigs = Integer.valueOf(jPigsHerd.get("female_count").toString());
                pigsHerd.breedingGap = Integer.valueOf(jPigsHerd.get("breading_gap").toString());
                pigsHerd.trufSnout = Double.valueOf(jPigsHerd.get("truf").toString());
                pigsHerd.meatq = Double.valueOf(jPigsHerd.get("meatq").toString());
                pigsHerd.meatquan = Double.valueOf(jPigsHerd.get("meat").toString());
            }

            JSONObject jHorsesHerd = ( JSONObject ) jsonObject.get("horsesHerd");
            if(jHorsesHerd!=null) {
                horsesHerd.totalMares = Integer.valueOf(jHorsesHerd.get("female_count").toString());
                horsesHerd.metabolism = Double.valueOf(jHorsesHerd.get("meta").toString());
                horsesHerd.endurance = Double.valueOf(jHorsesHerd.get("endurance").toString());
            }

            JSONArray jingredients = ( JSONArray ) jsonObject.get ( "ingredients" );
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
                    if(item.get("isTokenUsed")!=null)
                    {
                        if((boolean) item.get("isTokenUsed")) {
                            logData.isTokenUsed = true;
                            JSONArray tokenArray = ( JSONArray ) item.get ( "token" );
                            logData.token = new byte[tokenArray.size()];
                            for (int i = 0; i < tokenArray.size(); i++) {
                                logData.token[i]=(byte)(((long)tokenArray.get(i)) & 0xFF);
                            }
                        }
                    }
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
