package haven.res.gfx.terobjs.items.gridiron;/* Preprocessed source code */
import haven.*;
import haven.render.*;
import java.util.*;
import java.util.function.*;

/* >spr: haven.res.gfx.terobjs.items.gridiron.Roastspit */
@haven.FromResource(name = "gfx/terobjs/items/gridiron", version = 9)
public class Roastspit extends SkelSprite {
    private static final String[] eqps = {"h0", "h1"};
    private RenderTree.Node[] equed = new RenderTree.Node[eqps.length];
	Resource res;
    private static MessageBuf fl(int fl) {
	int sfl = 0;
	if((fl & 1) != 0)
	    sfl |= 65536;
	if((fl & 2) != 0)
	    sfl |= 1;
	return(new MessageBuf(((MessageBuf)(new MessageBuf().addint32(sfl))).fin()));
    }

    public Roastspit(Owner owner, Resource res, Message sdt) {
	super(owner, res, new MessageBuf(sdt.eom() ? Message.nil : fl(sdt.uint8())));
	this.res = res;
	int[] eqid = new int[eqps.length];
	for(int i = 0; i < eqps.length; i++)
	    eqid[i] = sdt.eom() ? 65535 : sdt.uint16();
	equed = mkeq(eqid);
	update();
    }

    public void update(Message sdt) {
	MessageBuf b = new MessageBuf(sdt.eom() ? Message.nil : fl(sdt.uint8()));
	int[] eqid = new int[eqps.length];
	for(int i = 0; i < eqps.length; i++)
	    eqid[i] = sdt.eom() ? 65535 : sdt.uint16();
	equed = mkeq(eqid);
	super.update(b);
    }


    private RenderTree.Node[] mkeq(int[] eqid) {
	RenderTree.Node[] ret = new RenderTree.Node[eqps.length];
	for(int i = 0; i < eqps.length; i++) {
	    if(eqid[i] == 65535) {
		ret[i] = null;
	    } else {
		Resource eqr = owner.context(Resource.Resolver.class).getres(eqid[i]).get();
		Sprite eqs = Sprite.create(owner, eqr, Message.nil);
		Skeleton.BoneOffset bo = eqr.layer(Skeleton.BoneOffset.class, eqps[i]);
		if(bo == null)
		    bo = this.res.layer(Skeleton.BoneOffset.class, eqps[i]);
		ret[i] = RUtils.StateTickNode.from(eqs, bo.forpose(getpose()));
	    }
	}
	return(ret);
    }

    public void iparts(int mask, Collection<RenderTree.Node> rbuf, Collection<Runnable> tbuf, Collection<Consumer<Render>> gbuf) {
	super.iparts(mask, rbuf, tbuf, gbuf);
	if(equed != null) {	// XXXRENDER: This is kinda ugly.
	    for(int i = 0; i < eqps.length; i++) {
		if(equed[i] != null)
		    rbuf.add(equed[i]);
	    }
	}
    }

	public String getContent(){
			return res.name;
	}
}
