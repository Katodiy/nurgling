package nurgling;

import haven.*;


public class NDraggableWidget extends Widget {

	protected boolean isVisible = true;
    protected final String name;
    private UI.Grab dm;
    private Coord doff;
    protected boolean draggable = true;
	protected final NToggleButton btnLock;
	boolean locked = false;

	boolean over = false;
    public NDraggableWidget(String name) {
		this.name = name;
		locked = NConfiguration.getInstance().dragWidgets.get(name).locked;
		btnLock = add(new NToggleButton("hud/btn-ulock", "", "-d", "-h", "hud/btn-lock", "", "-d", "-h"), new Coord(sz.x, UI.scale(5)));
		btnLock.action(()->{locked=!locked;draggable=!locked;NConfiguration.getInstance().dragWidgets.get(name).locked = locked;});
		btnLock.recthit = true;
		btnLock.state(locked);
		draggable = !locked;
    }
    
    public void draggable(boolean draggable) {
	this.draggable = draggable;
	if(!draggable) {stop_dragging();}
    }
    
    private void stop_dragging() {
	if(dm != null) {
	    dm.remove();
	    dm = null;
		NConfiguration.getInstance().dragWidgets.get(name).coord.x = c.x;
		NConfiguration.getInstance().dragWidgets.get(name).coord.y = c.y;
	}
    }
    
    @Override
    public boolean mousedown(Coord c, int button) {
	if(super.mousedown(c, button)) {
		if(c.isect(Coord.z, sz) && draggable && ui.mousegrab.isEmpty()) {
			if (button == 1) {
				dm = ui.grabmouse(this);
				doff = c;
			}
		}
	    return true;
	}
	if(c.isect(Coord.z, sz) && draggable) {
	    if(button == 1) {
		dm = ui.grabmouse(this);
		doff = c;
	    }
	    parent.setfocus(this);
	    return true;
	}
	return false;
    }
    
    @Override
    public boolean mouseup(Coord c, int button) {
	if(dm != null) {
	    stop_dragging();
	} else {
	    super.mouseup(c, button);
	}
	return (true);
    }
    
    @Override
    public void mousemove(Coord c) {
	over = c.isect(Coord.z, sz);
	if(dm != null) {
	    this.c = this.c.add(c.add(doff.inv()));
	} else {
	    super.mousemove(c);
	}
    }

	@Override
	public void draw(GOut g) {
		if(isVisible) {
			Tex bg = Window.bg;
			Coord bgc = new Coord();
			Coord cbr = sz;
			Coord ctl = Coord.z;
			for (bgc.y = ctl.y; bgc.y < cbr.y; bgc.y += bg.sz().y) {
				for (bgc.x = ctl.x; bgc.x < cbr.x; bgc.x += bg.sz().x)
					g.image(bg, bgc, ctl, cbr);
			}
		}
		btnLock.visible = over;
		super.draw(g);
	}

	@Override
	public void resize(Coord sz) {
		btnLock.move(new Coord(sz.x-btnLock.sz.x, btnLock.sz.y));
		super.resize(sz);
	}
}
