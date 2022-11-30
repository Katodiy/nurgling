package nurgling.bots.actions;

import haven.*;
import haven.render.sl.InstancedUniform;
import haven.res.gfx.fx.fishline.FishLine;
import nurgling.*;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static haven.OCache.posres;

public class SpiningAction implements Action {
    class Candidate{
        public Button button;
        public String name;
    }
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        while (gui.getInventory().getNumberFreeCoord(new Coord(3,1))>1) {
            NUtils.waitEvent(() -> NUtils.getGameUI().map.player().findol(FishLine.class)!=null, 400);
            NUtils.waitEvent(() -> NUtils.getGameUI().map.player().findol(FishLine.class)==null || NUtils.getGameUI().getWindow("This is bait") != null, 2000);
            NUtils.waitEvent(() ->NUtils.getGameUI().getWindow("This is bait") != null, 50);
            if(NUtils.getGameUI().map.player().findol(FishLine.class)==null && NUtils.getGameUI().getWindow("This is bait") == null)
                return new Results(Results.Types.NO_ITEMS);
            Window win = NUtils.getGameUI().getWindow("This is bait");
            TreeMap<Integer, Candidate> candidates = new TreeMap<>();
            Candidate can = null;
            boolean selectNext = false;
            for (Widget ch : win.children()) {
                if (ch instanceof Button) {
                    if (((Button) ch).text.text.contains("Aim for")) {
                        can = new Candidate();
                        can.button = (Button) ch;
                    }

                } else if (ch instanceof Label) {
                    Label lab = (Label) ch;
                    if (!selectNext) {
                        if (lab.texts.contains("=")) {
                            selectNext = true;
                        }else if (lab.texts.contains(":")) {
                            if(can!=null) {
                                can.name = lab.texts.substring(0, lab.texts.indexOf(':'));
                            }
                        }
                    } else {
                        selectNext = false;
                        candidates.put(Integer.parseInt(lab.texts.substring(0, lab.texts.indexOf('%'))), can);
                        can = null;
                    }
                }
            }
            for(Map.Entry<Integer,Candidate> entry : candidates.entrySet()) {
                if(NConfiguration.getInstance().fishCandidates.contains(entry.getValue().name)) {
                    entry.getValue().button.click();
                    NUtils.waitEvent(() -> NUtils.getGameUI().getWindow("This is bait") == null, 500);
                }
            }
            if(NUtils.getGameUI().getWindow("This is bait") != null) {
                if (!candidates.isEmpty()) {
                    candidates.get(candidates.lastKey()).button.click();
                }
            }
            NUtils.waitEvent(() -> NUtils.getGameUI().getWindow("This is bait") == null, 500);
        }
        return new Results ( Results.Types.SUCCESS );
    }
}
