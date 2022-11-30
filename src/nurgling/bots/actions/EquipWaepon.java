package nurgling.bots.actions;

import haven.Coord;
import haven.WItem;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NInventory;
import nurgling.NUtils;
import nurgling.tools.Finder;

import static haven.OCache.posres;

public class EquipWaepon implements Action {
    NAlias shiled =  new NAlias("shield");
    NAlias sword =  new NAlias("sword");
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {

        /// Если предмет из списка уже надет, то ничего не нужно делать
        if (Finder.findDressedItem ( shiled ) != null && Finder.findDressedItem ( sword) != null ) {
            return new Results(Results.Types.SUCCESS);
        }

        /// Иначе освобождаем руки и берем нужный предмет

        new Equip(shiled).run(gui);
        new Equip(sword, shiled).run(gui);




        return new Results(Results.Types.SUCCESS);
    }

}
