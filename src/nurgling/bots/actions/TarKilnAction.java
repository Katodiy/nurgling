package nurgling.bots.actions;

import haven.Coord;
import haven.Gob;

import nurgling.*;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;

public class TarKilnAction implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        ArrayList<Gob> targets = new ArrayList<> ();
        /// Выбираем все не горящие таркилны
        for ( Gob gob : Finder
                .findAllinDistance ( new NAlias( "tarkiln" ), 3000 ) ) {
            if ( ( (  gob.getModelAttribute()&16 )==0 ) ) {
                targets.add ( gob );
            }
        }

        for ( Gob gob : targets ) {
            /// Собираем готовый уголь
            while ( ( gob.getModelAttribute() & 8 )!=0 ) {
                new TransferToPile ( coal_area, NHitBox.getByName ( "coal" ), new NAlias ("stockpile-coal"),
                        new NAlias ("coal") ).run(gui);
                new CollectFromGob ( "Collect coal", gob ).run ( gui );
                NUtils.stopWithClick();
            }
            new TransferToPile ( coal_area, NHitBox.getByName ( "coal" ), new NAlias ("stockpile-coal"),
                    new NAlias ("coal") ).run(gui);
            /// Заполняем новыми блоками
                int count = 80;
                while ( count>0 ) {
                    int num = Math.min ( gui.getInventory ().getNumberFreeCoord ( new Coord ( 2, 1 )  ) -1, count);

                    if ( new WorkWithLog ( num,
                            new NAlias ( "log" ), true, block_area ).run ( gui ).type != Results.Types.SUCCESS ) {
                        return new Results ( Results.Types.NO_ITEMS );
                    }
                    num = gui.getInventory ().getItems (new NAlias ("block")).size ();
                    PathFinder pf = new PathFinder ( gui, gob );
                    pf.setHardMode(true);
                    pf.run ();
                    for ( int i = 0 ; i < num ; i++ ) {
                        new TakeToHand ( new NAlias ( "block" ) ).run ( gui );
                        int counter = 0;
                        while ( !gui.hand.isEmpty () && counter < 20 ) {
                            NUtils.activateItem ( gob );
                            Thread.sleep ( 50 );
                            counter++;
                        }
                    }
                    count-=num;
                }
                /// И поджигаем
                new LightGob ( 16, gob ).run ( gui );
            }
            
        
        
        return new Results ( Results.Types.SUCCESS );
        
    }
    
    public TarKilnAction(NArea block_area, NArea coal_area ) {
        this.coal_area = coal_area;
        this.block_area = block_area;
    }
    
    NArea coal_area;
    NArea block_area;
}
