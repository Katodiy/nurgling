package nurgling.bots.actions;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class TransferBars implements Action {

    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        new CollectQuicksilver ().run ( gui );
        Results.Types res;
        do {
            res = new TakeMaxFromContainers (
                    new NAlias( new ArrayList<> ( Arrays.asList ( "bar", "nugget", "pebble-gold" ) ),
                            new ArrayList<> ( Arrays.asList ( "cinna" ) ) ),
                    new NAlias ( "smelter" ) , new ArrayList<>() ).run (
                    gui ).type;
//
//            new TransferItemsToBarter ( Objects.requireNonNull (NUtils.getWarhouse ( "tin" ) ) ).run ( gui );
//            new TransferItemsToBarter ( Objects.requireNonNull (NUtils.getWarhouse ( "copper" ) ) ).run ( gui );
//            new TransferItemsToBarter ( Objects.requireNonNull (NUtils.getWarhouse ( "cast" ) ) ).run ( gui );
//            new TransferItemsToBarter ( Objects.requireNonNull (NUtils.getWarhouse ( "lead" ) ) ).run ( gui );
//
//            new TransferItemsToBarter ( Objects.requireNonNull (NUtils.getWarhouse ( "nugget-gold" ) ) ).run ( gui );
//            new TransferItemsToBarter ( Objects.requireNonNull (NUtils.getWarhouse ( "pebble-gold" ) ) ).run ( gui );
//            new TransferItemsToBarter ( Objects.requireNonNull (NUtils.getWarhouse ( "bronze" ) ) ).run ( gui );
//            new TransferItemsToBarter ( Objects.requireNonNull (NUtils.getWarhouse ( "nugget-silver" ) ) ).run ( gui );
//
//            new TransferItemsToContainers ( -1, AreasID.copper, null, "", new NAlias ( "Bar of Copper" ), true ).run (
//                    gui );
//
//            new TransferItemsToContainers ( -1, AreasID.tin, null, "", new NAlias ( "Bar of Tin" ), true ).run ( gui );
//
//            new TransferItemsToContainers ( -1, AreasID.ciron, null, "", new NAlias ( "Bar of Cast Iron" ), true ).run (
//                    gui );
//
//            new TransferItemsToContainers ( -1, AreasID.lead, null, "", new NAlias ( "Bar of Lead" ), true ).run (
//                    gui );
//
//            new TransferItemsToContainers ( -1, AreasID.silver, null, "", new NAlias ( "Silver Nugget" ), true ).run (
//                    gui );
//
//            new TransferItemsToContainers ( -1, AreasID.gold, null, "",
//                    new NAlias ( new ArrayList<> ( Arrays.asList ( "Gold Nugget", "Gold Pebbles" ) ) ), true ).run (
//                    gui );
            
            
            new TransferItemsToContainers ( -1, AreasID.bar, null, "",
                    new NAlias ( new ArrayList<> ( Arrays.asList ( "bar", "pebble", "nugget" ) ) ), false ).run ( gui );
            
            
        }
        while ( res != Results.Types.FAIL );
        
        return new Results ( Results.Types.SUCCESS );
    }
}
