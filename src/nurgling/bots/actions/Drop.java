package nurgling.bots.actions;

import haven.Coord2d;
import haven.Gob;
import haven.WItem;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.PathFinder;
import nurgling.tools.Finder;

import static haven.OCache.posres;
import static java.lang.Math.sqrt;

public class Drop implements Action {
    public Drop(Type type, NAlias name) {
        this._type = type;
        this._name = name;
    }

    public enum Type {
        Center,
        Back
    }

    @Override
    public Results run(NGameUI gui) throws InterruptedException {
        /// Ищем предмет в экипировке
        WItem item = Finder.findDressedItem(_name);
        if (item != null) {
            /// Если предмет найден сбрасываем его на землю по заданным координатам
            switch (_type) {
                case Center:
                    break;
                case Back: {
                    Gob nearest = Finder.findNearestObject();
                    Coord2d player = new Coord2d(gui.map.player().rc.x, gui.map.player().rc.y);
                    double x = player.x - nearest.rc.x;
                    double y = player.y - nearest.rc.y;
                    double len = sqrt(x * x + y * y);
                    x = player.x + 15 * x / len;
                    y = player.y + 15 * y / len;
                    PathFinder pf = new PathFinder (gui,new Coord2d(x, y));
                    pf.run ();
//
//                    if (new Go(new Coord2d(x, y)).run(gui).type != Results.Types.SUCCESS) {
//                        return new Results(Results.Types.GO_FAIL);
//                    }
                }
            }
            int counter = 0;
            while (Finder.findDressedItem(_name) != null && counter < 20) {
                item.item.wdgmsg("drop", item.sz, gui.map.player().rc.floor ( posres ), 0);
                Thread.sleep(50);
                counter++;
            }
        }
        /// Возвращаем true Если предмет был найден и сброшен
        return (Finder.findDressedItem(_name) == null) ? new Results(Results.Types.SUCCESS) : new Results(Results.Types.DROP_FAIL);
    }

    Type _type;
    NAlias _name;
}
