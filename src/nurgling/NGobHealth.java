package nurgling;

import haven.Gob;
import haven.GobHealth;
import haven.render.Pipe;

import java.awt.*;

public class NGobHealth extends GobHealth {
    public NGobHealth(Gob g, float hp) {
        super(g, hp);
        Gob.Overlay hpOl = g.findol(NObjectLabel.class);
        if(hpOl!=null){
            hpOl.remove();
        }
        if(hp<1){
            g.addol(new Gob.Overlay(g,new NObjectLabel(g,String.format("%.0f",hp*100)+"%", hp>0.25f?new Color(255,255,125):Color.RED)));
        }
    }
    public Pipe.Op gobstate() {
        return(null);
    }

}
