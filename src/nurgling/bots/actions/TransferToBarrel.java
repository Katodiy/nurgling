package nurgling.bots.actions;

import haven.Gob;
import haven.WItem;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;

public class TransferToBarrel implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        
        while ( !gui.getInventory ().getItems ( items ).isEmpty () ) {
            ArrayList<Gob> gobs;
            if(targetBarrel==null) {
                gobs = Finder.findObjectsInArea(new NAlias("barrel"),
                        (area == null) ? Finder.findNearestMark(id) : area);
            }else{
                gobs = new ArrayList<>();
                gobs.add(targetBarrel);
            }
            Gob gob = null;
            for ( Gob candidate : gobs ) {
                if ( !full.contains ( gob ) ) {
                    if ( NUtils.isOverlay ( candidate, items ) ) {
                        gob = candidate;
                        break;
                    }
                }
            }
            if ( gob == null ) {
                for ( Gob candidate : gobs ) {
                    if ( !full.contains ( gob ) ) {
                        if ( !NUtils.isOverlay ( candidate ) ) {
                            gob = candidate;
                            break;
                        }
                    }
                }
            }
            if ( gob == null ) {
                return new Results ( Results.Types.NO_CONTAINER );
            }
            
            new PathFinder( gui, gob ).run ();
            for ( WItem item : gui.getInventory ().getItems ( items ) ) {
                if ( gui.hand.isEmpty () ) {
                    new TakeToHand ( item ).run ( gui );
                }
                NUtils.waitEvent(()->!gui.hand.isEmpty (),200);
                NUtils.activateItem ( gob );
                NUtils.waitEvent(()->gui.hand.isEmpty (),200);
                if ( !gui.hand.isEmpty () ) {
                    full.add ( gob );
                    break;
                }
            }
        }
        return new Results ( Results.Types.SUCCESS );
    }
    
    public TransferToBarrel(
            AreasID id,
            NAlias items
    ) {
        this.id = id;
        this.items = items;
    }
    
    public TransferToBarrel(
            NArea area,
            NAlias items
    ) {
        this.area = area;
        this.items = items;
    }

    public TransferToBarrel(
            Gob targetBarrel,
            NAlias items
    ) {
        this.targetBarrel = targetBarrel;
        this.items = items;
    }

    AreasID id;
    NArea area = null;
    int size;
    ArrayList<Gob> full = new ArrayList<> ();
    NAlias items;

    Gob targetBarrel = null;
}
