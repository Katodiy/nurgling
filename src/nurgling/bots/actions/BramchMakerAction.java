package nurgling.bots.actions;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NHitBox;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;
import java.util.Arrays;

public class BramchMakerAction implements Action {
    public static ArrayList<String> lumber_tools = new ArrayList<String> ( Arrays.asList ( "stoneaxe" ) );

    @Override
    public Results run(NGameUI gui) throws InterruptedException {
        while(Finder.findObjectsInArea ( new NAlias( "stockpile" ), tree_area )
                .isEmpty () || !Finder.findObjectsInArea ( new NAlias( "log" ), tree_area )
                .isEmpty () ) {
            new Equip( new NAlias ( lumber_tools, new ArrayList<String> () ) ).run(gui);
            new CreateBranch(tree_area).run(gui);
            new TransferToPile( output_area, NHitBox.get ( "gfx/terobjs/stockpile-branch" ),
                    new NAlias( "stockpile" ), new NAlias ( "branch" ) ).run(gui);

        }

        return new Results(Results.Types.SUCCESS);
    }

    public BramchMakerAction(NArea tree_area, NArea output_area) {
        this.tree_area = tree_area;
        this.output_area = output_area;
    }

    NArea tree_area;
    NArea output_area;
}
