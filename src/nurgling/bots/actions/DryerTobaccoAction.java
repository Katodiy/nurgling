package nurgling.bots.actions;


import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.tools.AreasID;

import java.util.ArrayList;

public class DryerTobaccoAction implements Action {

    @Override
    public Results run(NGameUI gui)
            throws InterruptedException {
        /// Забираем высушенный табак и переносим его в ящики
        new FillContainers(new NAlias("tobacco-cured"), AreasID.tobacco_out, new ArrayList<>(), new TakeMaxFromContainers(new NAlias("tobacco-cured"), new NAlias("htable"), new ArrayList<>())).run(gui);

        /// Заполняем новыми свежими листьями
        new FillContainers(new NAlias("tobacco-fresh"), new NAlias("htable"), new ArrayList<>(), new TakeMaxFromContainers(new NAlias("tobacco-fresh"), AreasID.tobacco_in, new ArrayList<>())).run(gui);

        /// Сбрасываем лишнее в изначальные стокпайлы
        new FillContainers(new NAlias("tobacco-fresh"), AreasID.tobacco_in, new ArrayList<>()).run(gui);

        return new Results(Results.Types.SUCCESS);
    }
}
