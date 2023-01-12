package nurgling.bots.actions;

import haven.*;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.tools.AreasID;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;

public class OpenBarrelWithContent implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        ArrayList<Gob> barrels;
        if ( area == null ) {
            barrels = Finder.findObjectsInArea ( new NAlias( "barrel" ), Finder.findNearestMark ( zone ) );
        }
        else {
            barrels = Finder.findObjectsInArea ( new NAlias ( "barrel" ), area );
        }
        for ( Gob barrel : barrels ) {
            if ( NUtils.isOverlay ( barrel, content ) ) {
                new PathFinder( gui, barrel ).run ();
                if ( new OpenTargetContainer ( barrel, "Barrel" ).run ( gui ).type != Results.Types.SUCCESS ) {
                    return new Results ( Results.Types.OPEN_FAIL );
                }
                Window wnd = gui.getWindow ( "Barrel" );
                for ( Widget widget = wnd.child ; widget != null ; widget = widget.next ) {
                    for ( Widget subwidget = widget.child ; subwidget != null ; subwidget = subwidget.next ) {
                        if ( subwidget.getClass ().getSimpleName ().equals ( "TipLabel" ) ) {
                            try {
                                for ( ItemInfo info : ( ArrayList<ItemInfo> ) subwidget.getClass ().getField ( "info" )
                                                                                       .get ( subwidget ) ) {
                                    if ( info.getClass ().getSimpleName ().equals ( "Name" ) ) {
                                        String strinf = ( ( Text.Line ) info.getClass ().getField ( "str" )
                                                                            .get ( info ) ).text;
                                        if ( NUtils.checkName ( strinf.substring ( strinf.lastIndexOf ( ' ' ) + 1 ),
                                                content ) ) {
                                            if ( Double.parseDouble (
                                                    strinf.substring ( 0, strinf.indexOf ( ' ' ) ) ) >=
                                                    content_count ) {
                                                return new Results ( Results.Types.SUCCESS );
                                            }
                                        }
                                    }
                                }
                            }
                            catch ( IllegalAccessException | NoSuchFieldException e ) {
                                e.printStackTrace ();
                            }
                        }
                    }
                }
            }
        }
        return new Results ( Results.Types.INGREDIENTS_NOT_FOUND );
    }
    
    public OpenBarrelWithContent(
            double content_count,
            NAlias content,
            AreasID zone
    ) {
        this.content_count = content_count;
        this.content = content;
        this.zone = zone;
    }
    
    
    public OpenBarrelWithContent(
            double content_count,
            NAlias content,
            NArea area
    ) {
        this.content_count = content_count;
        this.content = content;
        this.area = area;
    }
    
    /// Количество содержимого
    double content_count;
    /// Название содержимого
    NAlias content;
    /// Зона бочек для поиска
    AreasID zone;
    
    NArea area = null;
}
