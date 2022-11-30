/* Preprocessed source code */
/* $use: ui/tt/defn */

package haven.res.lib.layspr;

import haven.*;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.*;

@FromResource(name = "lib/layspr", version = 14)
public class Layered extends GSprite implements GSprite.ImageSprite {
    public final Layer[] lay;
    final Coord sz;

    public static List<Indir<Resource>> decode(Resource.Resolver rr, Message sdt) {
	List<Indir<Resource>> ret = new ArrayList<Indir<Resource>>();
	while(!sdt.eom())
	    ret.add(rr.getres(sdt.uint16()));
	return(ret);
    }

    public Layered(Owner owner, Collection<Indir<Resource>> lres) {
	super(owner);
	List<Layer> lay = new ArrayList<Layer>(lres.size());
	for(Indir<Resource> res : lres) {
	    boolean f = false;
	    for(Resource.Anim anim : res.get().layers(Resource.animc)) {
		f = true;
		lay.add(new Animation(anim));
	    }
	    if(f)
		continue;
	    for(Resource.Image img : res.get().layers(Resource.imgc))
		lay.add(new Image(img));
	}
	Collections.sort(lay, new Comparator<Layer>() {
		public int compare(Layer a, Layer b) {
		    return(a.z - b.z);
		}
	    });
	this.lay = lay.toArray(new Layer[0]);
	sz = new Coord();
	for(Layer l : this.lay) {
	    sz.x = Math.max(sz.x, l.sz.x);
	    sz.y = Math.max(sz.y, l.sz.y);
	}
	tick(Math.random() * 10);
    }

    public void draw(GOut g) {
	for(Layer l : lay)
	    l.draw(g);
    }

    public Coord sz() {
	return(sz);
    }

    public void tick(double dt) {
	for(Layer l : lay) {
	    if(l instanceof Animation)
		((Animation)l).tick(dt);
	}
    }

    public BufferedImage image() {
	Coord sz = sz();
	if((sz.x < 1) || (sz.y < 1))
	    return(null);
	BufferedImage img = TexI.mkbuf(sz);
	Graphics g = img.getGraphics();
	for(Layer l : lay) {
	    if(l instanceof Image) {
		Image il = (Image)l;
		g.drawImage(il.img.scaled(), il.img.o.x, il.img.o.y, null);
	    }
	}
	return(img);
    }
}

/* >ispr: haven.res.lib.layspr.BaseLayered */
