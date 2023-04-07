package nurgling.bots.actions;

import haven.GAttrib;
import haven.Gob;

public abstract class NGAttrib extends GAttrib {

    public abstract void update();
    public NGAttrib(Gob gob) {
        super(gob);
    }

    public boolean tick(double dt){
        return false;
    }
}
