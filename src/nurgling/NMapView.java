package nurgling;

import haven.Coord;
import haven.Coord2d;
import haven.Glob;
import haven.MapView;

import java.util.ArrayList;
import java.util.Collection;

public class NMapView extends MapView {
    public NMapView(Coord sz, Glob glob, Coord2d cc, long plgob) {
        super(sz, glob, cc, plgob);
    }

    Collection<NGItem> getItems(Class<?> cl)
    {
        Collection<NGItem> items = new ArrayList<>();
        return items;
    }
}
