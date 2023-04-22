package nurgling;

import haven.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.util.Collections.swap;

public class NAutoPickMenu extends Widget {
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
    public PickList list;
    TextEntry new_item;
    Button addbtn;
    public NAutoPickMenu() {
        prev = add(new Label("List of auto-select:"));
        list = add(new PickList(UI.scale(250, 200)),prev.pos("bl").add(0,UI.scale(5)));
        new_item = add(new TextEntry(250,""),list.pos("bl").add(0,UI.scale(10)));
        addbtn = add(new Button(75,"Add"){
            @Override
            public void click() {
                super.click();
                pickList.add(new PickItem(new_item.text()));
                NConfiguration.getInstance().setPickActions(pickList);
                NConfiguration.getInstance().write();
                int len = 0;
                for(NAutoPickMenu.PickItem pL : pickList)
                {
                    len = Math.max(len,pL.sz.x);
                }
                NAutoPickMenu.this.resize(len+UI.scale(10),NAutoPickMenu.this.sz.y);
                NAutoPickMenu.this.parent.sz.x = len+UI.scale(10);
            }
        },new_item.pos("ur").add(UI.scale(5),UI.scale(10)));
        pack();
    }

    public void readItem(String name, boolean isChecked){
        PickItem p = new PickItem(name);
        pickList.add(p);
        p.select.a = isChecked;
        int len = 0;
        for(PickItem pL : pickList)
        {
            len = Math.max(len,pL.sz.x);
        }
        this.parent.resize(len+UI.scale(5),p.sz.y*pickList.size());
        for(PickItem pL : pickList)
        {
            pL.resize(len,p.sz.y);
        }
    }

    public final LinkedList<PickItem> pickList = new LinkedList<>();

    public class PickItem extends Widget{
        Label text;
        IButton up;
        IButton down;
        ICheckBox select;
        IButton remove;

        @Override
        public void resize(Coord sz) {
            up.move(new Coord(sz.x - 3* (removei[0].sz().x + UI.scale(5)),  remove.c.y));
            down.move(new Coord(sz.x - 2*(removei[0].sz().x + UI.scale(5)),  remove.c.y));
            remove.move(new Coord(sz.x - removei[0].sz().x - UI.scale(5),  remove.c.y));
            super.resize(sz);
        }

        public PickItem(String text){
            select = add(new ICheckBox(checkboxi[0], checkboxi[1], checkboxi[2], checkboxi[3]) {
                @Override
                public void changed(boolean val) {
                    super.changed(val);
                    NConfiguration.getInstance().setPickActions(pickList);
                }
            }, new Coord(0, UI.scale(4)));
            this.text = add(new Label(text),this.select.pos("ur").add(UI.scale(5),UI.scale(1) ) );

            up = add(new IButton(upi[0].back,upi[1].back,upi[2].back){
                @Override
                public void click() {
                    int i = pickList.indexOf(PickItem.this);
                    if(i>0) {
                        swap(pickList, i-1, i);
                        NConfiguration.getInstance().setPickActions(pickList);
                        NConfiguration.getInstance().write();
                    }
                }
            },this.text.pos("ur").add(UI.scale(5),0 ));
            up.settip(Resource.remote().loadwait("nurgling/hud/buttons/upItem/u").flayer(Resource.tooltip).t);

            down = add(new IButton(downi[0].back,downi[1].back,downi[2].back){
                @Override
                public void click() {
                    int i = pickList.indexOf(PickItem.this);
                    if(i<pickList.size()-1) {
                        swap(pickList, i, i+1);
                        NConfiguration.getInstance().setPickActions(pickList);
                        NConfiguration.getInstance().write();
                    }
                }
            },this.up.pos("ur").add(UI.scale(5),0 ));
            down.settip(Resource.remote().loadwait("nurgling/hud/buttons/downItem/u").flayer(Resource.tooltip).t);

            remove = add(new IButton(removei[0].back,removei[1].back,removei[2].back){
                @Override
                public void click() {
                    pickList.remove(PickItem.this);
                    NConfiguration.getInstance().setPickActions(pickList);
                    NConfiguration.getInstance().write();
                }
            },this.down.pos("ur").add(UI.scale(5),0 ));
            remove.settip(Resource.remote().loadwait("nurgling/hud/buttons/removeItem/u").flayer(Resource.tooltip).t);

            pack();
        }
    }

    public class PickList extends SListBox<PickItem, Widget> {
        PickList(Coord sz) {
            super(sz, elh);
            pack();
        }

        protected List<PickItem> items() {return(new ArrayList<>(pickList));}

        protected Widget makeitem(PickItem item, int idx, Coord sz) {
            return(new ItemWidget<PickItem>(this, sz, item) {
                {
                    int len = 0;
                    int h = 0;
                    for(PickItem pL : pickList)
                    {
                        len = Math.max(len,pL.sz.x);
                        h = pL.sz.y;
                    }
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
