/* Preprocessed source code */
/* $use: ui/tt/defn */

package haven.res.gfx.invobjs.gems.gemstone;
import java.awt.image.*;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Random;
import haven.*;
import static haven.PUtils.*;

/* >ispr: Gemstone */
@haven.FromResource(name = "gfx/invobjs/gems/gemstone", version = 50)
public class Gemstone extends GSprite implements GSprite.ImageSprite, haven.res.ui.tt.defn.DynName {
    public  BufferedImage img = null;
    public  Tex tex = null;
    public  String name = "";

    public Gemstone(Owner owner, Resource res, Message sdt) {
	super(owner);
	Resource.Resolver rr = owner.context(Resource.Resolver.class);
	if(!sdt.eom()) {
	    Resource cut = rr.getres(sdt.uint16()).get();
	    int texid = sdt.uint16();
	    if(texid != 65535) {
		Resource tex = rr.getres(texid).get();
		this.tex = new TexI(this.img = construct(cut, tex));
		name = cut.layer(Resource.tooltip).t + " " + tex.layer(Resource.tooltip).t;
	    } else {
		this.tex = new TexI(this.img = construct(cut, null));
		name = cut.layer(Resource.tooltip).t + " Gemstone";
	    }
	} else {
	    this.tex = new TexI(this.img = TexI.mkbuf(new Coord(32, 32)));
	    name = "Broken gem";
	}
    }

    public static BufferedImage convert(BufferedImage img) {
	WritableRaster buf = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, img.getWidth(), img.getHeight(), 4, null);
	BufferedImage tgt = new BufferedImage(TexI.glcm, buf, false, null);
	Graphics g = tgt.createGraphics();
	g.drawImage(img, 0, 0, null);
	g.dispose();
	return(tgt);
    }

    public static final WritableRaster alphamod(WritableRaster dst) {
	int w = dst.getWidth(), h = dst.getHeight();
	for(int y = 0; y < h; y++) {
	    for(int x = 0; x < w; x++) {
		dst.setSample(x, y, 3, (dst.getSample(x, y, 3) * 3) / 4);
	    }
	}
	return(dst);
    }

    public static final WritableRaster alphasq(WritableRaster dst) {
	int w = dst.getWidth(), h = dst.getHeight();
	for(int y = 0; y < h; y++) {
	    for(int x = 0; x < w; x++) {
		int a = dst.getSample(x, y, 3);
		dst.setSample(x, y, 3, (a * a) / 255);
	    }
	}
	return(dst);
    }

	final static HashMap<String, BufferedImage> texCache = new HashMap<>();

    public static BufferedImage construct(Resource cut, Resource tex) {
	Resource.Image outl, body, hili;
	BufferedImage outli, bodyi, hilii;
	try {
	    outl = cut.layer(Resource.imgc, 0);
	    body = cut.layer(Resource.imgc, 1);
	    hili = cut.layer(Resource.imgc, 2);
	    outli = convert(outl.scaled());
	    bodyi = convert(body.scaled());
	    hilii = convert(hili.scaled());
	    Coord sz = UI.scale(body.tsz);
	    WritableRaster buf = imgraster(sz);
	    Coord o = UI.scale(outl.o);
	    o = new Coord(Utils.clip(o.x, 0, sz.x - outli.getWidth()), Utils.clip(o.y, 0, sz.y - outli.getHeight()));
	    blit(buf, outli.getRaster(), o);
	    WritableRaster buf2 = imgraster(sz);
	    o = UI.scale(body.o);
	    o = new Coord(Utils.clip(o.x, 0, sz.x - bodyi.getWidth()), Utils.clip(o.y, 0, sz.y - bodyi.getHeight()));
	    blit(buf2, bodyi.getRaster(), o);
	    o = UI.scale(hili.o);
	    o = new Coord(Utils.clip(o.x, 0, sz.x - hilii.getWidth()), Utils.clip(o.y, 0, sz.y - hilii.getHeight()));
	    alphablit(buf2, hilii.getRaster(), o);
	    if(tex != null) {
		BufferedImage texi;
		if(texCache.get(tex.name)==null) {
			texi = ((TexL) tex.layer(TexR.class).tex()).fill();
			texi = convolvedown(texi, sz.mul(2), new Lanczos(3));
			texCache.put(tex.name, texi);
		}
		else
		{
			texi =texCache.get(tex.name);
		}
		tilemod(buf2, texi.getRaster(), Coord.z);
	    }
	    // alphamod(buf2);
	    alphablit(buf2, alphasq(blit(imgraster(imgsz(hilii)), hilii.getRaster(), Coord.z)), UI.scale(hili.o));
	    alphablit(buf, buf2, Coord.z);
	    return(rasterimg(buf));
	} catch(RuntimeException e) {
	    throw(new RuntimeException(String.format("invalid gemstone in %s (using %s)", (cut == null) ? "null" : cut.name, (cut == null) ? null : tex.name), e));
	}
    }

    public Coord sz() {
	return(imgsz(img));
    }

    public void draw(GOut g) {
	g.image(tex, Coord.z);
    }

    public String name() {
	return(name);
    }

    public BufferedImage image() {
	return(img);
    }
}
