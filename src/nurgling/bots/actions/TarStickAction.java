package nurgling.bots.actions;

import haven.Coord;
import haven.Gob;
import nurgling.*;
import nurgling.bots.tools.OutContainer;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;
import java.util.Arrays;

public class TarStickAction implements Action {
    public static ArrayList<String> lumber_tools = new ArrayList<String> (
            Arrays.asList ( "stoneaxe", "axe-m", "woodsmansaxe"  ) );
    private NArea tree_area;
    private NArea barrel_area;
    private NArea output_area;

    public TarStickAction(NArea tree_area, NArea barrel_area, NArea output_area) {
        this.tree_area = tree_area;
        this.barrel_area = barrel_area;
        this.output_area = output_area;
    }

    @Override
    public Results run(NGameUI gui) throws InterruptedException {
        new Equip( new NAlias ( lumber_tools, new ArrayList<String> () ) ).run(gui);

        ArrayList<OutContainer> outContainers = new ArrayList<>();

        while ((!Finder.findObjectsInArea(new NAlias("log"), tree_area).isEmpty() || !Finder.findObjectsInArea(new NAlias("block"), tree_area).isEmpty()) && findTarBarrel()) {
            /// Создаем палку
            if(new CreateBranch(tree_area).run(gui).type == Results.Types.NO_FUEL)
                return new Results(Results.Types.NO_FUEL);
            /// Находим подходящую бочку
            new OpenBarrelWithContent(2.5, new NAlias(new ArrayList<>(Arrays.asList("tar", "Tar"))),
                    barrel_area).run(gui);
            /// Крафтим стики
            new Craft("Tarsticks", new char[]{'c', 'p'}, new NAlias("tarstick")).run(gui);
            if(gui.getInventory().getFreeSpace()<5 && gui.getInventory().getNumberFreeCoord(new Coord(2,1))<1)
                /// Переносим стики
                new FillContainers(new NAlias("tarstick"), output_area, outContainers).run(gui);
        }
        return new Results(Results.Types.SUCCESS);
    }

    boolean findTarBarrel(){
        for ( Gob barrel : Finder.findObjectsInArea ( new NAlias ( "barrel" ), barrel_area) ) {
            if (NUtils.isOverlay(barrel, new NAlias(new ArrayList<>(Arrays.asList("tar", "Tar"))))) {
                return true;
            }
        }
        return false;
    }
}
