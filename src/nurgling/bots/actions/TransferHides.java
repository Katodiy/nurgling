package nurgling.bots.actions;

import haven.GItem;

import nurgling.*;
import nurgling.bots.tools.AItem;
import nurgling.bots.tools.Ingredient;
import nurgling.tools.AreasID;

import java.util.ArrayList;
import java.util.Arrays;

public class TransferHides implements Action {
    private NAlias hides = new NAlias ( new ArrayList<> ( Arrays.asList ( "hide", "scale" ) ),
            new ArrayList<> ( Arrays.asList ( "blood", "raw" ) ) );

    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {

        ArrayList<GItem> items = gui.getInventory().getWItems(hides);
        for(GItem item : items){
            Ingredient ingredient = Ingredient.get(item);
            NAlias name = new NAlias(NUtils.getInfo(item));
            if((ingredient != null) && (((NGItem) item).quality() > ingredient.th)) {
                new TransferItemsToBarter(ingredient.barter_out, name, true, ingredient.th).run(gui);
                new FillContainers(new NAlias(item.resource().name), ingredient.area_out, new ArrayList<>(), ingredient.th).run(gui);
            }
        }

        new FillContainers(hides, AreasID.hqhides,new ArrayList<>(), AreasID.getTh( AreasID.hqhides)).run(gui);

        new FillContainers(hides, AreasID.lqhides,new ArrayList<>()).run(gui);

        return new Results ( Results.Types.SUCCESS );
    }
}
