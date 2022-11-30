/* Preprocessed source code */
package haven.res.lib.climb;

import haven.*;
import haven.render.*;
import java.util.*;
import haven.Skeleton.*;

@FromResource(name = "lib/climb", version = 4)
public class Climb extends Sprite implements Gob.SetupMod, Sprite.CDel {
    public final Composited comp;
    public final Composited.Poses op;
    public Composited.Poses mp;
    public boolean del = false;
    public Location loc = null;

    public Climb(Owner owner, Resource res) {
	super(owner, res);
	try {
	    comp = ((Gob)owner).getattr(Composite.class).comp;
	    op = comp.poses;
	} catch(NullPointerException e) {
	    throw(new Loading("Applying climbing effect to non-complete gob", e));
	}
    }

    public Pipe.Op placestate() {
	return(loc);
    }

    public boolean tick(double dt) {
	if(del)
	    return(true);
	return(false);
    }

    public void delete() {
	if(comp.poses == mp)
	    op.set(Composite.ipollen);
	del = true;
    }
}
