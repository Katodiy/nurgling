/*
 *  This file is part of the Haven & Hearth game client.
 *  Copyright (C) 2009 Fredrik Tolf <fredrik@dolda2000.com>, and
 *                     Björn Johannessen <johannessen.bjorn@gmail.com>
 *
 *  Redistribution and/or modification of this file is subject to the
 *  terms of the GNU Lesser General Public License, version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Other parts of this source tree adhere to other copying
 *  rights. Please see the file `COPYING' in the root directory of the
 *  source tree for details.
 *
 *  A copy the GNU Lesser General Public License is distributed along
 *  with the source tree of which this file is a part in the file
 *  `doc/LPGL-3'. If it is missing for any reason, please see the Free
 *  Software Foundation's website at <http://www.fsf.org/>, or write
 *  to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *  Boston, MA 02111-1307 USA
 */

package haven;

import java.awt.*;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;

public class Text implements Disposable {
    public static final Font serif = new Font("Serif", Font.PLAIN, 10);
    public static final Font sans  = new Font("Sans", Font.PLAIN, 10);
    public static final Font mono  = new Font("Monospaced", Font.PLAIN, 10);
    public static final Font fraktur = Resource.local().loadwait("ui/fraktur").flayer(Resource.Font.class).font;
    public static final Font dfont = sans;
    public static final Foundry std;
    public final BufferedImage img;
    public final String text;
    private Tex tex;
    public static final Color black = Color.BLACK;
    public static final Color white = Color.WHITE;
	
    static {
	std = new Foundry(sans, 10);
    }
	
    public static class Line extends Text {
	private final FontMetrics m;
	
	private Line(String text, BufferedImage img, FontMetrics m) {
	    super(text, img);
	    this.m = m;
	}
	
	public Coord base() {
	    return(new Coord(0, m.getLeading() + m.getAscent()));
	}
	
	public int advance(int pos) {
	    return(m.stringWidth(text.substring(0, pos)));
	}
	
	public int charat(int x) {
	    int l = 0, r = text.length() + 1;
	    while(true) {
		int p = (l + r) / 2;
		int a = advance(p);
		if((a < x) && (l < p)) {
		    l = p;
		} else if((a > x) && (r > p)) {
		    r = p;
		} else {
		    return(p);
		}
	    }
	}
    }

    public static int[] findspaces(String text) {
	java.util.List<Integer> l = new ArrayList<Integer>();
	for(int i = 0; i < text.length(); i++) {
	    char c = text.charAt(i);
	    if(Character.isWhitespace(c))
		l.add(i);
	}
	int[] ret = new int[l.size()];
	for(int i = 0; i < ret.length; i++)
	    ret[i] = l.get(i);
	return(ret);
    }
        
    public static abstract class Furnace {
	public abstract Text render(String text);

	public Text renderf(String fmt, Object... args) {
	    return(render(String.format(fmt, args)));
	}
    }

    public static class Foundry extends Furnace {
	private FontMetrics m;
	public final Font font;
	public final Color defcol;
	public boolean aa = false;
	private RichText.Foundry wfnd = null;
		
	public Foundry(Font f, Color defcol) {
	    font = f;
	    this.defcol = defcol;
	    BufferedImage junk = TexI.mkbuf(new Coord(10, 10));
	    java.awt.Graphics tmpl = junk.getGraphics();
	    tmpl.setFont(f);
	    m = tmpl.getFontMetrics();
	}
		
	public Foundry(Font f) {
	    this(f, Color.WHITE);
	}
	
	public Foundry(Font font, int psz, Color defcol) {
	    this(font.deriveFont(UI.scale((float)psz)), defcol);
	}

	public Foundry(Font font, int psz) {
	    this(font.deriveFont(UI.scale((float)psz)));
	}

	public Foundry aa(boolean aa) {
	    this.aa = aa;
	    return(this);
	}

	public int height() {
	    /* XXX? The only font which seems to have leading > 0 is
	     * the Moderne Fraktur font, for which the leading is
	     * necessary to get the full ascent of some glyphs.
	     * According to all specifications, this doesn't exactly
	     * seem right, so I'm not sure if it's that font that is
	     * buggy, or if this is actually as it should, but as it
	     * doesn't seem to affect any other fonts, perhaps it
	     * doesn't matter? */
	    return(m.getHeight());
	}

	public Coord strsize(String text) {
	    return(new Coord(m.stringWidth(text), height()));
	}
                
	public Text renderwrap(String text, Color c, int width) {
	    if(wfnd == null)
		wfnd = new RichText.Foundry(font, defcol);
	    wfnd.aa = aa;
	    text = RichText.Parser.quote(text);
	    if(c != null)
		text = String.format("$col[%d,%d,%d,%d]{%s}", c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha(), text);
	    return(wfnd.render(text, width));
	}
                
	public Text renderwrap(String text, int width) {
	    return(renderwrap(text, null, width));
	}
                
	public Line render(String text, Color c) {
	    Coord sz = strsize(text);
	    if(sz.x < 1)
		sz = sz.add(1, 0);
	    BufferedImage img = TexI.mkbuf(sz);
	    Graphics g = img.createGraphics();
	    if(aa)
		Utils.AA(g);
	    g.setFont(font);
	    g.setColor(c);
	    FontMetrics m = g.getFontMetrics();
	    /* See height() comment. */
	    g.drawString(text, 0, m.getLeading() + m.getAscent());
	    g.dispose();
	    return(new Line(text, img, m));
	}
		
	public Line render(String text) {
	    return(render(text, defcol));
	}

	public Line renderstroked(String text, Color c, Color stroke) {
		Coord sz = strsize(text);
		if (sz.x < 1)
			sz = sz.add(1, 0);
		sz = sz.add(2, 0);
		BufferedImage img = TexI.mkbuf(sz);
		Graphics g = img.createGraphics();
		if (aa)
			Utils.AA(g);
		g.setFont(font);
		FontMetrics m = g.getFontMetrics();
		g.setColor(stroke);
		g.drawString(text, 0, m.getAscent());
		g.drawString(text, 2, m.getAscent());
		g.drawString(text, 1, m.getAscent() - 1);
		g.drawString(text, 1, m.getAscent() + 1);
		g.setColor(c);
		g.drawString(text, 1, m.getAscent());
		g.dispose();
		return (new Line(text, img, m));
	}
    }

    public static abstract class Imager extends Furnace {
	private final Furnace back;

	public Imager(Furnace back) {
	    this.back = back;
	}

	protected abstract BufferedImage proc(Text text);

	public Text render(String text) {
	    return(new Text(text, proc(back.render(text))));
	}
    }

    public static abstract class UText<T> implements Indir<Text> {
	public final Furnace fnd;
	private Text cur = null;
	private T cv = null;

	public UText(Furnace fnd) {this.fnd = fnd;}

	protected Text render(String text) {return(fnd.render(text));}
	protected String text(T value) {return(String.valueOf(value));}
	protected abstract T value();

	public Text get() {
	    T value = value();
	    if(!Utils.eq(value, cv))
		cur = render(text(cv = value));
	    return(cur);
	}

	public Indir<Tex> tex() {
	    return(new Indir<Tex>() {
		    public Tex get() {
			return(UText.this.get().tex());
		    }
		});
	}

	public static UText forfield(Furnace fnd, final Object obj, String fn) {
	    final java.lang.reflect.Field f;
	    try {
		f = obj.getClass().getField(fn);
	    } catch(NoSuchFieldException e) {
		throw(new RuntimeException(e));
	    }
	    return(new UText<Object>(fnd) {
		    public Object value() {
			try {
			    return(f.get(obj));
			} catch(IllegalAccessException e) {
			    throw(new RuntimeException(e));
			}
		    }
		});
	}

	public static UText forfield(Object obj, String fn) {
	    return(forfield(std, obj, fn));
	}
    }

    public Text(String text, BufferedImage img) {
	this.text = text;
	this.img = img;
    }
	
    public Coord sz() {
	return(Utils.imgsz(img));
    }
	
    public static Line render(String text, Color c) {
	return(std.render(text, c));
    }
	
    public static Line renderf(Color c, String text, Object... args) {
	return(std.render(String.format(text, args), c));
    }
	
    public static Line render(String text) {
	return(render(text, Color.WHITE));
    }
	
    public Tex tex() {
	if(tex == null)
	    tex = new TexI(img);
	return(tex);
    }

    public void dispose() {
	if(tex != null)
	    tex.dispose();
    }
    
    public static void main(String[] args) throws Exception {
	String cmd = args[0].intern();
	if(cmd == "render") {
	    PosixArgs opt = PosixArgs.getopt(args, 1, "aw:f:s:c:");
	    boolean aa = false;
	    String font = "SansSerif";
	    int width = 100;
	    float size = 10;
	    Color col = Color.WHITE;
	    for(char c : opt.parsed()) {
		if(c == 'a') {
		    aa = true;
		} else if(c == 'f') {
		    font = opt.arg;
		} else if(c == 'w') {
		    width = Integer.parseInt(opt.arg);
		} else if(c == 's') {
		    size = Float.parseFloat(opt.arg);
		} else if(c == 'c') {
		    col = Color.decode(opt.arg);
		}
	    }
	    Foundry f = new Foundry(new Font(font, Font.PLAIN, 10).deriveFont(size), col);
	    f.aa = aa;
	    Text t = f.renderwrap(opt.rest[0], width);
	    try(java.io.OutputStream out = java.nio.file.Files.newOutputStream(Utils.path(opt.rest[1]))) {
		javax.imageio.ImageIO.write(t.img, "PNG", out);
	    }
	}
    }


	public static Line renderstroked(String text, Color c, Color s, Foundry fnd) {
		return fnd.renderstroked(text, c, s);
	}

	public static Line renderstroked(String text, Color c, Color s) {
		return renderstroked(text, c, s, std);
	}

	public static Line renderstroked(String text, Color c) {
		return renderstroked(text, c, Utils.contrast(c));
	}

	public static Line renderstroked(String text, Color c, Foundry fnd) {
		return renderstroked(text, c, Utils.contrast(c), fnd);
	}

	public static Line renderstroked(String text) {
		return renderstroked(text, Color.WHITE, Color.BLACK);
	}

	public static Line renderstroked(String text, Foundry fnd) {
		return renderstroked(text, Color.WHITE, Color.BLACK, fnd);
	}
}
