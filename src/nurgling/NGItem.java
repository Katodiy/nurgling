package nurgling;

import haven.*;
import haven.res.ui.tt.defn.DefName;

public class NGItem extends GItem {
    int old_infoseq;

    public static int HAVE_CONTENT = 0x08;
    public static int SPR_IS_READY = 0x04;
    public static int NAME_IS_READY = 0x02;
    public static int RAWINFO_IS_READY = 0x01;

    private int status = 0;
    private double quality = -1;
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
    private String defn = null;

    public NGItem(Indir<Resource> res, Message sdt) {
        super(res, sdt);
        old_infoseq = infoseq;
    }

    @Override
    public void tick(double dt) {
        super.tick(dt);
        if(infoseq!=old_infoseq)
        {
            if(rawinfo!= null) {
                status &= ~RAWINFO_IS_READY;
                status &= ~HAVE_CONTENT;
                for (Object o : rawinfo.data) {
                    if (o instanceof Object[]) {
                        Object[] a = (Object[]) o;
                        if (a[0] instanceof Integer) {
                            String resName = glob().sess.getResName((Integer) a[0]);
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
                                                        String resName2 = glob().sess.getResName((Integer) b[0]);
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
                defn = DefName.getname(this);
                if(defn!=null) {
                    status |= NAME_IS_READY;
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
        if((status & SPR_IS_READY) == SPR_IS_READY) {
            for (ItemInfo inf : info()) {
                if (inf instanceof NFoodInfo) {
                    return ((NFoodInfo) inf).check();
                }
            }
        }
        return false;
    }

    public boolean needlongtip() {
        if((status & SPR_IS_READY) == SPR_IS_READY) {
            for (ItemInfo inf : info()) {
                if (inf instanceof NFoodInfo) {
                    return ((NFoodInfo) inf).needToolTip;
                }
            }
        }
        return false;
    }
}
