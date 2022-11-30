package nurgling;

import haven.Window;
import haven.*;

import java.awt.*;
import java.util.Date;

public class NTimerWdg extends Widget {
    private static final Coord PAD = UI.scale(3, 3);
    private static final Color BG = new Color(8, 8, 8, 128);
    NTimer timer;
    public final haven.Label time;
    public haven.Label name;
    private final haven.Button start, stop, delete, edit;
    
    public NTimerWdg(NTimer timer) {
	super(Coord.z);
	
	this.timer = timer;
	timer.listener = new NTimer.UpdateCallback() {
	    
	    @Override
	    public void update(NTimer timer) {
		synchronized (time) {
		    time.settext(timer.toString());
		    updbtns();
		}
		
	    }
	};
	name = add(new haven.Label(timer.name), PAD);
	time = add(new haven.Label(timer.toString()), PAD.x, UI.scale(25));
	
	start = add(new haven.Button(UI.scale(50), "start", false), UI.scale(90), PAD.y+7);
	stop = add(new haven.Button(UI.scale(50), "stop", false), UI.scale(90), PAD.y+7);
	edit = add(new haven.Button(UI.scale(50), "edit", false), UI.scale(140,  PAD.y+7));
	delete = add(new haven.Button(UI.scale(50), "delete", false), UI.scale(190,  PAD.y+7));

	pack();
	sz = sz.add(PAD);
	updbtns();
    }

	public void update(){
		name.settext(timer.name);
		time.settext(timer.toString());
	}

    @Override
    public Object tooltip(Coord c, Widget prev) {
	if(timer.isWorking()) {
	    if(tooltip == null) {
		tooltip = Text.render(new Date(timer.getFinishDate()).toString()).tex();
	    }
	    return tooltip;
	}
	tooltip = null;
	return null;
    }

    private void updbtns() {
	start.visible = !timer.isWorking();
	stop.visible = timer.isWorking();
	edit.visible = !timer.isWorking();
    }
    
    @Override
    public void destroy() {
	unlink();
	Window wnd = getparent(Window.class);
	if(wnd != null) {
	    wnd.pack();
	}
	timer.listener = null;
	timer = null;
	super.destroy();
    }

    @Override
    public void draw(GOut g) {
	g.chcolor(BG);
	g.frect2(Coord.z, sz);
	g.chcolor();
	super.draw(g);
    }

    @Override
    public void wdgmsg(Widget sender, String msg, Object... args) {
	if(sender == start) {
	    timer.start();
	    updbtns();
	} else if(sender == stop) {
	    timer.stop();
	    updbtns();
	}else if(sender == edit) {
		timer.stop();
		updbtns();
		NTimerPanel.TimerAddWdg wdg = new NTimerPanel.TimerAddWdg((NTimerPanel) this.parent);
		wdg.btnedit.show();
		wdg.edited = this;
		wdg.btnadd.hide();
		wdg.name.settext(timer.name);
		wdg.hours.settext(String.valueOf(timer.duration/1000/3600));
		wdg.minutes.settext(String.valueOf((timer.duration/1000%3600)/60));
		wdg.seconds.settext(String.valueOf((timer.duration/1000%3600)%60));
		NUtils.getGameUI().add(wdg, c);
	}
	else if(sender == delete) {
	    timer.destroy();
	    ui.destroy(this);
	} else {
	    super.wdgmsg(sender, msg, args);
	}
    }


}
