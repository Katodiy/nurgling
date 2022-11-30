/* Preprocessed source code */
package nurgling;

import haven.*;
import haven.render.*;
import haven.render.sl.ShaderMacro;

import java.awt.*;
import java.util.HashMap;


/* >spr: BPRad */
public class NCropMarker extends NSprite implements RenderTree.Node, PView.Render2D  {

	final int z = 0;

	TexI img;
	Coord3f pos;

	public long cur;

	public NCropMarker(Owner owner, Color color){
		super(owner, null);
		img = NUtils.getCropTexI(color);
		pos = new Coord3f(0,0,0);
	}

	public NCropMarker(Owner owner, long cur, long max){
		super(owner, null);
		img = NUtils.getCropTexI((int)cur,(int)max);
		pos = new Coord3f(0,0,0);
		this.cur = cur;
	}


	@Override
	public void gtick(Render g) {

		super.gtick(g);
	}


	@Override
	public void draw(GOut g, Pipe state) {

		double dist = NUtils.getGameUI().map.camera.view.pos.dist(((Gob)owner).getrc());

		Coord sc = Homo3D.obj2view(pos, state, Area.sized(g.sz())).round2();

		g.aimage(img, sc, 0.5, 0.5);

	}
}