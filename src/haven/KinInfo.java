/*
 *  This file is part of the Haven & Hearth game client.
 *  Copyright (C) 2009 Fredrik Tolf <fredrik@dolda2000.com>, and
 *                     Björn Johannessen <johannessen.bjorn@gmail.com>
 *
 *  Redistribution and/or modification of this file is subject to the
 *  terms of the GNU Lesser General Public License, version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Other parts of this source tree adhere to other copying
 *  rights. Please see the file `COPYING' in the root directory of the
 *  source tree for details.
 *
 *  A copy the GNU Lesser General Public License is distributed along
 *  with the source tree of which this file is a part in the file
 *  `doc/LPGL-3'. If it is missing for any reason, please see the Free
 *  Software Foundation's website at <http://www.fsf.org/>, or write
 *  to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *  Boston, MA 02111-1307 USA
 */

package haven;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Color;
import haven.render.*;
import nurgling.*;

public class KinInfo extends GAttrib implements RenderTree.Node, PView.Render2D {
    public static final BufferedImage vlg = Resource.loadimg("gfx/hud/vilind");
    public static final Text.Foundry nfnd = new Text.Foundry(Text.dfont, 10);
    public String name;
    public int group, type;
    public double seen = 0;
    private Tex rnm = null;
    
    public KinInfo(Gob g, String name, int group, int type) {
	super(g);
	this.name = name;
	this.group = group;
	this.type = type;
    }
    
    public void update(String name, int group, int type) {
	this.name = name;
	this.group = group;
	this.type = type;
	rnm = null;
    }
    
    public Tex rendered() {
	if(rnm == null) {
	    boolean hv = (type & 2) != 0;
	    BufferedImage nm = null;
	    if(name.length() > 0)
		nm = Utils.outline2(nfnd.render(name, BuddyWnd.gc[group]).img, Utils.contrast(BuddyWnd.gc[group]));
	    int w = 0, h = 0;
	    if(nm != null) {
		w += nm.getWidth();
		if(nm.getHeight() > h)
		    h = nm.getHeight();
	    }
	    if(hv) {
		w += vlg.getWidth() + 1;
		if(vlg.getHeight() > h)
		    h = vlg.getHeight();
	    }
	    if(w == 0) {
		rnm = new TexI(TexI.mkbuf(new Coord(1, 1)));
	    } else {
		BufferedImage buf = TexI.mkbuf(new Coord(w, h));
		Graphics g = buf.getGraphics();
		int x = 0;
		if(hv) {
		    g.drawImage(vlg, x, (h / 2) - (vlg.getHeight() / 2), null);
		    x += vlg.getWidth() + 1;
		}
		if(nm != null) {
		    g.drawImage(nm, x, (h / 2) - (nm.getHeight() / 2), null);
		    x += nm.getWidth();
		}
		g.dispose();
		rnm = new TexI(buf);
	    }
	}
	return(rnm);
    }
    
    public void draw(GOut g, Pipe state) {
	Coord sc = Homo3D.obj2view(new Coord3f(0, 0, 15), state, Area.sized(g.sz())).round2();
	if(sc.isect(Coord.z, g.sz())) {
	    double now = Utils.rtime();
	    if(seen == 0)
		seen = now;
	    double tm = now - seen;
	    Color show = null;
	    boolean auto = (type & 1) == 0;
	    if(false) {
		/* XXX: QQ, RIP in peace until constant
		 * mouse-over checks can be had. */
		if(auto && (tm < 7.5)) {
		    show = Utils.clipcol(255, 255, 255, (int)(255 - ((255 * tm) / 7.5)));
		}
	    } else {
		show = Color.WHITE;
	    }
	    if(show != null) {
		Tex t = rendered();
		if(t != null) {
		    g.chcolor(show);
		    g.aimage(t, sc, 0.5, 1.0);
		    g.chcolor();
		}
	    }
	} else {
	    seen = 0;
	}
    }

    @OCache.DeltaType(OCache.OD_BUDDY)
    public static class $buddy implements OCache.Delta {
	public void apply(Gob g, Message msg) {
	    String name = msg.string();
	    if(name.length() > 0) {
		int group = msg.uint8();
		int btype = msg.uint8();
		KinInfo b = g.getattr(KinInfo.class);
		if(b == null) {
		    g.setattr(new KinInfo(g, name, group, btype));
		} else {
		    b.update(name, group, btype);
		}
		if (NUtils.getGameUI() != null && NUtils.getGameUI().map != null && g.tags.contains(NGob.Tags.notplayer)) {
			g.removeTag(NGob.Tags.unknown, NGob.Tags.notmarked, NGob.Tags.foe);

			g.removeol(NMarkedRing.class);
			g.removeol(NTargetRing.class);
			Gob player = NOCache.getgob(NGob.Tags.player);
			if (player != null) {
				for (Gob.Overlay ol : player.ols) {
					if (ol.spr instanceof NDirArrow) {
						if (((NDirArrow) ol.spr).target.id == g.id) {
							ol.remove();
							break;
						}
					}
				}
			}
			if (group == 0) {
				g.addTag(NGob.Tags.unknown, NGob.Tags.notmarked);
			} else if (group == 2) {
				g.addTag(NGob.Tags.foe, NGob.Tags.notmarked);
			}
		}
	    } else {
		g.delattr(KinInfo.class);
	    }
	}
    }

	public boolean isVillager() {return (type & 2) != 0;}
	public static boolean isFoe(Gob gob) {
		if(gob != null) {
			KinInfo ki = gob.getattr(KinInfo.class);
			if(ki != null) {
				//mark as foe if in RED(2) group or WHITE(0) and not villager
				return ki.group == 2 || ki.group == 0 && !ki.isVillager();
			}
		}
		return true;
	}

	public static int getGroup(Gob gob) {
		if(gob != null) {
			KinInfo ki = gob.getattr(KinInfo.class);
			if(ki != null) {
				return ki.group;
			}
		}
		return -1;
	}
}
