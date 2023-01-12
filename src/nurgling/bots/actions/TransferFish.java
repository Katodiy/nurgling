package nurgling.bots.actions;

import haven.WItem;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.bots.tools.AItem;
import nurgling.bots.tools.Ingredient;
import nurgling.tools.AreasID;

import java.util.ArrayList;


public class TransferFish implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        ArrayList<WItem> items = gui.getInventory().getItems(new NAlias("fish"));
        for(WItem item : items){
            AItem ingredient = Ingredient.get(item);
            NAlias name = new NAlias(NUtils.getInfo(item));
            if(ingredient!=null) {
                new TransferItemsToBarter(ingredient.barter_out, name, true).run(gui);
                new FillContainers(name, ingredient.area_out, new ArrayList<>()).run(gui);
            }
        }


        new FillContainers(new NAlias("fish"), AreasID.raw_fish,new ArrayList<>()).run(gui);

        return new Results ( Results.Types.SUCCESS );
    }
}
