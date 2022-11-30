package nurgling.bots.actions;

import haven.*;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.tools.AreasID;

import java.util.ArrayList;

public class OpenBarrelAndTransfer implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
                new PathFinder( gui, barrel ).run ();
                if ( new OpenTargetContainer ( barrel, "Barrel" ).run ( gui ).type != Results.Types.SUCCESS ) {
                    return new Results ( Results.Types.OPEN_FAIL );
                }
                Window wnd = gui.getWindow ( "Barrel" );
                for ( Widget widget = wnd.child ; widget != null ; widget = widget.next ) {
                    for ( Widget subwidget = widget.child ; subwidget != null ; subwidget = subwidget.next ) {
                        if ( subwidget.getClass ().getSimpleName ().equals ( "TipLabel" ) ) {
                            try {
                                double count = 0;
                            
                                do {
                                    if(NUtils.isOverlay ( barrel, content )) {
                                        for ( ItemInfo info : ( ArrayList<ItemInfo> ) subwidget.getClass ().getField ( "info" )
                                                                                               .get ( subwidget ) ) {
                                            if ( info.getClass ().getSimpleName ().equals ( "Name" ) ) {
                                                String strinf = ( ( Text.Line ) info.getClass ().getField ( "str" ).get ( info ) ).text;
                                                if ( NUtils.checkName (
                                                        strinf.substring ( strinf.lastIndexOf ( ' ' ) + 1 ), content ) ) {
                                                    count = Double.parseDouble ( strinf.substring ( 0, strinf.indexOf ( ' ' ) ) );
                                                }
                                            }
                                        }
                                    }
                                    WItem item = gui.getInventory ().getItem ( content ) ;
                                    if(item!=null) {
                                        if ( gui.hand.isEmpty () ) {
                                            new TakeToHand ( item ).run ( gui );
                                        }
                                        int counter = 0;
                                        while ( !gui.hand.isEmpty () && counter < 20 ) {
                                            NUtils.activateItem ( barrel );
                                            Thread.sleep ( 50 );
                                            counter++;
                                        }
                                    }else {
                                        return new Results ( Results.Types.SUCCESS );
                                    }

                                }while(count<content_count);
                            }
                            catch ( IllegalAccessException | NoSuchFieldException e ) {
                                e.printStackTrace ();
                            }
                        }
                    }
                
            
        }
        return new Results ( Results.Types.FULL );
    }
    
    public OpenBarrelAndTransfer(
            double content_count,
            NAlias content,
            AreasID zone,
            Gob barrel
    ) {
        this.content_count = content_count;
        this.content = content;
        this.zone = zone;
        this.barrel= barrel;
    }
    
    Gob barrel;
    /// Количество содержимого
    double content_count;
    /// Название содержимого
    NAlias content;
    /// Зона бочек для поиска
    AreasID zone;
}
