package nurgling.bots.settings;

import haven.*;
import haven.res.lib.itemtex.ItemTex;
import nurgling.tools.AreaSelecter;
import nurgling.tools.AreasID;

public class Dryer extends Settings {
    public Dryer(){
        prev = add(new Label("Raw hides:"));
        prev = add(new AreaIconSelecter(AreasID.raw_hides),prev.pos("bl").add(0,5));
        prev = add(new Label("Low quality hides:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.lqhides),prev.pos("bl").add(0,5));
        prev = add(new Label("High quality hides:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.hqhides),prev.pos("bl").add(0,5));
        prev = add(new ThresholdSetter(AreasID.hqhides),prev.pos("bl").add(0,5));
        pack();
    }
}
