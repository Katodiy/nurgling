/* Preprocessed source code */
package nurgling;

import haven.*;
import haven.render.*;
import haven.render.Model.Indices;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class NMarkedRing extends NTargetRing implements PView.Render2D {
	TexI img;

	public NMarkedRing(Owner owner, Color color, float range, float alpha, TexI img) {
		super(owner, color, range, alpha);
		this.img = img;
	}

	@Override
	public void draw(GOut g, Pipe state) {
		if(img!=null) {
			Coord sc = Homo3D.obj2view(new Coord3f(0,0,((Gob)owner).isTag( NGob.Tags.mounted)?35:25), state, Area.sized(g.sz())).round2();
			g.aimage(img, sc, 0.5, 0.5);
		}
	}
}