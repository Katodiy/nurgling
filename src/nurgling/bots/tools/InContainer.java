package nurgling.bots.tools;

import haven.Gob;

import java.util.ArrayList;

public class InContainer {
    public Gob gob;
    public boolean isFree = false;

    public InContainer(Gob gob, boolean isFree) {
        this.gob = gob;
        this.isFree = isFree;
    }

    public static ArrayList<InContainer> create(ArrayList<Gob> gobs) {
        ArrayList<InContainer> containers = new ArrayList<>();
        for (Gob gob : gobs) {
            containers.add(new InContainer(gob, false));
        }
        return containers;
    }

    public static boolean allFree(ArrayList<InContainer> inarray) {
        for (InContainer container : inarray) {
            if (!container.isFree)
                return false;
        }
        return true;
    }
}