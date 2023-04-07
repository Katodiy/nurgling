/* Preprocessed source code */
/* $use: lib/globfx */
/* $use: lib/leaves */
/* $use: lib/svaj */

package haven.res.lib.tree;

import haven.*;
import haven.render.*;
import haven.res.lib.leaves.*;
import haven.res.lib.svaj.*;
import java.util.*;

@FromResource(name = "lib/tree", version = 14)
public class TreeScale extends GAttrib implements Gob.SetupMod {
    public final float scale;
    public final Location mod;

    public TreeScale(Gob gob, float scale) {
	super(gob);
	this.scale = scale;
	this.mod = Tree.mkscale(scale);
    }

    public Pipe.Op placestate() {
	return(mod);
    }
}

/*
 * Tree mesh IDs are of the form 0xabcd, where:
 *
 * a selects by tree "stage":
 *   0x1 selects for ordinary planted tree
 *   0x2      "      falling tree
 *   0x4      "      stump
 * b != 0 means enable mesh only under relevant seasons:
     0x1: Spring, 0x2: Summer, 0x4: Autumn, 0x8: Winter
 * c != 0 means use material (c * 4 + season) for mesh
 * d is ordinary resdat selection id, but shifted one bit to allow
 *   0 to mean always on, and inverted.
 */
