package nurgling.bots.actions;

import haven.Gob;
import haven.Loading;
import haven.Widget;
import haven.Window;
import haven.res.ui.barterbox.Shopbox;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.bots.tools.Ingredient;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

import java.util.ArrayList;
import java.util.Arrays;

public class TakeItemsFromBarter implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        new TakeFromContainers ( new NAlias( "chest" ), new NAlias ( new ArrayList<> ( Arrays.asList ( "branch" ) ) ),
                count, id, "Chest" ).run ( gui );
        if ( gob == null ) {
            gob = Finder.findObjectInArea ( new NAlias ( "barter" ), 2000,
                    Finder.findNearestMark ( id) );
        }
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
                    if ( !isInfo ) {
                        if(sb.res!=null) {
                            if ( NUtils.isIt ( sb, items ) ) {
                                for ( int i = 0 ; i < count ; i++ ) {
                                    sb.bbtn.click ();
                                    int finalI = i + 1;
                                    NUtils.waitEvent ( () -> gui.getInventory ().getWItems( items ).size () >= ( finalI ),
                                            60 );
                                }
                            }
                        }
                    }
                    else {
                        NAlias alias = new NAlias ();
                        try {
                            for ( int i = 0 ;i < 5; i++ ) {
                                try {
                                    sb.sprite ();
                                    break;
                                }
                                catch ( Loading e ) {
                                    Thread.sleep ( 20 );
                                }
                            }
                            if(sb.spr!= null) {
                                String name = ( String ) sb.sprite ().getClass ().getField ( "name" ).get ( sb.sprite () );
                                alias.keys.add ( name );
                                if ( NUtils.checkName ( name, new NAlias ( "Raw Beef" ) ) ) {
                                    alias.keys.add ( "Raw Wild Beef" );
                                }
                                else if ( NUtils.checkName ( name, new NAlias ( "Raw Pork" ) ) ) {
                                    alias.keys.add ( "Raw Wild Pork" );
                                }
                                else if ( NUtils.checkName ( name, new NAlias ( "Raw Mutton" ) ) ) {
                                    alias.keys.add ( "Raw Wild Mutton" );
                                }
                            }
                        }
                        catch ( NoSuchFieldException | IllegalAccessException ignore ) {
                        }
                        boolean isFound = false;
                        for ( String key : items.keys ) {
                            if ( NUtils.checkName ( key, alias ) ) {
                                isFound = true;
                            }
                        }
                        if ( isFound ) {
                            for ( int i = 0 ; i < count ; i++ ) {
                                sb.bbtn.click ();
                                int finalI = i + 1;
                                NUtils.waitEvent (
                                        () -> gui.getInventory ().getWItems( items ).size () >= ( finalI ),
                                        60 );
                            }
                        }
                    }
                }
            }
        }
    
        if (  gui.getInventory ().getWItems( items ).size () >= count  ) {
            return new Results ( Results.Types.SUCCESS );
        }
        else {
            return new Results ( Results.Types.NO_ITEMS );
        }
    }
    public TakeItemsFromBarter(
            Gob gob,
            Ingredient ingredient
    ){
        this(gob,ingredient.item,ingredient.area_out,ingredient.isInfo,ingredient.count);
    }
    
    public TakeItemsFromBarter(
            Ingredient ingredient
    ){
        this( Finder.findObjectInArea ( new NAlias ( "barter" ), 2000,
                Finder.findNearestMark ( ingredient.barter_out) ),ingredient.item,ingredient.barter_out,ingredient.isInfo,ingredient.count);
    }
    
    public TakeItemsFromBarter(
            Gob gob,
            NAlias items,
            AreasID id,
            boolean isInfo,
            int count
    ) {
        this.gob = gob;
        this.items = items;
        this.id = id;
        this.isInfo = isInfo;
        this.count = count;
    }

    public TakeItemsFromBarter(
            NAlias items,
            AreasID id,
            boolean isInfo,
            int count
    ) {
        this.items = items;
        this.id = id;
        this.isInfo = isInfo;
        this.count = count;
    }



    Gob gob;
    AreasID id;
    NAlias items;
    boolean isInfo = false;
    int count;
}
