package nurgling.bots.actions;

import haven.Coord;
import haven.Gob;
import haven.Pair;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NHitBox;
import nurgling.NUtils;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class TanningAction implements Action {
    private NAlias leather = new NAlias ( new ArrayList<> ( Arrays.asList ( "leather" ) ));


    private NAlias hides = new NAlias ( new ArrayList<> ( Arrays.asList ( "hide", "scale" ) ),
            new ArrayList<> ( Arrays.asList ( "blood", "raw", "hpatch" ) ) );

    @Override
    public Results run(NGameUI gui)
            throws InterruptedException {


        new FillFluid( AreasID.tanning_flued ,2,new NAlias ( "ttub" ),
                new NAlias ( "tanfluid" )).run(gui);
        /// Забираем шкуры и переносим их в пайлы
        new TransferToPileFromContainer( new NAlias ( "ttub" ), new NAlias ( "stockpile-anyleather" ),
                leather  , AreasID.leather, "Tub" ).run(gui);



        /// Заполняем новыми шкурами
        new FillContainers( hides,new NAlias ( "ttub" ), new ArrayList<>(), new TakeMaxFromContainers(hides,AreasID.lqhides, new ArrayList<>())).run(gui);

        /// Сбрасываем лишнее
        new TransferToPile( AreasID.lqhides, NHitBox.getByName ( "stockpile-hide" ), new NAlias( "stockpile" +
                "-hide" ),
                hides ).run(gui);

        return new Results(Results.Types.SUCCESS);
    }
}
