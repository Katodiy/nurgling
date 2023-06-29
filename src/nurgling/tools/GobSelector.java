package nurgling.tools;

import haven.Gob;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;

import java.util.concurrent.atomic.AtomicBoolean;

public class GobSelector implements Runnable {
    NAlias result = null;
    private final AtomicBoolean flag;
    private final AtomicBoolean selection_start;

    public GobSelector(
            AtomicBoolean flag,
            AtomicBoolean selection_start,
            NAlias result
    ) {
        this.flag = flag;
        this.selection_start = selection_start;
        this.result = result;
    }

    @Override
    public void run() {
        NUtils.getGameUI().getMap ().isGobSelectorEnable.set(true);
        NGameUI gameUI = NUtils.getGameUI();
        try {
            gameUI.msg ( "Please, select object" );
            NUtils.waitEvent(()->!gameUI.getMap ().isGobSelectorEnable.get(),1000);
            synchronized ( gameUI.getMap () ) {
                Gob res = gameUI.getMap().getSelectedGob();
                if(res!=null)
                {
                    gameUI.msg ( "Selected: " +  res.getResName());
                    result.keys.add(res.getResName());
                    flag.set ( true );
                }
                else {
                    NUtils.getGameUI().error("No object selected");
                }
            }

            selection_start.set ( false );
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
