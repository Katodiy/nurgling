/* Preprocessed source code */
package nurgling;

import haven.*;
import haven.render.Homo3D;
import haven.render.Pipe;
import haven.render.RenderTree;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class NQuesterRing extends NTargetRing implements PView.Render2D {
	Tex img;

	String name;

	Gob gob;
	final NQuestInfo.QuestGob quester;
	final Set<NGob.Tags> allTags;
	Set<NGob.Tags> tagsSet = null;

	static Tex qrage = Resource.loadtex("icon/qrage");
	static Tex qlol = Resource.loadtex("icon/qlol");
	static Tex qbring = Resource.loadtex("icon/qbring");
	static Tex qwave = Resource.loadtex("icon/qwave");
	static Tex qgreet = Resource.loadtex("icon/qgreet");
	static Tex qcompleted = Resource.loadtex("icon/qcompleted");

	public NQuesterRing(Owner owner, Color color, float range, float alpha, NQuestInfo.QuestGob quester) {
		super(owner, color, range, alpha);
		this.img = RichText.renderstroked(quester.name,Color.white).tex();
		this.quester = quester;
		this.quester.isFound = true;
		this.gob = (Gob) owner;
		this.name = quester.name;
		this.tagsSet = NQuestInfo.tagsSet(name);
		allTags = new HashSet<NGob.Tags>();
		allTags.add(NGob.Tags.qgreet);
		allTags.add(NGob.Tags.qrage);
		allTags.add(NGob.Tags.qwave);
		allTags.add(NGob.Tags.qlaugh);
		allTags.add(NGob.Tags.qbring);
		allTags.add(NGob.Tags.qcompleted);
	}

	@Override
	public void removed(RenderTree.Slot slot) {
		quester.isFound = false;
		super.removed(slot);
	}

	@Override
	public boolean tick(double dt) {
		Set<NGob.Tags> remote = NQuestInfo.tagsSet(name);

		if(remote!=null) {
			for (NGob.Tags tag : allTags)
			{
				if(!remote.contains(tag))
					gob.removeTag(tag);
			}

			for (NGob.Tags tag : remote)
			{
				gob.addTag(tag);
			}
		}
		tagsSet = remote;
		return super.tick(dt);
	}

	@Override
	public void draw(GOut g, Pipe state) {

		if(img!=null) {
			Coord sc = Homo3D.obj2view(new Coord3f(0, 0, ((Gob) owner).isTag(NGob.Tags.mounted) ? 35 : 25), state, Area.sized(g.sz())).round2();
			g.aimage(img, sc, 0.5, 0.5);
			if (tagsSet != null) {
				double y = -(tagsSet.size()) / 2. + 1;
				double dy = 1;
				if (gob.isTag(NGob.Tags.qrage)) {

					Coord coord = Homo3D.obj2view(new Coord3f(0, 0, UI.scale(28) + NUtils.getDeltaZ()), state, Area.sized(g.sz())).round2();
					g.aimage(qrage, coord, y, 0.5);
					y += dy;
				}
				if (gob.isTag(NGob.Tags.qlaugh)) {
					Coord coord = Homo3D.obj2view(new Coord3f(0, 0, UI.scale(28) + NUtils.getDeltaZ()), state, Area.sized(g.sz())).round2();
					g.aimage(qlol, coord, y, 0.5);
					y += dy;
				}
				if (gob.isTag(NGob.Tags.qwave)) {
					Coord coord = Homo3D.obj2view(new Coord3f(0, 0, UI.scale(28) + NUtils.getDeltaZ()), state, Area.sized(g.sz())).round2();
					g.aimage(qwave, coord, y, 0.5);
					y += dy;
				}
				if (gob.isTag(NGob.Tags.qbring)) {
					Coord coord = Homo3D.obj2view(new Coord3f(0, 0, UI.scale(28) + NUtils.getDeltaZ()), state, Area.sized(g.sz())).round2();
					g.aimage(qbring, coord, y, 0.5);
					y += dy;
				}
				if (gob.isTag(NGob.Tags.qgreet)) {
					Coord coord = Homo3D.obj2view(new Coord3f(0, 0, UI.scale(28) + NUtils.getDeltaZ()), state, Area.sized(g.sz())).round2();
					g.aimage(qgreet, coord, y, 0.5);
					y += dy;
				}
				if (gob.isTag(NGob.Tags.qcompleted)) {
					Coord coord = Homo3D.obj2view(new Coord3f(0, 0, UI.scale(28) + NUtils.getDeltaZ()), state, Area.sized(g.sz())).round2();
					g.aimage(qcompleted, coord, y, 0.5);
				}
			}
		}
	}
}