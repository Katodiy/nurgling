package nurgling.bots;

import haven.Button;
import haven.Coord;

import nurgling.NGameUI;
import nurgling.bots.actions.DestroyAll;
import nurgling.tools.AreaSelecter;
import nurgling.tools.NArea;

import java.util.concurrent.atomic.AtomicBoolean;


public class Destroyer extends Bot {
    
    
    public Destroyer(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Destroyer";
        win_sz.y = 100;
        
        
        runActions.add ( new DestroyAll(tree_area  ) );
    }
    
    
    @Override
    public void initAction ()
            throws InterruptedException { super.initAction();
        gameUI.getMap ().isAreaSelectorEnable = true;
        if ( !m_selection_start.get () ) {
            m_selection_start.set ( true );
            new Thread ( new AreaSelecter( gameUI, _start, m_selection_start, tree_area ),
                    "Cont Area Selecter" ).start ();
        }
        while ( !_start.get ()  ) {
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
    private NArea tree_area = new NArea ();
    private AtomicBoolean m_selection_start = new AtomicBoolean ( false );
}
