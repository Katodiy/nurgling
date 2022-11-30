/* Preprocessed source code */
/* $use: lib/mapres */

package haven.res.lib.itemtex;

import haven.*;
import haven.render.*;
import haven.render.sl.InstancedUniform;
import haven.res.gfx.invobjs.meat.Meat;
import haven.res.lib.layspr.Image;
import haven.res.lib.layspr.Layered;
import haven.res.lib.mapres.ResourceMap;
import nurgling.NGob;
import nurgling.NUtils;
import nurgling.tools.AreasID;

import java.util.*;
import java.awt.image.BufferedImage;

@FromResource(name = "lib/itemtex", version = 2)
public class ItemTex {
    public static boolean isInited = false;

    public static class Icon implements GSprite.Owner, Resource.Resolver {
	public final Resource res;
	final Resource.Resolver pool;

	Icon(Resource res, Resource.Resolver pool) {
	    this.res = res;
	    this.pool = pool;
	}

	public Indir<Resource> getres(int id) {
	    return(null);
	}

	static final ClassResolver<Icon> rsv = new ClassResolver<Icon>()
	    .add(Resource.Resolver.class, ico -> ico.pool);
	public <C> C context(Class<C> cl) {return(rsv.context(cl, this));}
	public Resource getres() {return(res);}
	public Random mkrandoom() {return(new Random());}
    }
    
    public static GSprite mkspr(OwnerContext owner, Message sdt) {
	int resid = sdt.uint16();
	Message isdt = Message.nil;
	if((resid & 0x8000) != 0) {
	    resid &= ~0x8000;
	    isdt = new MessageBuf(sdt.bytes(sdt.uint8()));
	}
	Resource ires = owner.context(Resource.Resolver.class).getres(resid).get();
	GSprite.Owner ctx = new Icon(ires, new ResourceMap(owner.context(Resource.Resolver.class), sdt));
	return(GSprite.create(ctx, ires, isdt));
    }

    public static BufferedImage sprimg(GSprite spr) {
	if(spr instanceof GSprite.ImageSprite)
	    return(((GSprite.ImageSprite)spr).image());
	return(spr.owner.getres().layer(Resource.imgc).img);
    }

    public static final Map<MessageBuf, BufferedImage> made = new CacheMap<>();
    public static final Map<String, MessageBuf> made_str = new CacheMap<>();
    public static final Map<String, Long> made_id = new CacheMap<>();
    public static BufferedImage create(OwnerContext owner, Message osdt) {
		MessageBuf copy = new MessageBuf(osdt.bytes());
		synchronized (made) {
			BufferedImage ret = made.get(copy);
			if (ret == null) {
				GSprite sprite = mkspr(owner, copy.clone());
				ret = sprimg(sprite);
				if (sprite instanceof Layered) {
					StringBuilder resName = new StringBuilder("layers:");
					for (haven.res.lib.layspr.Layer lay : ((Layered) sprite).lay) {
						if (lay instanceof Image) {
							resName.append("&").append(((Image) lay).img.getres().name).append(";");
						}
					}
					made_str.put(resName.toString(), copy);
					made_id.put(resName.toString(), NGob.calcMarker(made_str.get(resName.toString())));

				} else {
					made_str.put(sprite.owner.getres().name.substring(12), copy);
					made_id.put(sprite.owner.getres().name.substring(12), NGob.calcMarker(made_str.get(sprite.owner.getres().name.substring(12))));
				}
				made.put(copy, ret);
			}
			return (ret);
		}
	}

    public static BufferedImage fixsz(BufferedImage img) {
	Coord sz = PUtils.imgsz(img);
	int msz = Math.max(sz.x, sz.y);
	int nsz = Math.max((int)Math.round(Math.pow(2, Math.round(Math.log(sz.x) / Math.log(2)))),
			   (int)Math.round(Math.pow(2, Math.round(Math.log(sz.y) / Math.log(2)))));
	BufferedImage ret = TexI.mkbuf(new Coord(nsz, nsz));
	java.awt.Graphics g = ret.getGraphics();
	int w = (sz.x * nsz) / msz, h = (sz.y * nsz) / msz;
	g.drawImage(img, (nsz - w) / 2, (nsz - h) / 2, (nsz + w) / 2, (nsz + h) / 2, 0, 0, sz.x, sz.y, null);
	g.dispose();
	return(ret);
    }

    public static final Map<BufferedImage, TexL> fixed = new CacheMap<>();
    public static TexL fixup(final BufferedImage img) {
	TexL tex;
	synchronized(fixed) {
	    tex = fixed.get(img);
	    if(tex == null) {
		BufferedImage fimg = img;
		Coord sz = PUtils.imgsz(fimg);
		if((sz.x != sz.y) || (sz.x != Tex.nextp2(sz.x)) || (sz.y != Tex.nextp2(sz.y))) {
		    fimg = fixsz(fimg);
		    sz = PUtils.imgsz(fimg);
		}
		final BufferedImage timg = fimg;
		tex = new TexL(sz) {
			public BufferedImage fill() {
			    return(timg);
			}
		    };
		tex.mipmap(Mipmapper.dav);
		tex.img.magfilter(Texture.Filter.LINEAR).minfilter(Texture.Filter.LINEAR).mipfilter(Texture.Filter.LINEAR);
		fixed.put(img, tex);
	    }
	}
	return(tex);
    }

	static LinkedList<Integer> unloaded = new LinkedList<>();
	public static GSprite mkspr(Message sdt) {
		int resid;
		Message isdt;
		try {
			resid = sdt.uint16();
			isdt = Message.nil;
			if ((resid & 0x8000) != 0) {
				resid &= ~0x8000;
				isdt = new MessageBuf(sdt.bytes(sdt.uint8()));
			}
		} catch (Message.EOF e) {
			return null;
		}
		try {
			if (NUtils.getUI().sess != null) {
				Resource ires = null;
				try {
					ires = NUtils.getUI().sess.getres(resid).get();
				} catch (Exception l) {
					unloaded.add(resid);
				}
				if (ires != null && isdt!=Message.nil) {
					GSprite.Owner ctx = new Icon(ires, new ResourceMap(NUtils.getUI().sess, sdt));
					return (GSprite.create(ctx, ires, isdt));
				}

			}
		} catch (Sprite.ResourceException  e) {
		}
		return null;
	}


	public static void tryLoad() {

		MessageBuf ms = new MessageBuf() {
			public boolean underflow(int hint) {
				return (false);
			}

			public void overflow(int min) {
				throw (new RuntimeException("nil message is not writable"));
			}

			public String toString() {
				return ("Message(nil)");
			}
		};
		ms.rt = 2;
		for (byte i = -23; i < 127; i++) {
			for (byte j = 9; j < 127; j++) {
				ms.rbuf = new byte[2];
				ms.rbuf[0] = i;
				ms.rbuf[1] = j;
				ms.rh = 0;
				ItemTex.create(ms);
			}
		}
		ItemTex.isInited = true;


	}

	public static BufferedImage create(Message osdt) {
		MessageBuf copy = new MessageBuf(osdt.bytes());
		synchronized(made) {
			BufferedImage ret = made.get(copy);
			//if(ret == null) {
				GSprite sprite = mkspr(copy.clone());
				if(sprite!=null) {
					if((sprite.owner.getres().name.contains("gfx/invobjs/")||sprite.owner.getres().name.contains("paginae/bld/"))) {
						String name = sprite.owner.getres().name.substring(12);
						made_str.put(name, copy);
						made_id.put(name, NGob.calcMarker(made_str.get(name)));
						made.put(copy,sprimg(sprite));
					}
				}
		//	}
			return ret;
		}
	}

	public static BufferedImage find(String name){
		try {
			if(name!=null) {
				if (name.contains("layers:")) {
					String first = name.substring(name.indexOf("&") + 1, name.indexOf(";"));
					String second = name.substring(name.lastIndexOf("&") + 1, name.lastIndexOf(";"));

					LinkedList<Indir<Resource>> lay = new LinkedList<>();
					lay.add(Resource.remote().loadwait(first).indir());
					lay.add(Resource.remote().loadwait(second).indir());

					Layered layered = new Layered(null, lay);
					return layered.image();
				}
			}

			Resource res = Resource.remote().loadwait("gfx/invobjs/" + name);
			if(res!=null)
				return res.layer(Resource.imgc).scaled();
		}catch (Resource.NoSuchResourceException e){
			try {
				Resource res = Resource.remote().loadwait("paginae/bld/" + name);
				if(res!=null)
					return res.layer(Resource.imgc).scaled();
			}catch (Resource.NoSuchResourceException ex){
				if(NUtils.getGameUI()!=null) {
					NUtils.getGameUI().msg("INCORRECT NAME");
				}
			}
		}
		return null;

	}

	public static BufferedImage find(AreasID id){
		String resstr = AreasID.get(id);
		if(resstr!=null) {
			if(resstr.contains("layers:")){
				String first = resstr.substring(resstr.indexOf("&")+1,resstr.indexOf(";"));
				String second = resstr.substring(resstr.lastIndexOf("&")+1,resstr.lastIndexOf(";"));

				LinkedList<Indir<Resource>> lay = new LinkedList<>();
				lay.add(Resource.remote().loadwait(first).indir());
				lay.add(Resource.remote().loadwait(second).indir());

				Layered layered = new Layered(null,lay);
				return layered.image();
			}
			try {
				Resource res = Resource.remote().loadwait("gfx/invobjs/" + resstr);
				if (res != null)
					return res.layer(Resource.imgc).scaled();
			} catch (Resource.NoSuchResourceException e) {
				try {
					Resource res = Resource.remote().loadwait("paginae/bld/" + resstr);
					if (res != null)
						return res.layer(Resource.imgc).scaled();
				} catch (Resource.NoSuchResourceException ex) {
				}
			}
		}
		return null;
	}
}
