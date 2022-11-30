package nurgling;

import haven.Coord;
import haven.UI;
import haven.Widget;


public class NDraggableWidget extends Widget {

    protected final String name;
    private UI.Grab dm;
    private Coord doff;
    protected boolean draggable = true;

    public NDraggableWidget(String name) {
	this.name = name;
    }
    
    public void draggable(boolean draggable) {
	this.draggable = draggable;
	if(!draggable) {stop_dragging();}
    }
    
    private void stop_dragging() {
	if(dm != null) {
	    dm.remove();
	    dm = null;
		NConfiguration.getInstance().dragWidgets.get(name).x = c.x;
		NConfiguration.getInstance().dragWidgets.get(name).y = c.y;
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
	if(dm != null) {
	    this.c = this.c.add(c.add(doff.inv()));
	} else {
	    super.mousemove(c);
	}
    }



}
