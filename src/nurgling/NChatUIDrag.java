package nurgling;

import haven.*;

import java.awt.event.KeyEvent;

public class NChatUIDrag extends NResizedWidget{
    public ChatUI chat;
    private static final int blpw = UI.scale(142), brpw = UI.scale(142);
    public NChatUIDrag(String name) {
        super(name);
        chat = add(new ChatUI(0,0));
        chat.show();
        pack();
        resize(500,500);
    }

    @Override
    public void draw(GOut g, boolean strict) {
        super.draw(g, strict);
    }


//    public boolean mousedown(Coord c, int button) {
//        chat.mousedown(c,button);
//        return super.mousedown(c,button);
//    }
//
//    public void mousemove(Coord c) {
//        chat.mousemove(c);
//        super.mousemove(c);
//    }
//
//    public boolean mouseup(Coord c, int button) {
//        chat.mouseup(c,button);
//       return super.mouseup(c,button);
//    }

    public boolean keydown(KeyEvent ev) {
        return chat.keydown(ev);
    }

    @Override
    public void resize(Coord sz) {
        super.resize(sz);
        chat.resize(sz.x - UI.scale(10), sz.y );


    }
}
