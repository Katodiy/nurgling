package nurgling.bots;

import nurgling.Dropper;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.bots.actions.MineAction;
import nurgling.tools.AreaSelecter;
import nurgling.tools.NArea;

import java.util.concurrent.atomic.AtomicBoolean;

public class MineBot extends Bot {
    
    public MineBot(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Miner";
        win_sz.y = 100;

        runActions.add ( new MineAction(mine_area) );
    }
    
    
    @Override
    public void initAction ()
            throws InterruptedException {
        super.initAction ();
        gameUI.getMap ().isAreaSelectorEnable = true;
        if ( !m_selection_start.get () ) {
            m_selection_start.set ( true );
            new Thread ( new AreaSelecter( gameUI, _start, m_selection_start, mine_area ),
                    "Cont Area Selecter" ).start ();
        }
        while ( !_start.get () ) {
            Thread.sleep ( 100 );
        }
        _dropper = new Dropper( gameUI, new NAlias("axe","sword","shield","saw") );
        dropper = new Thread ( _dropper );
        dropper.start ();
    }
    
    @Override
    public void endAction () {
        _start.set (false);
        m_selection_start.set ( false );
        if(dropper.isAlive ()){
            _dropper.isAlive.set ( false );
            try {
                dropper.join ();
            }
            catch ( InterruptedException e ) {
            }
        }
        super.endAction ();
    }
    
    private AtomicBoolean _start = new AtomicBoolean ( false );
    private NArea mine_area = new NArea();
    private AtomicBoolean m_selection_start = new AtomicBoolean ( false );
    Dropper _dropper;
    Thread dropper;
}
