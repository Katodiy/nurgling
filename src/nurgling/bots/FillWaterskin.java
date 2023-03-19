package nurgling.bots;

import haven.Button;
import nurgling.NGameUI;
import nurgling.bots.actions.TanningFluidMake;
import nurgling.bots.actions.WaterSkinWaterIn;
import nurgling.tools.AreaSelecter;
import nurgling.tools.NArea;

import java.util.concurrent.atomic.AtomicBoolean;

public class FillWaterskin extends Bot {

    public FillWaterskin(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Fill Waterskins";
        win_sz.y = 100;
        runActions.add ( new WaterSkinWaterIn(area) );
        
    }

    @Override
    public void initAction ()
            throws InterruptedException { super.initAction();
        window.add ( new Button( window.buttons_size, "Barrel or cistern" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter( gameUI, _start, m_selection_start, area ),
                            "Cont Area Selecter" ).start ();
                }
            }
        } );
        while ( !_start.get () ) {
            Thread.sleep ( 100 );
        }
    }

    @Override
    public void endAction () {
        _start.set ( false );
        m_selection_start.set ( false );
        super.endAction ();
    }

    private AtomicBoolean _start = new AtomicBoolean ( false );
    private NArea area = new NArea ();
    private AtomicBoolean m_selection_start = new AtomicBoolean ( false );

}
