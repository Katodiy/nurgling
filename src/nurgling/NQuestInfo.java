package nurgling;

import haven.*;
import sun.awt.Mutex;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static haven.ItemInfo.catimgsh;

public class NQuestInfo extends NDraggableWidget {
    Text.Furnace gfnd2 = new PUtils.BlurFurn(new Text.Foundry(Text.sans, 14, Color.white).aa(true), 2, 1, Color.BLACK);
    Text.Furnace gfnd2_under = new PUtils.BlurFurn(new Text.Foundry(Text.sans, 14, new Color(222, 205, 171)).aa(true), 2, 1, Color.BLACK);
    public static final RichText.Foundry fnd1 = new RichText.Foundry(new ChatUI.ChatParser(TextAttribute.FONT, Text.dfont.deriveFont(UI.scale(18f)), TextAttribute.FOREGROUND, Color.YELLOW));
    Text.Furnace active_title = new PUtils.BlurFurn(new Text.Foundry(Text.sans, 18, new Color(217, 127, 59)).aa(true), 2, 1, new Color(94, 56, 56));
    Text.Furnace unactive_title = new PUtils.BlurFurn(new Text.Foundry(Text.sans, 18, new Color(147, 131, 131)).aa(true), 2, 1, new Color(94, 56, 56));
    Text.Furnace credo_title = new PUtils.BlurFurn(new Text.Foundry(Text.sans, 18, new Color(126, 198, 194)).aa(true), 2, 1, new Color(94, 56, 56));

    public static final Tex[] gear = new Tex[]{Resource.loadtex("nurgling/hud/gear"),
            Resource.loadtex("nurgling/hud/gear1"),
            Resource.loadtex("nurgling/hud/gear2"),
            Resource.loadtex("nurgling/hud/gear3"),
            Resource.loadtex("nurgling/hud/gear4"),
            Resource.loadtex("nurgling/hud/gear5"),
            Resource.loadtex("nurgling/hud/gear6"),
            Resource.loadtex("nurgling/hud/gear7"),
            Resource.loadtex("nurgling/hud/gear8"),
            Resource.loadtex("nurgling/hud/gear9"),
            Resource.loadtex("nurgling/hud/gear10"),
            Resource.loadtex("nurgling/hud/gear11"),
            Resource.loadtex("nurgling/hud/gear12"),
            Resource.loadtex("nurgling/hud/gear13")};

    private final int dy;
    public static void selectedQuest() {
        if ( isAvailable() && (!questers.isEmpty() || credo!=null)) {
            synchronized (new_questers) {
                try {
                    NUtils.waitEvent(() -> NUtils.getGameUI().chrwdg.quest != null && ((CharWnd.Quest.DefaultBox) NUtils.getGameUI().chrwdg.quest).cond != null && ((CharWnd.Quest.DefaultBox) NUtils.getGameUI().chrwdg.quest).cond.length > 0, 200);

                    String qtitle = ((CharWnd.Quest.DefaultBox) NUtils.getGameUI().chrwdg.quest).title;
                    if (qtitle == null) {
                        NUtils.waitEvent(() -> ((CharWnd.Quest.DefaultBox) NUtils.getGameUI().chrwdg.quest).title() != null, 50);
                        qtitle = ((CharWnd.Quest.DefaultBox) NUtils.getGameUI().chrwdg.quest).title();
                    }
                    if (qtitle != null) {
                        for (Quester quester : questers.values()) {
                            for (String name : quester.main_quests.keySet()) {
                                if (name.contains(qtitle)) {
                                    quester.main_quests.get(name).conditions = ((CharWnd.Quest.DefaultBox) NUtils.getGameUI().chrwdg.quest).cond;
                                    break;
                                }
                            }
                            for (String name : quester.linked_quests.keySet()) {
                                if (name != null && name.contains(qtitle)) {
                                    quester.linked_quests.get(name).conditions = ((CharWnd.Quest.DefaultBox) NUtils.getGameUI().chrwdg.quest).cond;
                                    break;
                                }
                            }
                        }
                        if (credo != null && credo.name != null) {
                            if (credo.name.contains(qtitle)) {
                                credo.main_quests.get(qtitle).conditions = ((CharWnd.Quest.DefaultBox) NUtils.getGameUI().chrwdg.quest).cond;
                            }
                        }
                    }
                    needUpdate = true;
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    public static class QuestGob {
        public boolean isFound = false;
        public String name;

        public QuestGob(MapFile.Marker marker) {
            this.marker = marker;
            name = marker.name();
        }

        public MapFile.Marker marker;

        Set<NGob.Tags> tagsSet = new HashSet<>();
    }

    public static Set<NGob.Tags> tagsSet(String name){
        if(isAvailable())
            synchronized (markers) {
                return markers.get(name).tagsSet;
            }
        return null;
    }

    private static final HashMap<String, QuestGob> markers = new HashMap<>();



    public static void setMarker(String name, MapFile.Marker marker) {
        synchronized (markers) {
            markers.put(name, new QuestGob(marker));
        }
    }

    public static void updateTags(String name){
        if(isAvailable())
        {
            Quester q = questers.get(name);
            if(q!=null){
                synchronized (markers) {
                    QuestGob gob = markers.get(name);
                    if (gob != null) {
                        gob.tagsSet.clear();
                        checkTasks(q.main_quests, gob);
                        checkTasks(q.linked_quests, gob);
                        if (q.ended > 0)
                            gob.tagsSet.add(NGob.Tags.qcompleted);
                    }
                }
            }
        }
    }

    private static void checkTasks(HashMap<String, Quester.Quest> quests, QuestGob gob) {
        for(Quester.Quest quest: quests.values()){
            for(CharWnd.Quest.Condition c : quest.conditions){
                if(c.done==0 && c.desc.contains(gob.name)) {
                    if (c.desc.contains("Bring")) {
                        gob.tagsSet.add(NGob.Tags.qbring);
                    } else if (c.desc.contains("rage")) {
                        gob.tagsSet.add(NGob.Tags.qrage);
                    } else if (c.desc.contains("wave")) {
                        gob.tagsSet.add(NGob.Tags.qwave);
                    } else if (c.desc.contains("laugh")) {
                        gob.tagsSet.add(NGob.Tags.qlaugh);
                    } else if (c.desc.contains("Greet")) {
                        gob.tagsSet.add(NGob.Tags.qgreet);
                    }
                }
            }
        }
    }

    public static HashMap<String, QuestGob> getMarkers() {
        synchronized (markers) {
            return markers;
        }
    }


    public static class Quester {
        public String name;
        int ended = 0;

        public Quester(String name) {
            this.name = name;
        }

        public static class Quest {

            CharWnd.Quest.Condition[] conditions;

            int id;

            public Quest(CharWnd.Quest.Condition[] cond, int id) {
                conditions = cond;
                this.id = id;
            }
        }

        HashMap<String, Quest> main_quests = new HashMap<>();
        HashMap<String, Quest> linked_quests = new HashMap<>();
    }

    public static boolean needUpdate = false;
    private boolean isNQvisible = false;
    private boolean isVisible;
    private static final TreeMap<String, Quester> questers = new TreeMap<>();

    static Quester credo = new Quester(null);

    public static class Tasks {
        String name;
        LinkedList<CharWnd.Quest.Condition> conditions;
    }


    NQuestsStats stats;
    private Tex glowon;


    @Override
    public void dispose() {
        synchronized (markers) {
            questers.clear();
            in_work.set(false);
            updCompleted.set(false);
            needUpdate = false;
            items.clear();
            new_questers.clear();
            markers.clear();
            credo = null;
        }
        super.dispose();
    }


    public NQuestInfo() {
        super("NQuestInfo");
        stats = NUtils.getGameUI().getStats();
        stats.hide();
        isVisible = NConfiguration.getInstance().isQuestInfoVisible;
        NMiniMapWnd.NMenuCheckBox vc = (NMiniMapWnd.NMenuCheckBox) add(new NMiniMapWnd.NMenuCheckBox("lbtn-qshow", GameUI.kb_vil, "Show/hide list of quests"), 0, 0).changed(a -> {
            isVisible = !a;
            NConfiguration.getInstance().isQuestInfoVisible = !a;
            needUpdate = true;
        });
        vc.a = !isVisible;
        vc.h = !isVisible;
        add(new NMiniMapWnd.NMenuCheckBox("lbtn-hidenq", GameUI.kb_vil, "Show/hide without quest"), UI.scale(20), 0).changed(a -> {
            needUpdate = true;
            isNQvisible = a;
        });
//        add(new NMiniMapWnd.NMenuCheckBox("lbtn-stats", GameUI.kb_vil, "Show stats"), UI.scale(40), 0).changed(a -> {
//                    if (stats.visible())
//                        stats.hide();
//                    else
//                        stats.show();
//                }
//        );
        dy = Resource.loadtex("hud/lbtn-stats").sz().y;
        pack();
    }

    private Collection<QuestImage> imgs = null;
    public static AtomicBoolean updCompleted = new AtomicBoolean(false);
    public static AtomicBoolean in_work = new AtomicBoolean(false);

    static final TreeMap<String, Quester> new_questers = new TreeMap<>();

    public static class Loader implements Runnable {

        @Override
        public void run() {
            try {
                in_work.set(true);
                ArrayList<CharWnd.Quest> qs = new ArrayList<>(NUtils.getGameUI().chrwdg.cqst.quests);
                for (CharWnd.Quest q : qs) {
                    if (q.res != null && !q.title().isEmpty()) {
                        if (NUtils.getGameUI().chrwdg.quest != null) {
                            NUtils.getGameUI().chrwdg.wdgmsg("qsel", q.id);
                            NUtils.waitEvent(() -> NUtils.getGameUI().chrwdg.quest != null && ((CharWnd.Quest.DefaultBox) NUtils.getGameUI().chrwdg.quest).id == q.id && ((CharWnd.Quest.DefaultBox) NUtils.getGameUI().chrwdg.quest).cond != null && ((CharWnd.Quest.DefaultBox) NUtils.getGameUI().chrwdg.quest).cond.length > 0, 200);
                            for (CharWnd.Quest.Condition c : ((CharWnd.Quest.DefaultBox) NUtils.getGameUI().chrwdg.quest).cond) {
                                String qname = null;
                                if (c.desc.contains("Tell")) {
                                    qname = c.desc.substring(5, c.desc.indexOf(" ", 6));
                                    if (!new_questers.containsKey(qname)) {
                                        new_questers.put(qname, new Quester(qname));
                                    }
                                    new_questers.get(qname).main_quests.put(q.title, new Quester.Quest(((CharWnd.Quest.DefaultBox) NUtils.getGameUI().chrwdg.quest).cond, q.id));
                                } else {
                                    if (c.desc.contains("Greet")) {
                                        qname = c.desc.substring(6);
                                    } else if (c.desc.contains(" to ")) {
                                        qname = c.desc.substring(c.desc.indexOf(" to ") + 4);
                                    } else if (c.desc.contains(" at ")) {
                                        qname = c.desc.substring(c.desc.indexOf(" at ") + 4);
                                    }
                                    if (qname != null) {
                                        if (!new_questers.containsKey(qname)) {
                                            new_questers.put(qname, new Quester(qname));
                                        }
                                        new_questers.get(qname).linked_quests.put(q.title, new Quester.Quest(((CharWnd.Quest.DefaultBox) NUtils.getGameUI().chrwdg.quest).cond, q.id));
                                    }
                                }
                            }
                        }
                    }
                    if (q.title == null && q.title() != null) {
                        String name1 = q.title();
                        credo = new Quester(name1);
                        credo.name = name1;
                        credo.main_quests.clear();
                        credo.main_quests.put(name1, new Quester.Quest(((CharWnd.Quest.DefaultBox) NUtils.getGameUI().chrwdg.quest).cond, q.id));
                    }
                }
            } catch (InterruptedException ignored) {
            } finally {
                updCompleted.set(true);
                in_work.set(false);
            }
        }
    }

    @Override
    public void draw(GOut g) {
        if (isAvailable()) {
            super.draw(g);
            if (glowon != null) {
                g.image(glowon, new Coord(0, 25));
            }
        } else {
            if (glowon != null) {
                g.image(glowon, new Coord(0, 25));
            }
            super.draw(g);
            int id = (int) (NUtils.getTickId() / 2) % 13;
            if (!questers.isEmpty()) {
                g.image(gear[id], new Coord(sz.x / 2 - gear[0].sz().x / 2, sz.y / 2 - gear[0].sz().y / 2));
            }
        }
    }

    boolean needUpdate() {
        if (!updCompleted.get() && !in_work.get()) {
            for (CharWnd.Quest c : NUtils.getGameUI().chrwdg.cqst.quests) {
                boolean isFind = false;
                if (c.title != null) {
                    for (Quester q : questers.values()) {
                        if (q.main_quests.containsKey(c.title) || q.linked_quests.containsKey(c.title)) {
                            isFind = true;
                            break;
                        }
                    }
                } else {
                    if (c.done == 1)
                        return true;
                    isFind = true;
                }
                if (!isFind)
                    return true;
            }

            for (Quester q : questers.values()) {
                for (String title : q.main_quests.keySet()) {
                    if (title != null) {
                        boolean isFind = false;
                        for (CharWnd.Quest c : NUtils.getGameUI().chrwdg.cqst.quests) {
                            if (c.title != null && c.title.contains(title)) {
                                isFind = true;
                                break;
                            }
                        }
                        if (!isFind)
                            return true;
                    }
                }
                for (String title : q.linked_quests.keySet()) {
                    if (title != null) {
                        boolean isFind = false;
                        for (CharWnd.Quest c : NUtils.getGameUI().chrwdg.cqst.quests) {
                            if (c.title != null && c.title.contains(title)) {
                                isFind = true;
                                break;
                            }
                        }
                        if (!isFind)
                            return true;
                    }
                }
            }
        }
        return false;
    }

    Thread th = null;

    static class QuestImage {
        public Pair<Coord, Coord> area = new Pair<>(new Coord(), new Coord());
        public BufferedImage img;
        public int id;

        public QuestImage(BufferedImage img, int id) {
            this.img = img;
            this.id = id;
        }
    }

    public void tick(double dt) {
        if (!in_work.get() && new_questers!=null) {
            if (NUtils.getGameUI() != null && NUtils.getGameUI().chrwdg != null && NUtils.getGameUI().chrwdg.quest != null && !NUtils.getGameUI().chrwdg.cqst.quests.isEmpty() && needUpdate()) {
                (th = new Thread(new Loader())).start();
            }
        }
        if (updCompleted.get())
            if (!new_questers.isEmpty()) {
                synchronized (questers) {
                    questers.clear();
                    if (new_questers != null) {
                        questers.putAll(new_questers);
                        new_questers.clear();
                    }
                    updCompleted.set(false);
                    needUpdate = true;
                }
            }
        if (isAvailable() && needUpdate) {

            items.clear();
            synchronized (markers) {
                for (QuestGob gob : markers.values())
                    gob.tagsSet.clear();
                imgs = new LinkedList<>();
                if (isVisible) {
                    if (credo != null && !credo.main_quests.isEmpty()) {
                        int id = credo.main_quests.get(credo.name).id;
                        imgs.add(new QuestImage(credo_title.render(credo.name).img, id));
                        for (CharWnd.Quest.Condition c : credo.main_quests.get(credo.name).conditions) {
                            if (c.done != 1) {
                                imgs.add(new QuestImage(gfnd2_under.render(c.desc).img, id));
                            }
                        }
                    }
                    for (String name : questers.keySet()) {

                        Quester quester = questers.get(name);
                        int qid = -1;
                        quester.ended = 0;
                        for (Quester.Quest q : quester.main_quests.values()) {
                            int completed = 0;
                            qid = q.id;
                            for (CharWnd.Quest.Condition c : q.conditions) {
                                if (c.done == 1)
                                    completed += 1;
                                else
                                    checkTarget(c.desc);
                            }
                            if (completed == q.conditions.length - 1)
                                quester.ended += 1;
                        }
                        if (quester.main_quests.size() > 0) {
                            imgs.add(new QuestImage(catimgsh(5, active_title.render(name).img, fnd1.render(String.format("($col[128,255,128]{%d}|$col[255,128,128]{%d})", quester.ended, quester.main_quests.size() - quester.ended), UI.scale(200)).img), qid));
                        } else {
                            if (isNQvisible && quester.linked_quests.size() > 0) {
                                for (Quester.Quest q : quester.linked_quests.values()) {
                                    boolean need = false;
                                    for (CharWnd.Quest.Condition c : q.conditions) {
                                        if (c.done == 0 && c.desc.contains(name)) {
                                            need = true;
                                            break;
                                        }
                                    }
                                    if (need) {
                                        imgs.add(new QuestImage(unactive_title.render(name).img, -1));
                                        break;
                                    }
                                }
                            }
                        }
                        updateTags(name);
                        for (Quester.Quest q : quester.main_quests.values()) {
                            for (CharWnd.Quest.Condition c : q.conditions) {
                                if (c.done != 1 && !c.desc.contains("Tell")) {
                                    imgs.add(new QuestImage(gfnd2_under.render(c.desc).img, q.id));
                                }
                            }
                        }
                        if (isNQvisible || quester.main_quests.size() > 0) {
                            for (Quester.Quest q : quester.linked_quests.values()) {
                                for (CharWnd.Quest.Condition c : q.conditions) {
                                    if (c.done != 1 && !c.desc.contains("Tell") && c.desc.contains(quester.name)) {
                                        imgs.add(new QuestImage(gfnd2.render(c.desc).img, q.id));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (!imgs.isEmpty()) {
                glowon = new TexI(ncatimgs(1, imgs.toArray(new QuestImage[0])));
                resize(new Coord(glowon.sz().x + btnLock.sz.x, glowon.sz().y + dy));
            }else
            {
                glowon = null;
                resize(3*UI.scale(20) + btnLock.sz.x,dy);
            }
            needUpdate = false;
        }
    }

    public static BufferedImage ncatimgs(int margin, QuestImage... imgs) {
        int w = 0, h = -margin;
        for (QuestImage img : imgs) {
            if (img == null)
                continue;
            if (img.img.getWidth() > w)
                w = img.img.getWidth();
            h += img.img.getHeight() + margin;
        }
        BufferedImage ret = TexI.mkbuf(new Coord(w, h));
        Graphics g = ret.getGraphics();
        int y = 0;
        for (QuestImage img : imgs) {
            if (img == null)
                continue;
            img.area.a.x = 0;
            img.area.a.y = y;
            g.drawImage(img.img, 0, y, null);
            y += img.img.getHeight() + margin;
            img.area.b.x = img.img.getWidth();
            img.area.b.y = y - margin;
        }
        g.dispose();
        return (ret);
    }

    public static boolean isAvailable() {
        return !in_work.get() && !updCompleted.get();
    }

    public static Set<String> items = new HashSet<>();

    void checkTarget(String info) {
        if (info.contains("Defeat ") || info.contains("Pick ") || info.contains("Catch ")) {
            String name;
            int ind = info.indexOf(" a ");
            if (ind != -1)
                name = info.substring(info.indexOf(" a ") + 3);
            else {
                ind = info.indexOf(" an ");
                if (ind != -1)
                    name = info.substring(info.indexOf(" an ") + 4);
                else
                    name = info.substring(info.indexOf(" ") + 1);
            }
            if (!name.isEmpty()) {
                if (name.contains("blueberr"))
                    name = "blueberr";
                else if (name.contains("woodgrouse hen"))
                    name = "woodgrouse-f";
                else if (name.contains("lingon"))
                    name = "lingon";
                else if (name.contains("morel"))
                    name = "lorchel";
                else if (name.contains("yellowf"))
                    name = "yellowf";
                else if (name.contains("hen"))
                    name = "chicken/chicken";
                else if (name.contains("cock"))
                    name = "chicken/roast";
                else if (name.contains("mouflon"))
                    name = "sheep";
                else if (name.contains("auroch"))
                    name = "cattle";
                else if (name.contains("chantrell"))
                    name = "herbs/chantrell";
                else if (name.contains("horse"))
                    name = "horse/horse";
                else if (name.contains("rat"))
                    name = "rat/rat";
                items.add((name.replaceAll("\\s+", "")).replaceAll("'+", ""));
            }
        }
        if (info.contains("Raid a")) {
            String name = info.substring(info.indexOf(" a ") + 3);
            if (name.isEmpty())
                name = info.substring(info.indexOf(" an ") + 4);
            if (!name.isEmpty()) {
                if (name.contains("bird"))
                    items.add("nest");
                else
                    items.add("anthill");
            }
        }
    }

    public static boolean isQuested(Gob gob, Tex tex) {
        String name = gob.getResName();
        if (name != null) {
            for (String item : items) {
                if (NUtils.checkName(name, new NAlias(new ArrayList<>(Collections.singletonList(item)), new ArrayList<>(Collections.singletonList("crabapp"))))) {
                    gob.noteImg = tex;
                    gob.addTag(NGob.Tags.quest);
                    return true;
                }
            }
            gob.removeTag(NGob.Tags.quest);
        }
        return false;
    }

    @Override
    public boolean mousedown(Coord c, int button) {
        Coord pos = new Coord(c.x, c.y-dy);
        if (isAvailable()) {
            if (imgs != null) {
                for (QuestImage img : imgs) {
                    if (img.id >= 0) {
                        if (img.area.a.x <= pos.x && pos.x <= img.area.b.x && img.area.a.y <= pos.y && pos.y <= img.area.b.y) {
                            NUtils.getGameUI().chrwdg.wdgmsg("qsel", img.id);
                            return true;
                        }
                    }
                }
            }
        }
        return super.mousedown(c, button);
    }
}
