package nurgling.bots.actions;

import haven.Coord;
import haven.Gob;
import haven.Pair;
import nurgling.*;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

import static nurgling.NUtils.getGameUI;

public class EarthTransportBot implements Action {
    private NAlias hides = new NAlias(new ArrayList<>(Arrays.asList("hide", "scale")),
            new ArrayList<>(Arrays.asList("blood", "raw", "Fresh", "Jacket", "hidejacket")));

    private NAlias raw_hides = new NAlias(new ArrayList<String>(Arrays.asList("blood", "raw", "fresh")),
            new ArrayList<String>(Arrays.asList("stern")));

    @Override
    public Results run(NGameUI gui)
            throws InterruptedException {

        while (true) {
//            NUtils.command(new char[]{'a', 'h', 'h'});
//            NUtils.waitEvent(() -> gui.getProg() >= 0, 50);
//            NUtils.waitEvent(() -> gui.getProg() < 0, 10000);
//            NUtils.waitEvent(() -> getGameUI().ui.sess.glob.map.isLoaded(), 200);
//
//            NUtils.waitEvent(() -> Finder.findObject(new NAlias("pow")) != null && Finder.findObject(new NAlias("pow")).rc.dist(getGameUI().map.player().rc) < 10, 500);
//            new TakeMaxFromPile(Finder.findObject(new NAlias("stockpile-soil"))).run(gui);
//            new PathFinder(gui, Finder.findObject(new NAlias("runestone"))).run();
//            new PathFinder(gui, Finder.findObject(new NAlias("sign"))).run();
//
//            new TransferToPile(NHitBox.getByName("stockpile-soil"), new NAlias("soil"), new NAlias("soil","worm")).run(gui);
        }
//        return new Results(Results.Types.SUCCESS);
    }
}
