package nurgling.bots.actions;

import haven.GItem;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NHitBox;
import nurgling.NUtils;
import nurgling.bots.tools.AItem;
import nurgling.bots.tools.Ingredient;
import nurgling.tools.AreasID;

import java.util.ArrayList;
import java.util.Arrays;

public class TransferBones implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        
        
        NAlias bones = new NAlias ( new ArrayList<String> (
                Arrays.asList ( "/bone", "antlers", "adderskeleton", "claw", "beartooth", "antlers-moose",
                        "antlers" + "-reddeer", "tusk" ) ),
                new ArrayList<> ( Arrays.asList ( "saw", "finebonering", "borewormbeak","glue" ) ) );
        
        ArrayList<GItem> ditems = gui.getInventory ().getWItems( bones, AreasID.getTh(AreasID.lqbone), false );
        /// Переносим предметы в инвентарь
        
        for ( GItem item : ditems ) {
            NUtils.drop ( item );
        }


        ArrayList<GItem> items = gui.getInventory().getWItems(bones);
        for (GItem item : items) {
            AItem ingredient = Ingredient.get(item);
            NAlias name = new NAlias(NUtils.getInfo(item));
            if(ingredient!=null) {
                new TransferItemsToBarter(ingredient.barter_out, name, true).run(gui);
                new FillContainers(name, ingredient.area_out, new ArrayList<>()).run(gui);
            }
        }


        if(AreasID.get(AreasID.hqbone)!=null) {
            /// Высокое качество
            double th = AreasID.getTh(AreasID.hqbone);
            if (gui.getInventory().getWItems(bones, th, true).size() > 0) {

                new TransferItemsToBarter(AreasID.hqbone, bones, false, th).run(gui);

                new TransferToPile(AreasID.hqbone, NHitBox.getByName("stockpile"),
                        new NAlias("stockpile-bone"), bones, th).run(gui);

            }
        }
        
        /// остальное
        new TransferItemsToBarter ( AreasID.lqbone,bones,false ).run ( gui );

        new TransferToPile ( AreasID.lqbone, NHitBox.getByName ( "stockpile" ), new NAlias ( "stockpile-bone" ),
                bones ).run ( gui );
        
        return new Results ( Results.Types.SUCCESS );
    }
}
