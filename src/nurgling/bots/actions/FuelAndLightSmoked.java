package nurgling.bots.actions;

import haven.Coord;
import haven.Gob;
import haven.WItem;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;


public class FuelAndLightSmoked implements Action {

    public FuelAndLightSmoked(NArea log_area, NArea smoke_area) {
        this.log_area = log_area;
        this.smoke_area = smoke_area;
    }

    @Override
    public Results run(NGameUI gui)
            throws InterruptedException {
        ArrayList<Gob> targets = new ArrayList<>();
        for (Gob gob : Finder
                .findObjectsInArea(new NAlias("smokeshed"), smoke_area)) {
            if ((gob.getModelAttribute() & 16) == 0) {
                targets.add(gob);
            }
        }
        for (Gob out : targets) {
            if (new WorkWithLog(5, new NAlias("log"), true, log_area).run(gui).type != Results.Types.SUCCESS) {
                return new Results(Results.Types.NO_ITEMS);
            }
            new PathFinder(gui, out).run();
            for (int i = 0; i < 5; i++) {
                new TakeToHand(new NAlias("block")).run(gui);
                int counter = 0;
                while (!gui.hand.isEmpty() && counter < 20) {
                    NUtils.activateItem(out);
                    Thread.sleep(50);
                    counter++;
                }
            }
            new LightGob(16, out).run(gui);
        }

        return new Results(Results.Types.SUCCESS);
    }

    NArea log_area;
    NArea smoke_area;
}
