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

        if(Finder.findObjectsInArea(new NAlias("stockpile-hemp"), in_area).isEmpty()) {
            new TakeFromContainers(new NAlias("stockpile"), target, (NUtils.getGameUI().getInventory().getFreeSpace() / 5) * 5, in_area, "Stockpile").run(gui);

            if (NUtils.getStamina() < 0.5) {
                new Drink(0.9, false).run(gui);
            }
            new UseWorkStation(new NAlias("loom")).run(gui);
            new Craft("Linen Cloth", new char[]{'c', 'p', 'f', 'l'}).run(gui);
        }
        else
        {
            new TakeFromContainers(new NAlias("stockpile"), target, (NUtils.getGameUI().getInventory().getFreeSpace() / 6) * 6, in_area, "Stockpile").run(gui);

            if (NUtils.getStamina() < 0.5) {
                new Drink(0.9, false).run(gui);
            }
            new UseWorkStation(new NAlias("loom")).run(gui);
            new Craft("Hemp Cloth", new char[]{'c', 'p', 'f', 'p'}).run(gui);
        }
        new TransferToPile ( out_area, NHitBox.get (  ), new NAlias ( "cloth", "Cloth" ), new NAlias ( "cloth", "Cloth" ) )
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
