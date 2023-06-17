package nurgling.bots;

import haven.Button;
import haven.Coord;
import haven.UI;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.bots.actions.AllToPiles;
import nurgling.tools.AreaSelecter;
import nurgling.tools.GobSelector;
import nurgling.tools.NArea;

import java.util.concurrent.atomic.AtomicBoolean;


public class AllToPile extends Bot {

    public AllToPile(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Collect items to pile";
        win_sz.y = UI.scale(100);

        runActions.add ( new AllToPiles(pile_area,intput_area,items));
    }
    
    
    @Override
    public void initAction ()
            throws InterruptedException { super.initAction();
        int y = 0;
        window.add ( new Button ( window.buttons_size, "Select Item" ) {
            @Override
            public void click () {
                if ( !m_selection_start.get () ) {
                    items.keys.clear();
                    gameUI.getMap().isGobSelectorEnable.set(true);
                    new Thread ( new GobSelector( _gob_found, m_selection_start, items ),
                            "Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y  ) );
        y+=UI.scale(25);
        window.add ( new Button ( window.buttons_size, "Area of items" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter ( gameUI, _zone2, m_selection_start, intput_area ),
                            "Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        y+=UI.scale(25);
        window.add ( new Button ( window.buttons_size, "Output piles" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter ( gameUI, _zone, m_selection_start, pile_area ),
                            "Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        while (  !_gob_found.get () ) {
            Thread.sleep ( 100 );
        }

        while (  !_zone.get () || !_zone2.get () ) {
            Thread.sleep ( 100 );
        }
    }
    
    @Override
    public void endAction () {
        items.keys.clear();
        _zone.set ( false );
        _zone2.set ( false );
        _gob_found.set ( false );
        m_selection_start.set ( false );
        super.endAction ();
    }
    NAlias items = new NAlias();
    private AtomicBoolean _zone = new AtomicBoolean ( false );
    private NArea pile_area = new NArea ();
    private AtomicBoolean m_selection_start = new AtomicBoolean ( false );

    private AtomicBoolean _zone2 = new AtomicBoolean ( false );
    private AtomicBoolean _gob_found = new AtomicBoolean ( false );
    private NArea intput_area = new NArea ();


}
