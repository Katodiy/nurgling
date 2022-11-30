package nurgling.bots.actions;

import haven.Coord;
import haven.Gob;
import haven.Pair;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NHitBox;
import nurgling.NUtils;
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
            ArrayList<Gob> outarray = Finder.findObjects(new NAlias("dframe"));
            ArrayList<Pair<Gob, Boolean>> out = new ArrayList<>();
            outarray.sort(new Comparator<Gob>() {
                @Override
                public int compare(
                        Gob lhs,
                        Gob rhs
                ) {
                    // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                    return (lhs.rc.y > rhs.rc.y) ? -1 : ((lhs.rc.y < rhs.rc.y) ? 1 : (lhs.rc.x > rhs.rc.x) ? -1 : (
                            lhs.rc.x < rhs.rc.x) ? 1 : 0);
                }
            });

            for (Gob gob : outarray) {
                out.add(new Pair<Gob, Boolean>(gob, false));
            }
            do {
                while (gui.getInventory().getNumberFreeCoord(new Coord(2, 2)) > 1) {
//                    System.out.println((gui.getInventory().getNumberFreeCoord(new Coord(2, 2))));
                    int freeSpace = gui.getInventory().getFreeSpace();
                    if (new TakeItemsFromBarter(raw_hides,AreasID.raw_hides,false,gui.getInventory().getNumberFreeCoord(new Coord(2, 2))).run(gui).type != Results.Types.SUCCESS)
                        break;
                    NUtils.waitEvent(()->freeSpace!=gui.getInventory().getFreeSpace(),10);
                }
                current_size = gui.getInventory().getFreeSpace();
                new FillContainers(raw_hides, new NAlias("dframe"), new ArrayList<>()).run(gui);
                boolean isFull = true;
                for (Pair<Gob,Boolean> gob : out) {
                    if(!gob.b){
                        isFull = false;
                        break;
                    }
                }
                if(isFull)
                    break;
            }
            while (current_size != gui.getInventory().getFreeSpace());
        }
        new TransferToPile(AreasID.raw_hides, NHitBox.getByName("stockpile"), new NAlias("stockpile"), raw_hides).run(gui);
        return new Results(Results.Types.SUCCESS);
    }
}
