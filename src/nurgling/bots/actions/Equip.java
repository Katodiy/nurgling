package nurgling.bots.actions;

import haven.Coord;
import haven.Coord2d;
import haven.GItem;
import haven.WItem;

import nurgling.*;
import nurgling.tools.Finder;

public class Equip implements Action{
    public Equip(NAlias name) {
        this.name = name;
    }
    public Equip(NAlias name, NAlias exception) {
        this.name = name;
        this.exception = exception;
    }



    @Override
    public Results run(NGameUI gui) throws InterruptedException {

        /// Если предмет из списка уже надет, то ничего не нужно делать
        if (Finder.findDressedItem ( name ) != null ) {
            return new Results(Results.Types.SUCCESS);
        }

        NUtils.freeHands (exception);
        GItem item = null;
        WItem wbelt = Finder.findDressedItem ( new NAlias ("belt") );
        if(wbelt!=null) {
            NInventory belt = ((NInventory)wbelt.item.contents);
            item = belt.getItem ( name );
            if(item!=null) {
                NUtils.takeItemToHand(item);
                /// Дожидаемся окончания экипировки
                NUtils.waitEvent(() -> gui.vhand != null, 50);
                NUtils.getEquipment().wdgmsg("drop",-1);
                NUtils.waitEvent(() -> gui.vhand == null, 50);
            }
            if(Finder.findDressedItem ( name ) != null)
                return new Results(Results.Types.SUCCESS);
        }
        item = gui.getInventory ().getItem ( name );

        /// Одеваем
        if ( item != null ) {
            NUtils.getGameUI().setfocus(NUtils.getGameUI().getEquipment());
            /// Иначе освобождаем руки и берем нужный предмет
            item.wdgmsg ( "transfer", Coord.z, 1 );
            NUtils.getGameUI().getEquipment().lostfocus();
            /// Дожидаемся окончания экипировки
            NUtils.waitEvent(()-> Finder.findDressedItem ( name ) != null,50);
            if(Finder.findDressedItem ( name ) == null) {
                return new Results(Results.Types.FAIL);
            }
            return new Results(Results.Types.SUCCESS);
        }
        if(Finder.findDressedItem ( name ) != null)
            return new Results(Results.Types.SUCCESS);
        else
            return new Results(Results.Types.NO_ITEMS);
    }

    NAlias name;

    NAlias exception = new NAlias("bucket", "traveller");
}
