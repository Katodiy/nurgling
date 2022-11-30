package nurgling.bots.actions;


import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NHitBox;
import nurgling.NUtils;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class CraftCloth implements Action {


    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {

        target = new NAlias( new ArrayList<> ( Arrays.asList ( "flax", "hemp") ) );


        new TakeFromContainers ( new NAlias ( "stockpile" ), target, 5, in_area, "Stockpile" ).run ( gui );

        if(NUtils.getStamina()<0.5){
            new Drink(0.9,false).run(gui);
        }
        new UseWorkStation ( new NAlias ( "loom" ) ).run ( gui );
        new Craft ( "Linen Cloth", new char[]{ 'c', 'p', 'f', 'l' } ).run ( gui );

        new TransferToPile ( out_area, NHitBox.get (  ), new NAlias ( "cloth" ), new NAlias ( "cloth" ) )
                .run ( gui );
        return new Results ( Results.Types.SUCCESS );
    }

    public CraftCloth(
            NArea in_area,
            NArea out_area
    ) {
        this.in_area = in_area;
        this.out_area = out_area;
    }
    
    NAlias target;
    
    AtomicBoolean isFlax;
    NArea in_area;
    NArea out_area;
}
