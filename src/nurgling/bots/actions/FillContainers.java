package nurgling.bots.actions;

import haven.*;

import nurgling.*;
import nurgling.bots.tools.InContainer;
import nurgling.bots.tools.Ingredient;
import nurgling.bots.tools.OutContainer;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;

public class FillContainers implements Action
{
    @Override
    public Results run(NGameUI gui)
            throws InterruptedException {
        if(outName == null){
            outName = NUtils.getContainerType((outArea!=null)?outArea:Finder.findNearestMark(outId)).name;
        }

        if(outContainers.isEmpty()) {
            ArrayList<OutContainer> output = new ArrayList<>();
            if(needed!=-1)
            {
                if (outArea == null && outId == null) {
                    output = OutContainer.create(Finder.findObjects(outName), needed);
                } else {
                    output = OutContainer.create(Finder.findObjects(outName, (outArea != null) ? outArea : Finder.findNearestMark(outId)), needed);
                }
            }
            else
            {
                if (outArea == null && outId == null) {
                    output = OutContainer.create(Finder.findObjects(outName));
                } else {
                    output = OutContainer.create(Finder.findObjects(outName, (outArea != null) ? outArea : Finder.findNearestMark(outId)));
                }
            }
            outContainers.addAll(output);
        }

        OutContainer.sort(outContainers);

        if(allPiles()) {
            if (outArea != null ) {
                new TransferToPile(outArea,NHitBox._default,new NAlias("stockpile"), items,q).run(gui);
            }else if (outId != null) {
                new TransferToPile(outId,NHitBox._default,new NAlias("stockpile"), items,q).run(gui);
            }
        }
        else {
            do {
                int current = gui.getInventory().getWItems(items,q).size();
                for (OutContainer outContainer : outContainers) {
                    if (!outContainer.isFull) {
                        if (items != null && Ingredient.get(items.keys.get(0)) != null) {
                            for (GItem item : gui.getInventory().getWItems(items,q)) {
                                double th = Ingredient.get(items.keys.get(0)).th;
                                if (((NGItem) item).quality() < th)
                                    NUtils.drop(item);
                            }
                        }
                        if (gui.getInventory().getWItems(items,q).isEmpty()) {
                            break;
                        }
                        new PathFinder(gui, outContainer.gob, true).run();
                        if (new OpenTargetContainer(outContainer.gob, NUtils.getContainerType(outContainer.gob).cap).run(gui).type != Results.Types.SUCCESS) {
                            return new Results(Results.Types.OPEN_FAIL);
                        }
                        if (new TransferToContainer(items, NUtils.getContainerType(outContainer.gob).cap, needed, q).run(gui).type == Results.Types.FULL) {
                            outContainer.isFull = true;
                        }
                    }
                }
                if (current != 0 && current == gui.getInventory().getWItems(items).size()) {
                    return new Results(Results.Types.NO_ITEMS);
                }
                if (takeMaxFromContainers != null && !OutContainer.allFull(outContainers)) {
                    takeMaxFromContainers.run(gui);
                    if (gui.getInventory().getWItems(items).size() == 0) {
                        return new Results(Results.Types.NO_ITEMS);
                    }
                }
            }
            while (!OutContainer.allFull(outContainers) && takeMaxFromContainers != null && !InContainer.allFree(takeMaxFromContainers.inContainers));

        }
        return new Results(Results.Types.SUCCESS);
    }

    boolean allPiles(){
        for(OutContainer cont : outContainers){
            if(!NUtils.isIt(cont.gob,"stockpile")){
                return false;
            }
        }
        return true;
    }

    int getNumFree(ArrayList<Pair<Gob, Boolean>> out){
        int count = 0;
        for(Pair<Gob,Boolean> unit: out){
            if(!unit.b)
                count++;
        }
        return count;
    }

    public FillContainers(NAlias items, Object o, ArrayList<OutContainer> outContainers){
        this.items = items;
        if(o instanceof NArea){
            outArea = (NArea) o;
        }
        else if (o instanceof NAlias)
        {
            outName = (NAlias) o;
        }
        else
        {
            outId = (AreasID) o;
        }
        this.outContainers = outContainers;
    }

    public FillContainers(NAlias items, Object o, ArrayList<OutContainer> outContainers, double q){
        this(items,o,outContainers);
        this.q = q;

    }

    public FillContainers(NAlias items, Object o, ArrayList<OutContainer> outContainers, TakeMaxFromContainers takeMaxFromContainers) {
        this.items = items;
        if (o instanceof NArea) {
            outArea = (NArea) o;
        } else if (o instanceof NAlias) {
            outName = (NAlias) o;
        } else {
            outId = (AreasID) o;
        }
        this.outContainers = outContainers;
        this.takeMaxFromContainers = takeMaxFromContainers;
    }

    ArrayList<OutContainer> outContainers = null;
    NAlias items;
    NAlias outName = null;
    AreasID outId = null;
    NArea outArea = null;

    int needed = -1;

    double q = -1;
    TakeMaxFromContainers takeMaxFromContainers = null;
}
