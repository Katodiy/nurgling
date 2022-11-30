package nurgling;

import haven.GAttrib;
import haven.Gob;
import haven.render.MixColor;
import haven.render.Pipe;
import haven.render.RenderTree;

import java.awt.*;

public class NGobHighlight extends GAttrib implements Gob.SetupMod {


    public NGobHighlight(Gob g) {
        super(g);
        start = NUtils.getTickId();
    }

    private static final Color COLOR = new Color(64, 255, 64, 255);
    private static final long cycle = 50;
    private static final long duration = 720;
    private long start = 0;


    public Pipe.Op gobstate() {
        long active = NUtils.getTickId() - start;
        if(active > duration) {
                gob.removeTag(NGob.Tags.highlighted);
                return null;
        } else {
            float k = (float) Math.abs(Math.sin(Math.PI * active / cycle));
            return new MixColor(COLOR.getRed(), COLOR.getGreen(), COLOR.getBlue(), (int) (255 * k));
        }
    }
}