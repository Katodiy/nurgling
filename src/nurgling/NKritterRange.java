package nurgling;

import haven.*;
import haven.render.Pipe;
import haven.render.Render;
import haven.render.RenderTree;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public class NKritterRange extends NAreaRad{
    NConstMarker marker;
    boolean oldState = false;
    NConfiguration.Ring ring;

    Color c1;
    Color c2;
    double r;
    public NKritterRange(Owner owner, NConfiguration.Ring ring, Color color1, Color color2) {
        super((Gob) owner, (ring.isEnable)?(float)ring.size:0, color1, color2);
        c1 = color1;
        c2 = color2;
        r = ring.size;
        this.ring = ring;
        oldState = ring.isEnable;
    }

    @Override
    public void gtick(Render g) {
        if(ring.size!=r){
            if(oldState) {
                r = ring.size;
                setR(g, (float)r);
            }
        }
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

    @Override
    public boolean tick(double dt) {
        if(((Gob)owner).isTag(NGob.Tags.knocked))
            return true;
        return super.tick(dt);
    }

}
