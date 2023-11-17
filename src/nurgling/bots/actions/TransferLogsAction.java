package nurgling.bots.actions;

import haven.Gob;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUI;
import nurgling.NUtils;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;

public class TransferLogsAction implements Action {
    public TransferLogsAction( NArea input_area, NArea output_area) {
        this.input_area = input_area;
        this.output_area = output_area;
    }

    @Override
    public Results run(NGameUI gui) throws InterruptedException {
        while (!Finder.findObjectsInArea(new NAlias("log", "oldtrunk"), input_area).isEmpty()) {
            ArrayList<Gob> logs = Finder.findObjectsInArea(new NAlias("log", "oldtrunk"), input_area);
            logs.sort(NUtils.d_comp);

            new LiftObject(logs.get(0)).run(gui);
            new PlaceLifted(output_area, logs.get(0).getHitBox(), logs.get(0)).run(gui);
        }
        return new Results(Results.Types.SUCCESS);
    }

    NArea input_area;
    NArea output_area;
}
