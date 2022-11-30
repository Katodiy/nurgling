package nurgling;

import haven.*;

public class NFightSess extends Fightsess {
    public static final Tex indframe = Resource.loadtex("gfx/hud/combat/indframe");
    public static final Tex indbframe = Resource.loadtex("gfx/hud/combat/indbframe");

    public NFightSess ( int nact ) {
        super ( nact );
        instance = this;
    }
    
    public static NFightSess instance = null;
    
    public int getMyIp(){
        return fv.current.ip;
    }
    
    public double lastUse(){
        return ( Utils.rtime () - fv.atkcs) / (fv.atkct - fv.atkcs);
    }
    
    public void useAction(int n, Coord2d mc)
            throws InterruptedException {
        wdgmsg("use", n, 1, (n>=5)?1:0, mc.floor( OCache.posres));
        Thread.sleep ( 50 );
        wdgmsg("rel", n);
    }

    @Override
    public void draw(GOut g) {
        updatepos();
        double now = Utils.rtime();

        for(Buff buff : fv.buffs.children(Buff.class))
            buff.draw(g.reclip(pcc.add(-buff.c.x - Buff.cframe.sz().x - UI.scale(20), buff.c.y + pho - Buff.cframe.sz().y), buff.sz));
        if(fv.current != null) {
            for(Buff buff : fv.current.buffs.children(Buff.class))
                buff.draw(g.reclip(pcc.add(buff.c.x + UI.scale(20), buff.c.y + pho - Buff.cframe.sz().y), buff.sz));

            g.aimage(ip.get().tex(), pcc.add(-UI.scale(75), 0), 1, 0.5);
            g.aimage(oip.get().tex(), pcc.add(UI.scale(75), 0), 0, 0.5);

            if(fv.lsrel.size() > 1)
                curtgtfx = fxon(fv.current.gobid, tgtfx, curtgtfx);
        }

        {
            Coord cdc = pcc.add(cmc);
            if(now < fv.atkct) {
                double a = (now - fv.atkcs) / (fv.atkct - fv.atkcs);
                g.chcolor(255, 0, 128, 224);
                g.fellipse(cdc, new Coord(22, 22), Math.PI / 2 - (Math.PI * 2 * Math.min(1.0 - a, 1.0)), Math.PI / 2);
                g.chcolor();
            }
            g.image(cdframe, cdc.sub(cdframe.sz().div(2)));
        }
        try {
            Indir<Resource> lastact = fv.lastact;
            if(lastact != this.lastact1) {
                this.lastact1 = lastact;
                this.lastacttip1 = null;
            }
            double lastuse = fv.lastuse;
            if(lastact != null) {
                Tex ut = lastact.get().layer(Resource.imgc).tex();
                Coord useul =  pcc.add(usec1).sub(ut.sz().div(2));
                g.image(ut, useul);
                g.image(useframe, useul.sub(useframeo));
                double a = now - lastuse;
                if(a < 1) {
                    Coord off = new Coord((int)(a * ut.sz().x / 2), (int)(a * ut.sz().y / 2));
                    g.chcolor(255, 255, 255, (int)(255 * (1 - a)));
                    g.image(ut, useul.sub(off), ut.sz().add(off.mul(2)));
                    g.chcolor();
                }
            }
        } catch(Loading l) {
        }
        if(fv.current != null) {
            try {
                Indir<Resource> lastact = fv.current.lastact;
                if(lastact != this.lastact2) {
                    this.lastact2 = lastact;
                    this.lastacttip2 = null;
                }
                double lastuse = fv.current.lastuse;
                if(lastact != null) {
                    Tex ut = lastact.get().layer(Resource.imgc).tex();
                    Coord useul = pcc.add(usec2).sub(ut.sz().div(2));
                    g.image(ut, useul);
                    g.image(useframe, useul.sub(useframeo));
                    double a = now - lastuse;
                    if(a < 1) {
                        Coord off = new Coord((int)(a * ut.sz().x / 2), (int)(a * ut.sz().y / 2));
                        g.chcolor(255, 255, 255, (int)(255 * (1 - a)));
                        g.image(ut, useul.sub(off), ut.sz().add(off.mul(2)));
                        g.chcolor();
                    }
                }
            } catch(Loading l) {
            }
        }
        for(int i = 0; i < actions.length; i++) {
            Coord ca =  pcc.add(actc(i));
            Action act = actions[i];
            try {
                if(act != null) {
                    Tex img = act.res.get().layer(Resource.imgc).tex();
                    Coord hsz = img.sz().div(2);
                    g.image(img, ca);
                    if(now < act.ct) {
                        double a = (now - act.cs) / (act.ct - act.cs);
                        g.chcolor(0, 0, 0, 132);
                        g.prect(ca.add(hsz), hsz.inv(), hsz, (1.0 - a) * Math.PI * 2);
                        g.chcolor();
                        g.aimage(Text.renderstroked(String.format("%.1f", act.ct - now)).tex(), ca.add(hsz.x, 0), 0.5, 0);
                    }

                    if(i == use) {
                        g.image(indframe, ca.sub(indframeo));
                    } else if(i == useb) {
                        g.image(indbframe, ca.sub(indbframeo));
                    } else {
                        g.image(actframe, ca.sub(actframeo));
                    }
                }
            } catch(Loading l) {}
        }
    }
}
