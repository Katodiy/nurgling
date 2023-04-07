/* Preprocessed source code */
package nurgling;

import haven.*;
import haven.render.*;
import haven.render.Model.Indices;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


/* >spr: BPRad */
public class NTexMarker extends NSprite implements RenderTree.Node, PView.Render2D  {

	final HashMap<NGob.Tags,TexI> imgs = new HashMap<>();

	int z = 15;

	public NTexMarker(Owner owner, NGob.Tags... tags){
		super(owner, null);
		for (NGob.Tags tag: tags){
			imgs.put(tag,NUtils.getTexI(tag));
		}
	}

	public NTexMarker(Owner owner, int shift, NGob.Tags... tags){
		super(owner, null);
		z = shift;
		for (NGob.Tags tag: tags){
			imgs.put(tag,NUtils.getTexI(tag));
		}
	}


	@Override
	public void gtick(Render g) {

		super.gtick(g);
	}

	@Override
	public boolean tick(double dt) {
		ArrayList < NGob.Tags> forRemove = new ArrayList<>();
		for(NGob.Tags tag : imgs.keySet()){
			if(!((Gob)owner).isTag(tag))
				forRemove.add(tag);
		}
		for(NGob.Tags tag : forRemove){
			imgs.remove(tag);
		}
		if(imgs.isEmpty())
			return true;
		return super.tick(dt);
	}

	@Override
	public void draw(GOut g, Pipe state) {
		synchronized (imgs) {
			float y = -((imgs.size() - 1) * 5.f / 2);
			for (NGob.Tags tag : imgs.keySet()) {
				Coord3f markerPos = new Coord3f(0, y, z + NUtils.getDeltaZ());
				Coord sc = Homo3D.obj2view(markerPos, state, Area.sized(g.sz())).round2();
				g.aimage(imgs.get(tag), sc, 0.5, 0.5);
				y += 5;
			}
		}
	}
}