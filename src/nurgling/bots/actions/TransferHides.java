package nurgling.bots.actions;

import haven.Gob;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NHitBox;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

import java.util.ArrayList;
import java.util.Arrays;

public class TransferHides implements Action {
    private NAlias hides = new NAlias ( new ArrayList<> ( Arrays.asList ( "hide", "scale" ) ),
            new ArrayList<> ( Arrays.asList ( "blood", "raw" ) ) );
    private NAlias raw_hides = new NAlias ( new ArrayList<String> ( Arrays.asList ( "blood", "raw" ) ),
            new ArrayList<String> ( Arrays.asList ( "stern", "straw", "Straw" ) ) );
    
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        
        
        Gob bear_barter = Finder.findObjectInArea ( new NAlias ( "barter" ), 1000,
                Finder.findNearestMark ( AreasID.bone1 ) );
        if ( bear_barter != null ) {
            new TransferItemsToBarter ( bear_barter, new NAlias ("bearhide"), AreasID.bone1, false ,150).run ( gui );
        }
        new TransferToPile ( AreasID.raw_hides,  NHitBox.get(), new NAlias ( "stockpile" ),
                raw_hides ).run ( gui );
        /// медведи
        new TransferToPile ( AreasID.bear_hides,  NHitBox.get(),
                new NAlias ( "stockpile-hide" ), new NAlias ( new ArrayList<> ( Arrays.asList ( "bearhide" ) ) ), 150 )
                .run ( gui );
        /// Кабаны
        new TransferToPile ( AreasID.boar_hides,  NHitBox.get(),
                new NAlias ( "stockpile-hide" ), new NAlias ( new ArrayList<> ( Arrays.asList ( "boarhide" ) ) ), 150 )
                .run ( gui );
        /// Лоси
        new TransferToPile ( AreasID.moose_hides,  NHitBox.get(),
                new NAlias ( "stockpile-hide" ), new NAlias ( new ArrayList<> ( Arrays.asList ( "moosehide" ) ) ), 150 )
                .run ( gui );
        /// Рассомахи
        new TransferToPile ( AreasID.wolverine_hides,  NHitBox.get(),
                new NAlias ( "stockpile-hide" ), new NAlias ( new ArrayList<> ( Arrays.asList ( "wolverinehide" ) ) ), 70 )
                .run ( gui );
        /// Англеры
        new TransferToPile ( AreasID.angler_hides,  NHitBox.get(),
                new NAlias ( "stockpile-hide" ), new NAlias ( new ArrayList<> ( Arrays.asList ( "angler" ) ) ), 200 )
                .run ( gui );
        /// Котики
        new TransferToPile ( AreasID.greyseal_hides, NHitBox.get(),
                new NAlias ( "stockpile-hide" ), new NAlias ( new ArrayList<> ( Arrays.asList ( "greysealhide" ) ) ) )
                .run ( gui );
        new TransferToPile ( AreasID.leather,  NHitBox.get(),
                new NAlias ( "stockpile-hide" ), new NAlias ( new ArrayList<> ( Arrays.asList ( "borewormhide" ) ) ))
                .run ( gui );
        /// Высокое качество
        new TransferToPile ( AreasID.fox_hides,  NHitBox.get(),
                new NAlias ( "stockpile-hide" ), new NAlias ( new ArrayList<> ( Arrays.asList ( "fox" ) ) ), 30 )
                .run ( gui );

        new TransferToPile ( AreasID.bat_hides,  NHitBox.get(),
                new NAlias ( "stockpile-hide" ), new NAlias ( new ArrayList<> ( Arrays.asList ( "bat" ) ) ), 25 )
                .run ( gui );


        new TransferToPile ( AreasID.hqhides,  NHitBox.get(), new NAlias ( "stockpile-hide" ),
                hides, 40 ).run ( gui );


        
        /// остальное
        new TransferToPile ( AreasID.lqhides,  NHitBox.get(), new NAlias ( "stockpile-hide" ), hides )
                .run ( gui );
        
        return new Results ( Results.Types.SUCCESS );
    }
}
