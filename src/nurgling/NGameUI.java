package nurgling;

import haven.*;
import haven.Window;
import haven.res.ui.barterbox.Shopbox;
import haven.res.ui.tt.tiplabel.TipLabel;
import haven.res.ui.tt.relcont.RelCont;
import nurgling.json.parser.ParseException;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import haven.Button;
import haven.Label;
import haven.Window;

import java.util.ArrayList;
import java.util.Comparator;

public class NGameUI extends GameUI {
    public SearchItem itemsForSearch = null;
    public NSearchWidget searchwdg;
    public static class SearchItem
    {
        public String name ="";

        public static class Quality{
            public double val;
            public Type type;

            public Quality(double val, Type type) {
                this.val = val;
                this.type = type;
            }

            public enum Type{
                MORE,
                LOW,
                EQ
            }
        }

        public ArrayList<Quality> q= new ArrayList<>();
        public class Stat{
            public String v;
            public double a;
            public boolean isMore = false;

            public Stat(String v, double a, boolean isMore) {
                this.v = v;
                this.a = a;
                this.isMore = isMore;
            }

            public Stat(String v) {
                this.v = v;
                a = 0;
            }
        }

        public final ArrayList<Stat> food = new ArrayList<>();
        public final ArrayList<Stat> gilding = new ArrayList<>();
        boolean fgs = false;
        private void reset()
        {
            food.clear();
            gilding.clear();
            q.clear();
            fgs = false;
            name = "";
        }
        void install(String value)
        {
            synchronized (gilding) {
                reset();
                if (value.startsWith("$")) {
                    String[] items = value.split("\\s*;\\s*");
                    for (String val : items) {
                        int pos = val.indexOf(":");
                        if(val.length()>pos+1 && pos!=-1) {
                            if (val.startsWith("$name")) {
                                name = val.substring(pos+1).toLowerCase();
                            } else if (val.startsWith("$fep")) {
                                if (val.contains(":"))
                                {
                                    int minpos = val.indexOf("<");
                                    int maxpos = val.indexOf(">");
                                    if(minpos==maxpos)
                                    {
                                        food.add(new Stat (val.substring(pos+1)));
                                    }
                                    else{
                                        int endpos = Math.max(minpos,maxpos);
                                        if(val.length()>endpos+1)
                                        {
                                            try {
                                                food.add(new Stat(val.substring(pos+1,endpos),Double.parseDouble(val.substring(endpos+1)),maxpos>minpos));
                                            }catch (NumberFormatException e)
                                            {
                                                food.add(new Stat (val.substring(pos+1,endpos)));
                                            }
                                        }
                                        else
                                        {
                                            food.add(new Stat (val.substring(pos+1,endpos)));
                                        }
                                    }
                                }
                            } else if (val.startsWith("$gild")) {
                                if (val.contains(":"))
                                {
                                    int minpos = val.indexOf("<");
                                    int maxpos = val.indexOf(">");
                                    if(minpos==maxpos)
                                    {
                                        gilding.add(new Stat (val.substring(pos+1)));
                                    }
                                    else{
                                        int endpos = Math.max(minpos,maxpos);
                                        if(val.length()>endpos+1)
                                        {
                                            try {
                                                gilding.add(new Stat(val.substring(pos+1,endpos),Double.parseDouble(val.substring(endpos+1)),maxpos>minpos));
                                            }catch (NumberFormatException e)
                                            {
                                                gilding.add(new Stat (val.substring(pos+1,endpos)));
                                            }
                                        }
                                        else
                                        {
                                            gilding.add(new Stat (val.substring(pos+1,endpos)));
                                        }
                                    }
                                }
                            }
                        }
                        if (val.startsWith("$fgs")) {
                            fgs = true;
                        }
                        else if(val.startsWith("$q"))
                        {
                            int minpos = val.indexOf("<");
                            int maxpos = val.indexOf(">");
                            int eqpos = val.indexOf("=");
                            try {
                                if(minpos!=-1 && val.length()>minpos+1){
                                    double d = Double.parseDouble(val.substring(minpos+1));
                                    q.add(new Quality(d, Quality.Type.LOW));
                                }
                                else if(maxpos!=-1 && val.length()>maxpos+1){
                                    double d = Double.parseDouble(val.substring(maxpos+1));
                                    q.add(new Quality(d, Quality.Type.MORE));
                                }
                                else if(eqpos!=-1 && val.length()>eqpos+1){
                                    double d = Double.parseDouble(val.substring(eqpos+1));
                                    q.add(new Quality(d, Quality.Type.EQ));
                                }
                            }
                            catch (NumberFormatException ignored)
                            {
                            }
                        }
                    }
                } else {
                    name = value.toLowerCase();
                }
            }
        }

        public boolean isEmpty() {
            synchronized (gilding) {
                return name.isEmpty() && !fgs && gilding.isEmpty() && food.isEmpty() && q.isEmpty();
            }
        }

        public boolean onlyName() {
            synchronized (gilding) {
                return !name.isEmpty() && !fgs && gilding.isEmpty() && food.isEmpty() && q.isEmpty();
            }
        }
    }



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

    private static Pattern GOB_Q = Pattern.compile("Quality: (\\d+)");
    @Override
    public void msg(String msg, Color color, Color logcol) {
        msgtime = Utils.rtime();
        lastmsg = msgfoundry.render(msg, color);
        if(msg.contains("increased")) {
            NQuestsStats.checkReward(msg);
        }
        else if (msg.contains("Quality")) {
            if(detectedGob!=null)
            {
                Matcher m = GOB_Q.matcher(msg);
                if(m.matches()) {
                    try {
                        detectedGob.quality = Integer.parseInt(m.group(1));
                        detectedGob.addTag(NGob.Tags.quality);
                    } catch (NumberFormatException ignored) {
                    } finally {
                        detectedGob = null;
                    }
                }
            }
        }
        syslog.append(msg, logcol);
    }
    @Override
    public void msg(String msg) {
        if (toggleStatus == ToggleStatus.COMPLETED) {
            super.msg(msg);
            if (msg.contains("Stack")) {
                ((NInventory) maininv).bundle.a = !msg.contains("off");
            }
        } else {
            if (maininv != null && ((NInventory) maininv).toggles != null &&  ((NInventory) maininv).bundle!=null) {
                if (msg.contains("Stack")) {
                    if (toggleStatus == ToggleStatus.FOUND) {
                        ((NInventory) maininv).bundle.a = msg.contains("off");
                        toggleStatus = ToggleStatus.CHECKED;
                    } else if (toggleStatus == ToggleStatus.READY) {
                        toggleStatus = ToggleStatus.COMPLETED;
                    }
                }
            }
        }
    }

    public boolean updated() {
        return map!=null && map.glob!=null && map.glob.map!=null && map.glob.map.isLoaded() && map.player()!=null;
    }

    public NQuestsStats getStats() {
        return stats;
    }

    private Gob detectedGob = null;
    public void setDetectGob(Gob gob) {
        detectedGob = gob;
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
        super.tick(dt);
        questInfo.tick(dt);
        if (toggleStatus != ToggleStatus.COMPLETED) {
            if (maininv != null && menu != null && !menu.paginae.isEmpty()) {
                if (toggleStatus == ToggleStatus.NOTINTI) {
                    if (((NInventory) maininv).pagBundle == null) {
                        try {
                            for (MenuGrid.Pagina p : NUtils.getGameUI().menu.paginae) {
                                if (p.res().name.contains("paginae/act/itemcomb")) {
                                    toggleStatus = ToggleStatus.FOUND;
                                    (((NInventory) maininv).pagBundle = p).button().use(new MenuGrid.Interaction(1, 0));
                                    break;
                                }
                            }
                        } catch (Loading ignore) {
                        }
                    }
                } else {
                    if (toggleStatus == ToggleStatus.CHECKED) {
                        (((NInventory) maininv).pagBundle.button()).use(new MenuGrid.Interaction(1, 0));
                        toggleStatus = ToggleStatus.READY;
                    }
                }
            }
        }
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
        if (place.equals("chr") && chrwdg != null) {
            ((NUI) ui).sessInfo.characterInfo.setCharWnd(chrwdg);
        }
        if (maininv != null && searchwdg == null) {
            ((NInventory)maininv).installMainInv();
            searchwdg = ((NInventory)maininv).searchwdg;
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

    public Window getWindowWithButton ( String cap, String button ) {
        for ( Widget w = lchild ; w != null ; w = w.prev ) {
            if ( w instanceof Window ) {
                Window wnd = ( Window ) w;
                if ( wnd.cap != null && wnd.cap.text.equals(cap)) {
                    for(Widget w2 = wnd.lchild ; w2 !=null ; w2= w2.prev )
                    {
                        if ( w2 instanceof Button ) {
                            Button b = ((Button)w2);
                            if(b.text!=null && b.text.text.equals(button)){
                                return (Window)w;
                            }
                        }
                    }
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

    public double getTableMod() {
        double table_mod = 1;
        Window table = getWindowWithButton("Table", "Feast!");
        if(table!=null)
        {
            for (Widget wdg = table.child; wdg != null; wdg = wdg.next) {
                if (wdg instanceof Label) {
                    Label text = (Label) wdg;
                    if (text.texts.contains("Food")) {
                        table_mod = table_mod + Double.parseDouble(text.texts.substring(text.texts.indexOf(":") + 1, text.texts.indexOf("%"))) / 100.;
                        break;
                    }
                }
            }
        }
        return table_mod;
    }

    public double getRealmMod() {
        double realmBuff = 0;
        for (Widget wdg = child; wdg != null; wdg = wdg.next) {
            if (wdg instanceof GameUI.Hidepanel) {
                for (Widget wdg1 = wdg.child; wdg1 != null; wdg1 = wdg1.next) {
                    if (wdg1 instanceof Bufflist) {
                        for (Widget pbuff = wdg1.child; pbuff != null; pbuff = pbuff.next) {
                            if (pbuff instanceof Buff) {
                                if (NUtils.checkName(((Buff) pbuff).res.get().name, new NAlias("realm"))) {
                                    ArrayList<ItemInfo> realm = new ArrayList<>(((Buff) pbuff).info());
                                    for (Object data : realm) {
                                        if (data instanceof ItemInfo.AdHoc) {
                                            ItemInfo.AdHoc ah = ((ItemInfo.AdHoc) data);
                                            if (NUtils.checkName(ah.str.text, new NAlias("Food event"))) {
                                                realmBuff = realmBuff + Double.parseDouble(ah.str.text.substring(ah.str.text.indexOf("+") + 1, ah.str.text.indexOf("%"))) / 100.;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return realmBuff;
    }

    public int getMaxBase(){
        return chrwdg.base.stream().max(new Comparator<CharWnd.Attr>() {
            @Override
            public int compare(CharWnd.Attr o1, CharWnd.Attr o2) {
                return Integer.compare(o1.attr.base,o2.attr.base);
            }
        }).get().attr.base;
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

    enum ToggleStatus
    {
        NOTINTI,
        FOUND,
        CHECKED,
        READY,
        COMPLETED
    }
    ToggleStatus toggleStatus = ToggleStatus.NOTINTI;

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
