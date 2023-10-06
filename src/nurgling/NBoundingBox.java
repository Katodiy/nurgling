package nurgling;

import haven.Coord;
import haven.Coord2d;
import haven.Gob;
import haven.Resource;

import java.util.ArrayList;

public class NBoundingBox {
    
    public final ArrayList<Polygon> polygons;
    public int vertices = 0;
    public boolean blocks = true;

    public NBoundingBox(Coord begin, Coord end) {
        polygons = new ArrayList<>();
        Coord2d[] box = new Coord2d[4];
        box[0] = (new Coord2d(end.x, end.y));
        box[1] = (new Coord2d(begin.x, end.y));
        box[2] = (new Coord2d(begin.x, begin.y));
        box[3] = (new Coord2d(end.x, begin.y));
        polygons.add(new NBoundingBox.Polygon(box));
        for ( Polygon pol : polygons ) {
            vertices += 4;
        }
        this.blocks = true;
    }

    public static Polygon acbcPol (
            Coord ac,
            Coord bc
    ) {
        Coord2d[] vertices = new Coord2d[4];
        vertices[0] = new Coord2d ( ac.x, ac.y );
        vertices[1] = new Coord2d ( bc.x, ac.y );
        vertices[2] = new Coord2d ( bc.x, bc.y );
        vertices[3] = new Coord2d ( ac.x, bc.y );
        Polygon pol = new Polygon ( vertices );
        pol.neg = true;
        return pol;
    }
    
    public NBoundingBox(
            ArrayList<Polygon> polygons,
            boolean blocks
    ) {
        this.polygons = polygons;
        for ( Polygon pol : polygons ) {
            vertices += 4;
        }
        this.blocks = blocks;
    }
    
    public static class Polygon {
        public final Coord2d[] vertices;
        public boolean neg;
        
        public Polygon ( Coord2d[] vertices ) {
            this.vertices = vertices;
        }
    }
    
    public static NBoundingBox getBoundingBox ( Gob gob ) {
        NHitBox box = NHitBox.get ( gob , false);
        if ( box != null ) {
            ArrayList<Polygon> polygons = new ArrayList<> ();
            
            polygons.add ( new Polygon ( box.polyVertexes ) );
            
            return new NBoundingBox ( polygons, true );
        }
        else {
            return null;
        }
    }
}
