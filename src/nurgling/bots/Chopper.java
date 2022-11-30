package nurgling.bots;

import haven.Button;

import haven.CheckBox;
import haven.Widget;
import nurgling.NConfiguration;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.bots.actions.ChopperAction;
import nurgling.tools.AreaSelecter;
import nurgling.tools.NArea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;


public class Chopper extends Bot {
    
    public static ArrayList<String> lumber_tools = new ArrayList<String> (
            Arrays.asList ( "woodsmansaxe", "axe-m", "stoneaxe" ) );
    public static ArrayList<String> shovel_tools = new ArrayList<String> ( Arrays.asList ( "shovel-m", "shovel-w" ) );
    
    public Chopper(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Chopper";
        win_sz.y = 100;
        
    }
    
    
    @Override
    public void initAction ()
            throws InterruptedException {
        super.initAction ();
        //        new Thread (new BattleServer ()).start ();
        Widget prev = window.add (new Button ( window.buttons_size, "Деревья" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter( gameUI, _start, m_selection_start, tree_area ),
                            "Cont Area Selecter" ).start ();
                }
            }
        } );
        prev = window.add(new CheckBox("Корчевать пни"){
            {
                a = stump_mod.get();
            }

            public void set(boolean val) {
                stump_mod.set(val);
                a = val;
            }
        }, prev.c.add(0,40));
        window.add(new CheckBox("Игнорировать поросль"){
            {
                a = no_kid_mod.get();
            }

            public void set(boolean val) {
                no_kid_mod.set(val);
                a = val;
            }
        }, prev.c.add(0,20));
        while ( !_start.get () ) {
            Thread.sleep ( 100 );
        }
        
    }
    
    @Override
    public void runAction ()
            throws InterruptedException {
        new BotThread ( NUtils.getGameUI (), new ChopperAction( tree_area , stump_mod, no_kid_mod) ).run ();
    }
    
    @Override
    public void endAction () {
        _start.set ( false );
        m_selection_start.set ( false );
        super.endAction ();
    }
    
    private AtomicBoolean _start = new AtomicBoolean ( false );
    private NArea tree_area = new NArea();
    private AtomicBoolean m_selection_start = new AtomicBoolean ( false );
    AtomicBoolean stump_mod = new AtomicBoolean(false);
    AtomicBoolean no_kid_mod = new AtomicBoolean(false);
}
