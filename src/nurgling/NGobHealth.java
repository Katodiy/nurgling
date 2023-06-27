package nurgling;

import haven.Gob;
import haven.GobHealth;
import haven.render.Pipe;

import java.awt.*;

public class NGobHealth extends GobHealth {
    public NGobHealth(Gob g, float hp) {
        super(g, hp);
        Gob.Overlay hpOl = g.findol(NObjectTexLabel.class);
        if(hpOl!=null){
            hpOl.remove();
        }
        if(hp<1){
            if(hp<=0.25f)
            {
                g.addol(new Gob.Overlay(g,new NObjectTexLabel(g,String.format("%.0f",hp*100)+"%", Color.WHITE, "brokenh")));
            }
            else if(hp<=0.5f)
            {
                g.addol(new Gob.Overlay(g,new NObjectTexLabel(g,String.format("%.0f",hp*100)+"%", Color.WHITE, "brokenm")));
            }
            else
            {
                g.addol(new Gob.Overlay(g,new NObjectTexLabel(g,String.format("%.0f",hp*100)+"%", Color.WHITE, "brokens")));
            }
        }
    }
    public Pipe.Op gobstate() {
        return(null);
    }

}
