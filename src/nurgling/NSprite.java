package nurgling;

import com.sun.jndi.toolkit.ctx.AtomicContext;
import haven.Resource;
import haven.Sprite;

import java.util.concurrent.atomic.AtomicBoolean;

public class NSprite extends Sprite {
    protected NSprite(Owner owner, Resource res) {
        super(owner, res);
    }

    private final AtomicBoolean isKilled = new AtomicBoolean(false);

    public void setIsKilled(boolean isKilled) {
        this.isKilled.set(isKilled);
    }

    @Override
    public boolean tick(double dt) {
        if(isKilled.get())
            return true;
        return super.tick(dt);
    }
}
