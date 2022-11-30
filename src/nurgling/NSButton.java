package nurgling;

import haven.Button;
import haven.Window;
import haven.*;

import java.awt.*;
import java.awt.image.BufferedImage;

public class NSButton extends SIWidget {
    public static final BufferedImage bl = Resource.loadsimg("gfx/hud/buttons/tbtn/left");
    public static final BufferedImage br = Resource.loadsimg("gfx/hud/buttons/tbtn/right");
    public static final BufferedImage bt = Resource.loadsimg("gfx/hud/buttons/tbtn/top");
    public static final BufferedImage bb = Resource.loadsimg("gfx/hud/buttons/tbtn/bottom");
    public static final BufferedImage dt = Resource.loadsimg("gfx/hud/buttons/tbtn/dtex");
    public static final BufferedImage ut = Resource.loadsimg("gfx/hud/buttons/tbtn/utex");
    public static final BufferedImage bm = Resource.loadsimg("gfx/hud/buttons/tbtn/mid");
    public static final int hs = bl.getHeight(), hl = bm.getHeight();
    public static final Resource click = Loading.waitfor(Resource.local().load("sfx/hud/btn"));
    @Deprecated public static final Resource.Audio lbtdown = Loading.waitfor(Resource.local().load("sfx/hud/lbtn")).layer(Resource.audio, "down");
    @Deprecated public static final Resource.Audio lbtup   = Loading.waitfor(Resource.local().load("sfx/hud/lbtn")).layer(Resource.audio, "up");
    public static final Audio.Clip clbtdown = lbtdown, clbtup = lbtup;
    public static final int margin = UI.scale(10);
    public boolean lg;
    public Text text;
    public BufferedImage cont;
    public Indir<Resource> res;
    public Runnable action = null;
    static Text.Foundry tf = new Text.Foundry(Text.serif.deriveFont( Font.BOLD, UI.scale(12f))).aa(true);
    static Text.Furnace nf = new PUtils.BlurFurn(new PUtils.TexFurn(tf, Window.ctex), 1, 1, new Color(80, 40, 0));
    private boolean a = false;
    private UI.Grab d = null;



    @RName("btn")
    public static class $Btn implements Factory {
        public Widget create(UI ui, Object[] args) {
            if(args.length > 2)
                return(new Button(UI.scale((Integer)args[0]), (String)args[1], ((Integer)args[2]) != 0));
            else
                return(new Button(UI.scale((Integer)args[0]), (String)args[1]));
        }
    }
    @RName("ltbtn")
    public static class $LTBtn implements Factory {
        public Widget create(UI ui, Object[] args) {
            return(wrapped(UI.scale((Integer)args[0]), (String)args[1]));
        }
    }
    
    public static Button wrapped(int w, String text) {
        Button ret = new Button(w, tf.renderwrap(text, w - margin));
        return(ret);
    }
    
    private static boolean largep(int w) {
        return(w >= (bl.getWidth() + bm.getWidth() + br.getWidth()));
    }
    
    private NSButton(int w, boolean lg) {
        super(new Coord(w, lg?hl:hs));
        this.lg = lg;
    }
    
    private NSButton(int w, int h, boolean lg) {
        super(new Coord(w, h));
        this.lg = lg;
    }
    
    public NSButton(int w, String text, boolean lg, Runnable action) {
        this(w, lg);
        this.text = nf.render(text);
        this.cont = this.text.img;
        this.action = action;
    }
    
    public NSButton(int w, String text, boolean lg) {
        this(w, text, lg, null);
        this.action = () -> wdgmsg("activate");
    }
    
    public NSButton(int w, String text, Runnable action) {
        this(w, text, largep(w), action);
    }
    
    public NSButton(int w, String text) {
        this(w, text, largep(w));
    }
    
    public NSButton(int w, Text text) {
        this(w, largep(w));
        this.text = text;
        this.cont = text.img;
    }
    
    public NSButton(int w, BufferedImage cont) {
        this(w, largep(w));
        this.cont = cont;
    }
    
    public NSButton(int w, int h, BufferedImage cont) {
        this(w, h, largep(w));
        this.cont = cont;
    }

    public NSButton(int w, int h, Resource.Named load) {
        this(w,h,largep(w));
        this.res = load;
        this.cont = Loading.waitfor(res).flayer(Resource.imgc).scaled();
    }
    
    public NSButton action(Runnable action) {
        this.action = action;
        return(this);
    }
    
    public void draw(BufferedImage img) {
        Graphics g = img.getGraphics();
        int yo = lg?((hl - hs) / 2):0;
        
        g.drawImage(a?dt:ut, 4, yo + 4, sz.x - 8, hs - 8, null);
        
        Coord tc = sz.sub(Utils.imgsz(cont)).div(2);
        if(a)
            tc = tc.add(UI.scale(1), UI.scale(1));
        g.drawImage(cont, tc.x, tc.y, null);
        
        g.dispose();
    }
    
    public void change(String text, Color col) {
        this.text = tf.render(text, col);
        this.cont = this.text.img;
        redraw();
    }
    
    public void change(String text) {
        this.text = nf.render(text);
        this.cont = this.text.img;
        redraw();
    }
    
    public void click() {
        if(action != null)
            action.run();
    }
    
    public boolean gkeytype(java.awt.event.KeyEvent ev) {
        click();
        return(true);
    }
    
    public void uimsg(String msg, Object... args) {
        if(msg == "ch") {
            if(args.length > 1)
                change((String)args[0], (Color)args[1]);
            else
                change((String)args[0]);
        } else {
            super.uimsg(msg, args);
        }
    }
    
    public void mousemove(Coord c) {
        if(d != null) {
            boolean a = c.isect(Coord.z, sz);
            if(a != this.a) {
                this.a = a;
                redraw();
            }
        }
    }
    
    protected void depress() {
        ui.sfx(click);
    }
    
    protected void unpress() {
        ui.sfx(click);
    }
    
    public boolean mousedown(Coord c, int button) {
        if(button != 1)
            return(false);
        a = true;
        d = ui.grabmouse(this);
        depress();
        redraw();
        return(true);
    }
    
    public boolean mouseup(Coord c, int button) {
        if((d != null) && button == 1) {
            d.remove();
            d = null;
            a = false;
            redraw();
            if(c.isect(new Coord(0, 0), sz)) {
                unpress();
                click();
            }
            return(true);
        }
        return(false);
    }
}
