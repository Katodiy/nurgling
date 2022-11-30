package nurgling;

import haven.*;
import haven.Composite;
import haven.res.gfx.fx.fishline.FishLine;
import haven.res.lib.tree.TreeScale;

import java.awt.*;
import java.util.*;

public class NGob {
    public Tex noteImg = null;

    public int oldModSize = -1;

    private NHitBox hitBox = null;

    public NHitBox getHitBox() {
        Gob gob = (Gob)this;
        if(hitBox!=null) {
            hitBox.correct(gob.rc, gob.a);
        }
        return hitBox;
    }

    public enum Tags {
        boy,
        girl,
        child,
        angryhorse,
        borka,
        player,
        notplayer,
        knocked,
        mounted,
        worked,
        lifted,
        /// type
        house,
        container,
        liftable,
        dframe,
        cupboard,
        wchest,
        bchest,
        mcabinet,
        crate,
        marked,

        ttub,

        resource,
        station,
        htable,
        kritter,
        horse,
        cow,
        pig,
        goat,
        sheep,
        wool,
        transport,
        pushed,
        pushable,
        area,
        ready,
        inwork,
        free,
        warning,
        incubatorc,
        incubatorr,
        no_silo,
        no_water,
        no_soil,
        gardenpot,
        properties,
        not_full,
        full,
        demijohn,
        cheeserack,
        barrel,
        beeskelp,
        wax,
        trough,
        plant,
        tanning,
        highlighted,
        barterhand,
        brazier,
        gem,
        truffle,
        item,
        unknown,
        foe,
        notmarked,
        pow,
        notified,

        minesupport,
        stockpile,
        fishing,
        table,
        tree,
        bush,
        consobj,
        growth,
        trellis,
        gate, cellar;
    }

    public final HashSet<Tags> tags = new HashSet<>();
    public final HashSet<Tags> oldtags = new HashSet<>();
    public final ArrayList<NProperties> properties = new ArrayList<>();

    NProperties.Container getContainer() {
        if (!properties.isEmpty()) {
            for (NProperties prop : properties) {
                if (prop instanceof NProperties.Container) {
                    return (NProperties.Container) prop;
                }
            }
        }
        return null;
    }

    public NProperties.Crop getCrop() {
        if (!properties.isEmpty()) {
            for (NProperties prop : properties) {
                if (prop instanceof NProperties.Crop) {
                    return (NProperties.Crop) prop;
                }
            }
        }
        return null;
    }

    public enum Status {
        ready_for_update,
        updated,
        undefined,
        disposed
    }

    public Status status = Status.undefined;

    protected long modelAttribute = -1;

    public long getModelAttribute() {
        if(modelAttribute!=-1)
            return modelAttribute;
        else
            modelAttribute = setDrawAttribute((Gob) this);
        return modelAttribute;
    }

    protected void addTag(Tags tag) {
        if (tags.add(tag)) {
            if (tag == Tags.marked)
                modelAttribute = setDrawAttribute((Gob) this);
            status = Status.ready_for_update;
        }
    }

    public void addTag(Tags... tags) {
        for (Tags tag : tags)
            addTag(tag);
    }


    protected void removeTag(Tags tag) {
        if (tags.remove(tag))
        {
            oldtags.add(tag);
            status = Status.ready_for_update;

        }
    }

    protected void installTags(Resource res) {
        if (res != null ) {
            String name = res.name;

            if(NUtils.checkName(name, "tree")){
                addTag(Tags.tree);
            }
            if(NUtils.checkName(name, "cellar")){
                addTag(Tags.cellar);
            }
            if (NUtils.checkName(name, "borka")) {
                addTag(Tags.borka);
                status = Status.undefined;
            }
            else if(NUtils.checkName(name, "bushes")){
                addTag(Tags.bush);
            }
            else if (NUtils.checkName(name, "pow")) {
                    addTag(Tags.pow, Tags.marked);
            } else if (NUtils.checkName(name, "items")) {
                addTag(Tags.item);
                if (NUtils.checkName(name, "gem")) {
                    addTag(Tags.gem);
                } else if (NUtils.checkName(name, "truffle")) {
                    addTag(Tags.truffle);
                }
            } else if (NUtils.checkName(name, "pow")) {
                addTag(Tags.pow);
            } else if (NUtils.checkName(name, "minebeam", "column", "towercap", "ladder", "minesupport")) {
                addTag(Tags.minesupport);
            } else if (NUtils.checkName(name, new NAlias(new ArrayList<>(Arrays.asList("plants")), new ArrayList<>(Arrays.asList("trellis"))))) {
                addTag(Tags.plant);
                addTag(Tags.marked);
                int cropstgmaxval = 0;
                for (FastMesh.MeshRes layer : res.layers(FastMesh.MeshRes.class)) {
                    int stg = layer.id / 10;
                    if (stg > cropstgmaxval) {
                        cropstgmaxval = stg;
                    }
                }
                if(NUtils.checkName(name, "turnip"))
                    properties.add(new NProperties.Crop(1, cropstgmaxval));
                else if(NUtils.checkName(name, "carrot"))
                    properties.add(new NProperties.Crop(3, cropstgmaxval));
                else
                    properties.add(new NProperties.Crop(-1, cropstgmaxval));
            }
            else if (NUtils.checkName(name, "stockpile")){
                addTag(Tags.stockpile, Tags.marked);
            }
            else if (NUtils.checkName(name, new NAlias(new ArrayList<>(Arrays.asList("cheeserack", "bumlings", "cupboard", "demijohn","table", "dugout", "candelabrum", "gardenpot", "barrel", "chest", "crate", "log", "trough", "casket", "meatgri", "cauldron", "beehive", "dreca", "barrow", "rowboat", "potterswheel", "churn", "metalcabinet", "iconsign", "plow", "ttub")), new ArrayList<>(Arrays.asList("wild", "tree"))))) {
                addTag(Tags.liftable);
                if (NUtils.checkName(name, "table","cupboard", "chest", "crate", "metalcabinet", "boiler", "casket", "ttub")) {
                    addTag(Tags.container);
                    addTag(Tags.marked);
                    if (NUtils.checkName(name, "cauldron"))
                        addTag(Tags.station);
                    else if (NUtils.checkName(name, "ttub"))
                        addTag(Tags.ttub);
                    else if (NUtils.checkName(name, "cupboard", "chest", "crate", "metalcabinet", "casket")) {
                        addTag(Tags.properties);
                        if (NUtils.checkName(name, "cupboard"))
                            properties.add(new NProperties.Container("Cupboard", 3, 16));
                        else if (NUtils.checkName(name, "largechest"))
                            properties.add(new NProperties.Container("Large Chest", 3, 16));
                        else if (NUtils.checkName(name, "chest"))
                            properties.add(new NProperties.Container("Chest", 3, 28));
                        else if (NUtils.checkName(name, "metalcabinet"))
                            properties.add(new NProperties.Container("Metal Cabinet", 3, 64));
                        else if (NUtils.checkName(name, "crate"))
                            properties.add(new NProperties.Container("Crate", 0, 16));
                        else if (NUtils.checkName(name, "casket"))
                            properties.add(new NProperties.Container("Stone Casket", 3, 16));
                    }
                } else if (NUtils.checkName(name, "cheeserack")) {
                    addTag(Tags.container, Tags.cheeserack);
                    status = Status.undefined;
                } else if (NUtils.checkName(name, "rowboat", "dugout")) {
                    addTag(Tags.transport);
                } else if (NUtils.checkName(name, "log", "dreca")) {
                    addTag(Tags.resource);
                } else if (NUtils.checkName(name, "barrel")) {
                    addTag(Tags.barrel);
                    status = Status.undefined;
                } else if (NUtils.checkName(name, "potterswheel", "churn", "meatgri")) {
                    addTag(Tags.station);
                } else {
                    addTag(Tags.marked);
                    if (NUtils.checkName("demijohn")) {
                        addTag(Tags.demijohn);
                    } else if (NUtils.checkName(name, "gardenpot")) {
                        addTag(Tags.gardenpot);
                        status = Status.undefined;
                    } else if (NUtils.checkName(name, "beehive", "trough")) {
                        addTag(Tags.area);
                        if (NUtils.checkName(name, "trough")) {
                            addTag(Tags.trough);
                        } else if (NUtils.checkName(name, "beehive")) {
                            addTag(Tags.beeskelp);
                        }
                    } else if (NUtils.checkName(name, "barrow", "plow")) {
                        addTag(Tags.pushable);
                    }
                }
                if (((Gob) this).getattr(Following.class) != null)
                    status = Status.undefined;
            } else if (NUtils.checkName(name, "dframe", "oven", "smelter", "htable", "kiln", "smokeshed", "chickencoop", "rabbithutch")) {
                addTag(Tags.container);
                if (NUtils.checkName(name, "dframe")) {
                    addTag(Tags.dframe);
                    status = Status.undefined;
                } else if (NUtils.checkName(name, "htable"))
                    addTag(Tags.htable);
                else {
                    addTag(Tags.marked);
                    if (NUtils.checkName(name, "chickencoop")) {
                        addTag(Tags.incubatorc);
                    } else if (NUtils.checkName(name, "rabbithutch")) {
                        addTag(Tags.incubatorr);
                    } else {
                        addTag(Tags.station);
                    }
                }
            } else if (NUtils.checkName(name, new NAlias(new ArrayList<>(Arrays.asList("kritter")), new ArrayList<>(Arrays.asList("beef"))))) {
                addTag(Tags.kritter);
                if (NUtils.checkName(name, new NAlias(new ArrayList<>(Arrays.asList("pig", "horse", "goat", "cattle", "sheep")), new ArrayList<>(Collections.singletonList("wild"))))) {
                    if (NUtils.checkName(name, "horse")) {
                        addTag(Tags.horse);
                        addTag(Tags.transport);
                    } else if (NUtils.checkName(name, "pig"))
                        addTag(Tags.pig);
                    else if (NUtils.checkName(name, "cattle"))
                        addTag(Tags.cow);
                    else if (NUtils.checkName(name, "goat"))
                        addTag(Tags.goat);
                    else if (NUtils.checkName(name, "sheep"))
                        addTag(Tags.sheep);
                }
                Gob gob = ((Gob) this);
                Composite cmp = gob.getattr(Composite.class);
                if (cmp != null) {
                    checkPoses(cmp.nposes);
                }
            } else if (NUtils.checkName(name, "snekkja", "knarr")) {
                addTag(Tags.transport);
            } else if (NUtils.checkName(name, "barterhand")) {
                addTag(Tags.barterhand, Tags.area);
            } else if (NUtils.checkName(name, "brazier")) {
                addTag(Tags.brazier, Tags.area);
            }
            else if (NUtils.checkName(name, "consobj")) {
                addTag(Tags.consobj);
            }
            else if (NUtils.checkName(name, "trellis")) {
                addTag(Tags.trellis);
            }
            else if (NUtils.checkName(name, "gate")) {
                addTag(Tags.gate);
                addTag(Tags.marked);
            }
            if(!isTag(Tags.item) &&!isTag(Tags.plant) &&!isTag(Tags.cellar) && !(isTag(Tags.pow) && (getModelAttribute()&17)==17)){
                hitBox = NHitBox.hitboxes.get(name);
                if(hitBox == null){
                    hitBox = NHitBox.getByName(name);
                }
            }
        }
    }

    public boolean isTag(Tags... tags_candidates) {
        for (Tags tag : tags_candidates) {
            if (!tags.contains(tag))
                return false;
        }
        return true;
    }


    public static final OCache.ChangeCallback CHANGED = new OCache.ChangeCallback() {
        @Override
        public void added(Gob ob) {

        }

        @Override
        public void removed(Gob ob) {
            ob.disposeAction();
        }
    };

    protected void updateCustom() {
        if (status != Status.updated) {
            if(((Gob)this).getattr(GobIcon.class) == null) {
                GobIcon icon = NUtils.getIcon(this);
                if(icon != null) {
                    icon.img();
                    ((Gob)this).setattr(icon);
                }
            }
            switch (status) {
                case ready_for_update: {
                    if(NConfiguration.getInstance().enablePfBoundingBoxes){
                        Gob gob = ((Gob) this);
                        if(gob.getHitBox()!=null && gob.findol(NGobHiteBoxSpr.class)==null){
                            gob.addol(new NGobHiteBoxSpr(NBoundingBox.getBoundingBox(gob)));
                        }
                    }
                    if (isTag(Tags.notified)) {
                        Gob gob = ((Gob) this);
                        gob.removeol(NNotifiedRing.class);
                        gob.findoraddol(new NNotifiedRing(gob, Color.GREEN, 20, 0.7f, noteImg));
                    } else {
                        Gob gob = ((Gob) this);
                        gob.removeol(NNotifiedRing.class);
                    }
                    if (isTag(Tags.highlighted)) {
                        Gob gob = ((Gob) this);
                        if(gob.findol(NHighlightRing.class)==null) {
                            gob.findoraddol(new NHighlightRing(gob));
                            gob.setattr(new NGobHighlight(gob));
                        }
                    }
                    if (isTag(Tags.plant) && NConfiguration.getInstance().showCropStage ) {
                        Gob gob = ((Gob) this);
                        NProperties.Crop crop = getCrop();
                        if(modelAttribute!=crop.currentStage || gob.findol(NCropMarker.class) == null) {
                            if (gob.findol(NCropMarker.class) != null)
                            {
                                gob.removeol(NCropMarker.class);
                                return;
                            }
                            if (modelAttribute == crop.maxstage) {
                                if (crop.maxstage == 0) {
                                    gob.findoraddol(new Gob.Overlay(gob, new NCropMarker(gob, Color.GRAY)));
                                } else {
                                    gob.findoraddol(new Gob.Overlay(gob, new NCropMarker(gob, Color.GREEN)));
                                }
                            } else if (modelAttribute == 0) {
                                gob.findoraddol(new Gob.Overlay(gob, new NCropMarker(gob, Color.RED)));
                            } else {
                                if (crop.maxstage > 1 && crop.maxstage < 7) {
                                    if(gob.modelAttribute == crop.specstage){
                                        gob.findoraddol(new Gob.Overlay(gob, new NCropMarker(gob, Color.BLUE)));
                                    }else {
                                        gob.findoraddol(new Gob.Overlay(gob, new NCropMarker(gob, modelAttribute, crop.maxstage)));
                                    }
                                }
                            }
                        }
                        crop.currentStage = modelAttribute;
                    } else if (isTag(Tags.item)) {
                        Gob gob = ((Gob) this);
                        if (isTag(Tags.gem)) {
                            gob.findoraddol(new NTexMarker(gob, 5, Tags.gem));
                        } else if (isTag(Tags.truffle)) {
                            gob.findoraddol(new NTexMarker(gob, 10, Tags.truffle));
                        }
                    } else if (isTag(Tags.barrel)) {
                        Gob gob = ((Gob) this);
                        if (isTag(Tags.free)) {
                            gob.setattr(new NGobColor(gob, NConfiguration.getInstance().colors.get("free")));
                        } else {
                            gob.delattr(NGobColor.class);
                        }
                    } else if (isTag(Tags.pushable, Tags.marked)) {
                        if ((modelAttribute & 1) != 0)
                            addTag(Tags.pushed);
                        else
                            removeTag(Tags.pushed);
                    } else if(isTag(Tags.tree,Tags.growth) || isTag(Tags.bush,Tags.growth)){
                        Gob gob =  ((Gob)this);
                        double scale = 0;
                        TreeScale ts = ((Gob)this).getattr(TreeScale.class);
                        if(isTag(Tags.tree)) {
                            scale = Math.round(100*(ts.scale - 0.1)/0.9);
                        } else if(isTag(Tags.bush)) {
                            scale = Math.round(100*(ts.scale - 0.3)/0.7);
                        }
                        gob.findoraddol(new Gob.Overlay(gob, new NObjectLabel(gob, String.format("%.0f %%", (float) scale), Color.WHITE)));
                    }
                    else if (isTag(Tags.ttub)) {
                        if ((modelAttribute & 1) != 0 || modelAttribute == 0) {
                            addTag(Tags.warning);
                        } else {
                            removeTag(Tags.warning);
                        }
                        if ((modelAttribute & 8) != 0) {
                            addTag(Tags.ready);
                            removeTag(Tags.inwork, Tags.free);
                        } else if ((modelAttribute & 4) != 0) {
                            addTag(Tags.inwork);
                            removeTag(Tags.free, Tags.ready);
                        } else if ((modelAttribute & 2) != 0) {
                            addTag(Tags.free);
                            removeTag(Tags.inwork, Tags.ready);
                        } else {
                            removeTag(Tags.inwork, Tags.free, Tags.ready);
                        }
                    }
                    if (isTag(Tags.dframe) || isTag(Tags.ttub)) {
                        Gob gob = ((Gob) this);
                        if (isTag(Tags.ready))
                            gob.setattr(new NGobColor(gob, NConfiguration.getInstance().colors.get("ready")));
                        else if (isTag(Tags.warning)) {
                            gob.addTag(Tags.tanning);
                            gob.setattr(new NGobColor(gob, NConfiguration.getInstance().colors.get("warning")));
                            gob.findoraddol(new NTexMarker(gob, Tags.tanning));
                        } else {
                            gob.removeTag(Tags.tanning);
                            gob.removeol(NTexMarker.class);
                            if (isTag(Tags.free))
                                gob.setattr(new NGobColor(gob, NConfiguration.getInstance().colors.get("free")));
                            else if(isTag(Tags.inwork))
                                gob.setattr(new NGobColor(gob, NConfiguration.getInstance().colors.get("inwork")));
                        }
                    } else if (isTag(Tags.incubatorc) || isTag(Tags.incubatorr)) {
                        if (isTag(Tags.incubatorc)) {
                            if ((modelAttribute & 1) == 0) {
                                addTag(Tags.no_water);
                            } else {
                                removeTag(Tags.no_water);
                            }
                            if ((modelAttribute & 2) == 0) {
                                addTag(Tags.no_silo);
                            } else {
                                removeTag(Tags.no_silo);
                            }
                        } else {
                            if ((modelAttribute & 4) == 0) {
                                addTag(Tags.no_water);
                            } else {
                                removeTag(Tags.no_water);
                            }
                            if ((modelAttribute & 16) == 0) {
                                addTag(Tags.no_silo);
                            } else {
                                removeTag(Tags.no_silo);
                            }
                        }
                        Gob gob = ((Gob) this);
                        if (isTag(Tags.no_water) || isTag(Tags.no_silo)) {
                            gob.removeol(NTexMarker.class);
                            if (isTag(Tags.no_water) && isTag(Tags.no_silo))
                                gob.findoraddol(new NTexMarker(gob, Tags.no_water, Tags.no_silo));
                            else if (isTag(Tags.no_water)) {
                                gob.findoraddol(new NTexMarker(gob, Tags.no_water));
                            } else {
                                gob.findoraddol(new NTexMarker(gob, Tags.no_silo));
                            }
                            gob.setattr(new NGobColor(gob, NConfiguration.getInstance().colors.get("warning")));
                        } else {
                            gob.removeol(NTexMarker.class);
                            gob.delattr(NGobColor.class);
                        }
                    } else if (isTag(Tags.gardenpot)) {
                        if ((modelAttribute & 1) == 0) {
                            addTag(Tags.no_water);
                        } else if ((modelAttribute & 2) == 0) {
                            addTag(Tags.no_soil);
                        } else {
                            removeTag(Tags.no_water, Tags.no_soil);
                        }
                        Gob gob = ((Gob) this);
                        if (isTag(Tags.no_water))
                            gob.setattr(new NGobColor(gob, NConfiguration.getInstance().colors.get("no_water")));
                        else if (isTag(Tags.no_soil)) {
                            gob.setattr(new NGobColor(gob, NConfiguration.getInstance().colors.get("no_soil")));
                        } else if (isTag(Tags.free)) {
                            gob.setattr(new NGobColor(gob, NConfiguration.getInstance().colors.get("free")));
                        } else if (isTag(Tags.inwork)) {
                            gob.setattr(new NGobColor(gob, NConfiguration.getInstance().colors.get("inwork")));
                        } else {
                            gob.setattr(new NGobColor(gob, NConfiguration.getInstance().colors.get("ready")));
                        }
                    }
                    if (isTag(Tags.container, Tags.properties)) {
                        NProperties.Container cont = getContainer();
                        if (cont != null) {
                            if ((modelAttribute & ~cont.free) == 0) {
                                addTag(Tags.free);
                                removeTag(Tags.not_full, Tags.full);
                            } else if ((modelAttribute & cont.full) == cont.full) {
                                addTag(Tags.full);
                                removeTag(Tags.not_full, Tags.free);
                            } else {
                                addTag(Tags.not_full);
                                removeTag(Tags.free, Tags.full);
                            }
                            Gob gob = ((Gob) this);
                            if (isTag(Tags.full))
                                gob.setattr(new NGobColor(gob, NConfiguration.getInstance().colors.get("full")));
                            else if (isTag(Tags.not_full)) {
                                gob.setattr(new NGobColor(gob, NConfiguration.getInstance().colors.get("not_full")));
                            } else {
                                gob.delattr(NGobColor.class);
                            }
                        }
                    } else if (isTag(Tags.demijohn)) {
                        if ((modelAttribute & 1) == 0) {
                            addTag(Tags.free);
                        } else {
                            removeTag(Tags.free);
                        }
                        Gob gob = ((Gob) this);
                        if (isTag(Tags.free)) {
                            gob.setattr(new NGobColor(gob, NConfiguration.getInstance().colors.get("free")));
                        } else {
                            gob.delattr(NGobColor.class);
                        }
                    } else if (isTag(Tags.minesupport)) {
                        Gob gob = ((Gob) this);
                        if (NConfiguration.getInstance().rings.get("minesup").isEnable && gob.findol(NAreaRad.class)==null) {
                            if (NUtils.checkName(gob.getResName(), new NAlias(new ArrayList<>(Arrays.asList("natural"))))) {
                                gob.findoraddol(new Gob.Overlay(gob,
                                        new NAreaRad(gob, 93.5f, new Color(255, 0, 0, 128), new Color(0, 192, 192, 255))));
                            } else if (NUtils.checkName(gob.getResName(), new NAlias(new ArrayList<>(Arrays.asList("ladder", "minesupport", "towercap"))))) {
                                gob.findoraddol(new Gob.Overlay(gob,
                                        new NAreaRad(gob, 100, new Color(255, 0, 0, 128), new Color(0, 192, 192, 255))));
                            } else if (NUtils.checkName(gob.getResName(), new NAlias("minebeam"))) {
                                gob.findoraddol(new Gob.Overlay(gob,
                                        new NAreaRad(gob, 150, new Color(255, 0, 0, 128), new Color(0, 192, 192, 255))));
                            } else if (NUtils.checkName(gob.getResName(), new NAlias("column"))) {
                                gob.findoraddol(new Gob.Overlay(gob,
                                        new NAreaRad(gob, 125, new Color(255, 0, 0, 128), new Color(0, 192, 192, 255))));
                            }
                        }
                    }
                    if (isTag(Tags.knocked)) {
                        Gob gob = ((Gob) this);
                        gob.removeol(NAreaRad.class);
                        gob.removeol(NDmgOverlay.class);
                        gob.removeol(NTargetRing.class);
//                        gob.removeTag(Tags.notmarked);
                    } else if (isTag(Tags.kritter)) {
                        Gob gob = ((Gob) this);
                        for (String ring : NConfiguration.getInstance().rings.keySet()) {
                            if (gob.getResName().contains(ring)) {
                                NConfiguration.Ring ringprop = NConfiguration.getInstance().rings.get(ring);
                                if (ringprop.isEnable && gob.findol(NAreaRad.class)==null) {
                                    gob.findoraddol(new Gob.Overlay(gob,
                                            new NAreaRad(gob, (float) ringprop.size, new Color(255, 0, 0, 128), new Color(0, 192, 192, 255))));
                                }
                            }
                        }
                    } else if (isTag(Tags.trough)) {
                        if ((modelAttribute & 2) == 0) {
                            addTag(Tags.warning);
                        } else {
                            removeTag(Tags.warning);
                        }
                        Gob gob = ((Gob) this);
                        if (isTag(Tags.warning)) {
                            gob.setattr(new NGobColor(gob, NConfiguration.getInstance().colors.get("warning")));
                            gob.addTag(Tags.no_silo);
                            gob.findoraddol(new NTexMarker(gob, Tags.no_silo));
                        } else {
                            gob.removeTag(Tags.no_silo);
                            gob.delattr(NGobColor.class);
                            gob.removeol(NTexMarker.class);
                        }
                        if (NConfiguration.getInstance().rings.get("trough").isEnable) {
                            gob.findoraddol(new Gob.Overlay((Gob) this,
                                    new NAreaRad(gob, (float) NConfiguration.getInstance().rings.get("trough").size, new Color(192, 192, 0, 128), new Color(0, 164, 192, 255))));
                        } else {
                            gob.removeol(NAreaRad.class);
                        }
                    } else if (isTag(Tags.beeskelp)) {
                        if ((modelAttribute & 4) != 0) {
                            addTag(Tags.wax);
                        } else {
                            removeTag(Tags.wax);
                        }
                        Gob gob = ((Gob) this);
                        if (isTag(Tags.wax)) {
                            gob.findoraddol(new NTexMarker(gob, Tags.wax));
                        } else {
                            gob.removeol(NTexMarker.class);
                        }
                        if (NConfiguration.getInstance().rings.get("beeskep").isEnable) {
                            gob.findoraddol(new Gob.Overlay((Gob) this,
                                    new NAreaRad(gob, (float) NConfiguration.getInstance().rings.get("beeskep").size, new Color(0, 163, 192, 128), new Color(0, 192, 0, 255))));
                        } else {
                            gob.removeol(NAreaRad.class);
                        }
                    } else if (isTag(Tags.cheeserack)) {
                        Gob gob = ((Gob) this);
                        if (isTag(Tags.full)) {
                            gob.setattr(new NGobColor(gob, NConfiguration.getInstance().colors.get("full")));
                        } else if (isTag(Tags.not_full)) {
                            gob.setattr(new NGobColor(gob, NConfiguration.getInstance().colors.get("not_full")));
                        } else {
                            gob.delattr(NGobColor.class);
                        }
                    }
                    if(isTag(Tags.angryhorse)){
                        Gob gob = ((Gob) this);
                        if(gob.findol(NTexMarker.class)==null) {
                            gob.addol(new Gob.Overlay(gob, new NTexMarker(gob, 20, Tags.angryhorse)));
                        }
                    }
                    if(isTag(Tags.wool)){
                        Gob gob = ((Gob) this);
                        if(gob.findol(NTexMarker.class)==null) {
                            gob.addol(new Gob.Overlay(gob, new NTexMarker(gob, 10, Tags.wool)));
                        }
                    }
                    if (isTag(Tags.notmarked)) {
                        Gob gob = ((Gob) NOCache.getgob(Tags.player));
                        if (gob != null) {
                            for (Gob unk : NOCache.getObjects(Tags.unknown, Tags.notmarked)) {
                                for (int i = 1500; i < 1600; i++)
                                    if (gob.findol(i) == null) {
                                        if (!unk.tags.contains(Tags.pow) && !unk.tags.contains(Tags.knocked)) {
                                            if (NConfiguration.getInstance().players.get("white").arrow) {
                                                gob.addol(new Gob.Overlay(gob, new NDirArrow(gob, Color.WHITE, 25, unk, null, NConfiguration.getInstance().players.get("white")), i));
                                            }
                                            if (NConfiguration.getInstance().players.get("white").ring) {
                                                    unk.findoraddol(new NTargetRing(unk, Color.WHITE, 10, 0.9f));
                                            }
                                        }
                                        unk.removeTag(Tags.notmarked);
                                        break;
                                    }
                            }
                            for (Gob foe : NOCache.getObjects(Tags.foe, Tags.notmarked)) {
                                for (int i = 1500; i < 1600; i++)
                                    if (gob.findol(i) == null) {
                                        if (!foe.tags.contains(Tags.pow) && !foe.tags.contains(Tags.knocked)) {
                                            if (NConfiguration.getInstance().players.get("red").arrow) {
                                                gob.addol(new Gob.Overlay(gob, new NDirArrow(gob, Color.RED, 30, foe, NConfiguration.getInstance().players.get("red").mark ? new TexI(Resource.loadsimg("icon/devil")) : null, NConfiguration.getInstance().players.get("red")), i));
                                            }
                                            if (NConfiguration.getInstance().players.get("red").ring) {
                                                foe.findoraddol(new NMarkedRing(foe, Color.RED, 10, 0.9f, NConfiguration.getInstance().players.get("red").mark_target ? new TexI(Resource.loadsimg("icon/devil")) : null));
                                            }
                                        }
                                        foe.removeTag(Tags.notmarked);
                                        break;
                                    }
                            }
                        } else {
                            return;
                        }
                    } else if (isTag(Tags.barterhand)) {
                        Gob gob = ((Gob) this);
                        if (NConfiguration.getInstance().rings.get("barterhand").isEnable) {
                            gob.findoraddol(new Gob.Overlay((Gob) this,
                                    new NAreaRad(gob, (float) NConfiguration.getInstance().rings.get("barterhand").size, new Color(163, 0, 192, 128), new Color(0, 0, 192, 255))));
                        } else {
                            gob.removeol(NAreaRad.class);
                        }
                    } else if (isTag(Tags.brazier)) {
                        Gob gob = ((Gob) this);
                        if (NConfiguration.getInstance().rings.get("brazier").isEnable) {
                            gob.findoraddol(new Gob.Overlay((Gob) this,
                                    new NAreaRad(gob, (float) NConfiguration.getInstance().rings.get("brazier").size, new Color(192, 111, 55, 128), new Color(123, 23, 76, 255))));
                        } else {
                            gob.removeol(NAreaRad.class);
                        }
                    }
                    status = Status.updated;
                    break;
                }
                case undefined: {
                    if (NUtils.getGameUI() != null && NUtils.getGameUI().map != null) {
                        if (isTag(Tags.borka)) {
                            if (NUtils.getGameUI().map.player() != null) {
                                ArrayList<Gob> borkas = NOCache.getObjects(Tags.borka);
                                for (Gob borka : borkas) {
                                    if (borka.id == NUtils.getGameUI().map.player().id) {

                                        Following following;
                                        if ((following = borka.getattr(Following.class)) != null) {
                                            if (following.tgt() != null) {
                                                borka.removeTag(Tags.borka);
                                                borka.addTag(Tags.player);
                                                if (NUtils.getGameUI().drives != -1) {
                                                    installFollowing(borka, following);
                                                } else {
                                                    installFollowing(borka, following);
                                                    if (NUtils.isIt(NUtils.getGob(NUtils.getGameUI().drives), "horse"))
                                                        NUtils.setSpeed(NConfiguration.getInstance().horseSpeed);
                                                }
                                            }
                                        } else {
                                            borka.removeTag(Tags.borka);
                                            borka.addTag(Tags.player);
                                        }
                                    } else {
                                        borka.removeTag(Tags.borka);
                                        borka.addTag(Tags.notplayer);
                                        Following following;
                                        if ((following = borka.getattr(Following.class)) != null)
                                            installFollowing(borka, following);
                                        if (KinInfo.getGroup(borka) == 0 || (KinInfo.getGroup(borka) == -1 && borka.getattr(NGobHealth.class) == null)) {
                                            borka.addTag(Tags.unknown, Tags.notmarked);
                                        } else if (KinInfo.getGroup(borka) == 2) {
                                            borka.addTag(Tags.foe, Tags.notmarked);
                                        }
                                    }
                                }
                            } else {
                                return;
                            }
                        } else if (isTag(Tags.dframe)) {
                            if (((Gob) this).ols.isEmpty() || (((Gob) this).ols.size() == 1 && ((Gob) this).findol(NObjectLabel.class)!=null))
                                addTag(Tags.free);
                            else {
                                for(Gob.Overlay ol: ((Gob) this).ols){
                                    if(!NUtils.isIt(ol,"-blood", "-fishraw","-windweed") || NUtils.isIt(ol,"-windweed-dry") )
                                        addTag(Tags.ready);
                                    else
                                        addTag(Tags.inwork);
                                }
                            }
                        } else if (isTag(Tags.gardenpot)) {
                            if (((Gob) this).ols.isEmpty() || (((Gob) this).ols.size() == 1 && ((Gob) this).findol(NObjectLabel.class)!=null)) {
                                addTag(Tags.free);
                                removeTag(Tags.inwork, Tags.ready);
                            } else if (((Gob) this).ols.size() == 1) {
                                addTag(Tags.inwork);
                                removeTag(Tags.free, Tags.ready);
                            } else {
                                addTag(Tags.ready);
                                removeTag(Tags.free, Tags.inwork);
                            }

                        } else if (isTag(Tags.cheeserack)) {
                            if (((Gob) this).ols.isEmpty() || (((Gob) this).ols.size() == 1 && ((Gob) this).findol(NObjectLabel.class)!=null)) {
                                addTag(Tags.free);
                                removeTag(Tags.not_full, Tags.full);
                            } else if (((Gob) this).ols.size() < 3) {
                                addTag(Tags.not_full);
                                removeTag(Tags.free, Tags.full);
                            } else {
                                addTag(Tags.full);
                                removeTag(Tags.free, Tags.not_full);
                            }
                        } else if (isTag(Tags.barrel)) {
                            if (((Gob) this).ols.isEmpty() || (((Gob) this).ols.size() == 1 && ((Gob) this).findol(NObjectLabel.class)!=null) )
                                addTag(Tags.free);
                            else
                                removeTag(Tags.free);
                        } else if (isTag(Tags.liftable)) {
                            if (((Gob) this).getattr(Following.class) != null)
                                ((Gob) this).tags.add(Tags.lifted);
                            ///NOCache.addBoon(((Following) a).tgt,Tags.lifted);
                        }
                        status = Status.ready_for_update;
                    }
                }
            }
            if(!oldtags.isEmpty()){
                for(Tags tag: oldtags){
                    if(tag == Tags.highlighted)
                    {
                        ((Gob)this).delattr(NGobHighlight.class);
                        ((Gob)this).removeol(NHighlightRing.class);
                    }
                    if(tag == Tags.wool)
                    {
                        ((Gob)this).removeol(NTexMarker.class);
                    }
                }
                oldtags.clear();
            }
        }
    }

    public void removeTag(Tags... tags) {
        for (Tags tag : tags)
            removeTag(tag);
    }

    public void checkPoses(Collection<ResData> poses) {
        if (poses != null)
            for (ResData res : poses) {
                if (NUtils.isIt(res, "dead", "knock", "rigormortis", "drowned")) {
                    addTag(Tags.knocked);
                    ((Gob) this).removeol(NAreaRad.class);
                    ((Gob) this).removeol(NTargetRing.class);
                    ((Gob) this).removeol(NMarkedRing.class);
            }
        }
    }


    protected void checkattr(Gob gob, Class<? extends GAttrib> ac, GAttrib a, GAttrib prev) {
        if (a instanceof ResDrawable) {
            checkMark(gob);
        }
        ///TODO: ?????? а надо ли
        if (ac == Drawable.class) {
            if (a != prev) {
                status = Status.ready_for_update;
            }
        }
        if (prev instanceof Following) {
            Following follow = (Following) prev;

            if (status != Status.disposed) {
                if (tags.remove(Tags.mounted)) {
                    NOCache.removeBoon(follow.tgt, Tags.mounted);
                    if (tags.contains(Tags.player)) {
                        tags.remove(Tags.angryhorse);
                        gob.removeol(NTexMarker.class);
                        Gob drived;
                        if ((drived = NUtils.getGob(NUtils.getGameUI().drives)) != null)
                            if (drived.tags.contains(Tags.horse))
                                NUtils.setSpeed(NConfiguration.getInstance().playerSpeed);
                        NUtils.getGameUI().drives = -1;
                    } else {
                        NOCache.removeBoon(follow.tgt, Tags.mounted);
                    }
                } else if (tags.remove(Tags.lifted)) {
                    NOCache.removeBoon(follow.tgt, Tags.lifted);
                }
            }
        }
        if (a instanceof Following) {
            if (!tags.contains(Tags.borka) && (tags.contains(Tags.player) || tags.contains(Tags.notplayer))) {
                installFollowing(gob, (Following) a);
                if (tags.contains(Tags.player)) {
                    Gob drived;
                    if ((drived = NUtils.getGob(NUtils.getGameUI().drives)) != null)
                        if (drived.tags.contains(Tags.horse))
                            NUtils.setSpeed(NConfiguration.getInstance().horseSpeed);
                }
            } else if (tags.contains(Tags.liftable) || NUtils.isPose(NUtils.getGob(((Following) a).tgt), new NAlias("banzai"))) {
                gob.tags.add(Tags.lifted);
                NOCache.addBoon(((Following) a).tgt, Tags.lifted);
            } else {
                status = Status.undefined;
            }
        }
        if(prev instanceof TreeScale){
            gob.removeol(NObjectLabel.class);
        }
        if(a instanceof TreeScale) {
            addTag(Tags.growth);
            status = Status.undefined;
        }
        if(a instanceof GobHealth)
        {
            status = Status.undefined;
        }

        if (ac == Moving.class) {
            updateMovingInfo(a, prev);
        }
    }

    protected void updateMovingInfo(GAttrib a, GAttrib prev) {
        boolean me = isTag(Tags.player);
        if (NUtils.getGameUI() != null && NUtils.getGameUI().map != null) {
            if (prev instanceof Moving) {
                ((NOCache) NUtils.getGameUI().map.glob.oc).paths.removePath((Moving) prev);
//                if(me){
//                    NUtils.getGameUI().pathQueue.clear();
//                }
            }
            if (a instanceof LinMove || a instanceof Homing) {
                ((NOCache) NUtils.getGameUI().map.glob.oc).paths.addPath((Moving) a);
            }
            if (NUtils.getGameUI() != null && (me || ((Gob) this).id == NUtils.getGameUI().drives))
                NUtils.getGameUI().pathQueue().ifPresent(pathQueue -> pathQueue.movementChange((Gob) this, prev, a));
        }
    }

    void installFollowing(Gob gob, Following follow) {
        if(follow.tgt()!=null) {
            if (follow.tgt().isTag(Tags.transport)) {
                if(gob.isTag(Tags.player)||(NUtils.getGameUI().map!= null && gob.id == NUtils.getGameUI().map.player().id)) {
                    NUtils.getGameUI().drives = follow.tgt;
                }
                gob.tags.add(Tags.mounted);
                NOCache.addBoon(follow.tgt, Tags.mounted);
            } else if (follow.tgt().isTag(Tags.station)) {
                gob.tags.add(Tags.worked);
                NOCache.addBoon(follow.tgt, Tags.worked);
            }
        }
    }

    protected void checkol(Gob.Overlay ol, boolean isAdd) {
        if(!(ol.spr instanceof  NObjectLabel)) {
            if (isTag(Tags.dframe)) {
                if (isAdd) {
                    removeTag(Tags.free);
                    if (!NUtils.isIt(ol.res, "-blood", "-windweed", "-fishraw")) {
                        addTag(Tags.ready);
                        removeTag(Tags.inwork);
                    } else {
                        removeTag(Tags.ready);
                        addTag(Tags.inwork);
                    }
                } else {
                    removeTag(Tags.ready);
                    removeTag(Tags.inwork);
                    addTag(Tags.free);
                }
            } else if (isTag(Tags.cheeserack)) {
                status = Status.undefined;
            }
            if (isTag(Tags.barrel)) {
                if (isAdd)
                    removeTag(Tags.free);
                else
                    addTag(Tags.free);
            }
        }
//        if(ol.spr instanceof FishLine){
//            if (isAdd) {
//                addTag(Tags.fishing);
//            }else {
//                removeTag(Tags.fishing);
//            }
//        }
    }

    public static void checkMark(Gob gob) {
        if (gob.isTag(Tags.marked)) {
            long attribute = gob.setDrawAttribute(gob);
            if (attribute != gob.modelAttribute) {
                gob.modelAttribute = attribute;
                gob.status = Status.ready_for_update;
            }
        }
    }

    public static long setDrawAttribute(Gob gob) {
        ResDrawable rd = gob.getattr(ResDrawable.class);
        if (rd == null) {
            return 0;
        }
        return calcMarker(rd.sdt);

    }

    public static long calcMarker(Message sdt) {
        if (sdt.rbuf.length >= 4) {
            long res = (sdt.rbuf[3] << 24);
            res += (sdt.rbuf[2] << 16);
            res += ((sdt.rbuf[1] << 8) + sdt.rbuf[0]);
            return res;
        }
        if (sdt.rbuf.length == 2) {
            return ((sdt.rbuf[1] << 8) + sdt.rbuf[0]);
        } else if (sdt.rbuf.length - 1 < 0) {
            return -1;
        }
        return sdt.rbuf[0];

    }

    public static long getModelAttribute(Gob gob) {
        return gob.modelAttribute;
    }

    public static Gob from(Clickable ci) {
        if (ci instanceof Gob.GobClick) {
            return ((Gob.GobClick) ci).gob;
        } else if (ci instanceof Composited.CompositeClick) {
            Gob.GobClick gi = ((Composited.CompositeClick) ci).gi;
            return gi != null ? gi.gob : null;
        }
        return null;
    }


    protected void disposeAction() {
        status = Status.disposed;
        Map<Class<? extends GAttrib>, GAttrib> attr = ((Gob) this).attr;
        for (GAttrib a : attr.values()) {
            if (a instanceof Moving) {
                updateMovingInfo(null, a);
            }
        }
    }

    public static void updateMarked() {
        if(NUtils.getGameUI()!=null) {
            Gob player;
            if ((player = NOCache.getgob(Tags.player)) != null) {
                player.removeolIf(n -> n.spr instanceof NDirArrow);
            }
            for (Gob unk : NOCache.getObjects(Tags.unknown)) {
                unk.removeol(NTargetRing.class);
                unk.addTag(Tags.notmarked);
            }
            for (Gob foe : NOCache.getObjects(Tags.foe)) {
                foe.removeol(NMarkedRing.class);
                foe.addTag(Tags.notmarked);
            }
        }
    }

    public NProperties getProperties(Class<? extends NProperties> prop ){
        for(NProperties item: properties){
            if(item.getClass() == prop)
                return(item);
        }
        return null;
    }

    public void checkMode() {
        if(isTag(Tags.kritter)){
            if(isTag(Tags.sheep)||isTag(Tags.goat)){
                Gob gob = (Gob)this;
                for(GAttrib a : gob.attr.values())
                    if(a instanceof Composite){
                        Composited comp = ((Composite) a).comp;
                        if(findMode(comp.mod, new NAlias(new ArrayList<>(Arrays.asList("fleece")),new ArrayList<>(Arrays.asList("mouflon")))))
                        {
                            addTag(Tags.wool);
                        }else{
                            removeTag(Tags.wool);
                        }
                    }
            }
        }
    }

    private static boolean findMode(Collection<Composited.Model> mods, NAlias name){
        for (Composited.Model mod: mods){
            if(mod.m instanceof FastMesh.ResourceMesh)
                if(NUtils.checkName(((FastMesh.ResourceMesh)mod.m).res.name,name))
                    return true;
        }
        return false;
    }
}
