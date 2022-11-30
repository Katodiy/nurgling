package nurgling.bots.actions;

import haven.WItem;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;
import java.util.Arrays;

public class RestorePrimRod implements Action {
    NAlias bait_item = new NAlias ( Arrays.asList ( "lure", "Rock Lobster", "Feather Fly", "Woodfish", "Pinecone" ),
            new ArrayList<> () );

    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        //        gui.getEquipment ().freeHands ();
        NAlias hooks = new NAlias( Arrays.asList ( "Hook", "hook" ), new ArrayList<> () );
        NAlias fishlanes = new NAlias ( Arrays.asList ( "Fishline", "fline" ), new ArrayList<> () );
        int target_count = 1;

        if ( !NUtils.checkContent ( rod, fishlanes ) ) {
            NUtils.ContainerProp fishline_prop = NUtils.getContainerType ( tools );
            new TakeFromContainers ( fishline_prop.name, fishlanes, 1, tools, fishline_prop.cap ).run ( gui );
            new UseItemOnItem ( fishlanes, rod ).run ( gui );
        }
        NUtils.waitEvent(()->NUtils.checkContent ( rod, fishlanes ),50);
        if ( !NUtils.checkContent ( rod, hooks ) ) {
            NUtils.ContainerProp fishline_prop = NUtils.getContainerType ( tools );
            new TakeFromContainers ( fishline_prop.name, hooks, 1, tools, fishline_prop.cap ).run ( gui );
            new UseItemOnItem ( hooks, rod ).run ( gui );
        }
        NUtils.waitEvent(()->NUtils.checkContent ( rod, hooks ),50);
        if ( !NUtils.checkContent ( rod, bait_item ) ) {
            if ( !Finder.findObjectsInArea (
                    new NAlias ( new ArrayList<> ( Arrays.asList ( "stockpile-soil", "stockpile" + "-trash" ) ) ),
                    baits ).isEmpty () ) {
                new TakeFromPile (
                        new NAlias ( new ArrayList<> ( Arrays.asList ( "stockpile-soil", "stockpile-trash" ) ), new ArrayList<> () ), target_count, bait_item, baits ).run ( gui );
            }else{
                NUtils.ContainerProp fishline_prop = NUtils.getContainerType ( baits );
                new TakeFromContainers ( fishline_prop.name, bait_item, 1, baits, fishline_prop.cap ).run ( gui );
            }
            new UseItemOnItem ( bait_item, rod ).run ( gui );
        }
        return ( NUtils.checkContent ( rod, new NAlias ( "Fishline" ) ) &&
                NUtils.checkContent ( rod, new NAlias ( "Hook" ) ) &&
                NUtils.checkContent ( rod, bait_item ) ) ? new Results (
                Results.Types.SUCCESS ) : new Results ( Results.Types.NO_ITEMS );
    }


    public RestorePrimRod(
            NArea baits,
            NArea tools,
            WItem rod
    ) {
        this.baits = baits;
        this.tools = tools;
        this.rod = rod;
    }
    
    NArea baits;
    NArea tools;
    
    WItem rod;
    
}
