package nurgling.tools;

import haven.Coord2d;
import nurgling.NGameUI;

import java.util.concurrent.atomic.AtomicBoolean;

public class AreaSelecter implements Runnable {
    private AtomicBoolean flag;
    private NGameUI gameUI;
    private AtomicBoolean selection_start;
    protected NArea result;
    
    public AreaSelecter(
            NGameUI gameUI,
            AtomicBoolean flag,
            AtomicBoolean selection_start,
            NArea result
    ) {
        this.gameUI = gameUI;
        this.flag = flag;
        this.selection_start = selection_start;
        this.result = result;
    }
    
    @Override
    public void run () {
        gameUI.getMap ().isAreaSelectorEnable = true;
        try {
            gameUI.msg ( "Выберите зону" );
            while ( gameUI.getMap ().isAreaSelectorEnable ) {
                
                Thread.sleep ( 100 );
            }
            synchronized ( gameUI.getMap () ) {
                result.begin = new Coord2d ( gameUI.getMap ().getSelection ().begin.x,
                        gameUI.getMap ().getSelection ().begin.y );
                result.end = new Coord2d( gameUI.getMap ().getSelection ().end.x, gameUI.getMap ().getSelection ().end.y);
                result.center = new Coord2d ( result.begin.x + (result.end.x-result.begin.x)/2,
                        result.begin.y + (result.end.y-result.begin.y)/2 );
            } flag.set ( true );
            result.show();
            selection_start.set ( false );
        }
        catch ( InterruptedException e ) {
            e.printStackTrace ();
        }
    }
}
