package nurgling;

import haven.*;

public class NResizedWidget extends NDraggableWidget{
    public static final Tex sizer = Resource.loadtex("hud/wnd/sizer");
    public Coord minSize = new Coord(350,240);
    public NResizedWidget(String name) {
        super(name);
        locked = NConfiguration.getInstance().resizeWidgets.get(name).locked;
        btnLock = add(new NToggleButton("hud/btn-ulock", "", "-d", "-h", "hud/btn-lock", "", "-d", "-h"), new Coord(sz.x, UI.scale(5)));
        btnLock.action(()->{locked=!locked;draggable=!locked;NConfiguration.getInstance().resizeWidgets.get(name).locked = locked;});
        btnLock.recthit = true;
        btnLock.state(locked);
        draggable = !locked;
    }
    private UI.Grab drag;
    private Coord dragc;
    private final NToggleButton btnLock;
    boolean over = false;
    boolean locked = false;


    public boolean mousedown(Coord c, int button) {
        if (!locked) {
            int d = (c.x - (sz.x - UI.scale(12))) * (-UI.scale(12)) - (c.y - sz.y) * (UI.scale(12));
            if ((button == 1) && d <= 0) {
                if (drag == null) {
                    drag = ui.grabmouse(this);
                    dragc = sz.sub(c);
                    return (true);
                }
            }
        }
        return (super.mousedown(c, button));
    }

    public void mousemove(Coord c) {
        over = c.isect(Coord.z, sz);
        if(drag != null) {
            Coord nsz = c.add(dragc);
            nsz.x = Math.max(nsz.x, UI.scale(minSize.x));
            nsz.y = Math.max(nsz.y, UI.scale(minSize.y));
            resize(nsz);
            NConfiguration.getInstance().resizeWidgets.get(name).coord = nsz;
        }
        super.mousemove(c);
    }

    public boolean mouseup(Coord c, int button) {
        if((button == 1) && (drag != null)) {
            drag.remove();
            drag = null;
            return(true);
        }
        return(super.mouseup(c, button));
    }

    @Override
    public void draw(GOut g) {
        if(over || drag!=null) {
            btnLock.visible = true;
            if(!locked)
            g.image(sizer, sz.sub(sizer.sz()));
        }else{
            btnLock.visible = false;
        }
        super.draw(g);
    }

    @Override
    public void resize(Coord sz) {
        btnLock.move(new Coord(sz.x-btnLock.sz.x, btnLock.sz.y));
        super.resize(sz);
    }
}
