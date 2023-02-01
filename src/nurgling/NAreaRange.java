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

public class NAreaRange extends NSprite{
    public Collection<NAreaRange> current = new WeakList<>();
    final NAreaRad fx;
    final Collection<RenderTree.Slot> slots = new ArrayList<>(1);
    NConstMarker marker;
    boolean oldState = false;
    String name;

    public NAreaRange(Owner owner,String name, float r, Color color1, Color color2) {
        super(owner, null);
        fx = new NAreaRad((Gob) owner, r, color1, color2);
        marker = new NConstMarker((Gob) owner, NGob.Tags.no_silo);
        this.name = name;
    }

    public void show(boolean show) {
        for (NAreaRange spr : current)
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
        oldState = NConfiguration.getInstance().rings.get(name).isEnable;
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
        if(oldState!=NConfiguration.getInstance().rings.get(name).isEnable){
            oldState = NConfiguration.getInstance().rings.get(name).isEnable;
            show(oldState);
        }
        return super.tick(dt);
    }

    public void removed(RenderTree.Slot slot) {
        slots.remove(slot);
        if (slots.isEmpty())
            current.remove(this);
    }


}
