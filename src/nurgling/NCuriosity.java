package nurgling;

import haven.*;
import haven.resutil.Curiosity;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;

public class NCuriosity extends Curiosity implements GItem.OverlayInfo<Tex>{
    public static final float server_ratio = 3.287f;
    public int rm = 0;
    private static final int delta = 60;
    public transient final int lph;

    NGItem.MeterInfo m = null;
    public NCuriosity(Owner owner, int exp, int mw, int enc, int time) {
        super(owner, exp, mw, enc, time);
        this.lph = (exp > 0 && time > 0) ? (3600 * exp / time) : 0;
    }

    public NCuriosity(Curiosity inf) {
        this(inf.owner, inf.exp, inf.mw, inf.enc, inf.time);
    }

    public BufferedImage tipimg() {
        StringBuilder buf = new StringBuilder();
        if(exp > 0)
            buf.append(String.format("Learning points: $col[192,192,255]{%s} ($col[192,192,255]{%s}/h)\n", Utils.thformat(exp), Utils.thformat(Math.round(exp / (time / 3600.0)))));
        if(time > 0) {
            buf.append(String.format("Study time: $col[192,255,192]{%s} ($col[192,255,255]{%s})\n", timefmt(time), timefmt((int)(time/server_ratio))));
        }
        rm = (int)(remaining()/server_ratio);
        if(rm!=time)
        {
            buf.append(String.format("Remaining time: $col[192,255,192]{%s}\n", timefmt(rm)));
        }
        if(mw > 0)
            buf.append(String.format("Mental weight: $col[255,192,255]{%d}\n", mw));
        if(enc > 0)
            buf.append(String.format("Experience cost: $col[255,255,192]{%d}\n", enc));
        if(lph>0) {
            buf.append(String.format("LP/H: $col[192,255,255]{%d}\n", lph(this.lph)));
            buf.append(String.format("LP/H/Weight: $col[192,255,255]{%d}\n", lph(this.lph / mw)));
        }
        return(RichText.render(buf.toString(), 0).img);
    }

    public static int lph(int lph){
        return NConfiguration.getInstance().isRealTime ? ((int) (server_ratio * lph)) : lph;
    }


    public int remaining() {
        if(owner instanceof NGItem) {
            NGItem item = ((NGItem) owner);
            if(m == null)
            {
                m = ItemInfo.find(GItem.MeterInfo.class, item.info());
            }
            double meter = (m != null) ? m.meter() : 0;
            if(meter > 0) {
                long now = System.currentTimeMillis();
                long remStudy = (long) ((1.0 - meter) * time);
                long elapsed = (long) (server_ratio * (now - item.meterUpdated) / 1000);
                return (int) (remStudy - elapsed);
            }
        }
        return -1;
    }

    public boolean needUpdate(){
        if(rm>0) {
            return Math.abs(rm - (int) (remaining() / server_ratio)) > 1;
        }
        else
        {
            rm = (int) (remaining() / server_ratio);
        }
        return false;
    }

    static int[] div = {60, 60, 24};
    static String[] units = {"s", "m", "h", "d"};
    protected static String shorttime(int time) {
        int[] vals = new int[4];
        vals[0] = time;
        for(int i = 0; i < div.length; i++) {
            vals[i + 1] = vals[i] / div[i];
            vals[i] = vals[i] % div[i];
        }
        StringBuilder buf = new StringBuilder();
        int count = 0;
        for(int i = 3; i >= 0; i--) {
            if(vals[i] > 0) {
                if(count++ == 2)
                    break;
                buf.append(vals[i]);
                buf.append(units[i]);
            }
        }
        return(buf.toString());
    }
    public static final Text.Foundry ntimefnd = new Text.Foundry(Text.sans, 9, new java.awt.Color(255, 255, 50));
    @Override
    public Tex overlay() {
        BufferedImage text = ntimefnd.render(shorttime((int) (remaining() / server_ratio))).img;
        BufferedImage bi = new BufferedImage(text.getWidth(), text.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = bi.createGraphics();
        Color rgb = new Color(0, 0, 0, 115);
        graphics.setColor(rgb);
        graphics.fillRect(0, 0, bi.getWidth(), bi.getHeight());
        graphics.drawImage(text, 0, 0, null);
        return (new TexI(bi));
    }


    @Override
    public void drawoverlay(GOut g, Tex data) {
        if(data!=null) {
            g.aimage(data, new Coord(data.sz().x,g.sz().y - data.sz().y), 1, 0);
        }
//        long ct = System.currentTimeMillis();
//        if((ct - old)/1000 >=1)
//        {
//            if(m!=null) {
//                if (old == 0) {
//                    old = ct;
//                    lold = (long) ((1.0 - m.meter()) * time);
//                }
//
//                if(lold!=(long) ((1.0 - m.meter()) * time))
//                {
//                    System.out.println("old:" + old + "|" + lold);
//                    old = ct;
//                    lold = (long) ((1.0 - m.meter()) * time);
//                    System.out.println("new:" + old + "|" + lold);
//                }
//            }
//        }
    }
}
