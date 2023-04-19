package nurgling;


import haven.KeyBinding;
import nurgling.json.JSONArray;
import nurgling.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class NFoodWriter {
//    static NFoodWriter instance = new NFoodWriter();
//
//    Map<Integer,NGItem.ParsedFoodInfo> foodInfos = new HashMap<>();
//
//    public static void add(NGItem.ParsedFoodInfo foodInfo){
//        instance.foodInfos.put(foodInfo.hashCode(),foodInfo);
//    }
//    public void write(){
//        if(NConfiguration.getInstance().collectFoodInfo) {
//            JSONArray obj = new JSONArray();
//            for (Integer i : foodInfos.keySet()) {
//                NGItem.ParsedFoodInfo food = foodInfos.get(i);
//                JSONObject jfood = new JSONObject();
//                jfood.put("energy", food.getEnergy());
//                JSONArray jfeeps = new JSONArray();
//                for (NGItem.FoodFEP fep : food.getFeps()) {
//                    JSONObject jfep = new JSONObject();
//                    jfep.put("name", fep.getName());
//                    jfep.put("value", fep.getValue());
//                    jfeeps.add(jfep);
//                }
//                jfood.put("feps", jfeeps);
//                JSONArray jingredients = new JSONArray();
//                for (NGItem.FoodIngredient ingredient : food.getIngredients()) {
//                    JSONObject jingredient = new JSONObject();
//                    jingredient.put("name", ingredient.getName());
//                    jingredient.put("percentage", ingredient.getPercentage());
//                    jingredients.add(jingredient);
//                }
//                jfood.put("ingredients", jingredients);
//                jfood.put("hunger", food.getHunger());
//                jfood.put("itemName", food.getItemName());
//                jfood.put("resourceName", food.getResourceName());
//                obj.add(jfood);
//            }
//            SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
//            Date date = new Date();
//            new File("./food_info").mkdirs();
//            String path = "./food_info/" + formatter.format(date) + ".json";
//            try (FileWriter file = new FileWriter(path)) {
//                //String res = obj.toJSONString();
//                file.write(obj.toJSONString());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

}
