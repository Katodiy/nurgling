package nurgling.bots.actions;

import haven.Coord;
import haven.GItem;

import nurgling.NAlias;
import nurgling.NGItem;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;
import java.util.Arrays;

public class CreateBranch implements Action {
    public static ArrayList<String> lumber_tools = new ArrayList<String> (
            Arrays.asList ( "stoneaxe", "axe-m", "woodsmansaxe"  ) );
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        int targetSize = Math.min(gui.getInventory().getFreeSpace()/5,gui.getInventory().getNumberFreeCoord(new Coord(2,1)));
        if(!Finder.findObjectsInArea(new NAlias("stockpile-wblock"),area).isEmpty()) {
            new TakeFromPile(new NAlias("block"), targetSize, new NAlias("block"), area).run(gui);
        }else{
            if(!Finder.findObjectsInArea(new NAlias("log"),area).isEmpty()){
                new Equip(new NAlias(lumber_tools)).run(gui);
                if(gui.getInventory ().getItem ( new NAlias ( "block" ) ) == null)
                    if ( new WorkWithLog (targetSize , new NAlias ( "log" ), true, area ).run ( gui ).type !=
                            Results.Types.SUCCESS ) {
                        return new Results ( Results.Types.NO_ITEMS );
                    }
            }else{
                return new Results ( Results.Types.NO_FUEL );
            }
        }
        while (gui.getInventory ().getItem ( new NAlias ( "block" ) )!=null || gui.getInventory ().getFreeSpace()>3) {
            GItem block = gui.getInventory().getItem(new NAlias("block"));
            if (block == null) {
                return new Results(Results.Types.NO_ITEMS);
            }
            int oldSize = gui.getInventory().getWItems(new NAlias("branch")).size();
            new SelectFlowerAction((NGItem) block, "Split", SelectFlowerAction.Types.Item).run(gui);
            NUtils.waitEvent(() -> gui.getInventory().getWItems(new NAlias("branch")).size() == oldSize+5, 50);
        }
        if ( gui.getInventory ().getWItems( new NAlias ( "branch" ) ).size () > 0 ) {
            return new Results ( Results.Types.SUCCESS );
        }
        else {
            return new Results ( Results.Types.NO_FREE_SPACE );
        }
    }
    
    public CreateBranch(NArea area ) {
        this.area = area;
    }
    
    NArea area;
}
