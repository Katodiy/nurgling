package nurgling;

import haven.*;
import haven.render.*;
import nurgling.NConfiguration;
import nurgling.NGob;

import java.awt.*;
import java.util.*;
import java.util.List;

public class NPathVisualizer implements RenderTree.Node {
    public static final HashSet<PathCategory> DEF_CATEGORIES = new HashSet<>(Arrays.asList(PathCategory.ME, PathCategory.FOE));
    private static final VertexArray.Layout LAYOUT = new VertexArray.Layout(new VertexArray.Layout.Input(Homo3D.vertex, new VectorFormat(3, NumberFormat.FLOAT32), 0, 0, 12));

    public NPathQueue path;

    public List<Pair<Coord3f, Coord3f>> pflines = null;
    public final Collection<RenderTree.Slot> slots = new ArrayList<>(1);
    private final Set<Moving> moves = new HashSet<>();
    private final Map<PathCategory, MovingPath> paths = new HashMap<>();

    public NPathVisualizer() {
        for (PathCategory cat : PathCategory.values()) {
            paths.put(cat, new MovingPath(cat.state));
        }
    }

    @Override
    public void added(RenderTree.Slot slot) {
        synchronized (slots) {slots.add(slot);}
        for (MovingPath path : paths.values()) {
            slot.add(path);
        }
    }

    @Override
    public void removed(RenderTree.Slot slot) {
        synchronized (slots) {slots.remove(slot);}
    }

    private void update() {
        Set<Moving> tmoves;

        synchronized (moves) {
            tmoves = new HashSet<>(moves);
        }

        Map<PathCategory, List<Pair<Coord3f, Coord3f>>> categorized = new HashMap<>();

        for (Moving m : tmoves) {
            PathCategory category = categorize(m);
            if(!categorized.containsKey(category)) {
                categorized.put(category, new LinkedList<>());
            }
            try {
                categorized.get(category).add(new Pair<>(
                        m.getc(),
                        m.gett()
                ));
            } catch (Loading ignored) {}
        }

        Set<PathCategory> selected = new HashSet<>(NConfiguration.getInstance().pathCategories);
        if( path != null) {
            List<Pair<Coord3f, Coord3f>> lines = path.lines();
            categorized.put(PathCategory.QUEUED, lines);
            if(!selected.contains(PathCategory.ME) && lines.size() > 1) {
                selected.add(PathCategory.ME);
            }
            if(selected.contains(PathCategory.ME)) {selected.add(PathCategory.QUEUED);}
        }

        for (PathCategory cat : PathCategory.values()) {
            List<Pair<Coord3f, Coord3f>> lines = categorized.get(cat);
            MovingPath path = paths.get(cat);
            if(!selected.contains(cat) || lines == null || lines.isEmpty()) {
                if(path != null) {
                    path.update(null);
                }
            } else {
                path.update(lines);
            }
        }
        if(pflines!=null){
            MovingPath path = paths.get(PathCategory.PF);
            path.update(pflines);
        }

    }

        private PathCategory categorize(Moving m) {
            Gob gob =  m.gob;
            if(gob.isTag(NGob.Tags.player)) {
                return PathCategory.ME;
            } else if(gob.isTag(NGob.Tags.notplayer)) {
                return KinInfo.isFoe(gob) ? PathCategory.FOE : PathCategory.FRIEND;
            } else {
                return PathCategory.OTHER;
            }
        }

    private static final float Z = 1f;

    private static float[] convert(List<Pair<Coord3f, Coord3f>> lines) {
        float[] ret = new float[lines.size() * 6];
        int i = 0;
        for (Pair<Coord3f, Coord3f> line : lines) {
            ret[i++] = line.a.x;
            ret[i++] = -line.a.y;
            ret[i++] = line.a.z + Z;

            ret[i++] = line.b.x;
            ret[i++] = -line.b.y;
            ret[i++] = line.b.z + Z;
        }
        return ret;
    }

    public void addPath(Moving moving) {
        if(moving == null) {return;}
        synchronized (moves) { moves.add(moving); }
    }


    public void removePath(Moving moving) {
        if(moving == null) {return;}
        synchronized (moves) { moves.remove(moving); }
    }

    public void tick(double dt) {
        update();
    }

    private static class MovingPath implements RenderTree.Node, Rendered {
        private final Pipe.Op state;
        public final Collection<RenderTree.Slot> slots = new ArrayList<>(1);
        private Model model;

        public MovingPath(Pipe.Op state) {
            this.state = state;
        }

        @Override
        public void added(RenderTree.Slot slot) {
            slot.ostate(state);
            synchronized (slots) {slots.add(slot);}
        }

        @Override
        public void removed(RenderTree.Slot slot) {
            synchronized (slots) {slots.remove(slot);}
        }

        @Override
        public void draw(Pipe context, Render out) {
            if(model != null) {
                out.draw(context, model);
            }
        }

        public void update(List<Pair<Coord3f, Coord3f>> lines) {
            if(lines == null || lines.isEmpty()) {
                model = null;
            } else {
                float[] data = convert(lines);

                VertexArray.Buffer vbo = new VertexArray.Buffer(data.length * 4, DataBuffer.Usage.STATIC, DataBuffer.Filler.of(data));
                VertexArray va = new VertexArray(LAYOUT, vbo);

                model = new Model(Model.Mode.LINES, va, null);
            }

            Collection<RenderTree.Slot> tslots;
            synchronized (slots) { tslots = new ArrayList<>(slots); }
            try {
                tslots.forEach(RenderTree.Slot::update);
            } catch (Exception ignored) {}
        }

    }

    public enum PathCategory {
        ME(new Color(118, 254, 196, 255), true),
        QUEUED(new Color(112, 204, 164, 255), true),
        FRIEND(new Color(109, 211, 251, 255)),
        FOE(new Color(255, 134, 154, 255), true),
        AGGRESSIVE_ANIMAL(new Color(255, 179, 122, 255), true),
        PF(new Color(220, 255, 64, 255), true),
        OTHER(new Color(187, 187, 187, 255));


        private final Pipe.Op state;
        public final Color color;

        PathCategory(Color col, boolean top) {
            state = Pipe.Op.compose(
                    new BaseColor(col),
                    new States.LineWidth(1.5f),
                    top ? Pipe.Op.compose(Rendered.last, States.Depthtest.none, States.maskdepth) : null
            );
            color = col;
        }

        PathCategory(Color col) {
            this(col, false);
        }
    }

}
