/* Preprocessed source code */
/* $use: ui/tt/defn */

package haven.res.lib.layspr;

import haven.*;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.*;

@FromResource(name = "lib/layspr", version = 14)
public
class Image extends Layer {
    public final Resource.Image img;

    Image(Resource.Image img) {
	super(img.z, img.ssz);
	this.img = img;
    }

    void draw(GOut g) {
	g.image(img, Coord.z);
    }
}
