package nurgling;

import haven.GAttrib;
import haven.GOut;
import haven.Gob;
import haven.render.Render;
import haven.res.ui.croster.CattleId;

import java.awt.*;

public class NDomesticRing extends NTargetRing{
    Gob kritter;
    public NDomesticRing(Owner owner, Color color, float range, float alpha) {
        super(owner, color, range, alpha);
        kritter = (Gob)owner;
    }

    public NDomesticRing(Owner owner, Color color, float range) {
        super(owner, color, range);
        kritter = (Gob)owner;
    }

    @Override
    public boolean tick(double dt) {
        if(!kritter.isTag(NGob.Tags.selected))
            return true;
        return super.tick(dt);
    }

    @Override
    public void gtick(Render g) {
        super.gtick(g);
    }

    @Override
    public void draw(GOut g) {
        super.draw(g);
    }
}
