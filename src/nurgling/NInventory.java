package nurgling;

import haven.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class NInventory extends Inventory {

    public NInventory(Coord sz) {
        super(sz);
    }


    /**
     * Получить контейнеры с водой
     *
     * @return Массив контейнеров
     */
    public ArrayList<WItem> getWaterContainers(
    )
            throws InterruptedException {
        ArrayList<WItem> containers = new ArrayList<>();

        for (WItem item : getAll()) {
            if (item != null) {
                try {
                    NUtils.checkName(item.item.getres().name,
                            new NAlias(new ArrayList<>(Arrays.asList("waterskin", "waterflask", "kuksa")),
                                    new ArrayList<>()));
                } catch (Loading e) {

                }
                if (NUtils.isContentWater(item.item)) {
                    containers.add(item);
                }
            }
        }
        return containers;
    }

    /**
     * Процедура запроса предмета из инвентаря
     *
     * @param key ключ имя предмета
     * @return Предмет из инвентаря
     */
    public GItem getItem(NAlias key) throws InterruptedException {
        NUtils.waitEvent(this::isLoaded,50);
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
        NUtils.waitEvent(this::isLoaded,50);
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
        NUtils.waitEvent(this::isLoaded,50);
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
        NUtils.waitEvent(this::isLoaded,50);
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
        NUtils.waitEvent(this::isLoaded,50);
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
        NUtils.waitEvent(this::isLoaded,50);
        /// Рзбираются компоненты инвентаря
        for (WItem wdg : wmap.values()) {
            try {
                NUtils.waitEvent(() -> wdg.item != null && wdg.item.spr != null && wdg.item.info() != null && wdg.item.getinfo(ItemInfo.Name.class) != null, 50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            /// Для каждого найденго в компонентах предмета осуществляется проверка на его соответствие ключу
            if (NUtils.isIt(wdg, key)) {
                if ((((NGItem)(((WItem) wdg).item)).quality()) >= q)
                /// Если предмет соответствует , то возвращааем его
                {
                    return wdg;
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
        NUtils.waitEvent(this::isLoaded,50);
        /// Рзбираются компоненты инвентаря
        for (WItem wdg : wmap.values()) {
            try {
                NUtils.waitEvent(() -> wdg.item != null && wdg.item.spr != null && wdg.item.info() != null && wdg.item.getinfo(ItemInfo.Name.class) != null, 50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            /// Для каждого найденго в компонентах предмета осуществляется проверка на его соответствие ключу
            if (NUtils.isIt(wdg, key)) {

                if ((((NGItem)(((WItem) wdg).item)).quality()) >= q)
                /// Если предмет соответствует , то возвращааем его
                {
                    Coord size = new Coord((wdg.item.spr.sz().x+1) / sqsz.x, (wdg.item.spr.sz().y+1) / sqsz.y);
                    if(size.x*size.y<=freeSpace)
                        return ((WItem)wdg).item;
                }
            }
        }
        return null;
    }

    public boolean isInInventory(GItem item) {
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
    public ArrayList<GItem> getItems(
            final NAlias names
    ) throws InterruptedException {
        return getItems(names,-1);
    }
    public ArrayList<GItem> getItems(
            final NAlias names,
            double q
    ) throws InterruptedException {
        NUtils.waitEvent(this::isLoaded,50);
        ArrayList<GItem> result = new ArrayList<>();
        /// Рзбираются компоненты инвентаря
        for (GItem wdg : wmap.keySet()) {
            /// Для каждого найденго в компонентах предмета осуществляется проверка на его соответствие ключу
            if (NUtils.isIt(wdg, names) || (NUtils.isItInfo(wdg, names))) {
                if ((((NGItem)(wdg)).quality()) >= q) {
                    result.add(wdg);
                }
            }
        }
        return result;
    }

    public ArrayList<GItem> getItems(
            final NAlias names,
            double q,
            boolean isMore
    )
            throws InterruptedException {
        NUtils.waitEvent(this::isLoaded,50);
        ArrayList<GItem> result = new ArrayList<>();
        /// Рзбираются компоненты инвентаря
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                /// Для каждого найденго в компонентах предмета осуществляется проверка на его соответствие ключу
                if (NUtils.isIt((WItem) widget, names) || (NUtils.isItInfo(((WItem) widget).item, names)))
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

    public ArrayList<GItem> getItems(
    ) throws InterruptedException {
        NUtils.waitEvent(this::isLoaded,50);
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

    /**
     * Получить свободное место
     *
     * @return свободное место
     */
    public int getFreeSpace() throws InterruptedException {
        NUtils.waitEvent(this::isLoaded,50);
        int freespace = 0;
        if(parent instanceof NGameUI || parent instanceof Window) {
            boolean[][] inventory = new boolean[isz.x][isz.y];
            for (int i = 0; i < isz.x; i++) {
                for (int j = 0; j < isz.y; j++) {
                    inventory[i][j] = false;
                }
            }
            for (WItem wdg : wmap.values()) {
                Coord pos = new Coord(wdg.c.x / (sqsz.x - 1), wdg.c.y / (sqsz.x - 1));
                Coord size = ((NGItem)wdg.item).spriteSize;
                Coord endPos = new Coord(pos.x + size.x - 1, pos.y + size.y - 1);
                for (int i = pos.x; i <= endPos.x; i++) {
                    for (int j = pos.y; j <= endPos.y; j++) {
                        inventory[i][j] = true;
                    }
                }
            }
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

    public Coord getFreeCoord(WItem item) throws InterruptedException {
        NUtils.waitEvent(this::isLoaded,50);
        int freespace = isz.x * isz.y;
        boolean[][] inventory = new boolean[isz.x][isz.y];
        for (int i = 0; i < isz.x; i++) {
            for (int j = 0; j < isz.y; j++) {
                inventory[i][j] = false;
            }
        }
        for (WItem wdg : wmap.values()) {
            Coord pos = new Coord(wdg.c.x  / (sqsz.x-1), wdg.c.y / (sqsz.x-1));
            Coord size = ((NGItem)wdg.item).spriteSize;
            Coord endPos = new Coord(pos.x + size.x - 1, pos.y + size.y - 1);
            for (int i = pos.x; i <= endPos.x; i++) {
                for (int j = pos.y; j <= endPos.y; j++) {
                    inventory[i][j] = true;
                }
            }
        }

        Coord size = new Coord(item.sz.x / sqsz.x, item.sz.y / sqsz.y);
        for (int i = 0; i < isz.x; i++) {
            for (int j = 0; j < isz.y; j++) {
                if (!inventory[i][j]) {
                    if (i + size.x - 1 < isz.x && j + size.y - 1 < isz.y) {
                        boolean isFree = true;
                        for (int k = i; k <= i + size.x - 1; k++) {
                            for (int n = j; n <= j + size.y - 1; n++) {
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

    public int getNumberFreeCoord(GItem item) {
        if (item != null) {
            boolean[][] inventory = new boolean[isz.x][isz.y];
            for (int i = 0; i < isz.x; i++) {
                for (int j = 0; j < isz.y; j++) {
                    inventory[i][j] = false;
                }
            }
            for (WItem wdg : wmap.values()) {
                Coord pos = new Coord((wdg.c.x - 1) / sqsz.x, (wdg.c.y - 1) / sqsz.y);
                Coord size = ((NGItem)wdg.item).spriteSize;
                Coord endPos = new Coord(pos.x + size.x - 1, pos.y + size.y - 1);
                for (int i = pos.x; i <= endPos.x; i++) {
                    for (int j = pos.y; j <= endPos.y; j++) {
                        inventory[i][j] = true;
                    }
                }
            }
            int count = 0;
            Coord size = new Coord(item.sz.x / sqsz.x, item.sz.y / sqsz.y);
            if (NUtils
                    .isIt(item, new NAlias(new ArrayList<String>(Arrays.asList("pickaxe", "bough"))))) {
                size.y = 2;
            }
            for (int i = 0; i < isz.x; i++) {
                for (int j = 0; j < isz.y; j++) {
                    if (!inventory[i][j]) {
                        if (i + size.x - 1 < isz.x && j + size.y - 1 < isz.y) {
                            boolean isFree = true;
                            for (int k = i; k <= i + size.x - 1; k++) {
                                for (int n = j; n <= j + size.y - 1; n++) {
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

    public int getNumberFreeCoord(Coord target_size) {
        int count = 0;
        /// Вычисляем свободные слоты в инвентаре
        boolean[][] inventory = new boolean[isz.x][isz.y];
        for (int i = 0; i < isz.x; i++) {
            for (int j = 0; j < isz.y; j++) {
                inventory[i][j] = false;
            }
        }
        for (WItem wdg : wmap.values()) {
            try {
                NUtils.waitEvent(() -> wdg.item != null && wdg.item.spr != null && wdg.item.info() != null && wdg.item.getinfo(ItemInfo.Name.class) != null, 50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Coord pos = new Coord((wdg.c.x - 1) / sqsz.x, (wdg.c.y - 1) / sqsz.y);
            Coord size = ((NGItem)wdg.item).spriteSize;
            Coord endPos = new Coord(pos.x + size.x - 1, pos.y + size.y - 1);
            for (int i = pos.x; i <= endPos.x; i++) {
                for (int j = pos.y; j <= endPos.y; j++) {
                    inventory[i][j] = true;
                }
            }
        }

        for (int i = 0; i < isz.x; i++) {
            for (int j = 0; j < isz.y; j++) {
                if (!inventory[i][j]) {
                    if (i + target_size.x - 1 < isz.x && j + target_size.y - 1 < isz.y) {
                        boolean isFree = true;
                        for (int k = i; k <= i + target_size.x - 1; k++) {
                            for (int n = j; n <= j + target_size.y - 1; n++) {
                                if (inventory[k][n]) {
                                    isFree = false;
                                    break;
                                }
                            }
                        }
                        if (isFree) {
                            count += 1;
                            for (int k = i; k <= i + target_size.x - 1; k++) {
                                for (int n = j; n <= j + target_size.y - 1; n++) {
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


    public WItem getNew() {
        for (Widget wdg = child; wdg != null; wdg = wdg.next) {
            if (wdg instanceof WItem) {
                if (wdg.next == null) {
                    return (WItem) wdg;
                }
            }
        }
        return null;
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
        for (GItem item : wmap.keySet()) {
            if((((NGItem)item).status&NGItem.COMPLETED)==0)
                return false;
        }
        return true;
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
}

