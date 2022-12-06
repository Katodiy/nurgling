package nurgling.bots.actions;

import haven.Gob;
import nurgling.NAlias;
import nurgling.NGameUI;
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
        ArrayList<Gob> logs = Finder.findObjectsInArea(new NAlias("log", "oldtrunk"), input_area);
        for(Gob log : logs){
            new LiftObject(log).run(gui);
            new PlaceLifted(output_area,log.getHitBox(),log).run(gui);
        }
        return new Results(Results.Types.SUCCESS);
    }

    NArea input_area;
    NArea output_area;
}
