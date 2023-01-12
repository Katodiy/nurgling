package nurgling.bots;


import haven.Button;

import nurgling.NGameUI;
import nurgling.bots.actions.ClearSnowInArea;
import nurgling.tools.AreaSelecter;
import nurgling.tools.NArea;

import java.util.concurrent.atomic.AtomicBoolean;

public class SnowCleaner extends Bot {
    
    public SnowCleaner(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "SnowCleaner";
        win_sz.y = 100;

        runActions.add ( new ClearSnowInArea( area ) );
    }
    
    
    @Override
    public void initAction ()
            throws InterruptedException { super.initAction();
        window.add ( new Button ( window.buttons_size, "Area for cleaning" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter( gameUI, _start, m_selection_start, area ),
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
    private NArea area = new NArea ();
    private AtomicBoolean m_selection_start = new AtomicBoolean ( false );
}
