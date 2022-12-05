package nurgling;

import haven.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class NUI extends UI {
    public boolean inspectMode = false;
    long tickId = 0;
    public NUI(Context uictx, Coord sz, Runner fun) {
        super(uictx, sz, fun);
        Thread writeConfigHook = new Thread(() -> {NConfiguration.getInstance().write();});
        Runtime.getRuntime().addShutdownHook(writeConfigHook);
        NUtils.setUI(this);
    }

    @Override
    public void tick() {
        try {
            super.tick();
            tickId+=1;
        }catch (Exception e){
            e.printStackTrace();
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
}
