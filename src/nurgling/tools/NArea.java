package nurgling.tools;

import haven.*;
import nurgling.NAlias;
import nurgling.NHitBox;
import nurgling.NUtils;

import java.util.ArrayList;
import java.util.Comparator;


public class NArea {
    public Coord2d begin;
    public Coord2d end;
    public Coord2d center;
    public double radius;
    public double orientation = 0;
    public Coord2d[] coord2ds = new Coord2d[4];
    public Coord2d[] polyVertexes = new Coord2d[4];

    public long countTiles(){
        return Math.round(((end.x-begin.x+MCache.tilesz.x)/MCache.tilesz.x)*((end.y-begin.y+MCache.tilesz.y)/MCache.tilesz.y));
    }

    public long countFarmTiles(){
        farm_tiles.clear();
        Coord2d pos = new Coord2d ( begin.x, begin.y );
        int count = 0;
        while ( pos.x <= end.x ) {
            while ( pos.y <= end.y ) {
                Coord pltc = ( new Coord2d ( pos.x / 11, pos.y / 11 ) ).floor ();
                Resource res_beg = NUtils.getGameUI().ui.sess.glob.map.tilesetr ( NUtils.getGameUI().ui.sess.glob.map.gettile ( pltc ) );
                if ( NUtils.checkName ( res_beg.name, new NAlias( "field" ) ) ) {
                    count+=1;
                    farm_tiles.add(new Coord2d(pos.x, pos.y));
                }
                pos.y += MCache.tilesz.y;
            }
            pos.y = begin.y;
            pos.x += MCache.tilesz.x;
        }
        return count;
    }

    public Coord2d findMountain()
    {
        Coord2d pos = new Coord2d ( begin.x, begin.y );
        int count = 0;
        while ( pos.x <= end.x ) {
            while ( pos.y <= end.y ) {
                Coord pltc = ( new Coord2d ( pos.x / 11, pos.y / 11 ) ).floor ();
                Resource res_beg = NUtils.getGameUI().ui.sess.glob.map.tilesetr ( NUtils.getGameUI().ui.sess.glob.map.gettile ( pltc ) );
                if ( NUtils.checkName ( res_beg.name, new NAlias( "mountain" ) ) ) {
                    return new Coord2d(pos.x, pos.y);
                }
                pos.y += MCache.tilesz.y;
            }
            pos.y = begin.y;
            pos.x += MCache.tilesz.x;
        }
        return null;
    }

    ArrayList<Coord2d> farm_tiles = new ArrayList<>();

    public Coord2d findFarmTile(Coord2d pos){
        double dist = 10000;
        Coord2d res = pos;
        for(Coord2d coord: farm_tiles){
            double range = coord.dist(pos);
            if (range<dist) {
                res = coord;
                dist = range;
            }
        }
        return res;
    }

    public void show(){
        /// TODO отображение
//        if(Configuration.getInstance().showAreas){
//            NUtils.constructOverlay(this,-1);
//        }
    }


    public NArea(
            Coord2d start,
            double shift
    ) {
        this.begin = new Coord2d ( start.x - shift, start.y - shift );
        this.end = new Coord2d ( start.x + shift, start.y + shift );
        this.center = new Coord2d ( ( end.x - begin.x ) / 2. + begin.x, ( end.y - begin.y ) / 2. + begin.y );
        this.orientation = 0;
        polyVertexes[0] = end ;
        polyVertexes[1] = new Coord2d ( begin.x, end.y );
        polyVertexes[2] = begin;
        polyVertexes[3] = new Coord2d ( end.x, begin.y );
        coord2ds[0] = polyVertexes[2];
        coord2ds[1] = polyVertexes[1];
        coord2ds[2] = polyVertexes[0];
        coord2ds[3] = polyVertexes[3];
        this.radius = Math.max(Math.abs(end.x - begin.x), Math.abs(end.y - begin.y))/2;
    }
    
    public NArea(ArrayList<Coord2d> tilesArray ) {
        tilesArray.sort ( new Comparator<Coord2d> () {
            @Override
            public int compare (
                    Coord2d o1,
                    Coord2d o2
            ) {
                int res = Double.compare ( o1.x, o2.x );
                if ( res == 0 ) {
                    res = Double.compare ( o1.y, o2.y );
                }
                return res;
            }
        } );
        this.orientation = 0;
        this.begin = tilesArray.get ( 0 );
        this.end = tilesArray.get ( tilesArray.size () - 1 );
        this.center = new Coord2d ( ( end.x - begin.x ) / 2. + begin.x, ( end.y - begin.y ) / 2. + begin.y );
    }
    
    public void correct (
            Coord2d pos,
            double angle
    ) {
//        coord2ds.clear ();
//        polyVertexes.clear (  );
        orientation = angle;
        center = pos;
        
        polyVertexes[0] = end ;
        polyVertexes[1] = new Coord2d ( begin.x, end.y );
        polyVertexes[2] = begin;
        polyVertexes[3] = new Coord2d ( end.x, begin.y );
        
        coord2ds[0] = polyVertexes[2].rotate ( orientation ).shift ( pos );
        coord2ds[1] = polyVertexes[1].rotate ( orientation ).shift ( pos );
        coord2ds[2] = polyVertexes[0].rotate ( orientation ).shift ( pos );
        coord2ds[3] = polyVertexes[3].rotate ( orientation ).shift ( pos );
    }
    
    public void rotate90 (
    ) {
        double t = begin.x;
        begin.x = begin.y;
        begin.y = t;
        t = end.x;
        end.x = end.y;
        end.y = t;
        correct ( center, 0 );
    }
    
    public NArea(
            Coord2d begin,
            Coord2d end
    ) {
        this.orientation = 0;
        this.begin = new Coord2d(begin.x, begin.y);
        this.end = new Coord2d(end.x, end.y);
        this.center = new Coord2d ( ( end.x - begin.x ) / 2. + begin.x, ( end.y - begin.y ) / 2. + begin.y );
        this.radius = Math.max(Math.abs(end.x - begin.x), Math.abs(end.y - begin.y))/2;
    }

    public NArea(
            Coord2d begin,
            Coord2d end,
            boolean correct
    ) {
        this(new Coord2d(Math.min(begin.x,end.x),Math.min(begin.y,end.y)),new Coord2d(Math.max(begin.x,end.x),Math.max(begin.y,end.y)));
    }

    public void installBox(){
        coord2ds[0] = begin ;
        coord2ds[1] = new Coord2d ( begin.x, end.y ) ;
        coord2ds[2] = end ;
        coord2ds[3] = new Coord2d ( end.x, begin.y ) ;
    }
    
    public boolean check () {
        return begin != end;
    }
    
    public NArea(
            Gob first,
            Gob second
    ) {
        Coord fc = first.rc.floor ( MCache.tilesz );
        Coord2d first_c = new Coord2d ( fc.x * MCache.tilesz.x, fc.y * MCache.tilesz.y );
        Coord sc = second.rc.floor ( MCache.tilesz );
        Coord2d second_c = new Coord2d ( sc.x * MCache.tilesz.x, sc.y * MCache.tilesz.y );
        this.center = new Coord2d ();
        this.orientation = 0;
        this.begin = new Coord2d ( Math.min ( first_c.x, second_c.x ), Math.min ( first_c.y, second_c.y ) );
        this.end = new Coord2d ( Math.max ( first_c.x, second_c.x ) + MCache.tilesz.x,
                Math.max ( first_c.y, second_c.y ) + MCache.tilesz.y );
        this.center = new Coord2d ( ( end.x - begin.x ) / 2. + begin.x, ( end.y - begin.y ) / 2. + begin.y );
        coord2ds[0] = begin ;
        coord2ds[1] = new Coord2d ( begin.x, end.y ) ;
        coord2ds[2] = end ;
        coord2ds[3] = new Coord2d ( end.x, begin.y ) ;
        
        
    }

    
    public NArea() {
        this ( new Coord2d (), new Coord2d () );
    }
    
    public double getWidth () {
        return Math.abs ( end.y - begin.y );
    }
    
    public double getLength () {
        return Math.abs ( end.x - begin.x );
    }
    
    public boolean checkCross ( final NArea cell ) {
        double dist = cell.center.dist ( center );
        double hypo = Math.sqrt ( 2 ) *
                ( Math.max ( Math.abs ( cell.end.x - cell.begin.x ), Math.abs ( cell.end.y - cell.begin.y ) ) +
                        Math.max ( Math.abs ( end.x - begin.x ), Math.abs ( end.y - end.x ) ) );
        if ( hypo >= dist ) {
            return calcCross(cell);
        }
        return false;
    }

    public boolean calcCross(final NArea cell)
    {
        if ( orientation == cell.orientation && orientation == 0 ) {
            Coord2d r = new Coord2d ( end.x * Math.cos ( orientation ), end.x * Math.sin ( orientation ) );
            Coord2d q = new Coord2d ( -end.y * Math.sin ( orientation ), end.y * Math.cos ( orientation ) );
            return checkProjection ( r, this, cell ) && checkProjection ( q, this, cell );
        }
        else {
            Coord2d r = new Coord2d ( end.x * Math.cos ( orientation ), end.x * Math.sin ( orientation ) );
            Coord2d q = new Coord2d ( -end.y * Math.sin ( orientation ), end.y * Math.cos ( orientation ) );
            Coord2d s = new Coord2d ( cell.end.x * Math.cos ( cell.orientation ),
                    cell.end.x * Math.sin ( cell.orientation ) );
            Coord2d t = new Coord2d ( -cell.end.y * Math.sin ( cell.orientation ),
                    cell.end.y * Math.cos ( cell.orientation ) );

            return checkProjection ( r, this, cell ) && checkProjection ( q, this, cell ) &&
                    checkProjection ( s, this, cell ) && checkProjection ( t, this, cell );
        }
    }
    
    public static boolean checkProjection (
            Coord2d dir,
            NArea lhs,
            NArea rhs
    ) {
        ArrayList<Double> la = new ArrayList<> ();
        ArrayList<Double> ra = new ArrayList<> ();
        for ( int i = 0 ; i < 4 ; i++ ) {
            la.add ( dir.proj ( lhs.coord2ds[i] ) );
        }
        la.sort ( Double::compareTo );
        for ( int i = 0 ; i < 4 ; i++ ) {
            ra.add ( dir.proj ( rhs.coord2ds[ i ] ) );
        }
        ra.sort ( Double::compareTo );
        boolean f = ( la.get ( 3 ) >= ra.get ( 0 ) );
        boolean s = ( ra.get ( 3 ) >= la.get ( 0 ) );
        return f && s;
    }
}