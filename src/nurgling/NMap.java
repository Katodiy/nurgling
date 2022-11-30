package nurgling;

import haven.*;
import nurgling.tools.NArea;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import static haven.MCache.tilesz;


public class NMap {
    // Параметры карты по умолчанию
    public double cell_half;
    public int cell_num;
    public double start_shift = -360;
    // Ячейки карты
    public Cell[][] array;
    public Cell pCell = null;
    public LinkedList<Gob> neighGobs;

    public NMap(
            NGameUI gui,
            long id,
            Coord2d endCoord,
            double icell_half,
            int icell_num,
            int enableWater,
            boolean horseMode,
            boolean trellis
    ) {
        this.enableWater = enableWater;
        Coord2d center = new Coord2d ( ( gui.map.player ().rc.x + endCoord.x ) / 2.,
                ( gui.map.player ().rc.y + endCoord.y ) / 2. );
        
        cell_half = icell_half;
        cell_num = icell_num;
        start_shift = -cell_half * 2 * cell_num * 2;
        array = new Cell[cell_num][cell_num];
        pCell = null;

        Coord sfcoord = (new Coord2d ( center.x , center.y  )).div ( MCache.tilesz ).floor ();
        Coord2d start = new Coord2d ( ( sfcoord ).x * tilesz.x- cell_half/2 * (2*cell_num - 1), ( sfcoord ).y * tilesz.y - cell_half/2 * (2*cell_num - 1));
        Coord sc_fix = start.div ( MCache.tilesz ).floor();
        start = new Coord2d(sc_fix.x* tilesz.x,sc_fix.y* tilesz.y);
        Coord2d coord = new Coord2d (start.x  ,start.y);
        neighGobs = new LinkedList<> ();
        for ( int i = 0 ; i < cell_num ; i++ ) {
            for ( int j = 0 ; j < cell_num ; j++ ) {
                array[i][j] = new Cell ( coord );
                if ( pCell == null ) {
                    if ( ( array[i][j].begin.x < center.x && array[i][j].end.x > center.x ) &&
                            ( array[i][j].begin.y < center.y && array[i][j].end.y > center.y ) ) {
                        pCell = array[i][j];
                    }
                }
                coord.y += cell_half * 2;
            }
            coord.x += cell_half * 2;
            coord.y = start.y;
        }
        
        //        if ( id != -1 ) {
        //            Gob gob = Finder.findObject ( id );
        //            if ( gob != null ) {
        //                checkGob ( gui, NHitBox.get ( gob ), gob.rc, gob.id, gob.a );
        //            }
        //        }

        synchronized ( gui.ui.sess.glob.oc ) {
            for (Gob gob : gui.ui.sess.glob.oc) {
                if (horseMode) {
                    if (NOCache.getBounds(gob.id).contains(NGob.Tags.mounted) || NOCache.getBounds(gob.id).contains(NGob.Tags.lifted))
                        continue;
                }
                if (gob != null && gob != gui.map.player() && gob.id != id && !gob.isTag(NGob.Tags.lifted)) {

                    NHitBox checkedHitBox = NHitBox.get(gob, trellis);
                    if (checkedHitBox != null) {
                        checkGob(checkedHitBox, gob);
                    }
                }
            }
        }
        checkTiles ( gui );
    }
    
    public void print (int ii, int jj){
        try {
            File file = new File("notes3.txt");
            file.createNewFile(); // если файл существует - команда игнорируется
            FileOutputStream fileOutputStream = new FileOutputStream(file, false);
            Writer writer = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);

        for ( int i = 0; i < cell_num ; i++ ) {
            for ( int j = cell_num-1 ; j >= 0 ; j-- ) {
                if(i == ii && j == jj){
                    writer.write( "◉");
                }
                    else {
                        if (array[i][j].isVisited)
                            writer.write("◙");
                        else
                            writer.write((array[i][j].isFree) ? "⊡" : "⊠");
                }

            }
            writer.write ( '\n' );
        }
        writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    class Mark{
        public Coord2d cord;
        public String character;

        public Mark(Coord2d freePos, String val) {
            cord = freePos;
            character = val;
        }
    }
    ArrayList<Mark> marks = new ArrayList<>();
    public void print (){
        try {
            File file = new File("notes3.txt");
            file.createNewFile(); // если файл существует - команда игнорируется
            FileOutputStream fileOutputStream = new FileOutputStream(file, false);
            Writer writer = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);

            for ( int i = 0; i < cell_num ; i++ ) {
                for ( int j = cell_num-1 ; j >= 0 ; j-- ) {
                    for(Mark mark : marks)
                    if(array[i][j].inCell(mark.cord)){
                        writer.write( mark.character);
                    }
                    else {
                        if (array[i][j].isVisited)
                            writer.write("◙");
                        else
                            writer.write((array[i][j].isFree ) ? "⊡" : "⊠");
                    }

                }
                writer.write ( '\n' );
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    void checkTiles ( NGameUI gui ) {
        for ( int i = 0 ; i < cell_num ; i++ ) {
            for ( int j = 0 ; j < cell_num ; j++ ) {
                Coord pltc_beg =
                        ( new Coord2d ( ( array[i][j].begin.x ) / 11, ( array[i][j].begin.y ) / 11 ) ).floor ();
                Coord pltc_end =
                        ( new Coord2d ( ( array[i][j].end.x ) / 11, ( array[i][j].end.y ) / 11 ) ).floor ();
                try {
                    Resource res_beg = gui.ui.sess.glob.map.tilesetr ( gui.ui.sess.glob.map.gettile ( pltc_beg ) );
                    Resource res_end = gui.ui.sess.glob.map.tilesetr ( gui.ui.sess.glob.map.gettile ( pltc_end ) );
                    Coord2d beg = array[i][j].begin.div(tilesz).floor().mul(tilesz).add(tilesz.div(2));;
                    Coord2d end = array[i][j].end.div(tilesz).floor().mul(tilesz).add(tilesz.div(2));
                    if(enableWater == 0) {
                        if ( res_beg.name.contains ( "tiles/cave" ) || res_beg.name.contains ( "tiles/nil" ) || res_beg.name.contains ( "tiles/deep" ) ||
                                res_beg.name.contains ( "tiles/rocks" ) ) {
                            if ( !res_beg.name.contains ( "deeptangle" ) ) {
                                array[i][j].isFree = false;
                                array[i][j].tileCenter = beg;
                            }
                        }
                        else if ( res_beg.name.contains ( "paving" ) ) {
                            array[i][j].isPaved = true;
                        }
                        else if ( res_beg.name.contains ( "water" ) ) {
                            array[i][j].isWater = true;
                        }
    
                        if ( res_end.name.contains ( "tiles/cave" ) || res_end.name.contains ( "tiles/deep" ) || res_end.name.contains ( "tiles/rocks" ) ) {
                            if ( !res_end.name.contains ( "deeptangle" ) ) {
                                array[i][j].isFree = false;
                                array[i][j].tileCenter = end;
                            }
                        }
                        else if ( res_end.name.contains ( "paving" ) ) {
                            array[i][j].isPaved = true;
                        }
                        else if ( res_end.name.contains ( "water" ) ) {
                            array[i][j].isWater = true;
                        }
                    }else if(enableWater == 1) {
                        if ( !NUtils.checkName ( res_beg.name,
                                new NAlias ( new ArrayList<String> ( Arrays.asList ( "tiles/deep", "odeep" ) ),
                                        new ArrayList<String> ( Arrays.asList ( "tan" ) ) ) ) ) {
                            array[i][j].isFree = false;
                            array[i][j].tileCenter = beg;
                        }
                    }
                        else {
                        if ( !NUtils.checkName ( res_beg.name, new NAlias (
                                new ArrayList<String> ( Arrays.asList ( "tiles/deep", "odeep", "water", "owater" ) ),
                                new ArrayList<String> ( Arrays.asList ( "tan" ) ) ) ) ) {
                            array[i][j].isFree = false;
                            array[i][j].tileCenter = beg;
                        }
                    }
                }
                catch ( Resource.Loading | MCache.LoadingMap e  ) {
                
                }
            }
        }
    }
    
    public void checkGob (
            NHitBox hitBox,
            Gob gob
    ) {
//        System.out.println ( ( gob.getres ().name ) + gob.rc );
//        for ( Coord2d coord2d : hitBox.coord2ds ) {
//            System.out.println ( coord2d );
//        }

        int i_center = (int)Math.floor((gob.rc.x - array[0][0].begin.x )/(2*cell_half));
        int j_center = (int)Math.floor((gob.rc.y - array[0][0].begin.y)/(2*cell_half));
        int shift = (int)Math.ceil(hitBox.radius/(2*cell_half) + 1);
        for ( int i = Math.max(0, i_center - shift) ; i < Math.min(cell_num, i_center + shift) ; i++ ) {
            for (int j = Math.max(0, j_center - shift) ; j < Math.min(cell_num, j_center + shift) ; j++ ) {
                if ( hitBox.calcCross ( array[i][j] ) && gob.id!=NUtils.getGameUI().getMap().player().id ) {
                    array[i][j].isFree = false;
                    array[i][j].id.add ( gob.id );
                }
            }
        }
    }
    
    public void checkBR (
            Coord2d coord,
            double rad
    ) {
        for ( int i = 0 ; i < cell_num ; i++ ) {
            for ( int j = 0 ; j < cell_num ; j++ ) {
                if ( array[i][j].center.dist ( coord ) < rad ) {
                    array[i][j].isFree = false;
                    array[i][j].id.add ( 0L );
                }
            }
        }
    }
    
    
    public static boolean checkIn (
            Cell cell,
            Coord2d coord
    ) {
        return ( cell.begin.x <= coord.x && cell.begin.y <= coord.y ) &&
                ( cell.end.x > coord.x && cell.end.y > coord.y );
    }

    public void addMark(Coord2d freePos, String val) {
        marks.add(new Mark(freePos,val));
    }

    /**
     * Класс ячейки карты
     */
    public class Cell extends NArea {
        Coord2d tileCenter = new Coord2d();
        /// Тип ячейки
        boolean isFree;
        boolean isPaved;
        boolean isWater;
        boolean isVisited = false;
        /// Массив объектов в ячейке
        public ArrayList<Long> id = new ArrayList<> ();
        
        public Cell ( Coord2d center ) {
            isFree = true;
            isPaved = false;
            isWater = false;
            this.center = new Coord2d ( center.x, center.y );
            this.begin = new Coord2d ( center.x - cell_half, center.y - cell_half );
            this.end = new Coord2d ( center.x + cell_half, center.y + cell_half );
            coord2ds[0] =  begin ;
            coord2ds[1] = new Coord2d ( begin.x, end.y );
            coord2ds[2] = end;
            coord2ds[3] = new Coord2d ( end.x, begin.y );
        }
        
        /**
         * Проверка принадлежности координаты ячейке
         *
         * @param coord Координата
         * @return Принадлежность ячейке
         */
        public boolean inCell ( Coord coord ) {
            return ( coord.x >= center.x - cell_half && coord.x <= center.x + cell_half ) &&
                    ( coord.y >= center.y - cell_half && coord.y <= center.y + cell_half );
        }

        public boolean inCell ( Coord2d coord ) {
            return ( coord.x >= center.x - cell_half && coord.x <= center.x + cell_half ) &&
                    ( coord.y >= center.y - cell_half && coord.y <= center.y + cell_half );
        }
    }
    
    int enableWater = 0;
}