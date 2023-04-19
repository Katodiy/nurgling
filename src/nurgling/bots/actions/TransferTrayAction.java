package nurgling.bots.actions;

import haven.*;
import haven.res.ui.barterbox.Shopbox;
import nurgling.*;
import nurgling.bots.CheesedShedule;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import static nurgling.bots.CheesedShedule.read;
import static nurgling.bots.CheesedShedule.write;

public class TransferTrayAction implements Action {
    ArrayList<CheesedShedule.Task> forDelete = new ArrayList<>();

    void update(LinkedList<CheesedShedule.Task> tasks){
        write(tasks);
        tasks.removeAll(forDelete);
        forDelete.clear();
    }
    @Override
    public Results run(NGameUI gui)
            throws InterruptedException {
        LinkedList<CheesedShedule.Task> tasks = read();
        workWithBarter(gui, tasks, AreasID.cheese_main);
        update(tasks);
        workWithBarter(gui, tasks, AreasID.c_outside);
        update(tasks);
        workWithBarter(gui, tasks, AreasID.c_inside);
        update(tasks);
        workWithBarter(gui, tasks, AreasID.c_mine);
        update(tasks);
        workWithBarter(gui, tasks, AreasID.c_cellar);
        update(tasks);
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
                    NUtils.waitEvent(()->sb.price != null && sb.spr != null, 10);
                    if (sb.price != null && sb.spr != null) {
                        if (NUtils.isIt(sb.res, new NAlias("cheesetray"))) {
                            String value = NUtils.getContentName(sb.info());
                            if (value != null) {
                                for (CheesedShedule.Task task : tasks) {
                                    for(CheesedShedule.Task.Status status: task.status) {
                                        if (status.left >0 && status.name.contains(value)) {
                                            if(workWithTray(status,gui,gob,id,task,value))
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


    boolean workWithTray(CheesedShedule.Task.Status status, NGameUI gui, Gob gob, AreasID id, CheesedShedule.Task task, String value) throws InterruptedException {
        int num = Math.min(status.left,gui.getInventory().getNumberFreeCoord(new Coord(2,1))-((status.name.contains(task.target))?3:1));
        new TakeFromContainers(new NAlias("chest"), new NAlias(new ArrayList<>(Arrays.asList("branch"))),num
                , id, "Chest").run(gui);
        num = Math.min(num, gui.getInventory().getWItems(new NAlias("branch")).size());
        new PathFinder(gui, gob).run();
        new OpenTargetContainer(gob, "Barter Stand").run(gui);
        Window spwnd = gui.getWindow("Barter Stand");
        int transfered = 0;
        if (spwnd != null) {
            for (Widget sp = spwnd.lchild; sp != null; sp = sp.prev) {
                if (sp instanceof Shopbox) {
                    Shopbox sb = (Shopbox) sp;
                    NUtils.waitEvent(()->sb.price != null && sb.spr != null, 10);
                    if (sb.price != null && sb.spr != null && NUtils.isIt(sb.res, new NAlias("cheesetray")) && status.name.contains(NUtils.getContentName(sb.info()))) {
                        while (sb.spr != null && NUtils.getContentName(sb.info()) != null && status.name.contains(NUtils.getContentName(sb.info())) && transfered < num) {
                            int count = gui.getInventory().getWItems(new NAlias("cheesetray")).size();
                            sb.bbtn.click();
                            NUtils.waitEvent(() -> gui.getInventory().getWItems(new NAlias("cheesetray")).size() == count + 1, 60);
                            if (gui.getInventory().getWItems(new NAlias("cheesetray")).size() == count + 1)
                                transfered++;
                            NUtils.waitEvent(()->sb.price != null && sb.spr != null, 10);
                        }
                        break;
                    }
                }
            }
        }
        AreasID target_id = findTargetPlace(task, value);
        if (target_id != null) {
            new TransferItemsToBarter(target_id, new NAlias("cheesetray"), false).run(gui);
            status.left-=transfered;
        }
        else
        {
            int count = 0;
            for(GItem item: gui.getInventory().getWItems(new NAlias("cheesetray")))  {
                if(NUtils.checkName(NUtils.getContentName(item.info()),task.target)) {
                    new SelectFlowerAction((NGItem)item, "Slice up", SelectFlowerAction.Types.Item).run(gui);
                    NUtils.waitEvent(()->NUtils.getContent(item)==null,50);
                    count++;
                    if(gui.getInventory().getFreeSpace()<4) {
                        new TransferCheese().run(gui);
                    }
                }
            }
            new TransferCheese().run(gui);
            new TransferItemsToBarter(AreasID.cheese_main, new NAlias("cheesetray"), false).run(gui);
            status.left-=count;
            if(status.left==0){
                forDelete.add(task);
            }
        }
        new TransferItemsToContainers(id,
                new NAlias(new ArrayList<>(Arrays.asList("Branch", "branch"))), true).run(gui);
        return true;
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


