package nurgling.bots.actions;

import haven.*;

import nurgling.NAlias;
import nurgling.NConfiguration;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import static haven.MCache.tilesz;
import static haven.OCache.posres;
import static nurgling.bots.actions.NomadCalibration.anchors;

public class NomadFinder implements Action {
    @Override
    public Results run(NGameUI gui)
            throws InterruptedException {
        // URL url = Calibration.class.getProtectionDomain().getCodeSource().getLocation();
        // marks.clear();
        // if (url != null) {
            //
            //     try {
                //         System.out.println(Configuration.getInstance().nomadPath);
                //         DataInputStream in =
                //                 new DataInputStream(new FileInputStream(Configuration.getInstance().nomadPath));
                //
                //         while (true) {
                    //             try {
                        //                 if (!(in.available() > 0))
                        //                     break;
                        //                 marks.add(new Coord2d(in.readInt(), in.readInt()));
                        //             } catch (IOException e) {
                        //                 break;
                        //             }
                    //         }
                //     } catch (FileNotFoundException e) {
                //         e.printStackTrace();
                //     }
            // }

        Coord2d shift = Finder.findObjectInArea(anchors, 3000, mark_area).rc;
        for (Coord2d coord : marks) {
            Coord2d pos = coord.add(shift);
            Coord poscoord = pos.div(MCache.tilesz).floor();
            pos = new Coord2d((poscoord).x * tilesz.x + tilesz.x / 2, (poscoord).y * tilesz.y + tilesz.y / 2);
            gui.map.wdgmsg("click", Coord.z, pos.floor(posres), 1, 0);
            Coord2d finalPos = pos;
            do {
                NUtils.waitEvent(() -> gui.map.player().rc.dist(finalPos) < 5, 50);
                if(gui.map.player().rc.dist(finalPos) >= 5)
                    gui.map.wdgmsg("click", Coord.z, pos.floor(posres), 1, 0);
            }while(gui.map.player().rc.dist(finalPos) >= 5);
           //if (NUtils.alarmOrcalot()) {
           //    for (ChatUI.Selector.DarkChannel chan : gui.chat.chansel.chls) {
           //        if (chan.chan.name().equals(NConfiguration.getInstance().village)) {
           //            gui.chat.select(chan.chan);
           //            Thread.sleep(1000);
           //            Gob target = Finder.findObject( new NAlias("/orca", "/spermwhale"));
           //            gui.chat.sel.wdgmsg("msg", "Я нашел " + target.getres().name + "\040" + "!");
           //        }
           //    }
           //    return new Results(Results.Types.SUCCESS);
           //}
        }
        return new Results(Results.Types.SUCCESS);
    }

    public NomadFinder(NArea mark_area
    ) {
        this.mark_area = mark_area;

    }


    ArrayList<Coord2d> marks = new ArrayList<>();
    NArea mark_area = null;
}
