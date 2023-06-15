package nurgling;

import haven.*;

import java.util.ArrayList;
import java.util.Set;

public abstract class NEntryListSet extends Widget {

    public abstract void nsave();
    public abstract void nchange();
    public abstract void ndelete();

    public String get(){
        return name.text().toString();
    }

    public NEntryListSet(Set<String> names) {
        prev = add(new Label("Settings:"));
        prev = name = add(new TextEntry(UI.scale(110),""), prev.pos("bl").add(0,UI.scale(5)));

        prev = add(new Button(UI.scale(50),"Add") {
            @Override
            public void click() {
                nsave();
                if(!name.text().isEmpty())
                {
                    db.change(name.text());
                }
            }
        }, prev.pos("bl").add(0,UI.scale(5)));

        add(new Button(UI.scale(50),"Delete") {
            @Override
            public void click() {
                if(!name.text().isEmpty())
                {
                    ndelete();
                }
            }
        }, prev.pos("ur").add(UI.scale(5), 0));

        prev = db = add(new Dropbox<String>(UI.scale(100), UI.scale(5), UI.scale(16)) {
            @Override
            protected String listitem(int i) {
                return (new ArrayList<String>(names)).get(i);
            }

            @Override
            protected int listitems() {
                return names.size();
            }

            @Override
            protected void drawitem(GOut g, String item, int i) {
                g.text(item, Coord.z);
            }

            @Override
            public void change(String item) {
                super.change(item);
                name.settext(item);
                if(!name.text().isEmpty())
                    nchange();
            }
        }, prev.pos("bl").add(0,UI.scale(5)));
        pack();
    }

    Dropbox<String> db;
    TextEntry name;

    public void update(String value) {
        name.settext(value);
        db.change(value);
    }
}
