package nurgling.bots.settings;

import haven.*;
import haven.res.lib.itemtex.ItemTex;
import haven.res.lib.layspr.Layered;
import nurgling.NConfiguration;
import nurgling.NGob;
import nurgling.NUtils;
import nurgling.bots.tools.AItem;
import nurgling.bots.tools.Ingredient;
import nurgling.tools.AreasID;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static haven.res.lib.itemtex.ItemTex.made_str;

public class IngredientSettings extends Widget {

    public static Map<String, AItem> data = new HashMap<>();
    Icon icon;
    AItem item;

    AreaViewer bareter_in;
    AreaViewer bareter_out;
    AreaViewer area_in;
    AreaViewer area_out;
    TextEntry min_q;

    String name;
    public IngredientSettings() {
        item = new AItem();
        icon = (Icon)(prev = add(new Icon()));
        int y = 10;
        prev = add(new Label("Barter (In):"), prev.pos("bl").adds(0, y));
        Widget right = add( new AreaViewer(item.barter_in), prev.pos("ur").adds(15, 2));
        prev = right;
        bareter_in = (AreaViewer)prev;
        prev = add(new Label("Barter (Out):"), (new Coord(0, right.pos("bl").y)).add(0, y));
        right = prev = add(new AreaViewer(item.barter_out), right.pos("bl").adds(0, y));
        bareter_out = (AreaViewer)prev;
        prev = add(new Label("In:"), (new Coord(0, right.pos("bl").y)).add(0, y));
        right = prev = add(new AreaViewer(item.area_in), right.pos("bl").adds(0, y));
        area_in = (AreaViewer)prev;
        prev = add(new Label("Out:"), (new Coord(0, right.pos("bl").y)).add(0, y));
        right = prev = add(new AreaViewer(item.area_out), right.pos("bl").adds(0, y));
        area_out = (AreaViewer)prev;
        prev = add(new Label("Min quality:"), (new Coord(0, right.pos("bl").y)).add(0, y));
        prev = add(min_q = new TextEntry(60, ""), prev.pos("bl").add(0,5));
        add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().ingrTh.put(name,Integer.valueOf(min_q.text()));
                NConfiguration.getInstance().write();
            }
        },prev.pos("ur").add(5,-2));
        pack();
    }

    public void drop(WItem target) {
        if(target.item.spr instanceof Layered){
            icon.img = ((Layered) target.item.spr).image();
        }
        else {
            icon.img = target.item.res.get().layer(Resource.imgc).scaled();
        }
        if(icon.img!=null) {
            name = NUtils.getInfo(target);
            item = data.get(name);
            if(item == null) {
                item = new AItem();
                data.put(name, item);
            }
            bareter_in.dropbox.change(item.barter_in.toString());
            bareter_out.dropbox.change(item.barter_out.toString());
            area_in.dropbox.change(item.area_in.toString());
            area_out.dropbox.change(item.area_out.toString());
            if(NConfiguration.getInstance().ingrTh.containsKey(name))
                min_q.settext(String.valueOf(NConfiguration.getInstance().ingrTh.get(name)));
            else
                min_q.settext("0");
            icon.tex = new TexI(icon.img);
        }
    }


    public void save(){
        if(NUtils.getGameUI()!= null) {
            item.barter_in = bareter_in.current;
            item.barter_out = bareter_out.current;
            item.area_in = area_in.current;
            item.area_out = area_out.current;
        }
    }

    class AreaViewer extends Widget{
        Icon icon;
        TextEntry name;
        Dropbox<String> dropbox;
        public AreasID current;
        public AreaViewer(AreasID val) {
            current = val;

            prev = dropbox = add(new Dropbox<String>(100, 5, 16) {
                @Override
                protected String listitem(int i) {
                    return Stream.of(AreasID.values())
                            .map(Enum::name)
                            .collect(Collectors.toList()).get(i);
                }

                @Override
                protected int listitems() {
                    List<String> enumNames = Stream.of(AreasID.values())
                            .map(Enum::name)
                            .collect(Collectors.toList());
                    return enumNames.size();
                }

                @Override
                protected void drawitem(GOut g, String item, int i) {
                    g.text(item, Coord.z);
                }

                @Override
                public void change(String item) {
                    try {
                        current = AreasID.valueOf(item);

                        icon.img = ItemTex.find(AreasID.valueOf(item));
                        if (icon.img != null) {
                            icon.tex = new TexI(icon.img);
                            name.settext(item);
                        } else {
                            name.settext("");
                            icon.tex = null;
                        }
                        super.change(item);
                    }catch (IllegalArgumentException e){
                        NUtils.getGameUI().msg("INCORRECT AREA");
                    }
                }
            });
            prev = icon = add(new Icon(), prev.pos("ur").adds(5, 0));

            name = add(new TextEntry(110,""), prev.pos("ur").adds(5, -2));
            prev = name;
            prev = add(new Button(50,"Set"){
                @Override
                public void click() {
                    dropbox.change(name.text());
                }
            }, prev.pos("ur").adds(5, -2));
            dropbox.change(current.toString());
            pack();
        }
    }

    static class Icon extends Widget{
        BufferedImage img;
        Tex tex = null;

        public Icon(String path) {
            img = ItemTex.find(path);
            tex = new TexI(img);
            this.sz = new Coord(32,32);
        }

        public Icon() {
            this.sz = new Coord(32,32);
        }

        @Override
        public void draw(GOut g) {
            if(tex!=null) {
                g.image(tex, Coord.z);
            }
        }
    }
}
