package nurgling;

import haven.*;
import haven.render.Camera;
import haven.render.Homo3D;
import haven.render.Pipe;
import haven.render.RenderTree;

import java.awt.*;
import java.awt.image.BufferedImage;

public class NObjectTexLabel extends NSprite implements RenderTree.Node, PView.Render2D{
    private final Coord3f pos;
    protected TexI label;
    protected TexI img;
    boolean forced = false;
    Color color;
    public static final Font bsans  = new Font("Sans", Font.BOLD, 10);
    public NObjectTexLabel(Owner owner, String value, Color color, String key_icon) {
        super(owner, null);
        img = NUtils.getTexI(key_icon);
        Text.Furnace active_title = new PUtils.BlurFurn(new Text.Foundry(bsans, 15, color).aa(true), 2, 1, new Color(36, 25, 25));
        BufferedImage retlabel = active_title.render(value).img;
        BufferedImage ret = TexI.mkbuf(new Coord(UI.scale(1)+img.sz().x+retlabel.getWidth(), Math.max(img.sz().y,retlabel.getHeight())));
        Graphics g = ret.getGraphics();
        g.drawImage(img.back, 0, ret.getHeight()/2-img.sz().y/2, null);
        g.drawImage(retlabel,UI.scale(1)+img.sz().x,ret.getHeight()/2-retlabel.getHeight()/2,null);
        g.dispose();
        label = new TexI(ret);
        this.color = color;
        pos = new Coord3f(0,0,5);
    }

    public NObjectTexLabel(Owner owner, String value, Color color, String key_icon, boolean forced) {
        this(owner,value,color,key_icon);
        this.forced = forced;
    }

    @Override
    public void draw(GOut g, Pipe state) {
        MapView.Camera cam = NUtils.getGameUI().getMap().camera;
        if(NUtils.getGameUI().getMap().camera instanceof MapView.FreeCam) {
            HomoCoord4f sc3 = Homo3D.obj2clip(pos, state);
            Coord sc = Homo3D.obj2view(pos, state, Area.sized(g.sz())).round2();
            if (sc3.w > 1000 && !forced)
                g.aimage(img, sc, 0.5, 0.5);
            else
            {
                g.aimage(label, sc, 0.5, 0.5);
            }
        }
        else if(NUtils.getGameUI().getMap().camera instanceof MapView.OrthoCam)
        {
            Coord sc = Homo3D.obj2view(pos, state, Area.sized(g.sz())).round2();
            if (((MapView.OrthoCam)cam).field > 400 && !forced)
                g.aimage(img, sc, 0.5, 0.5);
            else
            {
                g.aimage(label, sc, 0.5, 0.5);
            }
        } else if(NUtils.getGameUI().getMap().camera instanceof MapView.SimpleCam)
        {
            Coord sc = Homo3D.obj2view(pos, state, Area.sized(g.sz())).round2();
            if (((MapView.SimpleCam)cam).dist > 600 && !forced)
                g.aimage(img, sc, 0.5, 0.5);
            else
            {
                g.aimage(label, sc, 0.5, 0.5);
            }
        }
        else
        {
            Coord sc = Homo3D.obj2view(pos, state, Area.sized(g.sz())).round2();
            g.aimage(label, sc, 0.5, 0.5);
        }

        g.chcolor();
    }
}
