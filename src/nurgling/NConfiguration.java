package nurgling;

import dolda.jglob.Loader;
import haven.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.List;

import haven.render.BufPipe;
import nurgling.bots.settings.IngredientSettings;
import nurgling.bots.tools.AItem;
import nurgling.json.*;
import nurgling.json.parser.JSONParser;
import nurgling.json.parser.ParseException;
import nurgling.tools.AreasID;

public class NConfiguration {
    public static final Text.Foundry fnd = new Text.Foundry(Text.sans, 14);
    public boolean isVerified = false;
    public boolean isSubscribed = false;
    public HashMap<String, Color> colors = new HashMap<>();
    private static final NConfiguration instance = new NConfiguration();
    public boolean disabledCheck = false;
    public boolean enablePfBoundingBoxes = false;
    public HashMap<String, Integer> playerSpeed_h = new HashMap<String, Integer>();
    public HashMap<String, Integer> ingrTh = new HashMap<String, Integer>();
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

    public static String getCharKey()
    {
        return NUtils.getUI().sess.username + "/" + NUtils.getGameUI().chrid;
    }
    public static void saveButtons(String name, NGameUI.NButtonBeltSlot[] custom) {
        String key = getCharKey();
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
                    new InputStreamReader(new FileInputStream(path), "cp1251"));

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
    public HashMap<String, Coord> dragWidgets = new HashMap<>();

    public class ResizeWdg{
        public boolean locked = false;
        public Coord coord;

        public ResizeWdg(boolean locked, Coord coord) {
            this.locked = locked;
            this.coord = coord;
        }
    }
    public HashMap<String, ResizeWdg>  resizeWidgets = new HashMap<>();
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

        colors.put("free",Color.green);
        colors.put("ready",Color.red);
        colors.put("inwork",new Color(0,0,0,0));
        colors.put("warning",Color.yellow);
        colors.put("no_soil",Color.yellow);
        colors.put("no_water",Color.yellow);
        colors.put("full",Color.red);
        colors.put("not_full",Color.orange);

        playerSpeed_h.put("Crawl On", 0);
        playerSpeed_h.put("Walk On", 1);
        playerSpeed_h.put("Run On", 2);
        playerSpeed_h.put("Sprint On", 3);

        horseSpeed_h.put("Crawl On", 0);
        horseSpeed_h.put("Walk On", 1);
        horseSpeed_h.put("Run On", 2);
        horseSpeed_h.put("Sprint On", 3);

        dragWidgets.put("EquipProxy", new Coord(500,30));
        dragWidgets.put("ChatUI", new Coord(500,30));
        dragWidgets.put("MiniMap", new Coord(400,400));
        dragWidgets.put("belt0", new Coord(50,200));
        dragWidgets.put("belt1", new Coord(50,230));
        dragWidgets.put("belt2", new Coord(50,250));
        resizeWidgets.put("ChatUI", new ResizeWdg(false,new Coord(700,300)));
        resizeWidgets.put("MiniMap", new ResizeWdg(false,new Coord(133,133)));

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
        if (NUtils.getGameUI() != null && NUtils.getGameUI().map != null && NUtils.getGameUI().map.player() != null) {
            synchronized (NUtils.getGameUI().getMap()) {
                synchronized (NUtils.getGameUI().ui.sess.glob.oc) {
                    for (Gob gob : NUtils.getGameUI().ui.sess.glob.oc) {
                        ArrayList<Gob.Overlay> forRemove = new ArrayList<>();
                        for(Gob.Overlay ol: gob.ols){
                            if(ol.spr instanceof NSprite){
                                if(!(ol.spr instanceof NCropMarker && showCropStage))
                                    forRemove.add(ol);
                            }
                        }
                        synchronized (gob.ols) {
                            for (Gob.Overlay ol : forRemove) {
                                ol.remove();
                            }
                        }
                        gob.status = NGob.Status.ready_for_update;
                    }
                }
            }
        }
        /// TODO
//        Light.isEnable = NConfiguration.getInstance().nightVision;
//        if( NUtils.getGameUI()!=null && NUtils.getGameUI().map!=null && NUtils.getGameUI().map.player()!=null) {
//            synchronized (NUtils.getGameUI().getMap()) {
//                synchronized (NUtils.getGameUI().ui.sess.glob.oc) {
//                    for (Gob gob : NUtils.getGameUI().ui.sess.glob.oc) {
//                        if (gob instanceof NGob) {
//                            NGob unit = (NGob) gob;
//                            unit.deleteRings();
//                            unit.deleteCropStage();
//                            unit.deleteRingsMarks();
//                            unit.setAnimalRad();
//                            unit.setRing();
//                            unit.setCropStage();
//                        }
//                    }
//                }
//            }
//        }
    }

    public void write () {

        JSONObject obj = new JSONObject ();

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
                if(ingrTh.get(ingredientKey)!=null) {
                    jingredientKey.put("th", ingrTh.get(ingredientKey));
                }

                ingredients.add(jingredientKey);
            }
        }
        obj.put("ingredients" ,ingredients);

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
            coord.put("x", dragWidgets.get(name).x);
            coord.put("y", dragWidgets.get(name).y);
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
            coord.put("x", resizeWidgets.get(name).coord.x);
            coord.put("y", resizeWidgets.get(name).coord.y);
            coord.put("locked", resizeWidgets.get(name).locked);
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
            Color clr = colors.get(key);
            colorobj.put("name", key);
            colorobj.put("r", clr.getRed());
            colorobj.put("g", clr.getGreen());
            colorobj.put("b", clr.getBlue());
            colorobj.put("a", clr.getAlpha());
            colorsarr.add(colorobj);
        }
        obj.put("colors",colorsarr);
        obj.put("isGrid",isGrid);
        obj.put("isEye",isEyed);
        obj.put("enablePfBoundingBoxes",enablePfBoundingBoxes);
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

        try ( FileWriter file = new FileWriter ( "./config.nurgling.json" ) ) {
            file.write ( obj.toJSONString () );
        }
        catch ( IOException e ) {
            e.printStackTrace ();
        }

    }

    public static void initDefault () {
            getInstance ().read ( "./config.nurgling.json" );
    }

    public void read ( String path ) {
        read_drink_data();
        try {
            BufferedReader reader = new BufferedReader (
                    new InputStreamReader( new FileInputStream( path ), "cp1251" ) );
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = ( JSONObject ) parser.parse ( reader );

            JSONArray jingredients = ( JSONArray ) jsonObject.get ( "ingredients" );
            if(jingredients!=null) {
                Iterator<JSONObject> jingredient = jingredients.iterator();
                while (jingredient.hasNext()) {
                    JSONObject ingredient = jingredient.next();
                    String ingName = ingredient.get("name").toString();
                    IngredientSettings.data.put(ingName,
                            new AItem(AreasID.valueOf(ingredient.get("area_out").toString()),
                                    AreasID.valueOf(ingredient.get("barter_out").toString()),
                                    AreasID.valueOf(ingredient.get("area_in").toString()),
                                    AreasID.valueOf(ingredient.get("barter_in").toString())));
                    if(ingredient.containsKey("th")){
                        ingrTh.put(ingName,Integer.valueOf(ingredient.get("th").toString()));
                    }
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
                Iterator<JSONObject> iterator = msg.iterator();
                while (iterator.hasNext()) {
                    JSONObject item = iterator.next();
                    logins.add(new NLoginData(item.get("name").toString(), item.get("pass").toString()));
                }
            }
            JSONArray widgetsPos = ( JSONArray ) jsonObject.get ( "widgetsPos" );
            if(widgetsPos!=null) {
                Iterator<JSONObject> iterator2 = widgetsPos.iterator();
                while (iterator2.hasNext()) {
                    JSONObject item = iterator2.next();
                    dragWidgets.put(item.get("name").toString(), new Coord((int)((long) item.get("x")), (int)((long) item.get("y"))));
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
                        resizeWidgets.put(item.get("name").toString(), new ResizeWdg((boolean) item.get("locked"), new Coord((int) ((long) item.get("x")), (int) ((long) item.get("y")))));
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
            if ( jsonObject.get ( "horseSpeed" ) != null ) {
                horseSpeed = ( int ) (long) jsonObject.get ( "horseSpeed" );
            }

            if(jsonObject.get("red_players")!=null){
                JSONObject red = (JSONObject)jsonObject.get("red_players");
                players.put("red", new ArrowProp((boolean) red.get("arrow"),(boolean) red.get("ring"),(boolean) red.get("mark"),(boolean) red.get("mark_target")));
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
                    colors.put(item.get("name").toString(), new Color((int)((long)item.get("r")),(int)((long)item.get("g")),(int)((long)item.get("b")),(int)((long)item.get("a"))));
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
            e.printStackTrace ();
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
                        new InputStreamReader(new FileInputStream(path + "/drink_data.json"), "cp1251"));
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
