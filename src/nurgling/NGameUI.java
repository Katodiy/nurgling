package nurgling;

import haven.*;
import haven.res.ui.tt.slot.Slotted;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;

public class NGameUI extends GameUI {
    public SearchItem itemsForSearch = null;
    public NSearchWidget searchwdg;
    public static class SearchItem
    {
        public String name ="";

        public static class Quality{
            public double val;
            public Type type;

            public Quality(double val, Type type) {
                this.val = val;
                this.type = type;
            }

            public enum Type{
                MORE,
                LOW,
                EQ
            }
        }

        public ArrayList<Quality> q= new ArrayList<>();
        public class Stat{
            public String v;
            public double a;
            public boolean isMore = false;

            public Stat(String v, double a, boolean isMore) {
                this.v = v;
                this.a = a;
                this.isMore = isMore;
            }

            public Stat(String v) {
                this.v = v;
                a = 0;
            }
        }

        public final ArrayList<Stat> food = new ArrayList<>();
        public final ArrayList<Stat> gilding = new ArrayList<>();
        boolean fgs = false;
        private void reset()
        {
            food.clear();
            gilding.clear();
            q.clear();
            fgs = false;
            name = "";
        }
        void install(String value)
        {
            synchronized (gilding) {
                reset();
                if (value.startsWith("$")) {
                    String[] items = value.split("\\s*;\\s*");
                    for (String val : items) {
                        int pos = val.indexOf(":");
                        if(val.length()>pos+1 && pos!=-1) {
                            if (val.startsWith("$name")) {
                               name = val.substring(pos+1).toLowerCase();
                            } else if (val.startsWith("$fep")) {
                                if (val.contains(":"))
                                {
                                    int minpos = val.indexOf("<");
                                    int maxpos = val.indexOf(">");
                                    if(minpos==maxpos)
                                    {
                                        food.add(new Stat (val.substring(pos+1)));
                                    }
                                    else{
                                        int endpos = Math.max(minpos,maxpos);
                                        if(val.length()>endpos+1)
                                        {
                                            try {
                                                food.add(new Stat(val.substring(pos+1,endpos),Double.parseDouble(val.substring(endpos+1)),maxpos>minpos));
                                            }catch (NumberFormatException e)
                                            {
                                                food.add(new Stat (val.substring(pos+1,endpos)));
                                            }
                                        }
                                        else
                                        {
                                            food.add(new Stat (val.substring(pos+1,endpos)));
                                        }
                                    }
                                }
                            } else if (val.startsWith("$gild")) {
                                if (val.contains(":"))
                                {
                                    int minpos = val.indexOf("<");
                                    int maxpos = val.indexOf(">");
                                    if(minpos==maxpos)
                                    {
                                        gilding.add(new Stat (val.substring(pos+1)));
                                    }
                                    else{
                                        int endpos = Math.max(minpos,maxpos);
                                        if(val.length()>endpos+1)
                                        {
                                            try {
                                                gilding.add(new Stat(val.substring(pos+1,endpos),Double.parseDouble(val.substring(endpos+1)),maxpos>minpos));
                                            }catch (NumberFormatException e)
                                            {
                                                gilding.add(new Stat (val.substring(pos+1,endpos)));
                                            }
                                        }
                                        else
                                        {
                                            gilding.add(new Stat (val.substring(pos+1,endpos)));
                                        }
                                    }
                                }
                            }
                        }
                        if (val.startsWith("$fgs")) {
                            fgs = true;
                        }
                        else if(val.startsWith("$q"))
                        {
                            int minpos = val.indexOf("<");
                            int maxpos = val.indexOf(">");
                            int eqpos = val.indexOf("=");
                            try {
                            if(minpos!=-1 && val.length()>minpos+1){
                                    double d = Double.parseDouble(val.substring(minpos+1));
                                    q.add(new Quality(d, Quality.Type.LOW));
                            }
                            else if(maxpos!=-1 && val.length()>maxpos+1){
                                double d = Double.parseDouble(val.substring(maxpos+1));
                                q.add(new Quality(d, Quality.Type.MORE));
                            }
                            else if(eqpos!=-1 && val.length()>eqpos+1){
                                double d = Double.parseDouble(val.substring(eqpos+1));
                                q.add(new Quality(d, Quality.Type.EQ));
                            }
                            }
                            catch (NumberFormatException ignored)
                            {
                            }
                        }
                    }
                } else {
                    name = value.toLowerCase();
                }
            }
        }

        public boolean isEmpty() {
            synchronized (gilding) {
                return name.isEmpty() && !fgs && gilding.isEmpty() && food.isEmpty() && q.isEmpty();
            }
        }

        public boolean onlyName() {
            synchronized (gilding) {
                return !name.isEmpty() && !fgs && gilding.isEmpty() && food.isEmpty() && q.isEmpty();
            }
        }
    }

    public NGameUI(String chrid, long plid, String genus) {
        super(chrid, plid, genus);
        NUtils.getUI().sessInfo.characterInfo = new NCharacterInfo(chrid);
        itemsForSearch = new SearchItem();
        add(NUtils.getUI().sessInfo.characterInfo);
        pack();
        NUtils.setGameUI(this);
    }

    public NCharacterInfo getCharInfo() {
        return ((NUI)ui).sessInfo.characterInfo;
    }

    private static final TexI[] collapsei = new TexI[]{
            new TexI(Resource.loadsimg("nurgling/hud/buttons/itogglecu")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/itogglecd")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/itogglech")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/itogglecdh"))};

    private static final TexI[] gildingi = new TexI[]{
            new TexI(Resource.loadsimg("nurgling/hud/buttons/gilding/u")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/gilding/d")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/gilding/h")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/gilding/dh"))};

    private static final TexI[] ssearchbi = new TexI[]{
            new TexI(Resource.loadsimg("nurgling/hud/buttons/ssearchu")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/ssearchd")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/ssearchh"))};

    public void addchild(Widget child, Object... args) {
        super.addchild(child, args);
        String place = ((String) args[0]).intern();
        if (place.equals("chr") && chrwdg != null) {
            ((NUI) ui).sessInfo.characterInfo.setCharWnd(chrwdg);
        }
        if (maininv != null && searchwdg == null) {

            NInventory window = (NInventory) maininv;
            searchwdg = new NSearchWidget(new Coord(window.sz));
            window.searchwdg = searchwdg;
            searchwdg.resize(window.sz);
            window.parent.add(searchwdg, (new Coord(0, window.sz.y + UI.scale(10))));
            window.parent.add(new ICheckBox(collapsei[0], collapsei[1], collapsei[2], collapsei[3]) {
                                  @Override
                                  public void changed(boolean val) {
                                      super.changed(val);
                                      window.showPopup = val;
                                  }
                              }
                    , new Coord(-gildingi[0].sz().x + UI.scale(2), UI.scale(27)));

            window.parent.pack();
            NPopUpWidget p = add(new NPopUpWidget(new Coord(UI.scale(50), UI.scale(80))));
            window.popup = p;

            Widget pw = p.add(new ICheckBox(gildingi[0], gildingi[1], gildingi[2], gildingi[3]) {
                                  @Override
                                  public void changed(boolean val) {
                                      super.changed(val);
                                      Slotted.show = val;
                                  }
                              }
                    , p.atl);
            pw = p.add(new IButton(ssearchbi[0].back, ssearchbi[1].back, ssearchbi[2].back), pw.pos("bl").add(UI.scale(new Coord(0, 5))));
            pw = p.add(new IButton(ssearchbi[0].back, ssearchbi[1].back, ssearchbi[2].back), pw.pos("bl").add(UI.scale(new Coord(0, 5))));
            pw = p.add(new IButton(ssearchbi[0].back, ssearchbi[1].back, ssearchbi[2].back), pw.pos("bl").add(UI.scale(new Coord(0, 5))));
            pw = p.add(new IButton(ssearchbi[0].back, ssearchbi[1].back, ssearchbi[2].back), pw.pos("bl").add(UI.scale(new Coord(0, 5))));
            pw = p.add(new IButton(ssearchbi[0].back, ssearchbi[1].back, ssearchbi[2].back), pw.pos("bl").add(UI.scale(new Coord(0, 5))));
            window.popup.pack();
            window.movePopup(window.parent.c);
            p.pack();
        }
    }

    public Window getWindow ( String cap ) {
        for ( Widget w = lchild ; w != null ; w = w.prev ) {
            if ( w instanceof Window ) {
                Window wnd = ( Window ) w;
                if ( wnd.cap != null && wnd.cap.text.equals(cap)) {
                    return ( Window ) w;
                }
            }
        }
        return null;
    }

    public Window getWindowWithButton ( String cap, String button ) {
        for ( Widget w = lchild ; w != null ; w = w.prev ) {
            if ( w instanceof Window ) {
                Window wnd = ( Window ) w;
                if ( wnd.cap != null && wnd.cap.text.equals(cap)) {
                    for(Widget w2 = wnd.lchild ; w2 !=null ; w2= w2.prev )
                    {
                        if ( w2 instanceof Button ) {
                            Button b = ((Button)w2);
                            if(b.text!=null && b.text.text.equals(button)){
                                return (Window)w;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public double getTableMod() {
        double table_mod = 1;
        Window table = getWindowWithButton("Table", "Feast!");
        if(table!=null)
        {
            for (Widget wdg = table.child; wdg != null; wdg = wdg.next) {
                if (wdg instanceof Label) {
                    Label text = (Label) wdg;
                    if (text.texts.contains("Food")) {
                        table_mod = table_mod + Double.parseDouble(text.texts.substring(text.texts.indexOf(":") + 1, text.texts.indexOf("%"))) / 100.;
                        break;
                    }
                }
            }
        }
        return table_mod;
    }

    public double getRealmMod() {
        double realmBuff = 0;
        for (Widget wdg = child; wdg != null; wdg = wdg.next) {
            if (wdg instanceof GameUI.Hidepanel) {
                for (Widget wdg1 = wdg.child; wdg1 != null; wdg1 = wdg1.next) {
                    if (wdg1 instanceof Bufflist) {
                        for (Widget pbuff = wdg1.child; pbuff != null; pbuff = pbuff.next) {
                            if (pbuff instanceof Buff) {
                                if (NUtils.checkName(((Buff) pbuff).res.get().name, new NAlias("realm"))) {
                                    ArrayList<ItemInfo> realm = new ArrayList<>(((Buff) pbuff).info());
                                    for (Object data : realm) {
                                        if (data instanceof ItemInfo.AdHoc) {
                                            ItemInfo.AdHoc ah = ((ItemInfo.AdHoc) data);
                                            if (NUtils.checkName(ah.str.text, new NAlias("Food event"))) {
                                                realmBuff = realmBuff + Double.parseDouble(ah.str.text.substring(ah.str.text.indexOf("+") + 1, ah.str.text.indexOf("%"))) / 100.;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return realmBuff;
    }

    public int getMaxBase(){
        return chrwdg.base.stream().max(new Comparator<CharWnd.Attr>() {
            @Override
            public int compare(CharWnd.Attr o1, CharWnd.Attr o2) {
                return Integer.compare(o1.attr.base,o2.attr.base);
            }
        }).get().attr.base;
    }
}
