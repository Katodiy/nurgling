package nurgling.tools;

import haven.*;
import haven.res.lib.itemtex.ItemTex;
import nurgling.*;
import nurgling.NExceptions.NoFreeSpace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;


public class Finder {



    public static ArrayList<Gob> findObjects(NAlias name, Object o){
        ArrayList<Gob> outarray = new ArrayList<>();

        if(o!=null) {
            if(name!=null) {
                outarray = Finder.findObjectsInArea(name, o instanceof AreasID ? Finder.findNearestMark((AreasID)o):(NArea)o);
            }else {
                if (o instanceof AreasID) {
                    NUtils.ContainerProp prop = NUtils.getContainerType(((AreasID) o));
                    outarray = Finder.findObjectsInArea(prop.name, Finder.findNearestMark((AreasID) o));
                } else if (o instanceof NArea) {
                    NUtils.ContainerProp prop = NUtils.getContainerType(((NArea) o));
                    outarray = Finder.findObjectsInArea(prop.name, (NArea) o);
                }
            }
        }else{
            outarray = Finder.findObjects ( name );
        }
        return outarray;
    }
    
    /**
     * Поиск ближайшего объекта
     *
     * @param name Имя объекта
     * @return Объект
     */
    public static Gob findObject (
            NAlias name

    ) {
        double distance = 10000;
        /// Расстояние до объекта с "запасом"
        Gob result = null;
        synchronized ( NUtils.getGameUI().ui.sess.glob.oc ) {
            for ( Gob gob : NUtils.getGameUI().ui.sess.glob.oc ) {
                
                if ( NUtils.isIt ( gob, name ) ) {
                    /// Сравнивается расстояние между игроком и объектом
                    double dist = NUtils.getGameUI().map.player ().rc.dist ( gob.rc );
                    /// Если расстояние минимально то оно и объект запоминаются
                    if ( dist < distance ) {
                        distance = dist;
                        result = gob;
                    }
                }
            }
        }
        return result;
    }
    
    public static Gob findObjectWithCoontent (
            NAlias name,
            NAlias content,
            double distance
    ) {
        Gob result = null;
        synchronized ( NUtils.getGameUI().ui.sess.glob.oc ) {
            for ( Gob gob : NUtils.getGameUI().ui.sess.glob.oc ) {
                
                if ( NUtils.isIt ( gob, name ) ) {
                    /// Сравнивается расстояние между игроком и объектом
                    double dist = NUtils.getGameUI().map.player ().rc.dist ( gob.rc );
                    /// Если расстояние минимально то оно и объект запоминаются
                    if ( dist < distance ) {
                        if ( NUtils.isOverlay ( gob, content ) ) {
                            distance = dist;
                            result = gob;
                        }
                    }
                }
            }
        }
//        if ( result != null ) {
//            if ( result.getres () != null ) {
//                System.out.println ( result.getres ().name );
//            }
//        }
        return result;
    }
    
    public static Gob findObjectWithCoontent (
            NAlias name,
            NAlias content,
            Coord2d coord2d,
            double distance
    ) {
        Gob result = null;
        synchronized ( NUtils.getGameUI().ui.sess.glob.oc ) {
            for ( Gob gob : NUtils.getGameUI().ui.sess.glob.oc ) {
                
                if ( NUtils.isIt ( gob, name ) ) {
                    /// Сравнивается расстояние между игроком и объектом
                    double dist = coord2d.dist ( gob.rc );
                    /// Если расстояние минимально то оно и объект запоминаются
                    if ( dist < distance ) {
                        if ( NUtils.isOverlay ( gob, content ) ) {
                            distance = dist;
                            result = gob;
                        }
                    }
                }
            }
        }
//        if ( result != null ) {
//            if ( result.getres () != null ) {
//                System.out.println ( result.getres ().name );
//            }
//        }
        return result;
    }
    
    
    public static Gob findObjectInArea (
            NAlias name,
            double distance,
            NArea area
    ) {
        /// Расстояние до объекта с "запасом"
        Gob result = null;
        if(area!=null) {
            synchronized ( NUtils.getGameUI().ui.sess.glob.oc ) {
                for ( Gob gob : NUtils.getGameUI().ui.sess.glob.oc ) {
            
                    if ( NUtils.isGobInArea ( gob, area ) ) {
                        if ( NUtils.isIt ( gob, name ) ) {
                            /// Сравнивается расстояние между игроком и объектом
                            double dist = NUtils.getGameUI().map.player ().rc.dist ( gob.rc );
                            /// Если расстояние минимально то оно и объект запоминаются
                            if ( dist < distance ) {
                                distance = dist;
                                result = gob;
                            }
                        }
                    }
                }
            }
//            if ( result != null ) {
//                if ( result.getres () != null ) {
//                    System.out.println ( result.getres ().name );
//                }
//            }
        }
        return result;
    }
    
    public static Gob findCropInArea (
            NAlias name,
            double distance,
            NArea area,
            boolean maxStage
    ) {
        /// Расстояние до объекта с "запасом"
        Gob result = null;
        synchronized ( NUtils.getGameUI().ui.sess.glob.oc ) {
            for ( Gob gob : NUtils.getGameUI().ui.sess.glob.oc ) {
                
                if ( NUtils.isGobInArea ( gob, area ) ) {
                    if ( NUtils.isIt ( gob, name ) ) {
                        
                        if ( ( maxStage && NUtils.isCropstgmaxval ( gob ) ) ||
                                ( !maxStage && NUtils.isSpecialStageCrop( gob ) ) ) {
                            /// Сравнивается расстояние между игроком и объектом
                            double dist = NUtils.getGameUI().map.player ().rc.dist ( gob.rc );
                            /// Если расстояние минимально то оно и объект запоминаются
                            if ( dist < distance ) {
                                distance = dist;
                                result = gob;
                            }
                        }
                        
                    }
                }
            }
        }
//        if ( result != null ) {
//            if ( result.getres () != null ) {
//                System.out.println ( result.getres ().name );
//            }
//        }
        return result;
    }
    
    public static ArrayList<Gob> findCropsInArea (
            NAlias name,
            NArea area,
            boolean maxStage
    ) {
        /// Расстояние до объекта с "запасом"
        ArrayList<Gob> result = new ArrayList<> ();
        synchronized ( NUtils.getGameUI().ui.sess.glob.oc ) {
            for ( Gob gob : NUtils.getGameUI().ui.sess.glob.oc ) {
    
                if ( NUtils.isGobInArea ( gob, area ) && gob != NUtils.getGameUI().map.player () ) {
                    if ( NUtils.isIt ( gob, name ) ) {
                        if ( ( maxStage && NUtils.isCropstgmaxval ( gob ) ) ||
                                ( !maxStage && NUtils.isSpecialStageCrop( gob ) ) ) {
                            result.add ( gob );
                        }
                    }
                }
            }
        }
        
        result.sort ( new Comparator<Gob> () {
            @Override
            public int compare (
                    Gob lhs,
                    Gob rhs
            ) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return ( lhs.rc.x > rhs.rc.x ) ? -1 : ( ( lhs.rc.x < rhs.rc.x ) ? 1 : ( lhs.rc.y > rhs.rc.y ) ? -1 : (
                        lhs.rc.y < rhs.rc.y ) ? 1 : 0 );
            }
        } );
        return result;
    }
    
    public static ArrayList<Gob> findObjectsInArea (
            NAlias name,
            NArea area
    ) {
        /// Расстояние до объекта с "запасом"
        ArrayList<Gob> result = new ArrayList<> ();
        if(area!=null) {
            synchronized ( NUtils.getGameUI().ui.sess.glob.oc ) {
                for ( Gob gob : NUtils.getGameUI().ui.sess.glob.oc ) {
                    if ( NUtils.isGobInArea ( gob, area ) && gob != NUtils.getGameUI().map.player () ) {
                        if ( NUtils.isIt ( gob, name ) ) {
                            result.add ( gob );
                        }
                    }
                }
            }
    
            result.sort ( new Comparator<Gob> () {
                @Override
                public int compare (
                        Gob lhs,
                        Gob rhs
                ) {
                    // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                    return ( lhs.rc.x > rhs.rc.x ) ? -1 : ( ( lhs.rc.x < rhs.rc.x ) ? 1 : ( lhs.rc.y > rhs.rc.y ) ? -1 : (
                            lhs.rc.y < rhs.rc.y ) ? 1 : 0 );
                }
            } );
        }
        return result;
    }

    public static ArrayList<Gob> findObjects (
            NAlias name
    ) {
        /// Расстояние до объекта с "запасом"
        ArrayList<Gob> result = new ArrayList<> ();
        synchronized ( NUtils.getGameUI().ui.sess.glob.oc ) {
            for ( Gob gob : NUtils.getGameUI().ui.sess.glob.oc ) {
                
                if ( NUtils.isIt ( gob, name ) ) {
                    result.add ( gob );
                }
            }
        }
        
        result.sort ( new Comparator<Gob> () {
            @Override
            public int compare (
                    Gob lhs,
                    Gob rhs
            ) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return ( lhs.rc.x > rhs.rc.x ) ? -1 : ( ( lhs.rc.x < rhs.rc.x ) ? 1 : ( lhs.rc.y > rhs.rc.y ) ? -1 : (
                        lhs.rc.y < rhs.rc.y ) ? 1 : 0 );
            }
        } );
        return result;
    }
    
    public static ArrayList<Gob> findObjectsInArea (
            NArea area
    ) {
        /// Расстояние до объекта с "запасом"
        ArrayList<Gob> result = new ArrayList<> ();
        if(area!=null) {
            synchronized ( NUtils.getGameUI().ui.sess.glob.oc ) {
                for ( Gob gob : NUtils.getGameUI().ui.sess.glob.oc ) {
            
                    if ( NUtils.isGobInArea ( gob, area ) && gob != NUtils.getGameUI().map.player () ) {
                        result.add ( gob );
                    }
                }
            }
    
            result.sort ( new Comparator<Gob> () {
                @Override
                public int compare (
                        Gob lhs,
                        Gob rhs
                ) {
                    // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                    return ( lhs.rc.x > rhs.rc.x ) ? -1 : ( ( lhs.rc.x < rhs.rc.x ) ? 1 : ( lhs.rc.y > rhs.rc.y ) ? -1 : (
                            lhs.rc.y < rhs.rc.y ) ? 1 : 0 );
                }
            } );
        }
        return result;
    }
    
    
    public static ArrayList<Coord2d> findTilesInArea (
            NAlias name,
            NArea area
    ) {
        ArrayList<Coord2d> result = new ArrayList<> ();
        for ( double x = area.begin.x ; x < area.end.x ; x += 11 ) {
            for ( double y = area.begin.y ; y < area.end.y ; y += 11 ) {
                Coord pltc = ( new Coord2d ( ( x ) / 11, ( y ) / 11 ) ).floor ();
                
                if ( NUtils.isIt ( pltc, name ) ) {
                    result.add ( new Coord2d ( x, y ) );
                }
            }
        }
        return result;
    }
    
    public static ArrayList<Gob> findAllinDistance (
            NAlias name,
            double distance
    ) {
        /// Расстояние до объекта с "запасом"
        ArrayList<Gob> result = new ArrayList<> ();
        synchronized ( NUtils.getGameUI().ui.sess.glob.oc ) {
            for ( Gob gob : NUtils.getGameUI().ui.sess.glob.oc ) {
                double dist = NUtils.getGameUI().map.player ().rc.dist ( gob.rc );
                /// Если расстояние минимально то оно и объект запоминаются
                if ( dist < distance ) {
                    if ( NUtils.isIt ( gob, name ) ) {
                        result.add ( gob );
                    }
                }
            }
        }
        
        result.sort ( new Comparator<Gob> () {
            @Override
            public int compare (
                    Gob lhs,
                    Gob rhs
            ) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                return ( lhs.rc.x > rhs.rc.x ) ? -1 : ( ( lhs.rc.x < rhs.rc.x ) ? 1 : ( lhs.rc.y > rhs.rc.y ) ? -1 : (
                        lhs.rc.y < rhs.rc.y ) ? 1 : 0 );
            }
        } );
        return result;
    }
    
    public static boolean isGobInArea (
            NArea hitBox
    ) {
        synchronized ( NUtils.getGameUI().getMap () ) {
            synchronized ( NUtils.getGameUI().ui.sess.glob.oc ) {
                for ( Gob gob : NUtils.getGameUI().ui.sess.glob.oc ) {
                    if ( gob != null && gob != NUtils.getGameUI().map.player () ) {
                        if ( gob.getres () != null ) {
                            if ( gob.getres ().name.contains ( "item" ) || gob.getres ().name.contains ( "plant" ) ) {
                                continue;
                            }
                        }
                        //                        if(NUtils.isIt(gob,names)) {
                        NHitBox gobHitBox = NHitBox.get ( gob, false );
                        if ( gobHitBox != null ) {
                            if ( hitBox.checkCross ( gobHitBox ) ) {
                                return true;
                            }
                            //                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public static boolean isGobInAreaEx (
            NArea hitBox,
            NAlias ignored
    ) {
        synchronized ( NUtils.getGameUI().getMap () ) {
            synchronized ( NUtils.getGameUI().ui.sess.glob.oc ) {
                for ( Gob gob : NUtils.getGameUI().ui.sess.glob.oc ) {
                    if ( gob != null && gob != NUtils.getGameUI().map.player () && !gob.isTag(NGob.Tags.lifted) ) {
                        if ( gob.getres () != null ) {
                            if ( NUtils.isIt ( gob, new NAlias ( "dframe" ) ) ) {
                                int a = 1;
                            }
                            if ( !NUtils.isIt ( gob, ignored ) ) {
                                NHitBox gobHitBox = NHitBox.get ( gob, false );
                                
                                if ( gobHitBox != null ) {
                                    if ( hitBox.calcCross ( gobHitBox ) ) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public static boolean isGobInArea (
            NArea hitBox,
            NAlias name
    ) {
        synchronized ( NUtils.getGameUI().getMap () ) {
            synchronized ( NUtils.getGameUI().ui.sess.glob.oc ) {
                for ( Gob gob : NUtils.getGameUI().ui.sess.glob.oc ) {
                    if ( gob != null && gob != NUtils.getGameUI().map.player () ) {
                        if ( NUtils.isIt ( gob, name ) ) {
                            NHitBox gobHitBox = NHitBox.get ( gob, false );
                            if ( gobHitBox != null ) {
                                if ( hitBox.checkCross ( gobHitBox ) ) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Поиск объекта по ID
     *
     * @param ID Идентификатор объекта
     * @return Объект
     */
    public static Gob findObject (
            long ID
    ) {
        synchronized ( NUtils.getGameUI().ui.sess.glob.oc ) {
            for ( Gob gob : NUtils.getGameUI().ui.sess.glob.oc ) {
                if ( gob != null ) {
                    if ( gob.id == ID ) {
                        return gob;
                    }
                }
            }
        }
        return null;
    }
    
    public static Gob findNearestObject ( ArrayList<Gob> gobs ) {
        double length = 50000;
        Gob result = null;
        for ( Gob gob : gobs ) {
            double dist = NUtils.getGameUI().map.player ().rc.dist ( gob.rc );
            /// Если расстояние минимально то оно и объект запоминаются
            if ( dist < length ) {
                length = dist;
                result = gob;
            }
        }
        return result;
    }
    
    public static Gob findNearestObject (
            long ignored
    ) {
        /// Расстояние до объекта с "запасом"
        double length = 50000;
        Gob result = null;
        synchronized ( NUtils.getGameUI().ui.sess.glob.oc ) {
            for ( Gob gob : NUtils.getGameUI().ui.sess.glob.oc ) {
                if ( gob != NUtils.getGameUI().map.player () ) {
                    if ( gob.id != ignored ) {
                        if ( gob.getres () != null ) {
                            /// Сравнивается расстояние между игроком и объектом
                            double dist = NUtils.getGameUI().map.player ().rc.dist ( gob.rc );
                            /// Если расстояние минимально то оно и объект запоминаются
                            if ( dist < length ) {
                                length = dist;
                                result = gob;
                            }
                        }
                    }
                }
            }
        }
        return result;
    }
    
    
    public static Gob findNearestObject (
    ) {
        /// Расстояние до объекта с "запасом"
        double length = 50000;
        Gob result = null;
        synchronized ( NUtils.getGameUI().ui.sess.glob.oc ) {
            for ( Gob gob : NUtils.getGameUI().ui.sess.glob.oc ) {
                if ( gob != NUtils.getGameUI().map.player () ) {
                    if ( gob.getres () != null ) {
                        /// Сравнивается расстояние между игроком и объектом
                        double dist = NUtils.getGameUI().map.player ().rc.dist ( gob.rc );
                        /// Если расстояние минимально то оно и объект запоминаются
                        if ( dist < length ) {
                            length = dist;
                            result = gob;
                        }
                    }
                }
            }
        }
        //        if (result != null) {
        //            if (result.getres() != null) {
        //                System.out.println(result.getres().name);
        ////                System.out.println("g_X = " + result.rc.x + "g_y = " + result.rc.y);
        //            }
        //        }
        return result;
    }
    
    public static WItem findDressedItem (
            NAlias name
    ) {
        /// Запрашиваем текующую экипировку
        NEquipory equipory = NUtils.getGameUI().getEquipment ();
        /// Просматриваем экипировку
        for ( int i = 0 ; i < Equipory.ecoords.length ; i++ ) {
            /// Если в слоте есть предмет, то проверяем его
            if ( equipory.quickslots[i] != null ) {
                /// Проверяем соответствие
                if ( NUtils.isIt ( equipory.quickslots[i].item, name ) ) {
                    /// Возвращаем виджет предмета в слоте экипировки
                    return equipory.quickslots[i];
                }
            }
        }
        /// Если предмет не найдет возвращаем null
        return null;
    }
    
    public static ArrayList<WItem> findDressedItems (
            NAlias name
    ) {
        ArrayList<WItem> res = new ArrayList<> ();
        /// Запрашиваем текующую экипировку
        NEquipory equipory = NUtils.getGameUI().getEquipment ();
        /// Просматриваем экипировку
        for ( int i = 0 ; i < Equipory.ecoords.length ; i++ ) {
            /// Если в слоте есть предмет, то проверяем его
            if ( equipory.quickslots[i] != null ) {
                /// Проверяем соответствие
                if ( NUtils.isIt ( equipory.quickslots[i].item, name ) ) {
                    ///  виджет предмета в слоте экипировки добавляем в массив
                    res.add ( equipory.quickslots[i] );
                }
            }
        }
        
        return res;
    }
    
    public static NArea findNearestMark (AreasID id ) {
        if(id!=null && AreasID.get(id)!=null) {
            Gob first = findSign(new NAlias("iconsign"), 5000, id);
            if (first != null) {
                Gob second = findSignButNotThis(new NAlias("iconsign"), 5000, id, first);
                if (second != null) {
                    NArea res = new NArea(first, second);
                    /// TODO AREAS SHOW
//                NUtils.constructOverlay(res, id);
                    return res;
                }
            }
        }
        return null;
    }
    public static Coord2d findTileInArea (
            NArea area,
            String sub
    ){
        Coord2d pos = new Coord2d ( area.begin.x, area.begin.y );
        if ( pos.x != 0.0 & pos.y != 0.0 ) {
            while ( pos.x <= area.end.x ) {
                while ( pos.y <= area.end.y ) {
                    Coord pltc = ( new Coord2d ( pos.x / 11, pos.y / 11 ) ).floor ();
                    Resource res_beg = NUtils.getGameUI().ui.sess.glob.map.tilesetr ( NUtils.getGameUI().ui.sess.glob.map.gettile ( pltc ) );
                    if ( NUtils.checkName ( res_beg.name, new NAlias ( sub ) ) ) {
                        return new Coord2d ( pos.x, pos.y );
                    }
                    pos.y += MCache.tilesz.y;
                }
                pos.y = area.begin.y;
                pos.x += MCache.tilesz.x;
            }
        }
        return null;
    }

    public static NArea findSubArea (
            AreasID main,
            String sub
    ) {
        NArea area = findNearestMark(main);
        Coord2d pos = new Coord2d ( area.begin.x, area.begin.y );
        Coord2d first = null;
        Coord2d second = null;
        if ( pos.x != 0.0 & pos.y != 0.0 ) {
            while ( pos.x <= area.end.x ) {
                while ( pos.y <= area.end.y ) {
                    Coord pltc = ( new Coord2d ( pos.x / 11, pos.y / 11 ) ).floor ();
                    Resource res_beg = NUtils.getGameUI().ui.sess.glob.map.tilesetr ( NUtils.getGameUI().ui.sess.glob.map.gettile ( pltc ) );
                    if ( NUtils.checkName ( res_beg.name, new NAlias ( sub ) ) ) {
                        if(first==null)
                            first = new Coord2d ( pos.x, pos.y );
                        else {
                            second = new Coord2d(pos.x, pos.y);
                            return new NArea(first,second, true);
                        }
                    }
                    pos.y += MCache.tilesz.y;
                }
                pos.y = area.begin.y;
                pos.x += MCache.tilesz.x;
            }
        }
        return null;
    }

    public static NArea findSubArea (
            AreasID main,
            AreasID sub
    ) {
        ArrayList<Gob> signs = Finder.findObjects(new NAlias("iconsign"), main);
        Gob first = null;
        long id = ItemTex.made_id.get(AreasID.get(sub));
        for (Gob sign : signs) {
            if (sign.getModelAttribute() == id) {
                if (first == null)
                    first = sign;
                else {
                    return new NArea(first, sign);
                }
            }
        }
        return null;
    }
    
    public static NArea findHarvestArea (
            AreasID id,
            String paving
    ) {
        ArrayList<Coord2d> coords = new ArrayList<Coord2d> ();
        NArea global = findNearestMark ( id );
        Coord2d first = findTile ( global, paving, new Coord2d () );
        Coord2d second = findTile ( global, paving, first );
        Coord2d min, max;
        if ( first.x < second.x ) {
            if ( first.y < second.y ) {
                min = first;
                max = second;
            }
            else {
                min = new Coord2d ( first.x, second.y );
                max = new Coord2d ( second.x, first.y );
            }
        }
        else {
            if ( second.y < first.y ) {
                min = second;
                max = first;
            }
            else {
                min = new Coord2d ( second.x, first.y );
                max = new Coord2d ( first.x, second.y );
            }
        }

        Coord2d pos = new Coord2d ( min.x, min.y );
        if ( pos.x != 0.0 & pos.y != 0.0 ) {
            while ( pos.x <= max.x ) {
                while ( pos.y <= max.y ) {
                    Coord pltc = ( new Coord2d ( pos.x / 11, pos.y / 11 ) ).floor ();
                    Resource res_beg = NUtils.getGameUI().ui.sess.glob.map.tilesetr ( NUtils.getGameUI().ui.sess.glob.map.gettile ( pltc ) );
                    if ( NUtils.checkName ( res_beg.name, new NAlias ( "field" ) ) ) {
                        coords.add ( new Coord2d ( pos.x, pos.y ) );
                    }
                    pos.y += MCache.tilesz.y;
                }
                pos.y = min.y;
                pos.x += MCache.tilesz.x;
            }
        }
        return new NArea( coords );
    }
    
    public static Coord2d findTile (
            NArea area,
            String paving,
            Coord2d titleEx
    ) {
        Coord first = ( new Coord2d ( titleEx.x / 11, titleEx.y / 11 ) ).floor ();
        Coord2d pos = new Coord2d ( area.begin.x + MCache.tilesz.x / 2, area.begin.y + MCache.tilesz.y / 2 );
        while ( pos.x <= area.end.x ) {
            while ( pos.y <= area.end.y ) {
                
                Coord pltc = ( new Coord2d ( pos.x / 11, pos.y / 11 ) ).floor ();
                Resource res_beg = NUtils.getGameUI().ui.sess.glob.map.tilesetr ( NUtils.getGameUI().ui.sess.glob.map.gettile ( pltc ) );
                if ( pltc.x != first.x && pltc.y != first.y ) {
                    if ( NUtils.checkName ( res_beg.name, new NAlias ( paving ) ) ) {
                        return new Coord2d ( pos.x, pos.y );
                    }
                }
                pos.y += MCache.tilesz.y;
            }
            pos.y = area.begin.y + MCache.tilesz.y / 2;
            pos.x += MCache.tilesz.x;
        }
        return new Coord2d ();
    }
    
    
    public static NArea findNearestPaving (
            AreasID id,
            String paving
    ) {
        NArea area;
        Gob first = findSign ( new NAlias ( "iconsign" ), 5000, id );
        if ( first != null ) {
            Gob second = findSignButNotThis ( new NAlias ( "iconsign" ), 5000, id, first );
            if ( second != null ) {
                area = new NArea( first, second );
            }
        }
        
        return new NArea();
    }

    public static NArea findNearestMark (
            AreasID id,
            String paiving
    ) {
        Gob first = findObject ( new NAlias ( "iconsign" ), 5000, ItemTex.made_id.get(AreasID.get(id)), paiving );
        if ( first != null ) {
            Gob second = findObjectButNotThis ( new NAlias ( "iconsign" ), 5000, id, first, paiving );
            if ( second != null ) {
                return new NArea( first, second );
            }
        }
        return new NArea();
    }
    
    public static Gob findObject (
            NAlias name,
            AreasID id
    ) {
        /// Расстояние до объекта с "запасом"
        Gob result = null;
        double distance = 10000;
        synchronized ( NUtils.getGameUI().ui.sess.glob.oc ) {
            for ( Gob gob : NUtils.getGameUI().ui.sess.glob.oc ) {
                if ( NUtils.isIt ( gob, name ) ) {
                    if ( gob.getModelAttribute() == ItemTex.made_id.get(AreasID.get(id)) ) {
                        /// Сравнивается расстояние между игроком и объектом
                        double dist = NUtils.getGameUI().map.player ().rc.dist ( gob.rc );
                        /// Если расстояние минимально то оно и объект запоминаются
                        if ( dist < distance ) {
                            distance = dist;
                            result = gob;
                        }
                    }
                }
            }
        }
        return result;
    }
    
    public static Gob findObjectMask (
            NAlias name,
            long mask,
            double distance
    ) {
        /// Расстояние до объекта с "запасом"
        Gob result = null;
        synchronized ( NUtils.getGameUI().ui.sess.glob.oc ) {
            for ( Gob gob : NUtils.getGameUI().ui.sess.glob.oc ) {
                if ( NUtils.isIt ( gob, name ) ) {
                    if ((gob.getModelAttribute() & mask) == 0) {
                        /// Сравнивается расстояние между игроком и объектом
                        double dist = NUtils.getGameUI().map.player().rc.dist(gob.rc);
                        /// Если расстояние минимально то оно и объект запоминаются
                        if (dist < distance) {
                            distance = dist;
                            result = gob;
                        }
                    }
                }
            }
        }

        return result;
    }
    
    
    public static Gob findObject (
            NAlias name,
            double distance,
            long id,
            String paving
    ) {
        /// Расстояние до объекта с "запасом"
        Gob result = null;
        synchronized ( NUtils.getGameUI().ui.sess.glob.oc ) {
            for ( Gob gob : NUtils.getGameUI().ui.sess.glob.oc ) {
                if ( NUtils.isIt ( gob, name ) ) {
                        if ( gob.getModelAttribute() == id ) {
                            /// Сравнивается расстояние между игроком и объектом
                            double dist = NUtils.getGameUI().map.player ().rc.dist ( gob.rc );
                            /// Если расстояние минимально то оно и объект запоминаются
                            if ( dist < distance ) {
                                Coord pltc = ( new Coord2d ( ( gob.rc.x ) / 11, ( gob.rc.y ) / 11 ) ).floor ();
                                Resource res_beg = NUtils.getGameUI().ui.sess.glob.map.tilesetr (
                                        NUtils.getGameUI().ui.sess.glob.map.gettile ( pltc ) );
                                if ( res_beg.name.contains ( paving ) ) {
                                    distance = dist;
                                    result = gob;
                                }
                            }
                        }
                }
            }
        }
        return result;
    }
    
    public static Gob findSign (
            NAlias name,
            double distance,
            AreasID id
    ) {
        Gob result = null;
        if (AreasID.get(id) != null && ItemTex.made_id.get(AreasID.get(id))!=null) {
            synchronized (NUtils.getGameUI().ui.sess.glob.oc) {
                for (Gob gob : NUtils.getGameUI().ui.sess.glob.oc) {
                    if (NUtils.isIt(gob, name)) {
                        if (gob.getModelAttribute() == ItemTex.made_id.get(AreasID.get(id))) {
                            /// Сравнивается расстояние между игроком и объектом
                            double dist = NUtils.getGameUI().map.player().rc.dist(gob.rc);
                            /// Если расстояние минимально то оно и объект запоминаются
                            if (dist < distance) {
                                Coord pltc = (new Coord2d((gob.rc.x) / 11, (gob.rc.y) / 11)).floor();
                                Resource res_beg = NUtils.getGameUI().ui.sess.glob.map.tilesetr(
                                        NUtils.getGameUI().ui.sess.glob.map.gettile(pltc));
                                distance = dist;
                                result = gob;
                            }
                        }
                    }
                }
            }
        }
        return result;
    }
    
    public static Gob findSigninArea (
            NAlias name,
            double distance,
            NArea mainArea,
            AreasID id
    ) {
        /// Расстояние до объекта с "запасом"
        Gob result = null;
        synchronized ( NUtils.getGameUI().ui.sess.glob.oc ) {
            for ( Gob gob : NUtils.getGameUI().ui.sess.glob.oc ) {
                if ( NUtils.isIt ( gob, name ) ) {
                    if ( mainArea.checkCross ( NHitBox.get ( gob, false ) ) ) {
                            if ( gob.getModelAttribute() == ItemTex.made_id.get(AreasID.get(id))) {
                                /// Сравнивается расстояние между игроком и объектом
                                double dist = NUtils.getGameUI().map.player ().rc.dist ( gob.rc );
                                /// Если расстояние минимально то оно и объект запоминаются
                                if ( dist < distance ) {
                                    Coord pltc = ( new Coord2d ( ( gob.rc.x ) / 11, ( gob.rc.y ) / 11 ) ).floor ();
                                    distance = dist;
                                    result = gob;
                                }
                            }
                    }
                }
            }
        }
        return result;
    }
    
    public static Gob findSigninAreaEx (
            NAlias name,
            double distance,
            NArea mainArea,
            AreasID id,
            Gob ex
    ) {
        /// Расстояние до объекта с "запасом"
        Gob result = null;
        synchronized ( NUtils.getGameUI().ui.sess.glob.oc ) {
            for ( Gob gob : NUtils.getGameUI().ui.sess.glob.oc ) {
                if ( NUtils.isIt ( gob, name ) ) {
                    if ( gob != ex ) {
                        if ( mainArea.checkCross ( NHitBox.get ( gob, false ) ) ) {
                                if ( gob.getModelAttribute() == ItemTex.made_id.get(AreasID.get(id))) {
                                    /// Сравнивается расстояние между игроком и объектом
                                    double dist = NUtils.getGameUI().map.player ().rc.dist ( gob.rc );
                                    /// Если расстояние минимально то оно и объект запоминаются
                                    if ( dist < distance ) {
                                        Coord pltc = ( new Coord2d ( ( gob.rc.x ) / 11, ( gob.rc.y ) / 11 ) ).floor ();
                                        distance = dist;
                                        result = gob;
                                    }
                                }
                        }
                    }
                }
            }
        }
        return result;
    }
    
    public static Gob findObject (
            NAlias name,
            double distance,
            AreasID id,
            ArrayList<Long> excep
    ) {
        /// Расстояние до объекта с "запасом"
        Gob result = null;
        synchronized ( NUtils.getGameUI().ui.sess.glob.oc ) {
            for ( Gob gob : NUtils.getGameUI().ui.sess.glob.oc ) {
                if ( NUtils.isIt ( gob, name ) ) {
                    if ( !excep.contains ( gob.id ) ) {
                            if ( gob.getModelAttribute() == ItemTex.made_id.get(AreasID.get(id)) ) {
                                /// Сравнивается расстояние между игроком и объектом
                                double dist = NUtils.getGameUI().map.player ().rc.dist ( gob.rc );
                                /// Если расстояние минимально то оно и объект запоминаются
                                if ( dist < distance ) {
                                    distance = dist;
                                    result = gob;
                                }
                            }
                    }
                }
            }
        }
        return result;
    }
    
    public static Gob findSignButNotThis (
            NAlias name,
            double distance,
            AreasID id,
            Gob badgob
    ) {
        /// Расстояние до объекта с "запасом"
        Gob result = null;
        if (AreasID.get(id) != null) {
            synchronized (NUtils.getGameUI().ui.sess.glob.oc) {
                for (Gob gob : NUtils.getGameUI().ui.sess.glob.oc) {
                    if (gob != badgob) {
                        if (NUtils.isIt(gob, name)) {
                            if (gob.getModelAttribute() == ItemTex.made_id.get(AreasID.get(id))) {
                                /// Сравнивается расстояние между игроком и объектом
                                double dist = NUtils.getGameUI().map.player().rc.dist(gob.rc);
                                /// Если расстояние минимально то оно и объект запоминаются
                                if (dist < distance) {
                                    distance = dist;
                                    result = gob;
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }
    
    static Gob findObjectButNotThis (
            NAlias name,
            double distance,
            long id,
            Gob badgob
    ) {
        /// Расстояние до объекта с "запасом"
        Gob result = null;
        synchronized ( NUtils.getGameUI().ui.sess.glob.oc ) {
            for ( Gob gob : NUtils.getGameUI().ui.sess.glob.oc ) {
                if ( gob != badgob ) {
                    if ( NUtils.isIt ( gob, name ) ) {
                            if ( gob.getModelAttribute() == id ) {
                                /// Сравнивается расстояние между игроком и объектом
                                double dist = NUtils.getGameUI().map.player ().rc.dist ( gob.rc );
                                /// Если расстояние минимально то оно и объект запоминаются
                                if ( dist < distance ) {
                                    distance = dist;
                                    result = gob;
                                }
                            }
                    }
                }
            }
        }
        return result;
    }
    
    static Gob findObjectButNotThis (
            NAlias name,
            double distance,
            AreasID id,
            Gob badgob,
            String paving
    ) {
        /// Расстояние до объекта с "запасом"
        Gob result = null;
        synchronized ( NUtils.getGameUI().ui.sess.glob.oc ) {
            for ( Gob gob : NUtils.getGameUI().ui.sess.glob.oc ) {
                if ( gob != badgob ) {
                    if ( NUtils.isIt ( gob, name ) ) {
                            if ( gob.getModelAttribute() == ItemTex.made_id.get(AreasID.get(id)) ) {
                                /// Сравнивается расстояние между игроком и объектом
                                double dist = NUtils.getGameUI().map.player ().rc.dist ( gob.rc );
                                /// Если расстояние минимально то оно и объект запоминаются
                                if ( dist < distance ) {
                                    Coord pltc = ( new Coord2d ( ( gob.rc.x ) / 11, ( gob.rc.y ) / 11 ) ).floor ();
                                    Resource res_beg = NUtils.getGameUI().ui.sess.glob.map.tilesetr (
                                            NUtils.getGameUI().ui.sess.glob.map.gettile ( pltc ) );
                                    if ( res_beg.name.contains ( paving ) ) {
                                        distance = dist;
                                        result = gob;
                                    }
                                }
                            }
                    }
                }
            }
        }
        return result;
    }
    
    public static Coord2d findPlace (
            NHitBox hitBox,
            NArea area,
            String exep
    )
            throws NoFreeSpace {
        NHitBox worked = new NHitBox ( hitBox );
        double shift_x = hitBox.end.x - hitBox.begin.x;
        double shift_y = hitBox.end.y - hitBox.begin.y;
        worked.begin.x += 0.5;
        worked.begin.y += 0.5;
        worked.end.x -= 0.5;
        worked.end.y -= 0.5;
        double x_pos = area.begin.x + shift_x / 2;
        double y_pos = area.begin.y + shift_y / 2;
        while ( x_pos < area.end.x ) {
            while ( y_pos < area.end.y ) {
                worked.correct ( new Coord2d ( x_pos, y_pos ), worked.orientation );
                if ( exep.length () == 0 ) {
                    if ( !Finder.isGobInAreaEx ( worked,
                            new NAlias ( new ArrayList<String> ( Arrays.asList ( "plant", "item" ) ),
                                    new ArrayList<String> ( Arrays.asList ( "trellis" ) ) ) ) ) {
                        return worked.center;
                    }
                }
                else if ( !Finder.isGobInAreaEx ( worked,
                        new NAlias ( new ArrayList<String> ( Arrays.asList ( exep, "plant", "item" ) ),
                                new ArrayList<String> ( Arrays.asList ( "trellis" ) ) ) ) ) {
                    return worked.center;
                }
                y_pos += shift_y;
            }
            y_pos = area.begin.y + shift_y / 2;
            x_pos += shift_x;
        }
        throw new NoFreeSpace ();
    }
    
    public static Coord2d findPlaceTrellis (
            NHitBox hitBox,
            NArea area,
            String exep
    )
            throws NoFreeSpace {
        NHitBox worked = new NHitBox ( hitBox );
        double shift_x = worked.end.x - worked.begin.x;
        double shift_y = worked.end.y - worked.begin.y;
        worked.begin.x += 0.05;
        worked.begin.y += 0.05;
        worked.end.x -= 0.05;
        worked.end.y -= 0.05;
        double x_pos = area.begin.x;
        double y_pos = area.begin.y;
        while ( x_pos < area.end.x ) {
            while ( y_pos < area.end.y ) {
                Coord tile = new Coord2d ( x_pos + 5.5, y_pos + 5.5 ).floor ( MCache.tilesz );
                Coord2d test_c = new Coord2d ( tile.x * MCache.tilesz.x + 5.5, tile.y * MCache.tilesz.y + 5.5 );
                if ( shift_x < shift_y ) {
                    test_c.x -= shift_x;
                }
                else {
                    test_c.y -= shift_y;
                }
                Coord ftext_c = test_c.floor ( MCache.tilesz );
                while ( ftext_c.x == tile.x && ftext_c.y == tile.y ) {
                    worked.correct ( test_c, worked.orientation );
                    if ( exep.length () == 0 ) {
                        if ( !Finder.isGobInAreaEx ( worked,
                                new NAlias ( new ArrayList<String> ( Arrays.asList ( "plant", "item" ) ),
                                        new ArrayList<String> ( Arrays.asList ( "trellis" ) ) ) ) ) {
                            return worked.center;
                        }
                    }
                    else if ( !Finder.isGobInAreaEx ( worked,
                            new NAlias ( new ArrayList<String> ( Arrays.asList ( exep, "plant", "item" ) ),
                                    new ArrayList<String> ( Arrays.asList ( "trellis" ) ) ) ) ) {
                        return worked.center;
                    }
                    if ( shift_x < shift_y ) {
                        test_c.x += shift_x;
                    }
                    else {
                        test_c.y += shift_y;
                    }
                    ftext_c = test_c.floor ( MCache.tilesz );
                }
                y_pos += 11;
            }
            y_pos = area.begin.y;
            x_pos += 11;
        }
        throw new NoFreeSpace ();
    }
    
    
    public static Coord2d findPlace2 (
            NHitBox ohitBox,
            NArea area,
            String exep
    )
            throws NoFreeSpace {
        double shift_x = ohitBox.end.x - ohitBox.begin.x;
        double shift_y = ohitBox.end.y - ohitBox.begin.y;
        shift_x = Math.max(shift_x,shift_y);
        shift_y = shift_x;
        NHitBox hitBox = new NHitBox(new Coord2d(-shift_x/2.,-shift_x/2.),new Coord2d(shift_x/2.,shift_x/2.));

        hitBox.begin.x += 0.01;
        hitBox.begin.y += 0.01;
        hitBox.end.x -= 0.01;
        hitBox.end.y -= 0.01;
        double x_pos = area.begin.x + shift_x / 2;
        double y_pos = area.begin.y + shift_y / 2;
        while ( x_pos < area.end.x ) {
            while ( y_pos < area.end.y ) {
                hitBox.correct ( new Coord2d ( x_pos, y_pos ), 0 );
                ArrayList<String> ignored = new ArrayList<> ();
                if(!Objects.equals(exep, ""))
                    ignored.add ( exep );
                ignored.add ( "plant" );
                ignored.add ( "items" );
                if ( !Finder.isGobInAreaEx ( hitBox, new NAlias ( ignored ) ) ) {
                    return hitBox.center;
                }
                y_pos += shift_y / 2.;
            }
            y_pos = area.begin.y + hitBox.end.y;
            x_pos += shift_x / 2.;
        }
        throw new NoFreeSpace ();
    }
    
    public static Coord2d findPlace2 (
            NHitBox hitBox,
            NArea area,
            NArea dummy
    )
            throws NoFreeSpace {
        double shift_x = hitBox.end.x - hitBox.begin.x;
        double shift_y = hitBox.end.y - hitBox.begin.y;
        hitBox.begin.x += 0.01;
        hitBox.begin.y += 0.01;
        hitBox.end.x -= 0.01;
        hitBox.end.y -= 0.01;
        double x_pos = area.begin.x + shift_x / 2;
        double y_pos = area.begin.y + shift_y / 2;
        while ( x_pos < area.end.x ) {
            while ( y_pos < area.end.y ) {
                hitBox.correct ( new Coord2d ( x_pos, y_pos ), 0 );
                if ( ( x_pos <= dummy.begin.x + 1 || x_pos >= dummy.end.x - 1 ) ||
                        ( y_pos <= dummy.begin.y + 1 || y_pos >= dummy.end.y - 1 ) ) {
                    if ( !Finder.isGobInAreaEx ( hitBox,
                            new NAlias ( new ArrayList<String> ( Arrays.asList ( "plant", "item" ) ),
                                    new ArrayList<String> ( Arrays.asList ( "trellis" ) ) ) ) ) {
                        return hitBox.center;
                    }
                }
                y_pos += shift_y;
            }
            y_pos = area.begin.y + hitBox.end.y;
            x_pos += shift_x;
        }
        throw new NoFreeSpace();
    }
    
}
