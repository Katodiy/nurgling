package nurgling.bots.actions;

import haven.WItem;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Arrays;

public class CreateBranch implements Action {
    public static ArrayList<String> lumber_tools = new ArrayList<String> ( Arrays.asList ( "stoneaxe" ) );
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        if(!Finder.findObjectsInArea(new NAlias("stockpile-block"),area).isEmpty()) {
            new TakeFromPile(new NAlias("block"), 1, new NAlias("block"), area).run(gui);
        }else{
            if(!Finder.findObjectsInArea(new NAlias("log"),area).isEmpty()){
                new Equip(new NAlias(lumber_tools)).run(gui);
                if ( new WorkWithLog ( 1, new NAlias ( "log" ), true, area ).run ( gui ).type !=
                        Results.Types.SUCCESS ) {
                    return new Results ( Results.Types.NO_ITEMS );
                }
            }
        }
        WItem block = gui.getInventory ().getItem ( new NAlias ( "block" ) );
        if ( block == null ) {
            return new Results ( Results.Types.NO_ITEMS );
        }
        new SelectFlowerAction ( block, "Split", SelectFlowerAction.Types.Inventory ).run ( gui );
        NUtils.waitEvent(()->!gui.getInventory ().getItems ( new NAlias ( "branch" ) ).isEmpty(),50);

        if ( gui.getInventory ().getItems ( new NAlias ( "branch" ) ).size () >= 5 ) {
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
