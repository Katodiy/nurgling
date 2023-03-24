package nurgling;

import haven.*;
import haven.res.ui.barterbox.Shopbox;
import haven.res.ui.tt.tiplabel.TipLabel;
import haven.res.ui.tt.relcont.RelCont;

import java.util.*;
import java.util.function.Supplier;


public class NGameUI extends GameUI {
    public NCraftWindow craftwnd;
    public NTimerPanel timers;

    public void toggleol(String tag, boolean a) {
        if(map != null) {
            if(a)
                map.enol(tag);
            else
                map.disol(tag);
        }
    }

    public NBotsInfo botsInfo;
    public NQuestInfo questInfo;
    public NQuestsStats stats;
    public NPathQueue pathQueue = null;
    public Optional<NPathQueue> pathQueue() {
        return (pathQueue != null) ? Optional.of(pathQueue) : Optional.empty();
    }
    public boolean nomadMod = false;
    NToolBelt t1;
    NToolBelt t2;
    NToolBelt t3;

    NEquipProxy extEquipory;
    public void updateButtons(){
        t1.updateButtons(NConfiguration.getInstance().toolBelts.get("belt0").toolKeys);
        t2.updateButtons(NConfiguration.getInstance().toolBelts.get("belt1").toolKeys);
        t3.updateButtons(NConfiguration.getInstance().toolBelts.get("belt2").toolKeys);
    }

    public boolean updated() {
        return map!=null && map.glob!=null && map.glob.map!=null && map.glob.map.isLoaded() && map.player()!=null;
    }

    public NQuestsStats getStats() {
        return stats;
    }

    public class PaginaBeltSlot extends BeltSlot {
        public final MenuGrid.Pagina pagina;

        public PaginaBeltSlot(int idx, MenuGrid.Pagina p) {
            super(idx, p.res, Message.nil, 1);
            pagina = p;
        }
    }

    public class NButtonBeltSlot extends BeltSlot {
        public final NBotsInfo.NButton button;

        public NButtonBeltSlot(int idx, NBotsInfo.NButton p) {
            super(idx, p.res, Message.nil, 1);
            button = p;
        }
    }

    public NChatUIDrag chat;
    public NMiniMapWnd mmapw;

    public NInventory getInventory () {
        return ( NInventory ) maininv;
    }
    public long drives = -1;
    public NGameUI(String chrid, long plid, String genus) {
        super(chrid, plid, genus);
        NUtils.setGameUI(this);
        extEquipory = add(new NEquipProxy(NEquipory.Slots.HAND_LEFT, NEquipory.Slots.HAND_RIGHT, NEquipory.Slots.BELT), NConfiguration.getInstance().dragWidgets.get("EquipProxy").coord );
        t1 = add(new NToolBelt("belt0", 132, 4, NConfiguration.getInstance().toolBelts.get("belt0").toolKeys), NConfiguration.getInstance().dragWidgets.get("belt0").coord.x, NConfiguration.getInstance().dragWidgets.get("belt0").coord.y);
        t2 = add(new NToolBelt("belt1", 120, 4, NConfiguration.getInstance().toolBelts.get("belt1").toolKeys), NConfiguration.getInstance().dragWidgets.get("belt1").coord.x, NConfiguration.getInstance().dragWidgets.get("belt1").coord.y);
        t3 = add(new NToolBelt("belt2", 108, 4, NConfiguration.getInstance().toolBelts.get("belt2").toolKeys), NConfiguration.getInstance().dragWidgets.get("belt2").coord.x, NConfiguration.getInstance().dragWidgets.get("belt2").coord.y);
        timers = add(new NTimerPanel(), 250, 100);
        timers.hide();
        chat = add(new NChatUIDrag("ChatUI"),NConfiguration.getInstance().dragWidgets.get("ChatUI").coord);
        botsInfo = add ( new NBotsInfo ( this ), new Coord ( 30, 150 ) );
        stats = add(new NQuestsStats(), new Coord ( 30, 150 ));
        questInfo = add(new NQuestInfo () , NConfiguration.getInstance().dragWidgets.get("NQuestInfo").coord);
        chat.resize(NConfiguration.getInstance().resizeWidgets.get("ChatUI"));
        syslog = chat.chat.add(new ChatUI.Log("System"));
        NConfiguration.getInstance().disabledCheck = true;
    }

    public NMapView getMap () {
        return ( NMapView ) map;
    }
    public boolean getInspectMode(){
        return ((NUI)ui).inspectMode;
    }


    @Override
    public void tick(double dt) {
        questInfo.tick(dt);
        super.tick(dt);
    }

    @Override
    public void resize(Coord sz) {
        super.resize(sz);
    }
    public void addchild(Widget child, Object... args) {
        String place = ((String) args[0]).intern();
        if (place == "chat") {
            chat.chat.addchild(child);
        } else if (place == "craft") {
            if (craftwnd == null) {
                craftwnd = add(new NCraftWindow(), new Coord(400, 200));
            }
            craftwnd.add(child);
            craftwnd.pack();
            craftwnd.raise();
            craftwnd.show();
        } else {
            super.addchild(child, args);
            if (place.equals("mapview")) {
                pathQueue = new NPathQueue(map);
                ((NOCache) ui.sess.glob.oc).paths.path = this.pathQueue;
                mmapw = add(new NMiniMapWnd("MiniMap", (NMapView) map, mapfile.file), NConfiguration.getInstance().dragWidgets.get("MiniMap").coord);
                mmap = mmapw.miniMap;
                mmapw.resize(NConfiguration.getInstance().resizeWidgets.get("MiniMap"));
            }
        }
    }

    @Override
    public void dispose() {
        if(pathQueue!=null)
            pathQueue.clear();
        super.dispose();
    }

    public Window getWindow ( String cap ) {
        for ( Widget w = lchild ; w != null ; w = w.prev ) {
            if ( w instanceof Window ) {
                Window wnd = ( Window ) w;
                if ( ( wnd.cap != null && NUtils.checkName ( wnd.cap.text,
                        new NAlias ( new ArrayList<>( Arrays.asList ( cap ) ),
                                new ArrayList<> ( Arrays.asList ( "Crafte" ) ) ) ) ) ) {
                    return ( Window ) w;
                }
            }
        }
        return null;
    }

    public double getProg(){
        if(prog == null){
            return -1;
        }else{
            return prog.prog;
        }
    }

    public NEquipory getEquipment () {
        if ( equwnd != null ) {
            for ( Widget w = equwnd.lchild ; w != null ; w = w.prev ) {
                if ( w instanceof Equipory ) {
                    return ( NEquipory ) w;
                }
            }
        }
        return null;
    }

    public IMeter.Meter getmeter (
            String name,
            int midx
    ) {
        List<IMeter.Meter> meters = getmeters ( name );
        if ( meters != null && midx < meters.size () ) {
            return meters.get ( midx );
        }
        return null;
    }

    public List<IMeter.Meter> getmeters (String name ) {
        for ( Widget meter : meters ) {
            if ( meter instanceof IMeter ) {
                IMeter im = ( IMeter ) meter;
                Resource res = im.bg.get ();
                if ( res != null ) {
                    if ( res.basename ().equals ( name ) ) {
                        return im.meters;
                    }
                }
            }
        }
        return null;
    }

    public NInventory getInventory ( String name ) {
        return getInventory(name, true);
    }
    public boolean isStockpile (  ) {
        Window spwnd = getWindow("Stockpile");
        if(spwnd!=null) {
            for (Widget sp = spwnd.lchild; sp != null; sp = sp.prev) {
                /// Выбираем внутренний контейнер
                if (sp instanceof NISBox) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isBarter (  ) {
        Window spwnd = getWindow("Barter Stand");
        if(spwnd!=null) {
            for (Widget sp = spwnd.lchild; sp != null; sp = sp.prev) {
                /// Выбираем внутренний контейнер
                if (sp instanceof Shopbox) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isBarrel (  ) {
        Window spwnd = getWindow("Barrel");
        if(spwnd!=null) {
            for (Widget sp = spwnd.lchild; sp != null; sp = sp.prev) {
                /// Выбираем внутренний контейнер
                if (sp instanceof RelCont) {
                    for(Pair<Widget, Supplier<Coord>> pair:((RelCont) sp).childpos)
                        if(pair.a instanceof TipLabel)
                            return true;
                }
            }
        }
        return false;
    }

    public double getBarrelContent(NAlias content){
        Window spwnd = getWindow ( "Barrel" );
        if(spwnd!=null) {
            for (Widget sp = spwnd.lchild; sp != null; sp = sp.prev) {
                /// Выбираем внутренний контейнер
                if (sp instanceof RelCont) {
                    for(Pair<Widget, Supplier<Coord>> pair:((RelCont) sp).childpos)
                        if(pair.a instanceof TipLabel)
                            for(ItemInfo inf : ((TipLabel)pair.a).info) {
                                if(inf instanceof ItemInfo.Name) {
                                    String name = ((ItemInfo.Name) inf).str.text;
                                    if (NUtils.checkName(name.toLowerCase(), content))
                                        return Double.parseDouble(name.substring(0, name.indexOf(' ')));
                                }else if(inf instanceof ItemInfo.AdHoc){
                                    if (NUtils.checkName(((ItemInfo.AdHoc)inf).str.text, "Empty")) {
                                        return 0;
                                    }
                                }
                            }
                }
            }
        }
        return -1;
    }

    public NInventory getInventory ( String name , boolean check) {
        Window spwnd = getWindow ( name );
        if(spwnd == null && check){
            try {
                NUtils.waitEvent(()->getWindow(name)!=null,200);
            } catch (InterruptedException e) {
            }
        }
        spwnd = getWindow(name);
        if ( spwnd != null ) {
            for ( Widget sp = spwnd.lchild ; sp != null ; sp = sp.prev ) {
                if ( sp instanceof Inventory ) {
                    return ( ( NInventory ) sp );
                }
            }
        }
        return null;
    }

    public int getMenuGridId () {
        int id = 0;
        /// Проверяем все зарегистрированные виджеты
        for ( Map.Entry<Widget, Integer> widget : ui.rwidgets.entrySet () ) {
            if ( widget.getKey () instanceof NMenuGrid ) {
                /// Если проверяемый виджет - Экипировка возвращаем id
                id = widget.getValue ();
            }
        }
        return id;
    }

    private final Map<MapFile.Marker, Widget> trackedMarkers = new HashMap<>();

    public void track(MapFile.Marker marker) {
        untrack(marker);
        try {
            Factory f = Widget.gettype2("ui/locptr");
            if(f != null) {
                Widget wdg = f.create(ui, new Object[]{marker});
                trackedMarkers.put(marker, wdg);
                add(wdg);
            }
        } catch (InterruptedException ignored) {
        }
    }

    public void untrack(MapFile.Marker marker) {
        Widget wdg = trackedMarkers.remove(marker);
        if(wdg != null) {
            if(marker instanceof MapWnd2.GobMarker){
                mapfile.untrack(((MapWnd2.GobMarker) marker).gobid);
            }
            wdg.reqdestroy();
        }
    }

}
