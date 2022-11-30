package nurgling.bots.actions;

import haven.Gob;
import haven.WItem;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NHitBox;
import nurgling.NUtils;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;

public class DigCellarAction implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        
        Gob cellar = Finder.findObject ( new NAlias( "cellar" ) );
        ArrayList<WItem> saved = gui.getInventory ().getItems ();
        while ( Finder.findObject ( new NAlias ( "cellar" ) ) != null ) {
            if ( NUtils.getStamina() <= 0.35 ) {
                new Drink ( 0.9, false ).run ( gui );
            }
            
            NUtils.activate ( cellar );
            NUtils.waitEvent ( () -> NUtils.getProg()>=0, 60 );
            NUtils.waitEvent ( () -> NUtils.getProg()<0, 5000 );

            NUtils.waitEvent ( () -> Finder.findObject ( new NAlias ( "bumlings" ) ) != null, 60 );
            new PlaceLifted ( workArea.center, NHitBox.get (  ), new NAlias ( "bumlings" ) )
                    .run ( gui );
            NUtils.waitEvent (
                    () -> Finder.findObject ( new NAlias ( "bumlings" ) ).rc.dist ( workArea.center ) < 1e-4, 60 );
            do {
                new SelectFlowerAction ( Finder.findObject ( new NAlias ( "bumlings" ) ), "Chip",
                        SelectFlowerAction.Types.Gob ).run ( gui );
                NUtils.waitEvent ( () -> NUtils.getProg() >= 0, 120 );
                
                while ( NUtils.getProg() >= 0 || gui.getInventory ().getItems ().size () != saved.size () ) {
                    for ( WItem item : gui.getInventory ().getItems () ) {
                        if ( !saved.contains ( item ) ) {
                            NUtils.drop ( item );
                        }
                    }
                }
            }
            while ( Finder.findObject ( new NAlias ( "bumlings" ) ) != null );
            for ( WItem item : gui.getInventory ().getItems () ) {
                if ( !saved.contains ( item ) ) {
                    NUtils.drop ( item );
                }
            }
        }
        return new Results ( Results.Types.SUCCESS );
    }
    
    
    public DigCellarAction(NArea workArea ) {
        this.workArea = workArea;
    }
    
    NArea workArea;
}
