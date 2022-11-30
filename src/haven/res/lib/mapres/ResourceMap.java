/* Preprocessed source code */
package haven.res.lib.mapres;

import haven.*;
import java.util.*;

@FromResource(name = "lib/mapres", version = 2)
public class ResourceMap implements Resource.Resolver {
    public final Resource.Resolver bk;
    public final Map<Integer, Integer> map;

    public ResourceMap(Resource.Resolver bk, Map<Integer, Integer> map) {
	this.bk = bk;
	this.map = map;
    }

    public ResourceMap(Resource.Resolver bk, Message data) {
	this(bk, decode(data));
    }

    public static Map<Integer, Integer> decode(Message sdt) {
	if(sdt.eom())
	    return(Collections.emptyMap());
	int n = sdt.uint8();
	Map<Integer, Integer> ret = new HashMap<>();
	for(int i = 0; i < n; i++)
	    ret.put(sdt.uint16(), sdt.uint16());
	return(ret);
    }

    public Indir<Resource> getres(int id) {
	return(bk.getres(map.get(id)));
    }
}
