package nurgling;

import haven.*;

public class NMiniMapWnd extends NResizedWidget{
    NMapView map;
    public Map miniMap;
    public static final KeyBinding kb_claim = KeyBinding.get("ol-claim", KeyMatch.nil);
    public static final KeyBinding kb_vil = KeyBinding.get("ol-vil", KeyMatch.nil);

    public static class NMenuCheckBox extends ICheckBox {
        public NMenuCheckBox(String base, KeyBinding gkey, String tooltip) {
            super("hud/" + base, "", "-d", "-h", "-dh");
            setgkey(gkey);
            settip(tooltip);
        }
    }

    ACheckBox map_box;
    public static final KeyBinding kb_eye = KeyBinding.get("ol-eye", KeyMatch.nil);
    public static final KeyBinding kb_grid = KeyBinding.get("ol-mgrid", KeyMatch.nil);
    public static final KeyBinding kb_path = KeyBinding.get("ol-mgrid", KeyMatch.nil);
    public NMiniMapWnd(String name, NMapView map,MapFile file) {
        super(name);
        minSize = new Coord(UI.scale(133),UI.scale(133));
        this.map = map;
        ResCache mapstore = ResCache.global;
        if(MapFile.mapbase.get() != null) {
            try {
                mapstore = HashDirCache.get(MapFile.mapbase.get().toURI());
            } catch(java.net.URISyntaxException e) {
            }
        }
        if(mapstore != null) {
//            try {
//
//            } catch (java.io.IOException e) {
//                /* XXX: Not quite sure what to do here. It's
//                 * certainly not obvious that overwriting the
//                 * existing mapfile with a new one is better. */
//                throw (new RuntimeException("failed to load mapfile", e));
//            }

            miniMap = add(new Map(new Coord(UI.scale(133), UI.scale(133)), file, map));
            miniMap.lower();
        }
        ACheckBox first = add(new NMenuCheckBox("lbtn-claim", GameUI.kb_claim, "Display personal claims"), 0, 0).changed(a -> NUtils.getGameUI().toggleol("cplot", a));
        add(new NMenuCheckBox("lbtn-vil", GameUI.kb_vil, "Display village claims"), (first.sz.x+UI.scale(3)), 0).changed(a -> NUtils.getGameUI().toggleol("vlg", a));
        add(new NMenuCheckBox("lbtn-rlm", GameUI.kb_rlm, "Display realms"), (first.sz.x+UI.scale(3))*2, 0).changed(a -> NUtils.getGameUI().toggleol("realm", a));
        ACheckBox eye = add(new NMenuCheckBox("lbtn-eye", kb_eye, "Display vision area"), (first.sz.x+UI.scale(3))*4, 0).changed(a -> NUtils.getGameUI().mmapw.miniMap.toggleol("eye", a));
        eye.a = NConfiguration.getInstance().isEyed;
        ACheckBox path = add(new NMenuCheckBox("lbtn-path", kb_path, "Display objects paths"), (first.sz.x+UI.scale(3))*5, 0).changed(a -> NUtils.getGameUI().mmapw.miniMap.toggleol("path", a));
        path.a = NConfiguration.getInstance().isPaths;
        ACheckBox grid = add(new NMenuCheckBox("lbtn-grid", kb_grid, "Display grid"), (first.sz.x+UI.scale(3))*6, 0).changed(a -> NUtils.getGameUI().mmapw.miniMap.toggleol("grid", a));
        grid.a = NConfiguration.getInstance().isGrid;
        map_box = add(new NMenuCheckBox("lbtn-map", GameUI.kb_map, "Map"), miniMap.sz.x-(first.sz.x), 0).state(() -> NUtils.getGameUI().wndstate(NUtils.getGameUI().mapfile)).click(() -> {
            NUtils.getGameUI().togglewnd(NUtils.getGameUI().mapfile);
            if(NUtils.getGameUI().mapfile != null)
                Utils.setprefb("wndvis-map", NUtils.getGameUI().mapfile.visible());
        });
        add(new NMenuCheckBox("lbtn-ico", GameUI.kb_ico, "Icon settings"), (first.sz.x+UI.scale(3))*3, 0).state(() -> NUtils.getGameUI().wndstate(NUtils.getGameUI().iconwnd)).click(() -> {
            if(NUtils.getGameUI().iconconf == null)
                return;
            if(NUtils.getGameUI().iconwnd == null) {
                NUtils.getGameUI().iconwnd = new GobIcon.SettingsWindow(NUtils.getGameUI().iconconf, () -> Utils.defer(NUtils.getGameUI()::saveiconconf));
                NUtils.getGameUI().fitwdg(NUtils.getGameUI().add(NUtils.getGameUI().iconwnd, Utils.getprefc("wndc-icon", new Coord(200, 200))));
            } else {
                ui.destroy(NUtils.getGameUI().iconwnd);
                NUtils.getGameUI().iconwnd = null;
            }
        });
        pack();
    }

    public static class Map extends NMiniMap {
        NMapView map;
        public Map(Coord sz, MapFile file,NMapView map) {
            super(sz, file);
            follow(new MapLocator(map));
            c = new Coord(0,0);
            this.map = map;
        }

        public boolean dragp(int button) {
            return(false);
        }

        public boolean clickmarker(DisplayMarker mark, Location loc, int button, boolean press) {
            if(mark.m instanceof MapFile.SMarker) {
                Gob gob = MarkerID.find(ui.sess.glob.oc, ((MapFile.SMarker)mark.m).oid);
                if(gob != null)
                    mvclick(map, null, loc, gob, button);
            }
            return(false);
        }

        public boolean clickicon(DisplayIcon icon, Location loc, int button, boolean press) {
            if(press) {
                mvclick(map, null, loc, icon.gob, button);
                return(true);
            }
            return(false);
        }

        public boolean clickloc(Location loc, int button, boolean press) {
            if(press) {
                mvclick(map, null, loc, null, button);
                return(true);
            }
            return(false);
        }

        public void draw(GOut g) {
            super.draw(g);
        }

        protected boolean allowzoomout() {
            /* XXX? The corner-map has the property that its size
             * makes it so that the one center grid will very commonly
             * touch at least one border, making indefinite zoom-out
             * possible. That will likely cause more problems than
             * it's worth given the resulting workload in generating
             * zoomgrids for very high zoom levels, eNUtilsly when
             * done by mistake, so lock to an arbitrary five levels of
             * zoom, at least for now. */
            if(zoomlevel >= 5)
                return(false);
            return(super.allowzoomout());
        }

        public void toggleol(String val, Boolean a) {
            switch (val){
                case "eye": {
                    NConfiguration.getInstance().isEyed = a;
                    break;
                }
                case "path": {
                    NConfiguration.getInstance().isPaths = a;
                    break;
                }
                case "grid": {
                    NConfiguration.getInstance().isGrid = a;
                    break;
                }
            }
        }
    }

    @Override
    public void resize(Coord sz) {
        super.resize(sz);
        miniMap.resize(sz.x - UI.scale(15), sz.y );
        map_box.move(new Coord(miniMap.sz.x-(map_box.sz.x), 0));
    }
}
