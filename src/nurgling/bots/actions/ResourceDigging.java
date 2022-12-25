package nurgling.bots.actions;

import haven.Coord;
import haven.Coord2d;

import nurgling.*;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;
import java.util.Arrays;

import static haven.OCache.posres;

public class ResourceDigging implements Action {
    public static ArrayList<String> shovel_tools = new ArrayList<String> ( Arrays.asList ( "shovel-m", "shovel-w" ) );
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        new Equip ( new NAlias( shovel_tools, new ArrayList<String> () ) ).run(gui);

        ArrayList<Coord2d> tiles = null;
        if(NUtils.checkName("clay", resource)) {
            tiles = Finder.findTilesInArea(new NAlias(new ArrayList<String>(Arrays.asList("water",
                    "dirt")), new ArrayList<String>()), area);
        }
        if(NUtils.checkName("sand", resource)) {
            tiles = Finder.findTilesInArea(new NAlias(new ArrayList<String>(Arrays.asList("beach")), new ArrayList<String>()), area);
        }
        transferToPile(gui);
        for ( Coord2d tile : tiles ) {
            Coord2d pos = new Coord2d ( tile.x + 5.5, tile.y + 5.5 );
            boolean cont = true;
            while(cont) {
                new PathFinder( gui, pos ).run ();
                gui.map.wdgmsg ( "click", Coord.z, pos.floor ( posres ), 1, 0 );
                while ( Math.sqrt ( Math.pow ( gui.map.player ().rc.x - pos.x, 2 ) +
                        Math.pow ( gui.map.player ().rc.y - pos.y, 2 ) ) > 1 ) {
                    Thread.sleep ( 160 );
                }
                NUtils.command ( new char[]{ 'a', 'd' } );
                gui.map.wdgmsg ( "click", Coord.z, pos.floor ( posres ), 1, 0 );
                int counter = 0;
                while ( gui.getInventory ().getFreeSpace () > 2 && ( counter < 20 || NUtils.getProg() >= 0 ) ) {
                    Thread.sleep ( 50 );
                    counter++;
                }
                NUtils.stopWithClick ();
    
                if ( NUtils.getStamina() <= 0.3 ) {
                    new Drink ( 0.9, false ).run ( gui );
                }
                if ( gui.getInventory ().getFreeSpace () <= 2 ) {
                    transferToPile(gui);
                }else{
                    cont = false;
                }
            }
        }
        transferToPile(gui);
        return new Results ( Results.Types.SUCCESS );
    }

    private void transferToPile(NGameUI gui) throws InterruptedException {
        if(NUtils.checkName("clay", resource)) {
            new TransferToPile(piles, NHitBox.get(), new NAlias("stockpile-clay"),
                    resource).run(gui);
        }
        if(NUtils.checkName("sand", resource)) {
            new TransferToPile(piles, NHitBox.get2(), new NAlias("stockpile-sand"),
                    resource).run(gui);
        }
    }
    
    
    public ResourceDigging(
            NArea area,
            NArea piles,
            NAlias resource
    ) {
        this.area = area;
        this.piles = piles;
        this.resource = resource;
    }
    
    NArea area;
    NArea piles;

    NAlias resource;
}
