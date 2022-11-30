package nurgling.bots.settings;

import haven.Label;
import nurgling.tools.AreasID;

public class Butcher extends Settings {
    public Butcher(){
        prev = add(new Label("Звери:"));
        prev = add(new AreaIconSelecter(AreasID.kritter),prev.pos("bl").add(0,5));
        prev = add(new Label("Шкуры:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.raw_hides),prev.pos("bl").add(0,5));
        prev = add(new Label("Внутренности:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.entr),prev.pos("bl").add(0,5));
        prev = add(new Label("Кишки:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.inten),prev.pos("bl").add(0,5));
        prev = add(new Label("Жир:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.fat),prev.pos("bl").add(0,5));
        prev = add(new Label("Мясо(любое):"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.raw_meat),prev.pos("bl").add(0,5));
        prev = add(new ThresholdSetter(AreasID.raw_meat),prev.pos("bl").add(0,5));
        prev = add(new Label("Кости низкого качества:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.lqbone),prev.pos("bl").add(0,5));
        prev = add(new ThresholdSetter(AreasID.lqbone),prev.pos("bl").add(0,5));
        prev = add(new Label("Кости высокого качеста:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.hqbone),prev.pos("bl").add(0,5));
        prev = add(new ThresholdSetter(AreasID.hqbone),prev.pos("bl").add(0,5));

        pack();
    }
}
