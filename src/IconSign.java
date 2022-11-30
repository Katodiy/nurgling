/* Preprocessed source code */
/* $use: lib/itemtex */

import haven.*;
import haven.render.*;
import haven.res.lib.itemtex.ItemTex;
import haven.res.lib.itemtex.ItemTex;
import nurgling.NUtils;

import java.util.*;
import java.awt.image.BufferedImage;

/* >spr: IconSign */
@FromResource(name = "gfx/terobjs/iconsign", version = 30)
public class IconSign implements Sprite.Factory {
    static final Material base = Resource.classres(IconSign.class).layer(Material.Res.class, 16).get();
    static final RenderTree.Node proj = Resource.classres(IconSign.class).layer(FastMesh.MeshRes.class, 0).m;

    public Sprite create(Sprite.Owner owner, Resource res, Message sdt) {
	Material sym = null;
	if(!sdt.eom()) {
	    BufferedImage img = ItemTex.create(owner, sdt);
	    if(img != null) {
		TexRender tex = ItemTex.fixup(img);
		sym = new Material(base, tex.draw, tex.clip);
	    }
	}
	RenderTree.Node[] parts = StaticSprite.lsparts(res, Message.nil);
	if(sym != null)
	    parts = Utils.extend(parts, sym.apply(proj));
	return(new StaticSprite(owner, res, parts));
    }
}
