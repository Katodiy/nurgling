package nurgling;

import haven.*;

public class NPopUpWidget extends Widget {
    public static final Tex bg = Resource.loadtex("nurgling/hud/popupwdg/bg");
    public static final Tex bl = Resource.loadtex("nurgling/hud/popupwdg/bl");
    public static final Tex br = Resource.loadtex("nurgling/hud/popupwdg/br");
    public static final Tex hor = Resource.loadtex("nurgling/hud/popupwdg/hor");
    public static final Tex tl = Resource.loadtex("nurgling/hud/popupwdg/tl");
    public static final Tex tr = Resource.loadtex("nurgling/hud/popupwdg/tr");
    public static final Tex ver = Resource.loadtex("nurgling/hud/popupwdg/ver");

    static enum Type
    {
        LEFT,
        TOP,
        BOTTOM,
        RIGHT
    }

    Type type = Type.RIGHT;

    public NPopUpWidget(Coord sz, Type t) {
        super(sz);
        atl = new Coord(bl.sz().x/2 + UI.scale(5),bl.sz().y/2 + UI.scale(5)) ;
        type = t;
        visible = false;
    }

    public Coord atl;
    @Override
    public void draw(GOut g) {
        //bg
        int y_pos = Math.max(0, tl.sz().x / 2);
        int x_pos = Math.max(0, tl.sz().y/ 2);
        for (int x = tl.sz().x / 2; x + bg.sz().x <sz.x- tl.sz().x / 2; x += bg.sz().x) {
            for (int y = tl.sz().y / 2; y + bg.sz().y <sz.y - tl.sz().y / 2; y += bg.sz().y) {
                g.image(bg, new Coord(x, y));
                y_pos = Math.max(y_pos, y + bg.sz().y);
                x_pos = Math.max(x_pos, x + bg.sz().x);
            }
        }
        for (int x = tl.sz().x / 2; x + bg.sz().x <sz.x- tl.sz().x / 2; x += bg.sz().x) {
            g.image(bg, new Coord(x, y_pos), new Coord(bg.sz().x,sz.y - y_pos - tl.sz().y / 2));
            x_pos = Math.max(x_pos, x + bg.sz().x);
        }
        for (int y = tl.sz().y / 2; y + bg.sz().y <sz.y - tl.sz().y / 2; y += bg.sz().y) {
            g.image(bg, new Coord(x_pos, y), new Coord(sz.x - x_pos - tl.sz().x / 2, bg.sz().y));
            y_pos = Math.max(y_pos, y + bg.sz().y);
        }
        if(x_pos <sz.x- tl.sz().x / 2 && y_pos <sz.y - tl.sz().y / 2) {
            g.image(bg, new Coord(x_pos, y_pos), new Coord(sz.x - x_pos - tl.sz().x / 2,sz.y - y_pos - tl.sz().y / 2));
        }
        //lines
        if(type !=Type.TOP)
            for (int x = tl.sz().x; x <sz.x-tl.sz().x; x += hor.sz().x)
                g.image(hor, new Coord(x, 0));

        if(type !=Type.LEFT)
            for (int y = tl.sz().y; y <sz.y - tl.sz().y; y += ver.sz().y)
                g.image(ver, new Coord(0, y));

        if(type !=Type.RIGHT)
            for (int y = tl.sz().y; y <sz.y - tl.sz().y; y += ver.sz().y)
                g.aimage(ver, new Coord(sz.x, y),1,0);

            if(type !=Type.BOTTOM)
            for (int x = tl.sz().x; x <sz.x - tl.sz().x; x += hor.sz().x)
                g.aimage(hor, new Coord(x,sz.y), 0, 1);

        //corners
        switch (type)
        {
            case TOP:
            {
                g.image(ver, new Coord(0, ver.sz().y));
                g.aimage(ver, new Coord(sz.x,ver.sz().y), 1, 0);
                g.aimage(bl, new Coord(0,sz.y), 0, 1);
                g.aimage(br, new Coord(sz.x,sz.y), 1, 1);
                break;
            }
            case BOTTOM:
            {
                g.image(tl, new Coord(0, 0));
                g.aimage(tr, new Coord(sz.x,0), 1, 0);
                g.aimage(ver, new Coord(0,sz.y -  ver.sz().y), 0, 1);
                g.aimage(ver, new Coord(sz.x,sz.y -  ver.sz().y), 1, 1);
                break;
            }
            case LEFT:
            {
                g.image(hor, new Coord(tl.sz().x/2, 0));
                g.aimage(tr, new Coord(sz.x,0), 1, 0);
                g.aimage(br, new Coord(sz.x,sz.y), 1, 1);
                g.aimage(hor, new Coord( tl.sz().x/2,sz.y), 0, 1);
                break;
            }
            case RIGHT:
            {
                g.aimage(hor, new Coord(sz.x-tl.sz().x, 0),0,0);
                g.image(tl, new Coord(0, 0));
                g.aimage(bl, new Coord(0,sz.y), 0, 1);
                g.aimage(hor, new Coord(sz.x - tl.sz().x,sz.y), 0, 1);
                break;
            }
        }
        super.draw(g);
    }



    @Override
    public void resize(Coord sz) {
        super.resize(new Coord(sz.x+ atl.x,sz.y+2*UI.scale(5) + atl.y/2));
    }
}
