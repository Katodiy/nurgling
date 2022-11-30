package nurgling.bots;

import haven.Button;
import haven.Coord;

import nurgling.NGameUI;
import nurgling.bots.actions.BranchAshMaker;
import nurgling.tools.AreaSelecter;
import nurgling.tools.NArea;

import java.util.concurrent.atomic.AtomicBoolean;


public class BranchAsh extends Bot {


    public BranchAsh(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "BranchAsh";
        win_sz.y = 150;

        ///Добавление цикла в действия бота
        runActions.add ( new BranchAshMaker(ash_area, block_area));
    }
    
    
    @Override
    public void initAction ()
            throws InterruptedException {
        super.initAction ();
        int y = 0;
        window.add ( new Button ( window.buttons_size, "Блоки" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter( gameUI, _zone1, m_selection_start, block_area ),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        y += 25;
        window.add ( new Button ( window.buttons_size, "Бочка" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter ( gameUI, _zone2, m_selection_start, ash_area),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );


        
        while ( !_zone1.get () || !_zone2.get ()) {
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
    private NArea block_area = new NArea();
    private NArea ash_area = new NArea ();
    private AtomicBoolean m_selection_start = new AtomicBoolean ( false );
}
