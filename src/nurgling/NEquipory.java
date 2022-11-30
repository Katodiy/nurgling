package nurgling;

import haven.*;
import haven.res.ui.tt.armor.Armor;
import haven.res.ui.tt.attrmod.AttrMod;
import haven.res.ui.tt.islots.ISlots;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
/**
 * Экипировка
 */
public class NEquipory extends Equipory {
    public static final RichText.Foundry fnd = new RichText.Foundry(new ChatUI.ChatParser(TextAttribute.FONT, Text.dfont.deriveFont(UI.scale(18f)), TextAttribute.FOREGROUND, Color.BLACK));
    /// Массив предметов в быстрых слотах экипировки
    public WItem[] quickslots = new NWItem[ecoords.length];
    private LinkedList<Tex> gildBufimgs;
    private Tex armorclass;

    public enum Slots {
        HEAD(0),       //00: Headgear
        ACCESSORY(1),  //01: Main Accessory
        SHIRT(2),      //02: Shirt
        ARMOR_BODY(3), //03: Torso Armor
        GLOVES(4),     //04: Gloves
        BELT(5),       //05: Belt
        HAND_LEFT(6),  //06: Left Hand
        HAND_RIGHT(7), //07: Right Hand
        RING_LEFT(8),  //08: Left Hand Ring
        RING_RIGHT(9), //09: Right Hand Ring
        ROBE(10),      //10: Cloaks & Robes
        BACK(11),      //11: Backpack
        PANTS(12),     //12: Pants
        ARMOR_LEG(13), //13: Armor
        CAPE(14),      //14: Cape
        BOOTS(15),     //15: Shoes
        STORE_HAT(16), //16: Hat from store
        EYES(17),      //17: Eyes
        MOUTH(18);     //18: Mouth

        public final int idx;
        Slots(int idx) {
            this.idx = idx;
        }
    }

    /**
     * Конструктор
     *
     * @param gobid ?
     */
    public NEquipory ( long gobid ) {
        super ( gobid );
    }

    /**
     * Процедура вызываемая при добавлении в экипировку предмета
     *
     * @param child ? Одеваемый предмет
     * @param args  ?
     */
    @Override
    public void addchild (
            Widget child,
            Object... args
    ) {
        if ( child instanceof NGItem ) {
            add ( child );
            NGItem g = ( NGItem ) child;
            WItem[] v = new NWItem[args.length];
            for ( int i = 0 ; i < args.length ; i++ ) {
                int ep = ( Integer ) args[i];
                /// Одеваемый предмет сохраняется в массиве слотов
                v[i] = quickslots[ep] = add ( new NWItem(g), ecoords[ep].add ( 1, 1 ) );
            }
            wmap.put ( g, Arrays.asList ( v.clone () ) );
            updBuffs = true;
        }
        else {
            super.addchild ( child, args );
        }
    }

    public WItem getFreeSlot ( NAlias name ) {
        if ( quickslots[6] != null && NUtils.isIt ( quickslots[6].item, name ) ) {
            return quickslots[6];
        }
        else if ( quickslots[7] != null && NUtils.isIt ( quickslots[7].item, name ) ) {
            return quickslots[7];
        }
        return null;
    }

    /**
     * Процедура вызываемая при удалении из экипировки предмета
     *
     * @param w Удаляемый предмет
     */
    @Override
    public void cdestroy ( Widget w ) {
        if ( w instanceof GItem ) {
            GItem i = ( GItem ) w;
            for ( WItem v : wmap.remove ( i ) ) {
                ui.destroy ( v );
                for ( int qsi = 0 ; qsi < ecoords.length ; qsi++ ) {
                    if ( quickslots[qsi] == v ) {
                        /// Снимаемый предмет удаляется из массива
                        quickslots[qsi] = null;
                        break;
                    }
                }
            }
            updBuffs = true;
        }
    }


    public boolean updBuffs = true;

    public void draw ( GOut g ) {
        drawslots ( g );
        super.draw ( g );
        if ( updBuffs ) {
            try {
                int aHard = 0, aSoft = 0;
                HashMap<String, AttrMod.Mod> gildBuffs = new HashMap<> ();
                for ( int i = 0 ; i < quickslots.length ; i++ ) {
                    if ( quickslots[i] != null ) {
                        GItem itm = quickslots[i].item;
                        if ( itm == null ) {
                            continue;
                        }
                        if ( i != 0 && quickslots[0] != null &&
                                quickslots[0].item.getres ().name.equals ( itm.getres ().name ) ) {
                            continue;
                        }
                        for ( ItemInfo info : itm.info () ) {
                            if ( info instanceof Armor) {
                                aHard += ( (Armor) info ).hard;
                                aSoft += ( ( Armor ) info ).soft;
                            }
                            else if ( info instanceof AttrMod ) {
                                for ( AttrMod.Mod mod : ( ( AttrMod ) info ).mods ) {
                                    String attributeName = mod.attr.layer ( Resource.tooltip ).t;
                                    gildBuffs.putIfAbsent ( attributeName, new AttrMod.Mod ( mod.attr, 0 ) );
                                    gildBuffs.get ( attributeName ).mod += mod.mod;
                                }
                            }
                            else if ( info instanceof ISlots) {
                                ( (ISlots) info ).s.forEach ( (sitem ) -> {
                                    sitem.info.forEach ( info2 -> {
                                        for ( AttrMod.Mod mod : ( ( AttrMod ) info2 ).mods ) {
                                            String attributeName = mod.attr.layer ( Resource.tooltip ).t;
                                            gildBuffs.putIfAbsent ( attributeName, new AttrMod.Mod ( mod.attr, 0 ) );
                                            gildBuffs.get ( attributeName ).mod += mod.mod;
                                        }
                                    } );
                                } );
                            }
                        }
                    }
                    gildBufimgs = new LinkedList<> ();
                    gildBufimgs.add ( Text.render ( "Total attributes: " ).tex () );
                    for ( Map.Entry<String, AttrMod.Mod> e : gildBuffs.entrySet () ) {
                        if ( e.getValue ().mod == 0 ) {
                            continue;
                        }
                        BufferedImage bufferedImage1 = ( RichText.render (
                                String.format ( "%s $col[%s]{%s%d}", e.getValue ().attr.layer ( Resource.tooltip ).t,
                                        ( e.getValue ().mod < 0 ) ? AttrMod.debuff : AttrMod.buff,
                                        ( char ) ( ( e.getValue ().mod < 0 ) ? 45 : 43 ),
                                        Math.abs ( e.getValue ().mod ) ), 0 ) ).img;
                        BufferedImage bufferedImage2 = PUtils
                                .convolvedown ( ( ( Resource.Image ) e.getValue ().attr.layer ( Resource.imgc ) ).img,
                                        new Coord ( bufferedImage1.getHeight (), bufferedImage1.getHeight () ),
                                        CharWnd.iconfilter );
                        BufferedImage combined = AttrMod.catimgsh ( 0, bufferedImage2, bufferedImage1 );
                        gildBufimgs.add ( new TexI ( combined ) );
                    }

                    armorclass = fnd.render ( "Armor class: " + aHard + "/" + aSoft).tex ();
                    updBuffs = false;
                }
            }
            catch ( Exception e ) {
                e.printStackTrace ();// Ignored}
            }
        }
        if ( armorclass != null ) {
            g.image ( armorclass, new Coord ( ( UI.scale ( 34 ) + bg.sz ().x / 2 ) - armorclass.sz ().x / 2,
                    bg.sz ().y - armorclass.sz ().y ) );
        }
        if ( gildBufimgs != null ) {
            int ofsY = 0;
            for ( Tex gTex : gildBufimgs ) {
                g.image ( gTex, new Coord ( UI.scale ( 320 ), ofsY += UI.scale ( 15 ) ) );
            }
        }
    }

    public void freeHands () {
        /// Освобождаем руки
        if ( quickslots[6] != null ) {
            quickslots[6].item.wdgmsg ( "transfer", quickslots[6].sz );
        }
        if ( quickslots[7] != null ) {
            quickslots[7].item.wdgmsg ( "transfer", quickslots[7].sz );
        }

    }

    public boolean isHandFree(){
        return quickslots[6] == null && quickslots[7] == null;
    }
}
