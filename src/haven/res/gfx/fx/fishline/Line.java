/* Preprocessed source code */
package haven.res.gfx.fx.fishline;

import haven.*;
import haven.render.*;
import java.util.*;
import java.util.function.*;
import java.nio.*;
import java.awt.Color;

@FromResource(name = "gfx/fx/fishline", version = 3)
public class Line implements Rendered, RenderTree.Node, Disposable {
    public static final VertexArray.Layout fmt = new VertexArray.Layout(new VertexArray.Layout.Input(Homo3D.vertex, new VectorFormat(3, NumberFormat.FLOAT32), 0, 0, 16),
									new VertexArray.Layout.Input(Homo3D.normal, new VectorFormat(3, NumberFormat.SNORM8),  0, 12, 16));
    public static final int points = 10;
    public final Model model;
    public final VertexArray va;
    public final VertexArray.Buffer data;
    public final boolean stretch;

    public Line(boolean stretch) {
	this.stretch = stretch;
	data = new VertexArray.Buffer(points * fmt.inputs[0].stride, DataBuffer.Usage.STREAM, DataBuffer.Filler.zero());
	va = new VertexArray(fmt, data);
	model = new Model(Model.Mode.LINE_STRIP, va, null);
    }

    public void draw(Pipe state, Render out) {
	out.draw(state, model);
    }

    public void update(Render out, Coord3f f, Coord3f t) {
	out.update(data, (dst, env) -> {
		FillBuffer ret = env.fillbuf(dst);
		ByteBuffer buf = ret.push();
		Coord3f d = t.sub(f);
		for(int i = 0; i < points; i++) {
		    float a = (float)i / (float)(points - 1);
		    float x, y, z;
		    x = f.x + (a * d.x);
		    y = f.y + (a * d.y);
		    if(stretch) {
			z = f.z + (a * d.z);
		    } else {
			if(d.z > 0)
			    z = f.z + ((a * a) * d.z);
			else
			    z = t.z - (((1 - a) * (1 - a)) * d.z);
		    }
		    buf.putFloat(x).putFloat(y).putFloat(z).put((byte)0).put((byte)0).put((byte)127).put((byte)0);
		}
		return(ret);
	    });
    }

    public void dispose() {
	model.dispose();
    }
}

/* >spr: FishLine */
