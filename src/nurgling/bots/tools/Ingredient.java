package nurgling.bots.tools;

import haven.WItem;
import nurgling.NAlias;
import nurgling.NUtils;
import nurgling.bots.settings.IngredientSettings;
import nurgling.tools.AreasID;
import nurgling.tools.NArea;


public class Ingredient extends AItem{
    public NAlias item;

    public int count;
    public NArea inarea = null;
    public boolean isInfo = true;
    
    public Ingredient (
            NAlias item,
            AreasID area_out,
            int count
    ) {
        this.item = item;
        this.area_out = area_out;
        this.count = count;
    }
    
    public Ingredient (
            NAlias item,
            AreasID area_out,
            int count,
            AreasID barter_out,
            boolean isInfo
    ) {
        this.item = item;
        this.area_out = area_out;
        this.barter_out = barter_out;
        this.count = count;
        this.isInfo = isInfo;
    }
    
    public Ingredient (
            NAlias item,
            NArea inarea ,
            int count
    ) {
        this.item = item;
        this.inarea = inarea;
        this.count = count;
    }

    public static AItem get(WItem item) {
        return IngredientSettings.data.get(NUtils.getInfo(item));
    }
}
