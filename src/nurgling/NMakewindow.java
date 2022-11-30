package nurgling;

import haven.*;
import haven.Button;
import haven.Label;
import haven.res.ui.tt.defn.DynName;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

import static haven.Inventory.invsq;

public class NMakewindow extends Widget {
    public static final Text.Foundry fnd = new Text.Foundry(Text.sans, 12);
    public static final Text qmodl = Text.render(("Quality:"));
    public static final Text tooll = Text.render(("Tools:"));
    public static final Coord boff = UI.scale(new Coord(7, 9));
    public String rcpnm;
    public List<Spec> inputs = Collections.emptyList();
    public List<Spec> outputs = Collections.emptyList();
    public List<Indir<Resource>> qmod = Collections.emptyList();
    public List<Indir<Resource>> tools = new ArrayList<>();;
    private int xoff = UI.scale(45), qmy = UI.scale(38), outy = UI.scale(65);
    public static final Text.Foundry nmf = new Text.Foundry(Text.serif, 20).aa(true);
    private static double softcap = 0;
    private static Tex softTex = null;

    @RName("make")
    public static class $_ implements Factory {
        public Widget create(UI ui, Object[] args) {
            return(new NMakewindow((String)args[0]));
        }
    }

    private static final OwnerContext.ClassResolver<NMakewindow> ctxr = new OwnerContext.ClassResolver<NMakewindow>()
            .add(Glob.class, wdg -> wdg.ui.sess.glob)
            .add(Session.class, wdg -> wdg.ui.sess);
    public class Spec implements GSprite.Owner, ItemInfo.SpriteOwner {
        public Indir<Resource> res;
        public MessageBuf sdt;
        public Tex num;
        private GSprite spr;
        private Object[] rawinfo;
        private List<ItemInfo> info;

        public Spec(Indir<Resource> res, Message sdt, int num, Object[] info) {
            this.res = res;
            this.sdt = new MessageBuf(sdt);
            if(num >= 0)
                this.num = new TexI(Utils.outline2(Text.render(Integer.toString(num), Color.WHITE).img, Utils.contrast(Color.WHITE)));
            else
                this.num = null;
            this.rawinfo = info;
        }

        public GSprite sprite() {
            if(spr == null)
                spr = GSprite.create(this, res.get(), sdt.clone());;
            return(spr);
        }

        public void draw(GOut g) {
            try {
                sprite().draw(g);
            } catch(Loading e) {}
            if(num != null)
                g.aimage(num, Inventory.sqsz, 1.0, 1.0);
        }

        private int opt = 0;
        public boolean opt() {
            if(opt == 0) {
                try {
                    opt = (ItemInfo.find(Optional.class, info()) != null) ? 1 : 2;
                } catch(Loading l) {
                    return(false);
                }
            }
            return(opt == 1);
        }

        public BufferedImage shorttip() {
            List<ItemInfo> info = info();
            if(info.isEmpty()) {
                Resource.Tooltip tt = res.get().layer(Resource.tooltip);
                if(tt == null)
                    return(null);
                return(Text.render(tt.t).img);
            }
            return(ItemInfo.shorttip(info()));
        }
        public BufferedImage longtip() {
            List<ItemInfo> info = info();
            BufferedImage img;
            if(info.isEmpty()) {
                Resource.Tooltip tt = res.get().layer(Resource.tooltip);
                if(tt == null)
                    return(null);
                img = Text.render(tt.t).img;
            } else {
                img = ItemInfo.longtip(info);
            }
            Resource.Pagina pg = res.get().layer(Resource.pagina);
            if(pg != null)
                img = ItemInfo.catimgs(0, img, RichText.render("\n" + pg.text, 200).img);
            return(img);
        }

        private Random rnd = null;
        public Random mkrandoom() {
            if(rnd == null)
                rnd = new Random();
            return(rnd);
        }
        public Resource getres() {return(res.get());}
        public <T> T context(Class<T> cl) {return(ctxr.context(cl, NMakewindow.this));}
        @Deprecated
        public Glob glob() {return(ui.sess.glob);}

        public List<ItemInfo> info() {
            if(info == null)
                info = ItemInfo.buildinfo(this, rawinfo);
            return(info);
        }
        public Resource resource() {return(res.get());}
    }

    public void tick(double dt) {
        for(Spec s : inputs) {
            if(s.spr != null)
                s.spr.tick(dt);
        }
        for(Spec s : outputs) {
            if(s.spr != null)
                s.spr.tick(dt);
        }
    }

    public static final KeyBinding kb_make = KeyBinding.get("make/one", KeyMatch.forcode(java.awt.event.KeyEvent.VK_ENTER, 0));
    public static final KeyBinding kb_makeall = KeyBinding.get("make/all", KeyMatch.forcode(java.awt.event.KeyEvent.VK_ENTER, KeyMatch.C));
    public NMakewindow(String rcpnm) {
        int inputW = add(new Label("Input:"), new Coord(0, UI.scale(8))).sz.x;
        int resultW = add(new Label("Result:"), new Coord(0, outy + UI.scale(8))).sz.x;
        xoff = Math.max(inputW, resultW) + UI.scale(10);
        add(new Button(UI.scale(85), "Craft"), UI.scale(new Coord(230, 75))).action(() -> wdgmsg("make", 0)).setgkey(kb_make);
        add(new Button(UI.scale(85), "Craft All"), UI.scale(new Coord(325, 75))).action(() -> wdgmsg("make", 1)).setgkey(kb_makeall);
        pack();
        this.rcpnm = rcpnm;
    }

    public void uimsg(String msg, Object... args) {
        if(msg == "inpop") {
            List<Spec> inputs = new LinkedList<Spec>();
            for(int i = 0; i < args.length;) {
                int resid = (Integer)args[i++];
                Message sdt = (args[i] instanceof byte[])?new MessageBuf((byte[])args[i++]):MessageBuf.nil;
                int num = (Integer)args[i++];
                Object[] info = {};
                if((i < args.length) && (args[i] instanceof Object[]))
                    info = (Object[])args[i++];
                inputs.add(new Spec(ui.sess.getres(resid), sdt, num, info));
            }
            this.inputs = inputs;
        } else if(msg == "opop") {
            List<Spec> outputs = new LinkedList<Spec>();
            for(int i = 0; i < args.length;) {
                int resid = (Integer)args[i++];
                Message sdt = (args[i] instanceof byte[])?new MessageBuf((byte[])args[i++]):MessageBuf.nil;
                int num = (Integer)args[i++];
                Object[] info = {};
                if((i < args.length) && (args[i] instanceof Object[]))
                    info = (Object[])args[i++];
                outputs.add(new Spec(ui.sess.getres(resid), sdt, num, info));
            }
            this.outputs = outputs;
        } else if(msg == "qmod") {
            List<Indir<Resource>> qmod = new ArrayList<Indir<Resource>>();
            for(Object arg : args)
                qmod.add(ui.sess.getres((Integer)arg));
            this.qmod = qmod;
        } else if(msg == "tool") {
            tools.add(ui.sess.getres((Integer)args[0]));
        } else {
            super.uimsg(msg, args);
        }
    }

    public static final Coord qmodsz = UI.scale(20, 20);
    private static final WeakHashMap<Indir<Resource>, Tex> qmicons = new WeakHashMap<>();
    private Tex qmicon(Indir<Resource> qm) {
        synchronized (qmicons) {
            return qmicons.computeIfAbsent(qm, NMakewindow.this::buildQTex);
        }
    }

    public void draw(GOut g) {
        Coord c = new Coord(xoff, 0);
        boolean popt = false;
        for(Spec s : inputs) {
            boolean opt = s.opt();
            if(opt != popt)
                c = c.add(10, 0);
            GOut sg = g.reclip(c, invsq.sz());
            if(opt) {
                sg.chcolor(0, 255, 0, 255);
                sg.image(invsq, Coord.z);
                sg.chcolor();
            } else {
                sg.image(invsq, Coord.z);
            }
            s.draw(sg);
            c = c.add(Inventory.sqsz.x, 0);
            popt = opt;
        }
        {
            int x = 0;
            if(!qmod.isEmpty()) {
//                g.aimage(qmodl.tex(), new Coord(x, qmy + (qmodsz.y / 2)), 0, 0.5);
                x += qmodl.sz().x + UI.scale(5);
                x = Math.max(x, xoff);
                qmx = x;
                int count = 0;
                double product = 1.0;
                for(Indir<Resource> qm : qmod) {
                    try {
                        Tex t = buildQTex(qm);
                        g.image(t, new Coord(x, qmy));
                        x += t.sz().x + UI.scale(1);

                        Glob.CAttr attr = NUtils.getGameUI().chrwdg.findattr(qm.get().basename());
                        if(attr != null) {
                            count++;
                            product = product * attr.comp;
                        }
                    } catch(Loading l) {
                    }
                }
                if(count > 0) {
                    x += drawSoftcap(g, new Coord(x, qmy), product, count);
                }
                x += UI.scale(25);
            }
            if(!tools.isEmpty()) {
                g.aimage(tooll.tex(), new Coord(x, qmy + (qmodsz.y / 2)), 0, 0.5);
                x += tooll.sz().x + UI.scale(5);
                x = Math.max(x, xoff);
                toolx = x;
                for(Indir<Resource> tool : tools) {
                    try {
                        Tex t = qmicon(tool);
                        g.image(t, new Coord(x, qmy));
                        x += t.sz().x + UI.scale(1);
                    } catch(Loading l) {
                    }
                }
                x += UI.scale(25);
            }
        }
        c = new Coord(xoff, outy);
        for(Spec s : outputs) {
            GOut sg = g.reclip(c, invsq.sz());
            sg.image(invsq, Coord.z);
            s.draw(sg);
            c = c.add(Inventory.sqsz.x, 0);
        }
        super.draw(g);
    }

    private int drawSoftcap(GOut g, Coord p, double product, int count) {
        if(count > 0) {
            double current = Math.pow(product, 1.0 / count);
            if(current != softcap || softTex == null) {
                softcap = current;
                String format = String.format("%s %.1f", "Softcap:", softcap);
                Text txt = Text.renderstroked(format, Color.WHITE, Color.BLACK, fnd);
                if(softTex != null) {
                    softTex.dispose();
                }
                softTex = new TexI(txt.img);
            }
            g.image(softTex, p.add(UI.scale(5), 0));
            return softTex.sz().x + UI.scale(6);
        }
        return 0;
    }

    private Tex buildQTex(Indir<Resource> res) {
        BufferedImage result = PUtils.convolve(res.get().layer(Resource.imgc).img, qmodsz, CharWnd.iconfilter);
        try {
            Glob.CAttr attr = NUtils.getGameUI().chrwdg.findattr(res.get().basename());
            if(attr != null) {
                result = ItemInfo.catimgsh(1, result, attr.compline().img);
            }
        } catch (Exception ignored) {
        }
        return new TexI(result);
    }

    public static void invalidate(String name) {
        synchronized (qmicons) {
            LinkedList<Indir<Resource>> tmp = new LinkedList<>(qmicons.keySet());
            tmp.forEach(res -> {
                if(name.equals(res.get().basename())) {
                    qmicons.remove(res);
                }
            });
        }
    }

    private int qmx, toolx;
    private long hoverstart;
    private Spec lasttip;
    private Indir<Object> stip, ltip;
    public Object tooltip(Coord mc, Widget prev) {
        String name = null;
        Spec tspec = null;
        Coord c;
        if(!qmod.isEmpty()) {
            c = new Coord(qmx, qmy);
            try {
                for(Indir<Resource> qm : qmod) {
                    Tex t = qmicon(qm);
                    Coord sz = t.sz();
                    if(mc.isect(c, sz))
                        return(qm.get().layer(Resource.tooltip).t);
                    c = c.add(sz.x + UI.scale(1), 0);
                }
            } catch(Loading l) {
            }
        }
        if(!tools.isEmpty()) {
            c = new Coord(toolx, qmy);
            try {
                for(Indir<Resource> tool : tools) {
                    Coord tsz = qmicon(tool).sz();
                    if(mc.isect(c, tsz))
                        return(tool.get().layer(Resource.tooltip).t);
                    c = c.add(tsz.x + UI.scale(1), 0);
                }
            } catch(Loading l) {
            }
        }
        find: {
            c = new Coord(xoff, 0);
            boolean popt = false;
            for(Spec s : inputs) {
                boolean opt = s.opt();
                if(opt != popt)
                    c = c.add(UI.scale(10), 0);
                if(mc.isect(c, Inventory.invsq.sz())) {
                    name = getDynamicName(s.spr);
                    if(name == null || name.contains("Raw")){
                        tspec = s;
                    }
                    break find;
                }
                c = c.add(Inventory.sqsz.x, 0);
                popt = opt;
            }
            c = new Coord(xoff, outy);
            for(Spec s : outputs) {
                if(mc.isect(c, invsq.sz())) {
                    tspec = s;
                    break find;
                }
                c = c.add(Inventory.sqsz.x, 0);
            }
        }
        if(lasttip != tspec) {
            lasttip = tspec;
            stip = ltip = null;
        }
        if(tspec == null)
            return(super.tooltip(mc, prev));
        long now = System.currentTimeMillis();
        boolean sh = true;
        if(prev != this)
            hoverstart = now;
        else if(now - hoverstart > 1000)
            sh = false;
        if(sh) {
            if(stip == null) {
                BufferedImage tip = tspec.shorttip();
                if(tip == null) {
                    stip = () -> null;
                } else {
                    Tex tt = new TexI(tip);
                    stip = () -> tt;
                }
            }
            return(stip);
        } else {
            if(ltip == null) {
                BufferedImage tip = tspec.longtip();
                if(tip == null) {
                    ltip = () -> null;
                } else {
                    Tex tt = new TexI(tip);
                    ltip = () -> tt;
                }
            }
            return(ltip);
        }
    }

    private static String getDynamicName(GSprite spr) {
        if(spr != null) {
            if(spr instanceof DynName)
            {
                return ((DynName)spr).name();
            }
        }
        return null;
    }

    public static Class[] interfaces(Class c) {
        try {
            return c.getInterfaces();
        } catch (Exception ignored) {}
        return new Class[0];
    }

    public static boolean hasInterface(String name, Class c) {
        Class[] interfaces = interfaces(c);
        for (Class in : interfaces) {
            if(in.getCanonicalName().equals(name)) {return true; }
        }
        return false;
    }

    public boolean globtype(char ch, java.awt.event.KeyEvent ev) {
        if(ch == '\n') {
            wdgmsg("make", ui.modctrl?1:0);
            return(true);
        }
        return(super.globtype(ch, ev));
    }

    public static class Optional extends ItemInfo.Tip {
        public static final Text text = RichText.render(String.format("$i{%s}", "Optional"), 0);
        public Optional(Owner owner) {
            super(owner);
        }

        public BufferedImage tipimg() {
            return(text.img);
        }

        public Tip shortvar() {return(this);}
    }

    public static class MakePrep extends ItemInfo implements GItem.ColorInfo {
        private final static Color olcol = new Color(0, 255, 0, 64);
        public MakePrep(Owner owner) {
            super(owner);
        }

        public Color olcol() {
            return(olcol);
        }
    }
//    private double softcap = 0;
//    public NNMakewindow(String rcpnm) {
//        super("Craft");
//        add(new Button(UI.scale(85), "Craft"), UI.scale(new Coord(0, 0))).action(() -> wdgmsg("make", 0)).setgkey(kb_make);
//        add(new Button(UI.scale(85), "Craft All"), UI.scale(new Coord(0, 0))).action(() -> wdgmsg("make", 1)).setgkey(kb_makeall);
//        pack();
//    }
//
//    @Override
//    public void draw(GOut g) {
//            Coord c = new Coord(xoff, 0);
//            boolean popt = false;
//            for(Spec s : inputs) {
//                boolean opt = s.opt();
//                if(opt != popt)
//                    c = c.add(10, 0);
//                GOut sg = g.reclip(c, invsq.sz());
//                if(opt) {
//                    sg.chcolor(0, 255, 0, 255);
//                    sg.image(invsq, Coord.z);
//                    sg.chcolor();
//                } else {
//                    sg.image(invsq, Coord.z);
//                }
//                s.draw(sg);
//                c = c.add(Inventory.sqsz.x, 0);
//                popt = opt;
//            }
//            {
//                int x = 0;
//                if(!qmod.isEmpty()) {
//                    g.aimage(qmodl.tex(), new Coord(x, qmy + (qmodsz.y / 2)), 0, 0.5);
//                    x += qmodl.sz().x + UI.scale(5);
//                    x = Math.max(x, xoff);
//                    qmx = x;
//                    int count = 0;
//                    double product = 1.0;
//                    for(Indir<Resource> qm : qmod) {
//                        try {
//                            Tex t = qmicon(qm);
//                            g.image(t, new Coord(x, qmy));
//                            x += t.sz().x + UI.scale(1);
//                            Glob.CAttr attr = NUtils.getGameUI().chrwdg.findattr(qm.get().basename());
//                            if(attr != null) {
//                                count++;
//                                product = product * attr.comp;
//
//                                Text txt = Text.renderstroked(String.valueOf(attr.comp), Color.WHITE, Color.BLACK);
//                                TexI attr_tex = new TexI(txt.img);
//                                g.image(attr_tex, new Coord(x, qmy).add(UI.scale(5), UI.scale(3)));
//                                x += attr_tex.sz().x + UI.scale(8);
//                            }
//                        } catch(Loading l) {
//                        }
//                    }
//                    if(count>0)
//                    {
//                        x += drawSoftcap(g, new Coord(x, qmy), product, count);
//                    }
//                    x += UI.scale(25);
//                }
//                if(!tools.isEmpty()) {
//                    g.aimage(tooll.tex(), new Coord(x, qmy + (qmodsz.y / 2)), 0, 0.5);
//                    x += tooll.sz().x + UI.scale(5);
//                    x = Math.max(x, xoff);
//                    toolx = x;
//                    for(Indir<Resource> tool : tools) {
//                        try {
//                            Tex t = qmicon(tool);
//                            g.image(t, new Coord(x, qmy));
//                            x += t.sz().x + UI.scale(1);
//                        } catch(Loading l) {
//                        }
//                    }
//                    x += UI.scale(25);
//                }
//            }
//            c = new Coord(xoff, outy);
//            for(Spec s : outputs) {
//                GOut sg = g.reclip(c, invsq.sz());
//                sg.image(invsq, Coord.z);
//                s.draw(sg);
//                c = c.add(Inventory.sqsz.x, 0);
//            }
//
//    }
//
//    TexI softTex;
//
//    private int drawSoftcap(GOut g, Coord p, double product, int count) {
//        if(count > 0) {
//            double current = Math.pow(product, 1.0 / count);
//            if(current != softcap || softTex == null) {
//                softcap = current;
//                String format = String.format("%s %.1f", "Softcap:", softcap);
//                Text txt = Text.renderstroked(format, Color.WHITE, Color.BLACK);
//                if(softTex != null) {
//                    softTex.dispose();
//                }
//                softTex = new TexI(txt.img);
//            }
//            g.image(softTex, p.add(UI.scale(5), UI.scale(3)));
//            return softTex.sz().x + UI.scale(6);
//        }
//        return 0;
//    }
}
