package nurgling;

import haven.*;
import haven.render.*;
import nurgling.bots.actions.NGAttrib;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;


public class NModelBox extends NSprite implements Rendered {
    private static final VertexArray.Layout LAYOUT = new VertexArray.Layout(new VertexArray.Layout.Input(Homo3D.vertex, new VectorFormat(3, NumberFormat.FLOAT32), 0, 0, 12));
    private Model model;
    private final Gob gob;
    private static final Map<Resource, Model> MODEL_CACHE = new HashMap<>();
    private static final float Z = 1.2f;
    private static final Color SOLID_COLOR = new Color(255, 255, 105, 255);
    private static final Color PASSABLE_COLOR = new Color(105, 207, 124, 255);
    private static final float PASSABLE_WIDTH = 1.5f;
    private static final float SOLID_WIDTH = 3f;
    private static final Pipe.Op SOLID = Pipe.Op.compose(new BaseColor(SOLID_COLOR), new States.LineWidth(SOLID_WIDTH));
    private static final Pipe.Op PASSABLE = Pipe.Op.compose(new BaseColor(PASSABLE_COLOR), new States.LineWidth(PASSABLE_WIDTH));
    private Pipe.Op state = SOLID;

	protected Collection<RenderTree.Slot> slots;
	protected Collection<RenderTree.Slot> backup;
    private NModelBox(Gob gob) {
	super(gob,null);
	model = getModel(gob);
	this.gob = gob;
    }

	@Override
	public boolean tick(double dt) {
		Pipe.Op n_state = (gob.isTag(NGob.Tags.gate)? ((gob.getModelAttribute()&1)==1?PASSABLE:SOLID) : state);
		if(n_state!=state) {
			if(slots!=null) {
				state = n_state;
				for (RenderTree.Slot slot : slots) {
					slot.ostate(state);
				}
			}
		}
		return !NConfiguration.getInstance().showBB && !(NConfiguration.getInstance().hideNature && (gob.isTag(NGob.Tags.tree) || gob.isTag(NGob.Tags.bumling) || gob.isTag(NGob.Tags.bush)));
	}

	public static NModelBox forGob(Gob gob) {
		if (NConfiguration.getInstance().showBB || (NConfiguration.getInstance().hideNature && (gob.isTag(NGob.Tags.tree) || gob.isTag(NGob.Tags.bumling) || gob.isTag(NGob.Tags.bush))))
			return new NModelBox(gob);
		return null;
	}

    @Override
    public void added(RenderTree.Slot slot) {
		if (slots == null)
			slots = new ArrayList<>(1);
		slots.add(slot);
		backup = new ArrayList<>(slots);
		slot.ostate(state);
	}

	public void removed(RenderTree.Slot slot) {
		if(slots != null)
			slots.remove(slot);
	}

    @Override
    public void draw(Pipe context, Render out) {
	if(model != null) {
	    out.draw(context, model);
	}
    }

	private static Model getModel(Gob gob) {
	Model model = null;
	Resource res = getResource(gob);
	if(res!=null) {
		synchronized (MODEL_CACHE) {
			model = MODEL_CACHE.get(res);
			if (model == null) {
				List<List<Coord3f>> polygons = new LinkedList<>();

				Collection<Resource.Neg> negs = res.layers(Resource.Neg.class);
				if (negs != null) {
					for (Resource.Neg neg : negs) {
						List<Coord3f> box = new LinkedList<>();
						box.add(new Coord3f(neg.ac.x, -neg.ac.y, Z));
						box.add(new Coord3f(neg.bc.x, -neg.ac.y, Z));
						box.add(new Coord3f(neg.bc.x, -neg.bc.y, Z));
						box.add(new Coord3f(neg.ac.x, -neg.bc.y, Z));

						polygons.add(box);
					}
				}

				Collection<Resource.Obstacle> obstacles = res.layers(Resource.Obstacle.class);
				if (obstacles != null) {
					for (Resource.Obstacle obstacle : obstacles) {
						if ("build".equals(obstacle.id)) {
							continue;
						}
						for (Coord2d[] polygon : obstacle.p) {
							polygons.add(Arrays.stream(polygon)
									.map(coord2d -> new Coord3f((float) coord2d.x, (float) -coord2d.y, Z))
									.collect(Collectors.toList()));
						}
					}
				}

				if (!polygons.isEmpty()) {
					List<Float> vertices = new LinkedList<>();

					for (List<Coord3f> polygon : polygons) {
						addLoopedVertices(vertices, polygon);
					}

					float[] data = convert(vertices);
					VertexArray.Buffer vbo = new VertexArray.Buffer(data.length * 4, DataBuffer.Usage.STATIC, DataBuffer.Filler.of(data));
					VertexArray va = new VertexArray(LAYOUT, vbo);

					model = new Model(Model.Mode.LINES, va, null);

					MODEL_CACHE.put(res, model);
				}
			}
		}
	}

	return model;
    }

    private static float[] convert(List<Float> list) {
	float[] ret = new float[list.size()];
	int i = 0;
	for (Float value : list) {
	    ret[i++] = value;
	}
	return ret;
    }

    private static void addLoopedVertices(List<Float> target, List<Coord3f> vertices) {
	int n = vertices.size();
	for (int i = 0; i < n; i++) {
	    Coord3f a = vertices.get(i);
	    Coord3f b = vertices.get((i + 1) % n);
	    Collections.addAll(target, a.x, a.y, a.z);
	    Collections.addAll(target, b.x, b.y, b.z);
	}
    }

    private static Resource getResource(Gob gob) {
	Resource res = gob.getres();
	if(res == null) {return null;}
	Collection<RenderLink.Res> links = res.layers(RenderLink.Res.class);
	for (RenderLink.Res link : links) {
	    if(link.l instanceof RenderLink.MeshMat) {
		RenderLink.MeshMat mesh = (RenderLink.MeshMat) link.l;
		return mesh.mesh.get();
	    }
	}
	return res;
    }



}
