package nurgling;

import haven.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class NCharacterInfo extends Widget {

//    public final Constipation constipation = new Constipation();

    String chrid;
    String path;
    final Set<String> varity = new HashSet<>();
    CharWnd charWnd = null;
    long lastWriting = 0;
    long delta = 300;

    double oldFEPSsize = 0;
    boolean needFEPreset = false;

    String varCand = null;

    public NCharacterInfo(String chrid) {
        this.chrid = chrid;
        path = ((HashDirCache) ResCache.global).base + "\\..\\" + chrid.strip() + ".dat";
        read();
    }

    void read() {

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.contains("varity")) {
                    synchronized (varity) {
                        for (int i = 0; i < Integer.parseInt(line.split("\t")[1]); i++) {
                            varity.add(reader.readLine());
                        }
                    }
                }
            }
            reader.close();
        } catch (IOException ignored) {
        }
    }

    void write() {
        try (FileWriter file = new FileWriter(path)) {
            if (!varity.isEmpty()) {
                file.write("varity\t" + String.valueOf(varity.size()) +"\n");
                for (String var : varity) {
                    file.write(var+"\n");
                }
            }
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCharWnd(CharWnd charWnd) {
        oldFEPSsize = calcFEPsize(charWnd);
        this.charWnd = charWnd;
    }

    private double calcFEPsize(CharWnd charWnd)
    {
        double len = 0;
        for (CharWnd.FoodMeter.El el : charWnd.feps.els){
            len+=el.a;
        }
        return len;
    }

    @Override
    public void tick(double dt) {
        super.tick(dt);
        if(charWnd!=null) {
            double fepssize = calcFEPsize(charWnd);
            if(Math.abs(oldFEPSsize-fepssize)>0.005) {
                if (varity.size() > 0 && fepssize==0) {
                    varity.clear();

                    oldFEPSsize = 0;
                }
                else
                {
                    if (varCand != null) {
                        varity.add(varCand);
                    }
                    oldFEPSsize = fepssize;
                }
                needFEPreset = true;
            }
            if (NUtils.getTickId() - lastWriting > delta && needFEPreset) {
                write();
                lastWriting = NUtils.getTickId();
                needFEPreset = false;
                oldFEPSsize = fepssize;
            }
        }
    }

    public void setCandidate(String defn) {
        varCand = defn;
    }
}
