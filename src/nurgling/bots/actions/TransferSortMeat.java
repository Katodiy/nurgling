package nurgling.bots.actions;

import haven.Gob;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.bots.tools.InContainer;
import nurgling.tools.NArea;

import java.util.ArrayList;
import java.util.Arrays;

public class TransferSortMeat implements Action{
    boolean allEmpty(ArrayList<InContainer> icont){
        for(InContainer container : icont){
            if(!container.isFree)
                return false;
        }
        return true;
    }
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        ArrayList<InContainer> icont = new ArrayList<>();
        do {
            new TakeMaxFromContainers ( new NAlias( "meat" ), area, icont ).run ( gui );
            new TransferMeat ().run ( gui );
        }while ( !allEmpty(icont) );
        return new Results ( Results.Types.SUCCESS );
    }

    public TransferSortMeat(NArea area ){
        this.area = area;
    }
    
    NArea area;
}
