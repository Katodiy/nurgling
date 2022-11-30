package nurgling.bots.actions;

import haven.Gob;
import haven.WItem;
import haven.Window;
import nurgling.*;
import nurgling.bots.tools.CraftCommand;
import nurgling.bots.tools.Ingredient;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;
import java.util.Objects;

public class CraftAndFill implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        if(oarea!=null){
            NUtils.ContainerProp prop = NUtils.getContainerType(oarea);
            oname = prop.name;
            ocap = prop.cap;
        }
        int size = 0;
        for ( Ingredient data : command.ingredients ) {
            size += data.count;
        }
        for ( int i = 0 ; i < count ; i++ ) {
            if ( gui.getInventory ().getFreeSpace () <= size ) {
                if(!NUtils.checkName("wurst",result)) {
                    if(oarea == null) {
                        new TransferItemsToContainers(fullMark, output, oname, ocap, result).run(gui);
                    }else {
                        new TransferItemsToContainers(fullMark, oarea, oname, ocap, result).run(gui);
                    }
                }else{
                    WItem out_item = gui.getInventory().getItem(new NAlias("wurst"));
                    if(out_item!=null) {
                        String out_name = out_item.item.getres().name;
                        out_name = out_name.substring(out_name.lastIndexOf('/')+1);

//                        new TransferItemsToBarter(Objects.requireNonNull(NUtils.getWarhouse(out_name))).run(gui);
                    }
                }
            }
            for ( Ingredient data : command.ingredients ) {
                Gob barter = null;
                if ( data.barter_out != null ) {
                    barter = Finder.findObjectInArea ( new NAlias ( "barter" ), 2000,
                            Finder.findNearestMark ( data.barter_out) );
                }
                if ( barter == null ) {
                    ArrayList<Gob> igobs;
                    NUtils.ContainerProp icontainer;
                    if(data.inarea==null) {
                        icontainer = NUtils.getContainerType(data.area_out);
                        igobs = Finder.findObjectsInArea(icontainer.name,
                                Finder.findNearestMark(data.area_out));
                    }else {
                        icontainer = NUtils.getContainerType(data.inarea);
                        igobs = Finder.findObjectsInArea(icontainer.name,
                               data.inarea);
                    }
                    int needed = data.count;
                    for ( Gob in : igobs ) {
                        if ( in.getModelAttribute() != 0 ) {
                            PathFinder pf = new PathFinder ( gui, in );
                            pf.run ();
                            if ( new OpenTargetContainer ( in, icontainer.cap ).run ( gui ).type ==
                                    Results.Types.OPEN_FAIL ) {
                                return new Results ( Results.Types.OPEN_FAIL );
                            }
                            if ( new TakeFromContainer ( icontainer.cap, data.item, needed ).run ( gui ).type ==
                                    Results.Types.FULL ) {
                                return new Results ( Results.Types.NO_FREE_SPACE );
                            }
                            Thread.sleep ( 500 );
                            int current = ( data.isInfo ) ? gui.getInventory ().getItemsWithInfo ( data.item )
                                                               .size () : gui.getInventory ().getItems ( data.item )
                                                                             .size ();
                            needed = data.count - current;
//                            System.out.println ( "neded" + data.item.keys.get ( 0 ) + needed );
                            if ( needed <= 0 ) {
                                Window wnd = gui.getWindow ( icontainer.cap );
                                if ( wnd != null ) {
                                    wnd.destroy ();
                                }
                                int count = 0;
                                while ( count < 20 && gui.getWindow ( icontainer.cap ) != null ) {
                                    Thread.sleep ( 50 );
                                    count++;
                                }
                                break;
                            }
                        }
                    }
                    if ( needed > 0 ) {
                        for ( Ingredient datarev : command.ingredients ) {
                            NUtils.ContainerProp irevcontainer = NUtils.getContainerType ( datarev.area_out);
                            if ( !gui.getInventory ().getItemsWithInfo ( datarev.item ).isEmpty () ) {
                                if ( !irevcontainer.cap.contains ( "Stockpile" ) ) {
                                    new TransferItemsToContainers ( fullMark, datarev.area_out, irevcontainer.name,
                                            irevcontainer.cap, datarev.item ).run ( gui );
                                }
                                else {
                                    new TransferToPile ( datarev.area_out,
                                            NHitBox.get ( irevcontainer.name.keys.get ( 0 ) ), irevcontainer.name,
                                            datarev.item );
                                }
                            }
                        }
                        if ( i == 0 ) {
                            return new Results ( Results.Types.NO_ITEMS );
                        }
                        else {
                            return new Results ( Results.Types.SUCCESS );
                        }
                    }
                }
                else {
                    if ( new TakeItemsFromBarter ( barter, data.item, data.barter_out, data.isInfo, data.count ).run (
                            gui ).type == Results.Types.NO_ITEMS ) {
                        return new Results ( Results.Types.NO_ITEMS );
                    }
                }
            }
            if ( command.withPepper ) {
                new TakeBlackPepper ().run ( gui );
            }
            
            if ( !withWorkStation ) {
                if ( !withWorkArea ) {
//                    if(oarea == null) {
//                        PathFinder pf = new PathFinder(gui,
//                                Finder.findObjectInArea(oname, 1000, Finder.findNearestMark(output)));
//                        pf.run();
//                    }else{
//                        PathFinder pf = new PathFinder(gui,
//                                Finder.findObjectInArea(oname, 1000, oarea));
//                        pf.run();
//                    }\
                    NUtils.stopWithClick();
                    if ( command.special_command == null ) {
                        if ( !NUtils.craft ( command.command, command.name, false ) ) {
                            return new Results ( Results.Types.FAIL );
                        }
                    }
                    else {
                        if ( !NUtils.craft ( command.command, command.special_command, command.name ) ) {
                            return new Results ( Results.Types.FAIL );
                        }
                    }
                }
                else {
                    if ( workArea == AreasID.barrel_work_zone ) {
                        ArrayList<Gob> barrels = Finder.findObjectsInArea ( new NAlias ( "barrel" ),
                                Finder.findNearestMark ( workArea ) );
                        new PathFinder ( gui, Finder.findNearestMark ( workArea ).center ).run ();
                        for ( Gob barrel : barrels ) {
                            new OpenTargetContainer ( barrel, "Barrel" ).run ( gui );
                        }
                        Thread.sleep ( 1000 );
                        if ( command.special_command == null ) {
                            if ( !NUtils.craft ( command.command, command.name, false ) ) {
                                return new Results ( Results.Types.FAIL );
                            }
                        }
                        else {
                            if ( !NUtils.craft ( command.command, command.special_command, command.name ) ) {
                                return new Results ( Results.Types.FAIL );
                            }
                        }
                    }
                }
            }
            else {
                /// Используем крафтовую станцию
                if ( mask == -1 ) {
                    if ( new UseWorkStation ( workStation ).run ( gui ).type != Results.Types.SUCCESS ) {
                        return new Results ( Results.Types.FAIL );
                    }
                }
                else {
                    if ( new UseWorkStation ( workStation, workStationCap, action, mask ).run ( gui ).type !=
                            Results.Types.SUCCESS ) {
                        return new Results ( Results.Types.FAIL );
                    }
                }
                /// Открываем рядом стоящие бочки ( если надо )
                if ( command.barrels != null ) {
                    for ( NAlias barrel : command.barrels ) {
                        Gob gob = Finder.findObjectWithCoontent ( new NAlias ( "barrel" ), barrel, 15 );
                        if ( gob != null ) {
                            if ( new OpenTargetContainer ( gob, "Barrel" ).run ( gui ).type != Results.Types.SUCCESS ) {
                                return new Results ( Results.Types.OPEN_FAIL );
                            }
                        }
                    }
                }
                /// Крафтим
                if ( command.special_command == null ) {
                    if ( !NUtils.craft ( command.command, command.name, false ) ) {
                        return new Results ( Results.Types.FAIL );
                    }
                }
                else {
                    if ( !NUtils.craft ( command.command, command.special_command, command.name ) ) {
                        return new Results ( Results.Types.FAIL );
                    }
                }
            }

            
        }
        
        return new Results ( Results.Types.SUCCESS );
    }
    
    public CraftAndFill(
            AreasID output,
            NAlias oname,
            String ocap,
            NAlias result,
            int fullMark,
            CraftCommand command,
            int count
    ) {
        this.output = output;
        this.oname = oname;
        this.ocap = ocap;
        this.result = result;
        this.fullMark = fullMark;
        this.command = command;
        this.count = count;
    }
    
    public CraftAndFill(
            AreasID output,
            NAlias oname,
            String ocap,
            NAlias result,
            int fullMark,
            CraftCommand command,
            int count,
            NAlias workStation
    ) {
        this.output = output;
        this.oname = oname;
        this.ocap = ocap;
        this.result = result;
        this.fullMark = fullMark;
        this.command = command;
        this.count = count;
        this.workStation = workStation;
        this.withWorkStation = true;
    }
    
    public CraftAndFill(
            AreasID output,
            NAlias oname,
            String ocap,
            NAlias result,
            int fullMark,
            CraftCommand command,
            int count,
            NAlias workStation,
            String workStationCap,
            String action,
            long mask
    ) {
        this.output = output;
        this.oname = oname;
        this.ocap = ocap;
        this.result = result;
        this.fullMark = fullMark;
        this.command = command;
        this.count = count;
        this.workStation = workStation;
        this.withWorkStation = true;
        this.action = action;
        this.workStationCap = workStationCap;
        this.mask = mask;
    }
    
    public CraftAndFill(
            AreasID output,
            NAlias oname,
            String ocap,
            NAlias result,
            int fullMark,
            CraftCommand command,
            int count,
            AreasID workArea
    ) {
        this.output = output;
        this.oname = oname;
        this.ocap = ocap;
        this.result = result;
        this.fullMark = fullMark;
        this.command = command;
        this.count = count;
        this.withWorkStation = false;
        this.withWorkArea = true;
        this.workArea = workArea;
    }

    public CraftAndFill(
            NArea out,
            NAlias result,
            int fullMark,
            CraftCommand command,
            int count
    ) {
        this.output = null;
        this.oname = null;
        this.ocap = "";
        this.oarea = out;
        this.result = result;
        this.fullMark = fullMark;
        this.command = command;
        this.count = count;
        this.withWorkStation = false;
        this.withWorkArea = false;
    }
    
    AreasID output;
    NAlias oname;
    String ocap;
    NAlias result;
    int fullMark;
    CraftCommand command;
    int count;
    boolean withWorkStation;
    boolean withWorkArea;
    NAlias workStation;
    AreasID workArea;
    long mask = -1;
    String action;
    String workStationCap;

    NArea oarea = null;
    
    public CraftAndFill(
            AreasID output,
            NAlias oname,
            String ocap,
            NAlias result,
            int fullMark,
            CraftCommand command,
            int count,
            AreasID workArea,
            NAlias workStation
    ) {
        this.output = output;
        this.oname = oname;
        this.ocap = ocap;
        this.result = result;
        this.fullMark = fullMark;
        this.command = command;
        this.count = count;
        this.withWorkStation = true;
        this.withWorkArea = true;
        this.workArea = workArea;
        this.workStation = workStation;
    }
    
}
