package nurgling.bots.settings;

import haven.Label;
import nurgling.tools.AreasID;

public class Tanning extends Settings {
    public Tanning(){
        prev = add(new Label("Tanning fluid:"));
        prev = add(new AreaIconSelecter(AreasID.tanning_flued),prev.pos("bl").add(0,5));
        prev = add(new Label("Low quality hides:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.lqhides),prev.pos("bl").add(0,5));
        prev = add(new Label("Leather:"),prev.pos("bl").add(0,5));
        prev = add(new AreaIconSelecter(AreasID.leather),prev.pos("bl").add(0,5));
        pack();
    }
}
