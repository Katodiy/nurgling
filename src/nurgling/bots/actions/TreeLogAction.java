package nurgling.bots.actions;

import haven.Coord;
import haven.Gob;
import nurgling.*;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class TreeLogAction implements Action {
    public static ArrayList<String> lumber_tools = new ArrayList<String> (
            Arrays.asList ( "woodsmansaxe", "axe-m", "stoneaxe", "ingaxe" ) );
    public static ArrayList<String> saw_tools = new ArrayList<String> ( Arrays.asList ( "saw-m", "bonesaw" ) );
    
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        ArrayList<Gob> gobs = ( !item.get () ) ? Finder.findObjectsInArea ( new NAlias ( "log" ), area ) : Finder
                .findObjectsInArea ( new NAlias( new ArrayList<> ( Arrays.asList ( "log", "oldtrunk" ) ) ), area );
        target = new NAlias ( item.get () ? "stockpile-wblock" : "stockpile-board" );
        String name = item.get () ? "gfx/terobjs/stockpile-wblock" : "gfx/terobjs/stockpile-board";
        items = new NAlias ( new ArrayList<String> ( Arrays.asList ( item.get () ? "wblock" : "board" ) ),
                new ArrayList<String> ( Arrays.asList ( "stockpile", "cupboard" ) ) );
        for ( Gob log : gobs ) {
            while (NOCache.getgob(log)!=null) {
                PathFinder pf = new PathFinder(gui, log);
                pf.run();
                /// Цикл рубки
                /// Пить
                if(NUtils.getStamina()<0.5)
                    new Drink(0.9, false).run(gui);
                /// Одеть топор
                new Equip(new NAlias((item.get() ? lumber_tools : saw_tools))).run(gui);
                /// Выбрать рубку
                new SelectFlowerAction(log, (item.get() ? "Chop" : "Make boards"), SelectFlowerAction.Types.Gob).run(gui);
                /// Ждать пока дерево не срублено или стамина больше 0.3
                new WaitAction(() -> NUtils.getStamina() > 0.3 && NUtils.getProg() >= 0 &&
                        gui.getInventory().getNumberFreeCoord(sz) > 0 && gui.hand.isEmpty(), 50).run(gui);
                NUtils.stopWithClick();
                new TransferToPile(pile_area, NHitBox.get(name), target, items).run(gui);
            }
//            new CollectItemsToPile ( name, target, pile_area, items, area ).run ( gui );
            
        }
        new TransferToPile(pile_area, NHitBox.get(name), target, items).run(gui);
        return new Results ( Results.Types.SUCCESS );
    }
    
    public TreeLogAction(
            AtomicBoolean item,
            NArea area,
            NArea pile_area,
            Coord sz
    ) {
        this.item = item;
        this.area = area;
        this.sz = sz;
        this.pile_area = pile_area;
    }
    
    AtomicBoolean item;
    NArea area;
    NArea pile_area;
    NAlias target;
    NAlias items;
    Coord sz;
}
