package nurgling;

import haven.Gob;
import haven.render.MixColor;
import haven.render.Pipe;
import nurgling.bots.actions.NGAttrib;

public class NBarrelColor extends NGAttrib implements Gob.SetupMod {
    private MixColor color;

    Gob owner;
    boolean isFree = true;
    int count;

    public NBarrelColor(Gob g) {
        super(g);
        owner = g;
        count = g.ols.size();
    }


    @Override
    public void update() {
        boolean isFound = false;
        for (Gob.Overlay ol : gob.ols) {
            if (!(ol.spr instanceof NSprite)) {
                isFound = true;
                break;
            }
        }
        if (!isFound) {
            color = new MixColor(NConfiguration.getInstance().colors.get("free"));
            gob.addTag(NGob.Tags.free);
            isFree = true;
        } else {
            color = new MixColor(NConfiguration.getInstance().colors.get("no_color"));
            gob.removeTag(NGob.Tags.free);
            isFree = false;
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

    public boolean disabled = true;
}
