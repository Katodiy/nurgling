package nurgling.bots.actions;

import haven.*;
import nurgling.*;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static haven.OCache.posres;

public class LPExplorer implements Action {
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
            try {
                File f = new File ( "./" + gui.chrid + ".dat" );
                OutputStream str;
                forWrite = new ArrayList<> ();
                ArrayList<Pair<String, String>> keys = new ArrayList<Pair<String, String>> ();
                InputStream in = null;
                if ( f.exists () ) {
                    try {
                        in = new FileInputStream ( f );
                        BufferedReader reader = new BufferedReader (
                                new InputStreamReader ( in, StandardCharsets.UTF_8 ) );
                        String buf = reader.readLine ();
                        while ( buf != null && buf.contains ( "|" ) ) {
                            keys.add ( new Pair<> ( buf.substring ( 0, buf.indexOf ( '|' ) ),
                                    buf.substring ( buf.indexOf ( '|' ) + 1 ) ) );
                            buf = reader.readLine ();
                        }
                    }
                    catch ( EOFException e ) {
                        e.printStackTrace ();
                    }
                    finally {
                        if ( in != null ) {
                            in.close ();
                        }
                    }
                    
                    str = new FileOutputStream ( f, true );
                }
                else {
                    str = new FileOutputStream ( "./" + gui.chrid + ".dat", false );
                }
                
                writer = new OutputStreamWriter ( str, StandardCharsets.UTF_8 );
                writer_is_ready = true;
                ArrayList<Gob> gobs = Finder.findObjectsInArea ( new NAlias( new ArrayList<> ( Arrays.asList ( "" )),
                        new ArrayList<> ( Arrays.asList ( "items", "oldstump","oldtrunk", "terobjs/arch","runestone",
                                "reflectingpool","kritter", "borka", "road", "ants","boostspeed", "birdsnest", "beeswarm", "s/dreca", "s/pow") ) ),
                        area );
//                System.out.println ( "Total gobs " + gobs.size () );
                while ( !gobs.isEmpty () ) {
                    Gob gob = Finder.findNearestObject (gobs);
                    boolean isChecked;
                    NFlowerMenu.stop ();
                    NUtils.waitEvent ( () -> NFlowerMenu.instance == null, 10 );
                    NAlias opt = NUtils.getMenuOpt ( gob, keys );
                    opt.keys.add ( "branch" );
                    opt.keys.add ( "bark" );
                    opt.keys.add ( "Chop" );
                    opt.keys.add ( "Open" );
                    opt.keys.add ( "Hack at" );
                    do {
                        isChecked = true;
                        gui.map.wdgmsg ( "click", Coord.z, gob.rc.floor ( posres ), 3, 0, 0, ( int ) gob.id,
                                gob.rc.floor ( posres ), 0, -1 );
                        if ( NUtils.waitEvent ( () -> NFlowerMenu.instance != null, 60 ) ) {
                            for ( FlowerMenu.Petal p : NFlowerMenu.instance.opts ) {
                                if ( !NUtils.checkName ( p.name, opt ) ) {
                                    isChecked = false;
                                    PathFinder pf = new PathFinder ( gui, gob );
//                                    pf.setDisableGrid ( true );
                                    pf.run ();
                                    int size = gui.getInventory ().getFreeSpace ();
                                    new SelectFlowerAction(gob, p.name, SelectFlowerAction.Types.Gob ).run(gui);
//                                    NUtils.waitEvent ( () -> NUtils.getProg()>=0, 20 );
                                    NUtils.waitEvent ( () -> gui.getInventory ().getFreeSpace () != size, 600 );
                                    NUtils.stopWithClick();
                                    String nameForWrite = gob.getres ().name;
                                    if(nameForWrite.contains("bumlings")) {
                                        Pattern pattern = Pattern.compile("(.*?)[0-9]");
                                        Matcher matcher = pattern.matcher(nameForWrite);
                                        if (matcher.find()) {
                                            forWrite.add(matcher.group(1) + '|' + p.name + '\n');
                                        }
                                        keys.add(new Pair<>(matcher.group(1), p.name));
                                        opt.keys.add(p.name);
                                    }else{
                                        forWrite.add ( gob.getres ().name + '|' + p.name + '\n' );
                                        keys.add ( new Pair<> ( gob.getres ().name, p.name ) );
                                        opt.keys.add ( p.name );
                                    }
                                    NFlowerMenu.stop ();
                                    break;
                                }
                            }
                        }
                        NFlowerMenu.stop ();

                    }
                    while ( !isChecked );
                    gobs.remove ( gob );
                }
                NFlowerMenu.stop ();
                NUtils.stopWithClick ();
            }
            catch ( IOException e ) {
                e.printStackTrace ();
            }
        return new Results ( Results.Types.SUCCESS );
    }
    
    public void write(){
        if(writer_is_ready) {
            try {
                for ( String value : forWrite ) {
            
                    writer.write ( value );
            
                }
                writer.close ();
            }
            catch ( IOException e ) {
    
            }
            finally {
                forWrite.clear ();
                writer_is_ready = false;
            }
        }
       
    }
    
    public LPExplorer(NArea area ) {
        this.area = area;
    }
    
    Writer writer;
    boolean writer_is_ready = false;
    ArrayList<String> forWrite;
    NArea area;
}
