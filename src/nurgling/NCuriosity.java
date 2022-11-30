package nurgling;

import haven.RichText;
import haven.Utils;
import haven.resutil.Curiosity;

import java.awt.image.BufferedImage;

public class NCuriosity extends Curiosity {
    public transient final int lph;
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
            buf.append(String.format("Study time: $col[192,255,192]{%s} ($col[192,255,255]{%s})\n", timefmt(time), timefmt((int)(time/3.29f))));
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
        return NConfiguration.getInstance().isRealTime ? ((int) (3.29f * lph)) : lph;
    }
}
