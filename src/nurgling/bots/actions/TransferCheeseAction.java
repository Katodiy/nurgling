package nurgling.bots.actions;

import haven.*;
import nurgling.*;
import nurgling.bots.CheesedShedule;
import nurgling.bots.tools.OutContainer;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

import java.util.*;

public class TransferCheeseAction implements Action {
    @Override
    public Results run(NGameUI gui)
            throws InterruptedException {
        ArrayList<OutContainer> outContainers = new ArrayList<>();
        if(candidates.isEmpty()){
            for(CheesedShedule.CheeseBranch branch :CheesedShedule.branches)
                for(CheesedShedule.CheeseBranch.Cheese cheese : branch.cheeses){
                    if(cheese.place == targets_area)
                        candidates.add(cheese.name);
                }
        }
        for (Gob gob : Finder.findObjects(new NAlias("cheeserack"))) {
            if(!gob.isTag(NGob.Tags.free)) {
                if (gui.getInventory().getNumberFreeCoord(new Coord(2, 1)) < 3) {
                    new FillContainers(new NAlias("cheesetray"), AreasID.cheese_out, outContainers).run(gui);
                }
                new PathFinder(gui, gob, true).run();
                new OpenTargetContainer(gob, "Rack").run(gui);

                Window spwnd = gui.getWindow("Rack");
                if (gui.getInventory().getFreeSpace() == 0) {
                    return new Results(Results.Types.NO_FREE_SPACE);
                }
                if (spwnd != null) {
                    /// Находим все необходимые предметы в контейнере
                    for (GItem item : gui.getInventory("Rack").getWItems()) {
                        NUtils.waitEvent(()->item.spr!=null,10);
                        if (candidates.contains(NUtils.getContentName(item.info()))) {
                            if (!NUtils.transferItem(gui.getInventory("Rack"), item, gui.getInventory())) {
                                return new Results(Results.Types.FULL);
                            }
                        }

                    }
                    spwnd.destroy();
                    NUtils.waitEvent(() -> gui.getWindow("Rack") == null, 50);
                }
            }
        }
        new FillContainers(new NAlias("cheesetray"), AreasID.cheese_out, outContainers).run(gui);
        new FillContainers(new NAlias("cheesetray"), new NAlias ( "cheeserack" ), new ArrayList<>(), new TakeMaxFromContainers(new NAlias("cheesetray"),AreasID.cheese_in, new ArrayList<>()) ).run(gui);
        new FillContainers(new NAlias("cheesetray"), AreasID.cheese_in, new ArrayList<>() ).run(gui);
        return new Results(Results.Types.SUCCESS);
    }

    public TransferCheeseAction(AreasID targets_area) {
        switch (targets_area){
            case c_cellar:
                this.targets_area = CheesedShedule.CheeseBranch.Place.cellar;
                break;
            case c_mine:
                this.targets_area = CheesedShedule.CheeseBranch.Place.mine;
                break;
            case c_inside:
                this.targets_area = CheesedShedule.CheeseBranch.Place.inside;
                break;
            case c_outside:
                this.targets_area = CheesedShedule.CheeseBranch.Place.outside;
                break;
        }
    }

    Set<String> candidates = new HashSet<>();
    CheesedShedule.CheeseBranch.Place targets_area;
}


