package nurgling.bots.tools;

import haven.Coord2d;
import haven.Gob;

import java.util.ArrayList;
import java.util.Comparator;

public class OutContainer {
    public Gob gob;
    public boolean isFull = false;

    int freeSpace = 0;

    public OutContainer(Gob gob, boolean isFull) {
        this.gob = gob;
        this.isFull = isFull;
    }

    public OutContainer(Gob gob, boolean isFull, int freeSpace) {
        this.gob = gob;
        this.isFull = isFull;
        this.freeSpace = freeSpace;
    }

    public static ArrayList<OutContainer> create(ArrayList<Gob> gobs) {
        ArrayList<OutContainer> containers = new ArrayList<>();
        for (Gob gob : gobs) {
            containers.add(new OutContainer(gob, false));
        }
        return containers;
    }

    public static ArrayList<OutContainer> create(ArrayList<Gob> gobs, int freeSpace) {
        ArrayList<OutContainer> containers = new ArrayList<>();
        for (Gob gob : gobs) {
            containers.add(new OutContainer(gob, false, freeSpace));
        }
        return containers;
    }

    public static boolean allFull(ArrayList<OutContainer> outarray){
        for (OutContainer container: outarray){
            if(!container.isFull)
                return false;
        }
        return true;
    }

    public static void sort(ArrayList<OutContainer> outarray){
        if(!outarray.isEmpty()) {
            Coord2d min = new Coord2d(outarray.get(0).gob.rc.x, outarray.get(0).gob.rc.y);
            Coord2d max = new Coord2d(outarray.get(0).gob.rc.x, outarray.get(0).gob.rc.y);
            for (OutContainer gob : outarray) {
                if (gob.gob.rc.x < min.x) {
                    min.x = gob.gob.rc.x;
                }
                if (gob.gob.rc.y < min.y) {
                    min.y = gob.gob.rc.y;
                }
                if (gob.gob.rc.x > max.x) {
                    max.x = gob.gob.rc.x;
                }
                if (gob.gob.rc.y > max.y) {
                    max.y = gob.gob.rc.y;
                }
            }
            if (Math.abs(max.x - min.x) > Math.abs(max.y - min.y)) {
                outarray.sort(new Comparator<OutContainer>() {
                    @Override
                    public int compare(
                            OutContainer lhs,
                            OutContainer rhs
                    ) {
                        // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                        return (lhs.gob.rc.y > rhs.gob.rc.y) ? -1 : ((lhs.gob.rc.y < rhs.gob.rc.y) ? 1 : Double.compare(rhs.gob.rc.x, lhs.gob.rc.x));
                    }
                });
            } else {
                outarray.sort(new Comparator<OutContainer>() {
                    @Override
                    public int compare(
                            OutContainer lhs,
                            OutContainer rhs
                    ) {
                        // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                        return (lhs.gob.rc.x > rhs.gob.rc.x) ? -1 : ((lhs.gob.rc.x < rhs.gob.rc.x) ? 1 : Double.compare(rhs.gob.rc.y, lhs.gob.rc.y));
                    }
                });
            }
        }
    }
}