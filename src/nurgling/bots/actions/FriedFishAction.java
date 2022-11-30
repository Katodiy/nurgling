package nurgling.bots.actions;

import haven.Gob;
import haven.Sprite;
import haven.WItem;
import haven.res.gfx.terobjs.roastspit.Roastspit;
import nurgling.*;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;


public class FriedFishAction implements Action {
    public FriedFishAction(NArea raw_fish) {
        this.raw_fish = raw_fish;
    }

    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        while (true) {
            ArrayList<Gob> allPow = Finder.findObjects(new NAlias("pow"));
            ArrayList<Gob> pows = new ArrayList<>();
            for (Gob gob : allPow) {
                if ((gob.getModelAttribute() & 48) == 0) {
                    Gob.Overlay ol = (gob.findol(Roastspit.class));
                    if (ol != null) {
                        pows.add(gob);
                    }
                }
            }
            new FillFuelFromPiles(2, new NAlias("block"), pows, new NAlias("block"),
                    AreasID.block, 1).run(gui);
            for (Gob gob : pows) {
                if ((gob.getModelAttribute() & 4) == 0)
                    if ((gob.getModelAttribute() & 8) == 0) {
                        new LightGob(4,gob).run(gui);
                    }
                    else{
                        if(NUtils.getStamina()<0.5)
                            new Drink((0.9),false).run(gui);
                        new SelectFlowerAction(gob, "Light My Fire", SelectFlowerAction.Types.Gob).run(gui);
                        NUtils.waitEvent(() -> NUtils.getProg() >= 0, 50);
                        NUtils.waitEvent(() -> NUtils.getProg() < 0, 10000);
                    }
            }
            for (Gob gob : pows) {
                Gob.Overlay ol = (gob.findol(Roastspit.class));
                String content = ((Roastspit) ol.spr).getContent();
                if (gui.getInventory().getFreeSpace() < 4) {
                    new FillContainers(new NAlias("Spitroast"), new NAlias("table"), new ArrayList<>()).run(gui);
                }
                if (content != null) {
                    if (!NUtils.checkName(((Roastspit) ol.spr).getContent(), new NAlias("raw"))) {
                        /// Собираем готовое
                        new PathFinder(gui, gob).run();
                        new SelectFlowerAction(gob, "Carve", SelectFlowerAction.Types.Roastspit).run(gui);
                        NUtils.waitEvent(() -> NUtils.getProg() >= 0, 50);
                        NUtils.waitEvent(() -> NUtils.getProg() < 0, 10000);
                    }
                }
            }
            new FillContainers(new NAlias("Spitroast"), new NAlias("table"), new ArrayList<>()).run(gui);

            for (Gob gob : pows) {
                Gob.Overlay ol = (gob.findol(Roastspit.class));
                String content = ((Roastspit) ol.spr).getContent();
                if (content == null) {
                    if (gui.getInventory().getItems(new NAlias("fish")).isEmpty()) {
                        new TakeFromPile(new NAlias("fish"), gui.getInventory().getFreeSpace(), new NAlias("fish"), raw_fish).run(gui);
                    }
                    new TakeToHand(new NAlias("fish")).run(gui);
                    new PathFinder(gui, gob).run();
                    NUtils.activateRoastspit(ol);
                    NUtils.waitEvent(() -> NUtils.getGameUI().hand.isEmpty(), 100);
                }
            }
            new TransferToPile(raw_fish, NHitBox.get(), new NAlias("fish"), new NAlias("fish")).run(gui);
            double current = 0;
            for (Gob gob : pows) {
                Gob.Overlay ol = (gob.findol(Roastspit.class));
                String content = ((Roastspit) ol.spr).getContent();
                if (content != null) {
                    if (NUtils.checkName(((Roastspit) ol.spr).getContent(), new NAlias("raw"))) {
                        /// Собираем готовое
                        new PathFinder(gui, gob).run();
                        new SelectFlowerAction(gob, "Turn", SelectFlowerAction.Types.Roastspit).run(gui);
                        NUtils.waitEvent(() -> NUtils.getProg() >= 0, 50);
                        current = NUtils.getProg();
                        long start_id = NUtils.getUI().getTickId();
                        while (NUtils.checkName(((Roastspit) ol.spr).getContent(), new NAlias("raw")) && (gob.getModelAttribute() & 4) != 0 && NUtils.getProg()>=0 && current <= NUtils.getProg() && (NUtils.getUI().getTickId() - start_id) < 100000) {
                            current = NUtils.getProg();
                            Thread.sleep(50);
                        }
                        break;
                    }
                }
            }
        }
    }


    NArea raw_fish;
}
