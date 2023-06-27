package nurgling;

import haven.*;
import haven.render.*;

import java.nio.ByteBuffer;
import java.util.*;


public class NModelBox extends NSprite implements RenderTree.Node {

	public static NBoundingBox defaultBb = new NBoundingBox (
			new ArrayList<> ( Arrays.asList ( NBoundingBox.acbcPol ( new Coord ( 5, 5 ), new Coord ( -5, -5 ) ) ) ),
			false );

	private static Resource getResource(Gob gob) {
		Resource res = gob.getres();
		if(res == null) {return null;}
		Collection<RenderLink.Res> links = res.layers(RenderLink.Res.class);
		for (RenderLink.Res link : links) {
			if(link.l instanceof RenderLink.MeshMat) {
				RenderLink.MeshMat mesh = (RenderLink.MeshMat) link.l;
				return mesh.mesh.get();
			}
		}
		return res;
	}

	public static NModelBox forGob(Gob gob) {
		if(!NConfiguration.getInstance().showBB)
			if(!NConfiguration.getInstance().hideNature || !(gob.isTag(NGob.Tags.tree) || gob.isTag(NGob.Tags.bumling) || gob.isTag(NGob.Tags.bush)))
				return null;
		Resource res = getResource(gob);
		if(res!=null) {
			Collection<Resource.Neg> negs = res.layers(Resource.Neg.class);
			if (negs != null) {
				ArrayList<NBoundingBox.Polygon> polygons = new ArrayList<>();
				for (Resource.Neg neg : negs) {
					Coord2d[] box = new Coord2d[4];
					box[0] = (new Coord2d(neg.bc.x, -neg.ac.y));
					box[1] = (new Coord2d(neg.ac.x, -neg.ac.y));
					box[2] = (new Coord2d(neg.ac.x, -neg.bc.y));
					box[3] = (new Coord2d(neg.bc.x, -neg.bc.y));
					polygons.add(new NBoundingBox.Polygon(box));
				}

				return new NModelBox(new NBoundingBox(polygons, true), gob);
			}
		}
		return null;
	}

	public static class HidePol extends Sprite implements RenderTree.Node {
		public static Pipe.Op emat = Pipe.Op.compose ( new BaseColor ( new java.awt.Color ( 224, 193, 79, 255 ) ) );
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

	Gob gob;
	public NModelBox(NBoundingBox bb, Gob gob) {
		super ( null, null );
		this.gob = gob;
		if ( bb == null ) {
			this.bb = defaultBb;
		}
		else {
			this.bb = bb;
		}
	}

	public void added ( RenderTree.Slot slot ) {
		for ( NBoundingBox.Polygon pol : bb.polygons ) {
			new HidePol( pol ).added ( slot );
		}
	}

	@Override
	public boolean tick(double dt) {
		if(!NConfiguration.getInstance().showBB) {
			if (NConfiguration.getInstance().hideNature) {
				if(gob.isTag(NGob.Tags.tree) || gob.isTag(NGob.Tags.bumling) || gob.isTag(NGob.Tags.bush))
					return false;
			}
			return true;
		}
		return super.tick(dt);
	}
}
