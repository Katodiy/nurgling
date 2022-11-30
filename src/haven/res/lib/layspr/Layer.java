/* Preprocessed source code */
/* $use: ui/tt/defn */

package haven.res.lib.layspr;

import haven.*;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.*;

@FromResource(name = "lib/layspr", version = 14)
public abstract class Layer {
    final int z;
    final Coord sz;

    Layer(int z, Coord sz) {
	this.z = z;
	this.sz = sz;
    }

    abstract void draw(GOut g);
}
