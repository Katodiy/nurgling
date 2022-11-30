package nurgling.bots.settings;

import haven.Pair;
import haven.*;
import haven.res.lib.itemtex.ItemTex;
import nurgling.NGob;
import nurgling.NMapView;
import nurgling.NUtils;
import nurgling.tools.AreasID;

import java.awt.image.BufferedImage;
import java.util.LinkedList;

import static haven.res.lib.itemtex.ItemTex.made;
import static haven.res.lib.itemtex.ItemTex.made_str;

public class AreaIconSelecter extends Widget {
    TextEntry lab;
    TextEntry name;
    Icon icon;
    Button find1;
    Button find2;
    Dropbox<String> dropbox;

    Pair<String, String> value;

    public void setValue(Pair<String, String> value){
        this.value = value;
    }

    public void setAreaID(AreasID id){
        this.value = new Pair<>(id.toString(),AreasID.get(id));
        dropbox.change(value.b);
    }

    public AreaIconSelecter(AreasID id) {
        this.value = new Pair<>(id.toString(),AreasID.get(id));

        this.sz = new Coord(200,200);
        icon = (Icon) (prev = add(new Icon()));
        prev =dropbox = add(new Dropbox<String>(100, 5, 16) {
            @Override
            protected String listitem(int i) {
                return new LinkedList<String>(made_str.keySet()).get(i);
            }

            @Override
            protected int listitems() {
                return made_str.keySet().size();
            }

            @Override
            protected void drawitem(GOut g, String item, int i) {
                g.text(item, Coord.z);
            }

            @Override
            public void change(String item) {
                icon.img = ItemTex.find(item);
                if(icon.img!=null) {
                    icon.tex = new TexI(icon.img);
                    name.rsettext(item);
                    MessageBuf ret = made_str.get(item);
                    if(ret!=null) {
                        super.change(item);
                        lab.rsettext(String.valueOf(NGob.calcMarker(ret)));
                    }
                    value.b = item;
                    AreasID.set(AreasID.valueOf(value.a), item);
                    AreasID.write();
                }
            }
        }, prev.pos("ur").adds(5, 2));
        prev = name = add(new TextEntry(110,""), prev.pos("ur").adds(5, -2));
        prev = find1 = add(new Button(50,"Set"){
            @Override
            public void click() {
                dropbox.change(name.text());
            }
        }, prev.pos("ur").adds(5, -2));
        prev = lab = add(new TextEntry(50,""), prev.pos("ur").adds(5, 2));
        find2 = add(new Button(50,"Set"){
            @Override
            public void click() {
                for(String name: made_str.keySet() ){
                    if(NGob.calcMarker(made_str.get(name))==Long.parseLong(lab.text()))
                        dropbox.change(name);
                }

            }
        }, prev.pos("ur").adds(5, -2));
        pack();

    }

    @Override
    public void show() {
        if(ItemTex.isInited) {
            if (value.b != null)
                dropbox.change(value.b);
            super.show();
        }
    }

    class Icon extends Widget{
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
