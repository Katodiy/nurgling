package nurgling.bots.settings;

import haven.CheckBox;
import haven.Label;
import nurgling.NConfiguration;
import nurgling.tools.AreasID;

public class Smelter extends Settings {
    public Smelter(){
        prev = add(new Label("Ore:"));
        prev = add(new AreaIconSelecter(AreasID.ore),prev.pos("bl").add(0,5));
        prev = add(new Label("Slag:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.slag),prev.pos("bl").add(0,5));
        prev = add(new ThresholdSetter(AreasID.slag),prev.pos("bl").add(0,5));
        prev = add(new Label("Coal:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.coal),prev.pos("bl").add(0,5));
        prev = add(new Label("Blocks:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.block),prev.pos("bl").add(0,5));
        prev = add(new Label("Candelabrum:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.candelabrum),prev.pos("bl").add(0,5));
        prev = add(new Label("Bars:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.bar),prev.pos("bl").add(0,5));
        prev = add(new Label("Barrel for Quicksilver:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.barrels),prev.pos("bl").add(0,5));
        prev = add(new CheckBox("Miner credo:") {
            {
                a = NConfiguration.getInstance().isMinerCredo;
            }

            public void set(boolean val) {
                NConfiguration.getInstance().isMinerCredo = val;
                a = val;
            }
        }, prev.pos("bl").adds(0, 5));
        pack();
    }
}
