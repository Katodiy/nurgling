package nurgling;

import haven.Gob;
import haven.render.MixColor;
import haven.render.Pipe;
import nurgling.bots.actions.NGAttrib;

public class NTubColor extends NGAttrib implements Gob.SetupMod {
    private MixColor color;
    Gob owner;
    boolean isFree = true;
    NConstMarker marker = null;

    long currentStage;
    public NTubColor(Gob g) {
        super(g);
        owner = g;
    }


    @Override
    public void update() {
        if(marker == null) {
            marker = new NConstMarker(gob, NGob.Tags.tanning);
            gob.addcustomol(marker);
        }
        if ((gob.modelAttribute & 1) != 0 || gob.modelAttribute == 0) {
            gob.addTag(NGob.Tags.warning);
            marker.isVisible = true;
        } else {
            gob.removeTag(NGob.Tags.warning);
            marker.isVisible = false;
        }
        if ((gob.modelAttribute & 8) != 0) {
            gob.addTag(NGob.Tags.ready);
            gob.removeTag(NGob.Tags.inwork, NGob.Tags.free);
            color = NConfiguration.getInstance().colors.get("ready");
        } else if ((gob.modelAttribute & 4) != 0) {
            gob.addTag(NGob.Tags.inwork);
            color = NConfiguration.getInstance().colors.get("inwork");
            gob.removeTag(NGob.Tags.free, NGob.Tags.ready);
        } else if ((gob.modelAttribute & 2) != 0) {
            gob.addTag(NGob.Tags.free);
            color = NConfiguration.getInstance().colors.get("free");
            gob.removeTag(NGob.Tags.inwork, NGob.Tags.ready);
        } else {
            gob.removeTag(NGob.Tags.inwork, NGob.Tags.free, NGob.Tags.ready);
        }
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
