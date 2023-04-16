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


public class BuildSShed extends Bot {


    public BuildSShed(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "BuildSShed";
        win_sz.y = 170;
        
        CraftCommand command = new CraftCommand();
        command.command = new char[]{ 'b', 'b', 'f', 'm' };
        command.name = "Smoke Shed";
        command.ingredients = new ArrayList<Ingredient> ();
        Ingredient board = new Ingredient();
        board.item = new NAlias ( "board");
        board.isGroup = true;
        command.ingredients.add (board);
        command.spec_in_area.put(board,board_area);
        command.ing_count.put(board,12);
        Ingredient brick = new Ingredient();
        brick.item = new NAlias ( "brick");
        brick.isGroup = true;
        command.ingredients.add (brick);
        command.spec_in_area.put(brick,brick_area);
        command.ing_count.put(brick,10);
        Ingredient bough = new Ingredient();
        bough.item = new NAlias ( "bough");
        bough.isGroup = true;
        command.ingredients.add (bough);
        command.spec_in_area.put(bough,bough_area);
        command.ing_count.put(bough,6);
        Ingredient block = new Ingredient();
        block.item = new NAlias ( "block");
        block.isGroup = true;
        command.ingredients.add (block);
        command.spec_in_area.put(block,block_area);
        command.ing_count.put(block,4);

        runActions.add ( new Build( build_area, "smokeshed", command ) );
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
        window.add ( new Button ( window.buttons_size, "Boards/logs" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter ( gameUI, _zone1, m_selection_start, board_area ),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        y += 25;
        window.add ( new Button ( window.buttons_size, "Blocks/logs" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter ( gameUI, _zone2, m_selection_start, block_area ),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        y += 25;
        window.add ( new Button ( window.buttons_size, "Bricks" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter ( gameUI, _zone3, m_selection_start, brick_area),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        y += 25;
        window.add ( new Button ( window.buttons_size, "Boughs" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter ( gameUI, _zone4, m_selection_start, bough_area),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        
        while ( !_start.get () || !_zone1.get () || !_zone2.get () || !_zone3.get () || !_zone4.get () ) {
            Thread.sleep ( 100 );
        }
    }
    
    @Override
    public void endAction () {
        _start.set ( false );
        _zone1.set ( false );
        _zone2.set ( false );
        _zone3.set ( false );
        _zone4.set ( false );
        m_selection_start.set ( false );
        super.endAction ();
    }
    
    private AtomicBoolean _start = new AtomicBoolean ( false );
    private AtomicBoolean _zone1 = new AtomicBoolean ( false );
    private AtomicBoolean _zone2 = new AtomicBoolean ( false );
    private AtomicBoolean _zone3 = new AtomicBoolean ( false );
    private AtomicBoolean _zone4 = new AtomicBoolean ( false );
    private NArea build_area = new NArea ();
    private NArea board_area = new NArea ();
    private NArea block_area = new NArea ();
    private NArea brick_area = new NArea ();
    private NArea bough_area = new NArea ();
    private AtomicBoolean m_selection_start = new AtomicBoolean ( false );
}
