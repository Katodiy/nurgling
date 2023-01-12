package nurgling;

import haven.Audio;
import haven.Resource;

import java.util.HashMap;

public class NAlarmManager {
    
    private static HashMap<String, Alarm> alarms = new HashMap<> ();
    
    public static void init () {
        load ();
    }
    
    public static boolean play (
            String resname
    ) {
        for(String key : alarms.keySet ()){
            if(resname.contains ( key )){
                alarms.get(key).play ();
                return true;
            }
        }
            return false;
    }

    public static void load () {
        //alarms.clear ();
        //alarms.put ( "bear", new Alarm ( path + "bear.wav", 5, false ) );
        //alarms.put ( "boar", new Alarm ( path + "boar.wav", 5, false ) );
        //alarms.put ( "lynx", new Alarm ( path + "lynx.wav", 5, false ) );
        //alarms.put ( "caveangler", new Alarm ( path + "lynx.wav", 5, false ) );
        //alarms.put ( "troll", new Alarm ( path + "troll.wav", 5, false ) );
        //alarms.put ( "mammoth", new Alarm ( path + "mammoth.wav", 5, false ) );
        //alarms.put ( "wolf", new Alarm ( path + "wolf.wav", 5, false ) );
        //alarms.put ( "eagle", new Alarm ( path + "eagle.wav", 5, false ) );
    }
    
    public static class Alarm {
        public Resource res;
        public int volume;

        public Alarm (
                Resource res,
                int volume
        ) {
            this.res = res;
            this.volume = volume;
        }
        
        public void play () {
            Audio.play(res);
        }
    }
}