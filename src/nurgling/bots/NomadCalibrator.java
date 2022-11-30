package nurgling.bots;

import haven.Button;
import haven.Coord;
import haven.Coord2d;

import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.bots.actions.NomadCalibration;
import nurgling.tools.AreaSelecter;
import nurgling.tools.NArea;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;


public class NomadCalibrator extends Bot {
    boolean m_start = false;
    
    /**
     * Базовый класс ботов
     *
     * @param gameUI Интерфейс клиента
     */
    public NomadCalibrator(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Nomad Calibration";
        m_start = false;
        win_sz.y = 100;
        /// Кнопка запуска основного цикла работы кота
        startButton = new Button ( 120, "Запуск" ){
            @Override
            public void click () {
                _start.set ( true );
                b.set ( true );
                this.hide ();
                stopButton.show ();
            }
        };
        stopButton = new Button ( 120, "Стоп" ){
            @Override
            public void click () {
                b.set ( false );
                this.hide ();
                startButton.show ();
            }
        };
    }
    
    @Override
    public void runAction ()
            throws InterruptedException {
        runActions.add ( new NomadCalibration(area, b, coords) );
    }
    
    @Override
    public void initAction ()
            throws InterruptedException {
        /// Смещение компонентов по вертикали
        int y = 0;

        window.add ( startButton, new Coord ( 0, y ) );
        window.add ( stopButton, new Coord ( 0, y) );
        startButton.hide ();
        stopButton.hide ();
        y+=25;
        window.add ( new Button ( window.buttons_size, "Стартовая зона" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    
                    this.hide ();
                    new Thread ( new AreaSelecter( gameUI, _zone, m_selection_start, area ),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        while (!_zone.get ()) {
            Thread.sleep ( 100 );
        }
        startButton.show ();
        while ( !_start.get ()) {
            Thread.sleep ( 100 );
        }
    }
    
    @Override
    public void endAction () {
        String path = "./nomad.dat";

        DataOutputStream out = null;
        try {

            out = new DataOutputStream(new FileOutputStream(path ));
            for (Coord2d coord2d: coords) {
                out.writeInt((int) coord2d.x);
                out.writeInt((int) coord2d.y);
            }
            out.close();
            gameUI.msg("writePath:" + path);

        } catch (IOException e) {
            e.printStackTrace();
        }
        _start.set ( false );
        _zone.set ( false );
        if(startButton!=null)
            startButton.hide ();
        if(stopButton!=null)
            stopButton.hide ();
        coords.clear();
        super.endAction ();
    }

    ArrayList<Coord2d> coords = new ArrayList<>();
    AtomicBoolean m_selection_start = new AtomicBoolean (false);
    AtomicBoolean _zone = new AtomicBoolean (false);
    NArea area = new NArea();
    Button startButton = null;
    Button stopButton = null;
    private AtomicBoolean _start = new AtomicBoolean ( false );
    AtomicBoolean b = new AtomicBoolean ( false );
}
