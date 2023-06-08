package nurgling.bots.settings;

import haven.Label;
import nurgling.NConfiguration;
import nurgling.NSettinsSetD;
import nurgling.NSettinsSetI;

public class Sheeps extends Settings {

    NSettinsSetI totalsheeps;
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

    public Sheeps() {
        prev = add(new Label("Main settings:"));
        prev = totalsheeps = add(new NSettinsSetI("Total sheeps:", NConfiguration.getInstance().sheepsHerd.totalSheeps), prev.pos("bl").add(0, 5));
        prev = totalAdult = add(new NSettinsSetI("Total adult:", NConfiguration.getInstance().sheepsHerd.adultSheeps), prev.pos("bl").add(0, 5));
        prev = gap = add(new NSettinsSetI("Gap:", NConfiguration.getInstance().sheepsHerd.breedingGap), prev.pos("bl").add(0, 5));
        prev = coverbreed = add(new NSettinsSetD("Breeding cover:", NConfiguration.getInstance().sheepsHerd.coverbreed), prev.pos("bl").add(0, 5));
        prev = add(new Label("Rang settings:"), prev.pos("bl").add(0, 15));
        prev = meatQuality = add(new NSettinsSetD("Meat:", NConfiguration.getInstance().sheepsHerd.meatq), prev.pos("bl").add(0, 5));
        prev = milkQuality = add(new NSettinsSetD("Milk:", NConfiguration.getInstance().sheepsHerd.milkq), prev.pos("bl").add(0, 5));
        prev = woolQuality = add(new NSettinsSetD("Wool:", NConfiguration.getInstance().sheepsHerd.woolq), prev.pos("bl").add(0, 5));
        prev = hideQuality = add(new NSettinsSetD("Hide:", NConfiguration.getInstance().sheepsHerd.hideq), prev.pos("bl").add(0, 5));
        prev = meatq1 = add(new NSettinsSetD("Meat quantity 1:", NConfiguration.getInstance().sheepsHerd.meatquan1), prev.pos("bl").add(0, 5));
        prev = meatqth = add(new NSettinsSetD("Meat quantity TH:", NConfiguration.getInstance().sheepsHerd.meatquanth), prev.pos("bl").add(0, 5));
        prev = meatq2 = add(new NSettinsSetD("Meat quantity 2:", NConfiguration.getInstance().sheepsHerd.meatquan2), prev.pos("bl").add(0, 5));
        prev = milk1 = add(new NSettinsSetD("Milk quantity 1:", NConfiguration.getInstance().sheepsHerd.meatquan1), prev.pos("bl").add(0, 5));
        prev = milkth = add(new NSettinsSetD("Milk quantity TH:", NConfiguration.getInstance().sheepsHerd.meatquanth), prev.pos("bl").add(0, 5));
        prev = milk2 = add(new NSettinsSetD("Milk quantity 2:", NConfiguration.getInstance().sheepsHerd.meatquan2), prev.pos("bl").add(0, 5));
        prev = wool1 = add(new NSettinsSetD("Wool quantity 1:", NConfiguration.getInstance().sheepsHerd.woolquan1), prev.pos("bl").add(0, 5));
        prev = woolth = add(new NSettinsSetD("Wool quantity TH:", NConfiguration.getInstance().sheepsHerd.woolquanth), prev.pos("bl").add(0, 5));
        prev = wool2 = add(new NSettinsSetD("Wool quantity 2:", NConfiguration.getInstance().sheepsHerd.woolquan2), prev.pos("bl").add(0, 5));

        pack();
    }

    @Override
    public void show() {
        totalsheeps.setText(String.valueOf(NConfiguration.getInstance().sheepsHerd.totalSheeps.get()));
        totalAdult.setText(String.valueOf(NConfiguration.getInstance().sheepsHerd.adultSheeps.get()));
        gap.setText(String.valueOf(NConfiguration.getInstance().sheepsHerd.breedingGap.get()));
        coverbreed.setText(String.valueOf(NConfiguration.getInstance().sheepsHerd.coverbreed.get()));
        milkQuality.setText(String.valueOf(NConfiguration.getInstance().sheepsHerd.milkq.get()));
        meatQuality.setText(String.valueOf(NConfiguration.getInstance().sheepsHerd.meatq.get()));
        woolQuality.setText(String.valueOf(NConfiguration.getInstance().sheepsHerd.woolq.get()));
        hideQuality.setText(String.valueOf(NConfiguration.getInstance().sheepsHerd.hideq.get()));
        meatq1.setText(String.valueOf(NConfiguration.getInstance().sheepsHerd.meatquan1.get()));
        meatq2.setText(String.valueOf(NConfiguration.getInstance().sheepsHerd.meatquan2.get()));
        meatqth.setText(String.valueOf(NConfiguration.getInstance().sheepsHerd.meatquanth.get()));
        milk1.setText(String.valueOf(NConfiguration.getInstance().sheepsHerd.milkquan1.get()));
        milk2.setText(String.valueOf(NConfiguration.getInstance().sheepsHerd.milkquan2.get()));
        milkth.setText(String.valueOf(NConfiguration.getInstance().sheepsHerd.milkquanth.get()));
        wool1.setText(String.valueOf(NConfiguration.getInstance().sheepsHerd.woolquan1.get()));
        wool2.setText(String.valueOf(NConfiguration.getInstance().sheepsHerd.woolquan2.get()));
        woolth.setText(String.valueOf(NConfiguration.getInstance().sheepsHerd.woolquanth.get()));
        super.show();
    }
}
