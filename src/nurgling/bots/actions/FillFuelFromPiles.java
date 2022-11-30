package nurgling.bots.actions;

import haven.Gob;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

import java.util.ArrayList;
import java.util.HashMap;

public class FillFuelFromPiles implements Action {
    
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {

        if(out.isEmpty()) {
            if (output != null) {
                out = Finder.findObjectsInArea(oname, Finder.findNearestMark(output));
            } else {
                out = Finder.findObjects(oname);
            }
        }
        Gob inPile = Finder.findObjectInArea ( iname, 1000, Finder.findNearestMark ( input ) );
        for ( Gob gob : out ) {
            if(marker==-1 || (gob.getModelAttribute()&marker) ==0) {
                int need = size;
                do {
                    if (inPile == null) {
                        return new Results(Results.Types.NO_FUEL);
                    }
                    new PathFinder(gui, inPile).run();
                    new OpenTargetContainer(inPile, "Stockpile").run(gui);

                    while (need > 0 && Finder.findObject(inPile.id) != null) {
                        NUtils.takeItemFromPile();
                        need = size - gui.getInventory().getItems(items).size();
                    }
                    if (Finder.findObject(inPile.id) == null) {
                        inPile = Finder.findObjectInArea(iname, 1000, Finder.findNearestMark(input));
                    }
                }
                while (need > 0);
                NUtils.getGameUI().msg("Fuel taked");
                new PathFinder(gui, gob).run();
                for (int i = 0; i < size; i++) {
                    new TakeToHand(items).run(gui);
                    int counter = 0;
                    while (!gui.hand.isEmpty() && counter < 20) {
                        NUtils.activateItem(gob);
                        Thread.sleep(50);
                        counter++;
                    }
                }
            }
        }
        return new Results ( Results.Types.SUCCESS );
    }
    public FillFuelFromPiles(
            int size,
            NAlias iname,
            NAlias oname,
            NAlias items,
            AreasID output,
            AreasID input
    ) {
        this.size = size;
        this.iname = iname;
        this.oname = oname;
        this.items = items;
        this.output = output;
        this.input = input;
    }

    public FillFuelFromPiles(
            int size,
            NAlias iname,
            NAlias oname,
            NAlias items,
            AreasID input
    ) {
        this.size = size;
        this.iname = iname;
        this.oname = oname;
        this.items = items;
        this.output = null;
        this.input = input;
    }

    public FillFuelFromPiles(
            int size,
            NAlias iname,
            ArrayList<Gob> out,
            NAlias items,
            AreasID input,
            long marker
    ) {
        this.size = size;
        this.iname = iname;
        this.out = out;
        this.items = items;
        this.output = null;
        this.input = input;
        this.marker = marker;
    }
    
    int size;
    NAlias iname;
    NAlias oname;
    NAlias items;
    AreasID output;
    AreasID input;
    String cap;
    long marker = -1;

    ArrayList<Gob> out = new ArrayList<> ();
}
