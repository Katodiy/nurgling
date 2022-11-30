package nurgling.bots.actions;

import haven.WItem;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.bots.tools.InContainer;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;

public class TakeMaxFromContainers implements Action {

    public final ArrayList<InContainer> inContainers;

    public TakeMaxFromContainers(NAlias items, Object o, ArrayList<InContainer> inContainers) {
        this.items = items;
        if(o instanceof NArea){
            inArea = (NArea) o;
        }
        else if (o instanceof NAlias)
        {
            inName = (NAlias) o;
        }
        else
        {
            inId = (AreasID) o;
        }
        this.inContainers = inContainers;
        this.isMax = true;
    }

    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {

        /// Получаем имя входного контейнера
        if(inName == null){
            inName = NUtils.getContainerType((inArea!=null)?inArea:Finder.findNearestMark(inId)).name;
        }

        /// Получаем массив всех контейнеров если нет ранее полученного
        if(inContainers.isEmpty()) {
            ArrayList<InContainer> input = new ArrayList<>();
            if (inArea == null && inId == null) {
                input = InContainer.create(Finder.findObjects(inName));
            } else {
                input = InContainer.create(Finder.findObjects(inName, (inArea != null) ? inArea : Finder.findNearestMark(inId)));
            }
            inContainers.addAll(input);
        }

        for ( InContainer in : inContainers ) {
            if (NUtils.getGob(in.gob.id) != null && !in.isFree) {
                if (gui.getInventory().getFreeSpace() == 0) {
                    return new Results(Results.Types.SUCCESS);
                }
                new PathFinder(gui, in.gob).run();
                String cap = NUtils.getContainerType(in.gob).cap;
                if (!NUtils.checkName(cap, "Stockpile")) {
                    if (new OpenTargetContainer(in.gob, cap).run(gui).type == Results.Types.SUCCESS) {

                        ArrayList<WItem> witems = gui.getInventory(cap).getItems(items);
                        for (WItem item : witems) {
                            if (!NUtils.transferItem(gui.getInventory(cap), item, gui.getInventory())) {
                                return new Results(Results.Types.SUCCESS);
                            }
                        }
                        gui.getInventory(cap).parent.destroy();
                    }
                } else {
                    if (new TakeMaxFromPile(in.gob).run(gui).type == Results.Types.SUCCESS)
                        return new Results(Results.Types.SUCCESS);
                }

            }
        }
        return new Results ( Results.Types.FAIL );
    }

    NAlias items;
    NArea inArea;
    AreasID inId = null;
    NAlias inName = null;

    int needed;

    boolean isMax = false;
}
