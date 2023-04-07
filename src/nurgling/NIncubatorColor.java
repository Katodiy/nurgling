package nurgling;

import haven.Gob;
import haven.render.MixColor;
import haven.render.Pipe;
import nurgling.bots.actions.NGAttrib;

public class NIncubatorColor extends NGAttrib implements Gob.SetupMod {
    private MixColor color;
    Gob owner;
    boolean isFree = true;
    NIncubatorMarker marker;

    long currentStage;
    public NIncubatorColor(Gob g) {
        super(g);
        owner = g;
        marker = new NIncubatorMarker(gob);
        gob.addcustomol(marker);
    }


    @Override
    public void update() {
        if (gob.isTag(NGob.Tags.chickencoop)) {
            if ((gob.modelAttribute & 1) == 0) {
                gob.addTag(NGob.Tags.no_water);
            } else {
                gob.removeTag(NGob.Tags.no_water);
            }
            if ((gob.modelAttribute & 2) == 0) {
                gob.addTag(NGob.Tags.no_silo);
            } else {
                gob.removeTag(NGob.Tags.no_silo);
            }
            if ((gob.modelAttribute & 3) != 3) {
                color = new MixColor(NConfiguration.getInstance().colors.get("warning"));
            } else {
                color = new MixColor(NConfiguration.getInstance().colors.get("no_color"));
            }
        } else {

            if ((gob.modelAttribute & 4) == 0) {
                gob.addTag(NGob.Tags.no_water);
            } else {
                gob.removeTag(NGob.Tags.no_water);
            }
            if ((gob.modelAttribute & 16) == 0) {
                gob.addTag(NGob.Tags.no_silo);
            } else {
                gob.removeTag(NGob.Tags.no_silo);
            }
            if ((gob.modelAttribute & 20) != 20) {
                color = new MixColor(NConfiguration.getInstance().colors.get("warning"));
            } else {
                color = new MixColor(NConfiguration.getInstance().colors.get("no_color"));
            }
        }
    }

    @Override
    public boolean tick(double dt) {
        if (owner.ols.size() != currentStage) {
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
