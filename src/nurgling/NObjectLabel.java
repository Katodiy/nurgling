package nurgling;

import haven.*;
import haven.render.Homo3D;
import haven.render.Pipe;
import haven.render.RenderTree;

import java.awt.*;

public class NObjectLabel extends NSprite implements RenderTree.Node, PView.Render2D{
    private static final Text.Foundry font = new Text.Foundry(Text.mono.deriveFont(Font.BOLD, UI.scale(16))).aa(true);
    private final Coord3f pos;
    protected TexI label;
    Color color;
    protected NObjectLabel(Owner owner,     String value, Color color) {
        super(owner, null);
        label = new TexI(font.render(value).img);
        pos = new Coord3f(0,0,5);
        this.color = color;
    }

    @Override
    public void draw(GOut g, Pipe state) {
        Coord sc = Homo3D.obj2view(pos, state, Area.sized(g.sz())).round2();
        g.chcolor(new Color(0, 0, 0, 145));
        g.frect(sc.sub(new Coord(2,0)).sub(new Coord(label.sz().x/2,label.sz().y/2)), label.sz().add(new Coord(4,0)));
        g.chcolor(color);
        g.aimage(label, sc, 0.5, 0.5);
        g.chcolor();
    }
}
