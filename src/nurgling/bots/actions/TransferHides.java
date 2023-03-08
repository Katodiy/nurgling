package nurgling.bots.actions;

import haven.GItem;
import haven.Gob;

import haven.WItem;
import nurgling.*;
import nurgling.bots.tools.AItem;
import nurgling.bots.tools.Ingredient;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

import java.util.ArrayList;
import java.util.Arrays;

public class TransferHides implements Action {
    private NAlias hides = new NAlias ( new ArrayList<> ( Arrays.asList ( "hide", "scale" ) ),
            new ArrayList<> ( Arrays.asList ( "blood", "raw" ) ) );

    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {

        ArrayList<GItem> items = gui.getInventory().getItems(hides);
        for(GItem item : items){
            AItem ingredient = Ingredient.get(item);
            NAlias name = new NAlias(NUtils.getInfo(item));
            if((ingredient != null) && (NConfiguration.getInstance().ingrTh.get(name.keys.get(0))==null || ((NGItem) item).quality() > NConfiguration.getInstance().ingrTh.get(name.keys.get(0)))) {
                new TransferItemsToBarter(ingredient.barter_out, name, true).run(gui);
                new FillContainers(name, ingredient.area_out, new ArrayList<>()).run(gui);
            }
        }

        new FillContainers(hides, AreasID.hqhides,new ArrayList<>(), AreasID.getTh( AreasID.hqhides)).run(gui);

        new FillContainers(hides, AreasID.lqhides,new ArrayList<>()).run(gui);

        return new Results ( Results.Types.SUCCESS );
    }
}
