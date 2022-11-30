package nurgling.bots.actions;

import haven.Gob;
import haven.ResDrawable;
import haven.res.lib.tree.TreeScale;
import nurgling.*;
import nurgling.tools.Finder;
import nurgling.tools.NArea;


import java.util.ArrayList;
import java.util.Arrays;

public class TreeItemsCollection implements Action {
    NAlias trees = new NAlias ( Arrays.asList ( "tree", "bushes" ),
            Arrays.asList ( "log", "block", "oldtrunk", "stump" ) );
    
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
//        int th = NUtils.checkName("leaf", item_name) ? 3 : 2;
        int th = 0;
        ArrayList<Gob> gobs = Finder.findObjectsInArea(trees, tree);
        if (gui.getInventory().getNumberFreeCoord(gui.getInventory().getItem(item_name)) <=
                1) {
            NUtils.stopWithClick();
            new TransferToPile(piles, NHitBox.get(pile_name),
                    new NAlias(pile_name), item_name).run(gui);
        }
        for (Gob in : gobs) {
            if (in.getattr(TreeScale.class) == null) {
                while (NUtils.checkGobFlower(item_name, in)) {
                    PathFinder pf = new PathFinder(gui, in);
                    pf.setHardMode(true);
                    pf.run();
                    NFlowerMenu.instance.selectInCurrent(item_name);
                    do {
                        NUtils.waitEvent(() -> NUtils.getProg() >= 0, 50, 5);
                        NUtils.waitEvent(() -> NUtils.getProg() < 0, 1000, 10);
                        if (gui.getInventory()
                                .getNumberFreeCoord(gui.getInventory().getItem(item_name)) <= th) {
                            new TransferToPile(piles, NHitBox.get(pile_name),
                                    new NAlias(pile_name), item_name).run(gui);
                        }
                        Thread.sleep(10);
                    } while (NUtils.getProg() >= 0);
                }
            }
        }
        NUtils.stopWithClick();
        new TransferToPile(piles, NHitBox.get(pile_name),
                new NAlias(pile_name), item_name).run(gui);
        return new Results(Results.Types.SUCCESS);
    }
    
    
    public TreeItemsCollection(
            NArea tree,
            NArea piles,
            NAlias item_name,
            String pile_name,
            String action
    ) {
        this.tree = tree;
        this.piles = piles;
        this.item_name = item_name;
        this.pile_name = pile_name;
        this.action = action;
    }
    
    NArea tree;
    NArea piles;
    NAlias item_name;
    String pile_name;
    String action;
}
