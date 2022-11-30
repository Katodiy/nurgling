package nurgling.bots;

import haven.Area;
import haven.Button;
import haven.Gob;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.tools.AreaSelecter;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;


public class Calibration extends Bot {
    boolean m_start = false;
    
    /**
     * Базовый класс ботов
     *
     * @param gameUI Интерфейс клиента
     */
    public Calibration(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Calibration";
        m_start = false;
        win_sz.y = 100;
    }
    
    @Override
    public void runAction ()
            throws InterruptedException {
        
        

                ArrayList<Gob> gobs = Finder.findObjectsInArea ( new NAlias( "iconsign" ), tree_area );
                DataOutputStream out = null;
                try {
                    out = new DataOutputStream ( new FileOutputStream ( "./calibr.dat" ) );
                    
                    for ( Gob gob : gobs ) {
                        out.writeLong ( gob.getModelAttribute() );
                    }
                    out.close ();
                    
                    AreasID.init ();
//                    DataInputStream in = new DataInputStream ( new FileInputStream ( path + "/calibr.dat" ) );
//                    try {
//                        while ( true ) {
//                            System.out.println ( in.readInt () );
//                        }
//                    }
//                    catch ( EOFException ignored ) {
//                        System.out.println ( "[EOF]" );
//                    }
//                    in.close ();
                }
                catch ( IOException e ) {
                    e.printStackTrace ();
                }

        endAction ();
        
        
    }
    
    @Override
    public void initAction ()
            throws InterruptedException { super.initAction();
        window.add ( new Button ( window.buttons_size, "Объекты" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter( gameUI, _start, m_selection_start, tree_area ), "Area Selecter" )
                            .start ();
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
    private NArea tree_area = new NArea ();
    private AtomicBoolean m_selection_start = new AtomicBoolean ( false );
}
