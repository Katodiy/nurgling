package nurgling.bots.actions;

import haven.Gob;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

import java.util.ArrayList;
import java.util.Arrays;

public class GardenPotMakerAction implements Action{


    @Override
    public Results run(NGameUI gui) throws InterruptedException {
        Results res = new Results(Results.Types.SUCCESS);
        do {
            boolean isReady = false;
            while(!isReady) {
                isReady = true;
                for (Gob gob : Finder.findObjectsInArea(new NAlias("kiln"), Finder.findNearestMark(AreasID.kiln))) {
                    if ((gob.getModelAttribute() & 1) == 1) {
                        isReady = false;
                        break;
                    }
                }
                Thread.sleep(1000);
            }

            new nurgler.bots.actions.TakeAndPlace( new NAlias ( "kiln" ), "Kiln", new NAlias( "gardenpot" ),
                    Finder.findNearestMark(AreasID.readyPot),
                    Finder.findNearestMark(AreasID.kiln)).run(gui);

            new FillContainers(new NAlias( "gardenpot" ),AreasID.kiln, new ArrayList<>(), new TakeMaxFromContainers(new NAlias("gardenpot"), AreasID.ugardenpot, new ArrayList<>())).run(gui);
            new FillContainers(new NAlias( "gardenpot" ), AreasID.ugardenpot, new ArrayList<>()).run(gui);

            new FillFuelFromPiles ( 23, new NAlias ( "branch" ), new NAlias ( "kiln" ), new NAlias ( "branch" ),
                    AreasID.kiln, AreasID.branch ).run(gui);
            /// Поджигаем
            new LightGob ( new NAlias ( "kiln" ), AreasID.kiln, 1 ).run(gui);

        }while (res.type!=Results.Types.FAIL);

        /// Ждем пока килны не потухнут



        /// Заполняем килны горшками
//        new Craft ( new NAlias ( "kiln" ), "Kiln", AreasID.kiln, new char[]{ 'c','t','g' },
//                new NAlias ( "potterswheel" ), 4, new ArrayList<TakeFromContainers> ( Arrays.asList (
//                new TakeFromContainers ( new NAlias ( "clay" ), new NAlias ( "clay" ), 10, AreasID.clay, "Stockpile" ) ) ),
//                "Garden Pot", new NAlias ( "gardenpot-u" ) ).run(gui);
        /// Заполняем поленьями
        return new Results(Results.Types.SUCCESS);
    }


}
