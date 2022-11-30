package nurgling.bots.settings;

import haven.Label;
import nurgling.tools.AreasID;

public class Tanning extends Settings {
    public Tanning(){
        prev = add(new Label("Танящая жидкость:"));
        prev = add(new AreaIconSelecter(AreasID.tanning_flued),prev.pos("bl").add(0,5));
        prev = add(new ThresholdSetter(AreasID.tanning_flued),prev.pos("bl").add(0,5));
        prev = add(new Label("Шкуры низкого качества:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.lqhides),prev.pos("bl").add(0,5));
        prev = add(new ThresholdSetter(AreasID.lqhides),prev.pos("bl").add(0,5));
        prev = add(new Label("Кожа:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.leather),prev.pos("bl").add(0,5));
        prev = add(new ThresholdSetter(AreasID.leather),prev.pos("bl").add(0,5));

        pack();
    }
}
