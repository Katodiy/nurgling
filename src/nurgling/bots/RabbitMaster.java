package nurgling.bots;


import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.bots.actions.Equip;
import nurgling.bots.actions.FillFluid;
import nurgling.tools.AreasID;

import java.util.ArrayList;
import java.util.Arrays;


public class RabbitMaster extends Bot {
    
    
    public RabbitMaster(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "RabbitMaster";
        win_sz.y = 100;
        /// Одеваем топор
        runActions.add ( new Equip(
                new NAlias( new ArrayList<> ( Arrays.asList ( "axe" ) ), new ArrayList<String> () ) ) );
        /// доливаем воды
        runActions.add ( new FillFluid( AreasID.water, AreasID.bunny, 4, new NAlias ( "rabbithutch" ),
                new NAlias ( "water" ) ) );
        runActions.add ( new FillFluid ( AreasID.water, AreasID.rabbit, 4, new NAlias ( "rabbithutch" ),
                new NAlias ( "water" ) ) );
        /// досыпаем корма
        runActions.add ( new FillFluid ( AreasID.swill, AreasID.bunny, 32, new NAlias ( "rabbithutch" ),
                new NAlias ( new ArrayList<> ( Arrays.asList ( "flax", "wheat", "swill" ) ) ) ) );
        runActions.add ( new FillFluid ( AreasID.swill, AreasID.rabbit, 32, new NAlias ( "rabbithutch" ),
                new NAlias ( new ArrayList<> ( Arrays.asList ( "flax", "wheat", "swill" ) ) ) ) );
        /// Повышаем качество зайцев, собираем урожай
        runActions.add ( new nurgling.bots.actions.RabbitMaster () );
    }
    
    
    @Override
    public void initAction () {
    }
    
    @Override
    public void endAction () {
        super.endAction ();
    }
}
