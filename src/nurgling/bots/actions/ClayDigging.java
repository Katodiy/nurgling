package nurgling.bots.actions;

import haven.Coord;
import haven.Coord2d;

import nurgling.*;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;
import java.util.Arrays;

import static haven.OCache.posres;

public class ClayDigging implements Action {
    public static ArrayList<String> shovel_tools = new ArrayList<String> ( Arrays.asList ( "shovel-m", "shovel-w" ) );
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        new Equip ( new NAlias( shovel_tools, new ArrayList<String> () ) ).run(gui);

        ArrayList<Coord2d> tiles = Finder.findTilesInArea ( new NAlias (new ArrayList<String>(Arrays.asList ( "water",
                "dirt")),new ArrayList<String>()), tree );
        new TransferToPile ( piles, NHitBox.get (  ), new NAlias ( "stockpile-clay" ),
                new NAlias ("clay") ).run(gui);
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
                    new TransferToPile ( piles, NHitBox.get ( ), new NAlias ( "stockpile-clay" ),
                            new NAlias ( "clay" ) ).run ( gui );
                }else{
                    cont = false;
                }
            }
        }
        new TransferToPile ( piles, NHitBox.get (  ), new NAlias ( "stockpile-clay" ),
                new NAlias ("clay") ).run(gui);
        return new Results ( Results.Types.SUCCESS );
    }
    
    
    public ClayDigging(
            NArea tree,
            NArea piles
    ) {
        this.tree = tree;
        this.piles = piles;
    }
    
    NArea tree;
    NArea piles;
}
