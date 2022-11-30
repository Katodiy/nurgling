package nurgling.bots.actions;

import haven.Gob;
import haven.WItem;
import haven.res.ui.tt.leashed.Leashed;
import nurgling.*;
import nurgling.tools.Finder;

public class LeashAnimal implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        Gob gob = Finder.findObject(name);
        while(gui.map.player().rc.dist(NUtils.getGob(gob.id).rc)>15) {
            new PathFinder(gui, gob, PathFinder.Type.dyn).run();
        }
        WItem rope = gui.getInventory().getItem(new NAlias("rope"));
        if(rope == null && gui.vhand!=null)
            if(NUtils.isIt(gui.vhand,new NAlias("rope")))
                rope = gui.vhand;

        new TakeToHand(gui.getInventory().getItem(new NAlias("rope"))).run(gui);
        NUtils.activateItem(gob);
        WItem finalRope = rope;
        NUtils.waitEvent(()-> finalRope.item.info()!=null && ((NGItem) finalRope.item).getInfo(Leashed.class)!=null,50);
        NUtils.transferToInventory();
        NUtils.waitEvent(()->gui.getInventory().getItem(new NAlias("rope"), Leashed.class)!=null,20);
        if(gui.getInventory().getItem(new NAlias("rope"), Leashed.class)!=null)
            return new Results ( Results.Types.SUCCESS );
        else
            return new Results ( Results.Types.FAIL );
    }

    public LeashAnimal(
            NAlias name
    ) {
        this.name = name;
    }

    NAlias name;
}
