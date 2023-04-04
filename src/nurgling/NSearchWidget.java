package nurgling;

import haven.*;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;

import static haven.ItemInfo.catimgs;
import static haven.ItemInfo.catimgsh;

public class NSearchWidget extends Widget {
    TextEntry searchF = null;

    Window helpwnd;
    private static final BufferedImage[] searchbi = new BufferedImage[] {
            Resource.loadsimg("nurgling/hud/buttons/searchu"),
            Resource.loadsimg("nurgling/hud/buttons/searchd"),
            Resource.loadsimg("nurgling/hud/buttons/searchh")};
    private static final BufferedImage[] ssearchbi = new BufferedImage[] {
            Resource.loadsimg("nurgling/hud/buttons/ssearchu"),
            Resource.loadsimg("nurgling/hud/buttons/ssearchd"),
            Resource.loadsimg("nurgling/hud/buttons/ssearchh")};

    private static final Tex[] lsearchbi = new Tex[] {
            Resource.loadtex("nurgling/hud/buttons/lsearchu"),
            Resource.loadtex("nurgling/hud/buttons/lsearchd"),
            Resource.loadtex("nurgling/hud/buttons/lsearchh"),
            Resource.loadtex("nurgling/hud/buttons/lsearchdh")};

    IButton help;
    IButton save;
   ICheckBox list;
    int tpos_y;

    public Widget create(UI ui, Object[] args) {
        return(new NSearchWidget((Coord)args[0]));
    }

    public NSearchWidget(Coord sz) {
        super(sz);
        searchF = new TextEntry(sz.x,""){
            @Override
            public boolean keydown(KeyEvent e) {
                boolean res = super.keydown(e);
                NUtils.getGameUI().itemsForSearch.install(text());
                return res;
            }
        };

        help = new IButton(searchbi[0], searchbi[1], searchbi[2]){
            @Override
            public void click() {
                super.click();
                helpwnd.show();
            }
        };
        save = new IButton(ssearchbi[0], ssearchbi[1], ssearchbi[2]);
        list = new ICheckBox(lsearchbi[0], lsearchbi[1], lsearchbi[2],lsearchbi[3]);
        tpos_y = searchF.sz.y/2-help.sz.y/2;
        add(help,new Coord(0,tpos_y));
        add(save,new Coord(0,tpos_y));
        add(list,new Coord(0,tpos_y));
        add(searchF,new Coord(help.sz.x+UI.scale(5), 0));
        helpwnd = new Window(new Coord(UI.scale(200),UI.scale(500)),"Help: search")
        {
            @Override
            public void draw(GOut g) {
                super.draw(g);
                if(helpLayer!=null)
                    g.aimage(helpLayer,atl, 0,0);

            }

            @Override
            public void resize(Coord sz) {
                super.resize(sz);
                if(helpLayer!=null)
                    sz = new Coord(helpLayer.sz().x,helpLayer.sz().y);
            }

            public void cdestroy(Widget w) {
                if(w == helpwnd) {
                    this.hide();
                }
            }

        };
        NUtils.getGameUI().add(helpwnd);

        initHelp();
        helpwnd.hide();
    }

    @Override
    public void resize(Coord sz) {
        searchF.resize(sz.x - UI.scale(5)*3 - help.sz.x*3);
        this.sz.y = searchF.sz.y;
        this.sz.x = sz.x;
        save.move(new Coord(sz.x - save.sz.x, tpos_y));
        list.move(new Coord(sz.x - save.sz.x - UI.scale(5) - list.sz.x, tpos_y));
    }

    TexI helpLayer;
    void initHelp()
    {
        ArrayList<BufferedImage> imgs =new ArrayList<>();
        String[] src = Resource.remote().loadwait("nurgling/hud/wnd/search").flayer(Resource.tooltip).t.split("\\|");
        for (String s : src)
            imgs.add(RichText.render(s,0).img);
        helpLayer = new TexI(catimgs(5, imgs.toArray(new BufferedImage[0])));
        helpwnd.resize(new Coord(helpLayer.sz()));
    }
}
