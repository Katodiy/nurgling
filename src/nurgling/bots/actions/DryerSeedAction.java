package nurgling.bots.actions;


import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.tools.AreasID;

import java.util.ArrayList;
import java.util.Arrays;

public class DryerSeedAction implements Action {

    private final NAlias seed = new NAlias ( new ArrayList<String> (
            Arrays.asList ( "onion", "carrot", "seed-flax", "seed-barley", "seed-poppy", "beet", "cucumber",
                    "seed-wheat","seed-hemp","hopcones","peppercorn","seed-millet","seed-lettuce","seed-grape","peapod","seed" +
                            "-pumpkin","seed-pipeweed", "seed-turnip" ) ) );

    @Override
    public Results run(NGameUI gui)
            throws InterruptedException {
        /// Забираем семена и переносим их в ящики
        new FillContainers(seed, AreasID.seed_out, new ArrayList<>(), new TakeMaxFromContainers(seed, new NAlias("dframe"), new ArrayList<>())).run(gui);

        /// Заполняем новыми свежими семенами
        new FillContainers(new NAlias("windweed"), new NAlias("dframe"), new ArrayList<>(), new TakeMaxFromContainers(new NAlias("windweed"), AreasID.seed_in, new ArrayList<>())).run(gui);

        /// Сбрасываем лишнее
        new FillContainers(new NAlias("windweed"), AreasID.seed_in, new ArrayList<>()).run(gui);

        return new Results(Results.Types.SUCCESS);
    }
}
