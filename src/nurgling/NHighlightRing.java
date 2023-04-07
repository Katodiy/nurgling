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
	Gob gob;
	private long start = 0;
	private static final long duration = 720;
	public NHighlightRing(Owner owner) {
		super(owner, new Color(64, 255, 64, 255),20);
		gob = (Gob)owner;
		start = NUtils.getTickId();
	}


	@Override
	public boolean tick(double dt) {
		long active = NUtils.getTickId() - start;
		if(active > duration) {
			gob.removeTag(NGob.Tags.highlighted);
			return true;
		}
		return super.tick(dt);
	}
}