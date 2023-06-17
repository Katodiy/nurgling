package nurgling;

import haven.Audio;
import haven.Resource;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class NAlarmManager {
    static NGob.Tags last;
    static long startTick = 0;
    static final long duration = 1000;
    static private final ReentrantLock mutex = new ReentrantLock();

    private static HashMap<NGob.Tags, String> alarms = new HashMap<NGob.Tags, String>();

    private static void init(){
        alarms.put(NGob.Tags.bear,"alarm/bear");
        alarms.put(NGob.Tags.wolf,"alarm/wolf");
        alarms.put(NGob.Tags.greyseal,"alarm/greyseal");
        alarms.put(NGob.Tags.mammoth,"alarm/mammoth");
        alarms.put(NGob.Tags.orca,"alarm/orca");
        alarms.put(NGob.Tags.spermwhale,"alarm/spermwhale");
        alarms.put(NGob.Tags.quest,"alarm/quest");
        alarms.put(NGob.Tags.foe,"alarm/white");
        alarms.put(NGob.Tags.unknown,"alarm/white");
        alarms.put(NGob.Tags.winter_stoat,"alarm/stoat");
        alarms.put(NGob.Tags.stalagoomba,"alarm/stalagoomba");
        alarms.put(NGob.Tags.troll,"alarm/troll");


    }

    public static void play (
            NGob.Tags tag
    ) {
        mutex.lock();
        if (startTick > NUtils.getTickId())
            startTick = 0;
        if (last != tag || NUtils.getTickId() - startTick > duration) {
            if (alarms.isEmpty())
                init();
            Audio.play(Audio.fromres(Resource.local().loadwait(alarms.get(tag))));
            last = tag;
            startTick = NUtils.getTickId();
        }
        mutex.unlock();
    }
}