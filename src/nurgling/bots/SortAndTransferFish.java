package nurgling.bots;

import haven.Button;
import nurgling.NGameUI;
import nurgling.bots.actions.TransferSortFish;
import nurgling.tools.AreaSelecter;
import nurgling.tools.NArea;

import java.util.concurrent.atomic.AtomicBoolean;


public class SortAndTransferFish extends Bot {


    public SortAndTransferFish(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Sort and Transfer Fish";
        win_sz.y = 100;
        
        runActions.add ( new TransferSortFish(fish_area) );
    }
    
    
    @Override
    public void initAction ()
            throws InterruptedException {
        super.initAction ();
        window.add ( new Button ( window.buttons_size, "Meat" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable.set(true);
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter( gameUI, _start, m_selection_start, fish_area),
                            "Cont Area Selecter" ).start ();
                }
            }
        } );
        while ( !_start.get () ) {
            Thread.sleep ( 100 );
        }
    }
    
    @Override
    public void endAction () {
        _start.set ( false );
        m_selection_start.set ( false );
        super.endAction ();
    }
    
    private AtomicBoolean _start = new AtomicBoolean ( false );
    private NArea fish_area = new NArea ();
    private AtomicBoolean m_selection_start = new AtomicBoolean ( false );
}
