package nurgling.bots.actions;

import haven.Gob;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NHitBox;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;
import java.util.Arrays;

public class BrickAction implements Action {
    NAlias kiln = new NAlias(new ArrayList<>(Arrays.asList("kiln")),new ArrayList<>(Arrays.asList("tar")));
    @Override
    public Results run(NGameUI gui) throws InterruptedException {

        while (!Finder.findObjectsInArea(new NAlias("clay"), clay_area).isEmpty()) {
            /// Ждем пока килны не потухнут
            new WaitAction(() -> {
                for (Gob gob : Finder.findObjects(new NAlias("kiln"))) {
                    if ((gob.getModelAttribute() & 1) != 0) {
                        return true;
                    }
                }
                return false;
            }, 1000).run(gui);
            /// Забираем кирпичи и переносим их в пайлы
            new TransferToPileFromContainer(kiln, new NAlias("stockpile-brick"),
                    new NAlias("brick"), brick_area, "Kiln").run(gui);
            /// Заполняем килны с пайлов
            new FillContainers(new NAlias("clay"), kiln, new ArrayList<>(), new TakeMaxFromContainers(new NAlias("clay"), clay_area, new ArrayList<>())).run(gui);

            new TransferToPile(clay_area, NHitBox.get(), new NAlias("clay"), new NAlias("clay")).run(gui);
            /// Заполняем килны веточками с пайлов
            new FillFuelFromPiles(2, new NAlias("branch"), kiln,
                    new NAlias("branch"), AreasID.branch).run(gui);
            /// Поджигаем
            new LightGob(kiln, 1).run(gui);


        }

        return new Results(Results.Types.SUCCESS);
    }

    public BrickAction(NArea clay_area, NArea brick_area) {
        this.clay_area = clay_area;
        this.brick_area = brick_area;
    }

    NArea clay_area;
    NArea brick_area;
}
