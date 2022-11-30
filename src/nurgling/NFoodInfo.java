package nurgling;

import haven.*;
import haven.resutil.FoodInfo;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import static haven.CharWnd.Constipations.tflt;
import static haven.PUtils.convolvedown;

public class NFoodInfo extends FoodInfo {
    private final NCharacterInfo.Constipation constipation;

    public NFoodInfo(FoodInfo foodInfo) {
        super(foodInfo.owner, foodInfo.end, foodInfo.glut, foodInfo.evs, foodInfo.efs, foodInfo.types);
        NCharacterInfo.Constipation constipation = null;
        try {
            constipation = owner.context(Session.class).character.constipation;
            if(constipation.chrid == null){
                constipation.chrid = NUtils.getGameUI().chrid;
                NCharacterInfo.Constipation.init();
            }
            if (!constipation.hasRenderer(FoodInfo.class)) {
                constipation.addRenderer(FoodInfo.class, NFoodInfo::renderConstipation);
            }
        } catch (NullPointerException | OwnerContext.NoContext ignore) {
        }
        this.constipation = constipation;
    }

    private static BufferedImage renderConstipation(NCharacterInfo.Constipation.Data data) {
        int h = 14;
        BufferedImage img = data.res.get().layer(Resource.imgc).img;
        String nm = data.res.get().layer(Resource.tooltip).t;
        Color col = CharWnd.color(data.value);
        Text rnm = RichText.render(String.format("%s: $col[%d,%d,%d]{%s%%}", nm, col.getRed(), col.getGreen(), col.getBlue(), Utils.odformat2(100 * data.value, 2)), 0);
        BufferedImage tip = TexI.mkbuf(new Coord(h + 5 + rnm.sz().x, h));
        Graphics g = tip.getGraphics();
        g.drawImage(convolvedown(img, new Coord(h, h), tflt), 0, 0, null);
        g.drawImage(rnm.img, h + 5, ((h - rnm.sz().y) / 2) + 1, null);
        g.dispose();

        return tip;
    }

    @Override
    public BufferedImage tipimg(int w) {
        if (NUtils.getGameUI().chrwdg.feps.els.isEmpty()) {
            if (!constipation.variety_food.isEmpty()) {
                constipation.variety_food.clear();
            }
        }
        String head = String.format("Energy: $col[128,128,255]{%s%%}, Hunger: $col[255,192,128]{%s%%}, Energy/Hunger: $col[128,128,255]{%s%%}", Utils.odformat2(end * 100, 2), Utils.odformat2(glut * 100, 2),Utils.odformat2(end/glut, 2));
        double fepSum = 0;
        for (int i = 0; i < evs.length; i++) {
            fepSum += evs[i].a;
        }
        if (cons != 0)
            head += String.format(", Satiation: $col[192,192,128]{%s%%}", Utils.odformat2(cons * 100, 2));
        BufferedImage base = RichText.render(head, 0).img;
        Collection<BufferedImage> imgs = new LinkedList<BufferedImage>();
        imgs.add(base);
        for (int i = 0; i < evs.length; i++) {
            double probability = evs[i].a / fepSum;
            String fepItemString = String.format("%s (%s%%)", Utils.odformat2(evs[i].a, 2), Utils.odformat2(probability * 100, 2));
            Color col = Utils.blendcol(evs[i].ev.col, Color.WHITE, 0.5);
            imgs.add(catimgsh(5, evs[i].img, RichText.render(String.format("%s: $col[%d,%d,%d]{%s}", evs[i].ev.nm, col.getRed(), col.getGreen(), col.getBlue(), fepItemString), 0).img));
        }
        for (int i = 0; i < efs.length; i++) {
            BufferedImage efi = ItemInfo.longtip(efs[i].info);
            if (efs[i].p != 1)
                efi = catimgsh(5, efi, RichText.render(String.format("$i{($col[192,192,255]{%d%%} chance)}", (int) Math.round(efs[i].p * 100)), 0).img);
            imgs.add(efi);
        }
        imgs.add(RichText.render(String.format("$col[0,255,255]{%s}:", "Extended info"), 0).img);

        double effective = 1;
        for (int type : types) {
            NCharacterInfo.Constipation.Data c = constipation.get(type);
            if (c != null) {
                imgs.add(constipation.render(FoodInfo.class, c));
                effective = Math.min(effective, c.value);
            }
        }
        imgs.add(RichText.render(String.format("FEP Sum: $col[128,255,0]{%s}, FEP/Hunger: $col[128,255,0]{%s}", Utils.odformat2(fepSum, 2), Utils.odformat2(fepSum / (100 * glut), 2)), 0).img);
        if(owner instanceof GItem) {
            imgs.add(RichText.render(String.format("$col[205,125,255]{%s}:", "Calculation"), 0).img);
            double expeted_fep = (((NConfiguration.getInstance().isSubscribed) ? NConfiguration.getInstance().coefSubscribe : (NConfiguration.getInstance().isVerified) ? NConfiguration.getInstance().coefVerif : 0) * fepSum * NUtils.getGameUI().chrwdg.glut.gmod * NUtils.getTableFepModifier() + fepSum * NUtils.getGameUI().chrwdg.glut.gmod * NUtils.getTableFepModifier() * NUtils.getRealmFepModifier()) * effective;

            double cur_fep = 0;
            for (CharWnd.FoodMeter.El el : NUtils.getGameUI().chrwdg.feps.els) {
                cur_fep += el.a;
            }


            if (!constipation.variety_food.contains(((GItem) owner).res.get().name)) {
                double needed_var = (NUtils.getGameUI().chrwdg.feps.cap - Math.sqrt(NConfiguration.getInstance().coefVar * NUtils.getMaxBase() * NUtils.getGameUI().chrwdg.glut.gmod / (constipation.variety_food.size() + 1)) - cur_fep);
                double delta_var = expeted_fep - needed_var;
                if (delta_var < 0)
                    imgs.add(RichText.render(String.format("Expected SFEP($col[0,255,255]{var}): $col[128,255,0]{%.2f} $col[0,196,255]{(%.2f)}", expeted_fep, delta_var), 0).img);
                else
                    imgs.add(RichText.render(String.format("Expected SFEP($col[0,255,255]{var}): $col[128,255,0]{%.2f} $col[255,0,0]{(+%.2f)} ", expeted_fep, delta_var), 0).img);
            } else {
                double needed = NUtils.getGameUI().chrwdg.feps.cap - cur_fep;
                double delta = expeted_fep - needed;
                if (delta < 0)
                    imgs.add(RichText.render(String.format("Expected SFEP: $col[128,255,0]{%.2f} $col[0,196,255]{(%.2f)}", expeted_fep, delta), 0).img);
                else
                    imgs.add(RichText.render(String.format("Expected SFEP: $col[128,255,0]{%.2f} $col[255,0,0]{(+%.2f)}", expeted_fep, delta), 0).img);
            }
            imgs.add(RichText.render(String.format("Expected total: $col[128,255,0]{%.2f}", expeted_fep + cur_fep), 0).img);

            String name;
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
        }
        return (catimgs(0, imgs.toArray(new BufferedImage[0])));
    }
}
