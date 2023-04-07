/* Preprocessed source code */
package nurgling;

import haven.Coord;
import haven.Sprite;
import haven.render.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

/* >spr: BPRad */
public class NGobHiteBoxSpr extends NSprite implements RenderTree.Node {
    public static NBoundingBox defaultBb = new NBoundingBox (
            new ArrayList<> ( Arrays.asList ( NBoundingBox.acbcPol ( new Coord ( 5, 5 ), new Coord ( -5, -5 ) ) ) ),
            false );
    
    public static class HidePol extends Sprite implements RenderTree.Node {
        public static Pipe.Op emat = Pipe.Op.compose ( new BaseColor ( new java.awt.Color ( 65, 125, 65, 120 ) ) );
        final Model emod;
        private NBoundingBox.Polygon pol;
        
        static final VertexArray.Layout pfmt = new VertexArray.Layout (
                new VertexArray.Layout.Input ( Homo3D.vertex, new VectorFormat ( 3, NumberFormat.FLOAT32 ), 0, 0,
                        12 ) );
        
        public HidePol ( NBoundingBox.Polygon pol ) {
            super ( null, null );
            this.pol = pol;
            
            VertexArray va = new VertexArray ( pfmt,
                    new VertexArray.Buffer ( ( 4 ) * pfmt.inputs[0].stride, DataBuffer.Usage.STATIC,
                            this::fill ) );
            
            this.emod = new Model ( Model.Mode.TRIANGLE_FAN, va, null );
        }
        
        private FillBuffer fill (
                VertexArray.Buffer dst,
                Environment env
        ) {
            FillBuffer ret = env.fillbuf ( dst );
            ByteBuffer buf = ret.push ();
            if ( pol.neg ) {
                for ( int i = 3 ; i >= 0 ; i-- ) {
                    buf.putFloat ( ( float ) pol.vertices[ i ].x ).putFloat ( ( float ) -pol.vertices[ i ].y )
                       .putFloat ( 1.0f );
                }
            }
            else {
                for ( int i = 0 ; i < 4 ; i++ ) {
                    buf.putFloat ( ( float ) pol.vertices[ i ].x ).putFloat ( ( float ) pol.vertices[ i ].y )
                       .putFloat ( 1.0f );
                }
            }
            return ( ret );
        }
        
        public void added ( RenderTree.Slot slot ) {
            slot.ostate ( Pipe.Op.compose ( emat ) );
            slot.add ( emod );
        }
    }
    
    private NBoundingBox bb;
    
    public NGobHiteBoxSpr(NBoundingBox bb ) {
        super ( null, null );
        if ( bb == null ) {
            this.bb = defaultBb;
        }
        else {
            this.bb = bb;
        }
    }
    
    public void added ( RenderTree.Slot slot ) {
        for ( NBoundingBox.Polygon pol : bb.polygons ) {
            new HidePol ( pol ).added ( slot );
        }
    }

    @Override
    public boolean tick(double dt) {
        if(!NConfiguration.getInstance().enablePfBoundingBoxes)
            return true;
        return super.tick(dt);
    }
}
