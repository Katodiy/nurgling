package nurgling.bots.actions;

import haven.Coord;
import haven.Gob;
import haven.Pair;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NHitBox;
import nurgling.NUtils;
import nurgling.bots.tools.OutContainer;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class DryerAction implements Action {
    private NAlias hides = new NAlias(new ArrayList<>(Arrays.asList("hide", "scale")),
            new ArrayList<>(Arrays.asList("blood", "raw", "Fresh", "Jacket", "hidejacket", "cape")));

    private NAlias raw_hides = new NAlias(new ArrayList<String>(Arrays.asList("blood", "raw", "fresh")),
            new ArrayList<String>(Arrays.asList("stern", "tea", "cape", "straw", "Straw")));

    @Override
    public Results run(NGameUI gui)
            throws InterruptedException {


        /// Забираем шкуры и переносим их в пайлы
        new FreeFrames(hides).run(gui);

        /// Заполняем новыми шкурами
        if (Finder.findObjectInArea(new NAlias("barter"), 3000, Finder.findNearestMark(AreasID.raw_hides)) == null) {
            new FillContainers(raw_hides, new NAlias("dframe"), new ArrayList<>(), new TakeMaxFromContainers( raw_hides, AreasID.raw_hides, new ArrayList<>())).run(gui);

        } else {
            int current_size;
            ArrayList<OutContainer> outContainers = new ArrayList<>();
            do {
                if (gui.getInventory().getNumberFreeCoord(new Coord(2, 2)) > 1) {
                    new TakeItemsFromBarter(raw_hides,AreasID.raw_hides,false,gui.getInventory().getNumberFreeCoord(new Coord(2, 2))).run(gui);
                }
                current_size = gui.getInventory().getFreeSpace();
                new FillContainers(raw_hides, new NAlias("dframe"), outContainers).run(gui);
                boolean isFull = true;
                for (OutContainer gob : outContainers) {
                    if(!gob.isFull){
                        isFull = false;
                        break;
                    }
                }
                if(isFull)
                    break;
            }
            while (current_size != gui.getInventory().getFreeSpace());
            new TransferItemsToBarter(AreasID.raw_hides,raw_hides,false).run(gui);
        }
        new TransferToPile(AreasID.raw_hides, NHitBox.getByName("stockpile"), new NAlias("stockpile"), raw_hides).run(gui);
        return new Results(Results.Types.SUCCESS);
    }
}
