package nurgling;

import haven.*;
import haven.render.Homo3D;
import haven.render.Pipe;
import haven.render.Render;
import haven.render.RenderTree;


public class NBeeMarker extends NSprite implements RenderTree.Node, PView.Render2D  {
	int z = 15;
	boolean isVisible = false;
	TexI img;
	Gob gob;
	long marker;
	public NBeeMarker(Owner owner){
		super(owner, null);
		gob =(Gob)owner;
		img = NUtils.getTexI(NGob.Tags.wax);
		marker = gob.getModelAttribute();
		update();
	}

	public NBeeMarker(Owner owner, int shift){
		super(owner, null);
		z = shift;
		img = NUtils.getTexI(NGob.Tags.wax);
	}

	void update() {
		if ((gob.modelAttribute & 4) != 0) {
			gob.addTag(NGob.Tags.wax);
		} else {
			gob.removeTag(NGob.Tags.wax);
		}
		marker = gob.getModelAttribute();
	}

	@Override
	public void gtick(Render g) {
		super.gtick(g);
	}

	@Override
	public boolean tick(double dt) {
		if(marker != gob.getModelAttribute())
			update();
		return super.tick(dt);
	}

	@Override
	public void draw(GOut g, Pipe state) {
		if(gob.isTag(NGob.Tags.wax)) {
			Coord3f markerPos = new Coord3f(0, 0, z + NUtils.getDeltaZ());
			Coord sc = Homo3D.obj2view(markerPos, state, Area.sized(g.sz())).round2();
			g.aimage(img, sc, 0.5, 0.5);
		}
	}
}