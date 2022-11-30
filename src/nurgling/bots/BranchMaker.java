package nurgling.bots;

import haven.Button;
import haven.Coord;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.bots.actions.*;
import nurgling.tools.AreaSelecter;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;


public class BranchMaker extends Bot {

    
    public BranchMaker(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "BranchMaker";
        win_sz.y = 100;
        runActions.add(new BramchMakerAction(tree_area, output_area));
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
                    new Thread ( new AreaSelecter ( gameUI, _start, m_selection_start, tree_area ),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord (0,y) );
        y+=25;
        window.add ( new Button ( window.buttons_size, "Веточки" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter( gameUI, _output, m_selection_start, output_area ),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord (0,y) );
        while ( !_start.get () || !_output.get ()) {
            Thread.sleep ( 100 );
        }
        
    }
    
    @Override
    public void endAction () {
        _start.set ( false );
        _output.set ( false );
        m_selection_start.set ( false );
        super.endAction ();
    }
    
    private AtomicBoolean _start = new AtomicBoolean ( false );
    private NArea tree_area = new NArea ();
    private AtomicBoolean _output = new AtomicBoolean ( false );
    private NArea output_area = new NArea();
    private AtomicBoolean m_selection_start = new AtomicBoolean ( false );
    
}
