package nurgling.bots.actions;

import haven.Coord;
import haven.WItem;
import nurgling.*;
import nurgling.tools.NArea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import static haven.OCache.posres;

public class Fishing implements Action {
    NAlias bait_item = new NAlias ( Arrays.asList ( "worm", "entrails", "pupae", "larvae" ), new ArrayList<> () );


    boolean isSpining = false;
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        isSpining = false;
        while ( true) {
            /// Избавляемся от рыбы
            new TransferToPile(piles, NHitBox.getByName("stockpile"), new NAlias("fish"),
                    new NAlias(new ArrayList<>(Arrays.asList("fish")),new ArrayList<>(Arrays.asList("lure", "Wood")))).run(gui);
            /// Ищем удочку
            NAlias fish_rod_name = new NAlias("bushpole");
            NAlias primrod_name = new NAlias("primrod");
            WItem fish_rod = gui.getEquipment().getFreeSlot(fish_rod_name);
            if (fish_rod == null) {
                fish_rod = gui.getInventory().getItem(fish_rod_name);
            }
            if (fish_rod == null) {

                WItem primrod = gui.getEquipment().getFreeSlot(primrod_name);
                if (primrod == null) {
                    primrod = gui.getInventory().getItem(primrod_name);
                }
                if(primrod == null) {
                    return new Results(Results.Types.NO_WORKSTATION);
                }else {
                    fish_rod = primrod;
                    isSpining = true;
                }
            }

            /// Ремонтируем удочку и пополняем наживку
            Results.Types types;
            if(!isSpining) {
                types = new RestoreRod(baits, tools, fish_rod).run(gui).type;
            }else {
                types = new RestorePrimRod(baits, tools, fish_rod).run(gui).type;
            }
            if (types != Results.Types.SUCCESS) {
                return new Results(Results.Types.NO_ITEMS);
            }
            if(!isSpining) {
                ///Экипируем удочку
                new Equip(fish_rod_name).run(gui);
            }else {
                new Equip(primrod_name).run(gui);
            }
            /// Идем на рыболовное место
            new PathFinder(gui, fishing_place.center).run();
            /// Рыбачим
            NUtils.command(new char[]{'a', 'i'});
            gui.map.wdgmsg("click", Coord.z, gui.map.player().rc.floor(posres), 1, 0);
            if (!disableDropper.get()) {
                dropper = new Thread(new Dropper(gui, new NAlias("fish", "oldboot")), "Dropper");
                dropper.start();
            }
            if(!isSpining) {
                new WaitAction(() -> NUtils.getProg() >= 0, 50).run(gui);
            }else{
                new SpiningAction().run(gui);
            }
            NUtils.stopWithClick ();

            if(!disableDropper.get() && dropper!=null) {
            dropper.interrupt ();
            dropper.join ();
            }
        }
    }
    
    
    public Fishing(
            NArea baits,
            NArea tools,
            NArea piles,
            NArea fishing_place,
            Thread dropper,
            AtomicBoolean disableDropper
    ) {
        this.baits = baits;
        this.piles = piles;
        this.tools = tools;
        this.fishing_place = fishing_place;
        this.dropper = dropper;
        this.disableDropper = disableDropper;
    }
    
    NArea baits;
    NArea tools;
    NArea fishing_place;
    NArea piles;
    Thread dropper;

    AtomicBoolean disableDropper;
}
