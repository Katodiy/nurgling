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

import java.awt.Color;
import java.awt.image.BufferedImage;
import static haven.PUtils.*;

public class Window extends Widget implements DTarget {
    public static final Tex bg = Resource.loadtex("nurgling/hud/wnd/lg/bg");
    public static final Tex bgl = Resource.loadtex("nurgling/hud/wnd/lg/bgl");
    public static final Tex bgr = Resource.loadtex("nurgling/hud/wnd/lg/bgr");
    public static final Tex cl = Resource.loadtex("nurgling/hud/wnd/lg/cl");
    public static final TexI cm = new TexI(Resource.loadsimg("nurgling/hud/wnd/lg/cm"));
    public static final Tex cr = Resource.loadtex("nurgling/hud/wnd/lg/cr");
    public static final Tex tm = Resource.loadtex("nurgling/hud/wnd/lg/tm");
    public static final Tex tr = Resource.loadtex("nurgling/hud/wnd/lg/tr");
    public static final Tex lm = Resource.loadtex("nurgling/hud/wnd/lg/lm");
    public static final Tex lb = Resource.loadtex("nurgling/hud/wnd/lg/lb");
    public static final Tex rm = Resource.loadtex("nurgling/hud/wnd/lg/rm");
    public static final Tex bl = Resource.loadtex("nurgling/hud/wnd/lg/bl");
    public static final Tex bm = Resource.loadtex("nurgling/hud/wnd/lg/bm");
    public static final Tex br = Resource.loadtex("nurgling/hud/wnd/lg/br");
    public static final Tex sizer = Resource.loadtex("nurgling/hud/wnd/sizer");
    public static final Coord tlm = UI.scale(18, 30);
    public static final Coord brm = UI.scale(13, 22);
    public static final Coord cpo = UI.rscale(36, 12.4);
    public static final int capo = 7, capio = 2;
    public static final Coord dlmrgn = UI.scale(23, 14);
    public static final Coord dsmrgn = UI.scale(9, 9);
    public static final BufferedImage ctex = Resource.loadimg("nurgling/hud/fonttex");
    public static final Text.Furnace cf = new Text.Imager(new PUtils.TexFurn(new Text.Foundry(Text.sans, 15).aa(true), ctex)) {
	    protected BufferedImage proc(Text text) {
		// return(rasterimg(blurmask2(text.img.getRaster(), 1, 1, Color.BLACK)));
		return(rasterimg(blurmask2(text.img.getRaster(), UI.rscale(0.75), UI.rscale(1.0), Color.BLACK)));
	    }
	};
    public static final IBox wbox = new IBox("gfx/hud/wnd", "tl", "tr", "bl", "br", "extvl", "extvr", "extht", "exthb") {
	    final Coord co = UI.scale(3, 3), bo = UI.scale(2, 2);

	    public Coord btloff() {return(super.btloff().sub(bo));}
	    public Coord ctloff() {return(super.ctloff().sub(co));}
	    public Coord bbroff() {return(super.bbroff().sub(bo));}
	    public Coord cbroff() {return(super.cbroff().sub(co));}
	    public Coord bisz() {return(super.bisz().sub(bo.mul(2)));}
	    public Coord cisz() {return(super.cisz().sub(co.mul(2)));}
	};
    private static final BufferedImage[] cbtni = new BufferedImage[] {
	Resource.loadsimg("nurgling/hud/wnd/lg/cbtnu"),
	Resource.loadsimg("nurgling/hud/wnd/lg/cbtnd"),
	Resource.loadsimg("nurgling/hud/wnd/lg/cbtnh")};
    public final Coord tlo, rbo, mrgn;
    public final IButton cbtn;
    public boolean dt = false;
    public Text cap;
    public Coord wsz, ctl, csz, atl, asz, cptl, cpsz;
    public int cmw;
    private UI.Grab dm = null;
    private Coord doff;
    public boolean decohide = false;

    @RName("wnd")
    public static class $_ implements Factory {
	public Widget create(UI ui, Object[] args) {
	    Coord sz = UI.scale((Coord)args[0]);
	    String cap = (args.length > 1) ? (String)args[1] : null;
	    boolean lg = (args.length > 2) ? ((Integer)args[2] != 0) : false;
	    return(new Window(sz, cap, lg, Coord.z, Coord.z));
	}
    }

    public Window(Coord sz, String cap, boolean lg, Coord tlo, Coord rbo) {
	this.tlo = tlo;
	this.rbo = rbo;
	this.mrgn = lg ? dlmrgn : dsmrgn;
	cbtn = add(new IButton(cbtni[0], cbtni[1], cbtni[2]));
	chcap(cap);
	resize2(sz);
	setfocustab(true);
    }

    public Window(Coord sz, String cap, boolean lg) {
	this(sz, cap, lg, Coord.z, Coord.z);
    }

    public Window(Coord sz, String cap) {
	this(sz, cap, false);
    }

    protected void added() {
	parent.setfocus(this);
    }

    public void chcap(String cap) {
	if(cap == null)
	    this.cap = null;
	else
	    this.cap = cf.render(cap);
    }

    public void cdraw(GOut g) {
    }

    protected void drawbg(GOut g) {
	Coord bgc = new Coord();
	Coord cbr = ctl.add(csz);
	for(bgc.y = ctl.y; bgc.y < cbr.y; bgc.y += bg.sz().y) {
	    for(bgc.x = ctl.x; bgc.x < cbr.x; bgc.x += bg.sz().x)
		g.image(bg, bgc, ctl, cbr);
	}
	bgc.x = ctl.x;
	for(bgc.y = ctl.y; bgc.y < cbr.y; bgc.y += bgl.sz().y)
	    g.image(bgl, bgc, ctl, cbr);
	bgc.x = cbr.x - bgr.sz().x;
	for(bgc.y = ctl.y; bgc.y < cbr.y; bgc.y += bgr.sz().y)
	    g.image(bgr, bgc, ctl, cbr);
    }

    protected void drawframe(GOut g) {
	Coord mdo, cbr;
	g.image(cl, tlo);
	mdo = tlo.add(cl.sz().x, 0);
	cbr = mdo.add(cmw, cm.sz().y);
	for(int x = 0; x < cmw; x += cm.sz().x)
	    g.image(cm, mdo.add(x, 0), Coord.z, cbr);
	g.image(cr, tlo.add(cl.sz().x + cmw, 0));
	g.image(cap.tex(), tlo.add(cpo));
	mdo = tlo.add(cl.sz().x + cmw + cr.sz().x, 0);
	cbr = tlo.add(wsz.add(-tr.sz().x, tm.sz().y));
	for(; mdo.x < cbr.x; mdo.x += tm.sz().x)
	    g.image(tm, mdo, Coord.z, cbr);
	g.image(tr, tlo.add(wsz.x - tr.sz().x, 0));

	mdo = tlo.add(0, cl.sz().y);
	cbr = tlo.add(lm.sz().x, wsz.y - bl.sz().y);
	if(cbr.y - mdo.y >= lb.sz().y) {
	    cbr.y -= lb.sz().y;
	    g.image(lb, new Coord(tlo.x, cbr.y));
	}
	for(; mdo.y < cbr.y; mdo.y += lm.sz().y)
	    g.image(lm, mdo, Coord.z, cbr);

	mdo = tlo.add(wsz.x - rm.sz().x, tr.sz().y);
	cbr = tlo.add(wsz.x, wsz.y - br.sz().y);
	for(; mdo.y < cbr.y; mdo.y += rm.sz().y)
	    g.image(rm, mdo, Coord.z, cbr);

	g.image(bl, tlo.add(0, wsz.y - bl.sz().y));
	mdo = tlo.add(bl.sz().x, wsz.y - bm.sz().y);
	cbr = tlo.add(wsz.x - br.sz().x, wsz.y);
	for(; mdo.x < cbr.x; mdo.x += bm.sz().x)
	    g.image(bm, mdo, Coord.z, cbr);
	g.image(br, tlo.add(wsz.sub(br.sz())));
    }

    protected void drawwnd(GOut g) {
	if(!decohide)
	    drawbg(g);
	cdraw(g.reclip(atl, asz));
	if(!decohide)
	    drawframe(g);
    }

    public void draw(GOut g) {
	drawwnd(g);
	super.draw(g);
    }

    public Coord contentsz() {
	Coord max = new Coord(0, 0);
	for(Widget wdg = child; wdg != null; wdg = wdg.next) {
	    if(wdg == cbtn)
		continue;
	    if(!wdg.visible)
		continue;
	    Coord br = wdg.c.add(wdg.sz);
	    if(br.x > max.x)
		max.x = br.x;
	    if(br.y > max.y)
		max.y = br.y;
	}
	return(max);
    }

    private void resize2(Coord sz) {
	asz = sz;
	csz = asz.add(mrgn.mul(2));
	wsz = csz.add(tlm).add(brm);
	this.sz = wsz.add(tlo).add(rbo);
	ctl = tlo.add(tlm);
	atl = ctl.add(mrgn);
	cmw = (cap == null) ? 0 : cap.sz().x;
	cmw = Math.max(cmw, wsz.x / 4);
	cptl = new Coord(ctl.x, tlo.y);
	cpsz = tlo.add(cpo.x + cmw, cm.sz().y).sub(cptl);
	cmw = cmw - (cl.sz().x - cpo.x) - UI.scale(5);
	cbtn.c = xlate(tlo.add(wsz.x - cbtn.sz.x - UI.scale(7),  UI.scale(-2)), false);
	for(Widget ch = child; ch != null; ch = ch.next)
	    ch.presize();
    }

    public void resize(Coord sz) {
	resize2(sz);
    }

    public void decohide(boolean h) {
	this.decohide = h;
	cbtn.show(!h);
    }

    public boolean decohide() {
	return(decohide);
    }

    public void uimsg(String msg, Object... args) {
	if(msg == "pack") {
		pack();
	}else if(msg == "dt") {
	    dt = (Integer)args[0] != 0;
	} else if(msg == "cap") {
	    String cap = (String)args[0];
	    chcap(cap.equals("") ? null : cap);
	} else if(msg == "dhide") {
	    decohide((Integer)args[0] != 0);
	} else {
	    super.uimsg(msg, args);
	}
    }

    public Coord xlate(Coord c, boolean in) {
	if(in)
	    return(c.add(atl));
	else
	    return(c.sub(atl));
    }

    public void drag(Coord off) {
	dm = ui.grabmouse(this);
	doff = off;
    }

    public boolean checkhit(Coord c) {
	if(decohide)
	    return(c.isect(atl, asz));
	Coord cpc = c.sub(cptl);
	return(c.isect(ctl, csz) || (c.isect(cptl, cpsz) && (cm.back.getRaster().getSample(cpc.x % cm.back.getWidth(), cpc.y, 3) >= 128)));
    }

    public boolean mousedown(Coord c, int button) {
	if(super.mousedown(c, button)) {
	    parent.setfocus(this);
	    raise();
	    return(true);
	}
	if(!decohide) {
	    if(checkhit(c)) {
		if(button == 1)
		    drag(c);
		parent.setfocus(this);
		raise();
		return(true);
	    }
	}
	return(false);
    }

    public boolean mouseup(Coord c, int button) {
	if(dm != null) {
	    dm.remove();
	    dm = null;
	} else {
	    super.mouseup(c, button);
	}
	return(true);
    }

    public void mousemove(Coord c) {
	if(dm != null) {
	    this.c = this.c.add(c.add(doff.inv()));
	} else {
	    super.mousemove(c);
	}
    }

    public boolean mousehover(Coord c) {
	super.mousehover(c);
	return(true);
    }

    public void wdgmsg(Widget sender, String msg, Object... args) {
	if(sender == cbtn) {
		if(justclose)
			close();
		else
			wdgmsg("close");
	} else {
	    super.wdgmsg(sender, msg, args);
	}
    }

	public boolean justclose = false;
	public void close(){
		ui.destroy(this);
	}

    public boolean keydown(java.awt.event.KeyEvent ev) {
	if(super.keydown(ev))
	    return(true);
	if(key_esc.match(ev)) {
	    wdgmsg("close");
	    return(true);
	}
	return(false);
    }

    public boolean drop(Coord cc, Coord ul) {
	if(dt) {
	    wdgmsg("drop", cc);
	    return(true);
	}
	return(false);
    }

    public boolean iteminteract(Coord cc, Coord ul) {
	return(false);
    }

    public Object tooltip(Coord c, Widget prev) {
	if(!checkhit(c))
	    return(super.tooltip(c, prev));
	Object ret = super.tooltip(c, prev);
	if(ret != null)
	    return(ret);
	else
	    return("");
    }

    public static void main(String[] args) {
	Window wnd = new Window(new Coord(300, 200), "Inventory", true);
	new haven.rs.DrawBuffer(haven.rs.Context.getdefault().env(), new Coord(512, 512))
	    .draw(g -> {
		    wnd.draw(g);
		    g.getimage(img -> Debug.dumpimage(img, args[0]));
	    });
    }
}
