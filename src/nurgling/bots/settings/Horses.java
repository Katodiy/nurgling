package nurgling.bots.settings;

import haven.CheckBox;
import haven.Label;
import haven.UI;
import nurgling.NConfiguration;
import nurgling.NEntryListSet;
import nurgling.NSettinsSetD;
import nurgling.NSettinsSetI;

import java.util.ArrayList;

public class Horses extends Settings {
    private final CheckBox ignorebd;
    NEntryListSet els;
    NSettinsSetI totalAdult;
    NSettinsSetI gap;
    NSettinsSetI meta;
    NSettinsSetI enduran;
    NSettinsSetD meatQuality;
    NSettinsSetD hideQuality;
    NSettinsSetD meatq1;
    NSettinsSetD meatq2;
    NSettinsSetD meatqth;
    NSettinsSetD stam1;
    NSettinsSetD stam2;
    NSettinsSetD stamth;
    NSettinsSetD coverbreed;

    CheckBox ic;
    CheckBox dk;
    public Horses() {
        prev = els = add(new NEntryListSet(NConfiguration.getInstance().horsesHerd.keySet()) {
            @Override
            public void nsave() {
                String name = this.get();
                if (!name.isEmpty()) {
                    NConfiguration.HorsesHerd gh = new NConfiguration.HorsesHerd();
                    NConfiguration.getInstance().horsesHerd.put(this.get(), gh);
                    totalAdult.setVal(gh.adultHorse);
                    gap.setVal(gh.breedingGap);
                    coverbreed.setVal(gh.coverbreed);
                    meatQuality.setVal(gh.meatq);
                    hideQuality.setVal(gh.hideq);
                    enduran.setVal(gh.enduran);
                    meta.setVal(gh.meta);
                    meatq1.setVal(gh.meatquan1);
                    meatqth.setVal(gh.meatquanth);
                    meatq2.setVal(gh.meatquan2);
                    stam1.setVal(gh.stam1);
                    stamth.setVal(gh.stamth);
                    stam2.setVal(gh.stam2);
                    ic.set(gh.ignoreChildren);
                    dk.set(gh.disable_killing);
                }
            }

            @Override
            public void nchange() {
                NConfiguration.getInstance().selected_horsesHerd = this.get();
                NConfiguration.HorsesHerd gh = NConfiguration.getInstance().horsesHerd.get(NConfiguration.getInstance().selected_horsesHerd);
                totalAdult.setVal(gh.adultHorse);
                gap.setVal(gh.breedingGap);
                coverbreed.setVal(gh.coverbreed);
                meatQuality.setVal(gh.meatq);
                hideQuality.setVal(gh.hideq);
                meatq1.setVal(gh.meatquan1);
                meatqth.setVal(gh.meatquanth);
                meatq2.setVal(gh.meatquan2);
                enduran.setVal(gh.enduran);
                meta.setVal(gh.meta);
                stam1.setVal(gh.stam1);
                stamth.setVal(gh.stamth);
                stam2.setVal(gh.stam2);
            }

            @Override
            public void ndelete() {
                NConfiguration.getInstance().horsesHerd.remove(NConfiguration.getInstance().selected_horsesHerd);
                if (!NConfiguration.getInstance().horsesHerd.isEmpty()) {
                    NConfiguration.getInstance().selected_horsesHerd = (new ArrayList<>(NConfiguration.getInstance().horsesHerd.keySet())).get(0);
                    update(NConfiguration.getInstance().selected_horsesHerd);
                } else {
                    NConfiguration.getInstance().selected_horsesHerd = "";
                    update("");
                }
            }
        });
        prev = add(new Label("Main settings:"), prev.pos("bl").add(0, 5));
        ic = (CheckBox)(prev = add (new CheckBox("Save cubs"){
            @Override
            public void changed(boolean val) {
                if(!NConfiguration.getInstance().selected_horsesHerd.isEmpty())
                    NConfiguration.getInstance().horsesHerd.get(NConfiguration.getInstance().selected_horsesHerd).ignoreChildren = val;
            }
        }, prev.pos("bl").add(0, 5)));

        if(!NConfiguration.getInstance().selected_horsesHerd.isEmpty()) {
            ic.set(NConfiguration.getInstance().horsesHerd.get(NConfiguration.getInstance().selected_horsesHerd).ignoreChildren);
        }
        dk = (CheckBox)(prev = add (new CheckBox("Disable slaughting"){
            @Override
            public void changed(boolean val) {
                if(!NConfiguration.getInstance().selected_horsesHerd.isEmpty()) {
                    NConfiguration.getInstance().horsesHerd.get(NConfiguration.getInstance().selected_horsesHerd).disable_killing = val;
                }
            }
        }, prev.pos("bl").add(0, 5)));
        if(!NConfiguration.getInstance().selected_horsesHerd.isEmpty()) {
            dk.set(NConfiguration.getInstance().horsesHerd.get(NConfiguration.getInstance().selected_horsesHerd).disable_killing);
        }

        ignorebd = (CheckBox)(prev = add (new CheckBox("Ignore breading for female"){
            @Override
            public void changed(boolean val) {
                if(!NConfiguration.getInstance().selected_horsesHerd.isEmpty()) {
                    NConfiguration.getInstance().horsesHerd.get(NConfiguration.getInstance().selected_horsesHerd).ignoreBD = val;
                }
            }
        }, prev.pos("bl").add(0, UI.scale(5))));

        if(!NConfiguration.getInstance().selected_horsesHerd.isEmpty()) {
            ignorebd.set(NConfiguration.getInstance().horsesHerd.get(NConfiguration.getInstance().selected_horsesHerd).ignoreBD);
        }

        prev = totalAdult = add(new NSettinsSetI("Total adult:"), prev.pos("bl").add(0, 5));
        prev = gap = add(new NSettinsSetI("Gap:"), prev.pos("bl").add(0, 5));
        prev = coverbreed = add(new NSettinsSetD("Overbreed ( 0.0 ... 0.3 ):"), prev.pos("bl").add(0, 5));
        prev = add(new Label("Rank settings:"), prev.pos("bl").add(0, 15));
        prev = add(new Label("All coefficients are arbitrary, only relations between them matters."), prev.pos("bl").add(0, 5));
        prev = meatQuality = add(new NSettinsSetD("Meat:"), prev.pos("bl").add(0, 5));
        prev = hideQuality = add(new NSettinsSetD("Hide:"), prev.pos("bl").add(0, 5));
        prev = enduran = add(new NSettinsSetI("Endurance:"), prev.pos("bl").add(0, 5));
        prev = meta = add(new NSettinsSetI("Metabolism:"), prev.pos("bl").add(0, 5));
        prev = add(new Label("Follow stats may be tracked with different coefficients below and above threshold. If you want to ignore threshold, set both coefficients equal."), prev.pos("bl").add(0, 5));
        prev = add(new Label("If you want to track stat up to threshold, but ignore stat gain over threshold simply set second coefficient to zero."), prev.pos("bl").add(0, 5));
        prev = meatq1 = add(new NSettinsSetD("Meat quantity 1:"), prev.pos("bl").add(0, 5));
        prev = meatqth = add(new NSettinsSetD("Meat quantity threshold:"), prev.pos("bl").add(0, 5));
        prev = meatq2 = add(new NSettinsSetD("Meat quantity 2:"), prev.pos("bl").add(0, 5));
        prev = stam1 = add(new NSettinsSetD("Stamina 1:"), prev.pos("bl").add(0, 5));
        prev = stamth = add(new NSettinsSetD("Stamina threshold:"), prev.pos("bl").add(0, 5));
        prev = stam2 = add(new NSettinsSetD("Stamina snout 2:"), prev.pos("bl").add(0, 5));

        pack();
    }

    @Override
    public void show() {
        els.update(NConfiguration.getInstance().selected_horsesHerd);
        super.show();
    }
}
