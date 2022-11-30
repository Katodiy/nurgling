package nurgling.bots.actions;

import haven.Gob;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.PathFinder;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;
import java.util.Arrays;

public class CabbageAction implements Action {
    final NAlias backed = new NAlias ( new ArrayList<> (
            Arrays.asList ( "wrap", "fruitroast", "nutje" )) );

    NAlias kiln = new NAlias (new ArrayList<>(Arrays.asList( "kiln" )), new ArrayList<>(Arrays.asList("tar")));
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        /// Ждем пока смелтеры не потухнут
        while ( true ) {
            new WaitAction ( () -> {
                for ( Gob gob : Finder.findObjects ( kiln ) ) {
                        if ( ( gob.getModelAttribute() & 1 ) != 0 ) {
                            return true;
                        }
                }
                return false;
            }, 100 ).run ( gui );
            for ( Gob gob : Finder.findObjects ( kiln ) ) {
                new PathFinder( gui, gob ).run ();
                new OpenTargetContainer ( gob, "Kiln" ).run ( gui );
                if ( !gui.getInventory ( "Kiln" ).getAll ().isEmpty () )
                /// Забираем булки и переносим их в сундуки
                {
                    new TransferFromContainerToContainer ( backed, gob, "Kiln", kiln, backed_area  ).run ( gui );
                }
                /// Загружаем новыми булками
                new TransferFromContainerToContainer ( backed, unbacked_area, gob, "Kiln", kiln ).run ( gui );
                /// Проверяем есть ли булки
                new PathFinder ( gui, gob ).run ();
                new OpenTargetContainer ( gob, "Kiln" ).run ( gui );
                if ( gui.getInventory ( "Kiln" ).getAll ().isEmpty () )
                /// Если нет, то выходим
                {
                    return new Results(Results.Types.NO_ITEMS);
                }
                /// Заполняем овны палочками с пайлов
                new FillFuelFromPiles ( 4, new NAlias ( "branch" ), kiln, new NAlias ( "branch" ),
                        AreasID.branch ).run ( gui );
                /// Поджигаем
                new LightGob ( kiln, 1 ).run ( gui );
            }
            Thread.sleep ( 10 );
        }
        
//        return new Results ( Results.Types.SUCCESS );
    }

    NArea backed_area;
    NArea unbacked_area;

    public CabbageAction(NArea backed_area, NArea unbacked_area) {
        this.backed_area = backed_area;
        this.unbacked_area = unbacked_area;
    }
}
