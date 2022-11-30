/* Preprocessed source code */
/* $use: ui/tt/defn */

package haven.res.lib.layspr;

import haven.*;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.*;

@FromResource(name = "lib/layspr", version = 14)
public class BaseLayered extends Layered implements haven.res.ui.tt.defn.DynName {
    public final String name;

    public BaseLayered(Owner owner, List<Indir<Resource>> lay) {
	super(owner, lay);
	Resource nres = Utils.el(lay).get();
	Resource.Tooltip tt = nres.layer(Resource.tooltip);
	if(tt == null)
	    throw(new RuntimeException("Item resource " + nres + " is missing default tooltip"));
	name = tt.t;
    }

    public BaseLayered(Owner owner, Resource res, Message sdt) {
	this(owner, decode(owner.context(Resource.Resolver.class), sdt));
    }

    public String name() {
	return(name);
    }
}
