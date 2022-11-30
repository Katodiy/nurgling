/* Preprocessed source code */
/* $use: lib/layspr */
/* $use: ui/tt/defn */

package haven.res.gfx.invobjs.meat;

import haven.*;
import haven.res.lib.layspr.*;
import java.util.*;

/* >ispr: Meat */
@FromResource(name = "gfx/invobjs/meat", version = 22)
public class Meat extends Layered implements haven.res.ui.tt.defn.DynName {
    public final String name;

    private static String ncomb(String a, String b) {
	int p = a.indexOf('%');
	if(p < 0) {
	    if(b.indexOf('%') >= 0)
		return(ncomb(b, a));
	    return(a);
	}
	return(a.substring(0, p) + b + a.substring(p + 1));
    }

    private Meat(Owner owner, List<Indir<Resource>> lay) {
	super(owner, lay);
	String cn = null;
	for(Indir<Resource> res : lay) {
	    Resource.Tooltip tt = res.get().layer(Resource.tooltip);
	    if(tt == null)
		continue;
	    if(cn == null)
		cn = tt.t;
	    else
		cn = ncomb(cn, tt.t);
	}
	if(cn != null) {
	    int p = cn.indexOf('%');
	    if(p >= 0)
		cn = cn.substring(0, p).trim() + " " + cn.substring(p + 1).trim();
	    name = cn;
	} else {
	    name = "Meat";
	}
    }

    public Meat(Owner owner, Resource res, Message sdt) {
	this(owner, decode(owner.context(Resource.Resolver.class), sdt));
    }

    public String name() {
	return(name);
    }
}
