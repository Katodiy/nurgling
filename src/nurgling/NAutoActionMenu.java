package nurgling;

import haven.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NAutoActionMenu extends Widget {
    private static final Text.Foundry elf = CharWnd.attrf;
    private static final int elh = elf.height() + UI.scale(2);

    private static final TexI[] removei = new TexI[]{
            new TexI(Resource.loadsimg("nurgling/hud/buttons/removeItem/u")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/removeItem/d")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/removeItem/h"))};

    private static final TexI[] upi = new TexI[]{
            new TexI(Resource.loadsimg("nurgling/hud/buttons/upItem/u")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/upItem/d")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/upItem/h"))};

    private static final TexI[] downi = new TexI[]{
            new TexI(Resource.loadsimg("nurgling/hud/buttons/downItem/u")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/downItem/d")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/downItem/h"))};

    private static final TexI[] checkboxi = new TexI[]{
            new TexI(Resource.loadsimg("nurgling/hud/buttons/checkbox/u")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/checkbox/d")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/checkbox/h")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/checkbox/dh"))};
    public PatternList list;
    TextEntry new_item;
    Button addbtn;
    public NAutoActionMenu() {
        prev = add(new Label("List of gobs for quick action:"));
        list = add(new PatternList(UI.scale(250, 200)),prev.pos("bl").add(0,UI.scale(5)));
        new_item = add(new TextEntry(UI.scale(250-85),""),list.pos("bl").add(0,UI.scale(10)));
        addbtn = add(new Button(UI.scale(75),"Add"){
            @Override
            public void click() {
                super.click();
                patternList.add(new PatternItem(new_item.text()));
                NConfiguration.getInstance().setQuickActions(patternList);
                NConfiguration.getInstance().write();
                int len = 0;
                for(PatternItem pL : patternList)
                {
                    len = Math.max(len,pL.sz.x-UI.scale(10));
                }
                len = Math.max(len,UI.scale(240));
                NAutoActionMenu.this.resize(len+UI.scale(10), NAutoActionMenu.this.sz.y);
            }
        },new_item.pos("ur").add(UI.scale(5),UI.scale(10)));
        Label quick_rad_lab = add(new Label("Quick radius:"),new_item.pos("bl").add(0,UI.scale(5)) );
        HSlider hs = (HSlider)(prev = add(new HSlider(UI.scale(160), 10, 200,1) {
            public void changed() {
                quick_rad_lab.settext("Quick radius: " + String.valueOf(val));
                NConfiguration.getInstance().quickRange = val;
            }
        }, quick_rad_lab.pos("bl").add(0,UI.scale(5))));
        hs.val = NConfiguration.getInstance().quickRange;
        quick_rad_lab.settext("Quick radius: " + String.valueOf(hs.val));
        pack();
    }

    public void readItem(String name){
        PatternItem p = new PatternItem(name);
        patternList.add(p);
        int len = 0;
        for(PatternItem pL : patternList)
        {
            len = Math.max(len,pL.sz.x);
        }
//        this.resize(len+UI.scale(5),p.sz.y* patternList.size());
        for(PatternItem pL : patternList)
        {
            pL.resize(len,p.sz.y);
        }
    }

    public final LinkedList<PatternItem> patternList = new LinkedList<>();

    public class PatternItem extends Widget{
        Label text;
        ICheckBox select;
        IButton remove;

        @Override
        public void resize(Coord sz) {
            super.resize(sz);
            remove.move(new Coord(sz.x - removei[0].sz().x - UI.scale(15),  remove.c.y));
        }

        public PatternItem(String text){
            select = add(new ICheckBox(checkboxi[0], checkboxi[1], checkboxi[2], checkboxi[3]) {
                @Override
                public void changed(boolean val) {
                    super.changed(val);
                }
            }, new Coord(0, UI.scale(4)));
            this.text = add(new Label(text),this.select.pos("ur").add(UI.scale(5),UI.scale(1) ) );

            remove = add(new IButton(removei[0].back,removei[1].back,removei[2].back){
                @Override
                public void click() {
                    patternList.remove(PatternItem.this);
                    NConfiguration.getInstance().setQuickActions(patternList);
                    NConfiguration.getInstance().write();
                }
            },this.text.pos("ur").add(UI.scale(5),0 ));
            remove.settip(Resource.remote().loadwait("nurgling/hud/buttons/removeItem/u").flayer(Resource.tooltip).t);

            pack();
        }
    }

    public class PatternList extends SListBox<PatternItem, Widget> {
        PatternList(Coord sz) {
            super(sz, elh);
            pack();
        }

        protected List<PatternItem> items() {return(new ArrayList<>(patternList));}

        protected Widget makeitem(PatternItem item, int idx, Coord sz) {
            return(new ItemWidget<PatternItem>(this, sz, item) {
                {
                    int len = 0;
                    int h = 0;
                    for(PatternItem pL : patternList)
                    {
                        len = Math.max(len,pL.sz.x);
                        h = pL.sz.y;
                    }
                    len = Math.max(len,UI.scale(250));
                    item.resize(new Coord(len,h));
                    add(item);
                }

                public boolean mousedown(Coord c, int button) {
                    boolean psel = sel == item;
                    super.mousedown(c, button);
                    return(true);
                }
            });
        }
    }

    @Override
    public void resize(Coord sz) {
        super.resize(sz);
        list.resize(new Coord(sz.x, list.sz.y));
        new_item.resize(new Coord(sz.x - addbtn.sz.x-UI.scale(5), new_item.sz.y));
        addbtn.move(new_item.pos("ur").add(UI.scale(5),-UI.scale(6)));
    }
}
