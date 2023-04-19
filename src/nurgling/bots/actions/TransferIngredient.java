package nurgling.bots.actions;

import haven.GItem;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.bots.actions.Action;
import nurgling.bots.actions.Results;
import nurgling.bots.tools.Ingredient;

import java.util.ArrayList;

public class TransferIngredient implements Action {
    String name;

    public TransferIngredient(String name) {
        this.name = name;
    }

    @Override
    public Results run(NGameUI gui) throws InterruptedException {
        if (Ingredient.get(name) != null)
            for (GItem item : gui.getInventory().getGItems(new NAlias(name))) {
                if(Ingredient.get(item).barter_out!=null) {
                    new TransferItemsToBarter(Ingredient.get(item).barter_out, new NAlias(name), true).run(gui);
                }
                else
                {
                    new FillContainers(new NAlias(name),Ingredient.get(name).area_out,new ArrayList<>()).run(gui);
                }
            }

        return new Results(Results.Types.SUCCESS);
    }


}
