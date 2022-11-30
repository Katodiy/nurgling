package nurgling.bots.actions;

import haven.Gob;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

import java.util.ArrayList;

public class TransferFromContainerToBarter implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        
        ArrayList<Gob> igobs;
        if ( inGob == null ) {
            if ( iname == null ) {
                NUtils.ContainerProp iprop = NUtils.getContainerType ( input );
                iname = iprop.name;
                icap = iprop.cap;
                ifull = iprop.fullMark;
            }
            igobs = Finder.findObjectsInArea ( iname, Finder.findNearestMark ( input ) );
        }
        else {
            igobs = new ArrayList<> ();
            igobs.add ( inGob );
        }
    
        Gob barter = Finder.findObjectInArea ( new NAlias ( "barter" ), 1000, Finder.findNearestMark ( output ) );
    
    
        for ( Gob gob : igobs ) {
            if ( Finder.findObject ( gob.id ) == null ) {
                continue;
            }
            Results.Types res = Results.Types.FAIL;

            do {
                if(gui.getInventory().getFreeSpace()<=1) {
                    new TransferItemsToBarter(barter, items, output, isInfo).run(gui);
                }
                new PathFinder( gui, gob ).run ();
                if ( !qMode ) {
                    if ( NUtils.checkName ( icap, new NAlias ( "Stockpile" ) ) ) {
                        if ( new TakeFromPile ( iname, gui.getInventory ().getFreeSpace (), items, this.input ).run (
                                gui ).type == Results.Types.SUCCESS ) {
                            res = Results.Types.FULL;
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
            }
            while ( res != Results.Types.SUCCESS );
            if(gui.getInventory().getFreeSpace()<=1) {
                new TransferItemsToBarter(barter, items, output, isInfo).run(gui);
            }
        }
        new TransferItemsToBarter ( barter, items, output,isInfo ).run ( gui );
        return new Results ( Results.Types.SUCCESS );
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
    public TransferFromContainerToBarter(
            NAlias items,
            Gob inGob,
            String icap,
            NAlias iname,
            AreasID output,
            boolean isInfo
    ) {
        
        this.items = items;
        this.output = output;
        this.inGob = inGob;
        this.icap = icap;
        this.iname = iname;
        this.isInfo = isInfo;
    }
    
    NAlias iname = null;
    NAlias items;
    AreasID input;
    String icap;
    AreasID output;
    int ifull;
    boolean qMode = false;
    double q;
    boolean isMore = false;
    boolean isInfo = false;
    Gob inGob = null;
    
    
    /**
     * Конструктор транспортировки предметов по качеству
     *
     * @param items  имена предметов
     * @param input  зона входа
     * @param output зона выхода
     * @param q      порог по качеству
     * @param isMore true - предметы с качеством выше , false - предметы с качеством ниже
     */
    public TransferFromContainerToBarter(
            NAlias items,
            AreasID input,
            AreasID output,
            double q,
            boolean isMore,
            boolean isInfo
    ) {
        
        this.items = items;
        this.input = input;
        this.output = output;
        this.isMore = isMore;
        this.q = q;
        qMode = true;
        this.isInfo = isInfo;
    }

}