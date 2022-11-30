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

    public boolean isLoaded() {
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                if (((WItem) widget).item.spr == null) {
                  return false;
                }
            }
        }
        return true;
    }
    /**
     * Процедура запроса предмета из инвентаря
     *
     * @param key ключ имя предмета
     * @return Предмет из инвентаря
     */
    public WItem getItem(NAlias key) {
        /// Рзбираются компоненты инвентаря
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                /// Для каждого найденго в компонентах предмета осуществляется проверка на его соответствие ключу
                if (NUtils.isIt((WItem) widget, key)) {
                    /// Если предмет соответствует , то возвращааем его
                    return (WItem) widget;
                }
            }
        }
        return null;
    }

    public WItem getItem(GItem item) {
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

    public WItem getItem(NAlias key, Class <? extends ItemInfo> candidate) {
        /// Рзбираются компоненты инвентаря
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                /// Для каждого найденго в компонентах предмета осуществляется проверка на его соответствие ключу
                if (NUtils.isIt((WItem) widget, key)) {
                    for(ItemInfo inf : ((WItem) widget).item.info()){
                        if(inf.getClass() == candidate){
                            return (WItem) widget;
                        }
                    }

                }
            }
        }
        return null;
    }

    public boolean findItem(WItem item) {
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                /// Для каждого найденго в компонентах предмета осуществляется проверка на его соответствие ключу
                if ((WItem) widget == item) {
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
        double quality = (type == QualityType.High) ? -1 : 10000;
        WItem res = null;
        /// Рзбираются компоненты инвентаря
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                /// Для каждого найденго в компонентах предмета осуществляется проверка на его соответствие ключу
                if (NUtils.isIt((WItem) widget, key)) {
                    double q = NUtils.getWItemQuality((WItem) widget);
                    if (((type == QualityType.High) ? (q > quality) : (q < quality))) {
                        res = (WItem) widget;
                        quality = q;
                    }
                }
            }
        }
        return res;
    }


    public WItem getItem(
            double quality,
            NAlias name
    )
            throws InterruptedException {
        /// Рзбираются компоненты инвентаря
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                /// Для каждого найденго в компонентах предмета осуществляется проверка на его соответствие ключу
                if (NUtils.isIt((WItem) widget, name) &&
                        quality == NUtils.getWItemQuality((WItem) widget)) {
                    /// Если предмет соответствует , то возвращааем его
                    return (WItem) widget;
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
        /// Рзбираются компоненты инвентаря
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                /// Для каждого найденго в компонентах предмета осуществляется проверка на его соответствие ключу
                if (NUtils.isIt((WItem) widget, key)) {
                    if (NUtils.getWItemQuality((WItem) widget) >= q)
                    /// Если предмет соответствует , то возвращааем его
                    {
                        return (WItem) widget;
                    }
                }
            }
        }
        return null;
    }

    public boolean isInInventory(WItem item) {
        /// Рзбираются компоненты инвентаря
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                /// Для каждого найденго в компонентах предмета осуществляется проверка на его соответствие ключу
                if (item.item == ((WItem) (widget)).item) {
                    return true;
                }

            }
        }
        return false;
    }
    public ArrayList<WItem> getItems(
            final NAlias names
    ) throws InterruptedException {
        ArrayList<WItem> items = getItems(names,-1);
        if(items.isEmpty()){
            items = getItemsWithInfo(names,-1,true);
        }
        return items;
    }
    public ArrayList<WItem> getItems(
            final NAlias names,
            double q
    ) throws InterruptedException {
        ArrayList<WItem> result = new ArrayList<>();
        /// Рзбираются компоненты инвентаря
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                /// Для каждого найденго в компонентах предмета осуществляется проверка на его соответствие ключу
                if (NUtils.isIt((WItem) widget, names)) {
                    if(NUtils.getWItemQuality(((WItem)widget))>=q){
                        result.add((WItem) widget);
                    }
                }
            }
        }
        return result;
    }

    public ArrayList<WItem> getItems(
            final NAlias names,
            double q,
            boolean isMore
    )
            throws InterruptedException {
        ArrayList<WItem> result = new ArrayList<>();
        /// Рзбираются компоненты инвентаря
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                /// Для каждого найденго в компонентах предмета осуществляется проверка на его соответствие ключу
                if (NUtils.isIt((WItem) widget, names)) {
                    if (isMore) {
                        if (NUtils.getWItemQuality((WItem) widget) >= q) {
                            result.add((WItem) widget);
                        }
                    } else {
                        if (NUtils.getWItemQuality((WItem) widget) <= q) {
                            result.add((WItem) widget);
                        }
                    }
                }
            }
        }
        return result;
    }

    public ArrayList<WItem> getItems(
    )
            throws InterruptedException {
        ArrayList<WItem> result = new ArrayList<>();
        /// Рзбираются компоненты инвентаря
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                while (((WItem) widget).item.spr == null) {
                    Thread.sleep(10);
                }
                result.add((WItem) widget);
            }
        }
        return result;
    }

    public ArrayList<WItem> getItemsWithInfo(
            final NAlias names
    )
            throws InterruptedException {
        ArrayList<WItem> result = new ArrayList<>();
        /// Рзбираются компоненты инвентаря
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                while (((WItem) widget).item.spr == null) {
                    Thread.sleep(10);
                }
                /// Для каждого найденго в компонентах предмета осуществляется проверка на его соответствие ключу
                if (NUtils.isItInfo((WItem) widget, names)) {
                    result.add((WItem) widget);
                }
            }
        }
        return result;
    }

    public ArrayList<WItem> getItemsWithInfo(
            final NAlias names,
            double q,
            boolean isMore
    )
            throws InterruptedException {
        ArrayList<WItem> result = new ArrayList<>();
        /// Рзбираются компоненты инвентаря
        for (Widget widget = child; widget != null; widget = widget.next) {
            if (widget instanceof WItem) {
                /// Для каждого найденго в компонентах предмета осуществляется проверка на его соответствие ключу
                if (NUtils.isItInfo((WItem) widget, names)) {
                    if (isMore) {
                        if (NUtils.getWItemQuality((WItem) widget) >= q) {
                            result.add((WItem) widget);
                        }
                    } else {
                        if (NUtils.getWItemQuality((WItem) widget) <= q) {
                            result.add((WItem) widget);
                        }
                    }
                }
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
    public int getFreeSpace() {
        int freespace = isz.x * isz.y;
        for (Widget wdg = child; wdg != null; wdg = wdg.next) {
            if (wdg instanceof WItem) {

                if (NUtils.isIt((WItem) wdg, new NAlias(new ArrayList<>(
                        Arrays.asList("boarhide", "aurochshide", "reddeerhide", "moosehide", "bearhide")),
                        new ArrayList<>()))) {
                    freespace -= 4;
                } else if (NUtils.isIt((WItem) wdg,
                        new NAlias(new ArrayList<>(Arrays.asList("foxhide")), new ArrayList<>()))) {
                    freespace -= 2;

                } else {
                    freespace -= (wdg.sz.x * wdg.sz.y) / (sqsz.x * sqsz.y);
                }
            }
        }
        return freespace;
    }

    public Coord getFreeCoord(WItem item) {
        int freespace = isz.x * isz.y;
        boolean[][] inventory = new boolean[isz.x][isz.y];
        for (int i = 0; i < isz.x; i++) {
            for (int j = 0; j < isz.y; j++) {
                inventory[i][j] = false;
            }
        }
        for (Widget wdg = child; wdg != null; wdg = wdg.next) {
            if (wdg instanceof WItem) {
                Coord pos = new Coord((wdg.c.x - 1) / sqsz.x, (wdg.c.y - 1) / sqsz.y);
                Coord size = new Coord(wdg.sz.x / sqsz.x, wdg.sz.y / sqsz.y);
                Coord endPos = new Coord(pos.x + size.x - 1, pos.y + size.y - 1);
                for (int i = pos.x; i <= endPos.x; i++) {
                    for (int j = pos.y; j <= endPos.y; j++) {
                        inventory[i][j] = true;
                    }
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

    public int getNumberFreeCoord(WItem item) {
        if (item != null) {
            boolean[][] inventory = new boolean[isz.x][isz.y];
            for (int i = 0; i < isz.x; i++) {
                for (int j = 0; j < isz.y; j++) {
                    inventory[i][j] = false;
                }
            }
            for (Widget wdg = child; wdg != null; wdg = wdg.next) {
                if (wdg instanceof WItem) {
                    Coord pos = new Coord((wdg.c.x - 1) / sqsz.x, (wdg.c.y - 1) / sqsz.y);
                    Coord size = new Coord(wdg.sz.x / sqsz.x, wdg.sz.y / sqsz.y);
                    Coord endPos = new Coord(pos.x + size.x - 1, pos.y + size.y - 1);
                    for (int i = pos.x; i <= endPos.x; i++) {
                        for (int j = pos.y; j <= endPos.y; j++) {
                            inventory[i][j] = true;
                        }
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
        for (Widget wdg = child; wdg != null; wdg = wdg.next) {
            if (wdg instanceof WItem) {
                Coord pos = new Coord((wdg.c.x - 1) / sqsz.x, (wdg.c.y - 1) / sqsz.y);
                Coord size = new Coord(wdg.sz.x / sqsz.x, wdg.sz.y / sqsz.y);
                Coord endPos = new Coord(pos.x + size.x - 1, pos.y + size.y - 1);
                for (int i = pos.x; i <= endPos.x; i++) {
                    for (int j = pos.y; j <= endPos.y; j++) {
                        inventory[i][j] = true;
                    }
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

    public static final Comparator<NWItem> ITEM_COMPARATOR_ASC = new Comparator<NWItem>() {
        @Override
        public int compare(NWItem o1, NWItem o2) {
            return Double.compare(o1.quality(), o2.quality());
        }
    };
    public static final Comparator<NWItem> ITEM_COMPARATOR_DESC = new Comparator<NWItem>() {
        @Override
        public int compare(NWItem o1, NWItem o2) {
            return ITEM_COMPARATOR_ASC.compare(o2, o1);
        }
    };

    private List<NWItem> getSame(GItem item, Boolean ascending) {
        List<NWItem> items = new ArrayList<>();
        if (item != null && item.res != null) {
            boolean isMeat = (NUtils.isIt(item,"meat"));
            NAlias name =(isMeat)? new NAlias(NUtils.getInfo(item)) : new NAlias(item.res.get().name);
            for (Widget wdg = lchild; wdg != null; wdg = wdg.prev) {
                if (wdg.visible && wdg instanceof NWItem) {
                    NWItem wItem = (NWItem) wdg;
                    if(isMeat) {
                        if (NUtils.isItInfo(wItem, name))
                            items.add(wItem);
                    }else{
                        if (NUtils.isIt(wItem, name))
                            items.add(wItem);
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

    private void process(List<NWItem> items, String action) {
        for (WItem item : items) {
            item.item.wdgmsg(action, Coord.z);
        }
    }
}

