package nurgling;

import haven.Gob;
import haven.render.MixColor;
import haven.render.Pipe;
import nurgling.bots.actions.NGAttrib;

public class NContainerColor extends NGAttrib implements Gob.SetupMod {
    private MixColor color;

    Gob owner;
    long currentStage;
    NProperties.Container cont;

    public NContainerColor(Gob g, NProperties.Container cont) {
        super(g);
        owner = g;
        currentStage = g.modelAttribute;
        this.cont = cont;
    }

    @Override
    public void update() {
        if ((gob.modelAttribute & ~cont.free) == 0) {
            gob.addTag(NGob.Tags.free);
            gob.removeTag(NGob.Tags.not_full, NGob.Tags.full);
        } else if ((gob.modelAttribute & cont.full) == cont.full) {
            gob.addTag(NGob.Tags.full);
            gob.removeTag(NGob.Tags.not_full, NGob.Tags.free);
        } else {
            gob.addTag(NGob.Tags.not_full);
            gob.removeTag(NGob.Tags.free, NGob.Tags.full);
        }
        if (gob.isTag(NGob.Tags.full))
            color = new MixColor(NConfiguration.getInstance().colors.get("full"));
        else if (gob.isTag(NGob.Tags.not_full)) {
            color = new MixColor(NConfiguration.getInstance().colors.get("not_full"));
        } else if (gob.isTag(NGob.Tags.free)) {
            color = new MixColor(NConfiguration.getInstance().colors.get("free"));
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
        if (disabled)
            return color;
        else
            return null;
    }

    public boolean disabled = true;
}
