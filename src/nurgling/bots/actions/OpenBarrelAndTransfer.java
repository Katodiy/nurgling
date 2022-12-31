package nurgling.bots.actions;

import haven.*;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.tools.AreasID;

public class OpenBarrelAndTransfer implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
                if(barrel==null){
                    return new Results(Results.Types.NO_BARREL);
                }
                new PathFinder( gui, barrel ).run ();
                if ( new OpenTargetContainer ( barrel, "Barrel" ).run ( gui ).type != Results.Types.SUCCESS ) {
                    return new Results ( Results.Types.OPEN_FAIL );
                }
                double barrelCont = gui.getBarrelContent(content);
                if(barrelCont>-1 && barrelCont <content_count) {
                    do {

                        WItem item = gui.getInventory().getItem(content);
                        if (item != null) {
                            if (gui.hand.isEmpty()) {
                                new TakeToHand(item).run(gui);
                            }
                            barrelCont+=NUtils.getAmount(item.item);
                            NUtils.activateItem(barrel);
                            NUtils.waitEvent(()->gui.hand.isEmpty(),200);
                        } else {
                            return new Results(Results.Types.SUCCESS);
                        }
                    }while (barrelCont<content_count);
                }
                else if(barrelCont==-1)
                {
                    return new Results ( Results.Types.BARREL_NOT_FOUND );
                }
        return new Results ( Results.Types.FULL );
    }
    
    public OpenBarrelAndTransfer(
            double content_count,
            NAlias content,
            AreasID zone,
            Gob barrel
    ) {
        this.content_count = content_count;
        this.content = content;
        this.zone = zone;
        this.barrel= barrel;
    }
    
    Gob barrel;
    /// Количество содержимого
    double content_count;
    /// Название содержимого
    NAlias content;
    /// Зона бочек для поиска
    AreasID zone;
}
