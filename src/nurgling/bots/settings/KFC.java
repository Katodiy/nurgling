package nurgling.bots.settings;

import haven.Label;
import nurgling.tools.AreasID;

public class KFC extends Settings {
    public KFC(){
        prev = add(new Label("Chicken Coops:"));
        prev = add(new AreaIconSelecter(AreasID.hens),prev.pos("bl").add(0,5));
        prev = add(new Label("Incubator(for chick):"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.chicken),prev.pos("bl").add(0,5));
        prev = add(new Label("Swill:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.swill),prev.pos("bl").add(0,5));
        prev = add(new Label("Water:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.water),prev.pos("bl").add(0,5));
        prev = add(new Label("Feather:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.feather),prev.pos("bl").add(0,5));
        prev = add(new ThresholdSetter(AreasID.feather),prev.pos("bl").add(0,5));
        prev = add(new Label("Entrails:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.entr),prev.pos("bl").add(0,5));
        prev = add(new ThresholdSetter(AreasID.entr),prev.pos("bl").add(0,5));
        prev = add(new Label("Chicken meat:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.raw_meat),prev.pos("bl").add(0,5));
        prev = add(new Label("Bones:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.lqbone),prev.pos("bl").add(0,5));
        prev = add(new ThresholdSetter(AreasID.lqbone),prev.pos("bl").add(0,5));
        prev = add(new Label("Eggs:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.eggs),prev.pos("bl").add(0,5));

        pack();
    }
}
