package nurgling.tools;


import haven.*;
import nurgling.*;
import nurgling.NExceptions.NoFreeSpace;


import static haven.OCache.posres;

public class PileMaker {
    NArea area;
    NHitBox hitBox;
    Thread pfThread;
    PathFinder pathFinder;
    NGameUI gameUI;
    NAlias pile_name;
    NAlias item_name;
    double q = -1;
    
    public PileMaker (
            NGameUI gui,
            NArea area,
            NHitBox hitBox,
            NAlias name
    ) {
        this.item_name = null;
        this.pile_name = name;
        this.gameUI = gui;
        this.area = area;
        this.hitBox = hitBox;
    }
    
    public PileMaker (
            NGameUI gui,
            NArea area,
            NHitBox hitBox,
            NAlias name,
            double q
    ) {
        this.item_name = null;
        this.pile_name = name;
        this.gameUI = gui;
        this.area = area;
        this.hitBox = hitBox;
        this.q = q;
    }
    
    public NAlias setItemName ( NAlias name ) {
        item_name = name;
        return item_name;
    }
    
    public Gob create ()
            throws InterruptedException, NoFreeSpace {
        double dx = (hitBox.end.x-hitBox.begin.x)/2.;
        double dy = (hitBox.end.y-hitBox.begin.y)/2.;
        hitBox = new NHitBox(new Coord2d(-dx,-dy),new Coord2d(dx,dy));
        Coord2d target_coord = Finder.findPlace ( hitBox, area, "" );
        hitBox.correct ( target_coord, 0 );
        pathFinder = new PathFinder ( gameUI, target_coord );
        pathFinder.setPhantom ( target_coord, hitBox);
        pathFinder.setHardMode ( true );
        pathFinder.run ();
        WItem itemForPile = null;
        if(gameUI.hand.isEmpty () && gameUI.vhand == null) {
            if (item_name == null) {
                itemForPile = gameUI.getInventory().getItem(pile_name, q);
            } else {
                itemForPile = gameUI.getInventory().getItem(item_name, q);
            }
        }else{
            itemForPile = gameUI.vhand;
        }
        return placePile ( gameUI, hitBox, target_coord, itemForPile );
    }
    
    public Gob placePile (
            GameUI gameUI,
            NHitBox hitBox,
            Coord2d target_coord,
            WItem itemForPile
    )
            throws InterruptedException {
        if ( gameUI.hand.isEmpty () ) {
            itemForPile.item.wdgmsg ( "take", new Coord ( itemForPile.sz.x / 2, itemForPile.sz.y / 2 ) );
        }
        while ( gameUI.hand.isEmpty () ) {
            Thread.sleep ( 100 );
        }
        gameUI.map.wdgmsg ( "itemact", itemForPile.item.sz, target_coord.floor ( posres ), 0 );
        
        Thread.sleep ( 300 );
        if ( NFlowerMenu.instance != null ) {
            NFlowerMenu.instance.selectInCurrent ( "Pile" );
            Thread.sleep ( 300 );
        }
        gameUI.map.wdgmsg ( "place", target_coord.floor ( posres ), 0, 1, 0 );
        while ( !Finder.isGobInArea ( NHitBox.getDummy(target_coord) ) ) {
            Thread.sleep ( 100 );
        }

        return Finder.findObject ( pile_name );
    }
}

