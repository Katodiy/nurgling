package nurgling;

import haven.*;
import haven.res.ui.tt.highlighting.Highlighting;
import haven.res.ui.tt.slot.Slotted;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class NUI extends UI {
    public NSessInfo sessInfo;
    public NDataTables dataTables;
    public boolean inspectMode = false;
    long tickId = 0;
    public NUI(Context uictx, Coord sz, Runner fun) {
        super(uictx, sz, fun);
        Thread writeConfigHook = new Thread(() -> {
//            NFoodWriter.instance.write();
            NConfiguration.getInstance().write();});
        Runtime.getRuntime().addShutdownHook(writeConfigHook);
        NUtils.setUI(this);
        dataTables = new NDataTables();
        Highlighting.init();
        Slotted.init();
        NFoodInfo.init();
    }

    @Override
    public void tick() {
        super.tick();
        tickId += 1;
        if (sessInfo == null && sess != null) {
            sessInfo = new NSessInfo(sess.username);
        }
        if (NUtils.getGameUI() == null && sessInfo != null) {
            for (Widget wdg : widgets.values()) {
                if (wdg instanceof Img) {
                    Img img = (Img) wdg;
                    if (img.tooltip instanceof Widget.KeyboundTip) {
                        if (!sessInfo.isVerified && ((Widget.KeyboundTip) img.tooltip).base.contains("Verif"))
                            sessInfo.isVerified = true;
                        else if (!sessInfo.isSubscribed && ((Widget.KeyboundTip) img.tooltip).base.contains("Subsc"))
                            sessInfo.isSubscribed = true;
                    }
                }
            }
        }
        if(NConfiguration.getInstance().showAreas && NUtils.getGameUI()!=null ) {
            ArrayList<NOCache.OverlayInfo> forRemove = new ArrayList<>();
            for (NOCache.OverlayInfo ol : ((NOCache) NUtils.getGameUI().ui.sess.glob.oc).overlays) {
                if (ol.ol.tick())
                    forRemove.add(ol);
            }
            for (NOCache.OverlayInfo ol : forRemove) {
                ol.ol.destroy();
                ((NOCache) NUtils.getGameUI().ui.sess.glob.oc).overlays.remove(ol);
            }
        }else{
            if(NUtils.getGameUI()!=null && !((NOCache) NUtils.getGameUI().ui.sess.glob.oc).overlays.isEmpty())
            {
                for (NOCache.OverlayInfo ol : ((NOCache) NUtils.getGameUI().ui.sess.glob.oc).overlays) {
                    ol.ol.destroy();
                }
                ((NOCache) NUtils.getGameUI().ui.sess.glob.oc).overlays.clear();
            }
        }
    }

    @Override
    public void keyup(KeyEvent ev) {
        if(NUtils.getGameUI()!=null && NUtils.getGameUI().map!=null) {
            if (ev.getKeyCode() == KeyEvent.VK_SHIFT) {
                if (inspectMode) {
                    inspectMode = false;
                    ((NMapView) NUtils.getGameUI().map).ttip.clear();
                }
            }
        }
        super.keyup(ev);
    }

    @Override
    public void keydown(KeyEvent ev) {
        if(NUtils.getGameUI()!=null && NUtils.getGameUI().map!=null) {
            if (ev.getKeyCode() == KeyEvent.VK_SHIFT) {
                inspectMode = true;
            }
        }
        super.keydown(ev);
    }

    Coord lastCoord = new Coord();

    @Override
    public void mousemove(MouseEvent ev, Coord c) {
        lastCoord = c;
        if ((dragged == null) && (pressed != null)) {
            dragged = pressed;
        }
        if(NUtils.getGameUI()!=null && NUtils.getGameUI().map!=null) {
            if (inspectMode) {
                if (modshift) {
                    ((NMapView) NUtils.getGameUI().map).inspect(c);
                } else {
                    inspectMode = false;
                    ((NMapView) NUtils.getGameUI().map).ttip.clear();
                }
            }
        }
        super.mousemove(ev, c);
    }

    public NBotsInfo.NButton dragged;
    public NBotsInfo.NButton pressed;

    @Override
    public void mouseup(MouseEvent ev, Coord c, int button) {
        if (pressed != null) {
            pressed = null;
        }
        if (dragged != null) {
            dropthing(root, mc, dragged.res.get());
            dragged = null;
        }
        super.mouseup(ev, c, button);
    }

    public float getDeltaZ() {
        return (float)Math.sin(tickId/10.)*1;
    }

    public long getTickId () {
        return tickId;
    }

    public Widget findInRoot(Class<?> c)
    {
        for(Widget wdg: root.children()){
            if(wdg.getClass()==c)
            {
                return wdg;
            }
        }
        return null;
    }

    @Override
    public void mousedown(MouseEvent ev, Coord c, int button) {
        if(button==3 && NConfiguration.getInstance().autoFlower)
            sessInfo.characterInfo.flowerCand = null;
        super.mousedown(ev, c, button);
    }

    public AtomicBoolean botMode = new AtomicBoolean(false);
}
