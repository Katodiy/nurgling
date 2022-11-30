package nurgling.bots;

import haven.Button;
import haven.Coord;
import nurgling.NGameUI;
import nurgling.bots.actions.FuelAndLightSmoked;
import nurgling.tools.AreaSelecter;
import nurgling.tools.NArea;

import java.util.concurrent.atomic.AtomicBoolean;


public class FLSmoked extends Bot {


    public FLSmoked(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "FLSmoked";
        win_sz.y = 100;
        

        runActions.add ( new FuelAndLightSmoked(log_area,smoke_area));
    }
    
    
    @Override
    public void initAction ()
            throws InterruptedException {
        super.initAction ();
        int y = 0;
        window.add ( new Button ( window.buttons_size, "Брёвна" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter( gameUI, _start, m_selection_start, log_area),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        y += 25;
        window.add ( new Button ( window.buttons_size, "Коптильни" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter ( gameUI, _zone1, m_selection_start, smoke_area),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );

        while ( !_start.get () || !_zone1.get () ) {
            Thread.sleep ( 100 );
        }
    }
    
    @Override
    public void endAction () {
        _start.set ( false );
        _zone1.set ( false );
        m_selection_start.set ( false );
        super.endAction ();
    }
    
    private AtomicBoolean _start = new AtomicBoolean ( false );
    private AtomicBoolean _zone1 = new AtomicBoolean ( false );
    private NArea log_area = new NArea ();
    private NArea smoke_area = new NArea ();
    private AtomicBoolean m_selection_start = new AtomicBoolean ( false );
}
