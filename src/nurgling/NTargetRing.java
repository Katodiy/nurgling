/* Preprocessed source code */
package nurgling;

import haven.*;
import haven.render.*;
import haven.render.Model.Indices;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class NTargetRing extends NSprite implements RenderTree.Node  {

	Pipe.Op smat;
	final VertexBuf.VertexData posa;
	final VertexBuf vbuf;
	final Model smod;
	float r;

	int size = 24;

	float alpha = 0.5f;
	TexI img;

	public NTargetRing(Owner owner, Color color, float range, float alpha) {
		this(owner, color, range);
		smat = new BaseColor(color,alpha);
	}
	public NTargetRing(Owner owner, Color color, float range) {
		super(owner, null);
 		r = range;
		smat = new BaseColor(color,alpha);
		FloatBuffer posb = Utils.wfbuf(size * 6 * 2);
		FloatBuffer nrmb = Utils.wfbuf(size * 6 * 2);
		double alpha = 0;
		for(int i = 0; i < size; i++) {
			float x = (float)(r*Math.cos(alpha));
			float y = (float)(r*Math.sin(alpha));
			posb.put(i * 3, x).put(     i  * 3 + 1,y).put(     i  * 3 + 2,  1);
			posb.put((size + i) * 3, 0.8f*x).put((size + i) * 3 + 1, 0.8f*y).put((size + i) * 3 + 2, 1);
			nrmb.put(i * 3, 0).put(     i  * 3 + 1, 0).put(     i  * 3 + 2, 1);
			nrmb.put((size + i) * 3, 0).put((size + i) * 3 + 1,0).put((size + i) * 3 + 2, 1);
			alpha+=2*Math.PI/size;
		}

		VertexBuf.VertexData posa = new VertexBuf.VertexData(posb);
		VertexBuf.NormalData nrma = new VertexBuf.NormalData(nrmb);
		VertexBuf vbuf = new VertexBuf(posa, nrma);
		this.smod = new Model(Model.Mode.TRIANGLES, vbuf.data(), new Indices(size*6 , NumberFormat.UINT16, DataBuffer.Usage.STATIC, this::sidx));
		this.posa = posa;
		this.vbuf = vbuf;
	}

	private FillBuffer sidx(Indices dst, Environment env) {
		FillBuffer ret = env.fillbuf(dst);
		ShortBuffer buf = ret.push().asShortBuffer();
		int b= 0;
		for(int i = 0; i <size-1;i++) {
			buf.put(b++, (short) i);
			buf.put(b++, (short) (size+i));
			buf.put(b++, (short) (i+1));
			buf.put(b++, (short) (size+i+1));
			buf.put(b++, (short) (size+i));
			buf.put(b++, (short) (i+1));
		}
		buf.put(b++, (short) (size-1));
		buf.put(b++, (short) (2*size-1));
		buf.put(b++, (short) (0));
		buf.put(b++, (short) (size));
		buf.put(b++, (short) (2*size-1));
		buf.put(b, (short) (0));
		return(ret);
	}


	public void added(RenderTree.Slot slot) {
		// XXXRENDER rl.prepo(Rendered.eyesort);
		slot.ostate(Pipe.Op.compose(Rendered.postpfx,
				new States.Facecull(States.Facecull.Mode.NONE),
				Location.goback("gobx")));
		slot.add(smod, smat);
	}

	@Override
	public void removed(RenderTree.Slot slot) {
		super.removed(slot);
	}

	@Override
	public boolean tick(double dt) {
		if(((Gob)owner).isTag(NGob.Tags.knocked, NGob.Tags.notplayer))
			return true;
		return super.tick(dt);
	}
}