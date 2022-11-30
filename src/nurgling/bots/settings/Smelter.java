package nurgling.bots.settings;

import haven.Label;
import nurgling.tools.AreasID;

public class Smelter extends Settings {
    public Smelter(){
        prev = add(new Label("Руда для плавки:"));
        prev = add(new AreaIconSelecter(AreasID.ore),prev.pos("bl").add(0,5));
        prev = add(new Label("Шлак:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.slag),prev.pos("bl").add(0,5));
        prev = add(new ThresholdSetter(AreasID.slag),prev.pos("bl").add(0,5));
        prev = add(new Label("Уголь:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.coal),prev.pos("bl").add(0,5));
        prev = add(new Label("Блоки:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.block),prev.pos("bl").add(0,5));
        prev = add(new Label("Канделябр:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.candelabrun),prev.pos("bl").add(0,5));
        prev = add(new Label("Слитки:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.bar),prev.pos("bl").add(0,5));
        prev = add(new Label("Бочка под ртуть:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.barrels),prev.pos("bl").add(0,5));
        pack();
    }
}
