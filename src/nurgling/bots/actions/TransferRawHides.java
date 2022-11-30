package nurgling.bots.actions;

import haven.WItem;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NHitBox;
import nurgling.NUtils;
import nurgling.tools.AreasID;

import java.util.ArrayList;
import java.util.Arrays;

public class TransferRawHides implements Action {
    
    public static NAlias raw_hides = new NAlias ( new ArrayList<String> ( Arrays.asList ( "blood", "raw" ) ),
            new ArrayList<String> ( Arrays.asList ( "stern", "straw" ) ) );
    
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        ArrayList<WItem> items = gui.getInventory().getItems(raw_hides, AreasID.getTh(AreasID.lqhides), false);
        /// Переносим предметы в инвентарь

        for (WItem item : items) {
            NUtils.drop(item);
        }

        new TransferItemsToBarter(AreasID.raw_hides, raw_hides, false).run(gui);

        /// остальное
        new TransferToPile(AreasID.raw_hides, NHitBox.getByName("stockpile"), new NAlias("stockpile"),
                raw_hides).run(gui);

        return new Results(Results.Types.SUCCESS);
    }
}
