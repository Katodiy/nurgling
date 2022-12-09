package nurgling.bots;


import haven.*;
import haven.res.gfx.hud.rosters.cow.Ochs;
import haven.res.gfx.hud.rosters.goat.Goat;
import haven.res.gfx.hud.rosters.pig.Pig;
import haven.res.gfx.hud.rosters.sheep.Sheep;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.bots.actions.BackerAction;
import nurgling.bots.actions.BranderAction;
import nurgling.tools.AreaSelecter;
import nurgling.tools.AreasID;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;


public class BrandedBot extends Bot {

    public BrandedBot(NGameUI gameUI ) {
        super ( gameUI );
        win_title = "Brander";
        win_sz.y = 100;
        
        runActions.add ( new BranderAction( area ) );
    }
    
    
    @Override
    public void initAction ()
            throws InterruptedException {
        super.initAction ();
        ArrayList<String> animals = new ArrayList<>();
        animals.add(AreasID.pigs.toString());
        animals.add(AreasID.goats.toString());
        animals.add(AreasID.sheeps.toString());
        animals.add(AreasID.cows.toString());
        window.add(new Dropbox<String>(100, 5, 16) {
            @Override
            protected String listitem(int i) {
                return animals.get(i);
            }

            @Override
            protected int listitems() {
                return animals.size();
            }

            @Override
            protected void drawitem(GOut g, String item, int i) {
                g.text(item, Coord.z);
            }

            @Override
            public void change(String item) {
                super.change(item);
                area.id = AreasID.valueOf(item);
                switch (item) {
                    case "goats":
                        area.pred = new Predicate<Gob>() {
                            @Override
                            public boolean test(Gob gob) {
                                Goat p1 = (Goat) (NUtils.getAnimalEntity(gob, Goat.class));
                                return !p1.dead && !p1.owned;
                            }
                        };
                        area.animal = new NAlias("goat");
                        area.cattleRoster = Goat.class;
                        break;
                    case "pigs":
                        area.pred = new Predicate<Gob>() {
                            @Override
                            public boolean test(Gob gob) {
                                Pig p1 = (Pig) (NUtils.getAnimalEntity(gob, Pig.class));
                                return !p1.dead && !p1.owned;
                            }
                        };
                        area.animal = new NAlias("pig");
                        area.cattleRoster = Pig.class;
                        break;
                    case "sheeps":
                        area.pred = new Predicate<Gob>() {
                            @Override
                            public boolean test(Gob gob) {
                                Sheep p1 = (Sheep) (NUtils.getAnimalEntity(gob, Sheep.class));
                                return !p1.dead && !p1.owned;
                            }
                        };
                        area.animal = new NAlias("sheep");
                        area.cattleRoster = Sheep.class;
                        break;
                    case "cows":
                        area.pred = new Predicate<Gob>() {
                            @Override
                            public boolean test(Gob gob) {
                                Ochs p1 = (Ochs) (NUtils.getAnimalEntity(gob, Ochs.class));
                                return !p1.dead && !p1.owned;
                            }
                        };
                        area.animal = new NAlias("cattle");
                        area.cattleRoster = Ochs.class;
                    break;
                }
            }
        });

        window.add ( new Button ( window.buttons_size, "Start" ) {
            @Override
            public void click () {
                if(area.id!=null)
                    start.set(true);
                else
                    gameUI.msg("Please select animal Area");
            }
        }, new Coord ( 0, 20 ) );

        while ( !start.get () ) {
            Thread.sleep ( 100 );
        }
    }
    
    @Override
    public void endAction () {
        start.set(false);
        super.endAction ();
    }

    BranderAction.AnimalArea area = new BranderAction.AnimalArea();

    AtomicBoolean start = new AtomicBoolean(false);
}
