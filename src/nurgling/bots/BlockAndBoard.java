package nurgling.bots;

import haven.Button;
import haven.Coord;

import nurgling.NGameUI;
import nurgling.bots.actions.TreeLogAction;
import nurgling.tools.AreaSelecter;
import nurgling.tools.NArea;

import java.util.concurrent.atomic.AtomicBoolean;


public class BlockAndBoard extends Bot {
    
    
    public BlockAndBoard(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Chopper";
        win_sz.y = 100;
        
        
        ///Добавление цикла в действия бота
        runActions.add ( new TreeLogAction( _blocks, tree_area , pile_area, _coord) );
    }
    
    
    @Override
    public void initAction ()
            throws InterruptedException { super.initAction();
        int y = 0;
        window.add ( new Button ( window.buttons_size, "Доски" ) {
            @Override
            public void click () {
                _boards.set ( true );
                _blocks.set ( false );
                _coord.x = 1;
                _coord.y = 4;
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter( gameUI, _start, m_selection_start, tree_area ),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y  ) );
        y+=25;
        window.add ( new Button ( window.buttons_size, "Блоки" ) {
            @Override
            public void click () {
                _boards.set ( false );
                _blocks.set ( true );
                gameUI.getMap ().isAreaSelectorEnable = true;
                _coord.x = 2;
                _coord.y = 1;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter ( gameUI, _start, m_selection_start, tree_area ),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        y+=25;
        window.add ( new Button ( window.buttons_size, "Пайлы" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter ( gameUI, _zone, m_selection_start, pile_area ),
                            "Cont Area Selecter" ).start ();
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
    private NArea tree_area = new NArea ();
    private NArea pile_area = new NArea ();
    private AtomicBoolean m_selection_start = new AtomicBoolean ( false );
    private AtomicBoolean _blocks = new AtomicBoolean ( false );
    private AtomicBoolean _boards = new AtomicBoolean ( false );
    private Coord _coord = new Coord ( );
}
