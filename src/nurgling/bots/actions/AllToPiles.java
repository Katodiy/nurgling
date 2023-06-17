package nurgling.bots.actions;

import haven.Coord;
import haven.Gob;
import haven.MCache;
import nurgling.*;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;
import java.util.Arrays;

public class AllToPiles implements Action {

    NArea output = null;
    NArea input = null;

    AreasID in;
    AreasID out;

    NAlias items;

    public AllToPiles(
            NArea output,
            NArea input,
            NAlias items
    ) {
        this.output = output;
        this.input = input;
        this.items = items;
    }

    public AllToPiles(
            AreasID output,
            AreasID input,
            NAlias items
    ) {
        this.out = output;
        this.in = input;
        this.items = items;
    }
    
    
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        if(input == null){
            input = Finder.findNearestMark(in);
        }
        if(output == null)
        {
            output = Finder.findNearestMark(out);
        }
        NAlias stockpile_name;
        Coord free_space = new Coord(1,1);
        if(NUtils.checkName(items.keys.get(0),"board"))
        {
            stockpile_name = new NAlias("gfx/terobjs/stockpile-board");
            free_space = new Coord(1,4);
            items.keys.clear();
            items.keys.add("board");
        }
        else if(NUtils.checkName(items.keys.get(0),"soil"))
        {
            stockpile_name = new NAlias("gfx/terobjs/stockpile-soil");
        }
        else if(NUtils.checkName(items.keys.get(0),"worm"))
        {
            stockpile_name = new NAlias("gfx/terobjs/stockpile-soil");
        }
        else if(NUtils.checkName(items.keys.get(0),"pumpkin"))
        {
            stockpile_name = new NAlias("gfx/terobjs/stockpile-pumpkin");
            free_space = new Coord(3,3);
        }
        else if(NUtils.checkName(items.keys.get(0),"bar"))
        {
            stockpile_name = new NAlias("gfx/terobjs/stockpile-metal");
        }
        else if(NUtils.checkName(items.keys.get(0),"straw"))
        {
            stockpile_name = new NAlias("gfx/terobjs/stockpile-straw");
        }
        else if(NUtils.checkName(items.keys.get(0),"brick"))
        {
            stockpile_name = new NAlias("gfx/terobjs/stockpile-brick");
        }
        else if(NUtils.checkName(items.keys.get(0),new NAlias("leaf", "leaves")))
        {
            stockpile_name = new NAlias("gfx/terobjs/stockpile-leaf");
        }
        else
        {
            stockpile_name = new NAlias("gfx/terobjs/stockpile");
        }

        if(NUtils.checkName(items.keys.get(0),"hide"))
        {
            free_space = new Coord(2,2);
        }
        else if(NUtils.checkName(items.keys.get(0),"bone"))
        {
            free_space = new Coord(2,2);
        }
        else if(NUtils.checkName(items.keys.get(0),"block"))
        {
            free_space = new Coord(2,1);
            items.keys.clear();
            items.keys.add("block");
        }
        else if(NUtils.checkName(items.keys.get(0),"leek"))
        {
            free_space = new Coord(1,2);
        }
        NAlias target = null;
        if(NUtils.checkName(items.keys.get(0),"beet"))
        {
            if(NUtils.checkName(items.keys.get(0),"leaves"))
            {
                target = new NAlias(new ArrayList<>(Arrays.asList("leaves")), new ArrayList<>(Arrays.asList("stockpile")));
            }
            else
            {
                target = new NAlias(new ArrayList<>(Arrays.asList("beet")), new ArrayList<>(Arrays.asList("stockpile", "leaves")));
            }
        }
        else if(items.keys.get(0).contains("/"))
        {
            String new_name = items.keys.get(0).substring(items.keys.get(0).lastIndexOf("/")+1);
            items.keys.clear();
            items.keys.add(new_name);
            target = new NAlias(new ArrayList<>(Arrays.asList(items.keys.get(0))), new ArrayList<>(Arrays.asList("stockpile")));
        }


        /// Выполняем процедуру подбора для каждого элемента в массиве
        while ( !Finder.findObjectsInArea ( target, input ).isEmpty () ){
            
            if ( gui.getInventory ().getNumberFreeCoord (free_space) == 0 && !gui.getInventory ().getWItems().isEmpty () ) {
                new TransferToPile ( output, NHitBox.get(stockpile_name.keys.get(0)), stockpile_name, target ).run ( gui );
            }
            
            Gob item = Finder.findObjectInArea ( target, 2000, input );
            if(item == null)
                break;
            /// Если предмет далеко, идем к нему с помощью ПФ
            if(item.rc.dist(gui.map.player().rc)> MCache.tilesz2.x) {
                PathFinder pf = new PathFinder(gui, item);
                pf.run();
            }
            /// Подбираем предмет
            NUtils.takeFromEarth ( item );
        }
       
        new TransferToPile ( output, NHitBox.get(stockpile_name.keys.get(0)), stockpile_name, target ).run ( gui );
        return new Results ( Results.Types.SUCCESS );
    }
    
    
}
