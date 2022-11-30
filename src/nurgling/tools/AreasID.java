package nurgling.tools;

import haven.*;
import haven.res.lib.itemtex.ItemTex;
import nurgling.NConfiguration;
import nurgling.NLoginData;
import nurgling.NPathVisualizer;
import nurgling.json.JSONArray;
import nurgling.json.JSONObject;
import nurgling.json.parser.JSONParser;
import nurgling.json.parser.ParseException;

import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;

public enum AreasID {
    no_area,
    branch ,
    seed_in,
    seed_out,
    raw_hides,
    lqhides,
    block,
    ore,
    bar,
    coal,
    raw_fish,
    dry_fish,
    candelabrun,
    fox_hides,
    bat_hides,
    hqhides,
    tanning_flued,
    leather, inten, entr, out_bone1, lqbone, fat, hqbone, out_bone2, g_bone, meat1, bear_meat, deer_meat, moose_meat, walrus_meat, badger_meat, meat2, fox_meat, greyseal_meat, lynx_meat, meat3, chiken_meat, meat4, tin, b_common_bar, copper, ciron, lead, gold, b_uncommon_bar, silver, sausages, sausages1, sausages2, special3, raw_meat, meat6, pork_meat, cow_meat, lamb_meat, horse_meat, goat_meat, special1, special7, special2, special5, special6, garden1, gardenRes, garden2, bloodstern, garden3, boar_meat, kit_meat1, kit_meat2, special9, special8, special4, onion, thyme, butter, calibration, candle, barrel_work_zone, slag, moose_hides, wolverine_hides, angler_hides, greyseal_hides, bear_hides, boar_hides, bone1, backed, unbacked, barrels, logs, kritter, carrot, silo, hens, chicken, feather, eggs, water, pigs, truffle, cows, sheeps, milk, wool, goats;

    public static void init() {
        read();
    }

    public static void read(){
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader( new FileInputStream( "./calibr.json" ), "cp1251" ) );
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = ( JSONObject ) parser.parse ( reader );
            JSONArray widgetsPos = ( JSONArray ) jsonObject.get ( "data" );
            if(widgetsPos!=null) {
                Iterator<JSONObject> iterator2 = widgetsPos.iterator();
                while (iterator2.hasNext()) {
                    JSONObject item = iterator2.next();
                    data.put(AreasID.valueOf(item.get("key").toString()), item.get("value").toString());
                    thresholds.put(AreasID.valueOf(item.get("key").toString()), item.get("th")!=null ? Integer.parseInt(item.get("th").toString()): 0);
                }
            }
        } catch (IOException | ParseException ex) {
            ex.printStackTrace();
        }
    }

    public static void write(){
        JSONObject obj = new JSONObject ();
        JSONArray is = new JSONArray ();
        Integer value = 0;
        for ( AreasID setting : data.keySet() ) {
            JSONObject is_obj = new JSONObject ();
            is_obj.put ( "key", setting.toString() );
            is_obj.put ( "value", data.get(setting) );
            is_obj.put ( "th", (value = thresholds.get(setting))!=null?value:0);
            is.add(is_obj);
        }
        obj.put("data",is);
        try ( FileWriter file = new FileWriter ( "./calibr.json" ) ) {
            file.write ( obj.toJSONString () );
        }
        catch ( IOException e ) {
            e.printStackTrace ();
        }
    }
    private static final HashMap<AreasID,String> data = new HashMap<>();
    private static final HashMap<AreasID,Integer> thresholds = new HashMap<>();
    public static String get(AreasID id) {
        return data.get(id);
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

    public static void set(AreasID valueOf, Integer item) {
        thresholds.put(valueOf,item);
    }
}

