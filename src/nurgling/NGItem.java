package nurgling;

import haven.*;
import haven.res.ui.tt.defn.DefName;
import haven.res.ui.tt.highlighting.Highlighting;
import haven.res.ui.tt.slots.ISlots;

import java.awt.event.KeyEvent;

public class NGItem extends GItem {
    public boolean isSeached = false;
    int old_infoseq;


    public static int HAVE_CONTENT = 0x08;
    public static int SPR_IS_READY = 0x04;
    public static int NAME_IS_READY = 0x02;
    public static int RAWINFO_IS_READY = 0x01;

    public static int READY = SPR_IS_READY|NAME_IS_READY;

    public int status = 0;
    private double quality = -1;
    public long meterUpdated = 0;

    public static class NContent{
        private double quality = -1;
        private String name = null;

        public NContent(double quality, String name) {
            this.quality = quality;
            this.name = name;
        }

        public double quality() {
            return quality;
        }

        public String name() {
            return name;
        }
    }
    private NContent content = null;
    private Coord sprSz = null;
    public String defn = null;

    public NGItem(Indir<Resource> res, Message sdt) {
        super(res, sdt);
        old_infoseq = infoseq;
    }

    @Override
    public void tick(double dt) {
        super.tick(dt);
        if(infoseq!=old_infoseq || (status&READY)!=READY)
        {
            if(rawinfo!= null) {
                status &= ~RAWINFO_IS_READY;
                status &= ~HAVE_CONTENT;
                for (Object o : rawinfo.data) {
                    if (o instanceof Object[]) {
                        Object[] a = (Object[]) o;
                        if (a[0] instanceof Integer) {
                            String resName = NUtils.getUI().sess.getResName((Integer) a[0]);
                            if (resName != null) {
                                if ( resName.equals("ui/tt/q/quality")) {
                                    quality = (Float)a[1];
                                }
                                else if(resName.equals("ui/tt/cont")) {
                                    double q = -1;
                                    String name = null;
                                    for (Object so : a) {
                                        if (so instanceof Object[]) {
                                            Object[] cont = (Object[]) so;
                                            for (Object sso : cont) {
                                                if (sso instanceof Object[]) {
                                                    Object[] b = (Object[]) sso;
                                                    if (b[0] instanceof Integer) {
                                                        String resName2 = NUtils.getUI().sess.getResName((Integer) b[0]);
                                                        if (resName2 != null) {
                                                            if (resName2.equals("ui/tt/cn")) {
                                                                name = (String) b[1];
                                                            } else if (resName2.equals("ui/tt/q/quality")) {
                                                                q = (Float) b[1];
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if(name!=null && q!=-1) {
                                        content = new NContent(q, name);
                                        status |= HAVE_CONTENT;
                                    }
                                }
                            }
                            else
                            {
                                return;
                            }
                        }

                    }
                }
                status |= RAWINFO_IS_READY;
            }
            if(spr!=null)
            {
                if(sprSz == null) {
                    sprSz = spr.sz();
                    status |= SPR_IS_READY;
                }
                status &= ~NAME_IS_READY;
                if(res.get().layer(Resource.tooltip)!=null || res.get().name.equals("gfx/invobjs/gems/gemstone")) {
                    defn = DefName.getname(this);
                    if (defn != null && !defn.isEmpty()) {
                        status |= NAME_IS_READY;
                    }
                }
            }
            old_infoseq = infoseq;
        }
    }

    public String name(){
        if((status & NAME_IS_READY) == NAME_IS_READY)
        {
            return defn;
        }
        return null;
    }

    public double quality(){
        if((status & RAWINFO_IS_READY) == RAWINFO_IS_READY)
        {
            return quality;
        }
        return -1.;
    }

    public Coord sprSz(){
        if((status & SPR_IS_READY) == SPR_IS_READY)
        {
            return sprSz;
        }
        return null;
    }

    public NContent content(){
        if((status & HAVE_CONTENT) == HAVE_CONTENT)
        {
            return content;
        }
        return null;
    }

    public int getStatus()
    {
        return status;
    }

    @Override
    public void wdgmsg(String msg, Object... args) {
        if((status&NAME_IS_READY)==NAME_IS_READY && msg.equals("take"))
            NUtils.getGameUI().getCharInfo().setCandidate(defn);
        super.wdgmsg(msg, args);
    }

    @Override
    public void wdgmsg(Widget sender, String msg, Object... args) {
        super.wdgmsg(sender, msg, args);
    }

    public boolean needrender() {
        if((status & SPR_IS_READY) == SPR_IS_READY && (status & NAME_IS_READY) == NAME_IS_READY) {
            try {
                for (ItemInfo inf : info()) {
                    if (inf instanceof NFoodInfo) {
                        return ((NFoodInfo) inf).check();
                    }
                }
            }
            catch (Loading ignored)
            {
            }
        }
        return false;
    }

    @Override
    public void uimsg(String name, Object... args) {
        super.uimsg(name, args);
        if(name.equals("tt") || name.equals("meter")) {
            meterUpdated = System.currentTimeMillis();
        }
    }

    public boolean needlongtip() {
        if((status & SPR_IS_READY) == SPR_IS_READY) {
            for (ItemInfo inf : info()) {
                if (inf instanceof NFoodInfo) {
                    return ((NFoodInfo) inf).needToolTip;
                } else if (inf instanceof NCuriosity) {
                    return ((NCuriosity) inf).needUpdate();
                }
                if (inf instanceof ISlots) {
                    return this.ui.modshift!=((ISlots)inf).isShifted;
                }
            }
        }
        return false;
    }

    @Override
    public boolean keydown(KeyEvent ev) {
        return super.keydown(ev);
    }

    @Override
    public boolean keyup(KeyEvent ev) {
        return super.keyup(ev);
    }

    public ItemInfo getInfo(Class<? extends ItemInfo> candidate) {
        for (ItemInfo inf : info) {
            if (inf.getClass() == candidate) {
                return inf;
            }
        }
        return null;
    }
}
