package nurgling;

import haven.*;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

public class NBuff extends Buff {
    public static final RichText.Foundry fnd = new RichText.Foundry(new ChatUI.ChatParser(TextAttribute.FONT, Text.dfont.deriveFont(UI.scale(18f)), TextAttribute.FOREGROUND, Color.WHITE));
    private static final Map<String, Color> OPENINGS = new HashMap<String, Color>(4) {{
        put("paginae/atk/offbalance", new Color(81, 165, 56));
        put("paginae/atk/reeling", new Color(210, 210, 64));
        put("paginae/atk/dizzy", new Color(39, 82, 191));
        put("paginae/atk/cornered", new Color(192, 28, 28));
    }};

    protected Tex nmeter() {
        if(ntext == null)
            ntext = new TexI(fnd.render(Integer.toString(nmeter)).img);
        return(ntext);
    }

    private int rnmeter = -1;

    public NBuff(Indir<Resource> res) {
        super(res);
    }

    Color cl = null;
    boolean done =false;

    public void uimsg(String msg, Object... args) {
        if(msg == "nm") {
            this.rnmeter = (Integer) args[0];
        }
        super.uimsg(msg, args);
    }

    @Override
    public void draw(GOut g) {
        if (!done) {
            try {
                if (res.get() != null) {
                    done = true;
                    if (OPENINGS.containsKey(res.get().name)) {
                        cl = OPENINGS.get(res.get().name);
                    }
                }
            } catch (Loading e) {
            }
        }
        if (done) {
            g.chcolor(255, 255, 255, a);
            Double ameter = (this.ameter >= 0) ? Double.valueOf(this.ameter / 100.0) : ameteri.get();
            int ameteri = 0;
            if (ameter != null) {
                ameteri = (int) (100 * ameter);
                g.image(cframe, Coord.z);
                g.chcolor(0, 0, 0, a);
                g.frect(ameteroff, ametersz);
                g.chcolor(255, 255, 255, a);
                g.frect(ameteroff, new Coord((int) Math.floor(ameter * ametersz.x), ametersz.y));
            } else {
                g.image(frame, Coord.z);
            }
            try {
                Tex img = res.get().layer(Resource.imgc).tex();
                Coord isz = img.sz();
                if (cl != null) {
                    g.chcolor(cl);
                    g.frect(imgoff, isz);
                    g.chcolor(Color.WHITE);
                    if (ameteri != nmeter) {
                        ntext = null;
                        nmeter = ameteri;
                    }
                } else {
                    g.image(img, imgoff);
                    if (rnmeter != nmeter) {
                        nmeter = rnmeter;
                        ntext = null;
                    }
                }
                if (nmeter >= 0) {
                    Tex nm = nmeter();
                    g.aimage(nmeter(), new Coord(imgoff.x / 2 * img.sz().x - nm.sz().x, imgoff.y / 2 * img.sz().y - nm.sz().y), 0, 0);
                }

                Double cmeter;
                if (this.cmeter >= 0) {
                    double m = this.cmeter;
                    if (cmrem >= 0) {
                        double ot = cmrem;
                        double pt = Utils.rtime() - gettime;
                        m *= (ot - pt) / ot;
                    }
                    cmeter = m;
                } else {
                    cmeter = cmeteri.get();
                }
                if (cmeter != null) {
                    double m = Utils.clip(cmeter, 0.0, 1.0);
                    g.chcolor(255, 255, 255, a / 2);
                    Coord ccc = isz.div(2);
                    g.prect(imgoff.add(ccc), ccc.inv(), isz.sub(ccc), Math.PI * 2 * m);
                    g.chcolor(255, 255, 255, a);
                }
            } catch (Loading e) {
            }
        }
    }
}
