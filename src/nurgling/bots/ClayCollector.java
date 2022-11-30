package nurgling.bots;

import haven.Button;
import haven.Coord;
import nurgling.NGameUI;
import nurgling.bots.actions.ClayDigging;
import nurgling.tools.AreaSelecter;
import nurgling.tools.NArea;

import java.util.concurrent.atomic.AtomicBoolean;


public class ClayCollector extends Bot {

    
    public ClayCollector(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Clay Digger";
        win_sz.y = 100;
        
        runActions.add ( new ClayDigging( clay_area, pile_area ) );
    }
    
    
    @Override
    public void initAction ()
            throws InterruptedException { super.initAction();
        int y = 0;
        window.add ( new Button ( window.buttons_size, "Зона глины" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter( gameUI, _start, m_selection_start, clay_area ),
                            "Clay Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        y += 25;
        window.add ( new Button ( window.buttons_size, "Пайлы" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter ( gameUI, _zone, m_selection_start, pile_area ),
                            "Pile Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        while ( !_start.get () || !_zone.get () ) {
            Thread.sleep ( 100 );
        }
    }
    
    @Override
    public void endAction () {
        _start.set ( false );
        _zone.set ( false );
        m_selection_start.set ( false );
        super.endAction ();
    }
    
    private AtomicBoolean _start = new AtomicBoolean ( false );
    private AtomicBoolean _zone = new AtomicBoolean ( false );
    private NArea clay_area = new NArea ();
    private NArea pile_area = new NArea();
    private AtomicBoolean m_selection_start = new AtomicBoolean ( false );
}
