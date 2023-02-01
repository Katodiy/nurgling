package nurgling;

import haven.Gob;
import haven.Loading;
import haven.RUtils;
import haven.WeakList;
import haven.render.Render;
import haven.render.RenderTree;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public class NKritterRange extends NSprite{
    public Collection<NKritterRange> current = new WeakList<>();
    NAreaRad fx;
    final Collection<RenderTree.Slot> slots = new ArrayList<>(1);
    NConstMarker marker;
    boolean oldState = false;
    NConfiguration.Ring ring;

    Color c1;
    Color c2;
    double r;
    public NKritterRange(Owner owner, NConfiguration.Ring ring, Color color1, Color color2) {
        super(owner, null);
        fx = new NAreaRad((Gob) owner, (float)ring.size, color1, color2);
        c1 = color1;
        c2 = color2;
        r = ring.size;
        marker = new NConstMarker((Gob) owner, NGob.Tags.no_silo);
        this.ring = ring;
    }

    public void show(boolean show) {
        for (NKritterRange spr : current)
            spr.show1(show);
    }

    public void show1(boolean show) {
        if (show) {
            Loading.waitfor(() -> RUtils.multiadd(slots, fx));
        } else {
            for (RenderTree.Slot slot : slots)
                slot.clear();
        }
    }


    public void added(RenderTree.Slot slot) {
        oldState = ring.isEnable;
        if (oldState)
            slot.add(fx);
        if (slots.isEmpty())
            current.add(this);
        slots.add(slot);
    }

    @Override
    public void gtick(Render g) {
        fx.gtick(g);
    }

    @Override
    public boolean tick(double dt) {
        if(oldState!=ring.isEnable ||((Gob)owner).isTag(NGob.Tags.knocked)){
            oldState = ring.isEnable;
            show(oldState && !((Gob)owner).isTag(NGob.Tags.knocked));
        }
        if(ring.size!=r){
            if(oldState) {
                for (RenderTree.Slot slot : slots)
                    slot.clear();
                fx = new NAreaRad((Gob) owner, (float) ring.size, c1, c2);
                Loading.waitfor(() -> RUtils.multiadd(slots, fx));
                current.clear();
                current.add(this);
                r = ring.size;
            }
        }
        return super.tick(dt);
    }

    public void removed(RenderTree.Slot slot) {
        slots.remove(slot);
        if (slots.isEmpty())
            current.remove(this);
    }


}
