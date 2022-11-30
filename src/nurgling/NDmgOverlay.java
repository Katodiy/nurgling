/* Preprocessed source code */
package nurgling;

import haven.*;
import haven.render.Homo3D;
import haven.render.Pipe;

import java.awt.*;

public class NDmgOverlay extends Sprite implements PView.Render2D {
	public static final Text.Foundry fnd = new Text.Foundry(Text.sans, 12);
	Color[] colt = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
	Tex[] dmgt = new Tex[3];
	int[] dmg = new int[3];

	public NDmgOverlay(Owner owner, Resource res) {
		super(owner, res);
	}

	public void updDmg(int dmg, int type) {
		this.dmg[type] += dmg;
		dmgt[type] = new TexI(Utils.outline2(fnd.render(Integer.toString(this.dmg[type]), colt[type]).img, Utils.contrast(colt[type])));
	}

	public void draw(GOut g, Pipe state) {
		Coord sc = Homo3D.obj2view(Coord3f.zu.add(0,0, 16), state, Area.sized(Coord.z, g.sz())).round2();
		if(sc == null)
			return;
		Coord c = Coord.z;
		for(int i=0; i<3; i++) {
			if(dmgt[i] != null) {
				if(c.x > 0)
					c.x += 2;
				c = c.add(dmgt[i].sz().x, 0);
			}
		}
		c = c.div(-2);
		g.chcolor(new Color(0, 0, 0, 64));
		g.frect2(sc.add(c).sub(1,0), sc.add(c.inv()).add(1,16));
		g.chcolor();
		for(int i=0; i<3; i++) {
			if(dmgt[i] == null)
				continue;
			g.image(dmgt[i], sc.add(c));
			sc.x += dmgt[i].sz().x + 2;
		}
		g.chcolor();
	}

	@Override
	public boolean tick(double dt) {
		if(((Gob)owner).isTag(NGob.Tags.knocked))
			return true;
		return super.tick(dt);
	}
}
