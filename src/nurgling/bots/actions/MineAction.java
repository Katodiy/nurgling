package nurgling.bots.actions;

import haven.*;
import nurgling.*;
import nurgling.tools.Finder;
import nurgling.tools.NArea;


import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import static haven.OCache.posres;

public class MineAction implements Action {
    private class Tile {
        public Coord2d coord;
        public boolean isAvailible = true;
        
        public Tile (
                Coord2d coord,
                boolean isAvailible
        ) {
            this.coord = coord;
            this.isAvailible = isAvailible;
        }
    }
    
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        new Equip(new NAlias("axe")).run(gui);
        Coord2d pos = new Coord2d ( area.begin.x + MCache.tilesz.x / 2, area.begin.y + MCache.tilesz.x / 2 );
        ArrayList<Tile> arrayList = new ArrayList<> ();
//        System.out.println ( "++++" );
        
        for ( double x = pos.x ; x <= area.end.x ; x += MCache.tilesz.x ) {
            for ( double y = pos.y ; y <= area.end.y ; y += MCache.tilesz.y ) {
                arrayList.add ( new Tile ( new Coord2d ( x, y ), true ) );
            }
        }
        for ( int i = 0 ; i < arrayList.size () ; i++ ) {
            arrayList.sort ( new Comparator<Tile> () {
                @Override
                public int compare (
                        Tile lhs,
                        Tile rhs
                ) {
                    double dist1 = gui.map.player ().rc.dist ( lhs.coord );
                    double dist2 = gui.map.player ().rc.dist ( rhs.coord );
                    return Double.compare ( dist1, dist2 );
                }
            } );
            for ( Tile tile : arrayList ) {
                if ( tile.isAvailible ) {
                    Coord tile_pos = ( new Coord2d ( tile.coord.x / 11, tile.coord.y / 11 ) ).floor ();
                    Resource res_beg = gui.ui.sess.glob.map.tilesetr ( gui.ui.sess.glob.map.gettile ( tile_pos ) );
                    if(NUtils.getFightView().getCurrentGob() != null){
                        return new Results(Results.Types.FIGHT);
                    }
                    ArrayList<Gob> mss = Finder.findObjects(new NAlias("minebeam", "column", "towercap", "ladder", "minesupport"));
                    for(Gob ms: mss) {

                        if ((NUtils.checkName(ms.getResName(), new NAlias(new ArrayList<>(Arrays.asList("ladder", "minesupport", "towercap")))) && ms.rc.dist(tile.coord) <= 100) ||
                                (NUtils.checkName(ms.getResName(), new NAlias(new ArrayList<>(Collections.singletonList("minebeam")))) && ms.rc.dist(tile.coord) <= 150) ||
                                (NUtils.checkName(ms.getResName(), new NAlias(new ArrayList<>(Collections.singletonList("column")))) && ms.rc.dist(tile.coord) <= 125)
                        ) {
                            NGobHealth attr = (NGobHealth) ms.getattr(NGobHealth.class);
                            if(attr!=null) {
                                if (attr.hp <= 0.25) {
                                    return new Results(Results.Types.LOW_COLUMN_HP);
                                }
                            }
                        }
                    }
                    if ( res_beg != null ) {
                        if ( NUtils.checkName ( res_beg.name, new NAlias( "rock" ) ) ) {
                            Coord2d target_pos = new Coord2d ( tile.coord.x, tile.coord.y );
                            PathFinder pf = new PathFinder( gui, target_pos );
                            pf.setPhantom ( target_pos, NHitBox.getByName ( "cupboard" ) );
                            pf.setHardMode(true);
                            pf.run ();
                            if ( NUtils.getStamina() < 0.5 ) {
                                if(new Drink ( 0.9, false ).run ( gui ).type== Results.Types.Drink_FAIL)
                                    return new Results(Results.Types.Drink_FAIL);
                            }
                            NUtils.command ( new char[]{ 'a', 'm' } );
                            gui.map.wdgmsg ( "sel", tile_pos, tile_pos, 0 );
                            NUtils.waitEvent(()->NUtils.getProg()>=0,50);
                            NUtils.waitEvent(()->NUtils.getProg()<0 || NUtils.getStamina()<0.3,20000);
                            if(NUtils.getFightView().getCurrentGob() != null){
                                return new Results(Results.Types.FIGHT);
                            }
                            gui.map.wdgmsg ( "click", Coord.z, gui.map.player ().rc.floor ( posres ), 3, 0 );
                            Thread.sleep ( 100 );
                            res_beg = gui.ui.sess.glob.map.tilesetr ( gui.ui.sess.glob.map.gettile ( tile_pos ) );
                            if ( res_beg != null ) {
                                if ( !NUtils.checkName ( res_beg.name, new NAlias ( "rock" ) ) ) {
                                    tile.isAvailible = false;
                                }
                            }
                            Gob bolder = Finder.findObject ( new NAlias ( "bumlings" ) );

                            if ( bolder != null && bolder.rc.dist(gui.map.player().rc)<=25 ) {
                                while ( Finder.findObject ( bolder.id ) != null ) {
                                    if ( NUtils.getStamina() < 0.5 ) {
                                        if(new Drink ( 0.9, false ).run ( gui ).type== Results.Types.Drink_FAIL)
                                            return new Results(Results.Types.Drink_FAIL);
                                    }
                                    new SelectFlowerAction ( bolder, "Chip", SelectFlowerAction.Types.Gob ).run ( gui );
                                    NUtils.waitEvent(()->NUtils.getProg()>=0,50);
                                    NUtils.waitEvent(()->NUtils.getProg()<0 || NUtils.getStamina()<0.3,10000);
                                }
                            }
                            break;
                        }
                        else {
                            tile.isAvailible = false;
                        }
                    }
                }
            }
        }
        int counter = 0;
        Coord2d p_pos = gui.getMap ().player ().rc;
        while ( true ) {
            Thread.sleep ( 100 );
            if ( NUtils.getProg() >= 0 || p_pos.dist ( gui.getMap ().player ().rc ) > 0 ) {
                p_pos = gui.getMap ().player ().rc;
                counter = 0;
            }
            else {
                p_pos = gui.getMap ().player ().rc;
                counter += 1;
                if ( counter == 10 ) {
                    break;
                }
            }
        }
        
        return new Results ( Results.Types.SUCCESS );
    }
    
    public MineAction(NArea area ) {
        this.area = area;
    }
    
    NArea area;
}
