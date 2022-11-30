package nurgling;

import haven.*;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class NISBox extends ISBox {
    private Value value;
    private TakeButton take;

    private int rem;
    private int av;

    private Indir<Resource> res;
    public NISBox(
            Indir<Resource> res,
            int rem,
            int av,
            int bi
    ) {
        super ( res, rem, av, bi );
        this.av = av;
        this.rem = rem;
        this.res = res;
    }
    
    public int getFreeSpace () {
        if ( label == null || label.text == null ) {
            return Integer.MAX_VALUE;
        }
        int sep = label.text.indexOf ( '/' );
        if ( sep > 0 ) {
            String count = label.text.substring ( 0, sep );
            String capacity = label.text.substring ( sep + 1 );
            try {
                int res = Integer.parseInt ( capacity ) - Integer.parseInt ( count );
                return res;
            }
            catch ( NumberFormatException nfe ) {
            }
        }
        return Integer.MAX_VALUE;
    }

    @Override
    protected void added() {
        if(parent instanceof Window) {
            boolean isStockpile = "Stockpile".equals(((Window) parent).cap.text);
            if(isStockpile) {
                take = new TakeButton(UI.scale(40), "Take");
                value = new Value(UI.scale(40), "");
                parent.add(value, UI.scale(60, 46));
                value.canactivate = true;


                parent.add(take, UI.scale(105, 44));
                take.canactivate = true;

                sz = sz.add(0, UI.scale(25));
            }
        }
    }


    public boolean mousedown(Coord c, int button) {
        if(take != null) {
            Coord cc = xlate(take.c, true);
            if(c.isect(cc, take.sz)) {
                return take.mousedown(c.sub(cc), button);
            }
        }
        if(value != null) {
            Coord cc = xlate(value.c, true);
            if(c.isect(cc, value.sz)) {
                return value.mousedown(c.sub(cc), button);
            }
        }
        if (button == 1) {
            if (ui.modshift ^ ui.modctrl) {           //SHIFT or CTRL means pull
                int dir = ui.modctrl ? -1 : 1;        //CTRL means pull out, SHIFT pull in
                int all = (dir > 0) ? av - rem : rem; //count depends on direction
                int k = ui.modmeta ? all : 1;         //ALT means pull all
                transfer(dir, k);
            } else {
                wdgmsg("click");
            }
            return (true);
        }
        return (false);
    }

    public void transfer(int dir, int amount) {
        for (int i = 0; i < amount; i++) {
            wdgmsg("xfer2", dir, 1); //modflags set to 1 to emulate only SHIFT pressed
        }
    }

    private class TakeButton extends Button{
        public TakeButton(int w, String text) {
            super(w, text);
        }

        @Override
            public void click () {
                int amount = rem;
                try {
                    amount = Integer.parseInt(value.text());
                } catch (Exception ignored) {
                }
                if (amount > rem) {
                    amount = rem;
                }
                if (amount > 0) {
                    transfer(-1, amount);
                }

            }
    }
    private static class Value extends TextEntry {
        private static final Set<Integer> ALLOWED_KEYS = new HashSet<Integer>(Arrays.asList(
                KeyEvent.VK_0, KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_4,
                KeyEvent.VK_5, KeyEvent.VK_6, KeyEvent.VK_7, KeyEvent.VK_8, KeyEvent.VK_9,
                KeyEvent.VK_NUMPAD0, KeyEvent.VK_NUMPAD1, KeyEvent.VK_NUMPAD2, KeyEvent.VK_NUMPAD3, KeyEvent.VK_NUMPAD4,
                KeyEvent.VK_NUMPAD5, KeyEvent.VK_NUMPAD6, KeyEvent.VK_NUMPAD7, KeyEvent.VK_NUMPAD8, KeyEvent.VK_NUMPAD9,
                KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
                KeyEvent.VK_ENTER, KeyEvent.VK_BACK_SPACE, KeyEvent.VK_DELETE
        ));

        public Value(int w, String deftext) {
            super(w, deftext);
        }

        @Override
        public boolean keydown(KeyEvent ev) {
            int keyCode = ev.getKeyCode();
            if(keyCode == 0){
                keyCode = ev.getKeyChar();
            }
            if (ALLOWED_KEYS.contains(keyCode)) {
                return super.keydown(ev);
            }
            return false;
        }
    }
}
