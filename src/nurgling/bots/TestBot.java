package nurgling.bots;

import haven.*;

import haven.res.lib.itemtex.ItemTex;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.tools.Finder;

import static haven.MCache.tilesz;


public class TestBot extends Bot {
    boolean m_start = false;
    
    /**
     * Базовый класс ботов
     *
     * @param gameUI Интерфейс клиента
     */
    public TestBot(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Get Info";
        m_start = false;
        win_sz.y = 100;
        //        runActions.add(new SelectFlowerAction(new NAlias(), "Chop", SelectFlowerAction.Types.Gob));
        //        runActions.add(new Destroy(new NAlias("stump")));
    }
    
    @Override
    public void runAction ()
            throws InterruptedException {
        while ( !m_start ) {
            Thread.sleep ( 100 );
        }
        try {
            gameUI.getInventory ().getItem ( new NAlias( "lemon" ) );




            Gob pl = gameUI.map.player ();

            Gob gob1 = Finder.findNearestObject ();
            //            PathFinder pf = new PathFinder ( gameUI,gob1.rc );
            //
            //            pf.run ();
            //            pf.map.print();
            /// Выбрать все объекты
            gameUI.getMap ().isAreaSelectorEnable = true;
            while ( gameUI.getMap ().isAreaSelectorEnable ) {
                Thread.sleep ( 100 );
            }
//            gameUI.msg ( String.valueOf ( gameUI.getInventory ().getNumberFreeCoord ( new Coord ( 4, 1 ) ) ) );
            Coord2d min = gameUI.getMap ().getSelection ().begin;
            Coord2d max = gameUI.getMap ().getSelection ().end;
            double x = min.x;
            double y = min.y;
            while (x < max.x){
                while(y<max.y){
                    Coord pltc_beg =
                            ( new Coord2d ( ( x ) / 11, ( y ) / 11 ) ).floor ();
                    try {
                        System.out.printf ( "++++" );
                        Resource res_beg = gameUI.ui.sess.glob.map.tilesetr ( gameUI.ui.sess.glob.map.gettile ( pltc_beg ) );
                        System.out.printf ( res_beg.name );
                    }catch ( Exception e ){
                    
                    }
                    y += tilesz.y;
                }
                x+= tilesz.x;
                y = min.y;
            }
            
            for(WItem item: gameUI.getInventory ().getItems ()){
                if(NUtils.isIt(item, "bronzesword")){
                    int a = 8;
                }
                System.out.println (item.item.getres ());
            }
            synchronized ( gameUI.ui.sess.glob.oc ) {
                for ( Gob gob : gameUI.ui.sess.glob.oc ) {
                    if ( gob != null ) {
                        if ( ( gob.rc.x > min.x && gob.rc.x < max.x ) && ( gob.rc.y > min.y && gob.rc.y < max.y ) ) {
                            if ( gob.getres () != null ) {
                                Resource res = gob.getres ();
                                //                                ArrayList<Gob.Overlay> core = (ArrayList<Gob.Overlay>)gob.ols;
                                //                                for(Gob.Overlay ov : core){
                                //                                    Resource res_ov = ov.res.get ();
                                //                                    if(res_ov!=null)
                                //                                        System.out.println ( res_ov.name);
                                //                                }
                                //                                System.out.println ( res );
                                System.out.println ( gob.rc );
                                
                                //                                Coord sfcoord = gob.rc.div ( MCache.tilesz ).floor ();
                                //                                Area calibr =  Finder.findCalibrationArea ();
                                //                                System.out.println (isGobInArea ( calibr ));
                                //                                Coord2d trel_pos = new Coord2d ( ( sfcoord ).x * tilesz.x, ( sfcoord ).y * tilesz.y );
                                //                                System.out.println ( trel_pos);
                                for ( Gob.Overlay ol : gob.ols ) {
                                    if ( ol.res != null ) {
                                        Resource olres = ol.res.get ();
                                        System.out.println ( olres.name );
                                    }
                                }
                                

                            }
                        }
                    }
                    
                }
            }
        }
        catch ( Exception e ) {
        
        }
        
        endAction ();
        
        
    }
    
    @Override
    public void initAction ()
            throws InterruptedException {
        super.initAction ();
        /// Смещение компонентов по вертикали
        int y = 0;
        /// Чекбокс использования лопаты
        
        /// Кнопка запуска основного цикла работы кота
        window.add ( new Button ( window.buttons_size, "Запуск" ) {
            @Override
            public void click () {
                m_start = true;
                hide ();
            }
        }, new Coord ( 0, y += 25 ) );
    }
    
    @Override
    public void endAction () {
        super.endAction ();
    }
}
