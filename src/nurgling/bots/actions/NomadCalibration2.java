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

public class NomadCalibration2 implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        Gob start_gob = Finder.findObjectInArea(new NAlias(new ArrayList<>(Arrays.asList("milestone-stone-m", "milestone-wood", "pow"))), 3000, area);
        if (start_gob != null) {
            Coord2d start_coord = Finder.findObjectInArea(new NAlias(new ArrayList<>(Arrays.asList("milestone-stone-m", "milestone-wood", "pow"))), 3000, area).rc;
            start.x = start_coord.x;
            start.y = start_coord.y;
            while (worked.get()) {
                Thread.sleep(100);
            }

            gui.msg("stopped");
            return new Results(Results.Types.SUCCESS);
        } else {
            return new Results(Results.Types.NO_START_OBJECT);
        }
    }

    public NomadCalibration2(NArea area, AtomicBoolean worked , ArrayList<Coord2d> coord2ds, Coord2d start) {
        this.worked = worked; this.area = area;this.coords = coord2ds; this.start = start;
    }
    ArrayList<Coord2d> coords;
    NArea area;
    AtomicBoolean worked;

    Coord2d start;
}
