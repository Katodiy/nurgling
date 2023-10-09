package nurgling.bots.actions;

import haven.Gob;
import nurgling.*;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

public class TakeFromPile implements Action {
    NAlias iname;
    NAlias items;
    int count;
    AreasID input;
    NArea area = null;
    
    public TakeFromPile (
            NAlias iname,
            int count,
            NAlias items,
            AreasID input

    ) {
        this.iname = iname;
        this.count = count;
        this.input = input;
        this.items = items;
    }
    
    public TakeFromPile (
            NAlias iname,
            int count,
            NAlias items,
            NArea area
    
    ) {
        this.iname = iname;
        this.count = count;
        this.area = area;
        this.items = items;
    }
    
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {

        if( Finder.findObjectsInArea ( iname, (area==null)?Finder.findNearestMark ( input ):area ).isEmpty())
            return new Results ( Results.Types.FAIL );
        Gob inPile = Finder.findObjectInArea ( iname, 1000, (area==null)?Finder.findNearestMark ( input ):area );
        int need = count;
        do {
            if ( inPile == null ) {
                return new Results ( Results.Types.NO_FUEL );
            }
            new PathFinder( gui, inPile ).run ();
            new OpenTargetContainer ( inPile, "Stockpile" ).run ( gui );

            NISBox sp = null;
            if((sp = NUtils.getStockpile())!=null && NUtils.getStockpile().getCount()>need) {
                for (int i = 0; i < count; i++)
                    sp.wdgmsg("xfer");
                NUtils.waitEvent(() -> gui.getInventory().getWItems(items).size() == count, 200);
                need = count - gui.getInventory().getWItems(items).size();
            }
            else
            {
                while (need > 0 && Finder.findObject(inPile.id) != null) {
                    if (!NUtils.takeItemFromPile())
                        return new Results(Results.Types.FULL);
                    need = count - gui.getInventory().getWItems(items).size();
                }
                if (Finder.findObject(inPile.id) == null) {
                    inPile = Finder.findObjectInArea(iname, 1000, Finder.findNearestMark(input));
                }
            }
        }
        while ( need > 0 );
        return new Results ( Results.Types.SUCCESS );
    }
}
