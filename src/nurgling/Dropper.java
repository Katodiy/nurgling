package nurgling;

import haven.GItem;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Dropper implements Runnable {
    
    public Dropper(NGameUI gui, NAlias regEx )
            throws InterruptedException {
        this.gui = gui;
        saved = gui.getInventory ().getWItems();
        this.regEx = regEx;
    }
    
    @Override
    public void run () {
        try {
            while ( isAlive.get () ) {
                Thread.sleep ( 500 );
                ArrayList<GItem> items = gui.getInventory ().getWItems();
                for(GItem item:items){
                    if(!saved.contains ( item ))
                        if(!NUtils.isIt ( item,regEx ))
                            NUtils.drop (item);
                }
            }
        } catch (InterruptedException ignored) {
        
        }
    }
    
    NGameUI gui;
    public ArrayList<GItem> saved;
    public AtomicBoolean isAlive = new AtomicBoolean (true);
    NAlias regEx;
}
