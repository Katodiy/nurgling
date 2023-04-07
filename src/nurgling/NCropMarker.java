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
	Coord3f pos = new Coord3f(0,0,0);

	NProperties.Crop crop;
	public NCropMarker(Gob gob, NProperties.Crop crop) {
		super(gob, null);
		this.crop = crop;
		updateMarker();
	}

	void updateMarker() {
		if (NConfiguration.getInstance().showCropStage) {
			Gob gob = (Gob) owner;
			if (gob.modelAttribute == crop.maxstage) {
				if (crop.maxstage == 0) {
					img = NUtils.getCropTexI(Color.GRAY);
				} else {
					img = NUtils.getCropTexI(Color.GREEN);
				}
			} else if (gob.modelAttribute == 0) {
				img = NUtils.getCropTexI(Color.RED);
			} else {
				if (crop.maxstage > 1 && crop.maxstage < 7) {
					if (gob.modelAttribute == crop.specstage) {
						img = NUtils.getCropTexI(Color.BLUE);
					} else {
						img = NUtils.getCropTexI((int) gob.modelAttribute, (int) crop.maxstage);
					}
				}
			}
		}
	}

	@Override
	public void gtick(Render g) {
		super.gtick(g);
	}


	@Override
	public boolean tick(double dt) {
		if (((Gob) owner).getModelAttribute() != crop.currentStage) {
			updateMarker();
			crop.currentStage = ((Gob) owner).getModelAttribute();
		}
		return super.tick(dt);
	}

	@Override
	public void draw(GOut g, Pipe state) {
		if (NConfiguration.getInstance().showCropStage && img!=null) {
			Coord sc = Homo3D.obj2view(pos, state, Area.sized(g.sz())).round2();
			g.aimage(img, sc, 0.5, 0.5);
		}
	}
}