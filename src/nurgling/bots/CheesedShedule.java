package nurgling.bots;


import haven.*;
import haven.render.sl.InstancedUniform;
import haven.res.gfx.hud.rosters.cow.Ochs;
import haven.res.gfx.hud.rosters.goat.Goat;
import haven.res.gfx.hud.rosters.horse.Horse;
import haven.res.gfx.hud.rosters.pig.Pig;
import haven.res.gfx.hud.rosters.sheep.Sheep;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.bots.actions.BranderAction;
import nurgling.bots.settings.IngredientSettings;
import nurgling.bots.tools.AItem;
import nurgling.json.JSONArray;
import nurgling.json.JSONObject;
import nurgling.json.parser.JSONParser;
import nurgling.json.parser.ParseException;
import nurgling.tools.AreasID;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;


public class CheesedShedule extends Bot {

    public static class CheeseBranch{

        public enum Place{
            start,
            cellar,
            inside,
            outside,
            mine
        }

        public static class Cheese{
            public Place place;
            public String name;

            public Cheese(Place place, String name) {
                this.place = place;
                this.name = name;
            }
        }

        public List<Cheese> cheeses = new LinkedList<>();

        public CheeseBranch(LinkedList<Cheese> cheeses) {
            this.cheeses = cheeses;
        }
    }
    public static List<CheeseBranch> branches = new LinkedList<>();


    public CheesedShedule(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Cheesed Schedule";
        win_sz.y = 100;
        if(branches.isEmpty()) {
            LinkedList<CheeseBranch.Cheese> creamy_camembert = new LinkedList<>();
            creamy_camembert.add(new CheeseBranch.Cheese(CheeseBranch.Place.start, "Cow's Curd"));
            creamy_camembert.add(new CheeseBranch.Cheese(CheeseBranch.Place.outside, "Creamy Camembert"));
            branches.add(new CheeseBranch(creamy_camembert));

            LinkedList<CheeseBranch.Cheese> musky_milben = new LinkedList<>();
            musky_milben.add(new CheeseBranch.Cheese(CheeseBranch.Place.start, "Cow's Curd"));
            musky_milben.add(new CheeseBranch.Cheese(CheeseBranch.Place.inside, "Tasty Emmentaler"));
            musky_milben.add(new CheeseBranch.Cheese(CheeseBranch.Place.mine, "Musky Milben"));
            branches.add(new CheeseBranch(musky_milben));

            LinkedList<CheeseBranch.Cheese> midnight_blue = new LinkedList<>();
            midnight_blue.add(new CheeseBranch.Cheese(CheeseBranch.Place.start, "Cow's Curd"));
            midnight_blue.add(new CheeseBranch.Cheese(CheeseBranch.Place.cellar, "Cellar Cheddar"));
            midnight_blue.add(new CheeseBranch.Cheese(CheeseBranch.Place.inside, "Brodgar Blue Cheese"));
            midnight_blue.add(new CheeseBranch.Cheese(CheeseBranch.Place.mine, "Jorbonzola"));
            midnight_blue.add(new CheeseBranch.Cheese(CheeseBranch.Place.cellar, "Midnight Blue Cheese"));
            branches.add(new CheeseBranch(midnight_blue));

            LinkedList<CheeseBranch.Cheese> cave_chedar = new LinkedList<>();
            cave_chedar.add(new CheeseBranch.Cheese(CheeseBranch.Place.start, "Cow's Curd"));
            cave_chedar.add(new CheeseBranch.Cheese(CheeseBranch.Place.cellar, "Cellar Cheddar"));
            cave_chedar.add(new CheeseBranch.Cheese(CheeseBranch.Place.mine, "Cave Cheddar"));
            branches.add(new CheeseBranch(cave_chedar));

            LinkedList<CheeseBranch.Cheese> sunlight = new LinkedList<>();
            sunlight.add(new CheeseBranch.Cheese(CheeseBranch.Place.start, "Cow's Curd"));
            sunlight.add(new CheeseBranch.Cheese(CheeseBranch.Place.mine, "Mothzarella"));
            sunlight.add(new CheeseBranch.Cheese(CheeseBranch.Place.cellar, "Harmesan Cheese"));
            sunlight.add(new CheeseBranch.Cheese(CheeseBranch.Place.outside, "Sunlit Stilton"));
            branches.add(new CheeseBranch(sunlight));

            LinkedList<CheeseBranch.Cheese> halloumi = new LinkedList<>();
            halloumi.add(new CheeseBranch.Cheese(CheeseBranch.Place.start, "Sheep's Curd"));
            halloumi.add(new CheeseBranch.Cheese(CheeseBranch.Place.inside, "Halloumi"));
            branches.add(new CheeseBranch(halloumi));

            LinkedList<CheeseBranch.Cheese> caciotta = new LinkedList<>();
            caciotta.add(new CheeseBranch.Cheese(CheeseBranch.Place.start, "Sheep's Curd"));
            caciotta.add(new CheeseBranch.Cheese(CheeseBranch.Place.cellar, "Feta"));
            caciotta.add(new CheeseBranch.Cheese(CheeseBranch.Place.inside, "Caciotta"));
            branches.add(new CheeseBranch(caciotta));

            LinkedList<CheeseBranch.Cheese> cabrales = new LinkedList<>();
            cabrales.add(new CheeseBranch.Cheese(CheeseBranch.Place.start, "Sheep's Curd"));
            cabrales.add(new CheeseBranch.Cheese(CheeseBranch.Place.cellar, "Feta"));
            cabrales.add(new CheeseBranch.Cheese(CheeseBranch.Place.outside, "Cabrales"));
            branches.add(new CheeseBranch(cabrales));

            LinkedList<CheeseBranch.Cheese> manchego = new LinkedList<>();
            manchego.add(new CheeseBranch.Cheese(CheeseBranch.Place.start, "Sheep's Curd"));
            manchego.add(new CheeseBranch.Cheese(CheeseBranch.Place.outside, "Pecorino"));
            manchego.add(new CheeseBranch.Cheese(CheeseBranch.Place.mine, "Manchego"));
            branches.add(new CheeseBranch(manchego));

            LinkedList<CheeseBranch.Cheese> roncal = new LinkedList<>();
            roncal.add(new CheeseBranch.Cheese(CheeseBranch.Place.start, "Sheep's Curd"));
            roncal.add(new CheeseBranch.Cheese(CheeseBranch.Place.outside, "Pecorino"));
            roncal.add(new CheeseBranch.Cheese(CheeseBranch.Place.cellar, "Gbejna"));
            roncal.add(new CheeseBranch.Cheese(CheeseBranch.Place.mine, "Roncal"));
            branches.add(new CheeseBranch(roncal));

            LinkedList<CheeseBranch.Cheese> oscypki = new LinkedList<>();
            oscypki.add(new CheeseBranch.Cheese(CheeseBranch.Place.start, "Sheep's Curd"));
            oscypki.add(new CheeseBranch.Cheese(CheeseBranch.Place.mine, "Abbaye"));
            oscypki.add(new CheeseBranch.Cheese(CheeseBranch.Place.cellar, "Zamorano"));
            oscypki.add(new CheeseBranch.Cheese(CheeseBranch.Place.inside, "Brique"));
            oscypki.add(new CheeseBranch.Cheese(CheeseBranch.Place.outside, "Oscypki"));
            branches.add(new CheeseBranch(oscypki));

            LinkedList<CheeseBranch.Cheese> robiola = new LinkedList<>();
            robiola.add(new CheeseBranch.Cheese(CheeseBranch.Place.start, "Goat's Curd"));
            robiola.add(new CheeseBranch.Cheese(CheeseBranch.Place.inside, "Banon"));
            robiola.add(new CheeseBranch.Cheese(CheeseBranch.Place.mine, "Robiola"));
            branches.add(new CheeseBranch(robiola));

            LinkedList<CheeseBranch.Cheese> picodon = new LinkedList<>();
            picodon.add(new CheeseBranch.Cheese(CheeseBranch.Place.start, "Goat's Curd"));
            picodon.add(new CheeseBranch.Cheese(CheeseBranch.Place.outside, "Bucheron"));
            picodon.add(new CheeseBranch.Cheese(CheeseBranch.Place.cellar, "Picodon"));
            branches.add(new CheeseBranch(picodon));

            LinkedList<CheeseBranch.Cheese> garrotxa = new LinkedList<>();
            garrotxa.add(new CheeseBranch.Cheese(CheeseBranch.Place.start, "Goat's Curd"));
            garrotxa.add(new CheeseBranch.Cheese(CheeseBranch.Place.outside, "Bucheron"));
            garrotxa.add(new CheeseBranch.Cheese(CheeseBranch.Place.mine, "Graviera"));
            garrotxa.add(new CheeseBranch.Cheese(CheeseBranch.Place.inside, "Gevrik"));
            garrotxa.add(new CheeseBranch.Cheese(CheeseBranch.Place.mine, "Garrotxa"));
            branches.add(new CheeseBranch(garrotxa));

            LinkedList<CheeseBranch.Cheese> formaela = new LinkedList<>();
            formaela.add(new CheeseBranch.Cheese(CheeseBranch.Place.start, "Goat's Curd"));
            formaela.add(new CheeseBranch.Cheese(CheeseBranch.Place.mine, "Chabichou"));
            formaela.add(new CheeseBranch.Cheese(CheeseBranch.Place.inside, "Chabis"));
            formaela.add(new CheeseBranch.Cheese(CheeseBranch.Place.inside, "Formaela"));
            branches.add(new CheeseBranch(formaela));

            LinkedList<CheeseBranch.Cheese> majorero = new LinkedList<>();
            majorero.add(new CheeseBranch.Cheese(CheeseBranch.Place.start, "Goat's Curd"));
            majorero.add(new CheeseBranch.Cheese(CheeseBranch.Place.mine, "Chabichou"));
            majorero.add(new CheeseBranch.Cheese(CheeseBranch.Place.outside, "Majorero"));
            branches.add(new CheeseBranch(majorero));

            LinkedList<CheeseBranch.Cheese> kasseri = new LinkedList<>();
            kasseri.add(new CheeseBranch.Cheese(CheeseBranch.Place.start, "Goat's Curd"));
            kasseri.add(new CheeseBranch.Cheese(CheeseBranch.Place.cellar, "Kasseri"));
            branches.add(new CheeseBranch(kasseri));
        }
    }
    
    
    @Override
    public void initAction ()
            throws InterruptedException {
        super.initAction ();
        TextEntry entry;
        ArrayList<String> cheeses = new ArrayList<>();
        for(CheeseBranch branch : branches){
            for(CheeseBranch.Cheese cheese : branch.cheeses){
                if(cheese.place!= CheeseBranch.Place.start)
                    cheeses.add(cheese.name);
            }
        }
        window.add(new Dropbox<String>(100, 5, 16) {
            @Override
            protected String listitem(int i) {
                return cheeses.get(i);
            }

            @Override
            protected int listitems() {
                return cheeses.size();
            }

            @Override
            protected void drawitem(GOut g, String item, int i) {
                g.text(item, Coord.z);
            }

            @Override
            public void change(String item) {
                super.change(item);
                name = item;
            }
        });


        window.add(entry = new TextEntry(60, "0"), new Coord ( 0, 20 ) );


        window.add ( new Button ( window.buttons_size, "Add" ) {
            @Override
            public void click () {
                try {
                    count = Integer.parseInt(entry.text());
                }catch (NumberFormatException e){
                    count = 0;
                    entry.settext("0");
                    gameUI.msg("Incorrect count");
                }
                if(!name.isEmpty() && count !=0 )
                    start.set(true);
                else
                    gameUI.msg("Please select cheese and result count");
            }
        }, new Coord ( 0, 40 ) );

        while ( !start.get () ) {
            Thread.sleep ( 100 );
        }
        LinkedList<Task> tasks = read();
        tasks.add(new Task(name,count));
        write(tasks);
        gameUI.msg("Task: " + name + " count:" + String.valueOf(count) + "  was added!");
    }
    
    @Override
    public void endAction () {
        start.set(false);
        super.endAction ();
    }

    public static class Task{
        public String target;
        public Integer count;

        public Task(String target, Integer count, ArrayList<Status> status) {
            this.target = target;
            this.count = count;
            this.status = status;
        }

        public static class Status{
            public String name;
            public Integer left;

            public Status(String name, Integer left) {
                this.name = name;
                this.left = left;
            }
        }
        public ArrayList<Status> status = new ArrayList<>();

        public Task(String target, Integer count) {
            this.target = target;
            this.count = count;
            for(CheeseBranch branch: branches){
                for(CheeseBranch.Cheese cheese: branch.cheeses){
                    if(cheese.name.contains(target)) {
                        for (int i = 0; !branch.cheeses.get(i).name.contains(target); i++) {
                            this.status.add(new Status(branch.cheeses.get(i).name, count));
                        }
                        this.status.add(new Status(target,count));
                        return;
                    }
                }
            }
        }
    }

    public static LinkedList<Task> read(){
        LinkedList<Task> tasks = new LinkedList<>();
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(Files.newInputStream(Paths.get("./cheese_shedule.json")), "cp1251"));
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(reader);

            if (jsonArray != null) {
                for (JSONObject jtask : (Iterable<JSONObject>) jsonArray) {
                    String target = jtask.get("name").toString();
                    Integer count = Integer.valueOf(jtask.get("count").toString());

                    JSONArray jstatuses = (JSONArray) jtask.get("status");
                    ArrayList<Task.Status> status = new ArrayList<>();
                    for (JSONObject jstatus : (Iterable<JSONObject>) jstatuses) {
                        status.add(new Task.Status(jstatus.get("name").toString(), Integer.valueOf(jstatus.get("left").toString())));
                    }
                    tasks.add(new Task(target, count, status));
                }
            }
            reader.close();
        } catch (IOException | ParseException ignored) {
        }
        return tasks;
    }

    public static void write(LinkedList<Task> tasks){
        JSONArray jtasks = new JSONArray();
        {
            for (Task task : tasks) {
                JSONObject jtask = new JSONObject();
                jtask.put("name", task.target);
                jtask.put("count", task.count);
                JSONArray jstatuses = new JSONArray();
                for(Task.Status status: task.status) {
                    JSONObject jstatus = new JSONObject();
                    jstatus.put("name", status.name);
                    jstatus.put("left", status.left);
                    jstatuses.add(jstatus);
                }
                jtask.put("status", jstatuses);
                jtasks.add(jtask);
            }
        }

        String path = "./cheese_shedule.json";
        try (FileWriter file = new FileWriter(path)) {
            //String res = obj.toJSONString();
            file.write(jtasks.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String name = "";
    Integer count = 0;
    AtomicBoolean start = new AtomicBoolean(false);
}
