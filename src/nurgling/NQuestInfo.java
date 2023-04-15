package nurgling;

import haven.*;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static haven.ItemInfo.catimgsh;
import static haven.MCache.tilesz;

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
    public void selectedQuest() {
        if ( isAvailable() && (!questers.isEmpty() || credo!=null)) {
            synchronized (new_questers) {
                try {
                    NUtils.waitEvent(() -> NUtils.getGameUI().chrwdg.quest != null && ((CharWnd.Quest.DefaultBox) NUtils.getGameUI().chrwdg.quest).cond != null && ((CharWnd.Quest.DefaultBox) NUtils.getGameUI().chrwdg.quest).cond.length > 0, 200);

                    Integer qid = ((CharWnd.Quest.DefaultBox) NUtils.getGameUI().chrwdg.quest).id;
                        for (Quester quester : questers.values()) {
                            for (Integer id: quester.main_quests.keySet()) {
                                if (id.equals(qid)) {
                                    quester.main_quests.get(qid).conditions = ((CharWnd.Quest.DefaultBox) NUtils.getGameUI().chrwdg.quest).cond;
                                    break;
                                }
                            }
                            for (Integer id : quester.linked_quests.keySet()) {
                                if (id.equals(qid)) {
                                    quester.linked_quests.get(qid).conditions = ((CharWnd.Quest.DefaultBox) NUtils.getGameUI().chrwdg.quest).cond;
                                    break;
                                }
                            }
                        }
                        if (credo != null && credo.main_quests.containsKey(qid)) {
                                credo.main_quests.get(qid).conditions = ((CharWnd.Quest.DefaultBox) NUtils.getGameUI().chrwdg.quest).cond;
                        }
                    needUpdate = true;
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    public class QuestGob {
        public String name;
        long id;

        public QuestGob(MapFile.Marker marker, long id) {
            this.marker = marker;
            name = marker.name();
            this.id = id;
        }

        public MapFile.Marker marker;

        Set<NGob.Tags> tagsSet = new HashSet<>();
    }

    public Set<NGob.Tags> tagsSet(String name){
        if(isAvailable())
            synchronized (markers) {
                return markers.get(name).tagsSet;
            }
        return null;
    }

    private final HashMap<String, QuestGob> markers = new HashMap<>();

    public void setMarker(String name, MapFile.Marker marker, long id) {
        synchronized (markers) {
            markers.put(name, new QuestGob(marker, id));
        }
    }

    public void updateTags(String name){
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

    private void checkTasks(HashMap<Integer, Quester.Quest> quests, QuestGob gob) {
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
                    } else if (c.desc.contains("Greet") || (c.desc.contains("Visit") && !c.desc.contains("cave"))) {
                        gob.tagsSet.add(NGob.Tags.qgreet);
                    }
                }
            }
        }
    }

    public QuestGob getMark(Gob gob) {
        synchronized (markers) {
            for (String name : markers.keySet()) {
                NQuestInfo.QuestGob questGob = markers.get(name);
                MiniMap.Location loc = NUtils.getGameUI().mapfile.view.sessloc;
                Coord2d tmp = questGob.marker.tc.sub(loc.tc).mul(tilesz).add(6, 6);
                if (Math.abs(gob.rc.x - tmp.x) < 10 && Math.abs(gob.rc.y - tmp.y) < 10) {
                    return questGob;
                }
            }
        }
        return null;
    }


    public static class Quester {
        public String name;
        int ended = 0;
        public Quester(String name) {
            this.name = name;
            main_quests = new HashMap<>();
            linked_quests = new HashMap<>();
        }

        public static class Quest {

            CharWnd.Quest.Condition[] conditions;

            int id;

            public Quest(CharWnd.Quest.Condition[] cond, int id) {
                conditions = cond;
                this.id = id;

            }
        }

        HashMap<Integer, Quest> main_quests;
        HashMap<Integer, Quest> linked_quests;
    }

    private boolean needUpdate = false;
    private boolean isNQvisible = false;
    private boolean isVisible;
    private final TreeMap<String, Quester> questers;

    private Quester credo = new Quester(null);

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

    boolean asTask = false;

    public NQuestInfo() {
        super("NQuestInfo");
        new_questers = new TreeMap<>();
        questers = new TreeMap<>();
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
        add(new NMiniMapWnd.NMenuCheckBox("lbtn-tasks", GameUI.kb_vil, "Show as tasks"), UI.scale(40), 0).changed(a -> {
            asTask = a;
            needUpdate = true;
        });
        add(new NMiniMapWnd.NMenuCheckBox("lbtn-stats", GameUI.kb_vil, "Show stats"), UI.scale(60), 0).changed(a -> {
            if (stats.visible())
                stats.hide();
            else
                stats.show();
        });
        dy = Resource.loadtex("hud/lbtn-stats").sz().y;
        pack();
    }

    private Collection<QuestImage> imgs = null;
    private final AtomicBoolean updCompleted = new AtomicBoolean(false);
    private final AtomicBoolean in_work = new AtomicBoolean(false);
    private final TreeMap<String, Quester> new_questers;

    public class Loader implements Runnable {

        @Override
        public void run() {
            Quester newcredo = new Quester("Credo");
            try {
                in_work.set(true);

                ArrayList<CharWnd.Quest> qs = new ArrayList<>(NUtils.getGameUI().chrwdg.cqst.quests);
                for (CharWnd.Quest q : qs) {
                        if (NUtils.getGameUI().chrwdg.quest != null) {
                            NUtils.getGameUI().chrwdg.wdgmsg("qsel", q.id);
                            NUtils.waitEvent(() -> NUtils.getGameUI().chrwdg.quest != null && ((CharWnd.Quest.DefaultBox) NUtils.getGameUI().chrwdg.quest).id == q.id && ((CharWnd.Quest.DefaultBox) NUtils.getGameUI().chrwdg.quest).cond != null && ((CharWnd.Quest.DefaultBox) NUtils.getGameUI().chrwdg.quest).cond.length > 0, 200);
                            for (CharWnd.Quest.Condition c : ((CharWnd.Quest.DefaultBox) NUtils.getGameUI().chrwdg.quest).cond) {
                                String qname = null;
                                if (c.desc.contains("Tell") || (c.desc.contains("Visit") && !c.desc.contains("cave"))) {
                                    qname = c.desc.contains("Tell") ? c.desc.substring(5, c.desc.indexOf(" ", 6)) : c.desc.substring(6);
                                    if (!new_questers.containsKey(qname)) {
                                        new_questers.put(qname, new Quester(qname));
                                    }
                                    new_questers.get(qname).main_quests.put(q.id, new Quester.Quest(((CharWnd.Quest.DefaultBox) NUtils.getGameUI().chrwdg.quest).cond, q.id));
                                } else {
                                    if (c.desc.contains("Greet") || (c.desc.contains("Visit") && !c.desc.contains("cave"))) {
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
                                        new_questers.get(qname).linked_quests.put(q.id, new Quester.Quest(((CharWnd.Quest.DefaultBox) NUtils.getGameUI().chrwdg.quest).cond, q.id));
                                    }
                                }
                            }
                    }
                    if (((CharWnd.Quest.DefaultBox) NUtils.getGameUI().chrwdg.quest)!=null && q.title == null) {
                        newcredo.main_quests.put(q.id, new Quester.Quest(((CharWnd.Quest.DefaultBox) NUtils.getGameUI().chrwdg.quest).cond, q.id));
                    }
                }
            } catch (InterruptedException ignored) {
            } finally {
                updCompleted.set(true);
                credo = newcredo;
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
                for (Quester q : questers.values()) {
                    if (q.main_quests.containsKey(c.id) || q.linked_quests.containsKey(c.id)) {
                        isFind = true;
                        break;
                    }
                }
                if (credo!= null && credo.main_quests.containsKey(c.id))
                    isFind = true;
                if (c.done == 1)
                    return true;
                if (!isFind)
                    return true;
            }

            for (Quester q : questers.values()) {
                for (Integer qid : q.main_quests.keySet()) {
                    boolean isFind = false;
                    for (CharWnd.Quest c : NUtils.getGameUI().chrwdg.cqst.quests) {
                        if (c.id == qid) {
                            isFind = true;
                            break;
                        }
                    }
                    if (!isFind)
                        return true;
                }

                for (Integer qid : q.linked_quests.keySet()) {
                    boolean isFind = false;
                    for (CharWnd.Quest c : NUtils.getGameUI().chrwdg.cqst.quests) {
                        if (c.id == qid) {
                            isFind = true;
                            break;
                        }
                    }
                    if (!isFind)
                        return true;
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
            if (!(new_questers != null && new_questers.isEmpty()) || (credo!=null && !credo.main_quests.isEmpty())) {
                synchronized (questers) {
                    questers.clear();
                    if (new_questers != null && !new_questers.isEmpty()) {
                        questers.putAll(new_questers);
                        new_questers.clear();
                    }
                    updCompleted.set(false);
                    needUpdate = true;
                }
            }
            else
            {
                return;
            }
        if (isAvailable() && needUpdate) {
            if(asTask)
                draw_as_tasks();
            else
                draw_as_questList();

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

    private void draw_as_questList() {
        items.clear();
        synchronized (markers) {
            for (QuestGob gob : markers.values())
                gob.tagsSet.clear();
            imgs = new LinkedList<>();
            if (isVisible) {
                if (credo != null && !credo.main_quests.isEmpty()) {
                    imgs.add(new QuestImage(credo_title.render(credo.name).img, -1));
                    for (Quester.Quest q : credo.main_quests.values()) {
                        for (CharWnd.Quest.Condition c : q.conditions) {
                            if (c.done != 1) {
                                imgs.add(new QuestImage(gfnd2_under.render(c.desc).img, q.id));
                            }
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
    }

    public static class Task {
        int id;
        CharWnd.Quest.Condition condition;

        public Task(int id, CharWnd.Quest.Condition conditions) {
            this.id = id;
            this.condition = conditions;
        }
    }

    void draw_as_tasks() {
        items.clear();
        synchronized (markers) {
            for (QuestGob gob : markers.values())
                gob.tagsSet.clear();
            imgs = new LinkedList<>();
            if (isVisible) {
                if (credo != null && !credo.main_quests.isEmpty()) {
                    imgs.add(new QuestImage(credo_title.render(credo.name).img, -1));
                    for(Quester.Quest q : credo.main_quests.values()) {
                        for (CharWnd.Quest.Condition c : credo.main_quests.get(q.id).conditions) {
                            if (c.done != 1) {
                                imgs.add(new QuestImage(gfnd2_under.render(c.desc).img, q.id));
                            }
                        }
                    }
                }
                LinkedList<Task> bring_t = new LinkedList<>();
                LinkedList<Task> consult_t = new LinkedList<>();
                LinkedList<Task> craft_t = new LinkedList<>();
                LinkedList<Task> hunting_t = new LinkedList<>();
                LinkedList<Task> stats_t = new LinkedList<>();
                LinkedList<Task> action_t = new LinkedList<>();
                LinkedList<Task> forage_t = new LinkedList<>();
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
                            else {
                                checkTarget(c.desc);
                                if (c.desc.contains("Bring"))
                                    bring_t.add(new Task(qid, c));
                                else if (c.desc.contains("Pick"))
                                    forage_t.add(new Task(qid, c));
                                else if (c.desc.contains("Kill") || c.desc.contains("Raid") || c.desc.contains("Catch"))
                                    hunting_t.add(new Task(qid, c));
                                else if (c.desc.contains("Greet") || (c.desc.contains("Visit") && !c.desc.contains("cave") ) || c.desc.contains("wave") || c.desc.contains("laugh") || c.desc.contains("rage"))
                                    consult_t.add(new Task(qid, c));
                                else if (c.desc.contains("Gain"))
                                    stats_t.add(new Task(qid, c));
                                else if (c.desc.contains("Create"))
                                    craft_t.add(new Task(qid, c));
                                else if (!c.desc.contains("Tell")) {
                                    action_t.add(new Task(qid, c));
                                }

                            }
                        }
                        if (completed == q.conditions.length - 1)
                            quester.ended += 1;
                    }
                    updateTags(quester.name);
                }
                if (bring_t.size() > 0) {
                    imgs.add(new QuestImage(active_title.render("Bring:").img, -1));
                    for (Task t : bring_t) {
                        imgs.add(new QuestImage(gfnd2_under.render(t.condition.desc).img, t.id));
                    }
                }
                if (forage_t.size() > 0) {
                    imgs.add(new QuestImage(active_title.render("Foraging:").img, -1));
                    for (Task t : forage_t) {
                        imgs.add(new QuestImage(gfnd2_under.render(t.condition.desc).img, t.id));
                    }
                }
                if (hunting_t.size() > 0) {
                    imgs.add(new QuestImage(active_title.render("Hunting:").img, -1));
                    for (Task t : hunting_t) {
                        imgs.add(new QuestImage(gfnd2_under.render(t.condition.desc).img, t.id));
                    }
                }
                if (consult_t.size() > 0) {
                    imgs.add(new QuestImage(active_title.render("Conversation:").img, -1));
                    for (Task t : consult_t) {
                        imgs.add(new QuestImage(gfnd2_under.render(t.condition.desc).img, t.id));
                    }
                }
                if (stats_t.size() > 0) {
                    imgs.add(new QuestImage(active_title.render("Attributes:").img, -1));
                    for (Task t : stats_t) {
                        imgs.add(new QuestImage(gfnd2_under.render(t.condition.desc).img, t.id));
                    }
                }
                if (craft_t.size() > 0) {
                    imgs.add(new QuestImage(active_title.render("Craft:").img, -1));
                    for (Task t : craft_t) {
                        imgs.add(new QuestImage(gfnd2_under.render(t.condition.desc).img, t.id));
                    }
                }
                if (action_t.size() > 0) {
                    imgs.add(new QuestImage(active_title.render("Other:").img, -1));
                    for (Task t : action_t) {
                        imgs.add(new QuestImage(gfnd2_under.render(t.condition.desc).img, t.id));
                    }
                }
            }
        }
    }


    private BufferedImage ncatimgs(int margin, QuestImage... imgs) {
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

    public boolean isAvailable() {
        return !in_work.get() && !updCompleted.get();
    }

    private final Set<String> items = new HashSet<>();

    private void checkTarget(String info) {
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

    public boolean isQuested(Gob gob, Tex tex) {
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
