package nurgling.bots.actions;

import haven.GItem;
import haven.Gob;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.bots.tools.Ingredient;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;


import java.util.ArrayList;
import java.util.Arrays;


public class Butching implements Action {

    private NAlias hides = new NAlias(new ArrayList<String>(Arrays.asList("blood", "raw")),
            new ArrayList<String>(Arrays.asList("stern")));

    static ArrayList<String> tools = new ArrayList<String>(Arrays.asList("stoneaxe", "axe-m"));


    @Override
    public Results run(NGameUI gui)
            throws InterruptedException {
        if (new Equip(new NAlias("butcherscleaver")).run(gui).type != Results.Types.SUCCESS)
            new Equip(new NAlias(tools, new ArrayList<String>())).run(gui);
        ArrayList<Gob> targets = Finder.findObjectsInArea(new NAlias("kritter"),
                Finder.findNearestMark(AreasID.kritter));
        while(targets.size()>0) {
            targets.sort(NUtils.d_comp);
            Gob gob = targets.get(0);
            /// Снимаем шкуры
            new CollectFromGob("Skin", gob, new NAlias("butch")).run(gui);
            NUtils.waitEvent(() -> !gui.getInventory().getWItems(hides).isEmpty(), 5);
            new CollectFromGob("Scale", gob, new NAlias("butch")).run(gui);
            NUtils.waitEvent(() -> !gui.getInventory().getWItems(hides).isEmpty(), 5);
            new CollectFromGob("Break", gob).run(gui);
            NUtils.waitEvent(() -> !gui.getInventory().getWItems(hides).isEmpty(), 5);
            new TransferRawHides().run(gui);

            /// Чистим
            new CollectFromGob("Clean", gob, new NAlias("butch")).run(gui);
            new TransferTrash().run(gui);

            new TransferIngredient("Suckling's Maw").run(gui);

            // new TransferButCury().run(gui);
            if (!NUtils.isIt(gob, new NAlias(new ArrayList<>(Arrays.asList("orca", "spermwhale"))))) {
                /// Собираем мясо
                new CollectFromGob("Butcher", gob, new NAlias("butch")).run(gui);
            } else {
                new CollectFromGob("Cut", gob, new NAlias("bushpi")).run(gui);
            }
            new TransferMeat().run(gui);

            ArrayList<GItem> fat_and_other = gui.getInventory().getWItems(new NAlias("animalfat", "fishyeyeball"));
            if (fat_and_other.size() > 0) {
                if (Finder.findObjectInArea(new NAlias("barter"), 1000, Finder.findNearestMark(AreasID.fat)) !=
                        null) {
                    new TransferItemsToBarter(AreasID.fat, new NAlias("animalfat"), false).run(gui);
                } else {
                    new FillContainers(new NAlias("animalfat", "fishyeyeball"), AreasID.fat, new ArrayList<>()).run(gui);
                }
            }

            for (GItem item : gui.getInventory().getWItems(new NAlias("Bollock"))) {
                new TransferItemsToBarter(Ingredient.get(item).barter_out, new NAlias(NUtils.getInfo(item)), true).run(gui);
            }

            new TransferIngredient("Beast Unborn").run(gui);

            new CollectFromGob("Collect bones", gob).run(gui);

            new TransferBones().run(gui);


            targets = Finder.findObjectsInArea(new NAlias("kritter"),
                    Finder.findNearestMark(AreasID.kritter));
        }



        return new Results(Results.Types.SUCCESS);
    }

    public Butching(
    ) {
    }

}
