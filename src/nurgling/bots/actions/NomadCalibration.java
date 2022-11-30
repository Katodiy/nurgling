package nurgling.bots.actions;

import haven.Coord2d;
import haven.Gob;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class NomadCalibration implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        Gob start_gob = Finder.findObjectInArea(new NAlias(new ArrayList<>(Arrays.asList("milestone-stone-m", "milestone-wood", "pow"))), 3000, area);
        if (start_gob != null) {
            Coord2d start = Finder.findObjectInArea(new NAlias(new ArrayList<>(Arrays.asList("milestone-stone-m", "milestone-wood", "pow"))), 3000, area).rc;
            Coord2d next = new Coord2d(start.x, start.y);
            int counter = 0;
            int sum = 0;

            while (worked.get()) {
                Thread.sleep(100);
                Coord2d current = gui.getMap().player().rc;
                if (next.dist(current) >= 100) {
                    next = current;
                    Coord2d forWrite = next.sub(start);
                    coords.add(forWrite);
                    if (counter > 10) {
                        counter = 0;
                        gui.msg("Total coord: " + sum);
                    }
                    counter++;
                    sum++;
                }
            }

            gui.msg("stopped");
            return new Results(Results.Types.SUCCESS);
        } else {
            return new Results(Results.Types.NO_START_OBJECT);
        }
    }
    
    public NomadCalibration(NArea area, AtomicBoolean worked , ArrayList<Coord2d> coord2ds) {
        this.worked = worked; this.area = area;this.coords = coord2ds;
    }
    ArrayList<Coord2d> coords;
    NArea area;
    AtomicBoolean worked;
}
