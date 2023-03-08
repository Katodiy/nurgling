package nurgling.bots.actions;

import haven.GItem;
import haven.WItem;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NInventory;
import nurgling.NUtils;
import nurgling.tools.Finder;

import java.util.ArrayList;


public class Drink implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        if ( NUtils.getStamina() < stopLvl ) {
            NUtils.stopWithClick ();
            Results.Types bucket;
            if ( withDrop ) {
                bucket = new TakeAndEquip ( new NAlias ( "bucket-water" ), false ).run ( gui ).type;
                if ( bucket == Results.Types.DROP_FAIL ) {
                    withDrop = false;
                }
                
            }
            else {
                bucket = ( Finder.findDressedItem ( new NAlias( "bucket-water" ) ) !=
                        null ) ? Results.Types.SUCCESS : Results.Types.FAIL;
            }
            if ( bucket == Results.Types.SUCCESS ) {
                Thread.sleep ( 200 );
                if ( new SelectFlowerAction ( new NAlias ( "bucket-water" ), "Drink",
                        SelectFlowerAction.Types.Equipment ).run ( gui ).type != Results.Types.SUCCESS ) {
                    return new Results ( Results.Types.SELECT_FLOWER_FAIL );
                }
                if ( new BarChangeAction<> ( NUtils::getStamina, () -> NUtils.getStamina() >= stopLvl )
                        .run ( gui ).type != Results.Types.SUCCESS ) {
                    return new Results ( Results.Types.WAIT_FAIL );
                }
                if ( withDrop ) {
                    if ( new Drop ( Drop.Type.Center, new NAlias ( "bucket" ) ).run ( gui ).type !=
                            Results.Types.SUCCESS ) {
                        return new Results ( Results.Types.DROP_FAIL );
                    }
                }
            }
            else {
                WItem wbelt = Finder.findDressedItem ( new NAlias ("belt") );
                if(wbelt!=null) {
                    NInventory belt = ((NInventory)wbelt.item.contents);
                    ArrayList<GItem> wskins = belt.getItems ( new NAlias ( "waterskin" ) );
                    for ( GItem witem : wskins ) {
                        if ( NUtils.isContentWater ( witem ) ) {
                            if ( new SelectFlowerAction ( witem, "Drink", SelectFlowerAction.Types.Inventory )
                                    .run ( gui ).type != Results.Types.SUCCESS ) {
                                return new Results ( Results.Types.SELECT_FLOWER_FAIL );
                            }
                            if ( new BarChangeAction<> ( NUtils::getStamina,
                                    () -> NUtils.getStamina() >= stopLvl ).run ( gui ).type ==
                                    Results.Types.SUCCESS ) {
                                return new Results ( Results.Types.SUCCESS );
                            }
                        }
                    }
                }
                else {
                    return new Results ( Results.Types.BELT_FAIL );
                }
            }
        }
        return NUtils.getStamina() >= stopLvl ? new Results ( Results.Types.SUCCESS ) : new Results (
                Results.Types.Drink_FAIL );
    }
    
    public Drink(
            double stopLvl,
            boolean withDrop
    ) {
        this.stopLvl = stopLvl;
        this.withDrop = withDrop;
    }
    
    public Drink(
            double stopLvl,
            boolean withDrop,
            boolean nostop
    ) {
        this.stopLvl = stopLvl;
        this.withDrop = withDrop;
        nostop = true;
    }
    
    double stopLvl;
    boolean withDrop;
    boolean nostop = false;
}
