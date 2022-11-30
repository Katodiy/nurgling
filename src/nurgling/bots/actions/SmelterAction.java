package nurgling.bots.actions;

import haven.Gob;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

import java.util.ArrayList;
import java.util.Arrays;
/// TODO флаг 16 для мехов
public class SmelterAction implements Action {
    static NAlias ores = new NAlias ( new ArrayList<> (
            Arrays.asList ( "cassiterite", "hematite", "peacockore", "chalcopyrite", "malachite", "leadglance",
                    "cinnabar", "ilmenite", "hornsilver", "argentite", "sylvanite" , "magnetite", "nagyagite", "petzite", "cuprite","limonite") ) );
    private final NAlias smelter_name = new NAlias ( new ArrayList<> ( Arrays.asList ( "smelter" ) ),
            new ArrayList<> () );

    boolean isPrim = false;
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        while ( !Finder.findObjectsInArea ( new NAlias ( "ore" ), Finder.findNearestMark ( AreasID.ore ) )
                       .isEmpty () ) {
            /// Ждем пока смелтеры не потухнут
            new WaitAction ( () -> {
                for ( Gob gob : Finder.findObjects ( smelter_name ) ) {
                        if(NUtils.isIt(gob,"primsmelter")){
                            isPrim = true;
                        }
                        if ( ( gob.getModelAttribute() & 2 ) != 0 ) {
                            return true;
                        }
                }
                return false;
            }, 50 ).run ( gui );

            /// Забираем слитки и переносим их в сундуки
            new TransferBars ().run ( gui );
            /// Выбрасываем шлак
            if ( Finder.findNearestMark ( AreasID.slag ) == null ) {
                new ClearContainers ( smelter_name, isPrim?"Furnace":"Ore Smelter", new NAlias ( "slag" ) ).run ( gui );
            }
            else {
                new TransferToPileFromContainer ( smelter_name, new NAlias ( "stockpile-stone" ), new NAlias ( "slag" ),
                        AreasID.slag, isPrim?"Furnace":"Ore Smelter" ).run ( gui );
            }
            /// Заполняем смелтеры с пайлов
            new TransferFromContainerToContainer ( new NAlias ( "stockpile-ore" ), smelter_name, ores, AreasID.ore,
                    "Stockpile", isPrim?"Furnace":"Ore Smelter", 1024 ).run ( gui );

            /// Заполняем смелтеры топливом с пайлов
            new FillFuelSmelter ( smelter_name ).run ( gui );
            /// Поджигаем
            new LightGob ( new NAlias ( "smelter" ), 2 ).run ( gui );
        }
        return new Results ( Results.Types.SUCCESS );
    }
}
