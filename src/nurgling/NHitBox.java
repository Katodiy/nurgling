package nurgling;

import haven.*;
//import haven.res.gfx.terobjs.consobj.Consobj;
import haven.res.gfx.terobjs.consobj.Consobj;
import nurgling.tools.NArea;

import java.util.HashMap;

public class NHitBox extends NArea {
    public String toString() {
        return  ("Sizes:" + coord2ds[0].toString() + "|" + coord2ds[2].toString() + " Pos: " + center.toString());
    }

    public static NHitBox _default = new NHitBox ( new Coord2d( -5.5, -5.5 ), new Coord2d ( 5.5, 5.5 ) );;
    public static NHitBox _dummy = new NHitBox ( new Coord2d( -1, -1 ), new Coord2d ( 1, 1 ) );;
    public Coord2d rotBeg;
    
    public NHitBox(
            Coord2d begin,
            Coord2d end
    ) {
        super ( begin, end );
    }
    
    public NHitBox(
            NHitBox nHitBox,
            double delta
    ) {
        super ( new Coord2d ( nHitBox.begin.x - delta, nHitBox.begin.y - delta ),
                new Coord2d ( nHitBox.end.x + delta, nHitBox.end.y + delta ) );
    }
    
    public NHitBox(NHitBox hitBox ) {
        super ( hitBox.begin, hitBox.end );
        center = hitBox.center;
        orientation = hitBox.orientation;
    }

    public static NHitBox get (
            String name
    ) {
        NHitBox res;
        if((res= hitboxes.get(name)) ==null)
            return getByName(name);
        else
            return res;
    }

    public static NHitBox getDummy (
            Coord2d rc
    ) {
        NHitBox res = new NHitBox(_dummy);
        res.correct(rc,0);
        return res;
    }

    public static NHitBox get (
    ) {
        return new NHitBox(_default);
    }


    public static NHitBox get (
            Gob gob,
            boolean trellis
    ) {
        NHitBox result = null;
        if (gob.isTag(NGob.Tags.gate)) {
            if (gob.getModelAttribute() == 1) {
                return null;
            }
            else
            {
                result = gob.getHitBox();
            }
        } else if (trellis) {
            if (gob.isTag(NGob.Tags.trellis)) {
                result = getByName("ptrellis");
                result.correct(NUtils.getTrellisCoord(gob.rc), gob.a);
                return result;
            }
        } else if (gob.isTag(NGob.Tags.consobj)) {
            Consobj co = (Consobj) (((ResDrawable) gob.attr.get(Drawable.class)).spr);
            result = NHitBox.get(co.built.res.get().name);
            result.correct(gob.rc, gob.a);
        } else {
            result = gob.getHitBox();
        }

        return result;
    }


    public static HashMap<String, NHitBox> hitboxes = new HashMap<>();

    public static void init() {
        hitboxes.put("gfx/terobjs/stockpile-branch", new NHitBox(new Coord2d(-3.4375, -3.4375), new Coord2d(3.4375, 3.4375)));
        hitboxes.put("gfx/terobjs/barrel", new NHitBox(new Coord2d(-4.125, -4.125), new Coord2d(4.125, 4.125)));
        hitboxes.put("gfx/terobjs/churn", new NHitBox(new Coord2d(-4.125, -4.125), new Coord2d(4.125, 4.125)));
        hitboxes.put("gfx/borka/body", new NHitBox(new Coord2d(-4.125, -4.125), new Coord2d(4.125, 4.125)));
        hitboxes.put("gfx/terobjs/arch/logcabin", new NHitBox(new Coord2d(-26, -26), new Coord2d(26, 26)));
        hitboxes.put("gfx/terobjs/arch/stonemansion-door", new NHitBox(new Coord2d(-2.75, -8.25), new Coord2d(2.75, 8.25)));
        hitboxes.put("gfx/terobjs/arch/greathall-door", new NHitBox(new Coord2d(-2.75, -8.25), new Coord2d(2.75, 8.25)));
        hitboxes.put("gfx/terobjs/arch/stonestead-door", new NHitBox(new Coord2d(-2.75, -8.25), new Coord2d(2.75, 8.25)));
        hitboxes.put("gfx/terobjs/hitchingpost", new NHitBox(new Coord2d(-2.75, -8.25), new Coord2d(2.75, 8.25)));
        hitboxes.put("gfx/terobjs/arch/windmill-door", new NHitBox(new Coord2d(-2.75, -8.25), new Coord2d(2.75, 8.25)));
        hitboxes.put("gfx/terobjs/arch/stonetower-door", new NHitBox(new Coord2d(-2.75, -8.25), new Coord2d(2.75, 8.25)));
        hitboxes.put("gfx/terobjs/plants/trellis", new NHitBox(new Coord2d(-1.375, -5.5), new Coord2d(1.375, 5.5)));
        hitboxes.put("gfx/terobjs/arch/hwall", new NHitBox(new Coord2d(-0.02, -5.5), new Coord2d(0.02, 5.5)));
        hitboxes.put("gfx/terobjs/vehicle/dugout", new NHitBox(new Coord2d(-11, -2.75), new Coord2d(11, 2.75)));
        hitboxes.put("gfx/terobjs/trees/oldtrunk", new NHitBox(new Coord2d(-11, -2.75), new Coord2d(11, 2.75)));
        ///TODO:dreca
        hitboxes.put("gfx/terobjs/furn/boughbed", new NHitBox(new Coord2d(-11, -5.5), new Coord2d(11, 5.5)));
        hitboxes.put("gfx/terobjs/htable", new NHitBox(new Coord2d(-5.5, -8.25), new Coord2d(5.5, 8.25)));
        hitboxes.put("gfx/kritter/cattle/calf", new NHitBox(new Coord2d(-9.625, -5.5), new Coord2d(9.625, 5.5)));
        hitboxes.put("gfx/kritter/horse/foal", new NHitBox(new Coord2d(-8.25, -5.5), new Coord2d(8.25, 5.5)));
        hitboxes.put("gfx/kritter/pig/piglet", new NHitBox(new Coord2d(-8.25, -5.5), new Coord2d(8.25, 5.5)));
        hitboxes.put("gfx/kritter/pig/sow", new NHitBox(new Coord2d(-8.25, -5.5), new Coord2d(8.25, 5.5)));
        hitboxes.put("gfx/kritter/pig/hog", new NHitBox(new Coord2d(-8.25, -5.5), new Coord2d(8.25, 5.5)));
        hitboxes.put("gfx/terobjs/stockpile-board", new NHitBox(new Coord2d(-11, -11), new Coord2d(11, 11)));
        hitboxes.put("gfx/terobjs/stockpile-soil", new NHitBox(new Coord2d(-11, -11), new Coord2d(11, 11)));
        hitboxes.put("gfx/terobjs/stockpile-pumpkin", new NHitBox(new Coord2d(-11, -11), new Coord2d(11, 11)));
        hitboxes.put("gfx/terobjs/stockpile-metal", new NHitBox(new Coord2d(-5.5, -8.25), new Coord2d(5.5, 8.25)));
        hitboxes.put("gfx/terobjs/stockpile-straw", new NHitBox(new Coord2d(-8.25, -8.25), new Coord2d(8.25, 8.25)));
        hitboxes.put("gfx/terobjs/stockpile-brick", new NHitBox(new Coord2d(-11, -5.5), new Coord2d(11, 5.5)));
        hitboxes.put("gfx/terobjs/stockpile-leaf", new NHitBox(new Coord2d(-8.25, -8.25), new Coord2d(8.25, 8.25)));
        hitboxes.put("gfx/terobjs/ttub", new NHitBox(new Coord2d(-5.5, -5.5), new Coord2d(5.5, 5.5)));
        hitboxes.put("gfx/terobjs/cupboard", new NHitBox(new Coord2d(-5.5, -5.5), new Coord2d(5.5, 5.5)));
        hitboxes.put("gfx/terobjs/vehicle/cart", new NHitBox(new Coord2d(-8.25, -6.875), new Coord2d(8.25, 6.875)));
        hitboxes.put("gfx/terobjs/steelcrucible", new NHitBox(new Coord2d(-4.75, -5.5), new Coord2d(4.75, 5.5)));
        hitboxes.put("gfx/terobjs/crucible", new NHitBox(new Coord2d(-2.75, -2.75), new Coord2d(2.75, 2.75)));
        hitboxes.put("gfx/terobjs/candelabrum", new NHitBox(new Coord2d(-2.75, -2.75), new Coord2d(2.75, 2.75)));
        hitboxes.put("gfx/terobjs/arch/downstairs", new NHitBox(new Coord2d(-11, -5.5), new Coord2d(11, 5.5)));
        hitboxes.put("gfx/terobjs/trough", new NHitBox(new Coord2d(-5.5, -12.375), new Coord2d(5.5, 12.375)));
        hitboxes.put("gfx/terobjs/vehicle/rowboat", new NHitBox(new Coord2d(-21, -8.25), new Coord2d(21, 8.25)));
        hitboxes.put("gfx/terobjs/claypit", new NHitBox(  new Coord2d ( -27.5, -27.5 ), new Coord2d ( 27.5, 27.5 )  ));
        hitboxes.put("gfx/terobjs/geyser", new NHitBox(  new Coord2d ( -27.5, -27.5 ), new Coord2d ( 27.5, 27.5 )  ));
        hitboxes.put("gfx/terobjs/woodheart", new NHitBox(  new Coord2d ( -27.5, -27.5 ), new Coord2d ( 27.5, 27.5 )  ));
        hitboxes.put("gfx/terobjs/saltbasin", new NHitBox(  new Coord2d ( -27.5, -27.5 ), new Coord2d ( 27.5, 27.5 )  ));
        hitboxes.put("gfx/terobjs/tarkiln", new NHitBox( new Coord2d ( -25, -25 ), new Coord2d ( 25, 25 )   ));
        hitboxes.put("gfx/terobjs/primsmelter", new NHitBox( new Coord2d ( -11, -8.25 ), new Coord2d ( 11, 8.25 )  ));
        hitboxes.put("gfx/terobjs/smelter", new NHitBox(  new Coord2d ( -12.375, -22 ), new Coord2d ( 12.375, 22 )  ));
        hitboxes.put("gfx/terobjs/minehole", new NHitBox(  new Coord2d ( -12.375, -12.375 ), new Coord2d ( 12.375, 12.375 ) ));
        hitboxes.put("gfx/terobjs/trees/stonepine", new NHitBox(  new Coord2d ( -8.25, -8.25 ), new Coord2d ( 8.25, 8.25 )));
        hitboxes.put("gfx/terobjs/trees/blackpine", new NHitBox(  new Coord2d ( -8.25, -8.25 ), new Coord2d ( 8.25, 8.25 )));
        hitboxes.put("gfx/terobjs/trees/carobtree", new NHitBox(  new Coord2d ( -8.25, -8.25 ), new Coord2d ( 8.25, 8.25 )));
        hitboxes.put("gfx/terobjs/trees/strawberrytree", new NHitBox(  new Coord2d ( -8.25, -8.25 ), new Coord2d ( 8.25, 8.25 )));
        hitboxes.put("gfx/terobjs/trees/alder", new NHitBox(  new Coord2d ( -8.25, -8.25 ), new Coord2d ( 8.25, 8.25 )));
        hitboxes.put("gfx/terobjs/trees/birch", new NHitBox(  new Coord2d ( -8.25, -8.25 ), new Coord2d ( 8.25, 8.25 )));
        hitboxes.put("gfx/terobjs/trees/spruce", new NHitBox(  new Coord2d ( -8.25, -8.25 ), new Coord2d ( 8.25, 8.25 )));
        hitboxes.put("gfx/terobjs/trees/sycamore", new NHitBox(  new Coord2d ( -8.25, -8.25 ), new Coord2d ( 8.25, 8.25 )));
        ///TODO:wagon
        hitboxes.put("gfx/terobjs/arch/timberhouse", new NHitBox( new Coord2d ( -33, -33 ), new Coord2d ( 33, 33 ) ));
        hitboxes.put("gfx/terobjs/ropewalk", new NHitBox(  new Coord2d ( -22, -8.25 ), new Coord2d ( 22, 8.25 )  ));
        hitboxes.put("gfx/terobjs/loom", new NHitBox(   new Coord2d ( -8.25, -8.25 ), new Coord2d ( 8.25, 8.25 )    ));
        hitboxes.put("gfx/terobjs/kiln", new NHitBox(    new Coord2d ( -13, -13 ), new Coord2d ( 13, 13 )    ));
        hitboxes.put("gfx/terobjs/arch/palisadebiggate", new NHitBox(   new Coord2d ( -5.5, -12.375 ), new Coord2d ( 5.5, 12.375 )   ));
        hitboxes.put("gfx/terobjs/arch/polebiggate", new NHitBox(   new Coord2d ( -5.5, -12.375 ), new Coord2d ( 5.5, 12.375 )   ));
        hitboxes.put("gfx/terobjs/stonepillar", new NHitBox(  new Coord2d ( -13, -13 ), new Coord2d ( 13, 13 )   ));
        hitboxes.put("gfx/terobjs/leanto", new NHitBox(  new Coord2d ( -13, -13 ), new Coord2d ( 13, 13 )   ));
        hitboxes.put("gfx/terobjs/crate", new NHitBox( new Coord2d ( -5.5, -8.25 ), new Coord2d ( 5.5, 8.25 )  ));
        hitboxes.put("gfx/terobjs/dframe", new NHitBox( new Coord2d ( -3.4375, -11 ), new Coord2d ( 3.4375, 11 )   ));
        hitboxes.put("gfx/terobjs/arch/stonemansion", new NHitBox( new Coord2d ( -49.5, -49.5 ), new Coord2d ( 49.5, 49.5 )   ));
        hitboxes.put("gfx/terobjs/arch/greathall", new NHitBox( new Coord2d ( -79.75, -55 ), new Coord2d ( 79.75, 55 )  ));
        hitboxes.put("gfx/terobjs/oven", new NHitBox( new Coord2d ( -11, -11 ), new Coord2d ( 11, 11 )  ));
        hitboxes.put("gfx/terobjs/arch/stonetower", new NHitBox(  new Coord2d ( -38.75, -38.75 ), new Coord2d ( 38.75, 38.75 )  ));
        hitboxes.put("gfx/terobjs/arch/windmill", new NHitBox(  new Coord2d ( -27.5, -27.5 ), new Coord2d ( 27.5, 27.5 ) ));
        hitboxes.put("gfx/terobjs/villageidol", new NHitBox( new Coord2d ( -11, -16.5 ), new Coord2d ( 11, 16.5 )  ));
        hitboxes.put("gfx/terobjs/iconsign", new NHitBox(  new Coord2d ( -2.75, -2.75 ), new Coord2d ( 2.75, 2.75 )  ));
        hitboxes.put("gfx/terobjs/barterhand", new NHitBox(  new Coord2d ( -2.75, -2.75 ), new Coord2d ( 2.75, 2.75 )  ));
        hitboxes.put("gfx/terobjs/chest", new NHitBox(new Coord2d ( -4.125, -4.125 ), new Coord2d ( 4.125, 4.125 ) ));
        hitboxes.put("gfx/terobjs/metalcabinet", new NHitBox(new Coord2d ( -4.125, -4.125 ), new Coord2d ( 4.125, 4.125 ) ));
        hitboxes.put("gfx/kritter/reddeer", new NHitBox(new Coord2d ( -11, -5.5 ), new Coord2d ( 11, 5.5 ) ));
        hitboxes.put("gfx/kritter/boar", new NHitBox(new Coord2d ( -11, -5.5 ), new Coord2d ( 11, 5.5 ) ));
        hitboxes.put("gfx/kritter/horse", new NHitBox(new Coord2d ( -11, -5.5 ), new Coord2d ( 11, 5.5 ) ));
        hitboxes.put("gfx/kritter/reindeer", new NHitBox(new Coord2d ( -11, -5.5 ), new Coord2d ( 11, 5.5 ) ));
        hitboxes.put("gfx/kritter/wolf", new NHitBox(new Coord2d ( -11, -5.5 ), new Coord2d ( 11, 5.5 ) ));
        hitboxes.put("gfx/terobjs/barterstand", new NHitBox(new Coord2d ( -8.25, -11 ), new Coord2d ( 8.25, 11 ) ));
        hitboxes.put("gfx/kritter/moose", new NHitBox( new Coord2d ( -13.75, -8.25 ), new Coord2d ( 13.75, 8.25 ) ));
        hitboxes.put("gfx/kritter/walrus", new NHitBox( new Coord2d ( -13.75, -8.25 ), new Coord2d ( 13.75, 8.25 ) ));
        hitboxes.put("gfx/kritter/bear", new NHitBox( new Coord2d ( -13.75, -8.25 ), new Coord2d ( 13.75, 8.25 ) ));
        hitboxes.put("gfx/kritter/cattle/cattle", new NHitBox( new Coord2d ( -13.75, -8.25 ), new Coord2d ( 13.75, 8.25 ) ));
        hitboxes.put("gfx/kritter/caveangler", new NHitBox( new Coord2d ( -13.75, -8.25 ), new Coord2d ( 13.75, 8.25 ) ));
        hitboxes.put("gfx/kritter/beaverking", new NHitBox( new Coord2d ( -13.75, -8.25 ), new Coord2d ( 13.75, 8.25 ) ));
        hitboxes.put("gfx/terobjs/smokeshed", new NHitBox( new Coord2d ( -7.5625, -7.5625 ), new Coord2d ( 7.5625, 7.5625 ) ));
        hitboxes.put("gfx/terobjs/shed", new NHitBox(new Coord2d ( -11, -11 ), new Coord2d ( 11, 11 ) ));
        hitboxes.put("gfx/terobjs/chickencoop", new NHitBox(new Coord2d ( -11, -11 ), new Coord2d ( 11, 11 ) ));
        hitboxes.put("gfx/terobjs/rabbithutch", new NHitBox(new Coord2d ( -11, -11 ), new Coord2d ( 11, 11 ) ));
        hitboxes.put("gfx/kritter/greyseal", new NHitBox( new Coord2d ( -8.25, -5.5 ), new Coord2d ( 8.25, 5.5 )));
        hitboxes.put("gfx/terobjs/arch/stonestead", new NHitBox( new Coord2d ( -48.125, -27.5 ), new Coord2d ( 48.125, 27.5 )));
        hitboxes.put("gfx/terobjs/vehicle/wheelbarrow", new NHitBox( new Coord2d ( -8.25, -5.5 ), new Coord2d ( 8.25, 5.5 )));
        hitboxes.put("gfx/terobjs/vehicle/plow", new NHitBox( new Coord2d ( -8.25, -5.5 ), new Coord2d ( 8.25, 5.5 )));
        hitboxes.put("gfx/terobjs/cistern", new NHitBox(  new Coord2d ( -9.625, -9.625 ), new Coord2d ( 9.625, 9.625 )));
    }


    public static NHitBox getByName ( String name ) {
//        if ( name.contains ( "stockpile-branch" )  ) {
//            return new NHitBox ( new Coord2d ( -3.4375, -3.4375 ), new Coord2d ( 3.4375, 3.4375 ) );
//        }
//        else if (name.contains ( "barrel" ) || name.contains ( "churn" ) ||
//                name.contains ( "borka" ) || name.contains ( "dreca" ) ) {
//            return new NHitBox ( new Coord2d ( -4.125, -4.125 ), new Coord2d ( 4.125, 4.125 ) );
//        }
//        else if ( name.contains ( "locabin" ) ) {
//            return new NHitBox ( new Coord2d ( -26, -26 ), new Coord2d ( 26, 26 ) );
//        }
//        else if ( name.contains ( "stonemansion-door" ) || name.contains ( "greathall-door" ) || name.contains (
//                "stonetower-door" )|| name.contains ( "windmill-door" ) || name.contains ( "stonestead-door" ) ) {
//            return new NHitBox ( new Coord2d ( -2.75, -8.25 ), new Coord2d ( 2.75, 8.25 ) );
//        }
//        else if ( name.contains ( "ptrellis" ) ) {
//            return new NHitBox ( new Coord2d ( -4, -5.5 ), new Coord2d ( 4, 5.5 ) );
//        }
//        else if ( name.contains ( "trellis" ) ) {
//            return new NHitBox ( new Coord2d ( -1.375, -5.5 ), new Coord2d ( 1.375, 5.5 ) );
//        }
//        else if ( name.contains ( "hwall" ) ) {
//            return new NHitBox ( new Coord2d ( -0.02, -5.5 ), new Coord2d ( 0.02, 5.5 ) );
//        }
        if ( name.contains ( "log" ) ) {
            return new NHitBox ( new Coord2d ( -11, -2.75 ), new Coord2d ( 11, 2.75 ) );
        }
        else if ( name.contains ( "ptrellis" ) ) {
            return new NHitBox(new Coord2d(-4, -5.5), new Coord2d(4, 5.5));
        }
//        else if ( name.contains ( "boughbed" ) ) {
//            return new NHitBox ( new Coord2d ( -11, -5.5 ), new Coord2d ( 11, 5.5 ) );
//        }
//        else if ( name.contains ( "htable" ) ) {
//            return new NHitBox ( new Coord2d ( -5.5, -8.25 ), new Coord2d ( 5.5, 8.25 ) );
//        }
//        else if ( name.contains ( "horse/foal" ) || name.contains ( "cattle/calf" ) || name.contains ( "piglet" ) || name.contains ( "pig" ) ) {
//            return new NHitBox ( new Coord2d ( -8.25, -5.5 ), new Coord2d ( 8.25, 5.5 ) );
//        }
//        else if ( name.contains ( "stockpile" ) || name.contains ( "table" ) ) {
//            if ( name.contains ( "board" ) || name.contains ( "pump" )  ||  name.contains ( "soil" )) {
//                return new NHitBox ( new Coord2d ( -11, -11 ), new Coord2d ( 11, 11 ) );
//            }
//            else if ( name.contains ( "metal" ) ) {
//                return new NHitBox ( new Coord2d ( -5.5, -8.25 ), new Coord2d ( 5.5, 8.25 ) );
//            }
//            else if ( name.contains ( "straw" ) || name.contains ( "winepress" ) ) {
//                return new NHitBox ( new Coord2d ( -8.25, -8.25 ), new Coord2d ( 8.25, 8.25 ) );
//            }
//            else if ( name.contains ( "brick" ) ) {
//                return new NHitBox ( new Coord2d ( -11, -5.5 ), new Coord2d ( 11, 5.5 ) );
//            }
//            else if ( name.contains ( "leaf" ) ) {
//                return new NHitBox ( new Coord2d ( -8.25, -8.25 ), new Coord2d ( 8.25, 8.25 ) );
//            }
//            return new NHitBox ( new Coord2d ( -5.5, -5.5 ), new Coord2d ( 5.5, 5.5 ) );
//        }
//        else if ( name.contains ( "ttub" ) || name.contains ( "cupboard" ) ) {
//            return new NHitBox ( new Coord2d ( -5.5, -5.5 ), new Coord2d ( 5.5, 5.5 ) );
//        }
//        else if ( name.contains ( "cart" ) ) {
//            return new NHitBox ( new Coord2d ( -8.25, -6.875 ), new Coord2d ( 8.25, 6.875 ) );
//        }
//        else if ( name.contains ( "steelcrucible" ) ) {
//            return new NHitBox ( new Coord2d ( -4.75, -5.5 ), new Coord2d ( 4.75, 5.5 ) );
//        }
//        else if ( name.contains ( "crucible" ) || name.contains ( "candelabrum" ) ) {
//            return new NHitBox ( new Coord2d ( -2.75, -2.75 ), new Coord2d ( 2.75, 2.75 ) );
//        }
//        else if ( name.contains ( "downstairs" ) ) {
//            return new NHitBox ( new Coord2d ( -11, -5.5 ), new Coord2d ( 11, 5.5 ) );
//        }
//        else if ( name.contains ( "trough" ) ) {
//            return new NHitBox ( new Coord2d ( -5.5, -11 ), new Coord2d ( 5.5, 11 ) );
//        }
//        else if ( name.contains ( "boat" ) ) {
//            return new NHitBox ( new Coord2d ( -21, -8.25 ), new Coord2d ( 21, 8.25 ) );
//        }
//        else if ( name.contains ( "palisadebiggate" ) ) {
//            return new NHitBox ( new Coord2d ( -5.5, -12.375 ), new Coord2d ( 5.5, 12.375 ) );
//        }
//        else if ( name.contains ( "geyser" ) || name.contains ( "woodheart" ) || name.contains ( "saltbasin" ) ) {
//            return new NHitBox ( new Coord2d ( -27.5, -27.5 ), new Coord2d ( 27.5, 27.5 ) );
//        }
//        else if ( name.contains ( "tarkiln" ) ) {
//            return new NHitBox ( new Coord2d ( -25, -25 ), new Coord2d ( 25, 25 ) );
//        }
//        else if ( name.contains ( "primsmelter" ) ) {
//            return new NHitBox ( new Coord2d ( -11, -8.25 ), new Coord2d ( 11, 8.25 ) );
//        }
//        else if ( name.contains ( "smelter" ) ) {
//            return new NHitBox ( new Coord2d ( -12.375, -22 ), new Coord2d ( 12.375, 22 ) );
//        }
//        else if ( name.contains ( "minehole" ) ) {
//            return new NHitBox ( new Coord2d ( -12.375, -12.375 ), new Coord2d ( 12.375, 12.375 ) );
//        }
//        else if ( name.contains ( "stonewall" ) ) {
//            return new NHitBox ( new Coord2d ( -5.5, -5.5 ), new Coord2d ( 5.5, 5.5 ) );
//        }
//        else if ( name.contains ( "stonepine" ) || name.contains ( "blackpine" ) ) {
//            return new NHitBox ( new Coord2d ( -8.25, -8.25 ), new Coord2d ( 8.25, 8.25 ) );
//        }
//        else if ( name.contains ( "wagon" ) ) {
//
//            if ( name.contains ( "station" ) ) {
//                return new NHitBox ( new Coord2d ( -11, -38.5 ), new Coord2d ( 11, 38.5 ) );
//            }
//            else {
//                return new NHitBox ( new Coord2d ( -17.5, -11 ), new Coord2d ( 17.5, 11 ) );
//            }
//        }
//
//        else if ( name.contains ( "timberhouse" ) ) {
//            return new NHitBox ( new Coord2d ( -33, -33 ), new Coord2d ( 33, 33 ) );
//        }
//        else if ( name.contains ( "ropewalk" ) ) {
//            return new NHitBox ( new Coord2d ( -22, -8.25 ), new Coord2d ( 22, 8.25 ) );
//        }
//        else if ( name.contains ( "loom" ) ) {
//            return new NHitBox ( new Coord2d ( -8.25, -8.25 ), new Coord2d ( 8.25, 8.25 ) );
//        }
//        else if ( name.contains ( "kiln" ) || name.contains ( "gate" ) || name.contains ( "stonepillar" ) || name.contains ( "leanto" )) {
//            return new NHitBox ( new Coord2d ( -13, -13 ), new Coord2d ( 13, 13 ) );
//        }
//        else if ( name.contains ( "crate" ) ) {
//            return new NHitBox ( new Coord2d ( -5.5, -8.25 ), new Coord2d ( 5.5, 8.25 ) );
//        }
//        else if ( name.contains ( "dframe" ) ) {
//            return new NHitBox ( new Coord2d ( -3.4375, -11 ), new Coord2d ( 3.4375, 11 ) );
//            //            return new NHitBox ( new Coord2d ( -11, -11 ), new Coord2d ( 11, 11 ) );
//        }
//        else if ( name.contains ( "stonemansion" ) ) {
//            return new NHitBox ( new Coord2d ( -49.5, -49.5 ), new Coord2d ( 49.5, 49.5 ) );
//        }
//        else if ( name.contains ( "greathall" ) ) {
//            return new NHitBox ( new Coord2d ( -79.75, -55 ), new Coord2d ( 79.75, 55 ) );
//        }
//        else if ( name.contains ( "palisadeseg" ) ) {
//            return new NHitBox ( new Coord2d ( -5.5, -5.5 ), new Coord2d ( 5.5, 5.5 ) );
//        }
//        else if ( name.contains ( "oven" ) ) {
//            return new NHitBox ( new Coord2d ( -11, -11 ), new Coord2d ( 11, 11 ) );
//        }
//        else if ( name.contains ( "stonetower" ) ) {
//            return new NHitBox ( new Coord2d ( -38.75, -38.75 ), new Coord2d ( 38.75, 38.75 ) );
//        }
//
//        else if ( name.contains ( "windmill" ) ) {
//            return new NHitBox ( new Coord2d ( -27.5, -27.5 ), new Coord2d ( 27.5, 27.5 ) );
//        }
//        else if ( name.contains ( "villageidol" ) ) {
//            return new NHitBox ( new Coord2d ( -11, -16.5 ), new Coord2d ( 11, 16.5 ) );
//        }
//        else if ( name.contains ( "gardenpot" ) || name.contains ( "iconsign" ) || name.contains ( "barterstand-sign" )) {
//            return new NHitBox ( new Coord2d ( -2.75, -2.75 ), new Coord2d ( 2.75, 2.75 ) );
//        }
//        else if ( name.contains ( "chest" ) || name.contains ( "metalcabinet" ) ) {
//            return new NHitBox ( new Coord2d ( -4.125, -4.125 ), new Coord2d ( 4.125, 4.125 ) );
//        }
//        else if ( name.contains ( "reddeer" ) || name.contains ( "boar" ) || name.contains ( "horse" ) ||
//                name.contains ( "reindeer" ) || name.contains ( "wolf" ) ) {
//            return new NHitBox ( new Coord2d ( -11, -5.5 ), new Coord2d ( 11, 5.5 ) );
//        }
//        else if ( name.contains ( "barterstand" ) ) {
//            return new NHitBox ( new Coord2d ( -8.25, -11 ), new Coord2d ( 8.25, 11 ) );
//        }
//        else if ( name.contains ( "cattle" ) ) {
//            return new NHitBox ( new Coord2d ( -14.25, -8.25 ), new Coord2d ( 14.25, 8.25 ) );
//        }
//        else if ( name.contains ( "moose" ) || name.contains ( "walrus" ) || name.contains ( "bear" ) ||
//                name.contains ( "cattle" ) || name.contains ( "caveangler" ) || name. contains ( "beaverking" )) {
//            return new NHitBox ( new Coord2d ( -13.75, -8.25 ), new Coord2d ( 13.75, 8.25 ) );
//        }
//        else if ( name.contains ( "smokeshed" )) {
//            return new NHitBox ( new Coord2d ( -7.5625, -7.5625 ), new Coord2d ( 7.5625, 7.5625 ) );
//        }
//        else if ( name.contains ( "shed" ) || name.contains ( "chickencoop" ) || name.contains ( "rabbithutch" ) ) {
//            return new NHitBox ( new Coord2d ( -11, -11 ), new Coord2d ( 11, 11 ) );
//        }
//        else if ( name.contains ( "greyseal" ) ) {
//            return new NHitBox ( new Coord2d ( -8.25, -5.5 ), new Coord2d ( 8.25, 5.5 ) );
//        }
//        else if ( name.contains ( "stonestead" ) ) {
//            return new NHitBox ( new Coord2d ( -48.125, -27.5 ), new Coord2d ( 48.125, 27.5 ) );
//        }
//        else if ( name.contains ( "wheelbarrow" ) || name.contains ( "plow" ) ) {
//            return new NHitBox ( new Coord2d ( -8.25, -5.5 ), new Coord2d ( 8.25, 5.5 ) );
//        }
//        else if ( name.contains ( "cistern" ) ) {
//            return new NHitBox ( new Coord2d ( -9.625, -9.625 ), new Coord2d ( 9.625, 9.625 ) );
//        }
//        else if (NUtils.checkName(name, "blackpine", "carobtree", "dogwood", "stonepine", "strawberrytree", "alder", "birch", "spruce", "sycamore"))
//        {
//            return new NHitBox ( new Coord2d ( -8.25, -8.25 ), new Coord2d ( 8.25, 8.25 ) );
//        }
        else {
            return new NHitBox ( new Coord2d ( -5.5, -5.5 ), new Coord2d ( 5.5, 5.5 ) );
        }
    }
}
