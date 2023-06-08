package nurgling;

import haven.*;

public abstract class NSettinsSetW extends Widget {
    TextEntry textEntry;

    public NSettinsSetW(String label) {

        prev = add(new Label(label), new Coord(0,UI.scale(8)));
        prev = textEntry = add(new TextEntry(50,""),new Coord(250,UI.scale(4)));

        add(new Button(50,"Set"){
            @Override
            public void click() {
                parseValue();
            }
        }, prev.pos("ur").adds(5, -UI.scale(4)));
        pack();
    }

    abstract void parseValue();

    public void setText(String val){
        textEntry.settext(val);
    }
}

