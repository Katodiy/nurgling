package nurgling.bots.actions;

import haven.WItem;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.bots.tools.AItem;
import nurgling.bots.tools.Ingredient;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class TransferBars implements Action {
    public static final NAlias metals = new NAlias( new ArrayList<> ( Arrays.asList ( "bar", "nugget", "pebble-gold" ) ),
         new ArrayList<> ( Arrays.asList ( "cinna" ) ) );
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        new CollectQuicksilver ().run ( gui );
        ArrayList<WItem> items = gui.getInventory().getItems(metals);
        for(WItem item : items){
            AItem ingredient = Ingredient.get(item);
            NAlias name = new NAlias(item.item.res.get().name);
            if(ingredient!=null) {
                new TransferItemsToBarter(ingredient.barter_out, name, false).run(gui);
                new FillContainers(name, ingredient.area_out, new ArrayList<>()).run(gui);
            }
        }
        new FillContainers(metals, AreasID.bar,new ArrayList<>()).run(gui);

        return new Results ( Results.Types.SUCCESS );
    }
}
