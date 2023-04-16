package nurgling.bots.actions;

import haven.GItem;
import haven.Gob;
import haven.WItem;
import haven.res.ui.tt.leashed.Leashed;
import nurgling.*;
import nurgling.tools.Finder;

import static nurgling.NGItem.READY;

public class LeashAnimal implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        Gob gob = Finder.findObject(name);
        while(gui.map.player().rc.dist(NUtils.getGob(gob.id).rc)>15) {
            new PathFinder(gui, gob, PathFinder.Type.dyn).run();
        }
        GItem rope = gui.getInventory().getItem(new NAlias("rope"));
        if(rope == null && gui.vhand!=null)
            if(NUtils.isIt(gui.vhand,new NAlias("rope")))
                rope = gui.vhand.item;

        new TakeToHand(gui.getInventory().getItem(new NAlias("rope"))).run(gui);
        NUtils.activateItem(gob);
        GItem finalRope = gui.vhand.item;
        NUtils.waitEvent(()-> finalRope!= null && (((NGItem) finalRope).status&READY)==READY &&  finalRope.info()!=null && ((NGItem) finalRope).getInfo(Leashed.class)!=null,50);
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
