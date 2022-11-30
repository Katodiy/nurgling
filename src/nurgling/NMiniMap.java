package nurgling;

import haven.*;
import nurgling.minimap.NPMarker;
import nurgling.minimap.NSMarker;

import java.awt.*;
import java.util.List;

import static haven.MCache.cmaps;
import static haven.MCache.tilesz;
import static haven.OCache.posres;

public class NMiniMap extends MiniMap {
    public int scale = 1;
    public boolean big = false;

    public NMiniMap(Coord sz, MapFile file) {
        super(sz, file);
    }

    public NMiniMap(MapFile file) {
        super(file);
    }
    public void tick(double dt) {
        Coord mc = rootxlate(ui.mc);
        if(mc.isect(Coord.z, sz)) {
            setBiome(xlate(mc));
        } else {
            setBiome(null);
        }
        super.tick(dt);
    }
    @Override
    public boolean mousewheel(Coord c, int amount) {
        if(amount > 0) {
            if(scale > 1) {
                scale--;
            } else
            if(allowzoomout())
                zoomlevel = Math.min(zoomlevel + 1, dlvl + 1);
        } else {
            if(zoomlevel == 0 && scale < 4) {
                scale++;
            }
            zoomlevel = Math.max(zoomlevel - 1, 0);
        }
        return(true);
    }


    void drawbiome(GOut g) {
        if(biometex != null) {
            Coord mid = new Coord(g.sz().x / 2, 0);
            Coord tsz = biometex.sz();
            g.chcolor(new Color(0,0,0,85));
            g.frect(mid.sub(2 + tsz.x /2, 0), tsz.add(4, 2));
            g.chcolor();
            g.aimage(biometex, mid, 0.5f, 0);
        }
    }
    void drawgrid(GOut g) {
        int zmult = 1 << zoomlevel;
        Coord offset = sz.div(2).sub(dloc.tc.div(scalef()));
        Coord zmaps = cmaps.div( (float)zmult).mul(scale);

        double width = UI.scale(1f);
        Color col = g.getcolor();
        g.chcolor(Color.RED);
        for (int x = dgext.ul.x * zmult; x < dgext.br.x * zmult; x++) {
            Coord a = UI.scale(zmaps.mul(x, dgext.ul.y * zmult)).add(offset);
            Coord b = UI.scale(zmaps.mul(x, dgext.br.y * zmult)).add(offset);
            if(a.x >= 0 && a.x <= sz.x) {
                a.y = Utils.clip(a.y, 0, sz.y);
                b.y = Utils.clip(b.y, 0, sz.y);
                g.line(a, b, width);
            }
        }
        for (int y = dgext.ul.y * zmult; y < dgext.br.y * zmult; y++) {
            Coord a = UI.scale(zmaps.mul(dgext.ul.x * zmult, y)).add(offset);
            Coord b = UI.scale(zmaps.mul(dgext.br.x * zmult, y)).add(offset);
            if(a.y >= 0 && a.y <= sz.y) {
                a.x = Utils.clip(a.x, 0, sz.x);
                b.x = Utils.clip(b.x, 0, sz.x);
                g.line(a, b, width);
            }
        }
        g.chcolor(col);
    }

    @Override
    protected float scalef() {
        return(UI.unscale((float)(1 << dlvl))/scale);
    }

    @Override
    public Coord st2c(Coord tc) {
        return(UI.scale(tc.add(sessloc.tc).sub(dloc.tc).div(1 << dlvl)).mul(scale).add(sz.div(2)));
    }

    @Override
    public void drawgrid(GOut g, Coord ul, DisplayGrid disp) {
        try {
            Tex img = disp.img();
            if(img != null)
                g.image(img, ul, UI.scale(img.sz()).mul(scale));
        } catch(Loading l) {
        }
    }

    @Override
    public void drawmap(GOut g) {
        Coord hsz = sz.div(2);
        for(Coord c : dgext) {
            Coord ul = UI.scale(c.mul(cmaps).mul(scale)).sub(dloc.tc.div(scalef())).add(hsz);
            DisplayGrid disp = display[dgext.ri(c)];
            if(disp == null)
                continue;
            drawgrid(g, ul, disp);
        }
    }

    @Override
    public void drawmarkers(GOut g) {
        Coord hsz = sz.div(2);
        for(Coord c : dgext) {
            DisplayGrid dgrid = display[dgext.ri(c)];
            if(dgrid == null)
                continue;
            for(NDisplayMarker mark : dgrid.markers(true)) {
                if(filter(mark))
                    continue;
                mark.draw(g, mark.m.tc.sub(dloc.tc).div(scalef()).add(hsz), scale, ui, file, big);
            }
        }
    }
    @Override
    public DisplayMarker markerat(Coord tc) {
        for(DisplayGrid dgrid : display) {
            if(dgrid == null)
                continue;
            for(NDisplayMarker mark : dgrid.markers(false)) {
                Area hit = mark.hit();
                if((hit != null) && hit.contains(tc.sub(mark.m.tc).div(scalef())) && !filter(mark))
                    return(mark);
            }
        }
        return(null);
    }

    public static class NDisplayMarker extends DisplayMarker{

        public NDisplayMarker(MapFile.Marker marker) {
            super(marker);
            checkTip(marker.nm);
        }

        private void checkTip(final String nm) {
            if (tip == null || !tip.text.equals(nm)) {
                tip = Text.renderstroked(nm, Color.WHITE, Color.BLACK);
            }
        }

        public void draw(GOut g, Coord c, final float scale, final UI ui, final MapFile file, final boolean canShowName) {
//                checkTip(m.nm);
//                m.draw(g, c, canShowName ? tip : null, scale, file);
//                return;
//            }
            if(m instanceof NPMarker) {
                Coord ul = c.sub(flagcc);
                g.chcolor(((NPMarker)m).color);
                g.image(flagfg, ul);
                g.chcolor();
                g.image(flagbg, ul);
            } else if(m instanceof NSMarker) {
                NSMarker sm = (NSMarker)m;
                try {
                    if(cc == null) {
                        Resource res = sm.res.loadsaved(Resource.remote());
                        img = res.layer(Resource.imgc);
                        Resource.Neg neg = res.layer(Resource.negc);
                        cc = (neg != null) ? neg.cc : img.ssz.div(2);
                        if(hit == null)
                            hit = Area.sized(cc.inv(), img.ssz);
                    }
                } catch(Loading l) {
                } catch(Exception e) {
                    cc = Coord.z;
                }
                if(img != null)
                    g.image(img, c.sub(cc));
            }
        }


        private Area hit() {
            if(hit == null)
                hit = m.area();
            return hit;
        }

    }
    public static final Coord _sgridsz = new Coord(100, 100);
    public static final Coord VIEW_SZ = UI.scale(_sgridsz.mul(9).div(tilesz.floor()));
    public static final Color VIEW_BG_COLOR = new Color(255, 255, 255, 60);
    public static final Color VIEW_BORDER_COLOR = new Color(0, 0, 0, 128);
    void drawview(GOut g) {
        int zmult = 1 << zoomlevel;
        Coord2d sgridsz = new Coord2d(_sgridsz);
        Gob player = NUtils.getGameUI().map.player();
        if(player != null) {
            Coord rc = p2c(player.rc.floor(sgridsz).sub(4, 4).mul(sgridsz));
            Coord viewsz = VIEW_SZ.div(zmult).mul(scale);
            g.chcolor(VIEW_BG_COLOR);
            g.frect(rc, viewsz);
            g.chcolor(VIEW_BORDER_COLOR);
            g.rect(rc, viewsz);
            g.chcolor();
        }
    }

    @Override
    public void drawparts(GOut g){
        drawmap(g);
        drawmarkers(g);
        drawbiome(g);
        boolean playerSegment = (sessloc != null) && ((curloc == null) || (sessloc.seg == curloc.seg));
        if(zoomlevel <= 2 && NConfiguration.getInstance().isGrid) {drawgrid(g);}
        if(playerSegment && zoomlevel <= 1 && NConfiguration.getInstance().isEyed) {drawview(g);}
        if(playerSegment && NConfiguration.getInstance().isPaths) {drawMovement(g);}
//        if(big ) {drawPointers(g);}
        if(dlvl <= 1)
            drawicons(g);
        if(playerSegment) drawparty(g);
//        if(CFG.MMAP_SHOW_BIOMES.get()) {drawbiome(g); }
    }

    void drawMovement(GOut g) {
        if(NUtils.getGameUI().pathQueue!=null && NConfiguration.getInstance().pathCategories.contains(NPathVisualizer.PathCategory.ME)) {
            List<Pair<Coord2d, Coord2d>> lines = NUtils.getGameUI().pathQueue.minimapLines();
            g.chcolor(NPathVisualizer.PathCategory.ME.color);
            for (Pair<Coord2d, Coord2d> line : lines) {
                g.clippedLine(p2c(line.a), p2c(line.b), 1.5);
            }
            g.chcolor();
        }
    }
    @Override
    public void mvclick(MapView mv, Coord mc, Location loc, Gob gob, int button) {
        if(mc == null) mc = ui.mc;
        if((sessloc != null) && (sessloc.seg == loc.seg)) {
            if(gob == null) {
                Coord2d clickAt = loc.tc.sub(sessloc.tc).mul(tilesz).add(tilesz.div(2));
                NUtils.getGameUI().pathQueue().ifPresent(pathQueue -> pathQueue.click(clickAt));
                mv.click(clickAt, button, mc,
                        clickAt.floor(posres),
                        button, ui.modflags());
            }
            else {

                Coord2d clickAt = loc.tc.sub(sessloc.tc).mul(tilesz).add(tilesz.div(2));
                NUtils.getGameUI().pathQueue().ifPresent(pathQueue -> pathQueue.click(gob));
                mv.click(clickAt, button, mc,
                        clickAt.floor(posres), button, ui.modflags(), 0,
                        (int) gob.id, gob.rc.floor(posres), 0, -1);
            }
        }
    }

    private String biome;
    private Tex biometex;
    private void setBiome(Location loc) {
        try {
            String newbiome = biome;
            if(loc == null) {
                Gob player = NUtils.getGameUI().map.player();
                if(player != null) {
                    MCache mCache = ui.sess.glob.map;
                    int tile = mCache.gettile(player.rc.div(tilesz).floor());
                    Resource res = mCache.tilesetr(tile);
                    if(res != null) {
                        newbiome = res.name;
                    }
                }
            } else {
                MapFile map = loc.seg.file();
                if(map.lock.readLock().tryLock()) {
                    try {
                        MapFile.Grid grid = loc.seg.grid(loc.tc.div(cmaps)).get();
                        if(grid != null) {
                            int tile = grid.gettile(loc.tc.mod(cmaps));
                            newbiome = grid.tilesets[tile].res.name;
                        }
                    } finally {
                        map.lock.readLock().unlock();
                    }
                }
            }
            if(newbiome == null) {newbiome = "???";}
            if(!newbiome.equals(biome)) {
                biome = newbiome;
                biometex = Text.renderstroked(NUtils.prettyResName(biome)).tex();
            }
        } catch (Loading ignored) {}
    }

    public interface IPointer {
        Coord2d tc(long id);

        Coord sc(Coord c, Coord sz);

        Object tooltip();

        long seg();

        void drawmmarrow(GOut g, Coord tc, Coord sz);
    }

}
