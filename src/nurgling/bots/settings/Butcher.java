package nurgling.bots.settings;

import haven.Label;
import nurgling.tools.AreasID;

public class Butcher extends Settings {
    public Butcher(){
        prev = add(new Label("Kritters:"));
        prev = add(new AreaIconSelecter(AreasID.kritter),prev.pos("bl").add(0,5));
        prev = add(new Label("Raw Hides:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.raw_hides),prev.pos("bl").add(0,5));
        prev = add(new Label("Entrails:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.entr),prev.pos("bl").add(0,5));
        prev = add(new ThresholdSetter(AreasID.entr),prev.pos("bl").add(0,5));
        prev = add(new Label("Intensities:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.inten),prev.pos("bl").add(0,5));
        prev = add(new ThresholdSetter(AreasID.inten),prev.pos("bl").add(0,5));
        prev = add(new Label("Fat:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.fat),prev.pos("bl").add(0,5));
        prev = add(new Label("Any meat:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.raw_meat),prev.pos("bl").add(0,5));
        prev = add(new ThresholdSetter(AreasID.raw_meat),prev.pos("bl").add(0,5));
        prev = add(new Label("low quality bones:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.lqbone),prev.pos("bl").add(0,5));
        prev = add(new ThresholdSetter(AreasID.lqbone),prev.pos("bl").add(0,5));
        prev = add(new Label("high quality bones:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.hqbone),prev.pos("bl").add(0,5));
        prev = add(new ThresholdSetter(AreasID.hqbone),prev.pos("bl").add(0,5));

        pack();
    }
}
