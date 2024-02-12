/* Preprocessed source code */
package haven.res.gfx.terobjs.roastspit;

import haven.*;
import haven.render.*;
import java.util.*;
import java.util.function.*;

/* >spr: Roastspit */
@haven.FromResource(name = "gfx/terobjs/roastspit", version = 55)
public class Roastspit extends SkelSprite {
    private RenderTree.Node equed;
    private Pipe.Op eqp;

    public Roastspit(Owner owner, Resource res, Message sdt) {
	super(owner, res, new MessageBuf(sdt.bytes(1)));
	equed = mkeq(sdt.uint16());
	update();
    }

    public void update(Message sdt) {
	byte[] b = sdt.bytes(1);
	equed = mkeq(sdt.uint16());
	super.update(new MessageBuf(b));
    }

    private RenderTree.Node mkeq(int eqid) {
	if(eqid == 65535)
	    return(null);
	Resource eqr = owner.context(Resource.Resolver.class).getres(eqid).get();
	Sprite eqs = Sprite.create(owner, eqr, Message.nil);
	Skeleton.BoneOffset bo = eqr.layer(Skeleton.BoneOffset.class, "s");
	return(RUtils.StateTickNode.from(eqs, bo.from(this)));
    }

    public void iparts(int mask, Collection<RenderTree.Node> rbuf, Collection<Runnable> tbuf, Collection<Consumer<Render>> gbuf) {
	super.iparts(mask, rbuf, tbuf, gbuf);
	if(equed != null)
	    rbuf.add(equed);
    }

	public String getContent() {
		return equed != null ? equed.toString() : null;
	}
}
