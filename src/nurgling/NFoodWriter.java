package nurgling;


import haven.HashDirCache;
import haven.ItemInfo;
import haven.KeyBinding;
import haven.ResCache;
import haven.res.ui.tt.q.qbuff.QBuff;
import haven.resutil.FoodInfo;
import nurgling.json.JSONArray;
import nurgling.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

public class NFoodWriter {
    static NFoodWriter instance = new NFoodWriter();

    private static class HashedFoodInfo {
        public String hash;
        public ParsedFoodInfo foodInfo;

        public HashedFoodInfo(String hash, ParsedFoodInfo foodInfo) {
            this.hash = hash;
            this.foodInfo = foodInfo;
        }
    }

    public static class FoodIngredient {
        private String name;
        private Integer percentage;

        public FoodIngredient(String name, Integer percentage) {
            this.name = name;
            this.percentage = percentage;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, percentage);
        }

        public Integer getPercentage() {
            return percentage;
        }

        public String getName() {
            return name;
        }
    }

    public static class FoodFEP {
        private String name;
        private Double value;

        public FoodFEP(String name, Double value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, value);
        }

        public String getName() {
            return name;
        }

        public Double getValue() {
            return value;
        }
    }

    public static class ParsedFoodInfo {
        public String itemName;
        public String resourceName;
        public Integer energy;
        public double hunger;
        public ArrayList<FoodIngredient> ingredients;
        public ArrayList<FoodFEP> feps;

        public ParsedFoodInfo() {
            this.itemName = "";
            this.resourceName = "";
            this.ingredients = new ArrayList<>();
            this.feps = new ArrayList<>();
        }

        @Override
        public int hashCode() {
            return Objects.hash(itemName, resourceName, ingredients);
        }

        public ArrayList<FoodFEP> getFeps() {
            return feps;
        }

        public ArrayList<FoodIngredient> getIngredients() {
            return ingredients;
        }

        public String getItemName() {
            return itemName;
        }

        public String getResourceName() {
            return resourceName;
        }

        public double getHunger() {
            return hunger;
        }

        public Integer getEnergy() {
            return energy;
        }
    }

    Map<String,ParsedFoodInfo> foodInfos = new HashMap<>();

    public static void write(){
        if(NConfiguration.getInstance().collectFoodInfo && instance.foodInfos.size()>0) {
            JSONArray obj = new JSONArray();
            for (String i : instance.foodInfos.keySet()) {
                ParsedFoodInfo food = instance.foodInfos.get(i);
                JSONObject jfood = new JSONObject();
                jfood.put("energy", food.getEnergy());
                JSONArray jfeeps = new JSONArray();
                for (FoodFEP fep : food.getFeps()) {
                    JSONObject jfep = new JSONObject();
                    jfep.put("name", fep.getName());
                    jfep.put("value", fep.getValue());
                    jfeeps.add(jfep);
                }
                jfood.put("feps", jfeeps);
                JSONArray jingredients = new JSONArray();
                for (FoodIngredient ingredient : food.getIngredients()) {
                    JSONObject jingredient = new JSONObject();
                    jingredient.put("name", ingredient.getName());
                    jingredient.put("percentage", ingredient.getPercentage());
                    jingredients.add(jingredient);
                }
                jfood.put("ingredients", jingredients);
                jfood.put("hunger", food.getHunger());
                jfood.put("itemName", food.getItemName());
                jfood.put("resourceName", food.getResourceName());
                obj.add(jfood);
            }
            SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
            Date date = new Date();
            new File(((HashDirCache) ResCache.global).base + "\\..\\food_info").mkdirs();
            String path = ((HashDirCache)ResCache.global).base +"\\..\\food_info\\" + formatter.format(date) + ".json";
            try (FileWriter file = new FileWriter(path)) {
                //String res = obj.toJSONString();
                file.write(obj.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String generateHash(ParsedFoodInfo foodInfo) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(foodInfo.itemName).append(";")
                    .append(foodInfo.resourceName).append(";");
            foodInfo.ingredients.forEach(it -> {
                stringBuilder.append(it.name).append(";").append(it.percentage).append(";");
            });

            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
            return getHex(hash);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Cannot generate food hash");
        }
        return null;
    }

    private static String getHex(byte[] bytes) {
        BigInteger bigInteger = new BigInteger(1, bytes);
        return bigInteger.toString(16);
    }

    private static void checkAndSave(ParsedFoodInfo info) {
        String hash = generateHash(info);
        if (instance.foodInfos.containsKey(hash)) {
            return;
        }
        instance.foodInfos.put(hash, info);
    }
    private static double round2Dig(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
    public static void addFood(List<ItemInfo> infoList, String resName) {
        try {
            FoodInfo foodInfo = ItemInfo.find(FoodInfo.class, infoList);
            if (foodInfo != null) {
                QBuff qBuff = ItemInfo.find(QBuff.class, infoList);
                double quality = qBuff != null ? qBuff.q : 10.0;
                double multiplier = Math.sqrt(quality / 10.0);

                ParsedFoodInfo parsedFoodInfo = new ParsedFoodInfo();
                parsedFoodInfo.resourceName = resName;
                parsedFoodInfo.energy = (int)(Math.round(foodInfo.end * 100));
                parsedFoodInfo.hunger = round2Dig(foodInfo.glut * 100);

                for (int i = 0; i < foodInfo.evs.length; i++) {
                    parsedFoodInfo.feps.add(new FoodFEP(foodInfo.evs[i].ev.nm, round2Dig(foodInfo.evs[i].a / multiplier)));
                }

                for (ItemInfo info : infoList) {
                    if (info instanceof ItemInfo.AdHoc) {
                        String text = ((ItemInfo.AdHoc) info).str.text;
                        // Skip food which base FEP's cannot be calculated
                        if (text.equals("White-truffled")
                                || text.equals("Black-truffled")
                                || text.equals("Peppered")) {
                            return;
                        }
                    }
                    if (info instanceof ItemInfo.Name) {
                        parsedFoodInfo.itemName = ((ItemInfo.Name) info).str.text;
                    }
                    if (info.getClass().getName().equals("Ingredient")) {
                        String name = (String) info.getClass().getField("name").get(info);
                        Double value = (Double) info.getClass().getField("val").get(info);
                        parsedFoodInfo.ingredients.add(new FoodIngredient(name, (int) (value * 100)));
                    } else if(info.getClass().getName().equals("Smoke")) {
                        String name = (String) info.getClass().getField("name").get(info);
                        Double value = (Double) info.getClass().getField("val").get(info);
                        parsedFoodInfo.ingredients.add(new FoodIngredient(name, (int) (value * 100)));
                    }
                }

                checkAndSave(parsedFoodInfo);
            }
        } catch (Exception ex) {
            System.out.println("Cannot create food info: " + ex.getMessage());
        }
    }
}

