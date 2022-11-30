package nurgling.bots.actions;

import haven.*;
import nurgling.*;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;
import java.util.Arrays;

import static haven.OCache.posres;

public class PleaseLeaveMeAlone implements Action {

    @Override
    public Results run(NGameUI gui)
            throws InterruptedException {
        while (true) {
            ArrayList<Gob> gobs = Finder.findObjects(new NAlias("borka"));
            for (Gob gob : gobs) {
                if(gob.id!=gui.map.player().id) {
                    ChatUI.Channel chat = NUtils.getGameUI().chat.chat.sel;
                    if (chat instanceof ChatUI.EntryChannel) {
                        if (!chat.getClass().getName().contains("Realm")) {
                            ArrayList<Gob> palisad = Finder.findObjects(new NAlias("palis"));
                            boolean isLowHp = false;
                            boolean isFind = false;
                            for (Gob pali : palisad) {
                                GobHealth hp = pali.getattr(GobHealth.class);
                                if(hp.hp!=1 && !isFind){
                                    double i = Math.random();
                                    if(i<0.33) {
                                        ((ChatUI.EntryChannel) chat).send("Why are you doing this? Please leave me alone");
                                    }else if(i<0.66) {
                                        ((ChatUI.EntryChannel) chat).send("Please stop...");
                                    }else{
                                        ((ChatUI.EntryChannel) chat).send("((((");
                                    }

                                    isLowHp = true;
                                    isFind = true;
                                }
                                if(hp.hp==0.25){
                                    new SelectFlowerAction(gob,"Memorize", SelectFlowerAction.Types.Gob).run(gui);
                                    NUtils.waitEvent(()->gui.getProg()>0,20);
                                    NUtils.waitEvent(()->gui.getProg()<0,200);
                                    ((ChatUI.EntryChannel) chat).send("You piece of shit");
                                    NUtils.command ( new char[]{ 'a', 'h', 'h' } );
                                    return new Results(Results.Types.SUCCESS);
                                }

                            }
                            if (!isLowHp) {
                                double i = Math.random();
                                if(i<0.33) {
                                    ((ChatUI.EntryChannel) chat).send("Please.... Leave me alone");
                                }else if(i<0.66) {
                                    ((ChatUI.EntryChannel) chat).send("I knew that you would come. Please don't touch me...");
                                }else{
                                    ((ChatUI.EntryChannel) chat).send("I am a peaceful hermit who does not touch anyone");
                                }
                            }
                        }
                    }
                }
            }

            Thread.sleep(5000);
        }
    }

}
