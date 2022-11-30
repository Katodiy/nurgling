package nurgling.bots;

import haven.Button;
import haven.Coord;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.bots.actions.Action;
import nurgling.bots.actions.Equip;
import nurgling.bots.actions.TarKilnAction;
import nurgling.tools.AreaSelecter;
import nurgling.tools.NArea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class TarKilnRefiller extends Bot {
    public static ArrayList<String> lumber_tools = new ArrayList<String> (
            Arrays.asList ( "woodsmansaxe", "axe-m", "stoneaxe" ) );
    
    public TarKilnRefiller(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "TarKiln Re";
        win_sz.y = 200;
        ArrayList<Action> smokedLoop = new ArrayList<> ();
        
        runActions.add ( new Equip( new NAlias( lumber_tools ) ) );
        runActions.add ( new TarKilnAction( block_area,coal_area ) );
        
        
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
                    new Thread ( new AreaSelecter ( gameUI, _blocks, m_selection_start, block_area ),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        y+=25;
        window.add ( new Button ( window.buttons_size, "Уголь" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter( gameUI, _coal, m_selection_start, coal_area ),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        while ( !_blocks.get () || !_coal.get ()) {
            Thread.sleep ( 100 );
        }
    }
    
    @Override
    public void endAction () {
        _blocks.set ( false );
        
        super.endAction ();
    }
    
    NArea block_area = new NArea ();
    private AtomicBoolean _blocks = new AtomicBoolean ( false );
    NArea coal_area = new NArea();
    private AtomicBoolean _coal = new AtomicBoolean ( false );
    private AtomicBoolean m_selection_start = new AtomicBoolean ( false );
}
