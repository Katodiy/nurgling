package nurgling.bots.actions;

import haven.Gob;
import haven.WItem;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

import java.util.ArrayList;
import java.util.Arrays;

public class CollectQuicksilver implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        if ( name == null ) {
            NUtils.ContainerProp iprop = NUtils.getContainerType ( Finder.findObject ( new NAlias("smelter")) );
            name = iprop.name;
            cap = iprop.cap;
        }
        
        ArrayList<Gob> in = Finder.findObjects ( name );
        
        ArrayList<WItem> buckets = Finder.findDressedItems ( new NAlias ( "bucket" ) );
        if(!buckets.isEmpty ()) {
            /// Находим одетое ведро и берем его в руку
            WItem activeBucket = null;
            for ( WItem bucket : buckets ) {
                if ( NUtils.checkName ( NUtils.getContent ( bucket ),
                        new NAlias ( new ArrayList<> ( Arrays.asList ( "quicksilver", "free" ) ) ) ) ) {
                    activeBucket = bucket;
                }
            }
            if ( activeBucket == null ) {
                return new Results ( Results.Types.NO_CONTAINER );
            }
            NUtils.takeItemToHand ( activeBucket.item );
            int counter = 0;
            while ( gui.vhand == null && counter < 20 ) {
                counter += 1;
                Thread.sleep ( 50 );
            }
            if ( gui.vhand == null ) {
                return new Results ( Results.Types.NO_CONTAINER );
            }
    
            for ( Gob gob : in ) {
                if ( NUtils.checkName ( NUtils.getContent ( gui.vhand ), new NAlias ( new ArrayList<> ( Arrays.asList ( "quicksilver" ) ) ) ) ) {
                    if ( NUtils.getContentNumber ( gui.vhand ) > 1 ) {
                        /// Сливаем лишнюю ртуть
                        new TransferBucketToBarrel ( AreasID.barrels, new NAlias ( new ArrayList<> ( Arrays.asList ( "quicksilver", "mercury" ) ) ) ).run (
                                gui );
                    }
                }
                /// Собираем ртуть
                new PathFinder( gui, gob ).run ();
                new OpenTargetContainer ( gob, cap ).run ( gui );
                ArrayList<WItem> items = gui.getInventory ( cap ).getItems ( new NAlias ( "mercury" ) );
                for ( WItem item : items ) {
                    item.item.wdgmsg ( "itemact", 0 );
                    while ( gui.getInventory ( cap ).findItem ( item ) ) {
                        Thread.sleep ( 50 );
                    }
                }
            }
            /// Сливаем остатки ртути
            if ( NUtils.getContentNumber ( gui.vhand ) > 0 ) {
                new TransferBucketToBarrel ( AreasID.barrels,
                        new NAlias ( new ArrayList<> ( Arrays.asList ( "quicksilver", "mercury" ) ) ) ).run ( gui );
            }
            /// Одеваем ведро назад
            NUtils.transferToEquipmentHands ();
            counter = 0;
            while ( gui.vhand != null && counter < 20 ) {
                counter += 1;
                Thread.sleep ( 50 );
            }
        }
        return new Results ( Results.Types.SUCCESS );
        
    }
    
    public CollectQuicksilver(
            NAlias name,
            String cap
    ) {
        this.name = name;
        this.cap = cap;
    }
    
    public CollectQuicksilver() {
    }
    
    NAlias name;
    String cap;
    
}
