package nurgling.bots;

import haven.Button;
import haven.Coord;
import nurgling.NGameUI;

import nurgling.bots.actions.BrickAction;
import nurgling.tools.AreaSelecter;
import nurgling.tools.NArea;

import java.util.concurrent.atomic.AtomicBoolean;


public class BrickMaker extends Bot {
    
    public BrickMaker(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Brick Maker";
        win_sz.y = 100;
        
        runActions.add(new BrickAction(clay_area, brick_area));
    }
    
    
    @Override
    public void initAction () throws InterruptedException {
        int y = 0;
        window.add ( new Button ( window.buttons_size, "Глина" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter ( gameUI, _zone1, m_selection_start, clay_area),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        y += 25;
        window.add ( new Button ( window.buttons_size, "Кирпичи" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter ( gameUI, _zone2, m_selection_start, brick_area),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        y += 25;

        while ( !_zone1.get () || !_zone2.get () ) {
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
    private NArea clay_area = new NArea ();
    private NArea brick_area = new NArea ();
    private AtomicBoolean m_selection_start = new AtomicBoolean ( false );
}
