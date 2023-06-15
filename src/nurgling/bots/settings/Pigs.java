package nurgling.bots.settings;

import haven.CheckBox;
import haven.Label;
import nurgling.NConfiguration;
import nurgling.NEntryListSet;
import nurgling.NSettinsSetD;
import nurgling.NSettinsSetI;

import java.util.ArrayList;

public class Pigs extends Settings {
    NEntryListSet els;
    NSettinsSetI totalAdult;
    NSettinsSetI gap;
    NSettinsSetD meatQuality;
    NSettinsSetD hideQuality;
    NSettinsSetD meatq1;
    NSettinsSetD meatq2;
    NSettinsSetD meatqth;
    NSettinsSetD truf1;
    NSettinsSetD truf2;
    NSettinsSetD trufth;
    NSettinsSetD coverbreed;

    CheckBox ic;
    CheckBox dk;
    public Pigs() {
        prev = els = add(new NEntryListSet(NConfiguration.getInstance().pigsHerd.keySet()) {
            @Override
            public void nsave() {
                String name = this.get();
                if(!name.isEmpty()) {
                    NConfiguration.PigsHerd gh = new NConfiguration.PigsHerd();
                    NConfiguration.getInstance().pigsHerd.put(this.get(),gh);
                    totalAdult.setVal(gh.adultPigs);
                    gap.setVal(gh.breedingGap);
                    coverbreed.setVal(gh.coverbreed);
                    meatQuality.setVal(gh.meatq);
                    hideQuality.setVal(gh.hideq);
                    meatq1.setVal(gh.meatquan1);
                    meatqth.setVal(gh.meatquanth);
                    meatq2.setVal(gh.meatquan2);
                    truf1.setVal(gh.trufquan1);
                    trufth.setVal(gh.trufquanth);
                    truf2.setVal(gh.trufquan2);
                    ic.set(gh.ignoreChildren);
                    dk.set(gh.disable_killing);
                }
            }

            @Override
            public void nchange() {
                NConfiguration.getInstance().selected_pigsHerd = this.get();
                NConfiguration.PigsHerd gh = NConfiguration.getInstance().pigsHerd.get(NConfiguration.getInstance().selected_pigsHerd);
                totalAdult.setVal(gh.adultPigs);
                gap.setVal(gh.breedingGap);
                coverbreed.setVal(gh.coverbreed);
                meatQuality.setVal(gh.meatq);
                hideQuality.setVal(gh.hideq);
                meatq1.setVal(gh.meatquan1);
                meatqth.setVal(gh.meatquanth);
                meatq2.setVal(gh.meatquan2);
                truf1.setVal(gh.trufquan1);
                trufth.setVal(gh.trufquanth);
                truf2.setVal(gh.trufquan2);
            }

            @Override
            public void ndelete() {
                NConfiguration.getInstance().pigsHerd.remove(NConfiguration.getInstance().selected_pigsHerd);
                if(!NConfiguration.getInstance().pigsHerd.isEmpty())
                {
                    NConfiguration.getInstance().selected_pigsHerd = (new ArrayList<>(NConfiguration.getInstance().pigsHerd.keySet())).get(0);
                    update(NConfiguration.getInstance().selected_pigsHerd);
                }
                else
                {
                    NConfiguration.getInstance().selected_pigsHerd = "";
                    update("");
                }
            }
        });
        prev = add(new Label("Main settings:"), prev.pos("bl").add(0, 5));
        ic = (CheckBox)(prev = add (new CheckBox("Save cubs"){
            @Override
            public void changed(boolean val) {
                if(!NConfiguration.getInstance().selected_pigsHerd.isEmpty())
                    NConfiguration.getInstance().pigsHerd.get(NConfiguration.getInstance().selected_pigsHerd).ignoreChildren = val;
            }
        }, prev.pos("bl").add(0, 5)));

        if(!NConfiguration.getInstance().selected_pigsHerd.isEmpty()) {
            ic.set(NConfiguration.getInstance().pigsHerd.get(NConfiguration.getInstance().selected_pigsHerd).ignoreChildren);
        }
        dk = (CheckBox)(prev = add (new CheckBox("Disable slaughting"){
            @Override
            public void changed(boolean val) {
                if(!NConfiguration.getInstance().selected_pigsHerd.isEmpty()) {
                    NConfiguration.getInstance().pigsHerd.get(NConfiguration.getInstance().selected_pigsHerd).disable_killing = val;
                }
            }
        }, prev.pos("bl").add(0, 5)));
        if(!NConfiguration.getInstance().selected_pigsHerd.isEmpty()) {
            dk.set(NConfiguration.getInstance().pigsHerd.get(NConfiguration.getInstance().selected_pigsHerd).disable_killing);
        }

        prev = totalAdult = add(new NSettinsSetI("Total adult:"), prev.pos("bl").add(0, 5));
        prev = gap = add(new NSettinsSetI("Gap:"), prev.pos("bl").add(0, 5));
        prev = coverbreed = add(new NSettinsSetD("Overbreed ( 0.0 ... 0.3 ):"), prev.pos("bl").add(0, 5));
        prev = add(new Label("Rank settings:"), prev.pos("bl").add(0, 15));
        prev = add(new Label("All coefficients are arbitrary, only relations between them matters."), prev.pos("bl").add(0, 5));
        prev = meatQuality = add(new NSettinsSetD("Meat:"), prev.pos("bl").add(0, 5));
        prev = hideQuality = add(new NSettinsSetD("Hide:"), prev.pos("bl").add(0, 5));
        prev = add(new Label("Follow stats may be tracked with different coefficients below and above threshold. If you want to ignore threshold, set both coefficients equal."), prev.pos("bl").add(0, 5));
        prev = add(new Label("If you want to track stat up to threshold, but ignore stat gain over threshold simply set second coefficient to zero."), prev.pos("bl").add(0, 5));
        prev = meatq1 = add(new NSettinsSetD("Meat quantity 1:"), prev.pos("bl").add(0, 5));
        prev = meatqth = add(new NSettinsSetD("Meat quantity threshold:"), prev.pos("bl").add(0, 5));
        prev = meatq2 = add(new NSettinsSetD("Meat quantity 2:"), prev.pos("bl").add(0, 5));
        prev = truf1 = add(new NSettinsSetD("Truffle snout 1:"), prev.pos("bl").add(0, 5));
        prev = trufth = add(new NSettinsSetD("Truffle snout threshold:"), prev.pos("bl").add(0, 5));
        prev = truf2 = add(new NSettinsSetD("Truffle snout 2:"), prev.pos("bl").add(0, 5));

        pack();
    }

    @Override
    public void show() {
        els.update(NConfiguration.getInstance().selected_pigsHerd);
        super.show();
    }
}
