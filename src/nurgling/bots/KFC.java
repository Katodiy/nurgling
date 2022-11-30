package nurgling.bots;


import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.bots.actions.ChickenMaster;
import nurgling.bots.actions.Equip;
import nurgling.bots.actions.FillFluid;
import nurgling.tools.AreasID;

import java.util.ArrayList;
import java.util.Arrays;


public class KFC extends Bot {
    
    
    public KFC(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "KFC";
        win_sz.y = 100;
        /// Одеваем топор
        runActions.add ( new Equip(
                new NAlias( new ArrayList<> ( Arrays.asList ( "axe" ) ), new ArrayList<String> () ) ) );
        /// доливаем воды
        runActions.add ( new FillFluid( AreasID.water, AreasID.chicken, 1, new NAlias ( "chickencoop" ),
                new NAlias ( "water" ) ) );
        runActions.add ( new FillFluid ( AreasID.water, AreasID.hens, 1, new NAlias ( "chickencoop" ),
                new NAlias ( "water" ) ) );
        /// досыпаем корма
        runActions.add ( new FillFluid ( AreasID.silo, AreasID.chicken, 2, new NAlias ( "chickencoop" ),
                new NAlias ( new ArrayList<> ( Arrays.asList ( "flax", "wheat", "swill" ) ) ) ) );
        runActions.add ( new FillFluid ( AreasID.silo, AreasID.hens, 2, new NAlias ( "chickencoop" ),
                new NAlias ( new ArrayList<> ( Arrays.asList ( "flax", "wheat", "swill" ) ) ) ) );
        /// Повышаем качество курочки , собираем урожай
        runActions.add ( new ChickenMaster() );
    }
    
    
    @Override
    public void initAction () {
    }
    
    @Override
    public void endAction () {
        super.endAction ();
    }
}
