package nurgling.bots.actions;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.bots.tools.InContainer;
import nurgling.tools.NArea;

import java.util.ArrayList;

public class TransferSortFish implements Action{
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
            new TakeMaxFromContainers ( new NAlias( "fish" ), area, icont ).run ( gui );
            new TransferFish ().run ( gui );
        }while ( !allEmpty(icont) );
        return new Results ( Results.Types.SUCCESS );
    }

    public TransferSortFish(NArea area ){
        this.area = area;
    }
    
    NArea area;
}
