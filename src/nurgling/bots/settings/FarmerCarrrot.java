package nurgling.bots.settings;

import haven.Label;
import nurgling.tools.AreasID;

public class FarmerCarrrot extends Settings {
    public FarmerCarrrot(){
        prev = add(new Label("Through:"));
        prev = add(new AreaIconSelecter(AreasID.swill),prev.pos("bl").add(0,5));
        prev = add(new Label("Field:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.carrot),prev.pos("bl").add(0,5));
        pack();
    }
}
