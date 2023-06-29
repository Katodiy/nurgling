package nurgling;

import haven.*;
import haven.Composite;
import haven.res.lib.tree.TreeScale;
import nurgling.tools.AreasID;

import java.awt.*;
import java.util.*;
import java.util.List;

import static haven.res.lib.itemtex.ItemTex.made_id;

public class NGob {
    public Tex noteImg = null;

    public int oldModSize = -1;
    public int quality = -1;

    protected NHitBox hitBox = null;

    public NHitBox getHitBox() {
        Gob gob = (Gob) this;
        if (hitBox != null) {
            hitBox.correct(gob.rc, gob.a);
        }
        return hitBox;
    }

    String name = "";

    public void setName(String name) {
        this.name = name;
    }

    public enum Tags {
        angryhorse,
        greyseal,
        borka,
        player,
        notplayer,
        knocked,
        mounted,
        container,
        dframe,
        ttub,
        htable,
        station,
        kritter,
        horse,
        cow,
        pig,
        goat,
        sheep,
        selected,
        wool,
        transport,
        vehicle,
        area,
        ready,
        inwork,
        free,
        warning,
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
        beeskep,
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
        moundbed,
        barterarea,
        unknown,
        foe,
        notmarked,
        pow,
        notified,
        quest,
        minesupport,
        table,
        tree,
        bush,
        consobj,
        growth,
        trellis,
        gate,
        cellar,
        iconsign,
        quester,
        bumling,
        bear,
        wolf,
        wild,
        mammoth,
        orca,
        stoat,
        rabbithutch, chickencoop, stalagoomba, kritter_is_ready, qbring, qrage, qwave, qlaugh, qgreet, qcompleted, quality, troll, spermwhale, looserock, winter_stoat
    }

    public final HashSet<Tags> tags = new HashSet<>();
    public final ArrayList<NProperties> properties = new ArrayList<>();

    protected NProperties.Container getContainer() {
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
        return modelAttribute;
    }

    protected void addTag(Tags tag) {
        if (tags.add(tag))
            status = Status.ready_for_update;
    }

    public void addTag(Tags... tags) {
        for (Tags tag : tags)
            addTag(tag);
    }


    protected void removeTag(Tags tag) {
        tags.remove(tag);
    }

    public void removeTag(Tags... tags) {
        for (Tags tag : tags)
            removeTag(tag);
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

    protected void checkattr(Gob gob, Class<? extends GAttrib> ac, GAttrib a, GAttrib prev) {
        if (prev instanceof Following) {
            if (tags.remove(Tags.mounted)) {
                if (tags.contains(Tags.player)) {
                    tags.remove(Tags.angryhorse);
                    Gob drived;
                    if ((drived = NUtils.getGob(NUtils.getGameUI().drives)) != null)
                        if (drived.tags.contains(Tags.horse))
                            NUtils.setSpeed(NConfiguration.getInstance().playerSpeed);
                    NUtils.getGameUI().drives = -1;
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
            }
        }

        if (prev instanceof TreeScale) {
            removeTag(Tags.growth);
        }
        if (a instanceof TreeScale) {
            addTag(Tags.growth);
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
            }
            if (a instanceof LinMove || a instanceof Homing) {
                ((NOCache) NUtils.getGameUI().map.glob.oc).paths.addPath((Moving) a);
            }
            if (NUtils.getGameUI() != null && (me || ((Gob) this).id == NUtils.getGameUI().drives))
                NUtils.getGameUI().pathQueue().ifPresent(pathQueue -> pathQueue.movementChange((Gob) this, prev, a));
        }
    }

    protected void installFollowing(Gob gob, Following follow) {
        if (follow.tgt() != null) {
            if (follow.tgt().isTag(Tags.transport)) {
                if (gob.isTag(Tags.player) || (NUtils.getGameUI().map != null && gob.id == NUtils.getGameUI().map.player().id)) {
                    NUtils.getGameUI().drives = follow.tgt;
                }
                gob.tags.add(Tags.mounted);
            }
        }
    }


    public static long setDrawAttribute(Gob gob) {
        ResDrawable rd = gob.getattr(ResDrawable.class);
        if (rd == null) {
            return -1;
        }
        long res = calcMarker(rd.sdt);
        if (res != gob.modelAttribute || res == -1)
            gob.status = Status.ready_for_update;
        updateCustom(gob);
        return res;
    }

    public static long getModelAttribute(Gob gob) {
        return gob.modelAttribute;
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
        synchronized (((Gob) this).attr) {
            Map<Class<? extends GAttrib>, GAttrib> attr = ((Gob) this).attr;
            for (GAttrib a : attr.values()) {
                if (a instanceof Moving) {
                    updateMovingInfo(null, a);
                }
            }
        }
    }

    public static void updateMarked() {
        if (NUtils.getGameUI() != null) {
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

    public NProperties getProperties(Class<? extends NProperties> prop) {
        for (NProperties item : properties) {
            if (item.getClass() == prop)
                return (item);
        }
        return null;
    }


    private static boolean findMode(List<Composited.MD> mods, NAlias name) {
        if (mods != null)
            for (Composited.MD mod : mods) {
                for (ResData tex : mod.tex)
                    if (NUtils.isIt(tex.res, name))
                        return true;
            }
        return false;
    }

    private static boolean findModeLay(Collection<Composited.Model> mods, NAlias name) {
        for (Composited.Model mod : mods) {
            for (Composited.Model.Layer lay : mod.lay)
                if (NUtils.checkName(lay.mat.toString(), name))
                    return true;
        }
        return false;
    }

    public static void updateMods(Gob gob, List<Composited.MD> mods) {
        if (gob.isReady) {
            if (gob.isTag(Tags.kritter)) {
                if (gob.isTag(Tags.sheep) || gob.isTag(Tags.goat)) {
                    if (findMode(mods, new NAlias(new ArrayList<>(Collections.singletonList("fleece")), new ArrayList<>(Collections.singletonList("mouflon"))))) {
                        gob.addTag(Tags.wool);
                    } else {
                        gob.removeTag(Tags.wool);
                    }
                    if (gob.isTag(Tags.sheep)) {
                        if (findMode(mods, new NAlias("fleece")) && findMode(mods, new NAlias("mouflon"))) {
                            gob.addTag(Tags.wild);
                        }
                    } else if (gob.isTag(Tags.cow)) {
                        if (findMode(mods, new NAlias("aurochs-fleece"))) {
                            gob.addTag(Tags.wild);
                        }
                    }
                }
            }
        }
    }

    public static void updateLays(Gob gob) {
        if (gob.isReady) {
            Composite comp = gob.getattr(Composite.class);
            if (comp != null) {
                for (Composited.Model mod : comp.comp.mod) {
                    if (mod != null) {
                        for (Composited.Model.Layer lay : mod.lay)
                            if (NUtils.checkName(lay.mat.toString(), new NAlias(new ArrayList<>(Arrays.asList("winter")))))
                                gob.addTag(Tags.winter_stoat);
                    }
                }
            }
        }
    }

    public static void updatePoses(Gob gob, Collection<ResData> tposes) {
        if (gob.isReady) {
            if (gob.isTag(Tags.kritter)) {
                if (tposes != null) {
                    for (ResData pose : tposes)
                        if (NUtils.isIt(pose, "dead", "knock", "rigormortis", "drowned")) {
                            gob.addTag(Tags.knocked);
                        }
                        else
                        {
                            gob.addTag(Tags.kritter_is_ready);
                        }
                }
            }
        }
    }

    public static void updateOverlays(Gob gob) {
        if (gob.isReady) {

        }
    }

    protected boolean isReady = false;

    public static void updateRes(Gob gob) {
        if (gob.getres() != null && NUtils.getGameUI()!=null) {
            gob.isReady = true;
            String name = gob.getResName();
            if (NUtils.checkName(name, new NAlias(new ArrayList<>(Arrays.asList("gfx/terobjs/tree")), new ArrayList<>(Arrays.asList("log"))))) {
                gob.addTag(Tags.tree);
            } else if (NUtils.checkName(name, "gfx/terobjs/bumling")) {
                gob.addTag(Tags.bumling);
            } else if (NUtils.checkName(name, "gfx/terobjs/bushes")) {
                gob.addTag(Tags.bush);
            }
            else if (NUtils.checkName(name, "gfx/terobjs/looserock")) {
                gob.addTag(Tags.looserock);
            }
            else if (NUtils.checkName(name, new NAlias(new ArrayList<>(Arrays.asList("kritter")),new ArrayList<>(Arrays.asList("beef", "skeleton"))))) {
                gob.addTag(Tags.kritter);
                if (NUtils.checkName(name, "greyseal"))
                    gob.addTag(Tags.greyseal);
                else if (NUtils.checkName(name, "bear"))
                    gob.addTag(Tags.bear);
                else if (NUtils.checkName(name, "stalagoomba"))
                    gob.addTag(Tags.stalagoomba);
                else if (NUtils.checkName(name, "wolf"))
                    gob.addTag(Tags.wolf);
                else if (NUtils.checkName(name, "mammoth"))
                    gob.addTag(Tags.mammoth);
                else if (NUtils.checkName(name, "orca"))
                    gob.addTag(Tags.orca);
                else if (NUtils.checkName(name, "spermwhale"))
                    gob.addTag(Tags.spermwhale);
                else if (NUtils.checkName(name, "troll"))
                    gob.addTag(Tags.troll);
                else if (NUtils.checkName(name, "stoat"))
                    gob.addTag(Tags.stoat);
                if (NUtils.checkName(name, "horse")) {
                    gob.addTag(Tags.horse);
                    if (!NUtils.checkName(name, "foal"))
                        gob.addTag(Tags.transport);
                    if (NUtils.checkName(name, "horse/horse")) {
                        gob.addTag(Tags.wild);
                    }
                } else if (NUtils.checkName(name, "pig"))
                    gob.addTag(Tags.pig);
                else if (NUtils.checkName(name, "cattle"))
                    gob.addTag(Tags.cow);
                else if (NUtils.checkName(name, "goat")) {
                    gob.addTag(Tags.goat);
                    if (NUtils.checkName(name, "wild")) {
                        gob.addTag(Tags.wild);
                    }
                } else if (NUtils.checkName(name, "sheep"))
                    gob.addTag(Tags.sheep);
            } else if (NUtils.checkName(name, "borka")) {
                gob.addTag(Tags.borka);
            }
            else if (NUtils.checkName(name, "barterarea")) {
                gob.addTag(Tags.barterarea);
            }
            else if (NUtils.checkName(name, "items")) {
                gob.addTag(Tags.item);
                if (NUtils.checkName(name, "gem")) {
                    gob.addTag(Tags.gem);
                } else if (NUtils.checkName(name, "truffle")) {
                    gob.addTag(Tags.truffle);
                }
            } else if (NUtils.checkName(name, new NAlias(new ArrayList<>(Arrays.asList("plants")), new ArrayList<>(Arrays.asList("trellis"))))) {
                gob.addTag(Tags.plant);
                int cropstgmaxval = 0;
                for (FastMesh.MeshRes layer : gob.getres().layers(FastMesh.MeshRes.class)) {
                    int stg = layer.id / 10;
                    if (stg > cropstgmaxval) {
                        cropstgmaxval = stg;
                    }
                }
                if (NUtils.checkName(name, "turnip"))
                    gob.properties.add(new NProperties.Crop(1, cropstgmaxval));
                else if (NUtils.checkName(name, "carrot"))
                    gob.properties.add(new NProperties.Crop(3, cropstgmaxval));
                else if (NUtils.checkName(name, "hemp"))
                    gob.properties.add(new NProperties.Crop(cropstgmaxval - 1, cropstgmaxval));
                else
                    gob.properties.add(new NProperties.Crop(-1, cropstgmaxval));
            } else if (NUtils.checkName(name, "vehicle")) {
                gob.addTag(Tags.vehicle);
                if (NUtils.checkName(name, "rowboat", "dugout", "snekkja", "knarr")) {
                    gob.addTag(Tags.transport);
                }
            } else if (NUtils.checkName(name, "gfx/terobjs/beehive", "trough", "barterhand")) {
                gob.addTag(Tags.area);
                if (NUtils.checkName(name, "trough")) {
                    gob.addTag(Tags.trough);
                } else if (NUtils.checkName(name, "beehive")) {
                    gob.addTag(Tags.beeskep);
                } else if (NUtils.checkName(name, "barterhand")) {
                    gob.addTag(Tags.barterhand);
                }
            } else if (NUtils.checkName(name, "cellar")) {
                gob.addTag(Tags.cellar);
            } else if (NUtils.checkName(name, "moundbed")) {
                    gob.addTag(Tags.moundbed);
            } else if (NUtils.checkName(name, "gate")) {
                gob.addTag(Tags.gate);
            } else if (NUtils.checkName(name, "pow")) {
                gob.addTag(Tags.pow);
            } else if (NUtils.checkName(name, "gardenpot")) {
                gob.addTag(Tags.gardenpot);
            } else if (NUtils.checkName(name, "iconsign")) {
                gob.addTag(Tags.iconsign);
            }
            if (NUtils.checkName(name, "table", "cupboard", "chest", "crate", "metalcabinet", "boiler", "casket", "ttub", "dframe", "cheeserack")) {
                gob.addTag(Tags.container);
                if (NUtils.checkName(name, "dframe"))
                    gob.addTag(Tags.dframe);
                else if (NUtils.checkName(name, "ttub"))
                    gob.addTag(Tags.ttub);
                else if (NUtils.checkName(name, "cheeserack"))
                    gob.addTag(Tags.cheeserack);
                else if (NUtils.checkName(name, "curdingtub", "cupboard", "chest", "crate", "metalcabinet", "casket")) {
                    gob.addTag(Tags.properties);
                    if (NUtils.checkName(name, "cupboard"))
                        gob.properties.add(new NProperties.Container("Cupboard", 3, 16));
                    else if (NUtils.checkName(name, "largechest"))
                        gob.properties.add(new NProperties.Container("Large Chest", 3, 16));
                    else if (NUtils.checkName(name, "chest"))
                        gob.properties.add(new NProperties.Container("Chest", 3, 28));
                    else if (NUtils.checkName(name, "metalcabinet"))
                        gob.properties.add(new NProperties.Container("Metal Cabinet", 3, 64));
                    else if (NUtils.checkName(name, "crate"))
                        gob.properties.add(new NProperties.Container("Crate", 0, 16));
                    else if (NUtils.checkName(name, "casket"))
                        gob.properties.add(new NProperties.Container("Stone Casket", 3, 16));
                }
            } else if (NUtils.checkName(name, "barrel")) {
                gob.addTag(Tags.barrel);
            } else if (NUtils.checkName(name, "chickencoop")) {
                gob.addTag(Tags.chickencoop);
            } else if (NUtils.checkName(name, "rabbithutch")) {
                gob.addTag(Tags.rabbithutch);
            }


            /// Special section
            if (NUtils.checkName(name, new NAlias(new ArrayList<String>(Arrays.asList("minebeam", "column", "towercap", "ladder", "minesupport")), new ArrayList<String>(Arrays.asList("stump", "wrack", "log"))))) {
                gob.addTag(Tags.minesupport);
            }

            if (!gob.isTag(Tags.item) && !gob.isTag(Tags.barterarea) && !gob.isTag(Tags.plant) && !gob.isTag(Tags.cellar) && !gob.isTag(Tags.moundbed) && !(gob.isTag(Tags.pow) && (gob.getModelAttribute() & 17) == 17) && !(gob.isTag(Tags.kritter) && NUtils.checkName(name, "cavemoth"))) {
                gob.hitBox = NHitBox.get(name);
            }

            updateOverlays(gob);
            Composite comp = gob.getattr(Composite.class);
            if (comp != null) {
                updatePoses(gob, comp.oldposes);
                updateMods(gob, comp.oldnmod);
                updateLays(gob);
            } else {
                gob.modelAttribute = setDrawAttribute(gob);
            }
        }
    }


    protected static void updateCustom(Gob gob) {
        if (gob.status != Status.updated) {
            if (gob.status == Status.undefined) {
                updateRes(gob);
            }
            if (gob.getattr(GobIcon.class) == null) {
                GobIcon icon = NUtils.getIcon(gob);
                if (icon != null) {
                    icon.img();
                    gob.setattr(icon);
                }
            }
            if (gob.status == Status.ready_for_update) {
                NModelBox modelBox = NModelBox.forGob(gob);
                if (modelBox != null && gob.findol(NModelBox.class) == null) {
                    gob.addcustomol(modelBox);
                }
                if (NUtils.getGameUI() != null && NUtils.getGameUI().map != null) {
                    if (gob.isTag(Tags.borka)) {
                        if (NUtils.getGameUI().map.player() != null) {
                            if (gob.id == NUtils.getGameUI().map.player().id) {

                                Following following;
                                if ((following = gob.getattr(Following.class)) != null) {
                                    if (following.tgt() != null) {
                                        gob.removeTag(Tags.borka);
                                        gob.addTag(Tags.player);
                                        if (NUtils.getGameUI().drives != -1) {
                                            gob.installFollowing(gob, following);
                                        } else {
                                            gob.installFollowing(gob, following);
                                            if (NUtils.isIt(NUtils.getGob(NUtils.getGameUI().drives), "horse"))
                                                NUtils.setSpeed(NConfiguration.getInstance().horseSpeed);
                                        }
                                    }
                                } else {
                                    gob.removeTag(Tags.borka);
                                    gob.addTag(Tags.player);
                                }
                                gob.addcustomol(new NPlayerArrow(gob, 10));
                            } else {
                                gob.removeTag(Tags.borka);
                                gob.addTag(Tags.notplayer);
                            }
                        }
                    }
                    if (gob.isTag(Tags.notplayer)) {
                        if (!gob.isTag(Tags.knocked)) {
                            gob.addcustomol(new NPlayerMarker(gob, 10));
                        }
                    }
                } else if (gob.isTag(Tags.borka)) {
                    return;
                }

                if (NConfiguration.getInstance().enablePfBoundingBoxes) {
                    if (gob.getHitBox() != null && gob.findol(NGobHiteBoxSpr.class) == null) {
                        gob.addcustomol(new NGobHiteBoxSpr(NBoundingBox.getBoundingBox(gob)));
                    }
                }
                if (gob.status == Status.ready_for_update) {
                    if (gob.isTag(Tags.quality)) {
                        if (gob.quality != -1) {
                            gob.addcustomol(new NObjectTexLabel(gob, String.format("%d", gob.quality), Color.WHITE, "quality", true));
                        }
                    }
                    if (gob.isTag(Tags.tree) || gob.isTag(Tags.bumling) || gob.isTag(Tags.quester)) {
                        NQuestInfo.QuestGob qg;
                           if (NUtils.getGameUI()!= null && NUtils.getGameUI().questInfo!= null && (qg = NUtils.getGameUI().questInfo.getMark(gob))!=null) {
                                gob.addTag(Tags.quester);
                                gob.addcustomol(new NQuesterRing(gob, Color.ORANGE, 20, 0.7f, qg));
                            }

                    }
                    if (gob.isTag(Tags.highlighted)) {
                        gob.addcustomol(new NHighlightRing(gob));
                        gob.addcustomattr(new NGobHighlight(gob));
                    } else if ((gob.isTag(Tags.quest) && !gob.isTag(Tags.knocked)) /*|| (gob.getResName()!=null && gob.getResName().contains("woodensign"))*/) {
                        NAlarmManager.play(Tags.quest);
                        gob.addcustomol(new NNotifiedRing(gob, Color.CYAN, 30, 0.7f, gob.noteImg));
                    } else if (gob.isTag(Tags.notified)) {
                        gob.addcustomol(new NNotifiedRing(gob, Color.GREEN, 20, 0.7f, gob.noteImg));
                    }
                    if (gob.isTag(Tags.looserock))
                    {
                        NAlarmManager.play(Tags.looserock);
                        gob.addcustomol(new NAreaRad(gob,  (float) 93.5f, new Color(192, 68, 0, 128), new Color(128, 88, 88, 255)));
                    }
                    if (gob.isTag(Tags.plant)) {
                        NProperties.Crop crop = gob.getCrop();
                        crop.currentStage = gob.modelAttribute;
                        gob.addcustomol(new NCropMarker(gob, crop));
                    } else if (gob.isTag(Tags.item)) {
                        if (gob.isTag(Tags.gem)) {
                            gob.addcustomol(new NTexMarker(gob, 5, Tags.gem));
                        } else if (gob.isTag(Tags.truffle)) {
                            gob.addcustomol(new NTexMarker(gob, 10, Tags.truffle));
                        }
                    } else if (gob.isTag(Tags.container, Tags.properties)) {
                        NProperties.Container cont = gob.getContainer();
                        if (cont != null) {
                            gob.addcustomattr(new NContainerColor(gob, cont));
                        }
                    } else if (gob.isTag(Tags.barrel)) {
                        gob.addcustomattr(new NBarrelColor(gob));
                    } else if (gob.isTag(Tags.minesupport)) {
                        if (NConfiguration.getInstance().rings.get("minesup").isEnable && gob.findol(NAreaRad.class) == null) {
                            if (NUtils.checkName(gob.getResName(), new NAlias(new ArrayList<>(Arrays.asList("natural"))))) {
                                gob.addcustomol(new NAreaRange(gob, "minesup", 93.5f, new Color(128, 128, 128, 128), new Color(0, 192, 192, 255)));
                            } else if (NUtils.checkName(gob.getResName(), new NAlias(new ArrayList<>(Arrays.asList("ladder", "minesupport", "towercap"))))) {
                                if (gob.isTag(Tags.growth)) {
                                    TreeScale ts = gob.getattr(TreeScale.class);
                                    int scale = (int) Math.round(100 * (ts.scale - 0.1) / 0.9);
                                    gob.addcustomol(new NAreaRange(gob, "minesup", scale, new Color(128, 128, 128, 128), new Color(0, 192, 192, 255)));
                                } else {
                                    gob.addcustomol(new NAreaRange(gob, "minesup", 100, new Color(128, 128, 128, 128), new Color(0, 192, 192, 255)));
                                }
                            } else if (NUtils.checkName(gob.getResName(), new NAlias("minebeam"))) {
                                gob.addcustomol(new NAreaRange(gob, "minesup", 150, new Color(128, 128, 128, 128), new Color(0, 192, 192, 255)));
                            } else if (NUtils.checkName(gob.getResName(), new NAlias("column"))) {
                                gob.addcustomol(new NAreaRange(gob, "minesup", 125, new Color(128, 128, 128, 128), new Color(0, 192, 192, 255)));
                            }
                        }
                        if (gob.findol(NOverlayMap.class) == null) {
                            if (NUtils.checkName(gob.getResName(), new NAlias(new ArrayList<>(Arrays.asList("natural"))))) {
                                gob.addcustomol(new NOverlayMap(gob, "minesup", 93.5f));
                            } else if (NUtils.checkName(gob.getResName(), new NAlias(new ArrayList<>(Arrays.asList("ladder", "minesupport", "towercap"))))) {
                                if (gob.isTag(Tags.growth)) {
                                    TreeScale ts = gob.getattr(TreeScale.class);
                                    int scale = (int) Math.round(100 * (ts.scale - 0.1) / 0.9);
                                    gob.addcustomol(new NOverlayMap(gob, "minesup", scale));
                                } else {
                                    gob.addcustomol(new NOverlayMap(gob, "minesup", 100));
                                }
                            } else if (NUtils.checkName(gob.getResName(), new NAlias("minebeam"))) {
                                gob.addcustomol(new NOverlayMap(gob, "minesup", 150));
                            } else if (NUtils.checkName(gob.getResName(), new NAlias("column"))) {
                                gob.addcustomol(new NOverlayMap(gob, "minesup", 125));
                            }
                        }

                    } else if (gob.isTag(Tags.dframe)) {
                        gob.addcustomattr(new NDframeColor(gob));
                    } else if (gob.isTag(Tags.ttub)) {
                        gob.addcustomattr(new NTubColor(gob));
                    }
                    else if (gob.isTag(Tags.gardenpot)) {
                        gob.addcustomol(new NGardenPotMarker(gob));
                    }
                    else if (gob.isTag(Tags.chickencoop) || gob.isTag(Tags.rabbithutch)) {
                        gob.addcustomattr(new NIncubatorColor(gob));
                    } else if (gob.isTag(Tags.cheeserack)) {
                        gob.addcustomattr(new NCheeseColor(gob));
                    } else if (gob.isTag(Tags.barterhand)) {
                        gob.addcustomol(new NAreaRange(gob, "barterhand", (float) NConfiguration.getInstance().rings.get("barterhand").size, new Color(163, 0, 192, 128), new Color(0, 0, 192, 255)));
                    } else if (gob.isTag(Tags.trough)) {
                        gob.addcustomol(new NAreaRange(gob, "trough", (float) NConfiguration.getInstance().rings.get("trough").size, new Color(192, 192, 0, 128), new Color(0, 164, 192, 255)));
                        gob.addcustomattr(new NTroughColor(gob));
                    } else if (gob.isTag(Tags.beeskep)) {
                        gob.addcustomol(new NAreaRange(gob, "beeskep", (float) NConfiguration.getInstance().rings.get("beeskep").size, new Color(0, 163, 192, 128), new Color(0, 192, 0, 255)));
                        gob.addcustomol(new NBeeMarker(gob));
                    } else if (gob.isTag(Tags.kritter)) {
                        if (gob.isTag(Tags.selected))
                            gob.addcustomol(new NDomesticRing(gob,Color.GREEN,7, 1.f));
                        if (gob.isTag(Tags.sheep) || gob.isTag(Tags.goat))
                            gob.addcustomol(new NWoolMarker(gob, 12));
                        if (gob.isTag(Tags.stalagoomba))
                            NAlarmManager.play(Tags.stalagoomba);

                        if (gob.getattr(Composite.class) != null && gob.getpose() != null && !gob.isTag(Tags.knocked) && gob.isTag(Tags.kritter_is_ready)) {
                            for (String ring : NConfiguration.getInstance().rings.keySet()) {
                                if (gob.getResName().contains(ring)) {
                                    NConfiguration.Ring ringprop = NConfiguration.getInstance().rings.get(ring);
                                    gob.addcustomol(new NKritterRange(gob, ringprop, new Color(192, 0, 0, 128), new Color(0, 164, 192, 255)));
                                }
                            }
                            if (gob.isTag(Tags.bear))
                                NAlarmManager.play(Tags.bear);
                            else if (gob.isTag(Tags.greyseal))
                                NAlarmManager.play(Tags.greyseal);
                            else if (gob.isTag(Tags.wolf))
                                NAlarmManager.play(Tags.wolf);
                            else if (gob.isTag(Tags.winter_stoat))
                                NAlarmManager.play(Tags.winter_stoat);
                            else if (gob.isTag(Tags.mammoth))
                                NAlarmManager.play(Tags.mammoth);
                            else if (gob.isTag(Tags.orca))
                                NAlarmManager.play(Tags.orca);
                            else if (gob.isTag(Tags.spermwhale))
                                NAlarmManager.play(Tags.spermwhale);
                            else if (gob.isTag(Tags.troll))
                                NAlarmManager.play(Tags.troll);
                        }

                    } else if (gob.isTag(Tags.angryhorse)) {
                        gob.addcustomol(new NTexMarker(gob, 20, Tags.angryhorse));
                    }
                }

                if (gob.isTag(Tags.growth)) {
                    double scale = 0;
                    TreeScale ts = gob.getattr(TreeScale.class);
                    if (gob.isTag(Tags.tree)) {
                        scale = Math.round(100 * (ts.scale - 0.1) / 0.9);
                    } else if (gob.isTag(Tags.bush)) {
                        scale = Math.round(100 * (ts.scale - 0.3) / 0.7);
                    }
                    gob.addcustomol(new NObjectTexLabel(gob, String.format("%.0f %%", (float) scale), Color.WHITE, "growth"));
                }

                if (gob.isTag(Tags.iconsign)) {
                    if (NConfiguration.getInstance().showAreas) {
                        if (NUtils.getGameUI() != null && NUtils.getGameUI().updated()) {
                            for (String name : made_id.keySet()) {
                                if (made_id.get(name) == gob.modelAttribute)
                                    try {
                                        NOCache.constructOverlay(AreasID.find(name));
                                    } catch (IllegalArgumentException e) {
//                                    e.printStackTrace();
                                    } catch (Resource.Loading | MCache.LoadingMap e) {
                                        gob.status = Status.ready_for_update;
                                    }
                            }
                        } else {
                            gob.status = Status.ready_for_update;
                            return;
                        }
                    }
                }

                gob.status = Status.updated;
            }

        }
    }

}

