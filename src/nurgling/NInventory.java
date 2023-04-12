package nurgling;

import haven.*;
import haven.res.ui.tt.slot.Slotted;
import haven.res.ui.tt.stackn.Stack;

import java.util.*;

public class NInventory extends Inventory {
    public NSearchWidget searchwdg;
    public NPopUpWidget toggles;
    public ICheckBox bundle;

    public MenuGrid.Pagina pagBundle = null;

    public NInventory(Coord sz) {
        super(sz);
    }

    boolean showPopup = false;

    @Override
    public void resize(Coord sz) {
        super.resize(new Coord(sz));
        searchwdg.resize(new Coord(sz.x , 0));
        searchwdg.move(new Coord(0,sz.y + UI.scale(5)));
        parent.pack();


        movePopup(parent.c);
    }

    /**
     * Процедура запроса предмета из инвентаря
     *
     * @param key ключ имя предмета
     * @return Предмет из инвентаря
     */
    public GItem getItem(NAlias key) throws InterruptedException {
        waitLoading();
        /// Рзбираются компоненты инвентаря
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof GItem) {
                /// Для каждого найденго в компонентах предмета осуществляется проверка на его соответствие ключу
                if (NUtils.isIt((GItem) widget, key)) {
                    /// Если предмет соответствует , то возвращааем его
                    return ((GItem) widget);
                }
            }
        }
        return null;
    }

    public WItem getItem(GItem item) throws InterruptedException {
        waitLoading();
        /// Рзбираются компоненты инвентаря
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                /// Для каждого найденго в компонентах предмета осуществляется проверка на его соответствие ключу
                if (((WItem) widget).item == item) {
                    /// Если предмет соответствует , то возвращааем его
                    return (WItem) widget;
                }
            }
        }
        return null;
    }

    public GItem getItem(NAlias key, Class <? extends ItemInfo> candidate) throws InterruptedException {
        waitLoading();
        /// Рзбираются компоненты инвентаря
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                /// Для каждого найденго в компонентах предмета осуществляется проверка на его соответствие ключу
                if (NUtils.isIt((WItem) widget, key)) {
                    for(ItemInfo inf : ((WItem) widget).item.info()){
                        if(inf.getClass() == candidate){
                            return ((WItem) widget).item;
                        }
                    }

                }
            }
        }
        return null;
    }

    public boolean findItem(GItem item) {
        waitLoading();
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                /// Для каждого найденго в компонентах предмета осуществляется проверка на его соответствие ключу
                if (((WItem) widget).item == item) {
                    /// Если предмет соответствует , то возвращаем true
                    return true;
                }
            }
        }
        return false;
    }

    public enum QualityType {
        High, Low
    }

    /**
     * Процедура запроса предмета из инвентаря
     *
     * @param key ключ имя предмета
     * @return Предмет из инвентаря
     */
    public WItem getItem(
            NAlias key,
            QualityType type
    )
            throws InterruptedException {
        waitLoading();
        double quality = (type == QualityType.High) ? -1 : 10000;
        WItem res = null;
        /// Рзбираются компоненты инвентаря
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                /// Для каждого найденго в компонентах предмета осуществляется проверка на его соответствие ключу
                if (NUtils.isIt((WItem) widget, key)) {
                    double q = ((NGItem)((WItem) widget).item).quality();
                    if (((type == QualityType.High) ? (q > quality) : (q < quality))) {
                        res = (WItem) widget;
                        quality = q;
                    }
                }
            }
        }
        return res;
    }


    public GItem getItem(
            double quality,
            NAlias name
    )
            throws InterruptedException {
        waitLoading();
        /// Рзбираются компоненты инвентаря
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                /// Для каждого найденго в компонентах предмета осуществляется проверка на его соответствие ключу
                if (NUtils.isIt((WItem) widget, name) &&
                        quality == (((NGItem)((WItem) widget).item).quality())) {
                    /// Если предмет соответствует , то возвращааем его
                    return ((WItem) widget).item;
                }
            }
        }
        return null;
    }


    /**
     * Процедура запроса предмета из инвентаря
     *
     * @param key ключ имя предмета
     * @return Предмет из инвентаря
     */
    public WItem getItem(
            NAlias key,
            double q
    )
            throws InterruptedException {
        waitLoading();
        /// Рзбираются компоненты инвентаря
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                WItem wdg = (WItem) widget;
                /// Для каждого найденго в компонентах предмета осуществляется проверка на его соответствие ключу
                if (NUtils.isIt(wdg, key)) {
                    if ((((NGItem) (((WItem) wdg).item)).quality()) >= q)
                    /// Если предмет соответствует , то возвращааем его
                    {
                        return wdg;
                    }
                }
            }
        }
        return null;
    }

    public GItem getItem(
            NAlias key,
            double q,
            int freeSpace
    )
            throws InterruptedException {
        waitLoading();
        /// Рзбираются компоненты инвентаря
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                WItem wdg = (WItem) widget;
                /// Для каждого найденго в компонентах предмета осуществляется проверка на его соответствие ключу
                if (NUtils.isIt(wdg, key)) {

                    if ((((NGItem) (((WItem) wdg).item)).quality()) >= q)
                    /// Если предмет соответствует , то возвращааем его
                    {
                        Coord size = new Coord((wdg.item.spr.sz().x + 1) / sqsz.x, (wdg.item.spr.sz().y + 1) / sqsz.y);
                        if (size.x * size.y <= freeSpace)
                            return ((WItem) wdg).item;
                    }
                }
            }
        }
        return null;
    }

    public boolean isInInventory(GItem item) throws InterruptedException {
        waitLoading();
        /// Рзбираются компоненты инвентаря
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                /// Для каждого найденго в компонентах предмета осуществляется проверка на его соответствие ключу
                if (item == ((WItem) (widget)).item) {
                    return true;
                }
            }
        }
        return false;
    }

    public ArrayList<GItem> getWItems(
            final NAlias names,
            double q
    ) {
        waitLoading();
        ArrayList<GItem> result = new ArrayList<>();
            for (Widget widget = child; widget != null; widget = widget.next) {
                if (widget instanceof WItem) {
                    NGItem wdg = (NGItem) ((WItem) widget).item;
                    if (NUtils.checkName(wdg.name(), names)) {
                        if ((wdg.quality()) >= q) {
                            result.add(wdg);
                        }
                    }
                }
            }
        return result;
    }

    public ArrayList<GItem> getWItems(
            Class<?> cl
    ) {
        waitLoading();
        ArrayList<GItem> result = new ArrayList<>();
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                NGItem wdg = (NGItem) ((WItem) widget).item;
                if (wdg.isSeached) {
                    result.add(wdg);
                }
            }
        }
        return result;
    }

    public ArrayList<GItem> getWItems(
            final NAlias names,
            double q,
            boolean isMore
    )
            throws InterruptedException {
        waitLoading();
        ArrayList<GItem> result = new ArrayList<>();
        /// Рзбираются компоненты инвентаря
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                /// Для каждого найденго в компонентах предмета осуществляется проверка на его соответствие ключу
                if (NUtils.isIt((WItem) widget, names))
                {
                    if (isMore) {
                        if ((((NGItem)((WItem) widget).item).quality()) >= q) {
                            result.add(((WItem) widget).item);
                        }
                    } else {
                        if  ((((NGItem)((WItem) widget).item).quality()) <= q) {
                            result.add(((WItem) widget).item);
                        }
                    }
                }
            }
        }
        return result;
    }

    public ArrayList<GItem> getGItems(
            final NAlias names,
            double q,
            boolean isMore
    )
            throws InterruptedException {
        waitLoading();
        ArrayList<GItem> result = new ArrayList<>();
        /// Рзбираются компоненты инвентаря
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                /// Для каждого найденго в компонентах предмета осуществляется проверка на его соответствие ключу
                if (NUtils.checkName(((NGItem)((NWItem) widget).item).name(), names))
                {
                    if (isMore) {
                        if ((((NGItem)((WItem) widget).item).quality()) >= q) {
                            result.add(((WItem) widget).item);
                        }
                    } else {
                        if  ((((NGItem)((WItem) widget).item).quality()) <= q) {
                            result.add(((WItem) widget).item);
                        }
                    }
                }
            }
        }
        return result;
    }

    public ArrayList<GItem> getWItems(
    ) throws InterruptedException {
        waitLoading();
        ArrayList<GItem> result = new ArrayList<>();
        /// Рзбираются компоненты инвентаря
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                result.add(((WItem) widget).item);
            }
        }
        return result;
    }


    public ArrayList<WItem> getAll()
            throws InterruptedException {
        waitLoading();
        ArrayList<WItem> result = new ArrayList<>();
        /// Рзбираются компоненты инвентаря
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                while (((WItem) widget).item.spr == null) {
                    Thread.sleep(10);
                }
                /// Для каждого найденго в компонентах предмета осуществляется проверка на его соответствие ключу
                result.add((WItem) widget);
            }
        }
        return result;
    }

    /**
     * Получить количество предметов заданного типа в инвентаре
     *
     * @param key Ключ
     * @return Количество
     */
    public int getNumberItem(NAlias key) {
        waitLoading();
        int result = 0;
        /// Рзбираются компоненты инвентаря
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                GItem item = ((WItem) widget).item;
                if (item != null) {
                    /// Для каждого найденго в компонентах предмета осуществляется проверка на его соответствие ключу
                    if (NUtils.isIt(item, key)) {
                        result += 1;
                    }
                }
            }
        }
        return result;
    }

    public ArrayList<GItem> getWItems(
            NAlias name
    ) {
        waitLoading();
        ArrayList<GItem> result = new ArrayList<>();
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                NGItem wdg = (NGItem) ((WItem) widget).item;
                if (NUtils.checkName(wdg.res.get().name, name)) {
                    result.add(wdg);
                }
            }
        }
        return result;
    }

    public ArrayList<GItem> getGItems(
            NAlias name
    ) {
        waitLoading();
        ArrayList<GItem> result = new ArrayList<>();
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                NGItem wdg = (NGItem) ((WItem) widget).item;
                if (NUtils.checkName(wdg.name(), name)) {
                    result.add(wdg);
                }
            }
        }
        return result;
    }

    /**
     * Получить свободное место
     *
     * @return свободное место
     */
    public int getFreeSpace() throws InterruptedException {
        waitLoading();
        int freespace = 0;
        if(parent instanceof NGameUI || parent instanceof Window) {
            boolean[][] inventory = new boolean[isz.x][isz.y];
            fillInventorySpace(inventory);
            for (int i = 0; i < isz.x; i++) {
                for (int j = 0; j < isz.y; j++) {
                    if (!inventory[i][j])
                        freespace++;
                }
            }
        }
        else {
            freespace = isz.x * isz.y;
            for (Widget wdg = child; wdg != null; wdg = wdg.next) {
                if (wdg instanceof WItem) {
                    freespace -= (wdg.sz.x * wdg.sz.y) / (sqsz.x * sqsz.y);
                }
            }
        }
        return freespace;
    }

    private void fillInventorySpace(boolean[][] inventory) {
        for (int i = 0; i < isz.x; i++) {
            for (int j = 0; j < isz.y; j++) {
                inventory[i][j] = false;
            }
        }
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                WItem wdg = (WItem) widget;
                Coord pos = new Coord(wdg.c.x / (sqsz.x  - UI.scale(1)), wdg.c.y / (sqsz.x  - UI.scale(1)));
                Coord size = ((NGItem) wdg.item).sprSz();
                Coord endPos = new Coord(pos.x + size.x, pos.y + size.y );
                for (int i = pos.x; i < endPos.x; i++) {
                    for (int j = pos.y; j < endPos.y; j++) {
                        inventory[i][j] = true;
                    }
                }
            }
        }
    }

    public Coord getFreeCoord(WItem item) throws InterruptedException {
        waitLoading();
        boolean[][] inventory = new boolean[isz.x][isz.y];
        fillInventorySpace(inventory);

        Coord size = new Coord(((NGItem)item.item).sprSz());
        for (int i = 0; i < isz.x; i++) {
            for (int j = 0; j < isz.y; j++) {
                if (!inventory[i][j]) {
                    if (i + size.x - UI.scale(1) < isz.x && j + size.y - UI.scale(1) < isz.y) {
                        boolean isFree = true;
                        for (int k = i; k < i + size.x; k++) {
                            for (int n = j; n < j + size.y; n++) {
                                if (inventory[k][n]) {
                                    isFree = false;
                                    break;
                                }
                            }
                        }
                        if (isFree) {
                            return new Coord(i, j);
                        }
                    }
                }
            }
        }
        return new Coord(-1, -1);
    }

    public int getNumberFreeCoord(GItem item) throws InterruptedException {
        waitLoading();
        if (item != null) {
            boolean[][] inventory = new boolean[isz.x][isz.y];
            fillInventorySpace(inventory);
            int count = 0;
            Coord size = ((NGItem)item).sprSz();
            if (NUtils
                    .isIt(item, new NAlias(new ArrayList<String>(Arrays.asList("pickaxe", "bough"))))) {
                size.y = 2;
            }
            for (int i = 0; i < isz.x; i++) {
                for (int j = 0; j < isz.y; j++) {
                    if (!inventory[i][j]) {
                        if (i + size.x - UI.scale(1) < isz.x && j + size.y - UI.scale(1) < isz.y) {
                            boolean isFree = true;
                            for (int k = i; k < i + size.x; k++) {
                                for (int n = j; n < j + size.y; n++) {
                                    if (inventory[k][n]) {
                                        isFree = false;
                                        break;
                                    }
                                }
                            }
                            if (isFree) {
                                count += 1;
                            }
                        }
                    }
                }
            }
            return count;
        } else {
            return isz.x * isz.y;
        }
    }

    public int getNumberFreeCoord(Coord target_size) throws InterruptedException {
        waitLoading();
        int count = 0;
        /// Вычисляем свободные слоты в инвентаре
        boolean[][] inventory = new boolean[isz.x][isz.y];
        fillInventorySpace(inventory);
        for (int i = 0; i < isz.x; i++) {
            for (int j = 0; j < isz.y; j++) {
                if (!inventory[i][j]) {
                    if (i + target_size.x - 1 < isz.x && j + target_size.y - 1 < isz.y) {
                        boolean isFree = true;
                        for (int k = i; k < i + target_size.x; k++) {
                            for (int n = j; n < j + target_size.y; n++) {
                                if (inventory[k][n]) {
                                    isFree = false;
                                    break;
                                }
                            }
                        }
                        if (isFree) {
                            count += 1;
                            for (int k = i; k < i + target_size.x; k++) {
                                for (int n = j; n < j + target_size.y; n++) {
                                    inventory[k][n] = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return count;
    }

    public static final Comparator<NGItem> ITEM_COMPARATOR_ASC = new Comparator<NGItem>() {
        @Override
        public int compare(NGItem o1, NGItem o2) {
            return Double.compare(o1.quality(), o2.quality());
        }
    };
    public static final Comparator<NGItem> ITEM_COMPARATOR_DESC = new Comparator<NGItem>() {
        @Override
        public int compare(NGItem o1, NGItem o2) {
            return ITEM_COMPARATOR_ASC.compare(o2, o1);
        }
    };

    private List<NGItem> getSame(GItem item, Boolean ascending) {
        waitLoading();
        List<NGItem> items = new ArrayList<>();
        if (item != null && item.res != null) {
            boolean isMeat = (NUtils.isIt(item,"meat"));
            NAlias name =(isMeat)? new NAlias(NUtils.getInfo(item)) : new NAlias(item.res.get().name);
            for (Widget wdg = lchild; wdg != null; wdg = wdg.prev) {
                if (wdg.visible && wdg instanceof NWItem) {
                    NWItem wItem = (NWItem) wdg;
                    if(isMeat) {
                        if (NUtils.isItInfo(wItem.item, name))
                            items.add((NGItem) wItem.item);
                    }else{
                        if (NUtils.isIt(wItem, name))
                            items.add((NGItem) wItem.item);
                    }
                }
            }
        }
        items.sort(ascending ? ITEM_COMPARATOR_ASC : ITEM_COMPARATOR_DESC);
        return items;
    }

    @Override
    public void wdgmsg(Widget sender, String msg, Object... args) {
        if (msg.equals("transfer-same")) {
            process(getSame((GItem) args[0], (Boolean) args[1]), "transfer");
        } else if (msg.equals("drop-same")) {
            process(getSame((GItem) args[0], (Boolean) args[1]), "drop");
        } else {
            super.wdgmsg(sender, msg, args);
        }
    }

    private void process(List<NGItem> items, String action) {
        for (GItem item : items) {
            item.wdgmsg(action, Coord.z);
        }
    }

    public boolean isLoaded(){
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                NGItem item = ((NGItem) ((WItem) widget).item);
                if ( (item.getStatus() & NGItem.NAME_IS_READY) == 0)
                    return false;
            }
        }
        return true;
    }

    public void movePopup(Coord c) {
        if(toggles !=null)
        {
           toggles.move(new Coord(c.x - toggles.sz.x + toggles.atl.x +UI.scale(10),c.y + UI.scale(35)));
        }
        if(searchwdg!=null && searchwdg.history!=null) {
            searchwdg.history.move(new Coord(c.x  + ((Window)parent).ca().ul.x + UI.scale(7), c.y + parent.sz.y- UI.scale(37)));
        }
        super.mousemove(c);
    }


    public boolean locked = false;
    @Override
    public boolean mousewheel(Coord c, int amount) {
        if(!locked) {
            return super.mousewheel(c, amount);
        }
        return false;
    }

    @Override
    public boolean drop(Coord cc, Coord ul) {
        if(!locked) {
            return super.drop(cc, ul);
        }
        return false;
    }

    @Override
    public boolean mousedown(Coord c, int button) {
        return !locked && super.mousedown(c, button);
    }

    @Override
    public void tick(double dt) {
        super.tick(dt);
        if(toggles !=null)
            toggles.visible = parent.visible && showPopup;
    }

    private static final TexI[] collapsei = new TexI[]{
            new TexI(Resource.loadsimg("nurgling/hud/buttons/itogglecu")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/itogglecd")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/itogglech")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/itogglecdh"))};

    private static final TexI[] gildingi = new TexI[]{
            new TexI(Resource.loadsimg("nurgling/hud/buttons/gilding/u")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/gilding/d")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/gilding/h")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/gilding/dh"))};

    private static final TexI[] vari = new TexI[]{
            new TexI(Resource.loadsimg("nurgling/hud/buttons/var/u")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/var/d")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/var/h")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/var/dh"))};

    private static final TexI[] stacki = new TexI[]{
            new TexI(Resource.loadsimg("nurgling/hud/buttons/stack/u")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/stack/d")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/stack/h")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/stack/dh"))};

    private static final TexI[] bundlei = new TexI[]{
            new TexI(Resource.loadsimg("nurgling/hud/buttons/bundle/u")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/bundle/d")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/bundle/h")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/bundle/dh"))};

    public void installMainInv() {
        searchwdg = new NSearchWidget(new Coord(sz));
        searchwdg.resize(sz);
        parent.add(searchwdg, (new Coord(0, sz.y + UI.scale(10))));
        parent.add(new ICheckBox(collapsei[0], collapsei[1], collapsei[2], collapsei[3]) {
                       @Override
                       public void changed(boolean val) {
                           super.changed(val);
                           showPopup = val;
                       }
                   }
                , new Coord(-gildingi[0].sz().x + UI.scale(2), UI.scale(27)));

        parent.pack();
        toggles = NUtils.getGameUI().add(new NPopUpWidget(new Coord(UI.scale(50), UI.scale(80)), NPopUpWidget.Type.RIGHT));


        Widget pw = toggles.add(new ICheckBox(gildingi[0], gildingi[1], gildingi[2], gildingi[3]) {
            @Override
            public void changed(boolean val) {
                super.changed(val);
                Slotted.show = val;
            }
        }, toggles.atl);
        pw.settip(Resource.remote().loadwait("nurgling/hud/buttons/gilding/u").flayer(Resource.tooltip).t);
        ((ICheckBox)pw).a = Slotted.show;
        pw = toggles.add(new ICheckBox(vari[0], vari[1], vari[2], vari[3]) {
            @Override
            public void changed(boolean val) {
                super.changed(val);
                NFoodInfo.show = val;
            }
        }, pw.pos("bl").add(UI.scale(new Coord(0, 5))));
        pw.settip(Resource.remote().loadwait("nurgling/hud/buttons/var/u").flayer(Resource.tooltip).t);
        ((ICheckBox)pw).a = NFoodInfo.show;
        pw = toggles.add(new ICheckBox(stacki[0], stacki[1], stacki[2], stacki[3]) {
            @Override
            public void changed(boolean val) {
                super.changed(val);
                Stack.show = val;
            }
        }, pw.pos("bl").add(UI.scale(new Coord(0, 5))));
        ((ICheckBox)pw).a = Stack.show;
        pw.settip(Resource.remote().loadwait("nurgling/hud/buttons/stack/u").flayer(Resource.tooltip).t);

        bundle = toggles.add(new ICheckBox(bundlei[0], bundlei[1], bundlei[2], bundlei[3]) {
            @Override
            public void changed(boolean val) {
                super.changed(val);
                pagBundle.button().use(new MenuGrid.Interaction(1, 0));
            }
        }, pw.pos("ur").add(UI.scale(new Coord(5, 0))));
        bundle.settip(Resource.remote().loadwait("nurgling/hud/buttons/bundle/u").flayer(Resource.tooltip).t);
        toggles.pack();
        movePopup(parent.c);
        toggles.pack();

    }

    void waitLoading(){
        try {
            NUtils.waitEvent(this::isLoaded,50);
        } catch (InterruptedException ignored) {
        }
    }
}
