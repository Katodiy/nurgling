package nurgling.bots.actions;

import haven.Gob;
import haven.Widget;
import haven.Window;
import haven.res.ui.barterbox.Shopbox;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.bots.tools.Warhouse;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

import java.util.ArrayList;
import java.util.Arrays;

public class TransferItemsToBarter implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        if ( gob != null ) {
            if ( ( isInfo && gui.getInventory ().getItemsWithInfo ( items, q, true ).size () > 0 ) ||
                    ( !isInfo && gui.getInventory ().getItems ( items, q, true ).size () > 0 ) ) {
                PathFinder pf = new PathFinder( gui, gob );
                pf.run ();
                
                if ( new OpenTargetContainer ( gob, "Barter Stand" ).run ( gui ).type != Results.Types.SUCCESS ) {
                    return new Results ( Results.Types.OPEN_FAIL );
                }
                
                Window spwnd = gui.getWindow ( "Barter Stand" );
                if ( spwnd != null ) {
                    for ( Widget sp = spwnd.lchild ; sp != null ; sp = sp.prev ) {
                        if ( sp instanceof Shopbox ) {
                            Shopbox sb = ( Shopbox ) sp;
                            if ( sb.price != null ) {
                                NAlias alias = new NAlias ( ( String ) sb.price.res.res.get ().name );
                                try {
                                    String name = ( String ) sb.price.spr ().getClass ().getField ( "name" )
                                                                     .get ( sb.price.spr () );
                                    alias.keys.add ( name );
                                    if ( NUtils.checkName ( name, new NAlias ( "Raw Beef" ) ) ) {
                                        alias.keys.add ( "Raw Wild Beef" );
                                    }
                                    else if ( NUtils.checkName ( name, new NAlias ( "Raw Pork" ) ) ) {
                                        alias.keys.add ( "Raw Wild Pork" );
                                    }
                                    else if ( NUtils.checkName ( name, new NAlias ( "Raw Horse" ) ) ) {
                                        alias.keys.add ( "Raw Wildhorse" );
                                        alias.keys.add ( "Raw Horse" );
                                    }
                                    else if ( NUtils.checkName ( name, new NAlias ( "Raw Mutton" ) ) ) {
                                        alias.keys.add ( "Raw Wild Mutton" );
                                    }
                                }
                                catch ( NoSuchFieldException | IllegalAccessException ignore ) {
                                }
                                if ( NUtils.checkName ( "gfx/invobjs/food/offal", alias ) &&
                                        NUtils.checkName ( items.keys.get ( 0 ), new NAlias ( "Bollock" ) ) ) {
                                    alias = items;
                                }
                                else if ( NUtils.checkName ( "gfx/invobjs/bone", alias ) &&
                                        NUtils.checkName ( "/bone", items ) ) {
                                    alias = items;
                                }
                                else if ( NUtils.checkName ( "gfx/invobjs/glass", alias ) &&
                                        NUtils.checkName ( items.keys.get ( 0 ), new NAlias ( "glass" ) ) ) {
                                    alias = items;
                                }
                                else if ( NUtils.checkName ( "gfx/invobjs/rawhide", alias ) &&
                                        NUtils.checkName ( items.keys.get ( 0 ), new NAlias ( "blood" ) ) ) {
                                    alias = items;
                                }
                                else if ( NUtils.checkName ( "gfx/invobjs/small/fossilcollection", alias ) &&
                                        NUtils.checkName ( items.keys.get ( 0 ), new NAlias ( "fossil" ) ) ) {
                                    alias = items;
                                }
                                else if ( NUtils.checkName ( "gfx/invobjs/small/bearhide", alias ) &&
                                        NUtils.checkName ( items.keys.get ( 0 ), new NAlias ( "bearhide" ) ) ) {
                                    alias = items;
                                }
                                boolean isFind = false;
                                for ( String keys : alias.keys ) {
                                    if ( NUtils.checkName ( keys, items ) ) {
                                        isFind = true;
                                    }
                                }
                                if ( isFind ) {
                                    int size = gui.getInventory ().getItems ( alias, q, true ).size ();
                                    while ( ( isInfo &&
                                            gui.getInventory ().getItemsWithInfo ( alias, q, true ).size () > 0 ) ||
                                            ( !isInfo &&
                                                    gui.getInventory ().getItems ( alias, q, true ).size () > 0 ) ) {
                                        sb.bbtn.click ();
                                        Thread.sleep ( 50 );
                                        if ( sb.res == null ) {
                                            if ( paving == null ) {
                                                new TransferItemsToContainers ( id, new NAlias (
                                                        new ArrayList<> ( Arrays.asList ( "Branch", "branch" ) ) ),
                                                        true ).run ( gui );
                                            }
                                            else {
                                                new TransferItemsToContainers ( Finder.findNearestMark ( id, paving ),
                                                        new NAlias ( new ArrayList<> (
                                                                Arrays.asList ( "Branch", "branch" ) ) ), true ).run (
                                                        gui );
                                            }
                                            new TransferItemsToBarter ( gob, items, id, isInfo, q, paving ).run ( gui );
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Thread.sleep ( 300 );
                if ( paving == null ) {
                    new TransferItemsToContainers ( id,
                            new NAlias ( new ArrayList<> ( Arrays.asList ( "Branch", "branch" ) ) ), true ).run ( gui );
                }
                else {
                    new TransferItemsToContainers ( Finder.findNearestMark ( id, paving ),
                            new NAlias ( new ArrayList<> ( Arrays.asList ( "Branch", "branch" ) ) ), true ).run ( gui );
                }
            }
            return new Results ( Results.Types.SUCCESS );
        }
        else {
            return new Results ( Results.Types.NO_BARTER );
        }
    }
    
    
    public TransferItemsToBarter(
            Gob gob,
            NAlias items,
            AreasID id,
            boolean isInfo
    ) {
        this.gob = gob;
        this.items = items;
        this.id = id;
        this.isInfo = isInfo;
    }
    
    public TransferItemsToBarter(
            Warhouse warhouse
    ) {
        this.gob = Finder.findObjectInArea ( new NAlias ( "barterstand" ), 3000,
                Finder.findNearestMark ( warhouse.barter ) );
        this.items = warhouse.item;
        this.id = warhouse.barter;
        this.isInfo = warhouse.isInfo;
    }

    public TransferItemsToBarter(
            AreasID id,
            NAlias items,
            boolean isInfo
    ) {
        this.gob = Finder.findObjectInArea ( new NAlias ( "barterstand" ), 3000,
                Finder.findNearestMark ( id ));
        this.items = items;
        this.id = id;
        this.isInfo = isInfo;
    }

    public TransferItemsToBarter(
            AreasID id,
            NAlias items,
            boolean isInfo,
            double q
    ) {
        this.gob = Finder.findObjectInArea ( new NAlias ( "barterstand" ), 3000,
                Finder.findNearestMark ( id ));
        this.items = items;
        this.id = id;
        this.isInfo = isInfo;
        this.q = q;
    }
    
    public TransferItemsToBarter(
            Warhouse warhouse,
            double q
    ) {
        this.gob = Finder.findObjectInArea ( new NAlias ( "barter" ), 3000,
                Finder.findNearestMark ( warhouse.barter ) );
        this.items = warhouse.item;
        this.id = warhouse.barter;
        this.isInfo = warhouse.isInfo;
        this.q = q;
    }
    
    public TransferItemsToBarter(
            Gob gob,
            NAlias items,
            AreasID id,
            boolean isInfo,
            String paving
    ) {
        this.gob = gob;
        this.items = items;
        this.id = id;
        this.isInfo = isInfo;
        this.paving = paving;
    }
    
    public TransferItemsToBarter(
            Gob gob,
            NAlias items,
            AreasID id,
            boolean isInfo,
            double q
    ) {
        this.gob = gob;
        this.items = items;
        this.id = id;
        this.isInfo = isInfo;
        this.q = q;
    }
    
    public TransferItemsToBarter(
            Gob gob,
            NAlias items,
            AreasID id,
            boolean isInfo,
            double q,
            String paving
    ) {
        this.gob = gob;
        this.items = items;
        this.id = id;
        this.isInfo = isInfo;
        this.q = q;
        this.paving = paving;
    }
    
    
    String paving = null;
    Gob gob;
    AreasID id;
    NAlias items;
    boolean isInfo;
    double q = -1;
}
