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
import haven.res.lib.itemtex.ItemTex;
import nurgling.*;
import nurgling.bots.settings.*;
import nurgling.tools.AreasID;

import haven.render.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

interface Operationable{
	void addWidget(Widget w);
}
public class OptWnd extends Window {
    public final Panel main, noptwnd;
    public Panel current;

    public void chpanel(Panel p) {
	if(current != null)
	    current.hide();
	(current = p).show();
	cresize(p);
    }

    public void cresize(Widget ch) {
	if(ch == current) {
	    Coord cc = this.c.add(this.sz.div(2));
	    pack();
	    move(cc.sub(this.sz.div(2)));
	}
    }

    public class PButton extends Button {
	public final Panel tgt;
	public final int key;

	public PButton(int w, String title, int key, Panel tgt) {
	    super(w, title, false);
	    this.tgt = tgt;
	    this.key = key;
	}

	public void click() {
	    chpanel(tgt);
	}

	public boolean keydown(KeyEvent ev) {
	    if((this.key != -1) && (ev.getKeyChar() == this.key)) {
		click();
		return(true);
	    }
	    return(false);
	}
    }

    public static class Panel extends Widget {
	public Panel() {
	    visible = false;
	    c = Coord.z;
	}
    }

    private void error(String msg) {
	GameUI gui = getparent(GameUI.class);
	if(gui != null)
	    gui.error(msg);
    }

    public class VideoPanel extends Panel {
	private final Widget back;
	private CPanel curcf;

	public VideoPanel(Panel prev) {
	    super();
	    back = add(new PButton(UI.scale(200), "Back", 27, prev));
	}

	public class CPanel extends Widget {
	    public GSettings prefs;

	    public CPanel(GSettings gprefs) {
		this.prefs = gprefs;
		Widget prev;
		int marg = UI.scale(5);
		prev = add(new CheckBox("Render shadows") {
			{a = prefs.lshadow.val;}

			public void set(boolean val) {
			    try {
				GSettings np = prefs.update(null, prefs.lshadow, val);
				ui.setgprefs(prefs = np);
			    } catch(GSettings.SettingException e) {
				error(e.getMessage());
				return;
			    }
			    a = val;
			}
		    }, Coord.z);
			prev = add(new Label("Camera:"), prev.pos("bl").adds(0, 5));
			Dropbox cam = add(new Dropbox<String>(100, 5, 16) {
				@Override
				protected String listitem(int i) {
					return new LinkedList<>(NMapView.camlist()).get(i);
				}

				@Override
				protected int listitems() {
					return NMapView.camlist().size();
				}

				@Override
				protected void drawitem(GOut g, String item, int i) {
					g.text(item, Coord.z);
				}

				@Override
				public void change(String item) {
					super.change(item);
					NMapView.defcam(item);
					if(NUtils.getGameUI() != null && NUtils.getGameUI().map != null) {
						NUtils.getGameUI().map.camera = NUtils.getGameUI().map.restorecam();
					}
				}
			}, prev.pos("ur").adds(5, 0));
			cam.sel = NMapView.defcam();
		prev = add(new Label("Render scale"), prev.pos("bl").adds(0, 5));
		{
		    Label dpy = new Label("");
		    final int steps = 4;
		    addhlp(prev.pos("bl").adds(0, 2), UI.scale(5),
			   prev = new HSlider(UI.scale(160), -2 * steps, 2 * steps, (int)Math.round(steps * Math.log(prefs.rscale.val) / Math.log(2.0f))) {
			       protected void added() {
				   dpy();
			       }
			       void dpy() {
				   dpy.settext(String.format("%.2f\u00d7", Math.pow(2, this.val / (double)steps)));
			       }
			       public void changed() {
				   try {
				       float val = (float)Math.pow(2, this.val / (double)steps);
				       ui.setgprefs(prefs = prefs.update(null, prefs.rscale, val));
				   } catch(GSettings.SettingException e) {
				       error(e.getMessage());
				       return;
				   }
				   dpy();
			       }
			   },
			   dpy);
		}
		prev = add(new CheckBox("Vertical sync") {
			{a = prefs.vsync.val;}

			public void set(boolean val) {
			    try {
				GSettings np = prefs.update(null, prefs.vsync, val);
				ui.setgprefs(prefs = np);
			    } catch(GSettings.SettingException e) {
				error(e.getMessage());
				return;
			    }
			    a = val;
			}
		    }, prev.pos("bl").adds(0, 5));
		prev = add(new Label("Framerate limit (active window)"), prev.pos("bl").adds(0, 5));
		{
		    Label dpy = new Label("");
		    final int max = 250;
		    addhlp(prev.pos("bl").adds(0, 2), UI.scale(5),
			   prev = new HSlider(UI.scale(160), 1, max, (prefs.hz.val == Float.POSITIVE_INFINITY) ? max : prefs.hz.val.intValue()) {
			       protected void added() {
				   dpy();
			       }
			       void dpy() {
				   if(this.val == max)
				       dpy.settext("None");
				   else
				       dpy.settext(Integer.toString(this.val));
			       }
			       public void changed() {
				   try {
				       if(this.val > 10)
					   this.val = (this.val / 2) * 2;
				       float val = (this.val == max) ? Float.POSITIVE_INFINITY : this.val;
				       ui.setgprefs(prefs = prefs.update(null, prefs.hz, val));
				   } catch(GSettings.SettingException e) {
				       error(e.getMessage());
				       return;
				   }
				   dpy();
			       }
			   },
			   dpy);
		}
		prev = add(new Label("Framerate limit (background window)"), prev.pos("bl").adds(0, 5));
		{
		    Label dpy = new Label("");
		    final int max = 250;
		    addhlp(prev.pos("bl").adds(0, 2), UI.scale(5),
			   prev = new HSlider(UI.scale(160), 1, max, (prefs.bghz.val == Float.POSITIVE_INFINITY) ? max : prefs.bghz.val.intValue()) {
			       protected void added() {
				   dpy();
			       }
			       void dpy() {
				   if(this.val == max)
				       dpy.settext("None");
				   else
				       dpy.settext(Integer.toString(this.val));
			       }
			       public void changed() {
				   try {
				       if(this.val > 10)
					   this.val = (this.val / 2) * 2;
				       float val = (this.val == max) ? Float.POSITIVE_INFINITY : this.val;
				       ui.setgprefs(prefs = prefs.update(null, prefs.bghz, val));
				   } catch(GSettings.SettingException e) {
				       error(e.getMessage());
				       return;
				   }
				   dpy();
			       }
			   },
			   dpy);
		}
		prev = add(new Label("Lighting mode"), prev.pos("bl").adds(0, 5));
		{
		    boolean[] done = {false};
		    RadioGroup grp = new RadioGroup(this) {
			    public void changed(int btn, String lbl) {
				if(!done[0])
				    return;
				try {
				    ui.setgprefs(prefs = prefs
						 .update(null, prefs.lightmode, GSettings.LightMode.values()[btn])
						 .update(null, prefs.maxlights, 0));
				} catch(GSettings.SettingException e) {
				    error(e.getMessage());
				    return;
				}
				resetcf();
			    }
			};
		    prev = grp.add("Global", prev.pos("bl").adds(5, 2));
		    prev.settip("Global lighting supports fewer light sources, and scales worse in " +
				"performance per additional light source, than zoned lighting, but " +
				"has lower baseline performance requirements.", true);
		    prev = grp.add("Zoned", prev.pos("bl").adds(0, 2));
		    prev.settip("Zoned lighting supports far more light sources than global " +
				"lighting with better performance, but may have higher performance " +
				"requirements in cases with few light sources, and may also have " +
				"issues on old graphics hardware.", true);
		    grp.check(prefs.lightmode.val.ordinal());
		    done[0] = true;
		}
		prev = add(new Label("Light-source limit"), prev.pos("bl").adds(0, 5).x(0));
		{
		    Label dpy = new Label("");
		    int val = prefs.maxlights.val;
		    if(val == 0) {    /* XXX: This is just ugly. */
			if(prefs.lightmode.val == GSettings.LightMode.ZONED)
			    val = Lighting.LightGrid.defmax;
			else
			    val = Lighting.SimpleLights.defmax;
		    }
		    addhlp(prev.pos("bl").adds(0, 2), UI.scale(5),
			   prev = new HSlider(UI.scale(160), 1, 32, val / 4) {
			       protected void added() {
				   dpy();
			       }
			       void dpy() {
				   dpy.settext(Integer.toString(this.val * 4));
			       }
			       public void changed() {dpy();}
			       public void fchanged() {
				   try {
				       ui.setgprefs(prefs = prefs.update(null, prefs.maxlights, this.val * 4));
				   } catch(GSettings.SettingException e) {
				       error(e.getMessage());
				       return;
				   }
				   dpy();
			       }
			       {
				   settip("The light-source limit means different things depending on the " +
					  "selected lighting mode. For Global lighting, it limits the total "+
					  "number of light-sources globally. For Zoned lighting, it limits the " +
					  "total number of overlapping light-sources at any point in space.",
					  true);
			       }
			   },
			   dpy);
		}
		prev = add(new Label("Frame sync mode"), prev.pos("bl").adds(0, 5).x(0));
		{
		    boolean[] done = {false};
		    RadioGroup grp = new RadioGroup(this) {
			    public void changed(int btn, String lbl) {
				if(!done[0])
				    return;
				try {
				    ui.setgprefs(prefs = prefs.update(null, prefs.syncmode, JOGLPanel.SyncMode.values()[btn]));
				} catch(GSettings.SettingException e) {
				    error(e.getMessage());
				    return;
				}
			    }
			};
		    prev = add(new Label("\u2191 Better performance, worse latency"), prev.pos("bl").adds(5, 2));
		    prev = grp.add("One-frame overlap", prev.pos("bl").adds(0, 2));
		    prev = grp.add("Tick overlap", prev.pos("bl").adds(0, 2));
		    prev = grp.add("CPU-sequential", prev.pos("bl").adds(0, 2));
		    prev = grp.add("GPU-sequential", prev.pos("bl").adds(0, 2));
		    prev = add(new Label("\u2193 Worse performance, better latency"), prev.pos("bl").adds(0, 2));
		    grp.check(prefs.syncmode.val.ordinal());
		    done[0] = true;
		}
		/* XXXRENDER
		composer.add(new CheckBox("Antialiasing") {
			{a = cf.fsaa.val;}

			public void set(boolean val) {
			    try {
				cf.fsaa.set(val);
			    } catch(GLSettings.SettingException e) {
				error(e.getMessage());
				return;
			    }
			    a = val;
			    cf.dirty = true;
			}
		    });
		composer.add(new Label("Anisotropic filtering"));
		if(cf.anisotex.max() <= 1) {
		    composer.add(new Label("(Not supported)"));
		} else {
		    final Label dpy = new Label("");
		    composer.addRow(
			    new HSlider(UI.scale(160), (int)(cf.anisotex.min() * 2), (int)(cf.anisotex.max() * 2), (int)(cf.anisotex.val * 2)) {
			    protected void added() {
				dpy();
			    }
			    void dpy() {
				if(val < 2)
				    dpy.settext("Off");
				else
				    dpy.settext(String.format("%.1f\u00d7", (val / 2.0)));
			    }
			    public void changed() {
				try {
				    cf.anisotex.set(val / 2.0f);
				} catch(GLSettings.SettingException e) {
				    error(e.getMessage());
				    return;
				}
				dpy();
				cf.dirty = true;
			    }
			},
			dpy
		    );
		}
		*/
		add(new Button(UI.scale(200), "Reset to defaults", false).action(() -> {
			    ui.setgprefs(GSettings.defaults());
			    curcf.destroy();
			    curcf = null;
		}), prev.pos("bl").adds(0, 5));
		pack();
	    }
	}

	public void draw(GOut g) {
	    if((curcf == null) || (ui.gprefs != curcf.prefs))
		resetcf();
	    super.draw(g);
	}

	private void resetcf() {
	    if(curcf != null)
		curcf.destroy();
	    curcf = add(new CPanel(ui.gprefs), 0, 0);
	    back.move(curcf.pos("bl").adds(0, 15));
	    pack();
	}
    }

    public class AudioPanel extends Panel {
	public AudioPanel(Panel back) {
	    prev = add(new Label("Master audio volume"), 0, 0);
	    prev = add(new HSlider(UI.scale(200), 0, 1000, (int)(Audio.volume * 1000)) {
		    public void changed() {
			Audio.setvolume(val / 1000.0);
		    }
		}, prev.pos("bl").adds(0, 2));
	    prev = add(new Label("Interface sound volume"), prev.pos("bl").adds(0, 15));
	    prev = add(new HSlider(UI.scale(200), 0, 1000, 0) {
		    protected void attach(UI ui) {
			super.attach(ui);
			val = (int)(ui.audio.aui.volume * 1000);
		    }
		    public void changed() {
			ui.audio.aui.setvolume(val / 1000.0);
		    }
		}, prev.pos("bl").adds(0, 2));
	    prev = add(new Label("In-game event volume"), prev.pos("bl").adds(0, 5));
	    prev = add(new HSlider(UI.scale(200), 0, 1000, 0) {
		    protected void attach(UI ui) {
			super.attach(ui);
			val = (int)(ui.audio.pos.volume * 1000);
		    }
		    public void changed() {
			ui.audio.pos.setvolume(val / 1000.0);
		    }
		}, prev.pos("bl").adds(0, 2));
	    prev = add(new Label("Ambient volume"), prev.pos("bl").adds(0, 5));
	    prev = add(new HSlider(UI.scale(200), 0, 1000, 0) {
		    protected void attach(UI ui) {
			super.attach(ui);
			val = (int)(ui.audio.amb.volume * 1000);
		    }
		    public void changed() {
			ui.audio.amb.setvolume(val / 1000.0);
		    }
		}, prev.pos("bl").adds(0, 2));
	    add(new PButton(UI.scale(200), "Back", 27, back), prev.pos("bl").adds(0, 30));
	    pack();
	}
    }

    public class InterfacePanel extends Panel {
	public InterfacePanel(Panel back) {
	    Widget prev = add(new Label("Interface scale (requires restart)"), 0, 0);
	    {
		Label dpy = new Label("");
		final double smin = 1, smax = Math.floor(UI.maxscale() / 0.25) * 0.25;
		final int steps = (int)Math.round((smax - smin) / 0.25);
		addhlp(prev.pos("bl").adds(0, 2), UI.scale(5),
		       prev = new HSlider(UI.scale(160), 0, steps, (int)Math.round(steps * (Utils.getprefd("uiscale", 1.0) - smin) / (smax - smin))) {
			       protected void added() {
				   dpy();
			       }
			       void dpy() {
				   dpy.settext(String.format("%.2f\u00d7", smin + (((double)this.val / steps) * (smax - smin))));
			       }
			       public void changed() {
				   double val = smin + (((double)this.val / steps) * (smax - smin));
				   Utils.setprefd("uiscale", val);
				   dpy();
			       }
			   },
		       dpy);
	    }
	    prev = add(new Label("Object fine-placement granularity"), prev.pos("bl").adds(0, 5));
	    {
		Label pos = add(new Label("Position"), prev.pos("bl").adds(5, 2));
		Label ang = add(new Label("Angle"), pos.pos("bl").adds(0, 2));
		int x = Math.max(pos.pos("ur").x, ang.pos("ur").x);
		{
		    Label dpy = new Label("");
		    final double smin = 1, smax = Math.floor(UI.maxscale() / 0.25) * 0.25;
		    final int steps = (int)Math.round((smax - smin) / 0.25);
		    int ival = (int)Math.round(MapView.plobpgran);
		    addhlp(Coord.of(x + UI.scale(5), pos.c.y), UI.scale(5),
			   prev = new HSlider(UI.scale(155 - x), 2, 17, (ival == 0) ? 17 : ival) {
				   protected void added() {
				       dpy();
				   }
				   void dpy() {
				       dpy.settext((this.val == 17) ? "\u221e" : Integer.toString(this.val));
				   }
				   public void changed() {
				       Utils.setprefd("plobpgran", MapView.plobpgran = ((this.val == 17) ? 0 : this.val));
				       dpy();
				   }
			       },
			   dpy);
		}
		{
		    Label dpy = new Label("");
		    final double smin = 1, smax = Math.floor(UI.maxscale() / 0.25) * 0.25;
		    final int steps = (int)Math.round((smax - smin) / 0.25);
		    int[] vals = {4, 5, 6, 8, 9, 10, 12, 15, 18, 20, 24, 30, 36, 40, 45, 60, 72, 90, 120, 180, 360};
		    int ival = 0;
		    for(int i = 0; i < vals.length; i++) {
			if(Math.abs((MapView.plobagran * 2) - vals[i]) < Math.abs((MapView.plobagran * 2) - vals[ival]))
			    ival = i;
		    }
		    addhlp(Coord.of(x + UI.scale(5), ang.c.y), UI.scale(5),
			   prev = new HSlider(UI.scale(155 - x), 0, vals.length - 1, ival) {
				   protected void added() {
				       dpy();
				   }
				   void dpy() {
				       dpy.settext(String.format("%d\u00b0", 360 / vals[this.val]));
				   }
				   public void changed() {
				       Utils.setprefd("plobagran", MapView.plobagran = (vals[this.val] / 2.0));
				       dpy();
				   }
			       },
			   dpy);
		}
	    }
	    add(new PButton(UI.scale(200), "Back", 27, back), prev.pos("bl").adds(0, 30).x(0));
	    pack();
	}
    }

    private static final Text kbtt = RichText.render("$col[255,255,0]{Escape}: Cancel input\n" +
						     "$col[255,255,0]{Backspace}: Revert to default\n" +
						     "$col[255,255,0]{Delete}: Disable keybinding", 0);
    public class BindingPanel extends Panel {
	private int addbtn(Widget cont, String nm, KeyBinding cmd, int y) {
	    return(cont.addhl(new Coord(0, y), cont.sz.x,
			      new Label(nm), new SetButton(UI.scale(175), cmd))
		   + UI.scale(2));
	}
	private int addbtn(Widget cont, String nm, KeyBinding cmd, int y, boolean withUpd) {
		return(cont.addhl(new Coord(0, y), cont.sz.x,
				new Label(nm), new SetButton(UI.scale(175), cmd, withUpd))
				+ UI.scale(2));
	}
	public BindingPanel(Panel back) {
	    super();
	    Scrollport scroll = add(new Scrollport(UI.scale(new Coord(300, 300))), 0, 0);
	    Widget cont = scroll.cont;
	    Widget prev;
	    int y = 0;
	    y = cont.adda(new Label("Main menu"), cont.sz.x / 2, y, 0.5, 0.0).pos("bl").adds(0, 5).y;
	    y = addbtn(cont, "Inventory", GameUI.kb_inv, y);
	    y = addbtn(cont, "Equipment", GameUI.kb_equ, y);
	    y = addbtn(cont, "Character sheet", GameUI.kb_chr, y);
	    y = addbtn(cont, "Map window", GameUI.kb_map, y);
	    y = addbtn(cont, "Kith & Kin", GameUI.kb_bud, y);
	    y = addbtn(cont, "Options", GameUI.kb_opt, y);
	    y = addbtn(cont, "Search actions", GameUI.kb_srch, y);
	    y = addbtn(cont, "Toggle chat", GameUI.kb_chat, y);
//	    y = addbtn(cont, "Quick chat", ChatUI.kb_quick, y);
	    y = addbtn(cont, "Take screenshot", GameUI.kb_shoot, y);
	    y = addbtn(cont, "Minimap icons", GameUI.kb_ico, y);
	    y = addbtn(cont, "Toggle UI", GameUI.kb_hide, y);
	    y = addbtn(cont, "Log out", GameUI.kb_logout, y);
	    y = addbtn(cont, "Switch character", GameUI.kb_switchchr, y);
	    y = cont.adda(new Label("Map options"), cont.sz.x / 2, y + UI.scale(10), 0.5, 0.0).pos("bl").adds(0, 5).y;
	    y = addbtn(cont, "Display claims", GameUI.kb_claim, y);
	    y = addbtn(cont, "Display villages", GameUI.kb_vil, y);
	    y = addbtn(cont, "Display realms", GameUI.kb_rlm, y);
	    y = addbtn(cont, "Display grid-lines", MapView.kb_grid, y);
	    y = cont.adda(new Label("Camera control"), cont.sz.x / 2, y + UI.scale(10), 0.5, 0.0).pos("bl").adds(0, 5).y;
	    y = addbtn(cont, "Rotate left", MapView.kb_camleft, y);
	    y = addbtn(cont, "Rotate right", MapView.kb_camright, y);
	    y = addbtn(cont, "Zoom in", MapView.kb_camin, y);
	    y = addbtn(cont, "Zoom out", MapView.kb_camout, y);
	    y = addbtn(cont, "Reset", MapView.kb_camreset, y);
	    y = cont.adda(new Label("Map window"), cont.sz.x / 2, y + UI.scale(10), 0.5, 0.0).pos("bl").adds(0, 5).y;
	    y = addbtn(cont, "Reset view", MapWnd.kb_home, y);
	    y = addbtn(cont, "Place marker", MapWnd.kb_mark, y);
	    y = addbtn(cont, "Toggle markers", MapWnd.kb_hmark, y);
	    y = addbtn(cont, "Compact mode", MapWnd.kb_compact, y);
	    y = cont.adda(new Label("Walking speed"), cont.sz.x / 2, y + UI.scale(10), 0.5, 0.0).pos("bl").adds(0, 5).y;
	    y = addbtn(cont, "Increase speed", Speedget.kb_speedup, y);
	    y = addbtn(cont, "Decrease speed", Speedget.kb_speeddn, y);
	    for(int i = 0; i < 4; i++)
		y = addbtn(cont, String.format("Set speed %d", i + 1), Speedget.kb_speeds[i], y);
	    y = cont.adda(new Label("Combat actions"), cont.sz.x / 2, y + UI.scale(10), 0.5, 0.0).pos("bl").adds(0, 5).y;
	    for(int i = 0; i < Fightsess.kb_acts.length; i++)
		y = addbtn(cont, String.format("Combat action %d", i + 1), Fightsess.kb_acts[i], y);
	    y = addbtn(cont, "Switch targets", Fightsess.kb_relcycle, y);
		y = cont.adda(new Label("Bot binding"), cont.sz.x / 2, y + UI.scale(10), 0.5, 0.0).pos("bl").adds(0, 5).y;
		y = addbtn(cont, "Check clay", NMapView.kb_checkClay, y);
		y = addbtn(cont, "Check water", NMapView.kb_checkWater, y);
		y = addbtn(cont, "Feed clover", NMapView.kb_feedclower, y);
		y = addbtn(cont, "Give", NMapView.kb_give, y);

		y = cont.adda(new Label("Tool belt"), cont.sz.x / 2, y + UI.scale(10), 0.5, 0.0).pos("bl").adds(0, 5).y;
		y = addbtn(cont, "Button 1", NConfiguration.getInstance().toolBelts.get("belt0").toolKeys[0], y, true);
		y = addbtn(cont, "Button 2", NConfiguration.getInstance().toolBelts.get("belt0").toolKeys[1], y, true);
		y = addbtn(cont, "Button 3", NConfiguration.getInstance().toolBelts.get("belt0").toolKeys[2], y, true);
		y = addbtn(cont, "Button 4", NConfiguration.getInstance().toolBelts.get("belt0").toolKeys[3], y, true);
		y = addbtn(cont, "Button 5", NConfiguration.getInstance().toolBelts.get("belt0").toolKeys[4], y, true);
		y = addbtn(cont, "Button 6", NConfiguration.getInstance().toolBelts.get("belt0").toolKeys[5], y, true);
		y = addbtn(cont, "Button 7", NConfiguration.getInstance().toolBelts.get("belt0").toolKeys[6], y, true);
		y = addbtn(cont, "Button 8", NConfiguration.getInstance().toolBelts.get("belt0").toolKeys[7], y, true);
		y = addbtn(cont, "Button 9", NConfiguration.getInstance().toolBelts.get("belt0").toolKeys[8], y, true);
		y = addbtn(cont, "Button 10", NConfiguration.getInstance().toolBelts.get("belt0").toolKeys[9], y, true);
		y = addbtn(cont, "Button 11", NConfiguration.getInstance().toolBelts.get("belt0").toolKeys[10], y, true);
		y = addbtn(cont, "Button 12", NConfiguration.getInstance().toolBelts.get("belt0").toolKeys[11], y, true);
		y = addbtn(cont, "Ad1.Button 1", NConfiguration.getInstance().toolBelts.get("belt1").toolKeys[0], y, true);
		y = addbtn(cont, "Ad1.Button 2", NConfiguration.getInstance().toolBelts.get("belt1").toolKeys[1], y, true);
		y = addbtn(cont, "Ad1.Button 3", NConfiguration.getInstance().toolBelts.get("belt1").toolKeys[2], y, true);
		y = addbtn(cont, "Ad1.Button 4", NConfiguration.getInstance().toolBelts.get("belt1").toolKeys[3], y, true);
		y = addbtn(cont, "Ad1.Button 5", NConfiguration.getInstance().toolBelts.get("belt1").toolKeys[4], y, true);
		y = addbtn(cont, "Ad1.Button 6", NConfiguration.getInstance().toolBelts.get("belt1").toolKeys[5], y, true);
		y = addbtn(cont, "Ad1.Button 7", NConfiguration.getInstance().toolBelts.get("belt1").toolKeys[6], y, true);
		y = addbtn(cont, "Ad1.Button 8", NConfiguration.getInstance().toolBelts.get("belt1").toolKeys[7], y, true);
		y = addbtn(cont, "Ad1.Button 9", NConfiguration.getInstance().toolBelts.get("belt1").toolKeys[8], y, true);
		y = addbtn(cont, "Ad1.Button 10", NConfiguration.getInstance().toolBelts.get("belt1").toolKeys[9], y, true);
		y = addbtn(cont, "Ad1.Button 11", NConfiguration.getInstance().toolBelts.get("belt1").toolKeys[10], y, true);
		y = addbtn(cont, "Ad1.Button 12", NConfiguration.getInstance().toolBelts.get("belt1").toolKeys[11], y, true);
		y = addbtn(cont, "Ad2.Button 1", NConfiguration.getInstance().toolBelts.get("belt2").toolKeys[0], y, true);
		y = addbtn(cont, "Ad2.Button 2", NConfiguration.getInstance().toolBelts.get("belt2").toolKeys[1], y, true);
		y = addbtn(cont, "Ad2.Button 3", NConfiguration.getInstance().toolBelts.get("belt2").toolKeys[2], y, true);
		y = addbtn(cont, "Ad2.Button 4", NConfiguration.getInstance().toolBelts.get("belt2").toolKeys[3], y, true);
		y = addbtn(cont, "Ad2.Button 5", NConfiguration.getInstance().toolBelts.get("belt2").toolKeys[4], y, true);
		y = addbtn(cont, "Ad2.Button 6", NConfiguration.getInstance().toolBelts.get("belt2").toolKeys[5], y, true);
		y = addbtn(cont, "Ad2.Button 7", NConfiguration.getInstance().toolBelts.get("belt2").toolKeys[6], y, true);
		y = addbtn(cont, "Ad2.Button 8", NConfiguration.getInstance().toolBelts.get("belt2").toolKeys[7], y, true);
		y = addbtn(cont, "Ad2.Button 9", NConfiguration.getInstance().toolBelts.get("belt2").toolKeys[8], y, true);
		y = addbtn(cont, "Ad2.Button 10", NConfiguration.getInstance().toolBelts.get("belt2").toolKeys[9], y, true);
		y = addbtn(cont, "Ad2.Button 11", NConfiguration.getInstance().toolBelts.get("belt2").toolKeys[10], y, true);
		y = addbtn(cont, "Ad2.Button 12", NConfiguration.getInstance().toolBelts.get("belt2").toolKeys[11], y, true);
	    prev = adda(new PointBind(UI.scale(200)), scroll.pos("bl").adds(0, 10).x(scroll.sz.x / 2), 0.5, 0.0);
	    prev = adda(new PButton(UI.scale(200), "Back", 27, back), prev.pos("bl").adds(0, 10).x(scroll.sz.x / 2), 0.5, 0.0);
	    pack();
	}

	public class SetButton extends KeyMatch.Capture {
	    public final KeyBinding cmd;
		public boolean withUpd =false;

		public SetButton(int w, KeyBinding cmd,boolean withUpd) {
			super(w, cmd.key());
			this.cmd = cmd;
			this.withUpd = withUpd;
		}
	    public SetButton(int w, KeyBinding cmd) {
		super(w, cmd.key());
		this.cmd = cmd;
	    }

	    public void set(KeyMatch key) {
		super.set(key);
		cmd.set(key);
		if(withUpd){
			NConfiguration.resetToolBelts();
		}
	    }

	    public void draw(GOut g) {
		if(cmd.key() != key)
		    super.set(cmd.key());
		super.draw(g);
	    }

	    protected KeyMatch mkmatch(KeyEvent ev) {
		return(KeyMatch.forevent(ev, ~cmd.modign));
	    }

	    protected boolean handle(KeyEvent ev) {
		if(ev.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
		    cmd.set(null);
		    super.set(cmd.key());
		    return(true);
		}
		return(super.handle(ev));
	    }

	    public Object tooltip(Coord c, Widget prev) {
		return(kbtt.tex());
	    }
	}
    }


    public static class PointBind extends Button {
	public static final String msg = "Bind other elements...";
	public static final Resource curs = Resource.local().loadwait("gfx/hud/curs/wrench");
	private UI.Grab mg, kg;
	private KeyBinding cmd;

	public PointBind(int w) {
	    super(w, msg, false);
	    tooltip = RichText.render("Bind a key to an element not listed above, such as an action-menu " +
				      "button. Click the element to bind, and then press the key to bind to it. " +
				      "Right-click to stop rebinding.",
				      300);
	}

	public void click() {
	    if(mg == null) {
		change("Click element...");
		mg = ui.grabmouse(this);
	    } else if(kg != null) {
		kg.remove();
		kg = null;
		change(msg);
	    }
	}

	private boolean handle(KeyEvent ev) {
	    switch(ev.getKeyCode()) {
	    case KeyEvent.VK_SHIFT: case KeyEvent.VK_CONTROL: case KeyEvent.VK_ALT:
	    case KeyEvent.VK_META: case KeyEvent.VK_WINDOWS:
		return(false);
	    }
	    int code = ev.getKeyCode();
	    if(code == KeyEvent.VK_ESCAPE) {
		return(true);
	    }
	    if(code == KeyEvent.VK_BACK_SPACE) {
		cmd.set(null);
		return(true);
	    }
	    if(code == KeyEvent.VK_DELETE) {
		cmd.set(KeyMatch.nil);
		return(true);
	    }
	    KeyMatch key = KeyMatch.forevent(ev, ~cmd.modign);
	    if(key != null)
		cmd.set(key);
	    return(true);
	}

	public boolean mousedown(Coord c, int btn) {
	    if(mg == null)
		return(super.mousedown(c, btn));
	    Coord gc = ui.mc;
	    if(btn == 1) {
		this.cmd = KeyBinding.Bindable.getbinding(ui.root, gc);
		return(true);
	    }
	    if(btn == 3) {
		mg.remove();
		mg = null;
		change(msg);
		return(true);
	    }
	    return(false);
	}

	public boolean mouseup(Coord c, int btn) {
	    if(mg == null)
		return(super.mouseup(c, btn));
	    Coord gc = ui.mc;
	    if(btn == 1) {
		if((this.cmd != null) && (KeyBinding.Bindable.getbinding(ui.root, gc) == this.cmd)) {
		    mg.remove();
		    mg = null;
		    kg = ui.grabkeys(this);
		    change("Press key...");
		} else {
		    this.cmd = null;
		}
		return(true);
	    }
	    if(btn == 3)
		return(true);
	    return(false);
	}

	public Resource getcurs(Coord c) {
	    if(mg == null)
		return(null);
	    return(curs);
	}

	public boolean keydown(KeyEvent ev) {
	    if(kg == null)
		return(super.keydown(ev));
	    if(handle(ev)) {
		kg.remove();
		kg = null;
		cmd = null;
		change("Click another element...");
		mg = ui.grabmouse(this);
	    }
	    return(true);
	}
    }

    public OptWnd(boolean gopts) {
	super(Coord.z, "Options", true);
	main = add(new Panel());
	Panel video = add(new VideoPanel(main));
	Panel audio = add(new AudioPanel(main));
	Panel iface = add(new InterfacePanel(main));
	Panel keybind = add(new BindingPanel(main));
	noptwnd = add(new NOptWnd(main));

	int y = 0;
	Widget prev;
	y = main.add(new PButton(UI.scale(200), "Interface settings", 'v', iface), 0, y).pos("bl").adds(0, 5).y;
	y = main.add(new PButton(UI.scale(200), "Video settings", 'v', video), 0, y).pos("bl").adds(0, 5).y;
	y = main.add(new PButton(UI.scale(200), "Audio settings", 'a', audio), 0, y).pos("bl").adds(0, 5).y;
	y = main.add(new PButton(UI.scale(200), "Keybindings", 'k', keybind), 0, y).pos("bl").adds(0, 5).y;
	y = main.add(new PButton(UI.scale(200), "Nurgling settings", 'k', noptwnd), 0, y).pos("bl").adds(0, 5).y;
	y += UI.scale(60);
	if(gopts) {
	    y = main.add(new Button(UI.scale(200), "Switch character", false).action(() -> {
			getparent(GameUI.class).act("lo", "cs");
	    }), 0, y).pos("bl").adds(0, 5).y;
	    y = main.add(new Button(UI.scale(200), "Log out", false).action(() -> {
			getparent(GameUI.class).act("lo");
	    }), 0, y).pos("bl").adds(0, 5).y;
	}
	y = main.add(new Button(UI.scale(200), "Close", false).action(() -> {
		    OptWnd.this.hide();
	}), 0, y).pos("bl").adds(0, 5).y;
	this.main.pack();

	chpanel(this.main);
    }

    public OptWnd() {
	this(true);
    }

    public void wdgmsg(Widget sender, String msg, Object... args) {
	if((sender == this) && (msg == "close")) {
	    hide();
	} else {
	    super.wdgmsg(sender, msg, args);
	}
    }

    public void show() {
	chpanel(main);
	super.show();
    }

	public class NOptWnd extends Panel implements NDTarget2  {
		private final Widget save;
		private final Widget back;

		private Widget oldVis;
		private Widget curVis;
		ArrayList<Widget> panels = new ArrayList<>();
		private QoL qol_p;
		private BotSettings botsettings_p;

		private IngredientSettings is_p;
		private AreaSettings areas_p;
		private AutoPicking autoPicking_p;
		private MarksNRings marks_p;
		private ColorPanel colors_p;
		private boolean needUpdate = false;

		public NOptWnd(Panel prev1) {
			super();
			Widget prev;
			int button_size = 150;
			prev = add(new Button(button_size, "Marks and Rings") {
				@Override
				public void click() {
					for (Widget w : panels)
						w.hide();
					marks_p.show();
					curVis = marks_p;

				}
			}, new Coord(0, 0));
			Widget start = prev;
			prev = add(new Button(button_size, "Quality of Life") {
				@Override
				public void click() {
					for (Widget w : panels)
						w.hide();
					qol_p.show();
					curVis = qol_p;

				}
			}, prev.pos("ur").adds(5, 0));

			prev = add(new Button(button_size, "Bots Settings") {
				@Override
				public void click() {
					for (Widget w : panels)
						w.hide();
					botsettings_p.show();
					curVis = botsettings_p;
				}
			}, prev.pos("ur").adds(5, 0));

			prev = add(new Button(button_size, "Ingredient") {
				@Override
				public void click() {
					for (Widget w : panels)
						w.hide();
					is_p.show();
					curVis = is_p;
				}
			}, prev.pos("ur").adds(5, 0));

			prev = add(new Button(button_size, "Areas ID") {
				@Override
				public void click() {
					for (Widget w : panels)
						w.hide();
					areas_p.show();
					curVis = areas_p;
				}
			}, prev.pos("ur").adds(5, 0));

			prev = add(new Button(button_size, "Auto Picking") {
				@Override
				public void click() {
					for (Widget w : panels)
						w.hide();
					autoPicking_p.show();
					curVis = autoPicking_p;
				}
			}, prev.pos("ur").adds(5, 0));

			prev = add(new Button(button_size, "Colors") {
				@Override
				public void click() {
					for (Widget w : panels)
						w.hide();
					colors_p.show();
					curVis = colors_p;
				}
			}, prev.pos("ur").adds(5, 0));


			qol_p = add(new QoL(), start.pos("bl").adds(0, 5));
			botsettings_p = add(new BotSettings(), start.pos("bl").adds(0, 5));
			is_p = add(new IngredientSettings(), start.pos("bl").adds(0, 5));
			areas_p = add(new AreaSettings(), start.pos("bl").adds(0, 5));
			marks_p = add(new MarksNRings(), start.pos("bl").adds(0, 5));
			colors_p = add(new ColorPanel(), start.pos("bl").adds(0, 5));
			autoPicking_p = add(new AutoPicking(), start.pos("bl").adds(0, 5));
			panels.add(qol_p);
			panels.add(botsettings_p);
			panels.add(is_p);
			panels.add(areas_p);
			panels.add(marks_p);
			panels.add(colors_p);
			panels.add(autoPicking_p);
			for (Widget w : panels)
				w.hide();
			oldVis = marks_p;
			curVis = marks_p;
			curVis.show();
			save = add(new Button(200, "Save") {
				@Override
				public void click() {
					if(curVis == is_p){
						is_p.is.save();
					}
					NConfiguration.getInstance().write();
					NConfiguration.getInstance().install();
					NGob.updateMarked();
				}
			}, curVis.pos("bl").adds(0, 5));
			back = add(new PButton(UI.scale(200), "Back", 27, prev1));
			back.move(save.pos("ur").adds(5, 6));
			pack();
		}

		@Override
		public boolean drop(WItem target, Coord cc, Coord ul) {
			if(is_p==curVis)
				is_p.drop(target);

			return true;
		}

		@Override
		public boolean iteminteract(WItem target, Coord cc, Coord ul) {
			return false;
		}


		class MarksNRings extends Widget {

			Widget arrow_red_mark;
			Widget ring_red_mark;

			public MarksNRings() {
				Widget prev;
				prev = add(new Label("Ranges of buildings:"), new Coord(0, 0));
				prev = add(new CheckBox("Barter Hand:") {
					{
						a = NConfiguration.getInstance().rings.get("barterhand").isEnable;
					}

					public void set(boolean val) {
						NConfiguration.getInstance().rings.get("barterhand").isEnable = val;
						a = val;
					}
				}, prev.pos("bl").adds(0, 5));
				prev = add(new CheckBox("Brazier:") {
					{
						a = NConfiguration.getInstance().rings.get("brazier").isEnable;
					}

					public void set(boolean val) {
						NConfiguration.getInstance().rings.get("brazier").isEnable = val;
						a = val;
					}
				}, prev.pos("bl").adds(0, 5));
				prev = add(new CheckBox("Trough:") {
					{
						a = NConfiguration.getInstance().rings.get("trough").isEnable;
					}

					public void set(boolean val) {
						NConfiguration.getInstance().rings.get("trough").isEnable = val;
						a = val;
					}
				}, prev.pos("bl").adds(0, 5));
				prev = add(new CheckBox("Mine support:") {
					{
						a = NConfiguration.getInstance().rings.get("minesup").isEnable;
					}

					public void set(boolean val) {
						NConfiguration.getInstance().rings.get("minesup").isEnable = val;
						a = val;
					}
				}, prev.pos("bl").adds(0, 5));

				prev = add(new CheckBox("Bee Skep:") {
					{
						a = NConfiguration.getInstance().rings.get("beeskep").isEnable;
					}

					public void set(boolean val) {
						NConfiguration.getInstance().rings.get("beeskep").isEnable = val;
						a = val;
					}
				}, prev.pos("bl").adds(0, 5));


				prev = add(new Label("Ranges of animals:"), prev.pos("bl").adds(0, 5));
				prev = add(new CheckBox("Adder:") {
					{
						a = NConfiguration.getInstance().rings.get("adder").isEnable;
					}

					public void set(boolean val) {
						NConfiguration.getInstance().rings.get("adder").isEnable = val;
						a = val;
					}
				}, prev.pos("bl").adds(0, 5));
				add(new TextEntry(150, String.valueOf(NConfiguration.getInstance().rings.get("adder").size)) {

					public void activate(String text) {
						try {
							NConfiguration.getInstance().rings.get("adder").size = Double.parseDouble(text);
							commit();
						} catch (NumberFormatException e) {
							this.settext(String.valueOf(NConfiguration.getInstance().rings.get("adder").size));
						}
					}
				}, new Coord(300, prev.c.y));
				prev = add(new CheckBox("Badger:") {
					{
						a = NConfiguration.getInstance().rings.get("badger").isEnable;
					}

					public void set(boolean val) {
						NConfiguration.getInstance().rings.get("badger").isEnable = val;
						a = val;
					}
				}, prev.pos("bl").adds(0, 5));
				add(new TextEntry(150, String.valueOf(NConfiguration.getInstance().rings.get("badger").size)) {

					public void activate(String text) {
						try {
							NConfiguration.getInstance().rings.get("badger").size = Double.parseDouble(text);
							commit();
						} catch (NumberFormatException e) {
							this.settext(String.valueOf(NConfiguration.getInstance().rings.get("badger").size));
						}
					}
				}, new Coord(300, prev.c.y));

				prev = add(new CheckBox("Bat:") {
					{
						a = NConfiguration.getInstance().rings.get("bat").isEnable;
					}

					public void set(boolean val) {
						NConfiguration.getInstance().rings.get("bat").isEnable = val;
						a = val;
					}
				}, prev.pos("bl").adds(0, 5));
				add(new TextEntry(150, String.valueOf(NConfiguration.getInstance().rings.get("bat").size)) {

					public void activate(String text) {
						try {
							NConfiguration.getInstance().rings.get("bat").size = Double.parseDouble(text);
							commit();
						} catch (NumberFormatException e) {
							this.settext(String.valueOf(NConfiguration.getInstance().rings.get("bat").size));
						}
					}
				}, new Coord(300, prev.c.y));

				prev = add(new CheckBox("Bear:") {
					{
						a = NConfiguration.getInstance().rings.get("bear").isEnable;
					}

					public void set(boolean val) {
						NConfiguration.getInstance().rings.get("bear").isEnable = val;
						a = val;
					}
				}, prev.pos("bl").adds(0, 5));
				add(new TextEntry(150, String.valueOf(NConfiguration.getInstance().rings.get("bear").size)) {

					public void activate(String text) {
						try {
							NConfiguration.getInstance().rings.get("bear").size = Double.parseDouble(text);
							commit();
						} catch (NumberFormatException e) {
							this.settext(String.valueOf(NConfiguration.getInstance().rings.get("bear").size));
						}
					}
				}, new Coord(300, prev.c.y));
				prev = add(new CheckBox("Boar:") {
					{
						a = NConfiguration.getInstance().rings.get("boar").isEnable;
					}

					public void set(boolean val) {
						NConfiguration.getInstance().rings.get("boar").isEnable = val;
						a = val;
					}
				}, prev.pos("bl").adds(0, 5));
				add(new TextEntry(150, String.valueOf(NConfiguration.getInstance().rings.get("boar").size)) {

					public void activate(String text) {
						try {
							NConfiguration.getInstance().rings.get("boar").size = Double.parseDouble(text);
							commit();
						} catch (NumberFormatException e) {
							this.settext(String.valueOf(NConfiguration.getInstance().rings.get("boar").size));
						}
					}
				}, new Coord(300, prev.c.y));
				prev = add(new CheckBox("Goat:") {
					{
						a = NConfiguration.getInstance().rings.get("wildgoat").isEnable;
					}

					public void set(boolean val) {
						NConfiguration.getInstance().rings.get("wildgoat").isEnable = val;
						a = val;
					}
				}, prev.pos("bl").adds(0, 5));
				add(new TextEntry(150, String.valueOf(NConfiguration.getInstance().rings.get("wildgoat").size)) {

					public void activate(String text) {
						try {
							NConfiguration.getInstance().rings.get("wildgoat").size = Double.parseDouble(text);
							commit();
						} catch (NumberFormatException e) {
							this.settext(String.valueOf(NConfiguration.getInstance().rings.get("wildgoat").size));
						}
					}
				}, new Coord(300, prev.c.y));
				prev = add(new CheckBox("Lynx:") {
					{
						a = NConfiguration.getInstance().rings.get("lynx").isEnable;
					}

					public void set(boolean val) {
						NConfiguration.getInstance().rings.get("lynx").isEnable = val;
						a = val;
					}
				}, prev.pos("bl").adds(0, 5));
				add(new TextEntry(150, String.valueOf(NConfiguration.getInstance().rings.get("lynx").size)) {

					public void activate(String text) {
						try {
							NConfiguration.getInstance().rings.get("lynx").size = Double.parseDouble(text);
							commit();
						} catch (NumberFormatException e) {
							this.settext(String.valueOf(NConfiguration.getInstance().rings.get("lynx").size));
						}
					}
				}, new Coord(300, prev.c.y));
				prev = add(new CheckBox("Mammoth:") {
					{
						a = NConfiguration.getInstance().rings.get("mammoth").isEnable;
					}

					public void set(boolean val) {
						NConfiguration.getInstance().rings.get("mammoth").isEnable = val;
						a = val;
					}
				}, prev.pos("bl").adds(0, 5));
				add(new TextEntry(150, String.valueOf(NConfiguration.getInstance().rings.get("mammoth").size)) {

					public void activate(String text) {
						try {
							NConfiguration.getInstance().rings.get("mammoth").size = Double.parseDouble(text);
							commit();
						} catch (NumberFormatException e) {
							this.settext(String.valueOf(NConfiguration.getInstance().rings.get("mammoth").size));
						}
					}
				}, new Coord(300, prev.c.y));

				prev = add(new CheckBox("Moose:") {
					{
						a = NConfiguration.getInstance().rings.get("moose").isEnable;
					}

					public void set(boolean val) {
						NConfiguration.getInstance().rings.get("moose").isEnable = val;
						a = val;
					}
				}, prev.pos("bl").adds(0, 5));
				add(new TextEntry(150, String.valueOf(NConfiguration.getInstance().rings.get("moose").size)) {

					public void activate(String text) {
						try {
							NConfiguration.getInstance().rings.get("moose").size = Double.parseDouble(text);
							commit();
						} catch (NumberFormatException e) {
							this.settext(String.valueOf(NConfiguration.getInstance().rings.get("moose").size));
						}
					}
				}, new Coord(300, prev.c.y));

				prev = add(new CheckBox("Orca:") {
					{
						a = NConfiguration.getInstance().rings.get("orca").isEnable;
					}

					public void set(boolean val) {
						NConfiguration.getInstance().rings.get("orca").isEnable = val;
						a = val;
					}
				}, prev.pos("bl").adds(0, 5));
				add(new TextEntry(150, String.valueOf(NConfiguration.getInstance().rings.get("orca").size)) {

					public void activate(String text) {
						try {
							NConfiguration.getInstance().rings.get("orca").size = Double.parseDouble(text);
							commit();
						} catch (NumberFormatException e) {
							this.settext(String.valueOf(NConfiguration.getInstance().rings.get("orca").size));
						}
					}
				}, new Coord(300, prev.c.y));

				prev = add(new CheckBox("Wolverine:") {
					{
						a = NConfiguration.getInstance().rings.get("wolverine").isEnable;
					}

					public void set(boolean val) {
						NConfiguration.getInstance().rings.get("wolverine").isEnable = val;
						a = val;
					}
				}, prev.pos("bl").adds(0, 5));
				add(new TextEntry(150, String.valueOf(NConfiguration.getInstance().rings.get("wolverine").size)) {

					public void activate(String text) {
						try {
							NConfiguration.getInstance().rings.get("wolverine").size = Double.parseDouble(text);
							commit();
						} catch (NumberFormatException e) {
							this.settext(String.valueOf(NConfiguration.getInstance().rings.get("wolverine").size));
						}
					}
				}, new Coord(300, prev.c.y));

				prev = add(new CheckBox("Walrus:") {
					{
						a = NConfiguration.getInstance().rings.get("walrus").isEnable;
					}

					public void set(boolean val) {
						NConfiguration.getInstance().rings.get("walrus").isEnable = val;
						a = val;
					}
				}, prev.pos("bl").adds(0, 5));
				add(new TextEntry(150, String.valueOf(NConfiguration.getInstance().rings.get("walrus").size)) {

					public void activate(String text) {
						try {
							NConfiguration.getInstance().rings.get("walrus").size = Double.parseDouble(text);
							commit();
						} catch (NumberFormatException e) {
							this.settext(String.valueOf(NConfiguration.getInstance().rings.get("walrus").size));
						}
					}
				}, new Coord(300, prev.c.y));
				prev = add(new CheckBox("Wolf:") {
					{
						a = NConfiguration.getInstance().rings.get("wolf").isEnable;
					}

					public void set(boolean val) {
						NConfiguration.getInstance().rings.get("wolf").isEnable = val;
						a = val;
					}
				}, prev.pos("bl").adds(0, 5));
				add(new TextEntry(150, String.valueOf(NConfiguration.getInstance().rings.get("wolf").size)) {

					public void activate(String text) {
						try {
							NConfiguration.getInstance().rings.get("wolf").size = Double.parseDouble(text);
							commit();
						} catch (NumberFormatException e) {
							this.settext(String.valueOf(NConfiguration.getInstance().rings.get("wolf").size));
						}
					}
				}, new Coord(300, prev.c.y));
				prev = add(new CheckBox("Troll:") {
					{
						a = NConfiguration.getInstance().rings.get("troll").isEnable;
					}

					public void set(boolean val) {
						NConfiguration.getInstance().rings.get("troll").isEnable = val;
						a = val;
					}
				}, prev.pos("bl").adds(0, 5));
				add(new TextEntry(150, String.valueOf(NConfiguration.getInstance().rings.get("troll").size)) {

					public void activate(String text) {
						try {
							NConfiguration.getInstance().rings.get("troll").size = Double.parseDouble(text);
							commit();
						} catch (NumberFormatException e) {
							this.settext(String.valueOf(NConfiguration.getInstance().rings.get("troll").size));
						}
					}
				}, new Coord(300, prev.c.y));
				prev = add(new Label("Notification about players:"), prev.pos("bl").adds(0, 5));
				Widget right;
				prev = add(new Label("White:"), prev.pos("bl").adds(0, 5));
				prev = add(new CheckBox("Arrow") {
					{
						a = NConfiguration.getInstance().players.get("white").arrow;
					}

					public void set(boolean val) {
						NConfiguration.getInstance().players.get("white").arrow = val;
						a = val;
					}
				}, prev.pos("bl").adds(0, 5));
				add(new CheckBox("Ring") {
					{
						a = NConfiguration.getInstance().players.get("white").ring;
					}

					public void set(boolean val) {
						NConfiguration.getInstance().players.get("white").ring = val;
						a = val;
					}
				}, prev.pos("ur").adds(5, 0));
				prev = add(new Label("Red:"), prev.pos("bl").adds(0, 5));
				prev = add(new CheckBox("Arrow") {
					{
						a = NConfiguration.getInstance().players.get("red").arrow;
					}

					public void set(boolean val) {
						NConfiguration.getInstance().players.get("red").arrow = val;
						a = val;
						if (!a)
							arrow_red_mark.hide();
						else
							arrow_red_mark.show();
					}
				}, prev.pos("bl").adds(0, 5));

				right = add(new CheckBox("Arrow mark") {
					{
						a = NConfiguration.getInstance().players.get("red").mark;
						if (!NConfiguration.getInstance().players.get("red").arrow)
							hide();
					}

					public void set(boolean val) {
						NConfiguration.getInstance().players.get("red").mark = val;
						a = val;
					}
				}, prev.pos("ur").adds(5, 0));
				arrow_red_mark = right;
				right = add(new CheckBox("Ring") {
					{
						a = NConfiguration.getInstance().players.get("red").ring;
					}

					public void set(boolean val) {
						NConfiguration.getInstance().players.get("red").ring = val;
						a = val;
						if (!a)
							ring_red_mark.hide();
						else
							ring_red_mark.show();
					}
				}, right.pos("ur").adds(5, 0));
				right = add(new CheckBox("Ring mark") {
					{
						a = NConfiguration.getInstance().players.get("red").mark_target;
						if (!NConfiguration.getInstance().players.get("red").ring)
							hide();
					}

					public void set(boolean val) {
						NConfiguration.getInstance().players.get("red").mark_target = val;
						a = val;
					}
				}, right.pos("ur").adds(5, 0));
				ring_red_mark = right;

				pack();
			}
		}

		class ColorPanel extends Widget {
			public ColorPanel() {
				Widget prev;
				Widget color_b;
				prev = add(new Label("Overlays colors:"), new Coord(0, 0));
				color_b = add(new NColorWidget("IDLE:","free"), prev.pos("bl").adds(0, 5));
				color_b = add(new NColorWidget("Ready:", "ready"), color_b.pos("bl").adds(0, 5));
				color_b = add(new NColorWidget("Warning:","warning"), color_b.pos("bl").adds(0, 5));
				color_b = add(new NColorWidget("In work:","inwork"), color_b.pos("bl").adds(0, 5));
				prev = add(new Label("Garden pots:"), color_b.pos("bl").adds(0, 5));
				color_b = add(new NColorWidget("No soil:","no_soil"), prev.pos("bl").adds(0, 5));
				color_b = add(new NColorWidget("No water:", "no_water"), color_b.pos("bl").adds(0, 5));
				prev = add(new Label("Containers:"), color_b.pos("bl").adds(0, 5));
				color_b = add(new NColorWidget("Full:","full"), prev.pos("bl").adds(0, 5));
				color_b = add(new NColorWidget("Not full:", "not_full"), color_b.pos("bl").adds(0, 5));
				pack();
			}
		}

		class QoL extends Widget {
			public QoL() {

				prev = add(new Label("Other:"), new Coord(0, 0));
				prev = add(new CheckBox("Show crop stage:") {
					{
						a = NConfiguration.getInstance().showCropStage;
					}

					public void set(boolean val) {
						NConfiguration.getInstance().showCropStage = val;
						a = val;
					}
				}, prev.pos("bl").adds(0, 5));
				prev = add(new CheckBox("Night vision:") {
					{
						a = NConfiguration.getInstance().nightVision;
					}

					public void set(boolean val) {
						NConfiguration.getInstance().nightVision = val;
						a = val;
					}
				}, prev.pos("bl").adds(0, 5));
				prev = add(new CheckBox("PF Bounding Boxes:") {
					{
						a = NConfiguration.getInstance().enablePfBoundingBoxes;
					}

					public void set(boolean val) {
						NConfiguration.getInstance().enablePfBoundingBoxes = val;
						a = val;
					}
				}, prev.pos("bl").adds(0, 5));
				prev = add(new CheckBox("Bots zones:") {
					{
						a = NConfiguration.getInstance().showAreas;
					}

					public void set(boolean val) {
						NConfiguration.getInstance().showAreas = val;
						a = val;
					}
				}, prev.pos("bl").adds(0, 5));

				prev = add(new Label("Panels:"), prev.pos("bl").adds(0, 5));
				prev = add(new CheckBox("Enable panel#0") {
					{
						a = NConfiguration.getInstance().toolBelts.get("belt0").isEnable;
					}

					public void set(boolean val) {
						NConfiguration.getInstance().toolBelts.get("belt0").isEnable = val;
						a = val;
					}
				}, prev.pos("bl").adds(0, 5));
				prev = add(new CheckBox("Enable panel#1") {
					{
						a = NConfiguration.getInstance().toolBelts.get("belt1").isEnable;
					}

					public void set(boolean val) {
						NConfiguration.getInstance().toolBelts.get("belt1").isEnable = val;
						a = val;
					}
				}, prev.pos("bl").adds(0, 5));
				prev = add(new CheckBox("Enable panel#2") {
					{
						a = NConfiguration.getInstance().toolBelts.get("belt2").isEnable;
					}

					public void set(boolean val) {
						NConfiguration.getInstance().toolBelts.get("belt2").isEnable = val;
						a = val;
					}
				}, prev.pos("bl").adds(0, 5));

				prev = add(new Label("Visualisation of path:"), prev.pos("bl").adds(0, 5));
				prev = add(new CheckBox("Player") {
                {
                    a = NConfiguration.getInstance().pathCategories.contains(NPathVisualizer.PathCategory.ME);
                }

                public void set(boolean val) {
                    if(val)
                        NConfiguration.getInstance().pathCategories.add(NPathVisualizer.PathCategory.ME);
                    else
                        NConfiguration.getInstance().pathCategories.remove(NPathVisualizer.PathCategory.ME);
                    a = val;
                    NConfiguration.getInstance().write();
                }
            }, prev.pos("bl").adds(0, 5));

            prev = add(new CheckBox("Foe") {
                {
                    a = NConfiguration.getInstance().pathCategories.contains(NPathVisualizer.PathCategory.FOE);
                }

                public void set(boolean val) {
                    if(val)
                        NConfiguration.getInstance().pathCategories.add(NPathVisualizer.PathCategory.FOE);
                    else
                        NConfiguration.getInstance().pathCategories.remove(NPathVisualizer.PathCategory.FOE);
                    a = val;
                    NConfiguration.getInstance().write();
                }
            }, prev.pos("bl").adds(0, 5));
            prev = add(new CheckBox("Friend") {
                {
                    a = NConfiguration.getInstance().pathCategories.contains(NPathVisualizer.PathCategory.FRIEND);
                }

                public void set(boolean val) {
                    if(val)
                        NConfiguration.getInstance().pathCategories.add(NPathVisualizer.PathCategory.FRIEND);
                    else
                        NConfiguration.getInstance().pathCategories.remove(NPathVisualizer.PathCategory.FRIEND);
                    a = val;
                    NConfiguration.getInstance().write();
                }
            }, prev.pos("bl").adds(0, 5));
            prev = add(new CheckBox("Other") {
                {
                    a = NConfiguration.getInstance().pathCategories.contains(NPathVisualizer.PathCategory.OTHER);
                }

                public void set(boolean val) {
                    if(val)
                        NConfiguration.getInstance().pathCategories.add(NPathVisualizer.PathCategory.OTHER);
                    else
                        NConfiguration.getInstance().pathCategories.remove(NPathVisualizer.PathCategory.OTHER);
                    a = val;
                    NConfiguration.getInstance().write();
                }
            }, prev.pos("bl").adds(0, 5));
            prev = add(new CheckBox("Path finder") {
                {
                    a = NConfiguration.getInstance().pathCategories.contains(NPathVisualizer.PathCategory.PF);
                }

                public void set(boolean val) {
                    if(val)
                        NConfiguration.getInstance().pathCategories.add(NPathVisualizer.PathCategory.PF);
                    else
                        NConfiguration.getInstance().pathCategories.remove(NPathVisualizer.PathCategory.PF);
                    a = val;
                    NConfiguration.getInstance().write();
                }
            }, prev.pos("bl").adds(0, 5));

				prev = add(new Label("Speed:"), prev.pos("bl").adds(0, 5));
				prev = add(new Label("Player:"), prev.pos("bl").adds(0, 5));
				Dropbox player = add(new Dropbox<String>(100, 5, 16) {
					@Override
					protected String listitem(int i) {
						return new LinkedList<>(NConfiguration.getInstance().playerSpeed_h.keySet()).get(i);
					}

					@Override
					protected int listitems() {
						return NConfiguration.getInstance().playerSpeed_h.keySet().size();
					}

					@Override
					protected void drawitem(GOut g, String item, int i) {
						g.text(item, Coord.z);
					}

					@Override
					public void change(String item) {
						super.change(item);
						NConfiguration.getInstance().playerSpeed = NConfiguration.getInstance().playerSpeed_h.get(item);
					}
				}, prev.pos("ur").adds(20, 0));

				for (String key : NConfiguration.getInstance().playerSpeed_h.keySet()) {
					if (NConfiguration.getInstance().playerSpeed_h.get(key) == NConfiguration.getInstance().playerSpeed)
						player.sel = key;
				}

				prev = add(new Label("Horse:"), prev.pos("bl").adds(0, 5));
				Dropbox horse = add(new Dropbox<String>(100, 5, 16) {
					@Override
					protected String listitem(int i) {
						return new LinkedList<>(NConfiguration.getInstance().horseSpeed_h.keySet()).get(i);
					}

					@Override
					protected int listitems() {
						return NConfiguration.getInstance().horseSpeed_h.keySet().size();
					}

					@Override
					protected void drawitem(GOut g, String item, int i) {
						g.text(item, Coord.z);
					}

					@Override
					public void change(String item) {
						super.change(item);
						NConfiguration.getInstance().horseSpeed = NConfiguration.getInstance().horseSpeed_h.get(item);
					}
				}, player.pos("bl").adds(0, 5));
				for (String key : NConfiguration.getInstance().horseSpeed_h.keySet()) {
					if (NConfiguration.getInstance().horseSpeed_h.get(key) == NConfiguration.getInstance().horseSpeed)
						horse.sel = key;
				}
				pack();
			}
		}

		class IngredientSettings extends Widget {
			public void drop(WItem target) {
				is.drop(target);
			}

			nurgling.bots.settings.IngredientSettings is;

			public IngredientSettings() {
				is = add(new nurgling.bots.settings.IngredientSettings());
				pack();
			}
		}

		class AreaSettings extends Widget {
			AreaIconSelecter area;
			public AreaSettings() {
			prev = add(new Dropbox<String>(100, 5, 16) {
					@Override
					protected String listitem(int i) {
						return Stream.of(AreasID.values())
								.map(Enum::name)
								.collect(Collectors.toList()).get(i);
					}

					@Override
					protected int listitems() {
						List<String> enumNames = Stream.of(AreasID.values())
								.map(Enum::name)
								.collect(Collectors.toList());
						return enumNames.size();
					}

					@Override
					protected void drawitem(GOut g, String item, int i) {
						g.text(item, Coord.z);
					}

					@Override
					public void change(String item) {
						area.setAreaID(AreasID.valueOf(item));
						super.change(item);
					}
				});

				prev = area = (AreaIconSelecter)add(new AreaIconSelecter(AreasID.branch),prev.pos("bl").adds(0, 5));

				pack();
			}
		}

		class BotSettings extends Widget {
			LinkedList<String> names = new LinkedList<>();
			LinkedList<Widget> settings = new LinkedList<>();

			public BotSettings() {
				prev = add(new Dropbox<String>(100, 5, 16) {
					@Override
					protected String listitem(int i) {
						return names.get(i);
					}

					@Override
					protected int listitems() {
						return names.size();
					}

					@Override
					protected void drawitem(GOut g, String item, int i) {
						g.text(item, Coord.z);
					}

					@Override
					public void change(String item) {
						super.change(item);
						for(Widget w: settings)
							if(w.getClass().getName().contains(item)){
								for(Widget unit: settings)
									unit.hide();
								w.show();
								parent.pack();
								((NOptWnd)parent.parent).needUpdate = true;
							}
					}
				});


				Operationable l = (w)->{
					add(w,prev.pos("bl").add(0,5));
					settings.add(w);
					names.add(w.getClass().getName().substring(23));
				};

				l.addWidget(new Dryer());
				l.addWidget(new Butcher());
				l.addWidget(new Smelter());
				l.addWidget(new Tanning());
				l.addWidget(new FarmerCarrrot());

				for(Widget w: settings)
					w.hide();
				pack();
			}
		}

		class AutoPicking extends Widget {
			class PickAction extends Widget{
				NConfiguration.PickingAction value;

				CheckBox isEnable;
				int id;
				public PickAction(NConfiguration.PickingAction val, int id) {
					this.id = id;
					this.value = val;
					isEnable = add(new CheckBox("") {
						{
							a = value.isEnable;
						}

						public void set(boolean val) {
							value.isEnable = val;
							a = val;
							ArrayList<NConfiguration.PickingAction> pa = new ArrayList<>();
							for(Integer key :wdgts.keySet()){
								PickAction p = ((PickAction) wdgts.get(key));
								pa.add(new NConfiguration.PickingAction(p.value.action,p.isEnable.a));
							}
							NConfiguration.getInstance().pickingActions = pa;
							NConfiguration.getInstance().write();
						}
					}, new Coord(0,0));
					add(new Label(value.action),isEnable.pos("ur").add(20,0));
					add(new Button(20,"\u25Bc"){
						@Override
						public void click() {
							int id = ((PickAction)this.parent).id;
							if(id<wdgts.size()-1){
								Widget w = wdgts.get(id);
								Coord c = w.c;
								w.move(w.pos("bl").adds(0,5));
								wdgts.get(id+1).move(c);
								wdgts.put(id,wdgts.get(id+1));
								wdgts.put(id+1,w);
								int old_id = id;
								((PickAction)w).id = old_id + 1;
								((PickAction)wdgts.get(old_id)).id = old_id;
								ArrayList<NConfiguration.PickingAction> pa = new ArrayList<>();
								for(Integer key :wdgts.keySet()){
									PickAction p = ((PickAction) wdgts.get(key));
									pa.add(new NConfiguration.PickingAction(p.value.action,p.isEnable.a));
								}
								NConfiguration.getInstance().pickingActions = pa;
								NConfiguration.getInstance().write();
							}
						}
					},isEnable.pos("ur").add(100,0));
					add(new Button(20,"\u25B2"){
						@Override
						public void click() {
							int id = ((PickAction)this.parent).id;
							if(id>0){
								Widget w = wdgts.get(id-1);
								Coord c = w.c;
								w.move(w.pos("bl").adds(0,5));
								wdgts.get(id).move(c);
								wdgts.put(id-1,wdgts.get(id));
								wdgts.put(id,w);
								int old_id = id;
								((PickAction)w).id = old_id;
								((PickAction)wdgts.get(old_id-1)).id = old_id-1;
								ArrayList<NConfiguration.PickingAction> pa = new ArrayList<>();
								for(Integer key :wdgts.keySet()){
									PickAction p = ((PickAction) wdgts.get(key));
									pa.add(new NConfiguration.PickingAction(p.value.action,p.isEnable.a));
								}
								NConfiguration.getInstance().pickingActions = pa;
								NConfiguration.getInstance().write();
							}
						}
					},isEnable.pos("ur").add(130,0));
					pack();


				}
			}
			public HashMap<Integer, Widget> wdgts = new HashMap<>();
			public AutoPicking() {

				prev = add(new Label("Other:"), new Coord(0, 0));
				prev = add(new CheckBox("Enable Auto Picking:") {
					{
						a = NConfiguration.getInstance().autoPicking;
					}

					public void set(boolean val) {
						NConfiguration.getInstance().autoPicking = val;
						a = val;
					}
				}, prev.pos("bl").adds(0, 5));
				int counter = 0;
				for (NConfiguration.PickingAction pickingAction : NConfiguration.getInstance().pickingActions) {
					prev = add(new PickAction(pickingAction, counter), prev.pos("bl").adds(0, 5));
					wdgts.put(counter,prev);
					counter++;
				}

				pack();
			}
		}

		public void draw(GOut g) {
			if (curVis != oldVis || needUpdate) {
				save.move(curVis.pos("bl").adds(0, 5));
				back.move(save.pos("ur").adds(5, 6));
				oldVis = curVis;
				pack();
			}
			super.draw(g);
		}
	}
}
