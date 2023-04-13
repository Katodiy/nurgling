package nurgling;

import haven.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    boolean isStarted = false;
    String varCand = null;

    public NCharacterInfo(String chrid) {
        this.chrid = chrid;
        path = ((HashDirCache) ResCache.global).base + "\\..\\" +NUtils.getUI().sessInfo.username + "_" + chrid.trim() + ".dat";
        read();
    }

    void read() {

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Files.newInputStream(Paths.get(path)), StandardCharsets.UTF_8))) {
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
        OutputStreamWriter file;
        try  {
            file = new OutputStreamWriter(Files.newOutputStream(Paths.get(path)), StandardCharsets.UTF_8);
            if (!varity.isEmpty()) {
                file.write("varity\t" + String.valueOf(varity.size()) +"\n");
                for (String var : varity) {
                    file.write(var+"\n");
                }
            }
        }  catch (IOException e) {
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
                isStarted = true;
            }
            if(varity.size()>0 && oldFEPSsize == 0 && isStarted)
            {
                varity.clear();
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
