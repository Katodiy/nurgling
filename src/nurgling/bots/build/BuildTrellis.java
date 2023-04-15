package nurgling.bots.build;

import haven.Button;
import haven.Coord;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.bots.Bot;
import nurgling.bots.actions.Build;
import nurgling.bots.tools.CraftCommand;
import nurgling.bots.tools.Ingredient;
import nurgling.tools.AreaSelecter;
import nurgling.tools.NArea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;


public class BuildTrellis extends Bot {
    
    
    public BuildTrellis ( NGameUI gameUI ) {
        super ( gameUI );
        win_title = "BuildTrell";
        win_sz.y = 100;
        
        CraftCommand command = new CraftCommand ();
        command.command = new char[]{ 'b', 'b', 't' };
        command.name = "Trellis";
        command.ingredients = new ArrayList<Ingredient> ();
        Ingredient block = new Ingredient();
        block.item = new NAlias( "block" );
        block.isGroup = true;
        command.ingredients.add (block);
        command.spec_in_area.put(block,block_area);
        command.ing_count.put(block,3);
        Ingredient string = new Ingredient();
        string.item = new NAlias ( new ArrayList<String> ( Arrays.asList ( "fibre", "taproot","nettle","toughroot", "hidestrap" ) ));
        string.isGroup = true;
        command.ingredients.add (string);
        command.spec_in_area.put(string,fibre_area);
        command.ing_count.put(string,1);
        ///Добавление цикла в действия бота
        runActions.add ( new Build( build_area, "trellis", command ) );
    }
    
    
    @Override
    public void initAction ()
            throws InterruptedException {
        super.initAction ();
        int y = 0;
        window.add ( new Button ( window.buttons_size, "Building area" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter( gameUI, _start, m_selection_start, build_area ),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        y += 25;
        window.add ( new Button ( window.buttons_size, "Logs/blocks" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter ( gameUI, _zone1, m_selection_start, block_area ),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        y += 25;
        window.add ( new Button ( window.buttons_size, "Strings" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter ( gameUI, _zone2, m_selection_start, fibre_area ),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        
        while ( !_start.get () || !_zone1.get () || !_zone2.get () ) {
            Thread.sleep ( 100 );
        }
    }
    
    @Override
    public void endAction () {
        _start.set ( false );
        _zone1.set ( false );
        _zone2.set ( false );
        m_selection_start.set ( false );
        super.endAction ();
    }
    
    private AtomicBoolean _start = new AtomicBoolean ( false );
    private AtomicBoolean _zone1 = new AtomicBoolean ( false );
    private AtomicBoolean _zone2 = new AtomicBoolean ( false );
    private NArea build_area = new NArea();
    private NArea block_area = new NArea ();
    private NArea fibre_area = new NArea ();
    private AtomicBoolean m_selection_start = new AtomicBoolean ( false );
}
