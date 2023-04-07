package nurgling;

import haven.GAttrib;
import haven.Gob;
import haven.render.MixColor;
import haven.render.Pipe;

import java.awt.*;

public class NGobColor extends GAttrib implements Gob.SetupMod {
	private final MixColor color;

	public NGobColor(Gob g, Color color) {
		super(g);
		this.color = new MixColor(color);
	}

	public Pipe.Op placestate() {
		if(disabled)
			return color;
		else
			return null;
	}


	public boolean disabled = true;
}
