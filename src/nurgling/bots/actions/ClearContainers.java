package nurgling.bots.actions;

import haven.Gob;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;

import java.util.ArrayList;

public class ClearContainers implements Action {
    
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        if ( name == null ) {
            NUtils.ContainerProp iprop = NUtils.getContainerType ( input );
            name = iprop.name;
            cap = iprop.cap;
        }
        
        ArrayList<Gob> in;
        if ( input != null ) {
            in = Finder.findObjectsInArea ( name, Finder.findNearestMark ( input ) );
        }
        else {
            in = Finder.findObjects ( name );
        }
        for ( Gob gob : in ) {
            new PathFinder( gui, gob ).run ();
            new OpenTargetContainer ( gob, cap ).run ( gui );
            new TakeFromContainerAndDrop ( cap, items ).run ( gui );
        }
        return new Results ( Results.Types.SUCCESS );
    }
    
    public ClearContainers(
            NAlias name,
            String cap,
            NAlias items,
            AreasID input
    ) {
        this.name = name;
        this.cap = cap;
        this.items = items;
        this.input = input;
    }
    
    public ClearContainers(
            NAlias name,
            String cap,
            NAlias items
    ) {
        this.name = name;
        this.cap = cap;
        this.items = items;
    }
    
    NAlias name;
    String cap;
    NAlias items;
    AreasID input = null;
    
    
}
