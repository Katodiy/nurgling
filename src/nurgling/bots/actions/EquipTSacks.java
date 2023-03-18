package nurgling.bots.actions;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.tools.Finder;

public class EquipTSacks implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        /// Иначе освобождаем руки и берем нужный предмет

        new Equip(new NAlias("traveller"), 2).run(gui);

        return new Results(Results.Types.SUCCESS);
    }

}
