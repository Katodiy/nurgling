package nurgling;

import haven.*;
import haven.resutil.FoodInfo;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

import static haven.CharWnd.Constipations.elh;
import static haven.CharWnd.Constipations.tflt;
import static haven.CharWnd.iconfilter;
import static haven.PUtils.convolvedown;

public class NFoodInfo extends FoodInfo implements GItem.OverlayInfo<Tex>, NSearchable {
    public static boolean show = false;
    static double coefSubscribe = 1.5;
    static double coefVerif = 1.2;

    static double coefVar = 0.3999;
    String name;
    String s_end;
    String s_glut;
    String s_end_glut;
    double fepSum = 0;

    double efficiency = 100;

    boolean isVarity;

    double expeted_fep;
    double needed;
    public boolean needToolTip = false;
    double delta = 0;
    HashMap<String, Double> searchImage = new HashMap<>();
    public NFoodInfo(Owner owner, double end, double glut, double cons, Event[] evs, Effect[] efs, int[] types) {
        super(owner, end, glut, cons, evs, efs, types);
        s_end = Utils.odformat2(end * 100, 2);
        s_glut = Utils.odformat2(glut * 100, 2);
        s_end_glut = Utils.odformat2(end / glut, 2);
        for (Event event : evs) {
            fepSum += event.a;
        }
        for (Event ev : evs) {
            double probability = ev.a / fepSum;
            fepVis.add(new FepVis(ev.img, ev.ev.nm, String.format("%s (%s%%)", Utils.odformat2(ev.a, 2), Utils.odformat2(probability * 100, 2)), Utils.blendcol(ev.ev.col, Color.WHITE, 0.5)));
            searchImage.put(fep_map.get(ev.ev.nm),probability*100);
        }
        if (owner instanceof NGItem) {
            name = ((NGItem) owner).name();
            NCharacterInfo ci = NUtils.getGameUI().getCharInfo();
            if (ci != null) {
                isVarity = !ci.varity.contains(name);
            }
            if (NUtils.getGameUI().chrwdg != null) {
                if (NUtils.getGameUI().chrwdg.cons.els.size() > 0) {
                    for (int type : types) {
                        CharWnd.Constipations.El c = NUtils.getGameUI().chrwdg.cons.els.get(type);
                        if (c != null) {
                            efficiency = c.a * 100;
                        }
                    }
                }
            }
            calcData();
        }
    }

    double calcExpectedFep() {
        if (nurgling.NUtils.getGameUI().chrwdg != null && NUtils.getUI().sessInfo != null) {
            return (((NUtils.getUI().sessInfo.isSubscribed) ? coefSubscribe : (NUtils.getUI().sessInfo.isVerified) ? coefVerif : 1) * fepSum * NUtils.getGameUI().chrwdg.glut.gmod * NUtils.getGameUI().getTableMod() + fepSum * NUtils.getGameUI().chrwdg.glut.gmod * NUtils.getGameUI().getTableMod() * NUtils.getGameUI().getRealmMod()) * efficiency / 100;
        }
        return 0;
    }

    double calcNeededFep() {
        if (nurgling.NUtils.getGameUI().chrwdg != null) {
            double cur_fep = 0;
            for (CharWnd.FoodMeter.El el : NUtils.getGameUI().chrwdg.feps.els) {
                cur_fep += el.a;
            }
            if (isVarity) {
                return (NUtils.getGameUI().chrwdg.feps.cap - Math.sqrt(coefVar * NUtils.getGameUI().getMaxBase() * NUtils.getGameUI().chrwdg.glut.gmod / (NUtils.getGameUI().getCharInfo().varity.size() + 1)) - cur_fep);
            } else {
                return NUtils.getGameUI().chrwdg.feps.cap - cur_fep;
            }
        }
        return 0;
    }

    public boolean check() {
        if (NUtils.getGameUI().chrwdg != null) {

            NCharacterInfo ci = NUtils.getGameUI().getCharInfo();
            if (ci != null) {
                if (name == null) {
                    name = ((NGItem) owner).name();
                }
                boolean res = !(isVarity == !ci.varity.contains(name));
                if (res) {
                    needToolTip = true;
                    return true;
                }
            }
            if (NUtils.getGameUI().chrwdg.cons.els.size() > 0) {
                for (int type : types) {
                    CharWnd.Constipations.El c = NUtils.getGameUI().chrwdg.cons.els.get(type);
                    if (efficiency != ((c != null) ? Math.min(100, c.a * 100) : 100)) {
                        needToolTip = true;
                        return true;
                    }
                }
            }
            if (expeted_fep != calcExpectedFep() || needed != calcNeededFep()) {
                expeted_fep = calcExpectedFep();
                needed = calcNeededFep();
                needToolTip = true;
            }
        }
        return false;
    }
    final static HashMap<String, String> fep_map = new HashMap<>();
    static void init()
    {
        synchronized (fep_map) {
            if (fep_map.isEmpty()) {
                fep_map.put( "Strength +2","str2");
                fep_map.put("Strength +1","str");
                fep_map.put( "Agility +2","agi2");
                fep_map.put("Agility +1","agi");
                fep_map.put("Constitution +1","con");
                fep_map.put( "Constitution +2","con2");
                fep_map.put( "Perception +2","per2");
                fep_map.put("Perception +1","per");
                fep_map.put("Will +1","wil");
                fep_map.put( "Will +2","wil2");
                fep_map.put("Psyche +1","psy");
                fep_map.put( "Psyche +2","psy2");
                fep_map.put("Intelligence +1","int");
                fep_map.put( "Intelligence +2","int2");
                fep_map.put("Dexterity +1","dex");
                fep_map.put( "Dexterity +2","dex2");
                fep_map.put("Charisma +1","csm");
                fep_map.put( "Charisma +2","csm");
            }
        }
    }


    @Override
    public boolean search() {
        if(name!=null) {
            calcData();
            NGameUI.SearchItem si = NUtils.getGameUI().itemsForSearch;
            if(!si.food.isEmpty()) {
                for (NGameUI.SearchItem.Stat fep : NUtils.getGameUI().itemsForSearch.food) {
                    if (searchImage.get(fep.v) == null || (fep.a!=0 && !(fep.isMore == (searchImage.get(fep.v) > fep.a))))
                        return false;
                }
                if (si.fgs)
                    return (delta > 0);
                if(!NUtils.getGameUI().itemsForSearch.name.isEmpty())
                {
                    return name.toLowerCase().contains(NUtils.getGameUI().itemsForSearch.name.toLowerCase());
                }
                return true;
            }
            if (si.fgs)
                return (delta > 0);
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

    public ArrayList<BufferedImage> fepImg() {
        if (fepImgs == null) {
            fepImgs = new ArrayList<>();
            for (FepVis value : fepVis) {
                fepImgs.add(catimgsh(5, value.img, RichText.render(String.format("%s: $col[%d,%d,%d]{%s}", value.nm, value.col.getRed(), value.col.getGreen(), value.col.getBlue(), value.str), 0).img));
            }
        }
        return fepImgs;
    }

    ArrayList<BufferedImage> effImgs = null;

    public ArrayList<BufferedImage> effImg() {
        if (effImgs == null) {
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

    public BufferedImage extentTitle() {
        if (extentTitle == null) {
            extentTitle = RichText.render(String.format("$col[0,255,255]{%s}:", "Extended info"), 0).img;
        }
        return extentTitle;
    }

    HashMap<Integer, BufferedImage> consImgs = new HashMap<>();

    BufferedImage getConsImg(int value) {
        if (consImgs.get(value) == null) {
            CharWnd.Constipations.El c = NUtils.getGameUI().chrwdg.cons.els.get(value);
            if (c != null)
                consImgs.put(value, convolvedown(new ItemSpec(OwnerContext.uictx.curry(NUtils.getUI()), c.t, null).image(), new Coord(elh, elh), tflt));
        }
        return consImgs.get(value);
    }

    void calcData()
    {
        if(name!=null) {
            expeted_fep = calcExpectedFep();
            needed = calcNeededFep();
            delta = expeted_fep - needed;
        }
    }
    @Override
    public BufferedImage tipimg(int w) {
        needToolTip = false;
        Collection<BufferedImage> imgs = new LinkedList<BufferedImage>();
        imgs.add(headImg());
        imgs.addAll(fepImg());
        imgs.addAll(effImg());
        imgs.add(extentTitle());
        for (int type : types) {
            CharWnd.Constipations.El c = NUtils.getGameUI().chrwdg.cons.els.get(type);
            if (c != null) {
                efficiency = c.a * 100;
                imgs.add(catimgsh(5, getConsImg(type), RichText.render(String.format("\tEfficiency: $col[255,%d,0]{%s}%%", Math.round(255 * efficiency / 100), Utils.odformat2(efficiency, 2)), 0).img));
            }
        }
        imgs.add(RichText.render(String.format("FEP Sum: $col[128,255,0]{%s}, FEP/Hunger: $col[128,255,0]{%s}", Utils.odformat2(fepSum, 2), Utils.odformat2(fepSum / (100 * glut), 2)), 0).img);

        if (name != null) {
            calcData();
            imgs.add(RichText.render(String.format("$col[205,125,255]{%s}:", "Calculation"), 0).img);


            double error = expeted_fep*0.005;
            if (delta < 0)
                imgs.add(RichText.render(String.format("Expected FEP: $col[128,255,0]{%.2f} $col[0,196,255]{(%.2f \u00B1 %.2f)}", expeted_fep, delta, error), 0).img);
            else
                imgs.add(RichText.render(String.format("Expected FEP: $col[128,255,0]{%.2f} $col[255,0,0]{(+%.2f \u00B1 %.2f)} ", expeted_fep, delta, error), 0).img);
            double cur_fep = 0;
            for (CharWnd.FoodMeter.El el : NUtils.getGameUI().chrwdg.feps.els) {
                cur_fep += el.a;
            }
            imgs.add(RichText.render(String.format("Expected total: $col[128,255,0]{%.2f}", expeted_fep + cur_fep), 0).img);

            if (NUtils.getUI().dataTables.data_food!= null && NUtils.getUI().dataTables.data_food.containsKey(name)) {
                drinkImg = drinkImg();
                if(!drinkImg.isEmpty()) {
                    imgs.add(RichText.render(String.format("$col[175,175,255]{%s}:", "Drink info"), 0).img);
                }
                imgs.addAll(drinkImg());
            }
        }
        return (catimgs(0, imgs.toArray(new BufferedImage[0])));
    }

    ArrayList<BufferedImage> drinkImg = null;
    private ArrayList<BufferedImage> drinkImg() {
        if(drinkImg==null && NUtils.getUI().dataTables.data_food.get(name)!=null) {
            drinkImg = new ArrayList<>();
            for (String type : NUtils.getUI().dataTables.data_food.get(name)) {
                if (NUtils.getUI().dataTables.data_drinks.get(type) != null) {
                    Iterator<String> iter = NUtils.getUI().dataTables.data_drinks.get(type).iterator();
                    BufferedImage img = null;
                    while (iter.hasNext()) {
                        String drink = iter.next();
                        String vessel = (NUtils.getUI().dataTables.data_vessel.getOrDefault(drink, ""));
                        if (vessel == null)
                            vessel = "Any";
                        img = RichText.render(String.format("%s$col[192,255,192]{%s}:", "\t", type), 0).img;
                        img = catimgsh(5, img, RichText.render(String.format("$col[255,255,128]{%s} (%s)", drink, vessel), 0).img);
                        img = catimgsh(5, img, convolvedown(Resource.loadsimg(NUtils.getUI().dataTables.vessel_res.get(vessel)), UI.scale(new Coord(16, 16)), iconfilter));
                        drinkImg.add(img);
                    }
                }
            }
        }
        return drinkImg;
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
                calcData();
            }
            if(!ci.varity.contains(name))
                return var_img;
        }
        return null;
    }

    @Override
    public void drawoverlay(GOut g, Tex data)
    {
        if(show && data!=null) {
            g.aimage(data, data.sz(), 1, 1);
        }
    }
}
