package nurgling;

import haven.Gob;
import haven.render.MixColor;
import haven.render.Pipe;
import nurgling.bots.actions.NGAttrib;

public class NCheeseColor extends NGAttrib implements Gob.SetupMod {
    private MixColor color;

    Gob owner;
    boolean isFree = true;
    int count = 0;
    Gob.Overlay targetOverlay = null;
    public NCheeseColor(Gob g) {
        super(g);
        owner = g;
        count = g.ols.size();
    }


    Gob.Overlay findOl(){
        for (Gob.Overlay ol : gob.ols) {
            if (!(ol.spr instanceof NSprite)) {
                return ol;
            }
        }
        return null;
    }


    @Override
    public void update() {
        boolean isFound = false;
        count = 0;
        for (Gob.Overlay ol : gob.ols) {
            if (!(ol.spr instanceof NSprite)) {
                isFound = true;
                count++;
            }
        }
        if (!isFound) {
            color = new MixColor(NConfiguration.getInstance().colors.get("free"));
            gob.addTag(NGob.Tags.free);
            gob.removeTag(NGob.Tags.not_full, NGob.Tags.full);
            isFree = true;
        } else if (count < 3) {
            gob.addTag(NGob.Tags.not_full);
            gob.removeTag(NGob.Tags.free, NGob.Tags.full);
            color = new MixColor(NConfiguration.getInstance().colors.get("not_full"));
        } else {
            gob.addTag(NGob.Tags.full);
            gob.removeTag(NGob.Tags.free, NGob.Tags.not_full);
            color = new MixColor(NConfiguration.getInstance().colors.get("full"));
        }
        count = gob.ols.size();
    }

    @Override
    public boolean tick(double dt) {
        if (owner.ols.size() != count) {
            update();
        }
        return super.tick(dt);
    }


    public Pipe.Op placestate() {
        if (isFree)
            return color;
        else
            return null;
    }
}
