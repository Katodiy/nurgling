package nurgling;

import haven.*;
import haven.render.*;
import haven.res.lib.itemtex.ItemTex;
import nurgling.bots.CheckClay;
import nurgling.bots.CheckWater;
import nurgling.bots.FeedClover;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static haven.MCache.tilesz;
import static haven.OCache.posres;

public class NMapView extends MapView {
    public Coord2d rc1 = new Coord2d();
    public Coord2d rc2 = new Coord2d();
    public AtomicBoolean isAreaSelectorEnable = new AtomicBoolean(false);
    public AtomicBoolean isGobSelectorEnable = new AtomicBoolean(false);
    public static final KeyBinding kb_checkClay = KeyBinding.get ( "checkClay", KeyMatch.forchar ( 'w', KeyMatch.C ) );
    public static final KeyBinding kb_checkWater = KeyBinding.get ( "checkWater", KeyMatch.forchar ( 'Y',KeyMatch.C ) );
    public static final KeyBinding kb_feedclower = KeyBinding.get ( "feedClover", KeyMatch.forchar ( 'U',
            KeyMatch.C ) );
    public static final KeyBinding kb_light = KeyBinding.get ( "light", KeyMatch.forchar ( 'H', KeyMatch.C ) );
    public static final KeyBinding kb_give = KeyBinding.get ( "giveS", KeyMatch.forchar ( 'Q', KeyMatch.C) );
    public static final KeyBinding kb_quick_action = KeyBinding.get ( "kb_quick_action", KeyMatch.forchar ( 'Q', 0) );
    private AtomicBoolean n_selection = new AtomicBoolean(false);
    private boolean withpf = false;

    private NSelector nselection = null;

    private final Map<MCache.OverlayInfo, Overlay> custom_ols = new HashMap<>();

    public NMapView(Coord sz, Glob glob, Coord2d cc, long plgob) {
        super(sz, glob, cc, plgob);
        basic.add(((NOCache)glob.oc).paths);
        olsinf.put("minesup", new NOverlayInfo(Resource.remote().loadwait("map/overlay/minesup-o").flayer(MCache.ResOverlay.class),false));
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
            if (ttip.get("tilerc") != null) {
                BufferedImage gob = RichText.render(String.format("$col[128,128,128]{%s}:", "TCoord"), 0).img;
                imgs.add(gob);
                imgs.add(RichText.render(ttip.get("tilerc"), 0).img);
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

    private Gob checkedGob = null;
    public Gob getSelectedGob() {
        return checkedGob;
    }

    public void resetSelectedGob(){
        checkedGob = null;
    }

    void getGob(Coord c) {
        new Hittest(c) {
            @Override
            protected void hit(Coord pc, Coord2d mc, ClickData inf) {
                if (inf != null) {
                    Gob gob = Gob.from(inf.ci);
                    if (gob != null) {
                        checkedGob = gob;
                    }
                    isGobSelectorEnable.set(false);
                }
            }
        }.run();
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
                            ttip.put("status", gob.status.toString());
                            if (NGob.getModelAttribute(gob)!=-1) {
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
                        ttip.put("tilerc", mc.div(MCache.tilesz).floor().mul(MCache.tilesz).add(MCache.tilesz.div(2)).toString());
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
        if(isGobSelectorEnable.get())
        {
            getGob(c);
            return false;
        }
        else if ( isAreaSelectorEnable.get() ) {
            if ( nselection == null ) {
                nselection = new NSelector ();
                n_selection.set(true);
            }
        }
        else if ( nselection != null && n_selection.get() ) {
            destroySelector();
            n_selection.set(false);
            isAreaSelectorEnable.set(false);
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

    public void destroySelector() {
        if(nselection!=null) {
            nselection.destroy();
            nselection = null;
        }
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
        if (kb_quick_action.key().match(ev)) {
            Gob gob = Finder.findQuickObject();
            if(gob!=null) {
                NUtils.activate(gob);
                return true;
            }
        }else if (kb_light.key().match(ev)) {
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
        return new NArea ( new Coord2d(Math.min(rc1.x, rc2.x),Math.min(rc1.y, rc2.y)), new Coord2d(Math.max(rc1.x, rc2.x),Math.max(rc1.y, rc2.y)) );
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
            if ( isAreaSelectorEnable.get() ) {
                ol.destroy ();
                mgrab.remove ();
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
                isAreaSelectorEnable.set(false);
            }
            return super.mmouseup ( mc, button );
        }
    }
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

    class NOverlayInfo
    {
        public MCache.OverlayInfo id;
        boolean needUpdate = false;

        public NOverlayInfo(MCache.OverlayInfo flayer, boolean b) {
            this.id = flayer;
            this.needUpdate = b;
        }

        HashMap<Long, ArrayList<NOverlayMap.History>> gobs = new HashMap<>();
    }

    public HashMap<String, NOverlayInfo> olsinf = new HashMap<>();

    @Override
    protected void oltick() {
        if (terrain.area != null) {
            for (NOverlayInfo olinf : olsinf.values()) {
                if ((olinf.needUpdate && !olinf.gobs.isEmpty()) && custom_ols.get(olinf.id) != null) {
                    synchronized (NUtils.getGameUI().getMap().glob.map.grids) {
                        for (MCache.Grid grid : NUtils.getGameUI().getMap().glob.map.grids.values()) {
                            for (int i = 0; i < grid.cuts.length; i++) {
                                try {
                                    MapMesh mesh = (grid.cuts[i].mesh != null) ? grid.cuts[i].mesh : grid.cuts[i].dmesh.get();
                                    if (mesh == null)
                                        return;
                                    grid.cuts[i].ols.put(olinf.id, mesh.makeol(olinf.id));
                                    grid.cuts[i].olols.put(olinf.id, mesh.makeolol(olinf.id));
                                } catch (Loading l) {
                                    l.boostprio(2);
                                }
                            }
                        }
                        olinf.needUpdate = false;
                    }
                }
                Overlay ol = custom_ols.get(olinf.id);
                if (ol == null) {
                    try {
                        basic.add(ol = new Overlay(olinf.id));
                        custom_ols.put(olinf.id, ol);
                    } catch (Loading l) {
                        l.boostprio(2);
                        continue;
                    }
                }
            }
        }
        super.oltick();
        if (terrain.area != null)
            for (NOverlayInfo olinf : olsinf.values())
                for (Iterator<Map.Entry<Long, ArrayList<NOverlayMap.History>>> iter = olinf.gobs.entrySet().iterator(); iter.hasNext(); ) {
                    Map.Entry<Long, ArrayList<NOverlayMap.History>> item = iter.next();
                    Long gobid = item.getKey();
                    if (NUtils.getGob(gobid) == null && placing == null || (gobid==-1 && NUtils.getGob(gobid)!=null)) {
                        for (NOverlayMap.History h : olinf.gobs.get(gobid)) {
                            for (int i = 0; i < h.g.ols.length; i++) {
                                if (h.g.ols[i].get().layer(MCache.ResOverlay.class) == olinf.id) {
                                    h.g.ol[i][h.t.x + (h.t.y * MCache.cmaps.x)] = false;
                                }
                            }
                        }
                        iter.remove();
                        olinf.needUpdate = true;
                    }
                }
    }

    public void setStatus(MCache.OverlayInfo id, boolean status){
        for(NOverlayInfo inf: olsinf.values()){
            if(inf.id == id){
                inf.needUpdate = status;
                return;
            }
        }
    }
}