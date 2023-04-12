package nurgling.bots.actions;

import haven.GItem;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NHitBox;
import nurgling.NUtils;
import nurgling.tools.AreasID;

import java.util.ArrayList;
import java.util.Arrays;

public class TransferTrash implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
    
        ArrayList<GItem> items = gui.getInventory ()
                                    .getWItems( new NAlias( new ArrayList<String> ( Arrays.asList ( "entrails" ) ) ),
                                            AreasID.getTh(AreasID.entr), false );
        /// Переносим предметы в инвентарь
        for ( GItem item : items ) {
            NUtils.drop ( item );
        }

        ArrayList<GItem> items2 = gui.getInventory ().getWItems(
                new NAlias ( new ArrayList<String> ( Arrays.asList ( "intestines" ) ) ), AreasID.getTh(AreasID.inten), false );
        /// Переносим предметы в инвентарь
        
        for ( GItem item : items2 ) {
            NUtils.drop ( item );
        }
        new TransferItemsToBarter ( AreasID.inten,new NAlias ( "intest" ), false ).run ( gui );
        new TransferItemsToBarter (AreasID.entr,new NAlias ( "entrails" ), false ).run ( gui );
        
        
        new TransferToPile ( AreasID.inten, NHitBox.getByName ( "stockpile" ), new NAlias ( "stockpile" ),
                new NAlias ( new ArrayList<> ( Arrays.asList ( "intestines" ) ) ) ).run ( gui );
        new TransferToPile ( AreasID.entr, NHitBox.getByName ( "stockpile" ), new NAlias ( "stockpile" ),
                new NAlias ( new ArrayList<> ( Arrays.asList ( "entrails" ) ) ) ).run ( gui );
        NUtils.waitEvent (
                () -> !gui.getInventory ().getWItems( new NAlias ( new ArrayList<> ( Arrays.asList ( "entrails" ) ) ) )
                          .isEmpty (), 5 );
        
        return new Results ( Results.Types.SUCCESS );
    }
}
