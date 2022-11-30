package nurgling.bots.actions;

import haven.Gob;

import nurgling.*;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;

public class TransferItemsToContainers implements Action {
    @Override
    public Results run(NGameUI gui)
            throws InterruptedException {
        Gob barter = null;
        if (oarea == null) {
            if (output != null) {
                barter = Finder.findObjectInArea(new NAlias("barter"), 1000, Finder.findNearestMark(output));
            }
        } else {
            barter = Finder.findObjectInArea(new NAlias("barter"), 1000, oarea);
        }
        if (barter != null && !ignoreBater) {
            new TransferItemsToBarter(barter, items, output, isInfo).run(gui);
        } else {
            if (target == null) {
                NUtils.ContainerProp prop;
                if (oarea == null) {
                    prop = NUtils.getContainerType(output);
                } else {
                    prop = NUtils.getContainerType(oarea);
                }
                target = prop.name;
                cap = prop.cap;
                fullMark = prop.fullMark;
            }

            ArrayList<Gob> out;
            if (oarea == null) {
                if (output != null) {
                    out = Finder.findObjectsInArea(target, Finder.findNearestMark(this.output));
                } else {
                    out = Finder.findObjects(target);
                }
            } else {
                out = Finder.findObjectsInArea(target, this.oarea);
            }
            if (!out.isEmpty()) {
                if (cap.equals("")) {
                    cap = NUtils.getContainerType(out.get(0)).cap;
                }
                if (!gui.getInventory().getItems(items).isEmpty() ||
                        !gui.getInventory().getItemsWithInfo(items).isEmpty()) {
                    for (Gob data : out) {
                        if (NUtils.isIt(data, new NAlias("htable"))) {
                            if (NUtils.checkHerbCount(data) == 2) {
                                continue;
                            }
                        }
                        boolean empty = true;
                        if (!NUtils.isIt(data, new NAlias("dframe"))) {
                            empty = (data.getModelAttribute() & fullMark) == 0;
                        }
                        if (empty) {
                            PathFinder pf = new PathFinder(gui, data);
                            pf.run();
                            if(!cap.contains("Stockpile")) {
                                if (new OpenTargetContainer(data, cap).run(gui).type != Results.Types.SUCCESS) {
                                    return new Results(Results.Types.OPEN_FAIL);
                                }

                                new TransferToContainerIfPossible(items, cap, isInfo).run(gui);
                                if (gui.getInventory().getItems(items).isEmpty() &&
                                        gui.getInventory().getItemsWithInfo(items).isEmpty()) {
                                    break;
                                }
                            }else{
                                if ( !gui.getInventory ().getItems ( items ).isEmpty () ) {
                                        new TransferToPile ( output, NHitBox.getByName ( items.keys.get ( 0 ) ), new NAlias("stockpile"), items )
                                                .run ( gui );
                                }
                            }
                        }
                    }
                }
            }
        }
        return new Results(Results.Types.SUCCESS);
    }

    public TransferItemsToContainers(
            int fullMark,
            AreasID output,
            NAlias target,
            String cap,
            NAlias items
    ) {
        this.fullMark = fullMark;
        this.output = output;
        this.target = target;
        this.cap = cap;
        this.items = items;
    }

    public TransferItemsToContainers(
            int fullMark,
            NArea oarea,
            NAlias target,
            String cap,
            NAlias items
    ) {
        this.fullMark = fullMark;
        this.oarea = oarea;
        this.target = target;
        this.cap = cap;
        this.items = items;
    }



    public TransferItemsToContainers(
            NArea oarea,
            NAlias items
    ) {
        this.fullMark = -1;
        this.oarea = oarea;
        this.target = null;
        this.cap = "";
        this.items = items;
    }

    public TransferItemsToContainers(
            NAlias target,
            NAlias items
    ) {
        this.target = target;
        this.cap = "";
        this.items = items;
        this.output = null;
    }

    public TransferItemsToContainers(
            int fullMark,
            AreasID output,
            NAlias target,
            String cap,
            NAlias items,
            boolean isInfo
    ) {
        this.fullMark = fullMark;
        this.output = output;
        this.target = target;
        this.cap = cap;
        this.items = items;
        this.isInfo = isInfo;
    }

    public TransferItemsToContainers(
            int fullMark,
            NArea out,
            NAlias target,
            String cap,
            NAlias items,
            boolean isInfo
    ) {
        this.fullMark = fullMark;
        this.oarea = out;
        this.target = target;
        this.cap = cap;
        this.items = items;
        this.isInfo = isInfo;
    }

    public TransferItemsToContainers(
            AreasID output,
            NAlias items,
            boolean ignoreBater
    ) {
        this.output = output;
        this.items = items;
        this.ignoreBater = ignoreBater;
    }

    public TransferItemsToContainers(
            NArea output,
            NAlias items,
            boolean ignoreBater
    ) {
        this.oarea = output;
        this.items = items;
        this.ignoreBater = ignoreBater;
    }

    int fullMark;
    AreasID output;
    NArea oarea = null;
    NAlias target = null;
    String cap = "";
    NAlias items;
    boolean isInfo = false;
    boolean ignoreBater = false;

    NArea outa = null;
}
