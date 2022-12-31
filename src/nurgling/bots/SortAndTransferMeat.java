package nurgling.bots;

import haven.Button;

import nurgling.NGameUI;
import nurgling.bots.actions.TransferSortMeat;
import nurgling.tools.AreaSelecter;
import nurgling.tools.NArea;

import java.util.concurrent.atomic.AtomicBoolean;


public class SortAndTransferMeat extends Bot {
    
    
    public SortAndTransferMeat(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Sort and Transfer Meat";
        win_sz.y = 100;
        
        runActions.add ( new TransferSortMeat( meat_area ) );
    }
    
    
    @Override
    public void initAction ()
            throws InterruptedException {
        super.initAction ();
        window.add ( new Button ( window.buttons_size, "Meat" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter( gameUI, _start, m_selection_start, meat_area ),
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
    private NArea meat_area = new NArea ();
    private AtomicBoolean m_selection_start = new AtomicBoolean ( false );
}
