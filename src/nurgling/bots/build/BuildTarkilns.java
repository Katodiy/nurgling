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


public class BuildTarkilns extends Bot {


    public BuildTarkilns(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "BuildTarkilns";
        win_sz.y = 100;
        
        CraftCommand command = new CraftCommand();
        command.command = new char[]{ 'b', 'b', 'f','t' };
        command.name = "Tar Kiln";
        Ingredient stone = new Ingredient();
        stone.item = new NAlias ( "stone");
        stone.isGroup = true;
        command.ingredients.add (stone);
        command.spec_in_area.put(stone,stone_area);
        command.ing_count.put(stone,35);
        Ingredient clay = new Ingredient();
        clay.item = new NAlias ( "clay");
        clay.isGroup = true;
        command.ingredients.add (clay);
        command.spec_in_area.put(clay,clay_area);
        command.ing_count.put(clay,50);


        ///Добавление цикла в действия бота
        runActions.add ( new Build( build_area, "tarkiln", command ) );
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
        window.add ( new Button ( window.buttons_size, "Clay or clay area" ) {
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
        window.add ( new Button ( window.buttons_size, "Stone piles or boulders" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter ( gameUI, _zone2, m_selection_start, stone_area),
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
    private NArea clay_area = new NArea ();
    private NArea stone_area = new NArea ();
    private AtomicBoolean m_selection_start = new AtomicBoolean ( false );
}
