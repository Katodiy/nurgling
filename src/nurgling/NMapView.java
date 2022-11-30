package nurgling;

import haven.*;
import haven.render.*;
import haven.res.lib.itemtex.ItemTex;
import nurgling.bots.CheckClay;
import nurgling.bots.CheckWater;
import nurgling.bots.FeedClover;
import nurgling.tools.NArea;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.*;

import static haven.MCache.tilesz;
import static haven.OCache.posres;

public class NMapView extends MapView {
    public static Coord2d rc1 = new Coord2d ();
    public static Coord2d rc2 = new Coord2d ();
    public boolean isAreaSelectorEnable = false;
    public static final KeyBinding kb_checkClay = KeyBinding.get ( "checkClay", KeyMatch.forchar ( 'w', KeyMatch.C ) );
    public static final KeyBinding kb_checkWater = KeyBinding.get ( "checkWater", KeyMatch.forchar ( 'Y',KeyMatch.C ) );
    public static final KeyBinding kb_feedclower = KeyBinding.get ( "feedClover", KeyMatch.forchar ( 'U',
            KeyMatch.C ) );
    public static final KeyBinding kb_light = KeyBinding.get ( "light", KeyMatch.forchar ( 'H', KeyMatch.C ) );
    public static final KeyBinding kb_give = KeyBinding.get ( "giveS", KeyMatch.forchar ( 'Q', KeyMatch.C) );
    private boolean n_selection = false;
    private boolean withpf = false;

    public NMapView(Coord sz, Glob glob, Coord2d cc, long plgob) {
        super(sz, glob, cc, plgob);
        basic.add(((NOCache)glob.oc).paths);
        ItemTex.tryLoad();
    }

    final HashMap<String, String> ttip = new HashMap<>();

    public Object tooltip(Coord c, Widget prev) {
        if (!ttip.isEmpty() && NUtils.getGameUI().getInspectMode()) {

            Collection<BufferedImage> imgs = new LinkedList<BufferedImage>();
            if (ttip.get("gob") != null) {
                BufferedImage gob = RichText.render(String.format("$col[128,128,255]{%s}:", "Gob"), 0).img;
                imgs.add(gob);
                imgs.add(RichText.render(ttip.get("gob"), 0).img);
            }
            if (ttip.get("rc") != null) {
                BufferedImage gob = RichText.render(String.format("$col[128,128,128]{%s}:", "Coord"), 0).img;
                imgs.add(gob);
                imgs.add(RichText.render(ttip.get("rc"), 0).img);
            }
            if (ttip.get("id") != null) {
                BufferedImage gob = RichText.render(String.format("$col[255,128,255]{%s}:", "id"), 0).img;
                imgs.add(gob);
                imgs.add(RichText.render(ttip.get("id"), 0).img);
            }
            if (ttip.get("tile") != null) {
                BufferedImage tile = RichText.render(String.format("$col[128,128,255]{%s}:", "Tile"), 0).img;
                imgs.add(tile);
                imgs.add(RichText.render(ttip.get("tile"), 0).img);
            }
            if (ttip.get("tags") != null) {
                BufferedImage gob = RichText.render(String.format("$col[255,128,128]{%s}:", "Tags"), 0).img;
                imgs.add(gob);
                imgs.add(RichText.render(ttip.get("tags"), 0).img);
            }
            if (ttip.get("status") != null) {
                BufferedImage gob = RichText.render(String.format("$col[255,128,128]{%s}:", "Status"), 0).img;
                imgs.add(gob);
                imgs.add(RichText.render(ttip.get("status"), 0).img);
            }
            if (ttip.get("bounds") != null) {
                BufferedImage gob = RichText.render(String.format("$col[255,128,255]{%s}:", "Bounds"), 0).img;
                imgs.add(gob);
                imgs.add(RichText.render(ttip.get("bounds"), 0).img);
            }
            if (ttip.get("marker") != null) {
                BufferedImage gob = RichText.render(String.format("$col[255,83,83]{%s}:", "Marker"), 0).img;
                imgs.add(gob);
                imgs.add(RichText.render(ttip.get("marker"), 0).img);
            }
            if (ttip.get("cont") != null) {
                BufferedImage gob = RichText.render(String.format("$col[83,255,83]{%s}:", "Container"), 0).img;
                imgs.add(gob);
                imgs.add(RichText.render(ttip.get("cont"), 0).img);
            }
            if (ttip.get("ols") != null) {
                BufferedImage gob = RichText.render(String.format("$col[83,255,155]{%s}:", "Overlays"), 0).img;
                imgs.add(gob);
                imgs.add(RichText.render(ttip.get("ols"), 0).img);
            }
            if (ttip.get("poses") != null) {
                BufferedImage gob = RichText.render(String.format("$col[255,128,128]{%s}:", "Poses"), 0).img;
                imgs.add(gob);
                imgs.add(RichText.render(ttip.get("poses"), 0).img);
            }
            return new TexI((ItemInfo.catimgs(0, imgs.toArray(new BufferedImage[0]))));
        }
        return (super.tooltip(c, prev));
    }

    void inspect(Coord c) {
        new Hittest(c) {
            @Override
            protected void hit(Coord pc, Coord2d mc, ClickData inf) {
                    ttip.clear();
                    if (inf != null) {
                        Gob gob = Gob.from(inf.ci);
                        if (gob != null) {
                            ttip.put("gob", gob.getResName());

                            ttip.put("rc" , gob.rc.toString());
                            if(!gob.ols.isEmpty()) {
                                StringBuilder ols = new StringBuilder();
                                boolean isPrinted = false;
                                for (Gob.Overlay ol : gob.ols) {
                                    if (ol.spr != null) {
                                        isPrinted = true;
                                        String res = ol.spr.getClass().toString();
                                        if(!res.contains("$"))
                                            ols.append(res + " ");
                                    }
                                }
                                if(isPrinted)
                                    ttip.put("ols", ols.toString());
                            }


                            ttip.put("id", String.valueOf(gob.id));
                            if (!gob.tags.isEmpty()) {
                                StringBuilder tags = new StringBuilder();
                                Iterator<NGob.Tags> tag = gob.tags.iterator();
                                while (tag.hasNext()) {
                                    tags.append(tag.next().toString());
                                    if (tag.hasNext())
                                        tags.append(", ");
                                }
                                ttip.put("tags", tags.toString());
                                if(!gob.properties.isEmpty()){
                                    for(NProperties prop: gob.properties){
                                        if(prop instanceof NProperties.Container){
                                            NProperties.Container cont = (NProperties.Container) prop;
                                            StringBuilder cont_str = new StringBuilder();
                                            cont_str.append(String.format("Cap: %s Free: %d Full %d",cont.cap, cont.free, cont.full));
                                            ttip.put("cont", cont_str.toString());
                                        }
                                    }
                                }
                            }
                            HashSet<NGob.Tags> bounds;
                            if ((bounds = NOCache.getBounds(gob.id)) != null) {
                                StringBuilder boon_str = new StringBuilder();
                                Iterator<NGob.Tags> bound = bounds.iterator();
                                while (bound.hasNext()) {
                                    boon_str.append(bound.next().toString());
                                    if (bound.hasNext())
                                        boon_str.append(", ");
                                }
                                ttip.put("bounds", boon_str.toString());
                            }
                            ttip.put("status", gob.status.toString());
                            if (gob.isTag(NGob.Tags.marked)) {
                                ttip.put("marker", String.valueOf(NGob.getModelAttribute(gob)));
                            }

                            if(gob.getattr(Drawable.class)!=null && gob.getattr(Drawable.class) instanceof Composite && ((Composite)gob.getattr(Drawable.class)).oldposes!=null)
                            {
                                StringBuilder poses = new StringBuilder();
                                Iterator<ResData> pose = ((Composite)gob.getattr(Drawable.class)).oldposes.iterator();
                                while (pose.hasNext()) {
                                    poses.append(pose.next().res.get().name);
                                    if (pose.hasNext())
                                        poses.append(", ");
                                }
                                ttip.put("poses", poses.toString());
                            }

                        }
                    }
                    MCache mCache = ui.sess.glob.map;
                    int tile = mCache.gettile(mc.div(tilesz).floor());
                    Resource res = mCache.tilesetr(tile);
                    if (res != null) {
                        ttip.put("tile", res.name);
                    }
            }

            @Override
            protected void nohit(Coord pc) {
                ttip.clear();
            }
        }.run();
    }

    public boolean mousedown (
            Coord c,
            int button
    ) {
        if ( isAreaSelectorEnable ) {
            if ( selection == null ) {
                selection = new NSelector ();
                n_selection = true;
            }
        }
        else if ( selection != null && n_selection ) {
            selection.destroy ();
            selection = null;
            n_selection = false;
            isAreaSelectorEnable = false;
        }
        else if ( withpf ) {
            parent.setfocus ( this );
            Loader.Future<Plob> placing_l = this.placing;
            if ( button == 2 ) {
                if ( ( ( Camera ) camera ).click ( c ) ) {
                    camdrag = ui.grabmouse ( this );
                }
            }
            else if ( ( placing_l != null ) && placing_l.done () ) {
                Plob placing = placing_l.get ();
                if ( placing.lastmc != null ) {
                    wdgmsg ( "place", placing.rc.floor ( posres ), ( int ) Math.round ( placing.a * 32768 / Math.PI ),
                            button, ui.modflags () );
                }
            }
            else if ( ( grab != null ) && grab.mmousedown ( c, button ) ) {
            }
            else {
                new NClick ( c, button ).run ();
            }
            return ( true );
        }
        return super.mousedown ( c, button );
        //        }
        //        return false;
    }

    public static String defcam(){
        return Utils.getpref("defcam", "ortho");
    }
    public static void defcam(String name) {
        Utils.setpref("defcam", name);
    }

    public static Collection<String> camlist(){
        return camtypes.keySet();
    }
    static {camtypes.put("northo", NOrthoCam.class);}
    public class NOrthoCam extends SOrthoCam {
        @Override
        public void release () {

        }

        @Override
        public void chfield ( float nf ) {
            if ( nf > 20 && nf < 800 ) {
                super.tfield = nf;
            }
        }

        @Override
        public boolean wheel (
                Coord c,
                int amount
        ) {
            chfield ( super.tfield + amount * 10 );
            return ( true );
        }
    }

    @Override
    public Camera restorecam() {
        Class<? extends Camera> ct = camtypes.get(Utils.getpref("defcam", null));
        if(ct == null)
            return(new SOrthoCam());
        String[] args = (String [])Utils.deserialize(Utils.getprefb("camargs", null));
        if(args == null) args = new String[0];
        try {
            return(makecam(ct, args));
        } catch(Exception e) {
            return(new NOrthoCam());
        }
    }

    public void click(Gob gob, int button) {
        click(gob, button, ui.mc);
    }

    public void click(Gob gob, int button, Coord mouse) {
//        if(button == 3) {NFlowerMenu.lastGob(gob);}
        Coord mc = gob.rc.floor(posres);
        click(gob.rc, button, mouse, mc, button, ui.modflags(), 0, (int) gob.id, mc, 0, -1);
    }

    public void click(Coord2d c, int button) {
        click(c, button, ui.mc, c.floor(posres), button, ui.modflags());
    }
    @Override
    public void click(Coord2d mc, int button, Object... args) {
        boolean send = true;
        if(button == 1 ) {
            if(ui.modmeta) {
                args[3] = 0;
                send = NUtils.getGameUI().pathQueue.add(mc);
            } else {
                if(NUtils.isIdleCurs())
                   NUtils.getGameUI().pathQueue.start(mc);
            }
        }
        if(button == 3){
            if(NUtils.getGameUI().pathQueue.size()<=1)
                NUtils.getGameUI().pathQueue.clear();
        }
        if(send && !NUtils.getGameUI().nomadMod)
            wdgmsg("click", args);
    }

    @Override
    public boolean globtype (
            char c,
            KeyEvent ev
    ) {
        if (kb_light.key().match(ev)) {
            NConfiguration.getInstance().nightVision = !NConfiguration.getInstance().nightVision;
            NConfiguration.getInstance().write();
            return true;
        }else if ( kb_checkClay.key ().match ( ev ) ) {
            Thread thread = new Thread ( new CheckClay( NUtils.getGameUI () ), "Check Clay" );
            thread.start ();
            return true;
        }
        else if ( kb_feedclower.key ().match ( ev ) ) {
            Thread thread = new Thread ( new FeedClover( NUtils.getGameUI () ), "FeedClover" );
            thread.start ();
            return true;
        }
        else if ( kb_checkWater.key ().match ( ev ) ) {
            Thread thread = new Thread ( new CheckWater( NUtils.getGameUI () ), "Check Water" );
            thread.start ();
            return true;
        }
        else if ( kb_give.key ().match ( ev ) ) {
            NUtils.getFightView().give ();
            return true;
        }
        return super.globtype(c,ev);
    }
    @Override
    public boolean drop(final Coord cc, Coord ul) {
        if(!ui.modctrl) {
            new Hittest(cc) {
                public void hit(Coord pc, Coord2d mc, ClickData inf) {
                    click(mc, 1, ui.mc, mc.floor(posres), 1, ui.modflags());
                }
            }.run();
            return true;
        }
        new Hittest(cc) {
            public void hit(Coord pc, Coord2d mc, ClickData inf) {
                wdgmsg("drop", pc, mc.floor(posres), ui.modflags());
            }
        }.run();
        return(true);
    }


    public NArea getSelection () {
        Coord2d min = new Coord2d ();
        Coord2d max = new Coord2d ();
        if ( rc1.x < rc2.x ) {
            min.x = rc1.x;
            max.x = rc2.x;
        }
        else {
            min.x = rc2.x;
            max.x = rc1.x;
        }
        if ( rc1.y < rc2.y ) {
            min.y = rc1.y;
            max.y = rc2.y;
        }
        else {
            min.y = rc2.y;
            max.y = rc1.y;
        }
        return new NArea ( min, max );
    }

    public class NSelector extends Selector {
        @Override
        public boolean mmousedown (
                Coord mc,
                int button
        ) {
            synchronized ( NMapView.this ) {
                if ( sc != null ) {
                    ol.destroy ();
                    mgrab.remove ();
                }

                sc = mc.div ( MCache.tilesz2 );

                modflags = ui.modflags ();
                xl.mv = true;
                mgrab = ui.grabmouse ( NMapView.this );
                ol = glob.map.new Overlay ( new Area(sc, sc), selol);
                rc1.x = sc.x * tilesz.x;
                rc1.y = sc.y * tilesz.y;
                return ( true );
            }
        }

        @Override
        public boolean mmouseup (
                Coord mc,
                int button
        ) {
            if ( isAreaSelectorEnable ) {
                Coord sc_fix = mc.div ( MCache.tilesz2 );

                rc2.x = sc_fix.x * tilesz.x;
                rc2.y = sc_fix.y * tilesz.y;
                if ( mc.x > sc.x * tilesz.x ) {
                    rc2.x += tilesz.x;
                }
                else {
                    rc1.x += tilesz.x;
                }
                if ( mc.y > sc.y * tilesz.y ) {
                    rc2.y += tilesz.y;
                }
                else {
                    rc1.y += tilesz.y;
                }
                isAreaSelectorEnable = false;
            }
            return super.mmouseup ( mc, button );
        }
    }
    Thread th = null;
    
    public class NClick extends Click {

        public NClick (
                Coord c,
                int b
        ) {
            super ( c, b );
        }

        @Override
        public void run () {
            Environment env = ui.env;
            Render out = env.render ();
            Pipe.Op basic = clickbasic ( NMapView.this.sz );
            Pipe bstate = new BufPipe().prep ( basic );
            out.clear ( bstate, FragID.fragid, FColor.BLACK );
            out.clear ( bstate, 1.0 );
            checkmapclick ( out, basic, pc, mc -> {
                mapcl = mc;
                ckdone ( 1 );
            } );
            out.clear ( bstate, FragID.fragid, FColor.BLACK );

            checkgobclick ( out, basic, pc, cl -> {
                objcl = cl;
                ckdone ( 2 );
            } );
            env.submit ( out );


        }
    }

}