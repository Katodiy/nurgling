package nurgling.bots.actions;

import haven.Gob;

import nurgling.*;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;

public class TransferFromContainerToContainer implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        
        ArrayList<Gob> igobs;
        if ( inGob == null ) {

            if ( iname == null ) {
                NUtils.ContainerProp iprop;
                if (in == null) {
                    iprop = NUtils.getContainerType(input);
                }else{
                    iprop = NUtils.getContainerType(in);
                }
                iname = iprop.name;
                icap = iprop.cap;
                ifull = iprop.fullMark;
            }
            if(in == null) {
                igobs = Finder.findObjectsInArea(iname, Finder.findNearestMark(input));
            }else {
                igobs = Finder.findObjectsInArea(iname, in);
            }
        }
        else {
            igobs = new ArrayList<> ();
            igobs.add ( inGob );
        }
        
        if ( oname == null ) {
            NUtils.ContainerProp oprop;
            if(out == null) {
                oprop = NUtils.getContainerType(output);
            }else{
                oprop = NUtils.getContainerType(out);
            }
            oname = oprop.name;
            full = oprop.fullMark;
            ocap = oprop.cap;
        }
        if ( flagMode ) {
            ArrayList<Gob> fixedigobs = new ArrayList<> ();
            for ( Gob gob : igobs ) {
                if ( ( gob.getModelAttribute() & flag ) == 0 ) {
                    fixedigobs.add ( gob );
                }
            }
            igobs = fixedigobs;
        }

        ArrayList<Status> sgobs = getSGobs();
        boolean exit = true;
        for ( Gob gob : igobs ) {
            if ( Finder.findObject ( gob.id ) == null ) {
                continue;
            }
            Results.Types res = Results.Types.FAIL;
            do {
                new PathFinder ( gui, gob ).run ();
                if ( !qMode ) {
                    if ( NUtils.checkName ( icap, new NAlias ( "Stockpile" ) ) ) {
                        if (in == null) {
                            Results.Types tres = new TakeFromPile(iname, gui.getInventory().getFreeSpace(), items, this.input)
                                    .run(gui).type;
                            if (tres == Results.Types.SUCCESS || tres == Results.Types.FAIL) {
                                res = Results.Types.FULL;
                            }
                        } else {
                            Results.Types tres = new TakeFromPile(iname, gui.getInventory().getFreeSpace(), items, in)
                                    .run(gui).type;
                            if (tres == Results.Types.SUCCESS || tres == Results.Types.FAIL) {
                                res = Results.Types.FULL;
                            }
                        }
                    }
                    else {
                        new OpenTargetContainer ( gob, icap ).run ( gui );
                        res = new TakeMaxFromContainer ( icap, items ).run ( gui ).type;
                    }
                }
                else {
                    new OpenTargetContainer ( gob, icap ).run ( gui );
                    res = new TakeMaxFromContainer ( icap, items, q, isMore ).run ( gui ).type;
                }
                if ( res == Results.Types.FULL ) {
                    
                    if ( !NUtils.checkName ( "stockpile", oname ) ) {
                        for ( Status ogob : sgobs ) {
                            if ( ( NUtils.isIt ( ogob.gob, new NAlias ( "dframe","table-" ) ) ||
                                    ( (  ogob.gob.getModelAttribute() & full ) == 0 ) ) && ogob.status ) {
                                new PathFinder ( gui, ogob.gob ).run ();
                                new OpenTargetContainer ( ogob.gob, ocap ).run ( gui );
                                    if ( new TransferToContainerIfPossible ( aFreeSpace, items, ocap ).run ( gui ).type != Results.Types.FULL ) {
                                        break;
                                    }
                                    else {
                                        ogob.status = false;
                                }
                            }
                        }
                    }
                    else {
                        if ( !gui.getInventory ().getItems ( items ).isEmpty () ) {
                            if(out == null) {
                                new TransferToPile ( output, NHitBox.get ( oname.keys.get ( 0 ) ), oname, items )
                                        .run ( gui );
                            }else{
                                new TransferToPile(out, NHitBox.get(oname.keys.get(0)), oname, items).run(gui);
                            }
                        }
                    }
                }
                exit = true;
                for ( Status ogob : sgobs ) {
                    if ( ogob.status ) {
                        exit = false;
                        break;
                    }
                }
                if ( exit ) {
                    break;
                }
            }
            while ( res != Results.Types.SUCCESS );
            if ( exit ) {
                if ( NUtils.checkName ( icap, new NAlias ( "Stockpile" ) ) ) {
                    if(in == null) {
                        new TransferToPile(input, NHitBox.get(iname.keys.get(0)), iname, items).run(gui);
                    }else {
                        new TransferToPile(in, NHitBox.get(iname.keys.get(0)), iname, items).run(gui);
                    }
                }
                else {
                    if(in == null) {
                        new TransferItemsToContainers ( ifull, input, iname, icap, items, true ).run ( gui );
                    }else {
                        new TransferItemsToContainers ( ifull, in, iname, icap, items, true ).run ( gui );
                    }

                }
                break;
            }
        }
        
        
        if ( !gui.getInventory ().getItems ( items ).isEmpty () ||
                !gui.getInventory ().getItemsWithInfo ( items ).isEmpty () ) {
            if ( !NUtils.checkName ( "stockpile", oname ) ) {
                for ( Status ogob : sgobs ) {
                    if ( ( NUtils.isIt ( ogob.gob, new NAlias ( "dframe","table-" ) ) || ((  ogob.gob.getModelAttribute() & full ) == 0 && ogob.status ))) {
                        new PathFinder( gui, ogob.gob ).run ();
                        new OpenTargetContainer ( ogob.gob, ocap ).run ( gui );
                            if ( new TransferToContainerIfPossible ( aFreeSpace, items, ocap ).run ( gui ).type == Results.Types.SUCCESS ) {
                                break;
                        }
                    }
                }
            }
            else {
                if(out == null) {
                    new TransferToPile(output, NHitBox.get(oname.keys.get(0)), oname, items).run(gui);
                }else{
                    new TransferToPile(out, NHitBox.get(oname.keys.get(0)), oname, items).run(gui);
                }
            }
            if(in == null) {
                new TransferItemsToContainers(ifull, input, iname, icap, items, false).run(gui);
            }else {
                new TransferItemsToContainers(ifull, in, iname, icap, items, false).run(gui);
            }
        }
        return new Results ( Results.Types.SUCCESS );
    }

    private ArrayList<Status> getSGobs() {
        ArrayList<Gob> ogobs;
        if ( outGob == null ) {
            if(output!=null)
                ogobs = Finder.findObjectsInArea ( oname, Finder.findNearestMark ( output ) );
            else
                if(out == null) {
                    ogobs = Finder.findObjects(oname);
                }else {
                    ogobs = Finder.findObjectsInArea(oname, out);
                }
        }
        else {
            ogobs = new ArrayList<> ();
            ogobs.add ( outGob );
        }
        ArrayList<Status> sgobs = new ArrayList<> ();
        for ( Gob ogob : ogobs ) {
            sgobs.add ( new Status ( ogob, true ) );
        }
        return sgobs;
    }

    private class Status {
        Gob gob;
        boolean status;
        
        public Status (
                Gob gob,
                boolean status
        ) {
            this.gob = gob;
            this.status = status;
        }
    }
    
    /**
     * Базовый конструктор
     *
     * @param iname     имена объектов входа (null для автоопределения)
     * @param oname     имена объектов выхода (null для автоопределения)
     * @param items     имена предметов
     * @param input     зона входа
     * @param icap      Шапка контейнеров входа
     * @param output    зона выхода
     * @param ocap      Шапка контейнеров выхода
     * @param full_mark маска полноты контейнеров выхода
     */
    public TransferFromContainerToContainer (
            NAlias iname,
            NAlias oname,
            NAlias items,
            AreasID input,
            String icap,
            AreasID output,
            String ocap,
            int full_mark
    ) {
        this.iname = iname;
        this.oname = oname;
        this.items = items;
        this.input = input;
        this.icap = icap;
        this.output = output;
        this.ocap = ocap;
        this.full = full_mark;
    }
    
    /**
     * Базовый конструктор
     *
     * @param iname     имена объектов входа (null для автоопределения)
     * @param oname     имена объектов выхода (null для автоопределения)
     * @param items     имена предметов
     * @param input     зона входа
     * @param icap      Шапка контейнеров входа
     * @param ocap      Шапка контейнеров выхода
     * @param full_mark маска полноты контейнеров выхода
     */
    public TransferFromContainerToContainer (
            NAlias iname,
            NAlias oname,
            NAlias items,
            AreasID input,
            String icap,
            String ocap,
            int full_mark
    ) {
        this.iname = iname;
        this.oname = oname;
        this.items = items;
        this.input = input;
        this.icap = icap;
        this.output = null;
        this.ocap = ocap;
        this.full = full_mark;
    }
    
    /**
     * Конструктор автоопределения
     *
     * @param items  Переносимые предметы
     * @param input  зона входа
     * @param output зона выхода
     */
    public TransferFromContainerToContainer (
            NAlias items,
            AreasID input,
            AreasID output
    ) {
        
        this.items = items;
        this.input = input;
        this.output = output;
    }
    
    /**
     * Конструктор автоопределения
     *
     * @param items  Переносимые предметы
     * @param input  зона входа
     * @param output зона выхода
     */
    public TransferFromContainerToContainer (
            int aFreeSpace,
            NAlias items,
            AreasID input,
            AreasID output
    ) {
        this.aFreeSpace = aFreeSpace;
        this.items = items;
        this.input = input;
        this.output = output;
    }
    
    /**
     * Конструктор автоопределнеия выходных контейнеров с маской
     *
     * @param items      имена предметов
     * @param input      зона входа
     * @param output     зона выхода
     * @param flagExMask маска контейнеров выхода
     */
    public TransferFromContainerToContainer (
            NAlias items,
            AreasID input,
            AreasID output,
            int flagExMask
    ) {
        
        this.items = items;
        this.input = input;
        this.output = output;
        this.flag = flagExMask;
        this.flagMode = true;
    }
    
    /**
     * Конструктор транспортировки предметов по качеству
     *
     * @param items  имена предметов
     * @param input  зона входа
     * @param output зона выхода
     * @param q      порог по качеству
     * @param isMore true - предметы с качеством выше , false - предметы с качеством ниже
     */
    public TransferFromContainerToContainer (
            NAlias items,
            AreasID input,
            AreasID output,
            double q,
            boolean isMore
    ) {
        
        this.items = items;
        this.input = input;
        this.output = output;
        this.isMore = isMore;
        this.q = q;
        qMode = true;
    }
    
    /**
     * Конструктор транспортировки из зоны в спец контейнер
     *
     * @param items  предметы
     * @param input  зона входа
     * @param outGob контейнер выхода
     * @param ocap   Шапка контейнера выхода
     * @param oname  Имя контейенра выхода
     */
    public TransferFromContainerToContainer (
            NAlias items,
            AreasID input,
            Gob outGob,
            String ocap,
            NAlias oname
    ) {
        
        this.items = items;
        this.input = input;
        this.outGob = outGob;
        this.ocap = ocap;
        this.oname = oname;
    }

    public TransferFromContainerToContainer (
            NAlias items,
            NArea in,
            Gob outGob,
            String ocap,
            NAlias oname
    ) {

        this.items = items;
        this.in = in;
        this.outGob = outGob;
        this.ocap = ocap;
        this.oname = oname;
    }
    
    /**
     * Конструктор транспортировки из  спец контейнер в зону
     *
     * @param items  предметы
     * @param output зона выхода
     * @param inGob  контейнер входа
     * @param icap   Шапка контейнера входа
     * @param iname  Имя контейенра входа
     */
    public TransferFromContainerToContainer (
            NAlias items,
            Gob inGob,
            String icap,
            NAlias iname,
            AreasID output
    
    ) {
        
        this.items = items;
        this.output = output;
        this.inGob = inGob;
        this.icap = icap;
        this.iname = iname;
    }

    public TransferFromContainerToContainer (
            NAlias items,
            Gob inGob,
            String icap,
            NAlias iname,
            NArea out
    ) {

        this.items = items;
        this.out = out;
        this.inGob = inGob;
        this.icap = icap;
        this.iname = iname;
    }

    NAlias iname = null;
    NAlias oname = null;
    NAlias items;
    AreasID input;
    String icap;
    AreasID output = null;
    String ocap;
    int full;
    int ifull;
    boolean qMode = false;
    double q;
    boolean isMore = false;
    boolean flagMode = false;
    int flag;
    Gob outGob = null;
    Gob inGob = null;
    int aFreeSpace = -1;

    NArea out = null;
    NArea in = null;
}
