package nurgling.bots;


import haven.Button;
import haven.Coord;
import nurgling.NGameUI;
import nurgling.bots.actions.CabbageAction;
import nurgling.tools.AreaSelecter;
import nurgling.tools.NArea;

import java.util.concurrent.atomic.AtomicBoolean;


public class Cabbager extends Bot {

    public Cabbager(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Cabbager";
        win_sz.y = 100;
        
        runActions.add ( new CabbageAction( backed_area, unbacked_area ) );
    }
    
    
    @Override
    public void initAction ()
            throws InterruptedException {
        super.initAction ();
        int y = 0;
        window.add ( new Button ( window.buttons_size, "Не готовые" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter ( gameUI, _zone1, m_selection_start, unbacked_area),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        y += 25;
        window.add ( new Button ( window.buttons_size, "Готовые" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter ( gameUI, _zone2, m_selection_start, backed_area),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        y += 25;

        while (!_zone1.get () || !_zone2.get () ) {
            Thread.sleep ( 100 );
        }
    }
    
    @Override
    public void endAction () {
        _zone1.set ( false );
        _zone2.set ( false );
        m_selection_start.set ( false );
        super.endAction ();
    }

    private AtomicBoolean _zone1 = new AtomicBoolean ( false );
    private AtomicBoolean _zone2 = new AtomicBoolean ( false );
    private NArea unbacked_area = new NArea ();
    private NArea backed_area = new NArea ();
    private AtomicBoolean m_selection_start = new AtomicBoolean ( false );
}
