package nurgling;

import haven.*;
import haven.render.Homo3D;
import haven.render.Pipe;
import haven.render.Render;
import haven.render.RenderTree;


public class NWoolMarker extends NSprite implements RenderTree.Node, PView.Render2D  {
	int z = 15;
	boolean isVisible = false;
	TexI img;
	public NWoolMarker(Owner owner){
		super(owner, null);
		img = NUtils.getTexI(NGob.Tags.wool);
	}

	public NWoolMarker(Owner owner, int shift){
		super(owner, null);
		z = shift;
		img = NUtils.getTexI(NGob.Tags.wool);
	}


	@Override
	public void gtick(Render g) {
		super.gtick(g);
	}

	@Override
	public boolean tick(double dt) {
		if(((Gob)owner).isTag(NGob.Tags.wild) || !((Gob)owner).isTag(NGob.Tags.wool))
			return true;
		return super.tick(dt);
	}

	@Override
	public void draw(GOut g, Pipe state) {
		if(((Gob)owner).isTag(NGob.Tags.wool)) {
			Coord3f markerPos = new Coord3f(0, 0, z + NUtils.getDeltaZ());
			Coord sc = Homo3D.obj2view(markerPos, state, Area.sized(g.sz())).round2();
			g.aimage(img, sc, 0.5, 0.5);
		}
	}
}