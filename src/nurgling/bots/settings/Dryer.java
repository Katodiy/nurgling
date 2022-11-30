package nurgling.bots.settings;

import haven.*;
import haven.res.lib.itemtex.ItemTex;
import nurgling.tools.AreaSelecter;
import nurgling.tools.AreasID;

public class Dryer extends Settings {
    public Dryer(){
        prev = add(new Label("Сырые шкуры:"));
        prev = add(new AreaIconSelecter(AreasID.raw_hides),prev.pos("bl").add(0,5));
        prev = add(new ThresholdSetter(AreasID.raw_hides),prev.pos("bl").add(0,5));
        prev = add(new Label("Шкуры низкого качества:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.lqhides),prev.pos("bl").add(0,5));
        prev = add(new ThresholdSetter(AreasID.lqhides),prev.pos("bl").add(0,5));
        prev = add(new Label("Шкуры высокого качества:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.hqhides),prev.pos("bl").add(0,5));
        prev = add(new ThresholdSetter(AreasID.hqhides),prev.pos("bl").add(0,5));
        prev = add(new Label("Дополнительно:"),prev.pos("bl").add(0,5));
        prev = add(new Label("\tМедведи:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.bear_hides),prev.pos("bl").add(0,5));
        prev = add(new ThresholdSetter(AreasID.bear_hides),prev.pos("bl").add(0,5));
        prev = add(new Label("\tЛоси:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.moose_hides),prev.pos("bl").add(0,5));
        prev = add(new ThresholdSetter(AreasID.moose_hides),prev.pos("bl").add(0,5));
        pack();
    }
}
