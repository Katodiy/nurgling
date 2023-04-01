package nurgling;

import haven.*;
import haven.resutil.FoodInfo;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import static haven.CharWnd.Constipations.elh;
import static haven.CharWnd.Constipations.tflt;
import static haven.PUtils.convolvedown;

public class NFoodInfo extends FoodInfo implements GItem.OverlayInfo<Tex> {
    String name;
    String s_end;
    String s_glut;
    String s_end_glut;
    double fepSum = 0;

    double efficiency = 100;

    boolean isVarity;

    public boolean needToolTip = false;
    public NFoodInfo(Owner owner, double end, double glut, double cons, Event[] evs, Effect[] efs, int[] types) {
        super(owner, end, glut, cons, evs, efs, types);
        s_end = Utils.odformat2(end * 100, 2);
        s_glut = Utils.odformat2(glut * 100, 2);
        s_end_glut = Utils.odformat2(end/glut, 2);
        for (Event event : evs) {
            fepSum += event.a;
        }
        for (Event ev : evs) {
            double probability = ev.a / fepSum;
            fepVis.add(new FepVis(ev.img, ev.ev.nm, String.format("%s (%s%%)", Utils.odformat2(ev.a, 2), Utils.odformat2(probability * 100, 2)), Utils.blendcol(ev.ev.col, Color.WHITE, 0.5)));
        }
        name = ((NGItem)owner).name();
        NCharacterInfo ci = NUtils.getGameUI().getCharInfo();
        if(ci!=null) {
            isVarity = !ci.varity.contains(name);
        }
        if(NUtils.getGameUI().chrwdg!=null) {
            if(NUtils.getGameUI().chrwdg.cons.els.size()>0) {
                for (int type : types) {
                    CharWnd.Constipations.El c = NUtils.getGameUI().chrwdg.cons.els.get(type);
                    if (c != null) {
                        efficiency = c.a * 100;
                    }
                }
            }
        }
    }

    public boolean check() {
        if(NUtils.getGameUI().chrwdg!=null) {
            if (NUtils.getGameUI().chrwdg.cons.els.size() > 0) {
                for (int type : types) {
                    CharWnd.Constipations.El c = NUtils.getGameUI().chrwdg.cons.els.get(type);
                    if (efficiency != ((c != null) ? Math.min(100, c.a * 100) : 100)) {
                        needToolTip = true;
                        return true;
                    }
                }
                NCharacterInfo ci = NUtils.getGameUI().getCharInfo();
                if (ci != null) {
                    if(name == null)
                    {
                        name = ((NGItem)owner).name();
                    }
                    boolean res = !(isVarity == !ci.varity.contains(name));
                    if(res)
                    {
                        needToolTip = true;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    class FepVis {
        BufferedImage img;
        String nm;
        String str;
        Color col;

        public FepVis(BufferedImage img, String nm, String str, Color col) {
            this.img = img;
            this.nm = nm;
            this.str = str;
            this.col = col;
        }
    }

    ArrayList<FepVis> fepVis = new ArrayList<>();

    public NFoodInfo(Owner owner, double end, double glut, Event[] evs, Effect[] efs, int[] types) {
        this(owner, end, glut, 0, evs, efs, types);
    }

    BufferedImage headImg = null;
    public BufferedImage headImg() {
        if (headImg == null) {
            String head = String.format("Energy: $col[128,128,255]{%s%%}, Hunger: $col[255,192,128]{%s%%}, Energy/Hunger: $col[128,128,255]{%s%%}", s_end, s_glut, s_end_glut);

            headImg = RichText.render(head, 0).img;
        }
        return headImg;
    }

    ArrayList<BufferedImage> fepImgs = null;
    public ArrayList<BufferedImage> fepImg(){
        if(fepImgs==null) {
            fepImgs = new ArrayList<>();
            for (FepVis value : fepVis) {
                fepImgs.add(catimgsh(5, value.img, RichText.render(String.format("%s: $col[%d,%d,%d]{%s}", value.nm, value.col.getRed(), value.col.getGreen(), value.col.getBlue(), value.str), 0).img));
            }
        }
        return fepImgs;
    }

    ArrayList<BufferedImage> effImgs = null;
    public ArrayList<BufferedImage> effImg(){
        if(effImgs==null) {
            effImgs = new ArrayList<>();
            for (Effect ef : efs) {
                BufferedImage efi = ItemInfo.longtip(ef.info);
                if (ef.p != 1)
                    efi = catimgsh(5, efi, RichText.render(String.format("$i{($col[192,192,255]{%d%%} chance)}", (int) Math.round(ef.p * 100)), 0).img);
                effImgs.add(efi);
            }
        }
        return effImgs;
    }

    BufferedImage extentTitle = null;
    public BufferedImage extentTitle(){
        if(extentTitle==null) {
            extentTitle = RichText.render(String.format("$col[0,255,255]{%s}:", "Extended info"), 0).img;
        }
        return extentTitle;
    }

    HashMap<Integer,BufferedImage> consImgs = new HashMap<>();

    BufferedImage getConsImg(int value){
        if(consImgs.get(value)==null){
            CharWnd.Constipations.El c = NUtils.getGameUI().chrwdg.cons.els.get(value);
            if(c!=null)
                consImgs.put(value, convolvedown(new ItemSpec(OwnerContext.uictx.curry(NUtils.getUI()), c.t, null).image(), new Coord(elh, elh), tflt));
        }
        return consImgs.get(value);
    }

    @Override
    public BufferedImage tipimg(int w) {
        needToolTip = false;
        Collection<BufferedImage> imgs = new LinkedList<BufferedImage>();
        imgs.add( headImg());
        imgs.addAll(fepImg());
        imgs.addAll(effImg());
        imgs.add(extentTitle());
        for (int type : types) {
            CharWnd.Constipations.El c = NUtils.getGameUI().chrwdg.cons.els.get(type);
            if(c!=null) {
                efficiency = c.a * 100;
                imgs.add(catimgsh(5, getConsImg(type), RichText.render(String.format("\tEfficiency: $col[255,%d,0]{%s}%%", Math.round(255 * efficiency / 100), Utils.odformat2(efficiency, 2)), 0).img));
            }
        }
        imgs.add(RichText.render(String.format("FEP Sum: $col[128,255,0]{%s}, FEP/Hunger: $col[128,255,0]{%s}", Utils.odformat2(fepSum, 2), Utils.odformat2(fepSum / (100 * glut), 2)), 0).img);

        imgs.add(RichText.render(String.format("$col[205,125,255]{%s}:", "Calculation"), 0).img);
//        double expeted_fep = (((NConfiguration.getInstance().isSubscribed) ? NConfiguration.getInstance().coefSubscribe : (NConfiguration.getInstance().isVerified) ? NConfiguration.getInstance().coefVerif : 0) * fepSum * NUtils.getGameUI().chrwdg.glut.gmod * NUtils.getTableFepModifier() + fepSum * NUtils.getGameUI().chrwdg.glut.gmod * NUtils.getTableFepModifier() * NUtils.getRealmFepModifier()) * efficiency;

//        double cur_fep = 0;
//        for (CharWnd.FoodMeter.El el : NUtils.getGameUI().chrwdg.feps.els) {
//            cur_fep += el.a;
//        }


//        if (isVarity) {
//            double needed_var = (NUtils.getGameUI().chrwdg.feps.cap - Math.sqrt(NConfiguration.getInstance().coefVar * NUtils.getMaxBase() * NUtils.getGameUI().chrwdg.glut.gmod / (NUtils.getGameUI().getCharInfo().varity.size() + 1)) - cur_fep);
//            double delta_var = expeted_fep - needed_var;
//            if (delta_var < 0)
//                imgs.add(RichText.render(String.format("Expected SFEP($col[0,255,255]): $col[128,255,0]{%.2f} $col[0,196,255]{(%.2f)}", expeted_fep, delta_var), 0).img);
//            else
//                imgs.add(RichText.render(String.format("Expected SFEP($col[0,255,255]): $col[128,255,0]{%.2f} $col[255,0,0]{(+%.2f)} ", expeted_fep, delta_var), 0).img);
//        } else {
//            double needed = NUtils.getGameUI().chrwdg.feps.cap - cur_fep;
//            double delta = expeted_fep - needed;
//            if (delta < 0)
//                imgs.add(RichText.render(String.format("Expected SFEP: $col[128,255,0]{%.2f} $col[0,196,255]{(%.2f)}", expeted_fep, delta), 0).img);
//            else
//                imgs.add(RichText.render(String.format("Expected SFEP: $col[128,255,0]{%.2f} $col[255,0,0]{(+%.2f)}", expeted_fep, delta), 0).img);
//        }
//        imgs.add(RichText.render(String.format("Expected total: $col[128,255,0]{%.2f}", expeted_fep + cur_fep), 0).img);

//            String name;
//            if ((name = NUtils.getInfo((GItem) owner)) != null) {
//                if (NConfiguration.getInstance().data_food.containsKey(name)) {
//                    imgs.add(RichText.render(String.format("$col[175,175,255]{%s}:", "Drink info"), 0).img);
//                    for (String type : NConfiguration.getInstance().data_food.get(name)) {
////                    BufferedImage title = catimgs(3,img, RichText.render(String.format("%s$col[192,255,192]{%s}:","\t", type), 0).img);
//                        Iterator<String> iter = NConfiguration.getInstance().data_drinks.get(type).iterator();
//                        StringBuilder drinkInfo = new StringBuilder();
//                        while (iter.hasNext()) {
//                            String drink = iter.next();
//                            String vessel = (NConfiguration.getInstance().data_vessel.getOrDefault(drink, ""));
//                            if (vessel == null)
//                                vessel = "Any";
//                            drinkInfo.append(String.format("$col[255,255,128]{%s} (%s)", drink, vessel));
//                            if (iter.hasNext()) {
//                                drinkInfo.append("$col[0,255,255]{ | }");
//                            }
//                        }
//                        imgs.add(RichText.render(drinkInfo.toString(), 0).img);
//
//                    }
//                }
//            }
//        }
        return (catimgs(0, imgs.toArray(new BufferedImage[0])));
    }

    public static Tex var_img = Resource.loadtex("overlays/items/varity");
    @Override
    public Tex overlay() {
        NCharacterInfo ci = NUtils.getGameUI().getCharInfo();
        if(ci!=null)
        {
            isVarity = !ci.varity.contains(name);
            for (int type : types) {
                CharWnd.Constipations.El c = NUtils.getGameUI().chrwdg.cons.els.get(type);
                if(c!=null) {
                    efficiency = c.a * 100;
                    needToolTip = true;
                }
            }
            if(name==null)
            {
                name = ((NGItem)owner).name();
            }
            if(!ci.varity.contains(name))
                return var_img;
        }
        return null;
    }

    @Override
    public void drawoverlay(GOut g, Tex data)
    {
        if(data!=null) {
            g.aimage(data, data.sz(), 1, 1);
        }
    }
}
