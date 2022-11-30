package nurgling.bots;

import haven.Button;

import nurgling.Dropper;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.bots.actions.LPExplorer;
import nurgling.tools.AreaSelecter;
import nurgling.tools.NArea;

import java.util.concurrent.atomic.AtomicBoolean;

public class LFExplorer extends Bot {
    
    public LFExplorer(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "LFExplorer";
        win_sz.y = 100;
        /// Ждем пока килны не потухнут
        explorer = new LPExplorer(tree_area);
        runActions.add ( explorer );
        
    }
    
    
    @Override
    public void initAction ()
            throws InterruptedException { super.initAction();
        window.add ( new Button ( window.buttons_size, "Деревья" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter( gameUI, _start, m_selection_start, tree_area ),
                            "Cont Area Selecter" ).start ();
                }
            }
        } );
        while ( !_start.get () ) {
            Thread.sleep ( 100 );
        }
        _dropper = new Dropper( NUtils.getGameUI (), new NAlias() );
        dropper = new Thread ( _dropper );
        dropper.start ();
    }
    
    @Override
    public void endAction () {
        _start.set ( false );
        if(dropper!=null) {
            if ( dropper.isAlive () ) {
                _dropper.isAlive.set ( false );
                try {
                    dropper.join ();
                }
                catch ( InterruptedException e ) {
                    e.printStackTrace ();
                }
            }
        }
        explorer.write ();
        super.endAction ();
    }
    Dropper _dropper;
    Thread dropper;
    private AtomicBoolean _start = new AtomicBoolean ( false );
    private NArea tree_area = new NArea();
    private AtomicBoolean m_selection_start = new AtomicBoolean ( false );
    LPExplorer explorer;

}
