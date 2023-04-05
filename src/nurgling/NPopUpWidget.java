package nurgling;

import haven.*;

public class NPopUpWidget extends Widget {
    public static final Tex bg = Resource.loadtex("nurgling/hud/popupwdg/bg");
    public static final Tex bl = Resource.loadtex("nurgling/hud/popupwdg/bl");
    public static final Tex bm = Resource.loadtex("nurgling/hud/popupwdg/bm");
    public static final Tex lb = Resource.loadtex("nurgling/hud/popupwdg/lb");
    public static final Tex tl = Resource.loadtex("nurgling/hud/popupwdg/tl");
    public static final Tex tm = Resource.loadtex("nurgling/hud/popupwdg/tm");
    final Coord clo = UI.scale(new Coord(14,15));
    final Coord tms = UI.scale(new Coord(0,43));
    final Coord tme = UI.scale(new Coord(0,8));
    final Coord ls = UI.scale(new Coord(4,0));

    final Coord be = UI.scale(new Coord(0,24));
    public NPopUpWidget(Coord sz) {
        super(sz);
        atl = new Coord(clo.x+ls.x + UI.scale(5),tms.y + UI.scale(5)) ;
        visible = false;
    }

    public Coord atl;
    @Override
    public void draw(GOut g) {

        g.image(tl, new Coord(ls.x,tms.y-clo.y));

        for(int x = tl.sz().x+ls.x; x < g.sz().x; x += tm.sz().x)
            g.image(tm, new Coord(x, 0));
        int y_pos = 0;
        for(int x = clo.x+ls.x; x < g.sz().x; x += bg.sz().x)
            for(int y = tms.y; y+bg.sz().y <  g.sz().y-be.y; y += bg.sz().y) {
                g.image(bg, new Coord(x, y));
                y_pos = Math.max(y_pos,y+bg.sz().y);
            }
        y_pos = Math.max(tms.y, y_pos);
        for(int x = clo.x+ls.x; x < g.sz().x; x += bg.sz().x)
           g.image(bg, new Coord(x, y_pos), new Coord(bg.sz().x,g.sz().y - y_pos - be.y));

        for(int y = tms.y-clo.y+tl.sz().y; y < g.sz().y-tme.y- bl.sz().y; y += lb.sz().y)
            g.image(lb, new Coord(0, y));

        for(int x = bl.sz().x+ls.x; x < g.sz().x; x += bm.sz().x)
            g.aimage(bm, new Coord(x, g.sz().y),0,1);
        g.aimage(bl, new Coord(ls.x,g.sz().y - tme.y),0,1);
        super.draw(g);
    }

    @Override
    public void resize(Coord sz) {
        super.resize(new Coord(sz.x+ ls.x,sz.y+UI.scale(5) + atl.y/2));
        for(Widget ch = child; ch != null; ch = ch.next)
            ch.presize();
    }
}
