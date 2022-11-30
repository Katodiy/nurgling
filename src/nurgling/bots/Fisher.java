package nurgling.bots;

import haven.Button;
import haven.CheckBox;
import haven.Coord;
import haven.Widget;
import nurgling.NGameUI;
import nurgling.bots.actions.Fishing;
import nurgling.tools.AreaSelecter;
import nurgling.tools.NArea;

import java.util.concurrent.atomic.AtomicBoolean;

public class Fisher extends Bot{
    
    
    public Fisher(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Fisher";
        win_sz.y = 150;
        
        
        ///Добавление цикла в действия бота
        runActions.add ( new Fishing(  bait_area, tools_area , pile_area, fish_area, dropper, disable_dropper) );
    }
    
    
    @Override
    public void initAction ()
            throws InterruptedException { super.initAction();
        int y = 0;
        window.add ( new Button ( window.buttons_size, "Инструменты" ) {
            @Override
            public void click () {

                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter( gameUI, _tools_b, m_selection_start, tools_area ),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y  ) );
        y+=25;
        window.add ( new Button ( window.buttons_size, "Наживка" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter ( gameUI, _bait_b, m_selection_start, bait_area ),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        y+=25;
        window.add ( new Button ( window.buttons_size, "Место рыбалки" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter ( gameUI, _fish_b, m_selection_start, fish_area ),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );
        y+=25;
        Widget prev = window.add (new Button ( window.buttons_size, "Пайлы" ) {
            @Override
            public void click () {
                gameUI.getMap ().isAreaSelectorEnable = true;
                if ( !m_selection_start.get () ) {
                    m_selection_start.set ( true );
                    new Thread ( new AreaSelecter ( gameUI, _pile_b, m_selection_start, pile_area ),
                            "Cont Area Selecter" ).start ();
                }
            }
        }, new Coord ( 0, y ) );

        prev = window.add(new CheckBox("Отключить сброс"){
            {
                a = disable_dropper.get();
            }

            public void set(boolean val) {
                disable_dropper.set(val);
                a = val;
            }
        }, prev.c.add(0,40));
//        while ( !_bait_b.get ()|| !_pile_b.get ()|| !_fish_b.get ()) {
        while ( !_bait_b.get () || !_pile_b.get () || !_tools_b.get () || !_fish_b.get ()) {
            Thread.sleep ( 100 );
        }
    }
    
    @Override
    public void endAction () {
        _bait_b.set ( false );
        _pile_b.set ( false );
        _tools_b.set ( false );
        _fish_b.set ( false );
        m_selection_start.set ( false );
        if(dropper!=null && dropper.isAlive ()){
            try {
                dropper.interrupt ();
                dropper.join ();
            }
            catch ( InterruptedException e ) {
                e.printStackTrace ();
            }
        }
        super.endAction ();
    }
    Thread dropper;
    private AtomicBoolean _bait_b = new AtomicBoolean ( false );
    private AtomicBoolean _pile_b = new AtomicBoolean ( false );
    private AtomicBoolean _tools_b = new AtomicBoolean ( false );
    private AtomicBoolean _fish_b = new AtomicBoolean ( false );
    private NArea bait_area = new NArea();
    private NArea pile_area = new NArea ();
    private NArea fish_area = new NArea ();
    private NArea tools_area = new NArea ();
    private AtomicBoolean m_selection_start = new AtomicBoolean ( false );

    private AtomicBoolean disable_dropper = new AtomicBoolean ( false );
}
