package nurgling;

import haven.*;
import haven.Label;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;

import static haven.ItemInfo.catimgs;

public class NQuestInfo extends Widget {

    Text.Furnace gfnd2 = new PUtils.BlurFurn(new Text.Foundry(Text.sans, 14, Color.white).aa(true), 2, 1, Color.BLACK);
    Text.Furnace gfnd2_under = new PUtils.BlurFurn(new Text.Foundry(Text.sans, 14, Color.orange).aa(true), 2, 1, Color.BLACK);
    public static final RichText.Foundry fnd1 = new RichText.Foundry(new ChatUI.ChatParser(TextAttribute.FONT, Text.dfont.deriveFont(UI.scale(18f)), TextAttribute.FOREGROUND, Color.YELLOW));
    public static final RichText.Foundry fnd1_nq = new RichText.Foundry(new ChatUI.ChatParser(TextAttribute.FONT, Text.dfont.deriveFont(UI.scale(18f)), TextAttribute.FOREGROUND, Color.WHITE));
    public static final RichText.Foundry fnd2 = new RichText.Foundry(new ChatUI.ChatParser(TextAttribute.FONT, Text.dfont.deriveFont(UI.scale(14f)), TextAttribute.FOREGROUND, Color.WHITE));
    public static final RichText.Foundry fnd2_under = new RichText.Foundry(new ChatUI.ChatParser(TextAttribute.FONT, Text.dfont.deriveFont(UI.scale(14f)), TextAttribute.FOREGROUND, Color.WHITE));
    public static final RichText.Foundry fndready2 = new RichText.Foundry(new ChatUI.ChatParser(TextAttribute.FONT, Text.dfont.deriveFont(UI.scale(14f)), TextAttribute.FOREGROUND, Color.GREEN));

    class TreeCondition{
        public String name;
        public CharWnd.Quest.Condition cond;

        public TreeCondition(String name, CharWnd.Quest.Condition cond) {
            this.name = name;
            this.cond = cond;
        }
    }

    class Quest{
        public List<CharWnd.Quest.Condition> cond;

        public Quest(List<CharWnd.Quest.Condition> conditions) {
            cond = conditions;
        }
    }

    public static HashMap<String, List<Quest>> questData = new HashMap<>();
    public static HashMap<String, List<TreeCondition>> treeData = new HashMap<>();
    public static HashMap<String, List<CharWnd.Quest.Condition>> condData = new HashMap<>();
    private Tex glowon;

    public static boolean update = true;
    public final Text.Foundry prsf = new Text.Foundry(Text.fraktur, 25).aa(true);
    public NQuestInfo() {
        super ( new Coord( NUtils.getGameUI().sz.x-500, NUtils.getGameUI().sz.y-200 ) );
        add(new NMiniMapWnd.NMenuCheckBox("lbtn-hidenq", GameUI.kb_vil, "Show/hide without quest"), 0, 0).changed(a -> {update=true; isNQvisible = a;});
        pack();
    }

    boolean isNQvisible = false;
    public class Loader implements Runnable{

        @Override
        public void run() {
            try {
                isReady.set(0);
                int oldId = NUtils.getGameUI().chrwdg.quest.questid();
                for (CharWnd.Quest q : NUtils.getGameUI().chrwdg.cqst.quests) {
                    if (q.res!=null && !q.title().isEmpty()) {
                        if( NUtils.getGameUI().chrwdg.quest!=null) {
                            NUtils.getGameUI().chrwdg.wdgmsg("qsel", q.id);
                            try{
                                NUtils.waitEvent(() -> NUtils.getGameUI().chrwdg.quest!=null && NUtils.getGameUI().chrwdg.quest.questid() == q.id, 50);
                            }catch (NullPointerException e){
                                NUtils.getGameUI().chrwdg.quest.questid();
                            }

                        }
                    }
                }
                NUtils.getGameUI().chrwdg.wdgmsg("qsel", oldId);
                isReady.set(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }catch (ConcurrentModificationException e){
                isReady.set(-1);
            }

        }
    }

    @Override
    public void draw(GOut g) {
        if(glowon!=null){
            g.image(glowon, new Coord(0,25));
        }
        super.draw(g);
    }

    public static AtomicInteger isReady = new AtomicInteger(-1);


    boolean needUpdate(){
        for(CharWnd.Quest c: NUtils.getGameUI().chrwdg.cqst.quests){
            boolean isFind = false;
            for(String q : condData.keySet()){
                if(c.title!=null && c.title.contains(q)) {
                    isFind = true;
                    break;
                }
            }
            if(!isFind && c.title!=null)
                return true;
        }
        return false;
    }
    void check(){
        if (th != null && isReady.get()==1)
            if (NUtils.getGameUI()!= null && NUtils.getGameUI().chrwdg!=null && NUtils.getGameUI().chrwdg.quest!=null && !NUtils.getGameUI().chrwdg.cqst.quests.isEmpty() && needUpdate()) {
                (th = new Thread(new Loader())).start();
        }
        for(String tree: questData.keySet()){
            for(Quest q : questData.get(tree)){
                int comleted = 0;
                for(CharWnd.Quest.Condition c : q.cond){
                    if(c.done==1)
                        comleted+=1;
                }
                if(comleted == q.cond.size()){
                    String name = null;
                    for(CharWnd.Quest.Condition c : q.cond) {
                        LinkedList<TreeCondition> forRemove = new LinkedList<>();
                        for (TreeCondition condition : treeData.get(tree)) {
                            if (condition.cond.desc.contains(c.desc) && c.done == condition.cond.done) {
                                forRemove.add(condition);
                            }
                        }
                        for(TreeCondition condition:forRemove){
                            treeData.get(tree).remove(condition);
                        }
                        if(forRemove.size()>0)
                            name = forRemove.get(0).name;
                    }
                    if(name!=null)
                    condData.remove(name);
                    questData.get(tree).remove(q);
                    update = true;
                    return;
                }
            }
        }
    }

    boolean treeDataHaveUncompleted(String tree){
        for(TreeCondition cond :treeData.get(tree)){
            if(cond.cond.done!=1)
                return true;
        }
        return false;
    }

    public static void update(String title, CharWnd.Quest.Condition c) {
        if(title!=null && condData.get(title)!=null)
            for(CharWnd.Quest.Condition c1 :condData.get(title)){
                if(c1.desc.contains(c.desc))
                {
                    c1.done = c.done;
                    update = true;
                }
            }
    }
    Thread th = null;
    public void tick(double dt) {
        check();
        if(isReady.get() == -1 && NUtils.getGameUI()!= null && NUtils.getGameUI().chrwdg!=null && NUtils.getGameUI().chrwdg.quest!=null && !NUtils.getGameUI().chrwdg.cqst.quests.isEmpty()) {
            (th = new Thread(new Loader())).start();
        }
        if(update){
            if(!condData.isEmpty()){
                updateQG();
                Collection<BufferedImage> imgs = new LinkedList<BufferedImage>();
                int maxW = 0;
                int maxH = 0;
                for(String tree: currentQG){
                        int ended = 0;
                        if(questData.get(tree)!=null) {
                            for (Quest q : questData.get(tree)) {
                                int completed = 0;
                                for (CharWnd.Quest.Condition c : q.cond) {
                                    if (c.done == 1)
                                        completed += 1;
                                }
                                if (completed == q.cond.size() - 1)
                                    ended += 1;
                            }
                        }
                        if(treeData.get(tree).size()>0) {
                            if (questData.get(tree) != null && questData.get(tree).size() > 0) {
                                imgs.add(fnd1.render(tree + String.format("($col[128,255,128]{%d}|$col[255,128,128]{%d})", ended, questData.get(tree).size() - ended), UI.scale(200)).img);
                            } else {
                                if(isNQvisible && treeDataHaveUncompleted(tree))
                                    imgs.add(fnd1_nq.render(tree, UI.scale(200)).img);
                            }
                        }
                        maxW = Math.max(tree.length(), maxW);
                        maxH+=1;
                        for(TreeCondition cond: treeData.get(tree)){
                            if(isNQvisible || (questData.get(tree) != null && questData.get(tree).size() > 0)) {
                                if (cond.cond.done != 1) {
                                    if (!cond.cond.desc.contains(tree))
                                        imgs.add(gfnd2_under.render(cond.cond.desc).img);
                                    else
                                        imgs.add(gfnd2.render(cond.cond.desc).img);
                                }
                                maxW = Math.max(cond.cond.desc.length(), maxW);
                                maxH += 1;
                            }
                        }
                }
                if(!imgs.isEmpty()) {
                    glowon = new TexI(catimgs(1, imgs.toArray(new BufferedImage[0])));
                    resize(new Coord(maxW*20,maxH*25));
                }
            }
        }
    }

    void updateQG(){
        for(String key: condData.keySet()){
            for(CharWnd.Quest.Condition c : condData.get(key)){
                if(c.desc.contains("Tell")){
                    String name = c.desc.substring(5,c.desc.indexOf(" ",6));
                    currentQG.add(name);
                    if(!treeData.containsKey(name))
                        treeData.put(name, new LinkedList<>());
                }
                else if(c.desc.contains("Greet")){
                    String name = c.desc.substring(6);
                    currentQG.add(name);
                    if(!treeData.containsKey(name))
                        treeData.put(name, new LinkedList<>());
                }
                else if(c.desc.contains(" to ")){
                    String name = c.desc.substring(c.desc.indexOf(" to ")+4);
                    currentQG.add(name);
                    if(!treeData.containsKey(name))
                        treeData.put(name, new LinkedList<>());
                }
                else if(c.desc.contains(" at ")){
                    String name = c.desc.substring(c.desc.indexOf(" at ")+4);
                    currentQG.add(name);
                    if(!treeData.containsKey(name))
                        treeData.put(name, new LinkedList<>());
                }
            }
        }

        for(String tree:treeData.keySet()) {
            for(String key: condData.keySet()) {
                boolean isFoundAll = false;
                boolean isFoundAction = false;
                for (CharWnd.Quest.Condition c : condData.get(key)) {
                    if (c.desc.contains(tree))
                        if (c.desc.contains("Tell")) {
                            isFoundAll = true;
                        } else {
                            isFoundAction = true;
                        }
                }
                if(isFoundAll){
                    boolean isFound = false;
                    for (CharWnd.Quest.Condition c : condData.get(key)) {

                        for(TreeCondition treeq : treeData.get(tree)){
                            if (treeq.name.contains(key)) {
                                if(treeq.cond.desc.contains(c.desc))
                                    isFound = true;
                            }
                        }
                        if(!isFound && !c.desc.contains("Tell")){
                            treeData.get(tree).add(new TreeCondition(key,c));
                        }
                    }
                    if(!isFound) {
                        if(!questData.containsKey(tree))
                            questData.put(tree, new LinkedList<Quest>());
                        questData.get(tree).add(new Quest(condData.get(key)));
                    }
                }
                if(isFoundAction){
                    for (CharWnd.Quest.Condition c : condData.get(key)) {
                        if(!c.desc.contains("Tell") && c.desc.contains(tree)) {
                            boolean isFound = false;
                            for (TreeCondition treeq : treeData.get(tree)) {
                                if (treeq.name.contains(key)) {
                                    if (treeq.cond.desc.contains(c.desc)) {
                                        isFound = true;
                                        break;
                                    }
                                }
                            }
                            if (!isFound ) {
                                treeData.get(tree).add(new TreeCondition(key, c));
                            }
                        }
                    }
                }
            }
        }
        update = false;
    }

    Set<String> currentQG = new HashSet<>();

    public void change(Coord sz) {
        move(new Coord(sz.x-300,  100 ));
    }
}
