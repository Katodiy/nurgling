package nurgling;

import haven.*;
import nurgling.json.JSONArray;
import nurgling.json.JSONObject;
import nurgling.json.parser.JSONParser;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;

public class NCharacterInfo {

    public final Constipation constipation = new Constipation();

    static NCharacterInfo instance;

    public NCharacterInfo() {
        instance = this;
    }

    public static class Constipation {
        public NWItem lastItem = null;
        public Constipation() {
            variety_food = new ArrayList<String>();
        }

        public ArrayList<String> variety_food = new ArrayList<String>();

        String chrid;

        public final List<Data> els = new ArrayList<Data>();
        private Integer[] order = {};

        public void updateFood(ResData t, double a) {
        }
        public void update(ResData t, double a)
        {
            /// Учет съеденной еды
            if(lastItem!=null) {
                String candidate = lastItem.item.res.get().name;
                if (!variety_food.contains(candidate))
                    variety_food.add(candidate);
                lastItem = null;
            }
            prev: {
                for(Iterator<Data> i = els.iterator(); i.hasNext();) {
                    Data el = i.next();
                    if(!Utils.eq(el.rd, t))
                        continue;
                    if(a == 1.0)
                        i.remove();
                    else
                        el.update(a);
                    break prev;
                }
                els.add(new Data(t, a));
            }
            order();
        }

        private void order() {
            int n = els.size();
            order = new Integer[n];
            for(int i = 0; i < n; i++)
                order[i] = i;
            Arrays.sort(order, (a, b) -> (ecmp.compare(els.get(a), els.get(b))));
        }

        private static final Comparator<Data> ecmp = (a, b) -> {
            if(a.value < b.value)
                return(-1);
            else if(a.value > b.value)
                return(1);
            return(0);
        };

        public Data get(int i) {
            return els.size() > i ? els.get(i) : null;
        }

        public static class Data {
            private final HashMap<Class, BufferedImage> renders = new HashMap<>();
            public final Indir<Resource> res;
            private ResData rd;
            public double value;

            public Data(ResData rd, double value) {
                this.rd = rd;
                this.res = rd.res;
                this.value = value;
            }

            public void update(double a) {
                value = a;
                renders.clear();
            }

            private BufferedImage render(Class type, Function<Data, BufferedImage> renderer) {
                if(!renders.containsKey(type)) {
                    renders.put(type, renderer.apply(this));
                }
                return renders.get(type);
            }
        }

        private final HashMap<Class, Function<Data, BufferedImage>> renderers = new HashMap<>();

        public void addRenderer(Class type, Function<Data, BufferedImage> renderer) {
            renderers.put(type, renderer);
        }

        public boolean hasRenderer(Class type) {
            return renderers.containsKey(type);
        }

        public BufferedImage render(Class type, Data data) {
            try {
                return renderers.containsKey(type) ? data.render(type, renderers.get(type)) : null;
            } catch (Loading ignored) {}
            return null;
        }

        public static void write() {
            if(instance !=null) {
                URL url = NUtils.class.getProtectionDomain().getCodeSource().getLocation();

                if (url != null) {
                    try {
                        String path = url.toURI().getPath().substring(0, url.toURI().getPath().lastIndexOf("/"));
                        JSONObject obj = new JSONObject();
                        JSONArray varietyData = new JSONArray();
                        for (String item : instance.constipation.variety_food)
                            varietyData.add(item);
                        obj.put ( "variety_food", varietyData );

                        FileWriter file = new FileWriter(path + "/" + NUtils.getGameUI().chrid + "_info.dat");
                        file.write(obj.toJSONString());
                        file.close();

                    } catch (URISyntaxException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        public static void init(){
            if(instance !=null) {
                if(instance.constipation.chrid!=null){
                    try {
                        URL url = NUtils.class.getProtectionDomain().getCodeSource().getLocation();
                        String path = url.toURI().getPath().substring(0, url.toURI().getPath().lastIndexOf("/"));

                        if(new File(path + "/" + NUtils.getGameUI().chrid + "_info.dat").exists()) {
                            BufferedReader reader = new BufferedReader (
                                    new InputStreamReader (Files.newInputStream(Paths.get(path + "/" + NUtils.getGameUI().chrid + "_info.dat")), "cp1251" ) );
                            JSONParser parser = new JSONParser();
                            JSONObject jsonObject = (JSONObject) parser.parse(reader);

                            // loop array
                            JSONArray msg = (JSONArray) jsonObject.get("variety_food");
                            instance.constipation.variety_food.addAll(msg);
                        }
                    }
                    catch ( IOException | nurgling.json.parser.ParseException e ) {
                        e.printStackTrace ();
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
