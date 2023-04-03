package nurgling;

import haven.*;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Comparator;

public class NGameUI extends GameUI {
    public SearchItem itemsForSearch = null;

    public class SearchItem
    {
        String name ="";
        public class Fep{
            public String v;
            public double a;
            boolean isMore = false;

            public Fep(String v, double a, boolean isMore) {
                this.v = v;
                this.a = a;
                this.isMore = isMore;
            }

            public Fep(String v) {
                this.v = v;
                a = 0;
            }
        }
        final ArrayList<Fep> food = new ArrayList<>();
        final ArrayList<String> gilding = new ArrayList<>();
        boolean fgs = false;
        private void reset()
        {
            food.clear();
            gilding.clear();
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
                                        food.add(new Fep (val.substring(pos+1)));
                                    }
                                    else{
                                        int endpos = Math.max(minpos,maxpos);
                                        if(val.length()>endpos+1)
                                        {
                                            try {
                                                food.add(new Fep(val.substring(pos+1,endpos),Double.parseDouble(val.substring(endpos+1)),maxpos>minpos));
                                            }catch (NumberFormatException e)
                                            {
                                                food.add(new Fep (val.substring(pos+1,endpos)));
                                            }
                                        }
                                        else
                                        {
                                            food.add(new Fep (val.substring(pos+1,endpos)));
                                        }
                                    }
                                }
                            } else if (val.startsWith("$gild")) {
                                if (val.contains(":"))
                                    gilding.add(val.substring(pos+1));
                            }
                        }
                        if (val.startsWith("$fgs")) {
                            fgs = true;
                        }
                    }
                } else {
                    name = value.toLowerCase();
                }
            }
        }

        public boolean isEmpty() {
            synchronized (gilding) {
                return name.isEmpty() && !fgs && gilding.isEmpty() && food.isEmpty();
            }
        }

        public boolean onlyName() {
            synchronized (gilding) {
                return !name.isEmpty() && !fgs && gilding.isEmpty() && food.isEmpty();
            }
        }
    }
    TextEntry searchF = null;
    public NGameUI(String chrid, long plid, String genus) {
        super(chrid, plid, genus);
        NUtils.getUI().sessInfo.characterInfo = new NCharacterInfo(chrid);
        itemsForSearch = new SearchItem();
        add(NUtils.getUI().sessInfo.characterInfo);
        pack();
        NUtils.setGameUI(this);
        NFoodInfo.init();
    }

    public NCharacterInfo getCharInfo() {
        return ((NUI)ui).sessInfo.characterInfo;
    }

    public void addchild(Widget child, Object... args) {
        super.addchild(child,args);
        String place = ((String) args[0]).intern();
        if (place.equals("chr") && chrwdg!=null) {
            ((NUI)ui).sessInfo.characterInfo.setCharWnd(chrwdg);
        }
        if(maininv!=null && searchF == null)
        {
            searchF = new TextEntry(UI.scale(200),""){
                @Override
                public boolean keydown(KeyEvent e) {
                    boolean res = super.keydown(e);
                    NUtils.getGameUI().itemsForSearch.install(text());
                    return res;
                }
            };
            Window window = maininv.getparent(Window.class);
            window.add(searchF,(new Coord(0,window.sz.y)));
            window.pack();
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
