package nurgling.bots;

import haven.Button;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.bots.actions.PatrolArea;
import nurgling.tools.AreaSelecter;
import nurgling.tools.NArea;

import java.util.concurrent.atomic.AtomicBoolean;

public class Plower extends Bot {
    
    public Plower(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Plower";
        win_sz.y = 100;
        
        
        /// Обходим зону с плугом
        runActions.add ( new PatrolArea( new NAlias( "vehicle/plow" ), plow_area ) );
    }
    
    
    @Override
    public void initAction ()
            throws InterruptedException { super.initAction(); super.initAction();
        gameUI.getMap ().isAreaSelectorEnable = true;
        if ( !m_selection_start.get () ) {
            m_selection_start.set ( true );
            new Thread ( new AreaSelecter( gameUI, _start, m_selection_start, plow_area ),
                    "Cont Area Selecter" ).start ();
        }
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
    private NArea plow_area = new NArea ();
    private AtomicBoolean m_selection_start = new AtomicBoolean ( false );
}
