package nurgling;


import haven.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static haven.MCache.cmaps;
import static haven.MCache.tilesz;
import static haven.OCache.posres;

public class NPathQueue {
    private static final Coord2d gsz = tilesz.mul(cmaps.x, cmaps.y);
    private final List<Coord2d> queue = new LinkedList<>();
    private final MapView map;
    private Moving moving;
    private boolean clicked = false;
    private Coord2d clickPos = null;
    private boolean passenger = false;

    public NPathQueue(MapView map) {
	this.map = map;
    }

	public ArrayList<Coord2d> getPath(){
		ArrayList<Coord2d> copy;
		synchronized (queue) {
			copy = new ArrayList<>(queue);
		}
		return copy;
	}

    public boolean add(Coord2d p) {
	boolean start = false;
	synchronized (queue) {
	    if(passenger) {return false;}
	    if(queue.isEmpty()) { start = true; }
	    queue.add(p);
	    unclick();
	}

	return start;
    }

	public int size(){
		return queue.size();
	}
    public void start(Coord2d p) {
	synchronized (queue) {
	    if(passenger) {return;}
	    queue.clear();
	    queue.add(p);
	    unclick();
	}
    }

    public void click(Coord2d mc, ClickData inf) {
	if(inf != null) {
	    click(NGob.from(inf.ci));
	} else {
	    click(mc);
	}
    }


    public void click(Gob gob) {
	click(gob != null ? gob.rc : null);
    }

    public void click(Coord2d pos) {
	clicked = true;
	this.clickPos = pos;
    }

    public void click() {
	clicked = true;
	clickPos = null;
    }

    public void unclick() {
	clicked = false;
	clickPos = null;
    }

    public List<Pair<Coord3f, Coord3f>> lines() {
	LinkedList<Coord2d> tmp;
	synchronized (queue) {
	    tmp = new LinkedList<>(queue);
	}

	List<Pair<Coord3f, Coord3f>> lines = new LinkedList<>();
	if(!tmp.isEmpty()) {
	    try {
		Gob player = map.player();
		if(player != null) {
		    Coord2d pc = player.rc;
		    Coord pgrid = pc.floor(gsz);
		    float z = 0;
		    Coord3f current = new Coord3f((float) pc.x, (float) pc.y, 0);
		    try {
			current = moving == null ? player.getrc() : moving.gett();
			z = current.z;
		    } catch (Loading ignored) {}
		    for (Coord2d p : tmp) {
			Coord3f next = new Coord3f((float) p.x, (float) p.y, z);
			if(pgrid.manhattan2(p.floor(gsz)) <= 1) {
			    try {
				next = map.glob.map.getzp(p);
			    } catch (Loading ignored) {}
			}
			lines.add(new Pair<>(current, next));
			current = next;
		    }
		}
	    } catch (Loading ignored) {}
	}
	return lines;
    }

    public List<Pair<Coord2d, Coord2d>> minimapLines() {
	LinkedList<Coord2d> tmp;
	synchronized (queue) {
	    tmp = new LinkedList<>(queue);
	}

	List<Pair<Coord2d, Coord2d>> lines = new LinkedList<>();
	if(!tmp.isEmpty()) {
	    Gob player = map.player();
	    if(player != null) {
		Coord2d current = player.rc;
		for (Coord2d p : tmp) {
		    lines.add(new Pair<>(current, p));
		    current = p;
		}
	    }
	}
	return lines;
    }

    private Coord2d pop() {
	synchronized (queue) {
	    if(queue.isEmpty()) { return null; }
	    queue.remove(0);
	    return queue.isEmpty() ? null : queue.get(0);
	}
    }



    public void movementChange(Gob gob, GAttrib from, GAttrib to) {
	if(gob.isTag(NGob.Tags.player) || (map!= null && map.player()!=null && map.player().isTag(NGob.Tags.mounted) && gob.id == NUtils.getGameUI().drives)) {
		synchronized (queue) {
			checkPassenger((Moving) to);
		}
		moving = (Moving) to;
		synchronized (queue) {
			if (to == null) {
				Coord2d next = pop();
				if (next != null) {
					unclick();
					if (!NUtils.getGameUI().nomadMod)
						map.wdgmsg("click", Coord.z, next.floor(posres), 1, 0);
				}
			} else if (to instanceof Homing || to instanceof Following) {
				clear();
			} else if (clicked) {
				if (this.clickPos != null) {
					start(this.clickPos);
				} else {
					clear();
				}
				unclick();
			}
		}

	}
    }

    private void checkPassenger(Moving moving) {
	boolean passenger = false;
	if(moving instanceof Following) {
	    Following follow = (Following) moving;
	    Gob vehicle = follow.tgt();
	    if(vehicle != null) {
		String pos = follow.xfname;
		if(NUtils.isIt(vehicle,"/vehicle/snekkja")) {
		    passenger = !pos.equals("m0");
		} else if(NUtils.isIt(vehicle,"/vehicle/knarr")) {
		    passenger = !pos.equals("m0"); //TODO: check if knarr works properly
		} else if(NUtils.isIt(vehicle,"/vehicle/rowboat")) {
		    passenger = !pos.equals("d");
		} else if(NUtils.isIt(vehicle,"/vehicle/spark")) {
		    passenger = !pos.equals("d");
		} else if(NUtils.isIt(vehicle,"/vehicle/wagon")) {
		    passenger = !pos.equals("d0");
		}
	    }
	}
	this.passenger = passenger;
	if(passenger) {
	    clear();
	}
    }

    public void clear() {
		synchronized (queue) {queue.clear();}
    }

}
