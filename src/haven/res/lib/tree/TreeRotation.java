/* Preprocessed source code */
/* $use: lib/globfx */
/* $use: lib/leaves */
/* $use: lib/svaj */

package haven.res.lib.tree;

import haven.*;
import haven.render.*;


@FromResource(name = "lib/tree", version = 14)
public class TreeRotation extends GAttrib implements Gob.SetupMod {
    public final Location rot;

    public TreeRotation(Gob gob, Location rot) {
	super(gob);
	this.rot = rot;
    }

    public Pipe.Op placestate() {
	return(rot);
    }
}
