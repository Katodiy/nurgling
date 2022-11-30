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

public class CraftRope implements Action {
    
    
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        
        target = new NAlias( new ArrayList<> ( Arrays.asList ( "flax", "hemp", "cattail" ) ) );
        
        if ( !Finder.findObjectsInArea ( new NAlias ( "stockpile-hide" ), in_area ).isEmpty () ) {
            while ( gui.getInventory ().getItems (new NAlias ("hidestrap")).size ()<10 ){
                new TakeFromContainers ( new NAlias ( "stockpile-hide" ),
                        new NAlias (new ArrayList<> (Arrays.asList ( "hide", "patch", "scale" )) ), 1,
                        in_area,
                        "Stockpile" ).run ( gui );
                NUtils.stopWithClick ();
                new Craft ( "Hide Straps", new char[]{ 'c', 'p', 'f', 'i' } ).run ( gui );
            }
        }
        else {
            new TakeFromContainers ( new NAlias ( "stockpile" ), target, 10, in_area, "Stockpile" ).run ( gui );
        }
        if(NUtils.getStamina()<0.5){
            new Drink(0.9,false).run(gui);
        }
        new UseWorkStation ( new NAlias ( "ropewalk" ) ).run ( gui );
        new Craft ( "Rope", new char[]{ 'c', 'p', 'f', 'r' } ).run ( gui );
        
        new TransferToPile ( out_area, NHitBox.get (  ), new NAlias ( "rope" ), new NAlias ( "rope" ) )
                .run ( gui );
        return new Results ( Results.Types.SUCCESS );
    }
    
    public CraftRope(
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
