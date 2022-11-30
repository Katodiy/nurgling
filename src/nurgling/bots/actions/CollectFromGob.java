package nurgling.bots.actions;

import haven.Gob;

import nurgling.*;

public class CollectFromGob implements Action {
    private NAlias pose = null;

    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        while( NUtils.checkGobFlower ( task, gob, 0 )) {
            new PathFinder( gui,gob ).run ();
            NFlowerMenu.instance.selectInCurrent ( task );
            if(pose == null) {
                NUtils.waitEvent(() -> NUtils.getProg() >= 0, 50);
                NUtils.waitEvent(() -> NUtils.getProg() < 0 || gui.getInventory().getFreeSpace() == 0, 10000);
            }else{
                NUtils.waitEvent(() -> NUtils.isPose(gui.getMap().player(),pose), 50);
                NUtils.waitEvent(() -> !NUtils.isPose(gui.getMap().player(),pose) || gui.getInventory().getFreeSpace() == 0, 10000);
            }
            if(gui.getInventory().getFreeSpace()==0)
                return new Results ( Results.Types.FULL );
        }
        return new Results ( Results.Types.SUCCESS );
    }
    
    public CollectFromGob(
            String task,
            Gob gob
    ) {
        this.task = task;
        this.gob = gob;
    }

    public CollectFromGob(
            String task,
            Gob gob,
            NAlias pose
    ) {
        this.task = task;
        this.gob = gob;
        this.pose = pose;
    }

    String task;
    Gob gob;
    
}
