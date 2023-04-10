package nurgling;

import haven.*;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static haven.ItemInfo.catimgs;
import static haven.ItemInfo.catimgsh;

public class NSearchWidget extends Widget {
    final NPopUpWidget history;
    public CmdList cmdList;
    TextEntry searchF = null;
    public static final Text.Foundry nfnd = new Text.Foundry(Text.dfont, 10);
    Window helpwnd;
    private static final BufferedImage[] searchbi = new BufferedImage[]{
            Resource.loadsimg("nurgling/hud/buttons/search/u"),
            Resource.loadsimg("nurgling/hud/buttons/search/d"),
            Resource.loadsimg("nurgling/hud/buttons/search/h")};
    private static final BufferedImage[] ssearchbi = new BufferedImage[]{
            Resource.loadsimg("nurgling/hud/buttons/ssearch/u"),
            Resource.loadsimg("nurgling/hud/buttons/ssearch/d"),
            Resource.loadsimg("nurgling/hud/buttons/ssearch/h")};

    private static final Tex[] lsearchbi = new Tex[]{
            Resource.loadtex("nurgling/hud/buttons/lsearch/u"),
            Resource.loadtex("nurgling/hud/buttons/lsearch/d"),
            Resource.loadtex("nurgling/hud/buttons/lsearch/h"),
            Resource.loadtex("nurgling/hud/buttons/lsearch/dh")};

    IButton help;
    IButton save;
    ICheckBox list;
    int tpos_y;

    public Widget create(UI ui, Object[] args) {
        return (new NSearchWidget((Coord) args[0]));
    }

    public NSearchWidget(Coord sz) {
        super(sz);
        searchF = new TextEntry(sz.x, "") {
            @Override
            public boolean keydown(KeyEvent e) {
                boolean res = super.keydown(e);
                NUtils.getGameUI().itemsForSearch.install(text());
                return res;
            }
        };

        help = new IButton(searchbi[0], searchbi[1], searchbi[2]) {
            @Override
            public void click() {
                super.click();
                helpwnd.show();
            }
        };
        help.settip(Resource.remote().loadwait("nurgling/hud/buttons/search/u").flayer(Resource.tooltip).t);
        save = new IButton(ssearchbi[0], ssearchbi[1], ssearchbi[2])
        {
            @Override
            public void click() {
                if(!searchF.text().isEmpty()) {
                    createHistoryItem(searchF.text());
                    write();
                    super.click();
                }else {
                    NUtils.getGameUI().error("Input field is empty");
                }
            }
        };
        save.settip(Resource.remote().loadwait("nurgling/hud/buttons/ssearch/u").flayer(Resource.tooltip).t);
        list = new ICheckBox(lsearchbi[0], lsearchbi[1], lsearchbi[2], lsearchbi[3])
        {
            @Override
            public void changed(boolean val) {
                super.changed(val);
            }
        };
        list.settip(Resource.remote().loadwait("nurgling/hud/buttons/lsearch/u").flayer(Resource.tooltip).t);
        tpos_y = searchF.sz.y / 2 - help.sz.y / 2;
        add(help, new Coord(0, tpos_y));
        add(save, new Coord(0, tpos_y));
        add(list, new Coord(0, tpos_y));
        add(searchF, new Coord(help.sz.x + UI.scale(5), 0));
        helpwnd = new Window(new Coord(UI.scale(200), UI.scale(500)), "Help: search") {
            @Override
            public void draw(GOut g) {
                super.draw(g);
                if (helpLayer != null)
                    g.aimage(helpLayer, ca().ul, 0, 0);

            }

            @Override
            public void resize(Coord sz) {
                super.resize(sz);
                if (helpLayer != null)
                    sz = new Coord(helpLayer.sz().x, helpLayer.sz().y);
            }

            @Override
            public void wdgmsg(Widget sender, String msg, Object... args) {
                if (sender == helpwnd) {
                    helpwnd.hide();
                }
            }

        };
        NUtils.getGameUI().add(helpwnd);

        initHelp();
        helpwnd.hide();
        history = NUtils.getGameUI().add(new NPopUpWidget(new Coord(UI.scale(200), UI.scale(150)), NPopUpWidget.Type.TOP));

        history.pack();
        cmdList = history.add(new NSearchWidget.CmdList(UI.scale(250, 200)),history.atl);
        read();
    }

    @Override
    public void resize(Coord sz) {
        searchF.resize(sz.x - UI.scale(5) * 3 - help.sz.x * 3);
        this.sz.y = searchF.sz.y;
        this.sz.x = sz.x;
        save.move(new Coord(sz.x - save.sz.x, tpos_y));
        list.move(new Coord(sz.x - save.sz.x - UI.scale(5) - list.sz.x, tpos_y));
        history.resize(new Coord(searchF.sz.x+UI.scale(12), UI.scale(150)));
        cmdList.resize(new Coord(0, UI.scale(120)));
    }

    TexI helpLayer;

    void initHelp() {
        ArrayList<BufferedImage> imgs = new ArrayList<>();
        String[] src = Resource.remote().loadwait("nurgling/hud/wnd/search").flayer(Resource.tooltip).t.split("\\|");
        for (String s : src)
            if (s.contains("$") && !s.contains("$col")) {
                imgs.add(nfnd.render(s).img);
            } else {
                imgs.add(RichText.render(s, 0).img);
            }

        helpLayer = new TexI(catimgs(5, imgs.toArray(new BufferedImage[0])));
        helpwnd.resize(new Coord(helpLayer.sz()));
    }

    @Override
    public void tick(double dt) {
        super.tick(dt);
        history.visible = parent.visible && list.a;
    }
    String path = ((HashDirCache) ResCache.global).base + "\\..\\" +"searchcmd.dat";
    void read() {

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
            while (reader.ready()) {
                String line = reader.readLine();
                createHistoryItem(line);
            }
            reader.close();
        } catch (IOException ignored) {
        }
    }

    void write() {
        try (FileWriter file = new FileWriter(path)) {
            for(String key : cmdHistory.keySet())
                file.write(key+"\n");
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ConcurrentHashMap<String,CmdItem> cmdHistory = new ConcurrentHashMap<>();
    private int cmdHistoryId = 0;
    public void createHistoryItem(String text)
    {
        CmdItem l = new CmdItem(text);
        cmdHistory.put(text,l);
        cmdList.makeitem(l, cmdHistoryId++, new Coord(40, 20));
    }
    private static final Text.Foundry elf = CharWnd.attrf;
    private static final int elh = elf.height() + UI.scale(2);

    private static final TexI[] removei = new TexI[]{
            new TexI(Resource.loadsimg("nurgling/hud/buttons/removeItem/u")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/removeItem/d")),
            new TexI(Resource.loadsimg("nurgling/hud/buttons/removeItem/h"))};

    public class CmdItem extends Widget{
        Label text;
        IButton remove;

        @Override
        public void resize(Coord sz) {
            remove.move(new Coord(sz.x - removei[0].sz().x - UI.scale(5),  remove.c.y));
            super.resize(sz);
        }

        public CmdItem(String text){
            this.text = add(new Label(text));
            remove = add(new IButton(removei[0].back,removei[1].back,removei[2].back){
                @Override
                public void click() {
                    cmdHistory.remove(text);
                    write();
                }
            },this.text.pos("ur").add(UI.scale(5),UI.scale(1) ));
            remove.settip(Resource.remote().loadwait("nurgling/hud/buttons/removeItem/u").flayer(Resource.tooltip).t);

            pack();
        }
    }

    public class CmdList extends SListBox<CmdItem, Widget> {
        CmdList(Coord sz) {
            super(sz, elh);
        }

        protected List<CmdItem> items() {return(new ArrayList<>(cmdHistory.values()));}


        @Override
        public void resize(Coord sz) {
            super.resize(new Coord(searchF.sz.x-UI.scale(6), sz.y));
        }

        protected Widget makeitem(CmdItem item, int idx, Coord sz) {
            return(new ItemWidget<CmdItem>(this, sz, item) {
                {
                    item.resize(new Coord(searchF.sz.x - removei[0].sz().x  + UI.scale(4), item.sz.y));
                    add(item);
                }

                public boolean mousedown(Coord c, int button) {
                    boolean psel = sel == item;
                    super.mousedown(c, button);
                    if(!psel) {
                        String value = item.text.text.text;
                        searchF.settext(value);
                        NUtils.getGameUI().itemsForSearch.install(value);
                    }
                    return(true);
                }
            });
        }
    }
}

