package nurgling.bots.actions;

import haven.WItem;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.bots.tools.AItem;
import nurgling.bots.tools.Ingredient;
import nurgling.tools.AreasID;

import java.util.ArrayList;
import java.util.Arrays;

public class TransferCheese implements Action {
    public static final NAlias cheese = new NAlias( new ArrayList<> ( Arrays.asList ( "cheese" ) ),
         new ArrayList<> ( Arrays.asList ( "tray" ) ) );
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        new CollectQuicksilver ().run ( gui );
        ArrayList<WItem> items = gui.getInventory().getItems(cheese);
        for(WItem item : items){
            AItem ingredient = Ingredient.get(item);
            NAlias name = new NAlias(item.item.res.get().name);
            if(ingredient!=null) {
                new TransferItemsToBarter(ingredient.barter_out, name, false).run(gui);
                new FillContainers(name, ingredient.area_out, new ArrayList<>()).run(gui);
            }
        }
        new TransferItemsToBarter(AreasID.cheese_final, cheese, false).run(gui);
        new FillContainers(cheese, AreasID.cheese_final,new ArrayList<>()).run(gui);

        return new Results ( Results.Types.SUCCESS );
    }
}
