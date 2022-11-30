package nurgling.bots.actions;

import haven.Gob;
import haven.WItem;
import haven.res.ui.tt.leashed.Leashed;
import nurgling.*;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

import java.util.ArrayList;

import static nurgling.NUtils.getGameUI;

public class TruffleCollect implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        NUtils.transferToInventory();
        if(new LeashAnimal(new NAlias("pig")).run(gui).type== Results.Types.FAIL)
            return new Results ( Results.Types.NO_PIG );
        new NomadCollector(new NAlias("truffle"),"./pig.dat", true, Finder.findObject(new NAlias("pig"))).run(gui);

        Gob hitch = Finder.findObject(new NAlias("hitching"));
        WItem rope = gui.getInventory().getItem(new NAlias("rope"), Leashed.class);
        new TakeToHand(rope).run(gui);
        new PathFinder(gui,hitch).run();
        NUtils.activateItem(hitch);
        NUtils.waitEvent(()->rope.item.info()!=null && ((NGItem)rope.item).getInfo(Leashed.class)!=null,50);
        NUtils.transferToInventory();
        NUtils.waitEvent(()->gui.getInventory().getItem(new NAlias("rope"))!=null,20);
        new FillContainers(new NAlias("truffle"), AreasID.truffle, new ArrayList<>()).run(gui);
        NUtils.logOut();
        return new Results ( Results.Types.SUCCESS );
    }

    public TruffleCollect(
    ) {
    }

}
