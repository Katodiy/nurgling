package nurgling.bots.actions;


import nurgling.NAlias;
import nurgling.NConfiguration;
import nurgling.NGameUI;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

public class FillFuelSmelter implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        if (Finder.findObjects(new NAlias("primsmelter"))
                .size() == 0) {
            new FillFuelFromPiles(NConfiguration.getInstance().isMinerCredo ? 9 : 12, new NAlias("coal"), smelter_name, new NAlias("coal"),
                    AreasID.smelter, AreasID.coal, "Smelter").run(gui);
        } else {
            new FillFuelFromPiles(NConfiguration.getInstance().isMinerCredo ? 10 : 13, new NAlias("branch"), smelter_name, new NAlias("branch"),
                    AreasID.smelter, AreasID.branch, "Furnace").run(gui);
        }
        return new Results(Results.Types.SUCCESS);
    }
    
    public FillFuelSmelter(
            NAlias smelter_name
    ) {
        this.smelter_name = smelter_name;
    }
    
    NAlias smelter_name;
}
