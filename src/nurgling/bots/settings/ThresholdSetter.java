package nurgling.bots.settings;

import haven.Button;
import haven.Label;
import haven.TextEntry;
import haven.Widget;
import nurgling.tools.AreasID;

public class ThresholdSetter extends Widget {
    AreasID id;
    TextEntry entry;
    public ThresholdSetter(AreasID id) {
        this.id = id;
        prev = add(new Label("Min quality:"));
        prev = add(entry = new TextEntry(60, String.valueOf(AreasID.getTh(id))), prev.pos("bl").add(0,5));
        add(new Button(50,"Set"){
            @Override
            public void click() {
                AreasID.set(id,Integer.valueOf(entry.text()));
                AreasID.write();
            }
        },prev.pos("ur").add(5,-2));
        pack();
    }
}
