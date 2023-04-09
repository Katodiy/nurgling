package nurgling;

import haven.Gob;
import haven.render.Render;

import java.awt.*;

public class NAreaRange extends NAreaRad{
    boolean oldState = false;
    NConfiguration.Ring ring;
    Color c1;
    Color c2;
    double r;
    public NAreaRange(Owner owner, String name, float r, Color color1, Color color2) {
        super((Gob) owner, (NConfiguration.getInstance().rings.get(name).isEnable) ? (float) NConfiguration.getInstance().rings.get(name).size : 0, color1, color2);
        c1 = color1;
        c2 = color2;
        this.r = r;
        this.ring = NConfiguration.getInstance().rings.get(name);
    }

    @Override
    public void gtick(Render g) {
        if(oldState!=ring.isEnable){
            oldState = ring.isEnable;
            if(oldState )
                setR(g,(float)r);
            else
                setR(g,0);
        }
        if(oldState)
            super.gtick(g);
    }
}
