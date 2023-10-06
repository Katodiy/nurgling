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
			if (negs != null && negs.size()>0) {
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
			else if(gob.getResName()!= null)
			{
				NBoundingBox box;
				if((box = findCustom(gob.getResName()))!=null)
					return new NModelBox(box,gob);
				else
					return new NModelBox(new NBoundingBox(new ArrayList<>(), true), gob);
			}
		}
		return null;
	}

	private final static HashMap<String, NBoundingBox> custom = new HashMap<String, NBoundingBox>()
	{
		{
			put("log", new NBoundingBox(new Coord(-10,-2),new Coord(10,2)));
			put("gfx/terobjs/vehicle/dugout", new NBoundingBox(new Coord(-10,-2),new Coord(10,2)));
			put("bumlings", new NBoundingBox(new Coord(-3,-3),new Coord(3,3)));
			put("gfx/terobjs/arch/stonemansion", new NBoundingBox(new Coord(-50,-50),new Coord(50,50)));
			put("gfx/terobjs/arch/logcabin", new NBoundingBox(new Coord(-23,-23),new Coord(23,23)));
			put("gfx/terobjs/arch/greathall", new NBoundingBox(new Coord(-80,-55),new Coord(80,55)));
			put("gfx/terobjs/arch/timberhouse", new NBoundingBox(new Coord(-33,-33),new Coord(33,33)));
			put("gfx/terobjs/arch/stonetower", new NBoundingBox(new Coord(-39,-39),new Coord(39,39)));
			put("gfx/terobjs/arch/windmill", new NBoundingBox(new Coord(-28,-28),new Coord(28,28)));
			put("gfx/terobjs/arch/stonestead", new NBoundingBox(new Coord(-45,-28),new Coord(45,28)));
			put("gfx/terobjs/villageidol", new NBoundingBox(new Coord(-11,-17),new Coord(11,17)));
			put("gfx/terobjs/pclaim", new NBoundingBox(new Coord(-3,-3),new Coord(3,3)));
			put("gfx/terobjs/iconsign", new NBoundingBox(new Coord(-2,-2),new Coord(2,2)));
			put("gfx/terobjs/candelabrum", new NBoundingBox(new Coord(-2,-2),new Coord(2,2)));
			put("gfx/terobjs/lanternpost", new NBoundingBox(new Coord(-2,-2),new Coord(2,2)));
			put("gfx/terobjs/cistern", new NBoundingBox(new Coord(-9,-9),new Coord(9,9)));
			put("gfx/terobjs/oven", new NBoundingBox(new Coord(-9,-9),new Coord(9,9)));
			put("gfx/terobjs/kiln", new NBoundingBox(new Coord(-9,-9),new Coord(9,9)));
			put("gfx/terobjs/leanto", new NBoundingBox(new Coord(-9,-9),new Coord(9,9)));
			put("gfx/terobjs/stonepillar", new NBoundingBox(new Coord(-12,-12),new Coord(12,12)));
			put("gfx/terobjs/fineryforge", new NBoundingBox(new Coord(-9,-9),new Coord(9,9)));
			put("gfx/terobjs/smelter", new NBoundingBox(new Coord(-11,-11),new Coord(11,19)));
			put("gfx/terobjs/charterstone", new NBoundingBox(new Coord(-9,-9),new Coord(9,9)));
			put("gfx/terobjs/steelcrucible", new NBoundingBox(new Coord(-3,-4),new Coord(3,4)));
			put("gfx/terobjs/beehive", new NBoundingBox(new Coord(-4,-4),new Coord(4,4)));
			put("gfx/terobjs/column", new NBoundingBox(new Coord(-4,-4),new Coord(4,4)));
			put("gfx/terobjs/brazier", new NBoundingBox(new Coord(-4,-4),new Coord(4,4)));
			put("gfx/terobjs/granary", new NBoundingBox(new Coord(-16,-16),new Coord(16,16)));
			put("gfx/terobjs/pow", new NBoundingBox(new Coord(-4,-4),new Coord(4,4)));
			put("gfx/terobjs/smokeshed", new NBoundingBox(new Coord(-6,-6),new Coord(6,6)));
			put("gfx/terobjs/knarrdock", new NBoundingBox(new Coord(-60,-14),new Coord(62,14)));
			put("gfx/terobjs/furn/bed-sturdy", new NBoundingBox(new Coord(-9,-6),new Coord(9,6)));
			put("gfx/terobjs/vehicle/wreckingball-fold", new NBoundingBox(new Coord(-5,-11),new Coord(5,11)));
			put("gfx/terobjs/quern", new NBoundingBox(new Coord(-4,-4),new Coord(4,4)));
			put("gfx/terobjs/arch/palisadeseg", new NBoundingBox(new Coord(-5,-5),new Coord(5,5)));
			put("gfx/terobjs/arch/palisadecp", new NBoundingBox(new Coord(-5,-5),new Coord(5,5)));
			put("gfx/terobjs/arch/polecp", new NBoundingBox(new Coord(-5,-5),new Coord(5,5)));
			put("gfx/terobjs/arch/poleseg", new NBoundingBox(new Coord(-5,-5),new Coord(5,5)));
			put("gfx/terobjs/arch/drystonewallseg", new NBoundingBox(new Coord(-5,-5),new Coord(5,5)));
			put("gfx/terobjs/arch/drystonewallcp", new NBoundingBox(new Coord(-5,-5),new Coord(5,5)));
			put("gfx/terobjs/potterswheel", new NBoundingBox(new Coord(-2,-6),new Coord(2,6)));
			put("gfx/terobjs/stockpile-oddtuber", new NBoundingBox(new Coord(-5,-5),new Coord(5,5)));
			put("gfx/kritter/cattle/calf", new NBoundingBox(new Coord(-6,-2),new Coord(6,2)));
			put("gfx/kritter/horse/stallion", new NBoundingBox(new Coord(-9,-3),new Coord(9,3)));
			put("gfx/kritter/horse/mare", new NBoundingBox(new Coord(-9,-3),new Coord(9,3)));
			put("gfx/kritter/horse/foal", new NBoundingBox(new Coord(-8,-3),new Coord(8,3)));
			put("gfx/kritter/pig/piglet", new NBoundingBox(new Coord(-5,-2),new Coord(5,2)));
			put("gfx/kritter/pig/sow", new NBoundingBox(new Coord(-5,-2),new Coord(5,2)));
			put("gfx/kritter/pig/hog", new NBoundingBox(new Coord(-5,-2),new Coord(5,2)));
		}
	};

	public static NBoundingBox findCustom(String name)
	{
		NBoundingBox res = custom.get(name);
		if(res!=null)
			return res;
		if(name.endsWith("log") && name.startsWith("gfx/terobjs/trees"))
			return custom.get("log");
		if(name.startsWith("gfx/terobjs/bumlings"))
			return custom.get("bumlings");
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
