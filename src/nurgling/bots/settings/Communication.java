package nurgling.bots.settings;

import haven.Button;
import haven.Label;
import haven.TextEntry;
import nurgling.NConfiguration;

public class Communication extends Settings{
    public Communication() {
        prev = add(new Label("Village chat name"));
        TextEntry name = new TextEntry(110, "");
        if (NConfiguration.getInstance().village != null) {
            name.settext(NConfiguration.getInstance().village);
        }
        prev = add(name, prev.pos("bl").add(0, 5));
        prev = add(new Button(50, "Set") {
            @Override
            public void click() {
                NConfiguration.getInstance().village = name.text();
            }
        }, prev.pos("bl").add(0, 5));
        pack();
    }
}
