package nurgling.bots.actions;

import haven.Coord;
import haven.Gob;
import nurgling.*;
import nurgling.bots.tools.InContainer;
import nurgling.bots.tools.OutContainer;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

import java.util.ArrayList;
import java.util.Arrays;

import static haven.OCache.posres;

public class FFAction implements Action
{
    @Override
    public Results run(NGameUI gui) throws InterruptedException {
        new Equip ( new NAlias( new ArrayList<> (Arrays.asList ( "smithshammer" )), new ArrayList<String> () ) ).run(gui);

        while (true) {
            while(true)
            {
                boolean needBreak = false;
                for ( Gob gob : Finder.findObjectsInArea ( new NAlias("fineryforge"), Finder.findNearestMark(AreasID.fineryforge) ) ) {
                    if ((gob.getModelAttribute() & 8) == 0) {
                        needBreak = true;
                    }
                }
                if(needBreak)
                    break;
                Thread.sleep(1000);
            }



            ArrayList<InContainer> ffs = new ArrayList<>();
            new FillContainers(new NAlias("dros"), AreasID.dros, new ArrayList<>(), new TakeMaxFromContainers(new NAlias("dros"), AreasID.fineryforge, ffs)).run(gui);

            for (InContainer ff : ffs) {
                new PathFinder(gui, ff.gob).run();
                new OpenTargetContainer(ff.gob, "Finery Forge").run(gui);
                new TakeMaxFromContainer("Finery Forge", new NAlias("Bloom", "bloom")).run(gui);
                if (NUtils.getGameUI().getInventory().getWItems(new NAlias("Bloom", "bloom")).size() > 0) {
                    new UseWorkStation(new NAlias("anvil")).run(gui);
                    new Craft("Wrought Iron", new char[]{'c', 'p', 'm', 'w'}).run(gui);
                    new TransferBars().run(gui);
                }
                new FillContainers(new NAlias("Dross", "dross"), AreasID.dros, new ArrayList<>(), new TakeMaxFromContainers( new NAlias("Dross", "dross"), AreasID.fineryforge, new ArrayList<>())).run(gui);
            }

            new FillContainers(new NAlias("bar-castiron", "Bar of Cast Iron"), AreasID.fineryforge, new ArrayList<>(), new TakeMaxFromContainers(new NAlias("bar-castiron", "Bar of Cast Iron"), AreasID.cast_iron, new ArrayList<>())).run(gui);
            new FillContainers(new NAlias("bar-castiron", "Bar of Cast Iron"), AreasID.cast_iron, new ArrayList<>()).run(gui);

            new FillFuelFromPiles(2, new NAlias("coal"), new NAlias("fineryforge"), new NAlias("coal"),
                    AreasID.fineryforge, AreasID.coal, "Finery Forge").run(gui);
            new LightGob(new NAlias("fineryforge"), AreasID.fineryforge, 8).run(gui);
        }
    }
}
