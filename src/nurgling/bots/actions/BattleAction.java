package nurgling.bots.actions;

import haven.*;

import nurgling.*;
import nurgling.tools.Finder;

import java.util.ArrayList;
import java.util.Arrays;

import static haven.OCache.posres;

public class BattleAction implements Action {
    
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        //        try {
        Gob kritter = null;
        
        
        while ( true ) {
            Thread.sleep(50);
            NFightView fightView = NUtils.getFightView ();
            Gob gob;
            if ( fightView != null && ( gob = fightView.getCurrentGob () ) != null ) {

                if(!   NUtils.isIdleCurs()) {
                    gui.map.wdgmsg("click", Coord.z, gui.map.player().rc.floor(posres), 3, 0);
                    NUtils.waitEvent(()->!NUtils.isIdleCurs(), 50);
                }
                if ( fightView.curdisp.give.state != 1 ) {
                    NUtils.getFightView().give ();
                    NUtils.waitEvent(()->(NUtils.getFightView ()== null || (NUtils.getFightView ().curdisp!=null && NUtils.getFightView ().curdisp.give.state==1)), 50);
                    continue;
                }
                kritter = gob;
                NFightSess fs = NUtils.getFightSess ();
                NFightView fv = NUtils.getFightView ();
                double th = ( NUtils.isIt ( kritter, new NAlias( "boar" ) ) ) ? 75 : 60;
                double battle_delta = 0;
                boolean waterMode = false;
                if ( Finder.findObject (
                        new NAlias ( new ArrayList<String> ( Arrays.asList ( "snekkja" ) ), new ArrayList<> () )) != null ) {
                    battle_delta = 40;
                    waterMode = true;
                }
                else if ( Finder.findObject (
                        new NAlias ( new ArrayList<String> ( Arrays.asList ( "knarr" ) ), new ArrayList<> () )) != null ) {
                    battle_delta = 70;
                    waterMode = true;
                }
                else if ( Finder.findObject (
                        new NAlias ( new ArrayList<String> ( Arrays.asList ( "dugout" ) ), new ArrayList<> () ) ) !=
                        null ) {
                    battle_delta = 20;
                    waterMode = true;
                }
                else if ( Finder.findObject (
                        new NAlias ( new ArrayList<String> ( Arrays.asList ( "boat" ) ), new ArrayList<> () ) ) !=
                        null ) {
                    battle_delta = 30;
                    waterMode = true;
                }
                else if ( NUtils.isIt ( kritter, new NAlias ( "angler" ) ) ) {
                    battle_delta = 15;
                }
//                if ( !NUtils.isIt ( gob, new NAlias ( new ArrayList<> ( Arrays.asList ( "bear", "moos" ) ) ) ) ) {
//                    battle_delta += 2;
//                }
                NHitBox box = NHitBox.get ( gob, false );

                double dist = gob.rc.dist ( gui.getMap ().player ().rc );
                boolean cut = false;
                for ( Widget ch : fv.buffs.children () ) {
                    if ( ch instanceof Buff ) {
                        Buff bf = (Buff) ch;
                        try {
                            NUtils.waitEvent(() -> bf.res.get() != null, 50);
                        }catch (Loading e){
                            e.waitfor();
                        }
                        {
                            if (NUtils.checkName(bf.res.get().name, new NAlias(
                                    new ArrayList<>(Arrays.asList("balance", "cornered", "reeling", "dizzy")))) &&
                                    bf.ameter >= ((waterMode) ? 15 : 30)) {

                                if (dist < ((waterMode) ? ((Finder.findObject(
                                        new NAlias(new ArrayList<String>(Arrays.asList("knarr")),
                                                new ArrayList<>())) !=
                                        null) ? 69 : 130) : 109) - box.end.y) {
                                    PathFinder pf = new PathFinder(gui, gob.rc);
                                    pf.setBattleRing(gob.rc, box.end.y + battle_delta + 60);
                                    if (waterMode) {
                                        pf.setBattleRing(gob.rc, box.end.y + battle_delta + 7);
                                        pf.enableWater(waterMode);
                                        pf.ignoreGob(Finder.findObject(new NAlias(
                                                new ArrayList<String>(Arrays.asList("snekkja", "dugout", "boat", "knarr")),
                                                new ArrayList<>())));
                                        pf.run();
                                    }
//                                else {
//                                    gui.wdgmsg ( "set", 2 );
//                                    gui.wdgmsg ( "set", 3 );
//                                }
//
                                }
                                cut = true;
                                if (bf.res.get().name.contains("cornered")) {
                                    NUtils.getFightSess().useAction(2, kritter.rc);
                                } else if (bf.res.get().name.contains("dizzy")) {
                                    NUtils.getFightSess().useAction(7, kritter.rc);
                                } else if (bf.res.get().name.contains("balance")) {
                                    NUtils.getFightSess().useAction(6, kritter.rc);
                                } else if (bf.res.get().name.contains("reeling")) {
                                    NUtils.getFightSess().useAction(5, kritter.rc);
                                }
                            }
                        }
                    }
                }
                
                if ( !cut ) {
                    if ( dist <= box.end.x + battle_delta-7 || dist >= box.end.x + battle_delta + 7 ) {
                        if(dist >= box.end.x + battle_delta + 7){
                            NUtils.waitEvent(()->Utils.rtime()>=fv.atkct,500,10);
                        }
                        PathFinder pf = new PathFinder ( gui, gob.rc );
                        pf.setBattleRing ( gob.rc, box.end.y + battle_delta );
                        if ( waterMode ) {
                            pf.enableWater ( waterMode );
                            pf.ignoreGob ( Finder.findObject ( new NAlias (
                                    new ArrayList<String> ( Arrays.asList ( "snekkja", "dugout", "boat", "knarr" ) ),
                                    new ArrayList<> () ) ) );
                        }
                        pf.run ();
                        //                            gui.msg ( "Succes!" );
                        Coord2d dir = gob.rc.sub ( gui.map.player ().rc );
                        dir = dir.div ( dir.len () * 2 );
                        Coord2d pos = dir.add ( gui.map.player ().rc );
                        gui.map.wdgmsg ( "click", Coord.z, pos.floor ( posres ), 1, 0 );
                        //                            gui.msg ( String.valueOf ( gob.rc.dist ( gui.getMap ().player ().rc ) ) );
                    }
                    else {
                        while ( fs.lastUse () < 1. && fs.lastUse () > 0 && fv.current != null ) {
                            Thread.sleep ( 50 );
                        }
                        boolean attack_sc = false;
                        if ( fv.current != null ) {
                            for ( Widget ch : fv.current.buffs.children () ) {
                                if ( ch instanceof Buff ) {
                                    Buff bf = ( Buff ) ch;
                                    //                                        if ( NUtils.isIt ( kritter, new NAlias (
                                    //                                                new ArrayList<> ( Arrays.asList ( "bear", "moos" ) ) ) ) ) {
                                    //                                            if ( bf.res.get ().name.contains ( "cornered" ) ) {
                                    //                                                if ( bf.ameter >= 75 && fs.getMyIp () >= 6 ) {
                                    //                                                    NUtils.getFightSess ().useAction ( 1, kritter.rc );
                                    //                                                }
                                    //                                                else if ( bf.ameter > 55 && fs.getMyIp () >= 10 ) {
                                    //                                                    NUtils.getFightSess ().useAction ( 3, kritter.rc );
                                    //                                                }
                                    //                                            }
                                    //                                        }
                                    //
                                    //                                        else {
                                    if ( !bf.res.get ().name.contains ( "balance" ) ) {
                                        if ( bf.ameter > 50 && fs.getMyIp () >= 4 ) {

                                            NUtils.getFightSess ().useAction ( 3, kritter.rc );
                                            attack_sc = true;
                                        }
                                        //                                            else if ( bf.res.get ().name.contains ( "cornered" ) ) {
                                        if ( NUtils.isIt ( kritter, new NAlias (
                                                new ArrayList<> ( Arrays.asList ( "bear", "moos" ) ) ) ) ) {
                                            if ( bf.ameter >= 85 ) {
                                                NUtils.getFightSess ().useAction ( 1, kritter.rc );
                                            }
                                        }
                                        else {
                                            if ( bf.ameter >= th ) {
                                                NUtils.getFightSess ().useAction ( 1, kritter.rc );
                                                attack_sc = true;
                                            }
                                        }

                                        //                                            }
                                        //                                        }
                                    }
                                }
                            }
                            if ( !attack_sc ) {
                                NUtils.getFightSess ().useAction ( 0, kritter.rc );
                            }
                            
                        }
                    }
                }
            }
            else {
                if ( kritter!= null && (kritter = Finder.findObject ( kritter.id )) != null && !kritter.isTag(NGob.Tags.knocked) ) {
                    do {
                        NUtils.command(new char[]{'t'});
                        NUtils.getGameUI().map.wdgmsg("click", Coord.z, kritter.rc.floor(posres), 1, 0, 1,
                                (int) kritter.id, kritter.rc.floor(posres), 0, -1);
                        NUtils.waitEvent(() -> NUtils.getFightView() != null, 50, 15);
                        if(NUtils.getFightView() != null && NUtils.getFightView().curdisp!=null && NUtils.getFightView().curdisp.give.state == 0) {
                            NUtils.getFightView().give();
                        }
                        else if((NUtils.getFightView() != null && NUtils.getFightView().curdisp!=null && NUtils.getFightView().curdisp.give.state == 2)) {
                            NUtils.getFightView().give();
                            NUtils.waitEvent(() -> NUtils.getFightView() == null, 50, 15);
                        }
                    }while (NUtils.getFightView () == null);
                }
                else {
                    break;
                }
            }


        }
        
        return new Results ( Results.Types.SUCCESS );
    }
    
    public BattleAction(
    ) {
    }
    
    
}