package nurgling.bots.actions;

import haven.Gob;
import haven.res.lib.tree.TreeScale;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChopperAction implements Action {
    static NAlias trees = new NAlias(Arrays.asList("tree", "bushes", "stump"),
            Arrays.asList("log", "block", "oldtrunk"));
    static NAlias tree_name = new NAlias(Arrays.asList("tree", "bushes"),
            Arrays.asList("log", "stump", "block", "oldtrunk"));
    static NAlias stump = new NAlias(Arrays.asList("stump"), Arrays.asList("block", "log", "oldtrunk"));
    static ArrayList<String> lumber_tools = new ArrayList<String>(
            Arrays.asList("woodsmansaxe", "axe-m", "stoneaxe"));
    static ArrayList<String> shovel_tools = new ArrayList<String>(Arrays.asList("shovel-m", "shovel-w","shovel-t"));

    @Override
    public Results run(NGameUI gui)
            throws InterruptedException {
        boolean withDrop = false;
        ArrayList<Gob> trees = Finder.findObjectsInArea(tree_name, tree_area);
        if (stump_mod.get()) {
            trees.addAll(Finder.findObjectsInArea(stump, tree_area));
        }
        if (Finder.findDressedItem(new NAlias("bucket-water")) != null) {
//            new Drop(Drop.Type.Back, new NAlias("bucket")).run(gui);
            withDrop = true;
        }
        /// Цикл рубки
        for (Gob tree : trees) {
            if(!(no_kid_mod.get() && tree.getattr(TreeScale.class)!=null)) {
                Gob stump_tree = null;
                PathFinder pf = new PathFinder(gui, tree);
                pf.setHardMode(true);
                pf.run();
                new Drop(Drop.Type.Back, new NAlias("bucket")).run(gui);
                if (NUtils.isIt(tree, tree_name)) {
                    while (Finder.findObject(tree.id) != null) {
                        /// Одеть топор
                        if (new Equip(new NAlias(lumber_tools, new ArrayList<String>())).run(gui).type != Results.Types.SUCCESS)
                            return new Results(Results.Types.NO_TOOLS);

                        /// Выбрать рубку
                        new SelectFlowerAction(tree, "Chop", SelectFlowerAction.Types.Gob).run(gui);
                        /// Ждать пока дерево не срублено или стамина больше 0.3
                        NUtils.waitEvent(() -> NUtils.getProg() >= 0, 120);
                        while (NUtils.getProg() >= 0 && NUtils.getStamina() > 0.3) {
                            Thread.sleep(50);
                        }
                        /// Пить
                        if (new Drink(0.9, withDrop).run(gui).type == Results.Types.Drink_FAIL)
                            return new Results(Results.Types.Drink_FAIL);
                    }
                    if (stump_mod.get()) {
                        stump_tree = Finder.findObject(stump);
                        if (trees.contains(stump_tree) ||  NUtils.isIt(tree,"bushes") ) {
                            new TakeAndEquip(new NAlias("bucket-water"), true).run(gui);
                            continue;
                        }
                    }
                } else {
                    stump_tree = tree;
                }
                if (stump_tree != null && stump_mod.get()) {
                    while (Finder.findObject(stump_tree.id) != null) {
                        if (new Equip(new NAlias(shovel_tools, new ArrayList<String>())).run(gui).type != Results.Types.SUCCESS)
                            return new Results(Results.Types.NO_TOOLS);
                        /// Корчевать пень
                        new Destroy(stump_tree).run(gui);
                        /// Ждать пока дерево не срублено или стамина больше 0.3
                        NUtils.waitEvent(() -> NUtils.getProg() >= 0, 120);
                        while (NUtils.getProg() >= 0 && NUtils.getStamina() > 0.3) {
                            Thread.sleep(50);
                        }
                        /// Пить
                        if (new Drink(0.9, withDrop).run(gui).type == Results.Types.Drink_FAIL)
                            return new Results(Results.Types.Drink_FAIL);
                    }
                }
                new TakeAndEquip(new NAlias("bucket-water"), true).run(gui);
            }
        }


        return new Results(Results.Types.SUCCESS);
    }

    public ChopperAction(NArea tree_area, AtomicBoolean stump_mod, AtomicBoolean no_kid_mod) {
        this.tree_area = tree_area;
        this.stump_mod = stump_mod;
        this.no_kid_mod = no_kid_mod;
    }

    NArea tree_area;
    AtomicBoolean stump_mod;
    AtomicBoolean no_kid_mod;
}
