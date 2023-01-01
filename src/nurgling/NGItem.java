package nurgling;

import haven.*;
import haven.res.ui.tt.q.qbuff.QBuff;
import haven.resutil.FoodInfo;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NGItem extends GItem {
    public double quantity = -1;
    public double wear;
    public int d;
    public int m;
    public NGItem(Indir<Resource> res, Message sdt) {
        super(res, sdt);
    }

    public ItemInfo getInfo(Class <? extends ItemInfo> candidate){
        for(ItemInfo inf : info){
            if(inf.getClass() == candidate){
                return inf;
            }
        }
        return null;
    }

    @Override
    public List<ItemInfo> info() {
        super.info();
        checkFood(info, getres().name);
        return(info);
    }

    private static double round2Dig(double value) {
        return Math.round(value * 100.0) / 100.0;
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
    public static void checkFood(List<ItemInfo> infoList, String resName) {
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
                if(NConfiguration.getInstance().collectFoodInfo)
                    NFoodWriter.add(parsedFoodInfo);
            }
        } catch (Exception ex) {
            System.out.println("Cannot create food info: " + ex.getMessage());
        }
    }

    public interface DoubleInfo extends OverlayInfo<Tex> {


        public double value();

        public default Tex overlay() {
            return(new TexI(RichText.render(String.format("$col[225,255,125]{%.2f}", value()),0).img));
        }

        public default void drawoverlay(GOut g, Tex tex) {
            g.aimage(tex, g.sz(), 1, 1);
        }

        public static BufferedImage doublerender(double value, Color col) {
            return(Utils.outline2(Text.render(Double.toString(value), col).img, Utils.contrast(col)));
        }
    }
    public static class Quantity extends ItemInfo implements DoubleInfo {
        private final double quantity;

        public Quantity(Owner owner, double quantity) {
            super(owner);
            this.quantity = quantity;
        }

        @Override
        public double value() {
            return quantity;
        }

        @Override
        public Tex overlay() {
            return DoubleInfo.super.overlay();
        }

        @Override
        public void drawoverlay(GOut g, Tex tex) {
            g.chcolor(new Color(0, 0, 0, 75));
            g.frect(g.sz().sub(tex.sz().x+2, tex.sz().y), tex.sz());
            g.chcolor();
            g.aimage(tex, g.sz(), 1, 1);
        }
    }
}
