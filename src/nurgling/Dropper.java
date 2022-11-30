package nurgling;

import haven.WItem;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Dropper implements Runnable {
    
    public Dropper(NGameUI gui, NAlias regEx )
            throws InterruptedException {
        this.gui = gui;
        saved = gui.getInventory ().getItems ();
        this.regEx = regEx;
    }
    
    @Override
    public void run () {
        try {
            while ( isAlive.get () ) {
                Thread.sleep ( 500 );
                ArrayList<WItem> items = gui.getInventory ().getItems ();
                for(WItem item:items){
                    if(!saved.contains ( item ))
                        if(!NUtils.isIt ( item,regEx ))
                            NUtils.drop (item);
                }
            }
        } catch (InterruptedException e) {
        
        }
        
    }
    
    NGameUI gui;
    public ArrayList<WItem> saved;
    public AtomicBoolean isAlive = new AtomicBoolean (true);
    NAlias regEx;
}
