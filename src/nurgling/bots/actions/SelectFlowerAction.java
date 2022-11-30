package nurgling.bots.actions;

import haven.Coord;
import haven.Gob;
import haven.WItem;
import haven.res.gfx.terobjs.roastspit.Roastspit;
import nurgling.*;
import nurgling.tools.Finder;

import static haven.OCache.posres;

public class SelectFlowerAction implements Action {
    public SelectFlowerAction (
            NAlias name,
            String action,
            Types type
    ) {
        _name = name;
        _action = action;
        _type = type;
    }
    
    public SelectFlowerAction (
            WItem item,
            String action,
            Types type
    ) {
        _item = item;
        _action = action;
        _type = type;
    }
    
    public SelectFlowerAction (
            Gob gob,
            String action,
            Types type
    ) {
        this.gob = gob;
        _action = action;
        _type = type;
    }
    
    public SelectFlowerAction (
            Gob gob,
            String action,
            Types type,
            boolean trellis
    ) {
        this.gob = gob;
        _action = action;
        _type = type;
        _trellis = trellis;
    }
    
    
    public enum Types {
        Inventory, Equipment, Gob, Roastspit
    }
    
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        if ( NFlowerMenu.instance != null ) {
            NFlowerMenu.stop ();
        }
        switch ( _type ) {
            case Inventory:
                selectInventory ( gui );
                break;
            case Equipment:
                selectEquipment ( gui );
                break;
            case Gob:
                selectGob ( gui );
                break;
            case Roastspit:
                selectRoastspit(gui);
                break;
        }
        gob = null;
        if ( !NFlowerMenu.selectNext ) {
            return new Results ( Results.Types.SUCCESS );
        }
        else {
            return new Results ( Results.Types.SELECT_FLOWER_FAIL );
        }
        
    }

    private void selectRoastspit(NGameUI gui) throws InterruptedException {
        if ( gob == null ) {
            gob = Finder.findObject ( _name );
        }
        Gob.Overlay ol = gob.findol(Roastspit.class);
        if(ol!=null) {
            for (int i = 0; i < 5; i++) {
                gui.map.wdgmsg("click", Coord.z, gob.rc.floor(posres), 3, 0, 1, (int) gob.id,
                        gob.rc.floor(posres), ol.id, -1);
                if (NUtils.waitEvent(() -> NFlowerMenu.instance != null, 5)) {
                    if (NFlowerMenu.instance.isContain(_action)) {
                        NFlowerMenu.instance.selectInCurrent(_action);
                        if (NUtils.waitEvent(() -> !NFlowerMenu.selectNext, 5)) {
                            NFlowerMenu.stop();
                            return;
                        }
                    }
                }
            }
        }
    }

    void selectGob ( NGameUI gui )
            throws InterruptedException {
        if ( gob == null ) {
            gob = Finder.findObject ( _name );
        }
        if(!_trellis) {
            if(gob.rc.dist(NUtils.getGameUI().map.player().rc)>15)
                new PathFinder(NUtils.getGameUI(), gob).run();
        }
        for ( int i = 0 ; i < 5 ; i++ ) {
            gui.map.wdgmsg ( "click", Coord.z, gob.rc.floor ( posres ), 3, 0, 0, ( int ) gob.id,
                    gob.rc.floor ( posres ), 0, -1 );
            if ( NUtils.waitEvent ( () -> NFlowerMenu.instance != null, 5 ) ) {
                if(NFlowerMenu.instance.isContain ( _action )) {
                    NFlowerMenu.instance.selectInCurrent ( _action );
                    if ( NUtils.waitEvent ( () -> !NFlowerMenu.selectNext, 5 ) ) {
                        NFlowerMenu.stop ();
                        return;
                    }
                }
            }
        }
    }
    
    void selectInventory ( NGameUI gui )
            throws InterruptedException {
        if ( _item != null ) {
            for ( int i = 0 ; i < 5 ; i++ ) {
                _item.item.wdgmsg ( "iact", _item.sz, 0 );
                if ( NUtils.waitEvent ( () -> NFlowerMenu.instance != null, 5 ) ) {
                    if(NFlowerMenu.instance.isContain ( _action )) {
                        NFlowerMenu.instance.selectInCurrent ( _action );
                        if ( NUtils.waitEvent ( () -> !NFlowerMenu.selectNext, 5 ) ) {
                            NFlowerMenu.stop ();
                            return;
                        }
                    }
                }
            }
        }
    }
    
    void selectEquipment ( NGameUI gui )
            throws InterruptedException {
        
        WItem wItem = Finder.findDressedItem ( _name );
        if ( wItem != null ) {
            for ( int i = 0 ; i < 5 ; i++ ) {
                wItem.item.wdgmsg ( "iact", wItem.sz, 0 );
                if ( NUtils.waitEvent ( () -> NFlowerMenu.instance != null, 5 ) ) {
                    if(NFlowerMenu.instance.isContain ( _action )) {
                        NFlowerMenu.instance.selectInCurrent ( _action );
                        if ( NUtils.waitEvent ( () -> !NFlowerMenu.selectNext, 5 ) ) {
                            NFlowerMenu.stop ();
                            return;
                        }
                    }
                }
            }
        }
    }
    
    
    NAlias _name;
    WItem _item = null;
    Gob gob = null;
    String _action;
    Types _type;
    boolean _trellis = false;
}
