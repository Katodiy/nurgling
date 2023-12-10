package nurgling.tools;

import haven.HashDirCache;
import haven.ResCache;
import nurgling.json.JSONArray;
import nurgling.json.JSONObject;
import nurgling.json.parser.JSONParser;
import nurgling.json.parser.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;

public enum AreasID {
    no_area,
    branch ,
    seed_in,
    seed_out,
    raw_hides,
    lqhides,
    tobacco_in,
    tobacco_out,
    block,
    ore,
    bar,
    coal,
    raw_fish,
    dry_fish,
    candelabrum,
    fox_hides,
    bat_hides,
    hqhides,
    tanning_flued,
    leather, inten, entr, out_bone1, lqbone, fat, hqbone, out_bone2, g_bone, meat1, bear_meat, deer_meat, moose_meat, walrus_meat, badger_meat, meat2, fox_meat, greyseal_meat, lynx_meat, meat3, chiken_meat, meat4, tin, b_common_bar, copper, ciron, lead, gold, b_uncommon_bar, silver, sausages, sausages1, sausages2, special3, raw_meat, meat6, pork_meat, cow_meat, lamb_meat, horse_meat, goat_meat, special1, special7, special2, special5, special6, garden1, gardenRes, garden2, bloodstern, garden3, boar_meat, kit_meat1, kit_meat2, special9, special8, special4, onion, thyme, butter, calibration, candle, barrel_work_zone, slag, moose_hides, wolverine_hides, angler_hides, greyseal_hides, bear_hides, boar_hides, bone1, backed, unbacked, barrels, logs, kritter, carrot, swill, hens, chicken, feather, eggs, water, pigs, truffle, cows, sheeps, milk, wool, goats, flax, flaxFibre,
    hide1, hide2, hide3, bunny, rabbit, bar1, bar2, horses,

    smelter,
    cheese_in, cheese_out, curd_out, cheese_main, c_outside, c_inside, c_cellar, c_mine,
    fish1,fish2,fish3,fish4,fish5,fish6,fish7,fish8,fish9,fish10,fish11,fish12,fish13,fish14,fish15,fish16,fish17,fish18,fish19,fish20,fish21,fish22,fish23,fish24,fish25,fish26,fish27,fish28,fish29,fish30,fish31,fish32,fish33,fish34,fish35,fish36,fish37,fish38,fish39,fish40, cheese_final, tobacco_work, pumpkin, hempFibre, hemp, turnip, kiln, readyPot, ugardenpot, dros, fineryforge, cast_iron;

    public static void init() {
        read(( ((HashDirCache) ResCache.global).base +"/../" + "./calibr.json" ));
    }

    public static void parseJson(JSONObject jsonObject){
        JSONArray widgetsPos = ( JSONArray ) jsonObject.get ( "data" );
        if(widgetsPos!=null) {
            Iterator<JSONObject> iterator2 = widgetsPos.iterator();
            while (iterator2.hasNext()) {
                JSONObject item = iterator2.next();
                try {
                    data.put(AreasID.valueOf(item.get("key").toString()), item.get("value").toString());
                    thresholds.put(AreasID.valueOf(item.get("key").toString()), item.get("th")!=null ? Double.parseDouble(item.get("th").toString()): 0);
                }catch (IllegalArgumentException e){
                    // skip old fields
                }
            }
        }
    }

    public static void read(String path){
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader( new FileInputStream(path), "UTF-8" ) );
            JSONParser parser = new JSONParser();
            parseJson(( JSONObject ) parser.parse ( reader ));
        } catch (IOException | ParseException ex) {
            System.out.println("No areas settings. calibr.json not found");
        }
    }


    public static JSONObject constructJson(){
        JSONObject obj = new JSONObject ();
        JSONArray is = new JSONArray ();
        Double value = 0.;
        for ( AreasID setting : data.keySet() ) {
            JSONObject is_obj = new JSONObject ();
            is_obj.put ( "key", setting.toString() );
            is_obj.put ( "value", data.get(setting) );
            is_obj.put ( "th", (value = thresholds.get(setting))!=null?value:0);
            is.add(is_obj);
        }
        obj.put("data",is);
        return obj;
    }

    public static void write(){
        try (OutputStreamWriter file = new OutputStreamWriter(Files.newOutputStream(Paths.get((((HashDirCache) ResCache.global).base +"/../" + "./calibr.json" ))), StandardCharsets.UTF_8)) {
            file.write ( constructJson().toJSONString () );
            file.close();
        }
        catch ( IOException e ) {
            e.printStackTrace ();
        }
    }
    private static final HashMap<AreasID,String> data = new HashMap<>();
    private static final HashMap<AreasID,Double> thresholds = new HashMap<>();
    public static String get(AreasID id) {
        return data.get(id);
    }
    public static AreasID find(String name) {
        for(AreasID key: data.keySet())
            if(data.get(key).equals(name))
                return key;
        return null;
    }

    public static double getTh(AreasID id) {
        if(thresholds.get(id)!= null){
            return thresholds.get(id);
        }
        return 0;
    }

    public static void set(AreasID valueOf, String item) {
        data.put(valueOf,item);
    }

    public static void set(AreasID valueOf, Double item) {
        thresholds.put(valueOf,item);
    }
}

