package nurgling.bots;

import haven.Button;
import haven.Coord;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.bots.actions.TreeItemsCollection;
import nurgling.tools.AreaSelecter;
import nurgling.tools.NArea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;


public class FruitCollector extends Bot {


    public FruitCollector(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Fruits Collector";
        win_sz.y = 100;
        
        
        ///Добавление цикла в действия бота
        runActions.add ( new TreeItemsCollection( tree_area , pile_area, new NAlias(new ArrayList<> ( Arrays.asList ( "apple")),new ArrayList<> ( Arrays.asList ( "worm"))), "apple"
                ,"Pick" ) );
        runActions.add ( new TreeItemsCollection( tree_area , pile_area, new NAlias(new ArrayList<> ( Arrays.asList ( "lemon"))), "lemon"
                ,"Pick" ) );
        runActions.add ( new TreeItemsCollection( tree_area , pile_area, new NAlias(new ArrayList<> ( Arrays.asList ( "medlar"))), "medlar"
                ,"Pick" ) );
        runActions.add ( new TreeItemsCollection( tree_area , pile_area, new NAlias(new ArrayList<> ( Arrays.asList ( "pear"))), "pear"
                ,"Pick" ) );
        runActions.add ( new TreeItemsCollection( tree_area , pile_area, new NAlias(new ArrayList<> ( Arrays.asList ( "persimmon"))), "persimmon"
                ,"Pick" ) );
        runActions.add ( new TreeItemsCollection( tree_area , pile_area, new NAlias(new ArrayList<> ( Arrays.asList ( "plum"))), "plum"
                ,"Pick" ) );
        runActions.add ( new TreeItemsCollection( tree_area , pile_area, new NAlias(new ArrayList<> ( Arrays.asList ( "quince"))), "quince"
                ,"Pick" ) );
        runActions.add ( new TreeItemsCollection( tree_area , pile_area, new NAlias(new ArrayList<> ( Arrays.asList ( "sorb"))), "sorb"
                ,"Pick" ) );
    }
    
    
    @Override
    public void initAction ()
            throws InterruptedException { super.initAction();
        int y = 0;
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
    private NArea tree_area = new NArea();
    private NArea pile_area = new NArea ();
    private AtomicBoolean m_selection_start = new AtomicBoolean ( false );
}
