package nurgling.bots.actions;

import haven.*;
import haven.res.ui.barterbox.Shopbox;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.bots.CheesedShedule;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;

import static nurgling.bots.CheesedShedule.read;
import static nurgling.bots.CheesedShedule.write;

public class TransferTrayAction implements Action {
    @Override
    public Results run(NGameUI gui)
            throws InterruptedException {
        LinkedList<CheesedShedule.Task> tasks = read();
        workWithBarter(gui, tasks, AreasID.cheese_main);
        workWithBarter(gui, tasks, AreasID.c_outside);
        workWithBarter(gui, tasks, AreasID.c_inside);
        workWithBarter(gui, tasks, AreasID.c_mine);
        workWithBarter(gui, tasks, AreasID.c_cellar);
        write(tasks);
        return new Results(Results.Types.FAIL);
    }

    void workWithBarter(NGameUI gui, LinkedList<CheesedShedule.Task> tasks, AreasID id) throws InterruptedException {
        boolean res = false;
        do {
            res = taskInBarter(gui, tasks, id);
        } while (res);
        new TransferItemsToContainers(id,
                new NAlias(new ArrayList<>(Arrays.asList("Branch", "branch"))), true).run(gui);
    }

    boolean taskInBarter(NGameUI gui, LinkedList<CheesedShedule.Task> tasks, AreasID id) throws InterruptedException {
        new TakeFromContainers(new NAlias("chest"), new NAlias(new ArrayList<>(Arrays.asList("branch"))),
                1, id, "Chest").run(gui);
        Gob gob = Finder.findObjectInArea(new NAlias("barter"), 2000,
                Finder.findNearestMark(id));

        PathFinder pf = new PathFinder(gui, gob);
        pf.run();
        new OpenTargetContainer(gob, "Barter Stand").run(gui);

        Window spwnd = gui.getWindow("Barter Stand");
        if (spwnd != null) {
            for (Widget sp = spwnd.lchild; sp != null; sp = sp.prev) {
                if (sp instanceof Shopbox) {
                    Shopbox sb = (Shopbox) sp;
                    if (sb.price != null && sb.spr != null) {
                        if (NUtils.isIt(sb.res, new NAlias("cheesetray"))) {
                            String value = NUtils.getContentName(sb.info());
                            if (value != null) {
                                for (CheesedShedule.Task task : tasks) {
                                    for(CheesedShedule.Task.Status status: task.status) {
                                        if (status.left >0 && status.name.contains(value)) {
                                            int count = gui.getInventory().getItems(new NAlias("cheesetray")).size();
                                            sb.bbtn.click();
                                            NUtils.waitEvent(() -> gui.getInventory().getItems(new NAlias("cheesetray")).size() == count + 1, 60);
                                            AreasID target_id = findTargetPlace(task, value);
                                            if (target_id != null) {
                                                new TransferItemsToBarter(target_id, new NAlias("cheesetray"), false).run(gui);
                                                status.left--;
                                            }
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private AreasID findTargetPlace(CheesedShedule.Task task, String value) {
        if(!value.contains(task.target)) {
            for (CheesedShedule.CheeseBranch branch : CheesedShedule.branches) {

                for (CheesedShedule.CheeseBranch.Cheese cheese : branch.cheeses) {
                    if(cheese.name.contains(task.target)) {
                        int i = 0;
                        for (CheesedShedule.CheeseBranch.Cheese stage : branch.cheeses) {
                            if (stage.name.contains(value)) {
                                switch (branch.cheeses.get(i + 1).place) {
                                    case cellar:
                                        return AreasID.c_cellar;
                                    case inside:
                                        return AreasID.c_inside;
                                    case outside:
                                        return AreasID.c_outside;
                                    case mine:
                                        return AreasID.c_mine;
                                    default:
                                        throw new IllegalStateException("Unexpected value: " + branch.cheeses.get(i + 1).place);
                                }
                            }
                            i++;
                        }
                    }
                }
            }
        }
        return null;
    }
}


