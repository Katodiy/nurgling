package nurgling.bots.actions;

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
                Results.Types res = new OpenBelt ().run ( gui ).type;
                if ( res == Results.Types.SUCCESS ) {
                    NInventory belt = gui.getInventory ( "elt" );
                    ArrayList<WItem> wskins = belt.getItems ( new NAlias ( "waterskin" ) );
                    for ( WItem witem : wskins ) {
                        if ( NUtils.isContentWater ( witem.item ) ) {
                            if ( new SelectFlowerAction ( witem, "Drink", SelectFlowerAction.Types.Inventory )
                                    .run ( gui ).type != Results.Types.SUCCESS ) {
                                belt.parent.destroy ();
                                return new Results ( Results.Types.SELECT_FLOWER_FAIL );
                            }
                            if ( new BarChangeAction<> ( NUtils::getStamina,
                                    () -> NUtils.getStamina() >= stopLvl ).run ( gui ).type ==
                                    Results.Types.SUCCESS ) {
                                belt.parent.destroy ();
                                return new Results ( Results.Types.SUCCESS );
                            }
                        }
                    }
                    belt.parent.hide ();
                }
                else if ( res == Results.Types.BELT_FAIL ) {
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
