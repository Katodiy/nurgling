package nurgling;


import haven.Config;
import haven.KeyBinding;
import haven.Widget;
import nurgling.json.JSONArray;
import nurgling.json.JSONObject;
import nurgling.json.parser.JSONParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class NTimer {
    public static final float SERVER_RATIO = 3.29f;

    private static final Object lock = new Object();
    private static final List<NTimer> timers = load();

    private static long server;
    private static long local;
    private static double delta;
    private static Widget parent = null;

    public interface UpdateCallback {
	void update(NTimer timer);
    }

    long start;
    long duration;
    public String name;
    transient public long remaining;
    transient public UpdateCallback listener;

    private static List<NTimer> load() {
	List<NTimer> timers = null;
	try {
		timers = new LinkedList<>();
		BufferedReader reader = new BufferedReader (
				new InputStreamReader(Files.newInputStream(Paths.get("./timers.json")), "cp1251" ) );
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = ( JSONObject ) parser.parse ( reader );

		JSONArray msg = ( JSONArray ) jsonObject.get ( "timers" );
		if(msg!=null) {
			Iterator<JSONObject> iterator = msg.iterator();
			while (iterator.hasNext()) {
				JSONObject item = iterator.next();
				timers.add(new NTimer((String)item.get("name"), (long)(item.get("start")), (long)item.get("duration")));
			}
		}
	} catch (Exception ignored) {
	}
	return timers == null ? new LinkedList<>() : timers;
    }

    public static void save() {
		JSONObject obj = new JSONObject ();
		JSONArray jtimers = new JSONArray ();
		for ( NTimer timer : timers ) {
			JSONObject timerobj = new JSONObject ();
			timerobj.put ( "name", timer.name );
			timerobj.put ( "start", timer.start );
			timerobj.put ( "duration", timer.duration );
			jtimers.add ( timerobj );
		}
		obj.put("timers",jtimers);
		try ( FileWriter file = new FileWriter ( "./timers.json" ) ) {
			file.write ( obj.toJSONString () );
		}
		catch ( IOException e ) {
			e.printStackTrace ();
		}
    }

    public static void server(long time) {
	server = time;
	local = System.currentTimeMillis();
    }

    public static void tick(double dt) {
	if(parent == null) {return;}
	delta += dt;
	if(delta > 0.2) {
	    updateTimers();
	    delta = 0;
	}
    }

    private static void updateTimers() {
	for (NTimer timer : timers()) {
	    if(timer.isWorking() && timer.update()) {
		timer.stop();
	    }
	}
    }

    public static void start(Widget parent) {
	NTimer.parent = parent;
	updateTimers();
    }

    public static NTimer add(String name, long duration) {
	NTimer t = new NTimer(name, duration);
	synchronized (lock) {
	    timers.add(t);
	    save();
	}
	return t;
    }

    public static List<NTimer> timers() {
	synchronized (lock) {
	    return new ArrayList<>(timers);
	}
    }

    public static int count() {
	synchronized (lock) {
	    return timers.size();
	}
    }

    private static void remove(NTimer t) {
	synchronized (lock) {
	    timers.remove(t);
	    save();
	}
    }

    private NTimer(String name, long duration) {
	this.name = name;
	this.duration = duration;
    }

	private NTimer(String name, long start, long duration) {
		this.name = name;
		this.start = start;
		this.duration = duration;
	}


	public boolean isWorking() {
	return start != 0;
    }
    
    public void stop() {
	start = 0;
	if(listener != null) {
	    listener.update(this);
	}
	save();
    }
    
    public void start() {
	start = (long) (server + SERVER_RATIO * (System.currentTimeMillis() - local));
	save();
    }
    
    public synchronized boolean update() {
	long now = System.currentTimeMillis();
	remaining = (long) (duration - now + local - (server - start) / SERVER_RATIO);
	if(remaining <= 0) {
	    if(parent != null) {NTimerPanel.complete(this, parent);}
	    return true;
	}
	if(listener != null) {
	    listener.update(this);
	}
	return false;
    }
    
    public synchronized long getStart() {
	return start;
    }
    
    public synchronized void setStart(long start) {
	this.start = start;
    }
    
    public synchronized long getFinishDate() {
	return (long) (duration + local - (server - start) / SERVER_RATIO);
    }
    
    @Override
    public String toString() {
	long t = Math.abs(isWorking() ? remaining : duration) / 1000;
	int h = (int) (t / 3600);
	int m = (int) ((t % 3600) / 60);
	int s = (int) (t % 60);
	if(h >= 24) {
	    int d = h / 24;
	    h = h % 24;
	    return String.format("%d:%02d:%02d:%02d", d, h, m, s);
	} else {
	    return String.format("%d:%02d:%02d", h, m, s);
	}
    }
    
    public void destroy() {
	NTimer.remove(this);
	listener = null;
    }
    
}