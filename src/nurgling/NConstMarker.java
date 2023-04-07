package nurgling;

import haven.*;
import haven.render.Homo3D;
import haven.render.Pipe;
import haven.render.Render;
import haven.render.RenderTree;


public class NConstMarker extends NSprite implements RenderTree.Node, PView.Render2D  {
	int z = 15;
	boolean isVisible = false;
	TexI img;
	public NConstMarker(Owner owner, NGob.Tags tag){
		super(owner, null);
		img = NUtils.getTexI(tag);
	}

	public NConstMarker(Owner owner, int shift, NGob.Tags tag){
		super(owner, null);
		z = shift;
		img = NUtils.getTexI(tag);
	}


	@Override
	public void gtick(Render g) {
		super.gtick(g);
	}

	@Override
	public boolean tick(double dt) {
		return super.tick(dt);
	}

	@Override
	public void draw(GOut g, Pipe state) {
		if(isVisible) {
			Coord3f markerPos = new Coord3f(0, 0, z + NUtils.getDeltaZ());
			Coord sc = Homo3D.obj2view(markerPos, state, Area.sized(g.sz())).round2();
			g.aimage(img, sc, 0.5, 0.5);
		}
	}
}