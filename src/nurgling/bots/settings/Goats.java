package nurgling.bots.settings;

import haven.CheckBox;
import haven.Label;
import nurgling.NConfiguration;
import nurgling.NEntryListSet;
import nurgling.NSettinsSetD;
import nurgling.NSettinsSetI;

import java.util.ArrayList;

public class Goats extends Settings {
    NEntryListSet els;
    NSettinsSetI totalAdult;
    NSettinsSetI gap;
    NSettinsSetD milkQuality;
    NSettinsSetD meatQuality;
    NSettinsSetD woolQuality;
    NSettinsSetD hideQuality;
    NSettinsSetD meatq1;
    NSettinsSetD meatq2;
    NSettinsSetD meatqth;
    NSettinsSetD milk1;
    NSettinsSetD milk2;
    NSettinsSetD milkth;
    NSettinsSetD wool1;
    NSettinsSetD wool2;
    NSettinsSetD woolth;
    NSettinsSetD coverbreed;
    CheckBox ic;
    CheckBox dk;
    public Goats() {
        prev = els = add(new NEntryListSet(NConfiguration.getInstance().goatsHerd.keySet()) {
            @Override
            public void nsave() {
                String name = this.get();
                if(!name.isEmpty()) {
                    NConfiguration.GoatsHerd gh = new NConfiguration.GoatsHerd();
                    NConfiguration.getInstance().goatsHerd.put(this.get(),gh);
                    totalAdult.setVal(gh.adultGoats);
                    gap.setVal(gh.breedingGap);
                    coverbreed.setVal(gh.coverbreed);
                    meatQuality.setVal(gh.meatq);
                    milkQuality.setVal(gh.milkq);
                    woolQuality.setVal(gh.woolq);
                    hideQuality.setVal(gh.hideq);
                    meatq1.setVal(gh.meatquan1);
                    meatqth.setVal(gh.meatquanth);
                    meatq2.setVal(gh.meatquan2);
                    milk1.setVal(gh.milkquan1);
                    milkth.setVal(gh.milkquanth);
                    milk2.setVal(gh.milkquan2);
                    wool1.setVal(gh.woolquan1);
                    woolth.setVal(gh.woolquanth);
                    wool2.setVal(gh.woolquan2);
                    ic.set(gh.ignoreChildren);
                    dk.set(gh.disable_killing);
                }
            }

            @Override
            public void nchange() {
                NConfiguration.getInstance().selected_goatsHerd = this.get();
                NConfiguration.GoatsHerd gh = NConfiguration.getInstance().goatsHerd.get(NConfiguration.getInstance().selected_goatsHerd);
                totalAdult.setVal(gh.adultGoats);
                gap.setVal(gh.breedingGap);
                coverbreed.setVal(gh.coverbreed);
                meatQuality.setVal(gh.meatq);
                milkQuality.setVal(gh.milkq);
                woolQuality.setVal(gh.woolq);
                hideQuality.setVal(gh.hideq);
                meatq1.setVal(gh.meatquan1);
                meatqth.setVal(gh.meatquanth);
                meatq2.setVal(gh.meatquan2);
                milk1.setVal(gh.milkquan1);
                milkth.setVal(gh.milkquanth);
                milk2.setVal(gh.milkquan2);
                wool1.setVal(gh.woolquan1);
                woolth.setVal(gh.woolquanth);
                wool2.setVal(gh.woolquan2);
                ic.set(gh.ignoreChildren);
                dk.set(gh.disable_killing);
            }

            @Override
            public void ndelete() {
                NConfiguration.getInstance().goatsHerd.remove(NConfiguration.getInstance().selected_goatsHerd);
                if(!NConfiguration.getInstance().goatsHerd.isEmpty())
                {
                    NConfiguration.getInstance().selected_goatsHerd = (new ArrayList<>(NConfiguration.getInstance().goatsHerd.keySet())).get(0);
                    update(NConfiguration.getInstance().selected_goatsHerd);
                }
                else
                {
                    NConfiguration.getInstance().selected_goatsHerd = "";
                    update("");
                }
            }
        });
        prev = add(new Label("Main settings:"), prev.pos("bl").add(0, 5));
         ic = (CheckBox)(prev = add (new CheckBox("Save cubs"){
            @Override
            public void changed(boolean val) {
                if(!NConfiguration.getInstance().selected_goatsHerd.isEmpty())
                    NConfiguration.getInstance().goatsHerd.get(NConfiguration.getInstance().selected_goatsHerd).ignoreChildren = val;
            }
        }, prev.pos("bl").add(0, 5)));

        if(!NConfiguration.getInstance().selected_goatsHerd.isEmpty()) {
            ic.set(NConfiguration.getInstance().goatsHerd.get(NConfiguration.getInstance().selected_goatsHerd).ignoreChildren);
        }
        dk = (CheckBox)(prev = add (new CheckBox("Disable slaughting"){
            @Override
            public void changed(boolean val) {
                if(!NConfiguration.getInstance().selected_goatsHerd.isEmpty()) {
                    NConfiguration.getInstance().goatsHerd.get(NConfiguration.getInstance().selected_goatsHerd).disable_killing = val;
                }
            }
        }, prev.pos("bl").add(0, 5)));
        if(!NConfiguration.getInstance().selected_goatsHerd.isEmpty()) {
            dk.set(NConfiguration.getInstance().goatsHerd.get(NConfiguration.getInstance().selected_goatsHerd).disable_killing);
        }

        prev = totalAdult = add(new NSettinsSetI("Total adult:"), prev.pos("bl").add(0, 5));
        prev = gap = add(new NSettinsSetI("Gap:"), prev.pos("bl").add(0, 5));
        prev = coverbreed = add(new NSettinsSetD("Overbreed ( 0.0 ... 0.3 ):"), prev.pos("bl").add(0, 5));
        prev = add(new Label("Rank settings:"), prev.pos("bl").add(0, 15));
        prev = add(new Label("All coefficients are arbitrary, only relations between them matters."), prev.pos("bl").add(0, 5));
        prev = meatQuality = add(new NSettinsSetD("Meat:"), prev.pos("bl").add(0, 5));
        prev = milkQuality = add(new NSettinsSetD("Milk:"), prev.pos("bl").add(0, 5));
        prev = woolQuality = add(new NSettinsSetD("Wool:"), prev.pos("bl").add(0, 5));
        prev = hideQuality = add(new NSettinsSetD("Hide:"), prev.pos("bl").add(0, 5));
        prev = add(new Label("Follow stats may be tracked with different coefficients below and above threshold. If you want to ignore threshold, set both coefficients equal."), prev.pos("bl").add(0, 5));
        prev = add(new Label("If you want to track stat up to threshold, but ignore stat gain over threshold simply set second coefficient to zero."), prev.pos("bl").add(0, 5));
        prev = meatq1 = add(new NSettinsSetD("Meat quantity 1:"), prev.pos("bl").add(0, 5));
        prev = meatqth = add(new NSettinsSetD("Meat quantity threshold:"), prev.pos("bl").add(0, 5));
        prev = meatq2 = add(new NSettinsSetD("Meat quantity 2:"), prev.pos("bl").add(0, 5));
        prev = milk1 = add(new NSettinsSetD("Milk quantity 1:"), prev.pos("bl").add(0, 5));
        prev = milkth = add(new NSettinsSetD("Milk quantity threshold:"), prev.pos("bl").add(0, 5));
        prev = milk2 = add(new NSettinsSetD("Milk quantity 2:"), prev.pos("bl").add(0, 5));
        prev = wool1 = add(new NSettinsSetD("Wool quantity 1:"), prev.pos("bl").add(0, 5));
        prev = woolth = add(new NSettinsSetD("Wool quantity threshold:"), prev.pos("bl").add(0, 5));
        prev = wool2 = add(new NSettinsSetD("Wool quantity 2:"), prev.pos("bl").add(0, 5));

        pack();
    }

    @Override
    public void show() {
        els.update(NConfiguration.getInstance().selected_goatsHerd);
        super.show();
    }
}
