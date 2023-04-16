package nurgling.bots.tools;

import haven.GItem;
import nurgling.NAlias;
import nurgling.NUtils;
import nurgling.bots.settings.IngredientSettings;
import nurgling.tools.AreasID;


public class Ingredient extends AItem{
    public NAlias item;
    public double th = 0;
    public boolean isGroup = false;

    public Ingredient(AreasID area_out, AreasID barter_out, AreasID area_in, AreasID barter_in, NAlias item, double th, boolean isGroup) {
        super(area_out, barter_out, area_in, barter_in);
        this.item = item;
        this.th = th;
        this.isGroup = isGroup;
    }

    public Ingredient()
    {
    }

    public static Ingredient get(GItem item) {
        return IngredientSettings.data.get(NUtils.getInfo(item));
    }

    public static Ingredient get(String info) {
        return IngredientSettings.data.get(info);
    }
}
