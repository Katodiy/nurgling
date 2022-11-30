package nurgling.bots.actions;

import haven.*;

import haven.res.ui.tt.leashed.Leashed;
import nurgling.*;
import nurgling.tools.Finder;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static haven.MCache.tilesz;
import static nurgling.NUtils.getGameUI;

public class NomadCollector implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        URL url = NUtils.class.getProtectionDomain ().getCodeSource ().getLocation ();
        if ( url != null ) {

            try {
                DataInputStream in =
                        new DataInputStream ( new FileInputStream ((NConfiguration.botmod!=null)?NConfiguration.botmod.nomad:path ));
                while( true ){
                    try {
                        if ( !(in.available ()>0) )
                            break;
                        marks.add ( new Coord2d (in.readInt (),in.readInt ()));
                    }
                    catch ( IOException e ) {
                        break;
                    }
                }
            }
            catch (  FileNotFoundException e ) {
                e.printStackTrace ();
            }
        }
        gui.msg("File is loaded");
        NUtils.waitEvent(()->(NMenuGrid) getGameUI().menu!=null,200);
        NUtils.command ( new char[]{ 'a', 'h', 'h' });
        NUtils.waitEvent(()->gui.getProg()>=0,50);
        NUtils.waitEvent(()->gui.getProg()<0,10000);
        NUtils.waitEvent(()->getGameUI ().ui.sess.glob.map.isLoaded(),5000);
        gui.msg("Map is loaded");
        NUtils.waitEvent(()->Finder.findObject (new NAlias ("pow") )!=null,5000);
        Thread.sleep(10000);
        Gob gob = Finder.findObject (new NAlias ("pow") );
        if(leashedAnimal) {
            leashed = Finder.findObject(new NAlias(leashed.getResName()));
        }
        if(gob==null)
            return new Results ( Results.Types.NO_PLACE );
        if(leashedAnimal){
            NUtils.waitEvent(()->NUtils.getGob(leashed.id)!=null,5000);
        }
        Coord2d shift = gob.rc;
        for(Coord2d coord : marks){
            Coord2d pos =  coord.add ( shift );
            Coord poscoord = pos.div ( MCache.tilesz ).floor ();
            pos = new Coord2d ( ( poscoord ).x * tilesz.x + tilesz.x/2, ( poscoord ).y * tilesz.y + tilesz.y/2);
//            gui.msg ( pos.toString () );
            PathFinder pf = new PathFinder ( gui,pos );
            if(!leashedAnimal) {
                pf.setWithAlarm(true);
            }else{
                if(NUtils.getGob(leashed.id).rc.dist(gui.map.player().rc)>=66) {
                    WItem rope = gui.getInventory().getItem(new NAlias("rope"), Leashed.class);
//                    new SelectFlowerAction(rope, "Pull", SelectFlowerAction.Types.Inventory).run(gui);
                    NUtils.waitEvent(() -> NUtils.getGob(leashed.id).rc.dist(gui.map.player().rc) < 55, 1000);
                }
            }
            try {
                pf.run();
            }catch (InterruptedException e) {
                if (!leashedAnimal) {
                    if (NUtils.alarm()) {
                        /// Тревога валим
                        NUtils.logOut();
                    }
                }
            }
            if(leashedAnimal){
                NUtils.waitEvent(()->NUtils.isPose(leashed,new NAlias("walking")),10);
                if(NUtils.isPose(leashed,new NAlias("walking")))
                {
                    NUtils.waitEvent(()->NUtils.isPose(leashed,new NAlias("idle")),5000);
                }
            }
            ArrayList<Gob> targets = Finder.findObjects(name);
            NUtils.sendToChat("Palace of Pleasure", "Я нашел трюфелей: "+targets.size());
            if(!targets.isEmpty() && leashedAnimal){
                boolean pigNear = false;
                leashed =NUtils.getGob(leashed.id);
                for(Gob truf: targets)
                {
                    if(truf.rc.dist(leashed.rc)<11) {
                        pigNear = true;
                        break;
                    }
                }
                if(!pigNear){
                    new CollectNearMark(pos, name, leashedAnimal, gob).run(gui);
                }
            }else
            {
                if ((new CollectNearMark(pos, name, leashedAnimal, gob).run(gui).type) != Results.Types.SUCCESS)
                    break;
            }
        }
        return new Results ( Results.Types.SUCCESS );
    }

    public NomadCollector(NAlias name, String path
    ) {
        this.name = name;
        this.path = path;
    }

    public NomadCollector(NAlias name, String path, boolean leashedAnimal, Gob leashed
    ) {
        this.name = name;
        this.leashedAnimal = leashedAnimal;
        this.leashed = leashed;
        this.path = path;
    }
    
    NAlias name;
    ArrayList<Coord2d> marks = new ArrayList<> ();

    String path;

    boolean leashedAnimal;

    Gob leashed;
}
