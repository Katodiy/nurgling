package nurgling;

import nurgling.json.JSONArray;
import nurgling.json.JSONObject;
import nurgling.json.parser.JSONParser;
import nurgling.json.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class NDataTables {


    public HashMap<String, String> data_vessel;
    public HashMap<String, List<String>> data_food;
    public HashMap<String, List<String>> data_drinks;
    public HashMap<String, String> vessel_res;

    public NDataTables() {
        read_drink_data();
    }

    private void read_drink_data() {
        try {
            data_vessel = new HashMap<>();
            data_food = new HashMap<>();
            data_drinks = new HashMap<>();
            vessel_res = new HashMap<>();
            URL url = NUtils.class.getProtectionDomain().getCodeSource().getLocation();

            if (url != null) {
                String path = url.toURI().getPath().substring(0, url.toURI().getPath().lastIndexOf("/"));
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(path + "/drink_data.json"), StandardCharsets.UTF_8));
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

                JSONArray msg3 = (JSONArray) jsonObject.get("vessel_res");
                Iterator<JSONObject> iterator3 = msg3.iterator();
                while (iterator3.hasNext()) {
                    JSONObject item = iterator3.next();
                    vessel_res.put((String)item.get("vessel"),(String)item.get("res"));
                }
            }
        }catch (IOException | ParseException | URISyntaxException e ) {
            e.printStackTrace ();
        }

    }
}
