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


public class BuildWChest extends Bot {
    
    
    public BuildWChest ( NGameUI gameUI ) {
        super ( gameUI );
        win_title = "BuildWChest";
        win_sz.y = 100;
        
        CraftCommand command = new CraftCommand ();
        command.command = new char[]{ 'b', 'c'};
        command.special_command = new NAlias("bld/chest");
        command.name = "Wooden Chest";
        command.ingredients = new ArrayList<Ingredient> ();
        Ingredient board = new Ingredient();
        board.item = new NAlias( "board" );
        board.isGroup = true;
        command.ingredients.add (board);
        command.spec_in_area.put(board,board_area);
        command.ing_count.put(board,4);
        Ingredient nugget = new Ingredient();
        nugget.item = new NAlias( "nugget" );
        nugget.isGroup = true;
        command.ingredients.add (nugget);
        command.spec_in_area.put(nugget,nugget_area);
        command.ing_count.put(nugget,4);

        ///Добавление цикла в действия бота
        runActions.add ( new Build( build_area, "chest", command ) );
    }
    
    
    @Override
    public void initAction ()
            throws InterruptedException {
        super.initAction ();
        int y = 0;
        window.add ( new Button ( window.buttons_size, "Building area" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable.set(true);
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
                gameUI.getMap ().isAreaSelectorEnable.set(true);
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter ( gameUI, _zone1, m_selection_start, board_area ),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        y += 25;
        window.add ( new Button ( window.buttons_size, "Nuggets" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable.set(true);
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter ( gameUI, _zone2, m_selection_start, nugget_area),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        y += 25;
        
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
    private NArea build_area = new NArea ();
    private NArea board_area = new NArea ();
    private NArea nugget_area = new NArea ();
    private AtomicBoolean m_selection_start = new AtomicBoolean ( false );
}
