/* Preprocessed source code */
package nurgling;

import haven.*;
import haven.render.Homo3D;
import haven.render.Pipe;
import haven.render.RenderTree;

import java.awt.*;

public class NQuesterRing extends NTargetRing implements PView.Render2D {
	Tex img;

	final NQuestInfo.Quester quester;
	public NQuesterRing(Owner owner, Color color, float range, float alpha, NQuestInfo.Quester quester) {
		super(owner, color, range, alpha);
		this.img = RichText.renderstroked(quester.name,Color.white).tex();
		this.quester = quester;
		this.quester.isFound = true;
	}

	@Override
	public void removed(RenderTree.Slot slot) {
		quester.isFound = false;
		super.removed(slot);
	}

	@Override
	public void draw(GOut g, Pipe state) {
		if(img!=null) {
			Coord sc = Homo3D.obj2view(new Coord3f(0,0,((Gob)owner).isTag( NGob.Tags.mounted)?35:25), state, Area.sized(g.sz())).round2();
			g.aimage(img, sc, 0.5, 0.5);
		}
	}
}