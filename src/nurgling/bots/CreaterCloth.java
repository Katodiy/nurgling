package nurgling.bots;

import haven.Button;
import haven.Coord;
import haven.UI;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.bots.actions.Action;
import nurgling.bots.actions.CraftCloth;
import nurgling.bots.actions.CraftRope;
import nurgling.bots.actions.Loop;
import nurgling.tools.AreaSelecter;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;


public class CreaterCloth extends Bot {


    public CreaterCloth(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Crafter Cloth";
        win_sz.y = UI.scale(100);

        
       ArrayList<Action> loop = new ArrayList<> ();
       loop.add ( new CraftCloth(pile_area,out_area) );
        
        runActions.add ( new Loop( loop,
                () -> !Finder.findObjectsInArea ( new NAlias( "stockpile" ), pile_area ).isEmpty () ) );
    }
    
    
    @Override
    public void initAction ()
            throws InterruptedException { super.initAction();
        int y = 0;
        window.add ( new Button ( window.buttons_size, "Output piles" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter( gameUI, _out_zone, m_selection_start, out_area ),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );

        y += UI.scale(35);
        window.add ( new Button ( window.buttons_size, "Fibres" ) {
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
        while ( !_zone.get () ||  !_out_zone.get () ) {
            Thread.sleep ( 100 );
        }
    }
    
    @Override
    public void endAction () {
        _zone.set ( false );
        _out_zone.set ( false );
        m_selection_start.set ( false );
        super.endAction ();
    }
    
    private AtomicBoolean _zone = new AtomicBoolean ( false );
    private NArea pile_area = new NArea();
    private AtomicBoolean m_selection_start = new AtomicBoolean ( false );
    
    private AtomicBoolean _out_zone = new AtomicBoolean ( false );
    private NArea out_area = new NArea ();
}
