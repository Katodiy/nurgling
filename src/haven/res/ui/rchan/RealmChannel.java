/* Preprocessed source code */
package haven.res.ui.rchan;

import haven.*;
import nurgling.NUtils;

import java.util.*;
import java.awt.Color;
import java.awt.font.TextAttribute;

/* >wdg: RealmChannel */
@haven.FromResource(name = "ui/rchan", version = 21)
public class RealmChannel extends ChatUI.MultiChat {
    private final Map<Long, Sender> senders = new HashMap<>();

    public RealmChannel(String name) {
	super(true, name, 0);
    }

    public static Widget mkwidget(UI ui, Object[] args) {
	String name = (String)args[0];
	return(new RealmChannel(name));
    }

    public class Sender {
	public final long cid;
	public final int bid;
	public final Color color;
	public String name;
	double lastseen, muted;

	public Sender(long cid, int bid) {
	    this.cid = cid;
	    this.bid = bid;
	    this.color = fromcolor(bid);
	    name = Utils.getpref(String.format("chat/name-%x", cid), null);
	    muted = Utils.getprefd(String.format("chat/muted-%x", cid), 0);
	    lastseen = Utils.getprefd(String.format("hcat/lastseen-%x", cid), Utils.ntime());
	}

	public void setname(String name) {
	    Utils.setpref(String.format("chat/name-%x", cid), this.name = name);
	}

	public void mute(double muted) {
	    Utils.setprefd(String.format("chat/muted-%x", cid), this.muted = muted);
	}
    }

    public class PNamedMessage extends Message {
	public final Sender from;
	public final String text;
	public final int w;
	private String cn;
	private Text r = null;

	public PNamedMessage(Sender from, String text, int w) {
	    this.from = from;
	    this.text = text;
	    this.w = w;
	}

	public class Rendered implements Indir<Text> {
	    public final int w;
	    public final String nm;

	    public Rendered(int w, String nm) {
		this.w = w;
		this.nm = nm;
	    }

	    public Text get() {
		return(ChatUI.fnd.render(RichText.Parser.quote(String.format("%s: %s", nm, text)), w, TextAttribute.FOREGROUND, from.color));
	    }
	}

	private String nm() {
	    BuddyWnd.Buddy b = getparent(GameUI.class).buddies.find(from.bid);
	    if(b != null)
		return(b.name);
	    if(from.name != null)
		return(from.name);
	    return("???");
	}

	public Indir<Text> render(int w) {
	    /* Saving cn right now is just for name lookup, but that
	     * should go throught rmsgs instead. */
	    return(new Rendered(w, cn = nm()));
	}

	public boolean valid(Indir<Text> data) {
	    return(((Rendered)data).nm.equals(nm()));
	}

	/* Remove me */
	private double lnmck = 0;
	public Text text() {
	    double now = Utils.rtime();
	    if((r == null) || (now - lnmck > 1)) {
		BuddyWnd.Buddy b = getparent(GameUI.class).buddies.find(from.bid);
		String nm = nm();
		if((r == null) || !nm.equals(cn)) {
			r = ChatUI.fnd.render(RichText.Parser.quote(String.format("[%s] %s: %s", NUtils.timestamp(), nm, text)), w, TextAttribute.FOREGROUND, from.color);
		    cn = nm;
		}
	    }
	    return(r);
	}

	/* Remove me */
	public Tex tex() {
	    return(text().tex());
	}

	/* Remove me */
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
		if((msg.cn != null) && (nm.equals("") || msg.cn.equals(nm)))
		    return(msg);
	    }
	}
	return(null);
    }

    public Sender frombyname(String nm) {
	PNamedMessage msg = msgbyname(nm);
	return((msg == null) ? null : msg.from);
    }

    public Sender frombyid(long cid, int bid) {
	synchronized(senders) {
	    Sender ret = senders.get(cid);
	    if(ret == null)
		senders.put(cid, ret = new Sender(cid, bid));
	    return(ret);
	}
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
		Sender from = frombyname(argv[1]);
		if(from == null)
		    throw(new UserError("%s: no such name", argv[1]));
		from.setname(argv[2]);
		break;
	    }
	    case "mute": {
		if(argv.length < 2)
		    throw(new UserError("usage: mute NAME [DURATION]"));
		Sender from = frombyname(argv[1]);
		if(from == null)
		    throw(new UserError("%s: no such name", argv[1]));
		int dur = 86400;
		if(argv.length > 2)
		    dur = parsedur(argv[2]);
		from.mute(Utils.ntime() + dur);
		break;
	    }
	    case "unmute": {
		if(argv.length < 2)
		    throw(new UserError("usage: unmute NAME"));
		Sender from = frombyname(argv[1]);
		if(from == null)
		    throw(new UserError("%s: no such name", argv[1]));
		from.mute(0);
		break;
	    }
	    case "ban": {
		if(argv.length < 2)
		    throw(new UserError("usage: ban NAME [DURATION]"));
		Sender from = frombyname(argv[1]);
		if(from == null)
		    throw(new UserError("%s: no such name", argv[1]));
		int dur = 3600;
		if(argv.length > 2)
		    dur = parsedur(argv[2]);
		wdgmsg("ban", from.bid, dur);
		break;
	    }
	    case "unban": {
		if(argv.length < 2)
		    throw(new UserError("usage: unban NAME"));
		Sender from = frombyname(argv[1]);
		if(from == null)
		    throw(new UserError("%s: no such name", argv[1]));
		wdgmsg("unban", from.bid);
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
	    double now = Utils.ntime();
	    Number cfrom = (Number)args[1];
	    Number bfrom = (Number)args[2];
	    String line = (String)args[3];
	    String pname = null;
	    if(args.length > 4)
		pname = (String)args[4];
	    if(cfrom == null) {
		append(new MyMessage(line, iw()));
	    } else {
		Sender from = frombyid(cfrom.longValue(), bfrom.intValue());
		if((pname != null) && (from.name == null))
		    from.name = pname;
		from.lastseen = now;
		if(now > from.muted) {
		    Message cmsg = new PNamedMessage(from, line, iw());
		    append(cmsg);
		}
	    }
	} else if(msg == "err") {
	    String err = (String)args[0];
	    Message cmsg = new SimpleMessage(err, wcol, iw());
	    append(cmsg);
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
