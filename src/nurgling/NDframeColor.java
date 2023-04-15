package nurgling;

import haven.Gob;
import haven.render.MixColor;
import haven.render.Pipe;
import nurgling.bots.actions.NGAttrib;

public class NDframeColor extends NGAttrib implements Gob.SetupMod {
    private MixColor color;

    Gob owner;
    boolean isFree = true;
    int count;
    Gob.Overlay targetOverlay = null;
    public NDframeColor(Gob g) {
        super(g);
        owner = g;
        count = g.ols.size();
    }


    haven.Gob.Overlay findOl(){
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

        for (Gob.Overlay ol : gob.ols) {
            if (!(ol.spr instanceof NSprite)) {
                isFound = true;
                targetOverlay = ol;
                break;
            }
        }
        if (!isFound) {
            color = NConfiguration.getInstance().colors.get("free");
            gob.addTag(NGob.Tags.free);
            isFree = true;
        }
        else
        {
            if (!NUtils.isIt(targetOverlay.res, "-blood", "-fishraw", "-windweed") || NUtils.isIt(targetOverlay, "-windweed-dry")) {
                gob.addTag(NGob.Tags.ready);
                color = NConfiguration.getInstance().colors.get("ready");
            }
            else {
                gob.addTag(NGob.Tags.inwork);
                color = NConfiguration.getInstance().colors.get("inwork");
            }
        }
        count = gob.ols.size();
    }

    @Override
    public boolean tick(double dt) {
        if (owner.ols.size() != count || targetOverlay !=findOl()) {
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
