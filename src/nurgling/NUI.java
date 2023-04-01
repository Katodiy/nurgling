package nurgling;

import haven.Coord;
import haven.UI;

public class NUI extends UI {
    long tickId = 0;

    public NUI(Context uictx, Coord sz, Runner fun) {
        super(uictx, sz, fun);
        NUtils.setUI(this);
    }

    @Override
    public void tick() {
        super.tick();
        tickId += 1;
    }

    public long getTickId () {
        return tickId;
    }
}
