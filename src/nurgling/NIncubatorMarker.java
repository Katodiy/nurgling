package nurgling;

import haven.*;
import haven.render.Homo3D;
import haven.render.Pipe;
import haven.render.Render;
import haven.render.RenderTree;

import java.util.HashMap;


public class NIncubatorMarker extends NSprite implements RenderTree.Node, PView.Render2D  {
	int z = 15;
	Gob gob;
	final HashMap<NGob.Tags,TexI> imgs = new HashMap<>();
	public NIncubatorMarker(Gob owner){
		super(owner, null);
		gob = owner;
		imgs.put(NGob.Tags.no_silo,NUtils.getTexI(NGob.Tags.no_silo));
		imgs.put(NGob.Tags.no_water,NUtils.getTexI(NGob.Tags.no_water));
	}

	public NIncubatorMarker(Gob owner, int shift){
		super(owner, null);
		z = shift;
		gob = owner;
		imgs.put(NGob.Tags.no_silo,NUtils.getTexI(NGob.Tags.no_silo));
		imgs.put(NGob.Tags.no_water,NUtils.getTexI(NGob.Tags.no_water));
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
		if(gob.isTag(NGob.Tags.no_silo) || gob.isTag(NGob.Tags.no_water) ) {
			float y = -((((gob.isTag(NGob.Tags.no_silo) && gob.isTag(NGob.Tags.no_water) ) ? 1 : 0) * 5.f )/ 2);
			for (NGob.Tags tag : imgs.keySet()) {
				Coord3f markerPos = new Coord3f(0, y, z + NUtils.getDeltaZ());
				Coord sc = Homo3D.obj2view(markerPos, state, Area.sized(g.sz())).round2();
				if ((gob.isTag(NGob.Tags.no_silo) && tag == NGob.Tags.no_silo) || (gob.isTag(NGob.Tags.no_water)  && tag == NGob.Tags.no_water)) {
					g.aimage(imgs.get(tag), sc, 0.5, 0.5);
				}
				if(y!=0)
					y += 5;
			}
		}
	}
}