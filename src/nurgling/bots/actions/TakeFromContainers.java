package nurgling.bots.actions;

import haven.Gob;
import haven.WItem;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;

public class TakeFromContainers implements Action {
    NAlias name;
    NAlias items;
    int count;
    AreasID input;
    String cap;
    NArea area = null;
    
    public TakeFromContainers (
            NAlias name,
            NAlias items,
            int count,
            AreasID input,
            String cap
    ) {
        this.name = name;
        this.items = items;
        this.count = count;
        this.input = input;
        this.cap = cap;
    }
    
    public TakeFromContainers (
            NAlias name,
            NAlias items,
            int count,
            NArea area,
            String cap
    ) {
        this.name = name;
        this.items = items;
        this.count = count;
        this.area = area;
        this.cap = cap;
    }
    
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        if( name == null){
            NUtils.ContainerProp prop = NUtils.getContainerType ( Finder.findNearestMark ( input ) );
            cap = prop.cap;
            name = prop.name;
        }
        ArrayList<Gob> in;
        if ( area == null ) {
            in = Finder.findObjectsInArea ( name, Finder.findNearestMark ( input ) );
        }
        else {
            in = Finder.findObjectsInArea ( name, area );
        }
        
        int need = count;
        /// Для каждого объекта в массиве
        for ( Gob gob : in ) {
            new PathFinder( gui, gob ).run ();
            if(cap.equals ( "Old Trunk" )){
                new SelectFlowerAction(gob, "Open", SelectFlowerAction.Types.Gob).run(gui);
                if(!NUtils.waitEvent ( () -> gui.getWindow ( cap )!=null, 50 ))
                    return new Results ( Results.Types.NO_CONTAINER );
                Thread.sleep ( 300 );
            }
            else
            {
                new OpenTargetContainer ( gob, cap ).run ( gui );
            }
            for ( int i = 0 ; i < count ; i++ ) {
                
                if ( cap.equals ( "Stockpile" ) ) {
                    if ( !NUtils.takeItemFromPile () ) {
                        break;
                    }
                    else {
                        need--;
                    }
                }
                else {
                    ArrayList<WItem> witems = gui.getInventory ( cap ).getItems ( items );
                    if ( witems.isEmpty () ) {
                        break;
                    }
                    if ( !NUtils.transferItem ( gui.getInventory ( cap ), witems.get ( 0 ), gui.getInventory() ) ) {
                        break;
                    }
                    else {
                        need--;
                    }
                }
                if ( need == 0 ) {
                    return new Results ( Results.Types.SUCCESS );
                }
            }
        }
        return new Results ( Results.Types.FAIL );
    }
}
