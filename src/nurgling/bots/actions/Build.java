package nurgling.bots.actions;

import haven.Coord;
import haven.Coord2d;
import haven.Gob;
import haven.Window;

import nurgling.*;
import nurgling.NExceptions.NoFreeSpace;
import nurgling.bots.tools.CraftCommand;
import nurgling.bots.tools.Ingredient;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;
import java.util.Arrays;

public class Build implements Action {
    public static NAlias lumber_tools = new NAlias( new ArrayList<String> (
            Arrays.asList ( "woodsmansaxe", "axe-m", "stoneaxe" ) ));
    public static NAlias saw_tools =new NAlias( new ArrayList<String> ( Arrays.asList ( "saw-m", "bonesaw" ) ) );

    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        int[] needed = new int[command.ingredients.size ()];

        NHitBox hitbox = null;
        NHitBox phantom = null;
        double rotation = 0;
        
        while ( true ) {
            Gob buildedObj = Finder.findObjectInArea ( new NAlias ( "consobj" ), 1000, workArea );
            if ( buildedObj == null ) {
                if(!NUtils.checkName("gfx/terobjs/trellis",name)) {
                    hitbox = NHitBox.get(name);
                    phantom = NHitBox.get(name);
                }else{
                    hitbox = NHitBox.getByName(name);
                    phantom = NHitBox.get();
                }
                if ( NUtils.checkName ( "gfx/terobjs/chest", new NAlias( name ) ) ) {
                    phantom = NHitBox.get ();
                    if ( ( workArea.end.x - workArea.begin.x ) > ( workArea.end.y - workArea.begin.y ) ) {
                        hitbox.rotate90 ();
                        phantom.rotate90 ();
                        rotation = Math.PI / 2;
                    }
                }
                else if ( NUtils.checkName ( "gfx/terobjs/trellis", new NAlias ( name ) ) ) {
                    phantom = NHitBox.get ( );
                    if ( ( workArea.end.x - workArea.begin.x ) > ( workArea.end.y - workArea.begin.y ) ) {
                        hitbox.rotate90 ();
                        phantom.rotate90 ();
                        rotation = Math.PI / 2;
                    }
                }
                else if ( NUtils.checkName ( "gfx/terobjs/dframe", new NAlias ( name ) ) ) {
                    if ( ( workArea.end.x - workArea.begin.x ) < ( workArea.end.y - workArea.begin.y ) ) {
                        hitbox.rotate90 ();
                        phantom.rotate90 ();
                        rotation = Math.PI / 2;
                    }
                }
                for ( int num = 0 ; num < command.ingredients.size () ; num += 1 ) {
                    needed[num] = command.ing_count.get(command.ingredients.get ( num ));
                }
                try {
                    Coord2d pos;
                    if (NUtils.checkName("gfx/terobjs/trellis", new NAlias(name))) {
                        pos = Finder.findPlaceTrellis(hitbox, workArea, "");
                    } else {
                        pos = Finder.findPlace(hitbox, workArea, "");
                    }
                } catch (NoFreeSpace e) {
                    return new Results(Results.Types.NO_FREE_SPACE);
                }
            }
            int taked = 0;
            int num = -1;
            for ( Ingredient data : command.ingredients ) {
                num += 1;
                if ( needed[num] > 0 ) {
                    
                    Coord size = new Coord ( 1, 1 );
                    if ( NUtils.checkName ( "board", data.item ) ) {
                        size.y = 4;
                    }
                    else if ( NUtils.checkName ( data.item.keys.get ( 0 ),
                            new NAlias ( new ArrayList<> ( Arrays.asList ( "rope", "block" ) ) ) ) ) {
                        size.x = 2;
                    }
                    if ( gui.getInventory ().getNumberFreeCoord ( size ) == 0 ) {
                        break;
                    }
                    if ( size.x == 1 && size.y == 1 ) {
                        taked = Math.min ( needed[num], gui.getInventory ().getFreeSpace () );
                    }
                    else {
                        taked = Math.min ( needed[num], gui.getInventory ().getNumberFreeCoord ( size ) );
                    }
                    if ( NUtils.checkName ( "clay", data.item ) && Finder.findObjectsInArea ( new NAlias ("stockpile" +
                            "-clay"), command.spec_in_area.get(data) ).isEmpty ())
                    {
                        gui.msg("Clay:" + taked);
                        if ( new ClayDiggingBuild ( command.spec_in_area.get(data) , taked).run ( gui ).type !=
                                Results.Types.SUCCESS ) {
                            return new Results ( Results.Types.NO_ITEMS );
                        }
                        int current = gui.getInventory ().getWItems( data.item ).size ();
                        taked = taked - current;
                    }
                    else if ( NUtils.checkName ( "board", data.item ) &&
                            Finder.findObjectInArea ( new NAlias ( "log" ), 2000, command.spec_in_area.get(data) ) != null ) {
                        new Equip(saw_tools).run(gui);
                        taked = Math.min ( needed[num], gui.getInventory ().getNumberFreeCoord ( size ) );
                        if(taked>0)
                            if ( new WorkWithLog ( taked, new NAlias ( "log" ), false, command.spec_in_area.get(data)).run ( gui ).type !=
                                    Results.Types.SUCCESS ) {
                                return new Results ( Results.Types.NO_ITEMS );
                            }
                        int current = gui.getInventory ().getWItems( data.item ).size ();
                        taked = taked - current;
                    }
                    else if ( NUtils.checkName ( "block", data.item ) &&
                            Finder.findObjectInArea ( new NAlias ( "log" ), 2000, command.spec_in_area.get(data) ) != null ) {
                        new Equip(lumber_tools).run(gui);
                        taked = Math.min ( needed[num], gui.getInventory ().getNumberFreeCoord ( size ) );
                        if(taked>0)
                            if ( new WorkWithLog ( taked, new NAlias ( "log" ), true, command.spec_in_area.get(data) ).run ( gui ).type !=
                                    Results.Types.SUCCESS ) {
                                return new Results ( Results.Types.NO_ITEMS );
                            }
                        int current = gui.getInventory ().getWItems( data.item ).size ();
                        taked = taked - current;
                    }
                    else if ( NUtils.checkName ( "stone", data.item ) &&
                            Finder.findObjectInArea ( new NAlias ( "bumlings" ), 2000, command.spec_in_area.get(data) ) != null ) {
                        gui.msg("Stone:" + taked);
                        if ( new Equip (
                                new NAlias ( new ArrayList<String> ( Arrays.asList ( "pickaxe", "stoneaxe" ) ) ) ).run (
                                gui ).type != Results.Types.SUCCESS ) {
                            return new Results ( Results.Types.NO_WORKSTATION );
                        }
                        int cFreeSpace = gui.getInventory ().getFreeSpace ();
                        if ( new WorkWithBumbling ( taked, new NAlias ( "bumlings" ), command.spec_in_area.get(data) ).run ( gui ).type !=
                                Results.Types.SUCCESS ) {
                            return new Results ( Results.Types.NO_ITEMS );
                        }
                        int current = cFreeSpace - gui.getInventory ().getFreeSpace ();
                        taked = taked - current;
                        gui.msg("Stone need:" +  needed[num]);
                        needed[num] -= current;
                    }
                    else {
                        NUtils.ContainerProp icontainer = NUtils.getContainerType(command.spec_in_area.get(data));
                        if (icontainer.name == null) {
                            return new Results(Results.Types.NO_ITEMS);
                        }
                        ArrayList<Gob> igobs = Finder.findObjectsInArea(icontainer.name, command.spec_in_area.get(data));
                        for (Gob in : igobs) {
                            if (in.getModelAttribute() != 0) {
                                PathFinder pf = new PathFinder(gui, in);
                                pf.run();
                                if (new OpenTargetContainer(in, icontainer.cap).run(gui).type ==
                                        Results.Types.OPEN_FAIL) {
                                    return new Results(Results.Types.OPEN_FAIL);
                                }
                                int fs = gui.getInventory().getFreeSpace();
                                if (new TakeFromContainer(icontainer.cap, data.item, taked).run(gui).type ==
                                        Results.Types.FULL) {
                                    return new Results(Results.Types.NO_FREE_SPACE);
                                }
                                int current = (NUtils.checkName("stone", data.item)) ? fs - gui.getInventory().getFreeSpace() : gui.getInventory().getWItems(data.item).size();
                                taked = taked - current;
                                if (taked <= 0) {
                                    Window wnd = gui.getWindow(icontainer.cap);
                                    if (wnd != null) {
                                        wnd.destroy();
                                    }
                                    NUtils.waitEvent(() -> gui.getWindow(icontainer.cap) == null, 50);
                                    break;
                                }
                            }
                        }
                    }
                    if ( taked > 0 ) {
                        for ( Ingredient datarev : command.ingredients ) {
                            NUtils.ContainerProp irevcontainer = NUtils.getContainerType ( command.spec_in_area.get(datarev) );
                            if ( irevcontainer.name != null ) {
                                if ( !gui.getInventory ().getWItems( datarev.item ).isEmpty () ) {
                                    if ( !irevcontainer.cap.contains ( "Stockpile" ) ) {
                                        new TransferItemsToContainers ( 1024, command.spec_in_area.get(datarev), irevcontainer.name,
                                                irevcontainer.cap, datarev.item ).run ( gui );
                                    }
                                    else {
                                        new TransferToPile ( command.spec_in_area.get(datarev),
                                                NHitBox.get ( irevcontainer.name.keys.get ( 0 ) ),
                                                irevcontainer.name, datarev.item );
                                    }
                                }
                            }
                        }
                        return new Results ( Results.Types.NO_ITEMS );
                    }
                    else {
                        if ( !NUtils.checkName ( "stone", data.item ) ) {
                            needed[num] -= gui.getInventory ().getWItems( data.item ).size ();
                        }
                    }
                }
            }
            
            if ( buildedObj == null ) {
                try {
                    Coord2d pos;
                    if ( NUtils.checkName ( "gfx/terobjs/trellis", new NAlias ( name ) ) ) {
                        pos = Finder.findPlaceTrellis ( hitbox, workArea, "" );
                    }
                    else {
                        pos = Finder.findPlace ( hitbox, workArea, "" );
                    }
                    PathFinder pf = new PathFinder ( gui, pos );
                    pf.setPhantom ( pos, phantom );
                    pf.setHardMode ( true );
                    if(NUtils.checkName ( "dframe", new NAlias ( name ) ))
                        pf.setDefaultDelta(4);
                    pf.run ();
//                    pf.printPath();
                    new Drink ( 0.9, false ).run ( gui );
                    if ( command.special_command == null ) {
                        NUtils.build ( command.command, pos, rotation, command.name, workArea );
                    }
                    else {
                        NUtils.build ( command.command, command.special_command, pos, rotation, command.name,
                                workArea );
                    }
                }
                catch ( NoFreeSpace noFreeSpace ) {
                    return new Results ( Results.Types.NO_FREE_SPACE );
                }
            }
            else {
                PathFinder pf = new PathFinder ( gui, buildedObj );
                pf.setPhantom ( buildedObj.rc, phantom );
                pf.setHardMode ( true );
                pf.run ();
                new Drink ( 0.9, false ).run ( gui );
                Thread.sleep ( 100 );
                new OpenTargetContainer ( buildedObj, command.name ).run ( gui );
                NUtils.buildCurrent ( command.name );
            }
        }
    }
    
    public Build(
            NArea workArea,
            String name,
            CraftCommand command
    ) {
        this.workArea = workArea;
        this.name = name;
        this.command = command;
    }
    
    CraftCommand command;
    NArea workArea;
    String name;
}
