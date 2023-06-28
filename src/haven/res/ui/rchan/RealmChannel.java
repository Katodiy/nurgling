/* Preprocessed source code */
package haven.res.ui.rchan;

import haven.*;
import nurgling.NUtils;

import java.util.*;
import java.awt.Color;
import java.awt.font.TextAttribute;

/* >wdg: RealmChannel */
@haven.FromResource(name = "ui/rchan", version = 19)
public class RealmChannel extends ChatUI.MultiChat {
    private final Map<Integer, String> pnames = new HashMap<>();

    public RealmChannel(String name) {
	super(true, name, 0);
    }

    public static Widget mkwidget(UI ui, Object[] args) {
	String name = (String)args[0];
	return(new RealmChannel(name));
    }

    public class PNamedMessage extends Message {
	public final int from;
	public final String text;
	public final int w;
	public final Color col;
	private String cn;
	private Text r = null;

	public PNamedMessage(int from, String text, Color col, int w) {
	    this.from = from;
	    this.text = text;
	    this.w = w;
	    this.col = col;
	}

	private double lnmck = 0;
	public Text text() {
	    double now = Utils.rtime();
	    if((r == null) || (now - lnmck > 1)) {
		BuddyWnd.Buddy b = getparent(GameUI.class).buddies.find(from);
		String nm = null;
		if((nm == null) && (b != null))
		    nm = b.name;
		if((nm == null) && pnames.containsKey(Integer.valueOf(from)))
		    nm = pnames.get(Integer.valueOf(from));
		if(nm == null)
		    nm = "???";
		if((r == null) || !nm.equals(cn)) {
		    r = ChatUI.fnd.render(RichText.Parser.quote(String.format("[%s] %s: %s", NUtils.timestamp(), nm, text)), w, TextAttribute.FOREGROUND, col);
		    cn = nm;
		}
	    }
	    return(r);
	}

	public Tex tex() {
	    return(text().tex());
	}

	public Coord sz() {
	    if(r == null)
		return(text().sz());
	    else
		return(r.sz());
	}
    }

    public PNamedMessage msgbyname(String nm) {
	for(ListIterator<Message> i = msgs.listIterator(msgs.size()); i.hasPrevious();) {
	    Message cur = i.previous();
	    if(cur instanceof PNamedMessage) {
		PNamedMessage msg = (PNamedMessage)cur;
		if((msg.cn != null) && msg.cn.equals(nm))
		    return(msg);
	    }
	}
	return(null);
    }

    public static class UserError extends Exception {
	public UserError(String fmt, Object... args) {
	    super(String.format(fmt, args));
	}
    }

    public static int parsedur(String d) {
	int m;
	switch(d.substring(d.length() - 1)) {
	case "s":
	    m = 1; break;
	case "m":
	    m = 60; break;
	case "h":
	    m = 3600; break;
	case "d":
	    m = 86400; break;
	case "w":
	    m = 604800; break;
	case "M":
	    m = 2419200; break;
	default:
	    m = 0;
	}
	if(m == 0) {
	    m = 1;
	} else {
	    d = d.substring(0, d.length() - 1);
	}
	return(Integer.parseInt(d) * m);
    }

    public static final Color wcol = new Color(255, 128, 0);
    public void cmd(String[] argv) {
	try {
	    switch(argv[0]) {
	    case "rename": {
		if(argv.length < 3)
		    throw(new UserError("usage: rename NAME NEW-NAME"));
		PNamedMessage msg = msgbyname(argv[1]);
		if(msg == null)
		    throw(new UserError("%s: no such name", argv[1]));
		pnames.put(msg.from, argv[2]);
		break;
	    }
	    case "ban": {
		if(argv.length < 2)
		    throw(new UserError("usage: ban NAME [DURATION]"));
		PNamedMessage msg = msgbyname(argv[1]);
		if(msg == null)
		    throw(new UserError("%s: no such name", argv[1]));
		int dur = 3600;
		if(argv.length > 2)
		    dur = parsedur(argv[2]);
		wdgmsg("ban", msg.from, dur);
		break;
	    }
	    case "unban": {
		if(argv.length < 2)
		    throw(new UserError("usage: unban NAME"));
		PNamedMessage msg = msgbyname(argv[1]);
		if(msg == null)
		    throw(new UserError("%s: no such name", argv[1]));
		wdgmsg("unban", msg.from);
		break;
	    }
	    default:
		throw(new UserError("%s: no such command", argv[0]));
	    }
	} catch(UserError e) {
	    append(e.getMessage(), wcol);
	} catch(Exception e) {
	    append(String.format("Error: %s", e), Color.RED);
	}
    }

    public void send(String msg) {
	if((msg.length() > 0) && (msg.charAt(0) == '/')) {
	    String[] argv = Utils.splitwords(msg.substring(1));
	    if((argv != null) && (argv.length > 0))
		cmd(argv);
	} else {
	    super.send(msg);
	}
    }

    public void uimsg(String msg, Object... args) {
	if(msg == "msg") {
	    Integer from = (Integer)args[0];
	    String line = (String)args[1];
	    String pname = null;
	    if(args.length > 2)
		pname = (String)args[2];
	    if(from == null) {
		append(new MyMessage(line, iw()));
	    } else {
		if((pname != null) && !pnames.containsKey(from))
		    pnames.put(from, pname);
		Message cmsg = new PNamedMessage(from, line, fromcolor(from), iw());
		append(cmsg);
		if(urgency > 0)
		    notify(cmsg, urgency);
	    }
	} else if(msg == "err") {
	    String err = (String)args[0];
	    Message cmsg = new SimpleMessage(err, wcol, iw());
	    append(cmsg);
	    notify(cmsg, 3);
	} else if(msg == "enter") {
	} else if(msg == "leave") {
	} else {
	    super.uimsg(msg, args);
	}
    }

    private static final Indir<Resource> icon = Resource.classres(RealmChannel.class).pool.load("gfx/hud/chat/rlm-p", 1);
    public Resource.Image icon() {
	return(icon.get().layer(Resource.imgc));
    }
}
