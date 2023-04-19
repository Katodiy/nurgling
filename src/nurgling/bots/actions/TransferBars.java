package nurgling.bots.actions;

import haven.GItem;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.bots.tools.AItem;
import nurgling.bots.tools.Ingredient;
import nurgling.tools.AreasID;

import java.util.ArrayList;
import java.util.Arrays;

public class TransferBars implements Action {
    public static final NAlias metals = new NAlias( new ArrayList<> ( Arrays.asList ( "bar", "nugget", "pebble-gold" ) ),
         new ArrayList<> ( Arrays.asList ( "cinna" ) ) );
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        new CollectQuicksilver ().run ( gui );
        ArrayList<GItem> items = gui.getInventory().getWItems(metals);
        for(GItem item : items){
            AItem ingredient = Ingredient.get(item);
            NAlias name = new NAlias(item.res.get().name);
            if(ingredient!=null) {
                new TransferItemsToBarter(ingredient.barter_out, name, false).run(gui);
                new FillContainers(name, ingredient.area_out, new ArrayList<>()).run(gui);
            }
        }
        new FillContainers(metals, AreasID.bar,new ArrayList<>()).run(gui);

        return new Results ( Results.Types.SUCCESS );
    }
}
