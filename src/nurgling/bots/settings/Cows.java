package nurgling.bots.settings;

import haven.CheckBox;
import haven.Label;
import haven.UI;
import nurgling.NConfiguration;
import nurgling.NEntryListSet;
import nurgling.NSettinsSetD;
import nurgling.NSettinsSetI;

import java.util.ArrayList;

public class Cows extends Settings {
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
    public Cows() {
        prev = els = add(new NEntryListSet(NConfiguration.getInstance().cowsHerd.keySet()) {
            @Override
            public void nsave() {
                String name = this.get();
                if(!name.isEmpty()) {
                    NConfiguration.CowsHerd gh = new NConfiguration.CowsHerd();
                    NConfiguration.getInstance().cowsHerd.put(this.get(),gh);
                    totalAdult.setVal(gh.adultCows);
                    gap.setVal(gh.breedingGap);
                    coverbreed.setVal(gh.coverbreed);
                    meatQuality.setVal(gh.meatq);
                    milkQuality.setVal(gh.milkq);
                    hideQuality.setVal(gh.hideq);
                    meatq1.setVal(gh.meatquan1);
                    meatqth.setVal(gh.meatquanth);
                    meatq2.setVal(gh.meatquan2);
                    milk1.setVal(gh.milkquan1);
                    milkth.setVal(gh.milkquanth);
                    milk2.setVal(gh.milkquan2);
                    ic.set(gh.ignoreChildren);
                    dk.set(gh.disable_killing);
                }
            }

            @Override
            public void nchange() {
                NConfiguration.getInstance().selected_cowsHerd = this.get();
                NConfiguration.CowsHerd gh = NConfiguration.getInstance().cowsHerd.get(NConfiguration.getInstance().selected_cowsHerd);
                totalAdult.setVal(gh.adultCows);
                gap.setVal(gh.breedingGap);
                coverbreed.setVal(gh.coverbreed);
                meatQuality.setVal(gh.meatq);
                milkQuality.setVal(gh.milkq);
                hideQuality.setVal(gh.hideq);
                meatq1.setVal(gh.meatquan1);
                meatqth.setVal(gh.meatquanth);
                meatq2.setVal(gh.meatquan2);
                milk1.setVal(gh.milkquan1);
                milkth.setVal(gh.milkquanth);
                milk2.setVal(gh.milkquan2);
            }

            @Override
            public void ndelete() {
                NConfiguration.getInstance().cowsHerd.remove(NConfiguration.getInstance().selected_cowsHerd);
                if (!NConfiguration.getInstance().cowsHerd.isEmpty()) {
                    NConfiguration.getInstance().selected_cowsHerd = (new ArrayList<>(NConfiguration.getInstance().cowsHerd.keySet())).get(0);
                    update(NConfiguration.getInstance().selected_cowsHerd);
                } else {
                    NConfiguration.getInstance().selected_cowsHerd = "";
                    update("");
                }
            }
        });
        prev = add(new Label("Main settings:"), prev.pos("bl").add(0, 5));
        ic = (CheckBox)(prev = add (new CheckBox("Save cubs"){
            @Override
            public void changed(boolean val) {
                if(!NConfiguration.getInstance().selected_cowsHerd.isEmpty())
                    NConfiguration.getInstance().cowsHerd.get(NConfiguration.getInstance().selected_cowsHerd).ignoreChildren = val;
            }
        }, prev.pos("bl").add(0, UI.scale(5))));

        if(!NConfiguration.getInstance().selected_cowsHerd.isEmpty()) {
            ic.set(NConfiguration.getInstance().cowsHerd.get(NConfiguration.getInstance().selected_cowsHerd).ignoreChildren);
        }
        dk = (CheckBox)(prev = add (new CheckBox("Disable slaughting"){
            @Override
            public void changed(boolean val) {
                if(!NConfiguration.getInstance().selected_cowsHerd.isEmpty()) {
                    NConfiguration.getInstance().cowsHerd.get(NConfiguration.getInstance().selected_cowsHerd).disable_killing = val;
                }
            }
        }, prev.pos("bl").add(0, UI.scale(5))));
        if(!NConfiguration.getInstance().selected_cowsHerd.isEmpty()) {
            dk.set(NConfiguration.getInstance().cowsHerd.get(NConfiguration.getInstance().selected_cowsHerd).disable_killing);
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
        els.update(NConfiguration.getInstance().selected_cowsHerd);
        super.show();
    }
}
