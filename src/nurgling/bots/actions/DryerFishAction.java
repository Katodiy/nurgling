package nurgling.bots.actions;


import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.tools.AreasID;

import java.util.ArrayList;
import java.util.Arrays;

public class DryerFishAction implements Action {

    private NAlias dry_fish = new NAlias ( new ArrayList<> ( Arrays.asList ( "Dried Filet" ) ),
            new ArrayList<> ( Arrays.asList (  "raw" ) ) );

    private NAlias raw_fish = new NAlias ( new ArrayList<String> ( Arrays.asList ( "Filet" ) ),
            new ArrayList<String> ( Arrays.asList ( "Dried" ) ) );

    @Override
    public Results run(NGameUI gui)
            throws InterruptedException {

        /// Забираем рыбу и переносим их в ящики
        new FillContainers (dry_fish,AreasID.dry_fish, new ArrayList<>(), new TakeMaxFromContainers(dry_fish, new NAlias("dframe"),new ArrayList<>())  ).run(gui);

        /// Заполняем новой рыбой
        new FillContainers (raw_fish, new NAlias("dframe"), new ArrayList<>(), new TakeMaxFromContainers(raw_fish, AreasID.raw_fish, new ArrayList<>())).run(gui);

        /// Сбрасываем лишнее
        new FillContainers ( raw_fish , AreasID.raw_fish, new ArrayList<>()).run(gui);

        return new Results(Results.Types.SUCCESS);
    }
}
