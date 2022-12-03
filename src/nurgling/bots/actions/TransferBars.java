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

    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        new CollectQuicksilver ().run ( gui );
        Results.Types res;
        do {
            res = new TakeMaxFromContainers (
                    new NAlias( new ArrayList<> ( Arrays.asList ( "bar", "nugget", "pebble-gold" ) ),
                            new ArrayList<> ( Arrays.asList ( "cinna" ) ) ),
                    new NAlias ( "smelter" ) , new ArrayList<>() ).run (
                    gui ).type;

            ArrayList<WItem> items = gui.getInventory().getItems(new NAlias("bar", "nugget", "pebble-gold"));
            for(WItem item : items){
                AItem ingredient = Ingredient.get(item);
                NAlias name = new NAlias(NUtils.getInfo(item));
                if(ingredient!=null) {
                    new TransferItemsToBarter(ingredient.barter_out, name, true).run(gui);
                    new FillContainers(name, ingredient.area_out, new ArrayList<>()).run(gui);
                }
            }

            new FillContainers(new NAlias("bar", "nugget", "pebble-gold"), AreasID.bar,new ArrayList<>()).run(gui);
        }
        while ( res != Results.Types.FAIL );
        
        return new Results ( Results.Types.SUCCESS );
    }
}
