package nurgling.bots;

import haven.Button;
import haven.Coord;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.bots.actions.*;
import nurgling.tools.AreaSelecter;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;


public class TarSticker extends Bot {

    
    public TarSticker(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "TarSticker";
        win_sz.y = 100;
        runActions.add(new TarStickAction(tree_area,barrel_area,output_area));
    }
    
    
    
    @Override
    public void initAction ()
            throws InterruptedException {
        super.initAction ();
        int y = 0;
        window.add ( new Button ( window.buttons_size, "Blocks/Logs" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter( gameUI, _start, m_selection_start, tree_area ),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord (0,y) );
        y+=25;
        window.add ( new Button ( window.buttons_size, "Output" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter ( gameUI, _output, m_selection_start, output_area ),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord (0,y) );
        y+=25;
        window.add ( new Button ( window.buttons_size, "Barrels with tar" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter ( gameUI, _barrel, m_selection_start, barrel_area ),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord (0,y) );
        while ( !_start.get () || !_output.get ()|| !_barrel.get ()) {
            Thread.sleep ( 100 );
        }
        
    }
    
    @Override
    public void endAction () {
        _start.set ( false );
        _output.set ( false );
        _barrel.set ( false );
        m_selection_start.set ( false );
        super.endAction ();
    }
    
    private AtomicBoolean _start = new AtomicBoolean ( false );
    private NArea tree_area = new NArea ();
    private AtomicBoolean _output = new AtomicBoolean ( false );
    private NArea output_area = new NArea ();
    private AtomicBoolean _barrel = new AtomicBoolean ( false );
    private NArea barrel_area = new NArea ();
    private AtomicBoolean m_selection_start = new AtomicBoolean ( false );


}
