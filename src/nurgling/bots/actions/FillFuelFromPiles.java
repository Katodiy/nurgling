package nurgling.bots.actions;

import haven.Gob;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

import java.awt.*;
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
                if(cap!=null && (cap.contains("Smelter")) ){
                    if((gob.getModelAttribute()&2)==0) {
                        new PathFinder(gui, gob).run();
                        new OpenTargetContainer(gob, cap).run(gui);
                        need -= (30 * NUtils.getFuelLvl(cap, new Color(255, 128, 0)));
                    }else {
                        need = 0;
                    }
                }
                if(need>0) {
                    need = need - gui.getInventory().getItems(items).size();
                    if (inPile == null) {
                        return new Results(Results.Types.NO_FUEL);
                    }
                    new TakeFromPile(iname,need,items,input).run(gui);
                    new PathFinder(gui, gob).run();
                    while (!gui.getInventory().getItems(items).isEmpty()) {
                        new TakeToHand(items).run(gui);
                        NUtils.activateItem(gob);
                        NUtils.waitEvent(() -> gui.hand.isEmpty(), 200);
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
            AreasID input,
            String cap
    ) {
        this.size = size;
        this.iname = iname;
        this.oname = oname;
        this.items = items;
        this.output = null;
        this.input = input;
        this.cap = cap;
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
    String cap = null;
    long marker = -1;

    ArrayList<Gob> out = new ArrayList<> ();
}
