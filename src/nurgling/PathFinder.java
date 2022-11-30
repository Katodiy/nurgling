package nurgling;

import haven.*;
import haven.render.sl.InstancedUniform;
import nurgling.tools.Finder;


import java.io.FileWriter;
import java.util.*;

import static haven.OCache.posres;
import static nurgling.PathFinder.Type.stat;

public class PathFinder {
    public NMap map;

    LinkedList<Vertex> path;
    public Coord2d phantom;
    NHitBox phantom_hitbox;

    class Edge{
        int start = -1;
        int end = -1;
    }
    ArrayList<Edge> edges = new ArrayList<>();
    long ignored_id = 0;
    NGameUI gui;
    private Coord2d endCoord;
    int default_delta = 1;
    Gob target;
    boolean withAlarm = false;


    boolean trellis = false;
    boolean quick = false;
    boolean kritter = false;
    private double cell_half = 1.375;
    private int cell_num = 32;

    int battleCount = 0;
    public void setWithAlarm(boolean withAlarm) {
        this.withAlarm = withAlarm;
    }

    public PathFinder(
            NGameUI gui,
            Coord2d endCoord
    ) {
        this.endCoord = endCoord;
        this.gui = gui;
        this.target = null;
        this.default_delta = 1;
    }

    public PathFinder(
            NGameUI gui,
            Coord2d endCoord,
            boolean quick
    ) {
        this.endCoord = endCoord;
        this.gui = gui;
        this.target = null;
        this.default_delta = 1;
        this.quick = true;
    }

    public PathFinder(
            NGameUI gui,
            Gob targetGob,
            boolean quick
    ) {
        if (NUtils.isIt(targetGob, new NAlias("trellis"))) {
            this.endCoord = NUtils.getTrellisCoord(targetGob.rc);
        } else {
            this.endCoord = targetGob.rc;
        }
        this.gui = gui;
        this.target = targetGob;
        this.default_delta = 1;
        if (NUtils.isIt(targetGob, new NAlias("kritter"))) {
            kritter = true;
        }
        this.quick = true;
    }

    public PathFinder(
            NGameUI gui,
            Gob targetGob
    ) {
        if (NUtils.isIt(targetGob, new NAlias("trellis"))) {
            this.endCoord = NUtils.getTrellisCoord(targetGob.rc);
        } else {
            this.endCoord = targetGob.rc;
        }
        this.gui = gui;
        this.target = targetGob;
        this.default_delta = 1;
        if (NUtils.isIt(targetGob, new NAlias("kritter"))) {
            kritter = true;
        }
    }

    public static enum Type{
        stat,
        dyn
    }
    public PathFinder(
            NGameUI gui,
            Gob targetGob,
            Type moved
    ) {
        this(gui,targetGob);
        type = moved;
    }

    Type type = stat;
    public void ignoreGob(Gob gob) {
        ignored_id = gob.id;
    }

    public void setDefaultDelta(int default_delta) {
        this.default_delta = default_delta;
    }

    public void setPhantom(
            Coord2d phantom,
            NHitBox hitBox
    ) {
        this.phantom = phantom;
        this.phantom_hitbox = hitBox;
        this.phantom_hitbox.correct(phantom, 0);
        this.default_delta = 5;
    }


    public void setTrellis(boolean trellis) {
        this.trellis = trellis;
    }

    public void setBattleRing(
            Coord2d phantom,
            double radius
    ) {
        b_coord = phantom;
        b_rad = radius;
        this.default_delta = 2;
        this.battleMode = true;
    }

    class PFCalculate implements Runnable{
        int start = -1;
        int end = -1;
        HashMap<Integer, Vertex> vertex;
        LinkedList<Vertex> candidates = new LinkedList<>();

        public double length = -1;

        public PFCalculate(int start, int end, HashMap<Integer, Vertex> vertex) {
            this.start = start;
            this.end = end;
            this.vertex = new HashMap<>(vertex.size());
            for(Integer key:vertex.keySet()){
                this.vertex.put(key, new Vertex(vertex.get(key)));
            }
        }

        @Override
        public void run() {
            if(vertex.get(start) == null)
                return;
            vertex.get(start).length = 0;
            Vertex next = vertex.get(start);
            //            do {
            Coord2d start_coord = vertex.get(start).coord;
            if(vertex.get(end)!=null) {
                Coord2d end_coord = vertex.get(end).coord;
                for (Integer key : vertex.keySet()) {
                    Vertex vert = vertex.get(key);
                    vert.distance = Math.min(vert.coord.dist(start_coord), vert.coord.dist(end_coord));
                }
                candidates = new LinkedList<>();
                candidates.add(vertex.get(start));
                do {
                    check(next);
                    candidates.remove(next);

                    candidates.sort(new Comparator<Vertex>() {
                        @Override
                        public int compare(Vertex o1, Vertex o2) {
                            return -Double.compare(o1.length, o2.length);
                        }
                    });
                    Vertex old_next = next;
                    for (Vertex cand : candidates)
                        if (!cand.isVisited || cand.length>next.length) {
                            next = cand;
                        }else {
                            candidates.remove(cand);
                        }
                    if(old_next == next)
                        break;
                }
                while (!vertex.get(end).isVisited && !candidates.isEmpty());

                if (vertex.get(end).length != 10000) {
                    length = vertex.get(end).length;
                }
            }
        }

        void addCandidates(Vertex vert) {
/*            for (Pair<Integer, Double> neighbor : vert.contacts) {
                Vertex cand = vertex.get(neighbor.a);
//                if (!cand.isVisited) {
                    boolean isFind = false;
                    for (Vertex cur_cand : candidates) {
                        if (cur_cand == cand) {
                            isFind = true;
                            break;
                        }
                    }
                    if (!isFind && cand.length>vert.length && cand.length!=10000)
                        candidates.add(cand);
//                }
            }*/
            candidates.sort(comparator);
            candidates.remove(vert);
        }

        private void check(Vertex vert) {
            vert.isVisited = true;
            for (Pair<Integer, Double> neighbor : vert.contacts) {
                Vertex n_vert = vertex.get(neighbor.a);
                if (!n_vert.isVisited) {
                    double new_length = vert.length + neighbor.b;
                    if (n_vert.length > new_length) {
                        n_vert.length = new_length;
                        candidates.add(n_vert);
                    }
                }
            }
        }
    }



    ArrayList<PFCalculate> calculates = new ArrayList<>();

    Gob horse = null;
    void coordFind(
            Coord2d endCoord,
            int icell_num,
            double icell_half
    ) throws InterruptedException {
        cell_num = icell_num;
        cell_half = icell_half;
        HashMap<Integer, Vertex> vertex;
        do {
            do {
                if(checkBattle()){
                    return;
                }
                horseMode = (Finder.findObject(new NAlias("horse"))!= null && gui.map.player().isTag(NGob.Tags.mounted)) ;
                if(horseMode){
                    horse = Finder.findObject(new NAlias("horse"));
                }
                calculateGridSize();


                map = new NMap(gui, ignored_id, endCoord, cell_half, cell_num,
                        (enableWater) ? 1 : enableAllWater ? 2 : 0, horseMode, trellis);
                if (phantom != null) {
                    map.checkGob(phantom_hitbox, new Gob(null, phantom, 0));
                }
                if (battleMode) {
                    map.checkBR(b_coord, b_rad);
                }
                vertex = buildGraph();
            }
            while (!installVertex(vertex,setStartAndEnd()));
            ArrayList<Thread> ths = new ArrayList<>();
            for(PFCalculate item : calculates){
                ths.add( new Thread(item));
            }
            try {

                for (Thread th : ths) {
                    th.start();
                }
                for (Thread th : ths) {
                    th.join();
                }
            }
            catch(InterruptedException e){
            }
            path = new LinkedList<>();

            calculates.sort(new Comparator<PFCalculate>() {
                @Override
                public int compare(PFCalculate o1, PFCalculate o2) {
                    return Double.compare(o1.length,o2.length);
                }
            });
            PFCalculate res = null;
            for(PFCalculate item : calculates){
                if(item.length!=-1 && !item.vertex.get(item.end).contacts.isEmpty()) {
                    res = item;
                    break;
                }
            }
            if(res!=null) {
                for(Integer key : res.vertex.keySet()){
                    if(res.vertex.get(key).isVisited) {
                        boolean isFind = false;
                        for (int i = 0; i < map.cell_num; i++) {
                            for (int j = 0; j < map.cell_num; j++) {
                                if (res.vertex.get(key).coord == map.array[i][j].center) {
                                    map.array[i][j].isVisited = true;
                                    isFind = true;
                                    break;
                                }
                            }
                            if(isFind)
                                break;
                        }
                    }
                }
//                map.print(0,0);
                if (addToPath(res, res.end))
                    break;
            }


        }
        while (true);

        fixPath();
    }


    int cell_max = 256;
    public void printPath(){
        if(phantom!=null){
            gui.msg(phantom_hitbox.toString());
        }
        gui.msg("Start:" + path.get(0).coord.toString());
        gui.msg("End:" + path.get(path.size()-1).coord.toString());
    }

    private void calculateGridSize() {
        double dx = Math.abs(endCoord.x - gui.getMap().player().rc.x)+20;
        double dy = Math.abs(endCoord.y - gui.getMap().player().rc.y)+20;
        cell_half = (!horseMode)?1.375:2.75;
        cell_num *= 1.2;
        while ((cell_num * cell_half * 2 < dx || cell_num * cell_half * 2 < dy) && cell_num < cell_max) {
            cell_num *= 1.2;
        }if(cell_num>cell_max){
            cell_num = cell_max;
        }
    }

    boolean isFree(int i , int j){
        return map.array[i][j].isFree || (map.array[i][j].id.size()==1 && map.array[i][j].id.get(0) == 0);
    }
    
    HashMap<Integer, Vertex>  buildGraph() {
        HashMap<Integer, Vertex> vertex = new HashMap<>();
        for (int i = 0; i < map.array.length; i += 1) {
            for (int j = 0; j < map.array.length; j += 1) {
                int key = i * map.array.length + j;
                if (isFree(i,j)) {
                    vertex.put(key, new Vertex(map.array[i][j].center));
                    Vertex ver = vertex.get(key);
                    if (j > 0) {
                        if (isFree(i,j-1)) {
                            ver.contacts.add(new Pair<>(i * map.array.length + j - 1, 10.));
                            if (i > 0) {
                                if (isFree(i-1,j)) {
                                    ver.contacts.add(new Pair<>((i - 1) * map.array.length + j, 10.));
                                    if (isFree(i-1,j-1)) {
                                        ver.contacts.add(new Pair<>((i - 1) * map.array.length + j - 1, 14.14));
                                    }
                                }
                            }
                            if (i < map.array.length - 1) {
                                if (isFree(i+1,j)) {
                                    ver.contacts.add(new Pair<>((i + 1) * map.array.length + j, 10.));
                                    if (isFree(i+1,j-1)) {
                                        ver.contacts.add(new Pair<>((i + 1) * map.array.length + j - 1, 14.14));
                                    }
                                }
                            }
                        }
                    } else {
                        if (i > 0) {
                            if (isFree(i-1,j)) {
                                ver.contacts.add(new Pair<>((i - 1) * map.array.length + j, 10.));
                            }
                        } else if (i < map.array.length - 1) {
                            if (isFree(i+1,j)) {
                                ver.contacts.add(new Pair<>((i + 1) * map.array.length + j, 10.));
                            }
                        }
                    }
                    if (j < map.array.length - 1) {
                        if (isFree(i,j+1)) {
                            ver.contacts.add(new Pair<>(i * map.array.length + j + 1, 10.));
                            if (i > 0) {
                                if (isFree(i-1,j) && isFree(i-1,j+1)) {
                                    ver.contacts.add(new Pair<>((i - 1) * map.array.length + j + 1, 14.14));
                                }
                            }
                            if (i < map.array.length - 1) {
                                if (isFree(i+1,j) && isFree(i+1,j+1)) {
                                    ver.contacts.add(new Pair<>((i + 1) * map.array.length + j + 1, 14.14));
                                }
                            }
                        }
                    }
                }
            }
        }
        return vertex;
    }

    private Edge setStartAndEnd() throws InterruptedException {
        Edge result = new Edge();
        Coord2d start = gui.map.player().rc;
        for (int i = 0; i < map.array.length; i += 1) {
            for (int j = 0; j < map.array.length; j += 1) {

                if (NMap.checkIn(map.array[i][j], start)) {
                    if (!map.array[i][j].isFree) {
                        if((horseMode && map.array[i][j].id.contains(horse.id))){
                            result.start = map.array.length * i + j;
                        }else {
                            Coord2d objPos = null;
                            if(!map.array[i][j].id.isEmpty() && NUtils.getGob(map.array[i][j].id.get(0))!=null) {
                                for(Long id : map.array[i][j].id) {
                                    if(id!=0) {
                                        objPos = NUtils.getGob(id).rc;
                                        break;
                                    }
                                }
                            }
                            else {
                                if(map.array[i][j].tileCenter.x!=0 && map.array[i][j].tileCenter.y!=0)
                                    objPos = map.array[i][j].tileCenter;
                            }
                            if(objPos == null){
                                result.start = map.array.length * i + j;
                                continue;
                            }
                            Coord2d dir =  gui.map.player().rc.sub(objPos);
                            dir = dir.norm().mul(1.375);
                            Coord2d freePos =  gui.map.player().rc.add(dir);
                            map.addMark(gui.getMap().player().rc, "◈");
                            map.addMark(objPos, "▩");
                            map.addMark(freePos, "◍");
                            map.print();
                            int count = 0;
                            while(!checkVertex(freePos,i,j,result)){
                                freePos = freePos.add(dir);
                                map.addMark(freePos, "◍");
                                count++;
                                if(count==11) {
                                    if(cell_half == 1.375 && cell_num >=cell_max)
                                    {
                                        map.print();
                                        gui.msg("PF FAIL");
                                        throw new InterruptedException();
                                    }
                                    break;
                                }
                            }

                        }
                    }else{
                        result.start = map.array.length * i + j;
                    }
                }
                if (NMap.checkIn(map.array[i][j], endCoord)) {
                    result.end = map.array.length * i + j;
                }
                if(result.end!=-1 && result.start!=-1)
                    return result;
            }
        }
        return result;
    }

    boolean checkVertex(Coord2d freePos, int i, int j, Edge result){
        Coord2d fixed = freePos.sub(map.array[0][0].center);
        Coord f = new Coord((int) (fixed.x/(2*cell_half)), (int)( fixed.y/(2*cell_half)));
        i = f.x; j =f.y;
        for (int ii= Math.max(0, i - 4); ii < Math.min(map.array.length, i + 4); ii += 1) {
            for (int jj = Math.max(0, j - 4); jj < Math.min(map.array.length, j + 4); jj += 1)
            {
                if (NMap.checkIn(map.array[ii][jj], freePos)) {

                    if (isFree(ii,jj)) {
                        result.start = map.array.length * ii + jj;
//                        map.print(ii,jj);
                        return true;
                    }
                    return false;
                }
            }
        }
        return false;
    }

    int checkVertex(
            int i,
            int j
    ) {
        if(i< map.array.length && j< map.array.length && i>0 && j>0) {
            if (horseMode) {
                if (!map.array[i][j].id.isEmpty()) {
                    for (Long id : map.array[i][j].id) {
                        if (id == horse.id) {
                            return 1;
                        }
                    }
                }
            }
            if (target != null ) {
                if (!map.array[i][j].id.isEmpty()) {
                    for (Long id : map.array[i][j].id) {
                        if (id == target.id) {
                            return 0;
                        }
                    }
                    return -1;
                }

            } else if (phantom_hitbox != null || battleMode) {
                if (!map.array[i][j].id.isEmpty()) {
                    for (Long id : map.array[i][j].id) {
                        if (id == 0) {
                            return 0;
                        }
                    }
                    return -1;
                }
            } else if (!map.array[i][j].id.isEmpty()) {
                return 0;
            }
            return 1;
        }else
            return -1;
    }


    private boolean installVertex(
            HashMap<Integer, Vertex> vertex,
            Edge edge
    ) {

        if(edge.start==-1 || edge.end==-1)
            return false;
        calculates.clear();

        int i = edge.end/map.array.length;
        int j = edge.end%map.array.length;
        if(checkVertex(i, j)==1){
            calculates.add(new PFCalculate(edge.start,edge.end,vertex));
            return true;
        }
        int delta = default_delta;
        int fi = i + delta;
        int fj = j;
        while (fi < map.array.length) {
            int res = checkVertex(fi, fj);
            if (res == -1) {
                break;
            }else if (res == 1) {
                calculates.add(new PFCalculate(edge.start,map.array.length*fi+fj,vertex));
                break;
            }
            fi += delta;
        }

        fi = i;
        fj = j + delta;
        while (fj < map.array.length) {
            int res = checkVertex(fi, fj);
            if (res == -1) {
                break;
            }else if (res == 1) {
                calculates.add(new PFCalculate(edge.start,map.array.length*fi+fj,vertex));
                break;
            }
            fj += delta;
        }

        fi = i - delta;
        fj = j;
        while (fi >= 0) {
            int res = checkVertex(fi, fj);
            if (res == -1) {
                break;
            }else if (res == 1) {
                calculates.add(new PFCalculate(edge.start,map.array.length*fi+fj,vertex));
                break;
            }
            fi -= delta;
        }

        fi = i;
        fj = j - delta;
        while (fj >= 0) {
            int res = checkVertex(fi, fj);
            if (res == -1) {
                break;
            }else if (res == 1) {
                calculates.add(new PFCalculate(edge.start,map.array.length*fi+fj,vertex));
                break;
            }
            fj -= delta;
        }

        if (!hardMode) {
            fi = i + delta;
            fj = j + delta;
            while (fi < map.array.length && fj < map.array.length) {
                int res = checkVertex(fi, fj);
                if (res == -1) {
                    break;
                }else if (res == 1) {
                    calculates.add(new PFCalculate(edge.start,map.array.length*fi+fj,vertex));
                    break;
                }
                fi += delta;
                fj += delta;
            }

            fi = i + delta;
            fj = j - delta;
            while (fi < map.array.length && fj >= 0) {
                int res = checkVertex(fi, fj);
                if (res == -1) {
                    break;
                }else if (res == 1) {
                    calculates.add(new PFCalculate(edge.start,map.array.length*fi+fj,vertex));
                    break;
                }
                fi += delta;
                fj -= delta;
            }

            fi = i - delta;
            fj = j - delta;
            while (fi >= 0 && fj >= 0) {
                int res = checkVertex(fi, fj);
                if (res == -1) {
                    break;
                }else if (res == 1) {
                    calculates.add(new PFCalculate(edge.start,map.array.length*fi+fj,vertex));
                    break;
                }
                fi -= delta;
                fj -= delta;
            }

            fi = i - delta;
            fj = j + delta;
            while (fj < map.array.length && fi >= 0) {
                int res = checkVertex(fi, fj);
                if (res == -1) {
                    break;
                }else if (res == 1) {
                    calculates.add(new PFCalculate(edge.start,map.array.length*fi+fj,vertex));
                    break;
                }
                fi -= delta;
                fj += delta;
            }
        }
        return !calculates.isEmpty();
    }



    boolean addToPath(PFCalculate result, int id) {
        Vertex vert = result.vertex.get(id);
        if (vert.contacts.isEmpty()) {
            NUtils.getGameUI().msg("No contacts");
            return false;
        }
        path.addFirst(vert);
        for (Pair<Integer, Double> neighbor : vert.contacts) {
            Vertex n_vert = result.vertex.get(neighbor.a);
            //            if ( neighbor.b != 10 || neighbor.b != 15 ) {
            //                int k = 9;
            //            }
            if (Math.abs(n_vert.length - (vert.length - neighbor.b)) < 1e-5) {
                return addToPath(result, neighbor.a);
            }
        }
        return true;
    }



    void installLines(){
        if(NConfiguration.getInstance().pathCategories.contains(NPathVisualizer.PathCategory.PF)) {
            List<Pair<Coord3f, Coord3f>> lines = new LinkedList<>();
            Iterator<Vertex> vert = path.iterator();
            float z = gui.map.player().getrc().z;
            Coord2d cur = vert.next().coord;
            while (vert.hasNext()) {
                Coord2d next = vert.next().coord;
                lines.add(new Pair<Coord3f, Coord3f>(new Coord3f((float) cur.x, (float) cur.y, z), new Coord3f((float) next.x, (float) next.y, z)));
                cur = next;
            }
            ((NOCache) gui.ui.sess.glob.oc).paths.pflines = lines;
        }
    }
    public void run()
            throws InterruptedException {

        boolean isSuccess = false;
        boolean isReset = false;

        Coord2d currentCoord = (target!=null)?target.rc:null;
        while (!isSuccess) {
            endCoord = (target==null)?endCoord:target.rc;
            coordFind(endCoord, 16, 1.375);
            if(checkBattle())
                return;
            if (!quick) {
                installLines();
                for (Vertex vert : path) {
                    gui.map.wdgmsg("click", Coord.z, vert.coord.floor(posres), 1, 0);
                    int reset_count = 0;
                    do {

                        NUtils.waitEvent(()->NUtils.isPose(gui.map.player(),new NAlias("walk")),10,50);
                        while (gui.map.player().rc.dist(vert.coord) > 2.75 && NUtils.isPose(gui.map.player(), new NAlias("walk"))) {
                            if (withAlarm) {
                                if (NUtils.alarm()) {
                                    /// Тревога валим
                                    NUtils.logOut();
                                    Thread.sleep(10000);
                                    //                                /// Выйти в панике
                                    System.exit(0);
                                }
                                if (NUtils.alarmOrcalot())
                                    return;
                            }
                            Thread.sleep(20);
                        }
                        if (gui.map.player().rc.dist(vert.coord) > 10 && NUtils.isPose(gui.map.player(), new NAlias("idle"))) {
                            reset_count+=1;
                            gui.map.wdgmsg("click", Coord.z, vert.coord.floor(posres), 1, 0);
                        }else{
                            if(gui.map.player().rc.dist(vert.coord) < 10)
                                break;
                        }
                    }while (reset_count<4);
                    if (reset_count==4 || (type== Type.dyn && target!=null && currentCoord.dist(target.rc)>15)) {
                        isReset = true;
                        if(target!=null) {
                            currentCoord = target.rc;
                        }
                        break;
                    }
                }
                if (!isReset) {
                    isSuccess = true;
                } else {
                    isReset = false;
                }
            }
        }
        ((NOCache)gui.ui.sess.glob.oc).paths.pflines = null;
    }

    private boolean checkBattle() {
        battleCount+=1;
        return battleMode && (battleCount>=6 || (endCoord.dist(NUtils.getGameUI().map.player().rc)<=b_rad + 5 && endCoord.dist(NUtils.getGameUI().map.player().rc)>=b_rad));
    }

    void fixPath() {
        double d_x_shift = 0;
        double d_y_shift = 0;

        for (int i = 1; i < path.size(); i++) {
            double new_d_x_shift = path.get(i).coord.x - path.get(i - 1).coord.x;
            double new_d_y_shift = path.get(i).coord.y - path.get(i - 1).coord.y;

            if (Math.abs(new_d_x_shift - d_x_shift) < 0.0001 && Math.abs(new_d_y_shift - d_y_shift) < 0.0001) {
                path.get(i - 1).nedRemove = true;
            } else {
                d_x_shift = new_d_x_shift;
                d_y_shift = new_d_y_shift;
            }
        }
        Iterator<Vertex> it;
        it = path.iterator();
        while (it.hasNext()) {
            Vertex item = it.next();
            if (item.nedRemove) {
                it.remove();
            }
        }
        if (quick) {
            path.get(0).coord = gui.map.player().rc;
        }
    }

    public void closeEvent() {
        path.clear();
    }

    public void enableWater(boolean b) {
        enableWater = b;
    }

    public void enableAllWater(boolean b) {
        enableAllWater = b;
    }

    public static class Vertex {
        boolean nedRemove = false;
        boolean isVisited;
        Vector<Pair<Integer, Double>> contacts;
        double length;
        Coord2d coord;

        double distance;

        Vertex(Coord2d coord) {
            contacts = new Vector<>();
            this.coord = coord;
            isVisited = false;
            length = 10000;
        }


        Vertex(Vertex vertex) {
            contacts = vertex.contacts;
            this.coord = vertex.coord;
            isVisited = false;
            length = 10000;
        }
    }

    Comparator<Vertex> comparator = new Comparator<Vertex>() {
        @Override
        public int compare(
                Vertex o1,
                Vertex o2
        ) {
            return -Double.compare(o1.length + o1.distance, o2.length +o2.distance);
        }
    };

    public void setHorseMode(boolean horseMode) {
        this.horseMode = horseMode;
    }

    boolean enableWater = false;
    boolean enableAllWater = false;

    public void setHardMode(boolean battleMode) {
        this.hardMode = battleMode;
    }

    boolean battleMode = false;
    boolean hardMode = false;
    boolean horseMode = false;
    double b_rad;
    Coord2d b_coord;
}