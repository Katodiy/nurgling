/* Preprocessed source code */
package nurgling;

import haven.Gob;
import haven.Sprite;
import haven.Utils;
import haven.VertexBuf;
import haven.render.*;
import haven.render.Model.Indices;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class NHighlightRing extends NTargetRing  {
	public NHighlightRing(Owner owner) {
		super(owner, new Color(64, 255, 64, 255),20);
	}


	@Override
	public boolean tick(double dt) {
		if(!((Gob)owner).isTag(NGob.Tags.highlighted))
			return true;
		return super.tick(dt);
	}
}