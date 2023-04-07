package nurgling;

import haven.Gob;
import haven.render.MixColor;
import haven.render.Pipe;
import nurgling.bots.actions.NGAttrib;

public class NTroughColor extends NGAttrib implements Gob.SetupMod {
    private MixColor color;
    Gob owner;
    boolean isFree = true;
    NConstMarker marker = null;

    long currentStage;
    public NTroughColor(Gob g) {
        super(g);
        owner = g;
    }


    @Override
    public void update() {
        if(marker == null) {
            marker = new NConstMarker(gob, NGob.Tags.no_silo);
            gob.addcustomol(marker);
        }
        if ((gob.modelAttribute & 2) == 0) {
            gob.addTag(NGob.Tags.warning);
            marker.isVisible = true;
            color = new MixColor(NConfiguration.getInstance().colors.get("warning"));
        } else {
            gob.removeTag(NGob.Tags.warning);
            marker.isVisible = false;
            color = new MixColor(NConfiguration.getInstance().colors.get("no_color"));
        }
        currentStage = gob.getModelAttribute();
    }

    @Override
    public boolean tick(double dt) {
        if (owner.getModelAttribute() != currentStage) {
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
