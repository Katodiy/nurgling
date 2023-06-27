/* Preprocessed source code */
package nurgling;

import haven.*;
import haven.render.Homo3D;
import haven.render.Pipe;
import haven.render.Render;
import haven.render.RenderTree;

import java.awt.*;
import java.awt.image.BufferedImage;


/* >spr: BPRad */
public class NGardenPotMarker extends NSprite implements RenderTree.Node, PView.Render2D  {

	Gob pot;
	Coord3f pos = new Coord3f(0,0,2);

	public NGardenPotMarker(Gob gob) {
		super(gob, null);
		this.pot = gob;
		check();
	}

	enum Stage{
		NOSEED,
		NOWATER,
		NOSOIL,
		NOSW,
		INWORK,
		READY,
	}

	Stage stage = Stage.NOSEED;

	@Override
	public void gtick(Render g) {
		super.gtick(g);
	}

	void check() {
		int count = 0;
		for (Gob.Overlay ol : pot.ols) {
			if (ol.res != null)
				if (ol.res.get().name.contains("gfx/fx/eq")) {
					count++;
				}
		}
		if (count == 2)
			stage = Stage.READY;
		else if (count == 1)
			stage = Stage.INWORK;
		else if (pot.getModelAttribute() == 3)
			stage = Stage.NOSEED;
		else if (pot.getModelAttribute() == 1)
			stage = Stage.NOSOIL;
		else if (pot.getModelAttribute() == 2)
			stage = Stage.NOWATER;
		else
			stage = Stage.NOSW;
	}

	@Override
	public boolean tick(double dt) {
		check();
		return super.tick(dt);
	}

	@Override
	public void draw(GOut g, Pipe state) {
		if (NConfiguration.getInstance().showCropStage) {
			Coord sc = Homo3D.obj2view(pos, state, Area.sized(g.sz())).round2();
			switch (stage)
			{
				case NOSEED:
					g.aimage(NUtils.getTexI("ggray"), sc, 0.5, 0.5);
					break;
				case INWORK:
					g.aimage(NUtils.getTexI("gred"), sc, 0.5, 0.5);
					break;
				case READY:
					g.aimage(NUtils.getTexI("ggreen"), sc, 0.5, 0.5);
					break;
				case NOSOIL:
					g.aimage(NUtils.getTexI("gbrown"), sc, 0.5, 0.5);
					break;
				case NOWATER:
					g.aimage(NUtils.getTexI("gblue"), sc, 0.5, 0.5);
					break;
				case NOSW:
					g.aimage(NUtils.getTexI("gbluebrown"), sc, 0.5, 0.5);
					break;
			}

		}
	}
}