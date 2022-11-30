package nurgling.bots.actions;


import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

public class FillFuelSmelter implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        if ( Finder.findObjects ( new NAlias( "primsmelter" ) )
                   .size () == 0 ) {
            new FillFuelFromPiles ( 9, new NAlias ( "coal" ), smelter_name, new NAlias ( "coal" ),
                    AreasID.coal ).run ( gui );
        }
        else {
            new FillFuelFromPiles ( 4, new NAlias ( "block" ), smelter_name, new NAlias ( "block" ),
                    AreasID.block ).run ( gui );
        }
        return new Results ( Results.Types.SUCCESS );
    }
    
    public FillFuelSmelter(
            NAlias smelter_name
    ) {
        this.smelter_name = smelter_name;
    }
    
    NAlias smelter_name;
}
