package nurgling.bots.actions;

import haven.*;

import nurgling.*;
import nurgling.bots.tools.HarvestOut;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;
import java.util.Arrays;

public class SeederSeed implements Action {


    class SeedArea extends NArea{
        public NArea seedArea;


        public SeedArea(Coord2d begin, Coord2d end) {
            super(new Coord2d(Math.min(begin.x,end.x),Math.min(begin.y,end.y)),new Coord2d(Math.max(begin.x,end.x),Math.max(begin.y,end.y)));
            seedArea = new NArea(begin,end);
        }
    }

    void seedCrop(
            NGameUI gui,
            SeedArea area
    )
            throws InterruptedException {
        if (gui.getInventory().getItems(in.items).size() < 2) {
            if (!gui.hand.isEmpty())
                NUtils.transferToInventory();
            new TakeFromBarrels(in.outArea, gui.getInventory().getFreeSpace(), in.items).run(gui);
        }
        new PathFinder(gui, area.seedArea.begin).run();

        NArea checkArea = new NArea(NHitBox.getByName("iconsign"), area.begin);

        do {

            NUtils.activateItem(gui.getInventory().getItem(in.items));
            long start_id = NUtils.getUI().getTickId();
            while ( NUtils.getUI().getTickId() - start_id < 20) {
                Resource res = gui.ui.getcurs(Coord.z);
                if(res!=null){
                    if(res.name.contains("harvest"))
                        break;
                }
                Thread.sleep(50);
            }
            gui.map.wdgmsg("sel", area.seedArea.begin.round().div(MCache.tilesz2), area.seedArea.end.round().div(MCache.tilesz2), 1);
            checkArea.correct(checkArea.center,0);
            checkArea.begin = checkArea.begin.add(checkArea.center);
            checkArea.end = checkArea.end.add(checkArea.center);
            NUtils.waitEvent(() -> checkArea.countTiles()== Finder.findObjectsInArea( in.items,checkArea).size(), 20);
//            System.out.println(Finder.findObjectsInArea( in.items,checkArea).size());
        }
        while (!Finder.isGobInArea(checkArea, in.items));
    }

    @Override
    public Results run(NGameUI gui)
            throws InterruptedException {
        NArea input = Finder.findNearestMark(in.outArea);

        NArea area = new NArea(new Coord2d(input.begin.x + MCache.tilesz.x, input.begin.y + MCache.tilesz.y),
                new Coord2d(input.end.x - MCache.tilesz.x, input.end.y - MCache.tilesz.y));

        Coord2d pos = new Coord2d(area.begin.x + MCache.tilesz.x / 2, area.begin.y + MCache.tilesz.x / 2);
        Coord2d endPos = new Coord2d(Math.min(pos.x + MCache.tilesz.x, area.end.x - MCache.tilesz.x / 2), Math.min(pos.y + MCache.tilesz.y, area.end.y - MCache.tilesz.y / 2));
        ArrayList<SeedArea> harvestCoord = new ArrayList<>();
        while (pos.x <= area.end.x) {
            while (pos.y <= area.end.y - MCache.tilesz.x / 2) {
                harvestCoord.add(new SeedArea(new Coord2d(pos.x, pos.y), new Coord2d(Math.min(pos.x + MCache.tilesz.x, area.end.x - MCache.tilesz.x / 2),Math.min(pos.y + MCache.tilesz.y, area.end.y - MCache.tilesz.y / 2))));
                pos.y += 2* MCache.tilesz.y;
            }
            pos.y = area.end.y - MCache.tilesz.y / 2;
            pos.x += 2* MCache.tilesz.x;
            if(pos.x > area.end.x)
                break;
            while (pos.y >= area.begin.y + MCache.tilesz.y / 2) {
                harvestCoord.add(new SeedArea(new Coord2d(pos.x, pos.y), new Coord2d(Math.min(pos.x + MCache.tilesz.x, area.end.x - MCache.tilesz.x / 2),Math.max(pos.y - MCache.tilesz.y, area.begin.y + MCache.tilesz.y / 2))));
                pos.y -= 2*MCache.tilesz.y;
            }
            pos.y = area.begin.y + MCache.tilesz.y / 2;
            pos.x += 2* MCache.tilesz.x;

        }

        for (SeedArea hpos : harvestCoord) {
            hpos.installBox();
            long size = hpos.countTiles();
            System.out.println(Finder.findObjectsInArea( in.items,hpos).size() +","+ String.valueOf(size));
            while (Finder.findObjectsInArea( in.items,hpos).size()<size) {
                seedCrop(gui, hpos);
                if (NUtils.getStamina() <= 0.3) {
                    if (!gui.hand.isEmpty())
                        NUtils.transferToInventory();
                    new Drink(0.9, false).run(gui);
                }
            }
        }

        if (!gui.hand.isEmpty())
            NUtils.transferToInventory();
        new TransferToBarrel(in.outArea, in.items).run(gui);
        return new Results(Results.Types.SUCCESS);
    }

    public SeederSeed(
            HarvestOut in
    ) {
        this.in = in;
    }

    HarvestOut in;

}
