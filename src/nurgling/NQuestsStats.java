package nurgling;

import haven.*;
import haven.res.lib.itemtex.ItemTex;
import nurgling.json.JSONArray;
import nurgling.json.JSONObject;
import nurgling.json.parser.JSONParser;
import nurgling.json.parser.ParseException;
import nurgling.tools.AreasID;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static haven.ItemInfo.catimgs;
import static haven.res.lib.itemtex.ItemTex.made_str;

public class NQuestsStats extends Window {
    Dropbox<String> dropbox;
    int def_h = 0;
    public static class QuesterStat{
        public Set<String> upped = new HashSet<>();
    }

    Label lp;
    static int lp_count = 0;

    Label exp;
    static int exp_count = 0;
    private static HashMap<String, QuesterStat> statHashMap = new HashMap<>();
    public NQuestsStats() {
        super(new Coord(300,300), "Quests stats");
        exp_count = 0;
        lp_count = 0;

        lp = (Label)(prev = add ( new Label("Current LP: 0")));
        def_h+=((Label)prev).sz.y;
        exp = (Label)(prev = add ( new Label("Current EXP: 0"), prev.pos("bl").adds(0, UI.scale(5))));
        def_h+=(((Label)prev).sz.y + UI.scale(5));
        dropbox = add(new Dropbox<String>(UI.scale(100), UI.scale(5), UI.scale(16)) {
            @Override
            protected String listitem(int i) {
                return new LinkedList<String>(statHashMap.keySet()).get(i);
            }

            @Override
            protected int listitems() {
                return statHashMap.keySet().size();
            }

            @Override
            protected void drawitem(GOut g, String item, int i) {
                g.text(item, Coord.z);
            }

            @Override
            public void change(String item) {
                current = item;
                needUpdate = true;
                super.change(item);
            }
        }, prev.pos("bl").adds(0, UI.scale(2)));
        def_h+=dropbox.sz.y + UI.scale(2);

        read();
        resize(new Coord(UI.scale(200), def_h));
    }

    private Collection<BufferedImage> imgs = null;
    private Tex glowon;
    String current = null;
    boolean needUpdate =false;
    @Override
    public void tick(double dt) {
        super.tick(dt);

        if(needUpdate) {
            imgs = new LinkedList<>();
            if (current != null)
                for (String target : statHashMap.get(current).upped) {
                    imgs.add(Text.std.render(target).img);
                }
            if (!imgs.isEmpty()) {
                glowon = new TexI(catimgs(1, imgs.toArray(new BufferedImage[0])));
                resize(new Coord(UI.scale(200), glowon.sz().y + def_h + UI.scale(20)));
            } else {
                resize(new Coord(UI.scale(200), def_h));
            }
            needUpdate = false;
        }
        lp.settext("Current LP: " + String.valueOf(lp_count));
        exp.settext("Current EXP: " + String.valueOf(exp_count));
    }

    @Override
    public void draw(GOut g) {
        super.draw(g);
        if (glowon != null) {
            g.image(glowon, new Coord(sz.x/8, 2*def_h));
        }

    }

    public static void checkReward(String name) {
        final String mark = "increased the local";
        final String mark_wand = "Additionally,";
        final String lp = "learning";
        final String exp = "experien";
        final String ye = "You earned";
        if (name.contains(mark))
        {
            String qname = null;
            if(name.contains(mark_wand)){
                qname = name.substring(name.indexOf(mark_wand) + mark_wand.length()+1,name.indexOf(mark)-1);
                if(!statHashMap.containsKey(qname)) {
                    statHashMap.put(qname,new QuesterStat());
                }

            }else if (name.contains(mark)){
                qname = name.substring(0,name.indexOf(mark)-1);
                if(!statHashMap.containsKey(qname)) {
                    statHashMap.put(qname,new QuesterStat());
                }
            }
            if(qname!=null){
                statHashMap.get(qname).upped.add(name.substring(name.indexOf(mark) + mark.length()+1,name.indexOf("quality")-1));
            }
            write();
        }
        else if (name.contains(lp))
        {
            lp_count += Integer.parseInt(name.substring(name.indexOf(ye) + ye.length()+1,name.indexOf(lp)-1));

        }
        else if (name.contains(exp))
        {
            exp_count += (Integer.parseInt(name.substring(name.indexOf(ye) + ye.length()+1,name.indexOf(exp)-1)));
        }
    }
    public static void write() {
        JSONObject obj = new JSONObject();
        JSONArray keys = new JSONArray();
        {
            for (String quester : statHashMap.keySet()) {
                JSONObject jquester = new JSONObject();
                jquester.put("name", quester);
                JSONArray targets = new JSONArray();
                for (String target : statHashMap.get(quester).upped)
                {
                    JSONObject jtarget = new JSONObject();
                    jtarget.put("name",target);
                    targets.add(jtarget);
                }
                jquester.put("targets", targets);
                keys.add(jquester);
            }
            obj.put("qusters",keys);
        }
        URL url = NQuestsStats.class.getProtectionDomain().getCodeSource().getLocation();
        if (url != null) {
            try {
                String path =  ((HashDirCache) ResCache.global).base + "/../"  + "./quest_stats.json";
                FileWriter file = new FileWriter(path, StandardCharsets.UTF_8);
                file.write(obj.toJSONString());
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void read (  ) {
        try {
            String path = ((HashDirCache) ResCache.global).base + "/../"  + "./quest_stats.json";
            if(!new File(path).exists())
                return;
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            JSONArray questers = (JSONArray)jsonObject.get("qusters");
            if(questers!=null) {
                Iterator<JSONObject> jquester = questers.iterator();
                while (jquester.hasNext()) {
                    QuesterStat stat = new QuesterStat();
                    JSONObject jquster = jquester.next();
                    JSONArray targets = (JSONArray)jquster.get("targets");
                    if(targets!=null) {
                        Iterator<JSONObject> jtarget = targets.iterator();
                        while (jtarget.hasNext()) {
                            JSONObject jtarg = jtarget.next();
                            stat.upped.add(jtarg.get("name").toString());
                        }
                    }
                    statHashMap.put(jquster.get("name").toString(),stat);
                }
            }
        }
        catch ( IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
