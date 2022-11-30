package nurgling.bots.actions;

import haven.Coord;
import haven.Following;
import haven.Gob;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.tools.Finder;

import static haven.OCache.posres;


public class UseWorkStation implements Action {
    @Override
    public Results run(NGameUI gui)
            throws InterruptedException {
        Gob workstation;
        if (markEx == -1)
            workstation = Finder.findObject(names);
        else
            workstation = Finder.findObjectMask(names, markEx, 1000);
        if (workstation != null) {
            PathFinder pf = new PathFinder(gui, workstation);
            pf.setHardMode ( hard );
            pf.run();
            if (withAction) {
                new SelectFlowerAction(workstation, action, SelectFlowerAction.Types.Gob).run(gui);
                NUtils.waitEvent(()->gui.getInventory(cap) != null, 100);
            } else {
                gui.map.wdgmsg("click", Coord.z, workstation.rc.floor(posres), 3, 0, 0, (int) workstation.id,
                        workstation.rc.floor(posres), 0, -1);
            }
            if (!NUtils.checkName("/crucible", names) && !NUtils.checkName("anvil", names) && !NUtils.checkName(
                    "/pow", names) ) {
                Following state;
                int counter = 0;
                do {
                    state = gui.map.player().getattr(Following.class);
                    Thread.sleep(50);
                    counter++;
                }
                while (state == null && counter < 20);
                if (state != null) {
                    if (state.xfres != null) {
                        if (state.xfres.get().name.contains(names.keys.get(0))) {
                            return new Results(Results.Types.SUCCESS);
                        }
                    }
                }
            } else {
                Thread.sleep(1000);
                return new Results(Results.Types.SUCCESS);
            }
        } else {
            return new Results(Results.Types.NO_WORKSTATION);
        }
        return new Results(Results.Types.FAIL);
    }

    public UseWorkStation(NAlias names) {
        this.names = names;
    }

    public UseWorkStation(
            NAlias names,
            String cap,
            String action
    ) {
        this.names = names;
        this.cap = cap;
        this.action = action;
        this.withAction = true;
    }
    
    public UseWorkStation(
            NAlias names,
            String cap,
            String action,
            boolean hard
    ) {
        this.names = names;
        this.cap = cap;
        this.action = action;
        this.withAction = true;
        this.hard = hard;
    }

    public UseWorkStation(
            NAlias names,
            String cap,
            String action,
            long markEx
    ) {
        this.names = names;
        this.cap = cap;
        this.action = action;
        this.withAction = true;
        this.markEx = markEx;
    }

    NAlias names;
    String cap;
    String action;
    boolean withAction = false;
    long markEx = -1;
    boolean hard = false;
}
