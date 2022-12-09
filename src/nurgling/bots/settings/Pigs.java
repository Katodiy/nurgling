package nurgling.bots.settings;

import haven.Button;
import haven.Label;
import haven.TextEntry;
import haven.Widget;
import nurgling.NConfiguration;

public class Pigs extends Settings {
    TextEntry totalpigs;
    TextEntry breedingGap;
    TextEntry trufSnout;
    TextEntry meatQuality;
    TextEntry meatQuantity;
    public Pigs(){
        Widget first, second, third;
        prev = add(new Label("Main settings:"));

        prev = first = add(new Label("Total pigs(female):"), prev.pos("bl").adds(0, 5));
        second = totalpigs = add(new TextEntry(50,""), first.pos("ur").adds(5, 2));

        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().pigsHerd.totalPigs = Integer.parseInt(totalpigs.text());
            }
        }, second.pos("ur").adds(5, -2));

        prev = first = add(new Label("Breeding Gap:"), first.pos("bl").adds(0, 15));
        second = breedingGap = add(new TextEntry(50,""), second.pos("bl").adds(0, 9));
        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().pigsHerd.breedingGap = Integer.parseInt(breedingGap.text());
            }
        }, third.pos("bl").adds(0, 5));


        prev = add(new Label("Quality constants:"), prev.pos("bl").adds(0, 25));

        prev = first = add(new Label("Truffle Snout:"),prev.pos("bl").adds(0, 5));
        second = trufSnout = add(new TextEntry(50,""), first.pos("ur").adds(15, 2));
        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().pigsHerd.trufSnout = Double.parseDouble(trufSnout.text());
            }
        }, second.pos("ur").adds(5, -2));

        prev = first = add(new Label("Meat Quality:"), first.pos("bl").adds(0, 15));
        second = meatQuality = add(new TextEntry(50,""), second.pos("bl").adds(0, 9));
        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().pigsHerd.meatq = Double.parseDouble(meatQuality.text());
            }
        }, third.pos("bl").adds(0, 5));

        prev = first = add(new Label("Meat Quantity:"), first.pos("bl").adds(0, 15));
        second = meatQuantity = add(new TextEntry(50,""), second.pos("bl").adds(0, 9));
        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().pigsHerd.meatquan = Double.parseDouble(meatQuantity.text());
            }
        }, third.pos("bl").adds(0, 5));
        pack();
    }

    @Override
    public void show() {

        if(meatQuantity!=null) {
            totalpigs.settext(String.valueOf(NConfiguration.getInstance().pigsHerd.totalPigs));
            breedingGap.settext(String.valueOf(NConfiguration.getInstance().pigsHerd.breedingGap));
            meatQuality.settext(String.valueOf(NConfiguration.getInstance().pigsHerd.meatq));
            meatQuantity.settext(String.valueOf(NConfiguration.getInstance().pigsHerd.meatquan));
            trufSnout.settext(String.valueOf(NConfiguration.getInstance().pigsHerd.trufSnout));
        }
        super.show();
    }
}
