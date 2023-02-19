package nurgling.bots.actions;

import haven.Coord;
import haven.Gob;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.bots.tools.AItem;
import nurgling.bots.tools.Ingredient;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

import java.util.ArrayList;
import java.util.Arrays;

public class SpindleMaking implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        
//        while ( true ) {
//            Gob cur_barter = Finder.findObjectInArea ( new NAlias ( "barter" ), 2000,
//                    Finder.findNearestMark ( Ingredient.get(item).area_out ) );
//            if(cur_barter==null)
//                return new Results(Results.Types.NO_CONTAINER);
//            if ( gui.getInventory ().getNumberFreeCoord (new Coord (2,1) ) < 3 ) {
//                new TransferItemsToBarter ( cur_barter, new NAlias("seersspindle"),Ingredient.get(item).area_out, false ).run ( gui );
//            }
//
//
//            Gob wool_barter = Finder.findObjectInArea ( new NAlias ( "barter" ), 2000,
//                    Finder.findNearestMark ( AreasID.special3 ) );
//            NAlias wool = new NAlias ( new ArrayList<> ( Arrays.asList ( "Mohair", "wool" ) ) );
//            if ( wool_barter == null ) {
//                new TakeFromContainers ( null, wool, 2,
//                        Finder.findNearestMark ( AreasID.wool ), "" ).run ( gui );
//            }
//            else {
//                if ( new TakeItemsFromBarter ( wool_barter, wool, AreasID.special3, false, 2 ).run ( gui ).type !=
//                        Results.Types.SUCCESS ) {
//                    return new Results ( Results.Types.NO_ITEMS );
//                }
//            }
//
//            Gob block_barter = Finder.findObjectInArea ( new NAlias ( "barter" ), 2000,
//                    Finder.findNearestMark ( AreasID.special5 ) );
//            NAlias blocks = new NAlias ( new ArrayList<> ( Arrays.asList ( "Block", "block" ) ) );
//            if ( block_barter == null ) {
//                new TakeFromContainers ( null, blocks, 1,
//                        Finder.findNearestMark ( AreasID.block ), "" ).run ( gui );
//            }
//            else {
//                if ( new TakeItemsFromBarter ( block_barter, blocks, AreasID.special5, false, 1 ).run ( gui ).type !=
//                        Results.Types.SUCCESS ) {
//                    return new Results ( Results.Types.NO_ITEMS );
//                }
//            }
//            Gob dream_barter = Finder.findObjectInArea ( new NAlias ( "barter" ), 2000,
//                    Finder.findNearestMark ( AreasID.special6 ) );
//            NAlias dreams = new NAlias ( new ArrayList<> ( Arrays.asList ( "Dream", "dream" ) ) );
//            if ( dream_barter == null ) {
//                new TakeFromContainers ( null, dreams, 1,
//                        Finder.findNearestMark ( AreasID.dream ), "" ).run ( gui );
//            }
//            else {
//                if ( new TakeItemsFromBarter ( dream_barter, dreams, AreasID.special6, false, 1 ).run ( gui ).type !=
//                        Results.Types.SUCCESS ) {
//                    return new Results ( Results.Types.NO_ITEMS );
//                }
//            }
//
//
//
//            //            new UseWorkStation ( new NAlias ( "cauldron" ), "Cauldron", "Open" ).run ( gui );
//            new Craft ( "Seer's Spindle", new char[]{ 'c', 'u', 'r', 'i' } ).run ( gui );
//
//
//            //            if ( new OpenBarrelAndTransfer ( 5000, new NAlias ( new ArrayList<> (Arrays.asList (
//            //                    "turnip" , "Turnip"))),
//            //                    AreasID.turnip,barrel )
//            //                    .run ( gui ).type == Results.Types.FULL ) {
//            //                return new Results ( Results.Types.FULL );
//            //            }
//            //            new OpenBarrelAndTransfer ( 1, )
//
//        }
        return new Results ( Results.Types.FULL );
    }
}
