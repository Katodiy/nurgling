package nurgling.bots.actions;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.tools.Finder;

public class MonitorGob implements Action {
    @Override
    public Results run(NGameUI gui) throws InterruptedException {
        while(Finder.findObject(new NAlias("/orca", "/spermwhale"))!=null){
            Thread.sleep(100);
        }
        return new Results(Results.Types.SUCCESS);
    }
}
