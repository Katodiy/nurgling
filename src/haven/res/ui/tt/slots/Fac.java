package haven.res.ui.tt.slots;/* Preprocessed source code */
/* $use: lib/tspec */

import haven.*;

import java.util.*;

/* >tt: haven.res.ui.tt.slots.Fac */
@haven.FromResource(name = "ui/tt/slots", version = 28)
public class Fac implements ItemInfo.InfoFactory {
    public ItemInfo build(ItemInfo.Owner owner, ItemInfo.Raw rawi, Object... args) {
	Resource.Resolver rr = owner.context(Resource.Resolver.class);
	int a = 1;
	double pmin = ((Number)args[a++]).doubleValue();
	double pmax = ((Number)args[a++]).doubleValue();
	List<Resource> attrs = new LinkedList<Resource>();
	while(args[a] != null)
	    attrs.add(rr.getres((Integer)args[a++]).get());
	a++;
	int left = (Integer)args[a++];
	ISlots ret = new ISlots(owner, left, pmin, pmax, attrs.toArray(new Resource[0]));
	while(a < args.length) {
	    Indir<Resource> res = rr.getres((Integer)args[a++]);
	    Message sdt = Message.nil;
	    if(args[a] instanceof byte[])
		sdt = new MessageBuf((byte[])args[a++]);
	    Object[] raw = (Object[])args[a++];
	    ret.s.add(ret.new SItem(new ResData(res, sdt), raw));
	}
	return(ret);
    }
}
