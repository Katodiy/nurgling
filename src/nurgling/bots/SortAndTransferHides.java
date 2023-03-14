package nurgling.bots;

import haven.Button;
import nurgling.NGameUI;
import nurgling.bots.actions.TransferSortHides;
import nurgling.tools.AreaSelecter;
import nurgling.tools.NArea;

import java.util.concurrent.atomic.AtomicBoolean;


public class SortAndTransferHides extends Bot {


    public SortAndTransferHides(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Sort and Transfer Hides";
        win_sz.y = 100;
        
        runActions.add ( new TransferSortHides(hides_area) );
    }
    
    
    @Override
    public void initAction ()
            throws InterruptedException {
        super.initAction ();
        window.add ( new Button ( window.buttons_size, "Hides" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter( gameUI, _start, m_selection_start, hides_area),
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
    private NArea hides_area = new NArea ();
    private AtomicBoolean m_selection_start = new AtomicBoolean ( false );
}
