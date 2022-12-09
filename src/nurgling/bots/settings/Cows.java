package nurgling.bots.settings;

import haven.Button;
import haven.Label;
import haven.TextEntry;
import haven.Widget;
import nurgling.NConfiguration;

public class Cows extends Settings {
    TextEntry totalcows;
    TextEntry totalAdult;
    TextEntry breedingGap;
    TextEntry milkQuality;
    TextEntry milkQuantity;
    TextEntry meatQuality;
    TextEntry meatQuantity;
    public Cows(){
        Widget first, second, third;
        prev = add(new Label("Main settings:"));

        prev = first = add(new Label("Total cows(female):"), prev.pos("bl").adds(0, 5));
        second = totalcows = add(new TextEntry(50,""), first.pos("ur").adds(5, 2));

        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().cowsHerd.totalCows = Integer.parseInt(totalcows.text());
            }
        }, second.pos("ur").adds(5, -2));

        prev = first = add(new Label("Breeding Gap:"), first.pos("bl").adds(0, 15));
        second = breedingGap = add(new TextEntry(50,""), second.pos("bl").adds(0, 9));
        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().cowsHerd.breedingGap = Integer.parseInt(breedingGap.text());
            }
        }, third.pos("bl").adds(0, 5));

        prev = first = add(new Label("Total with milk:"), first.pos("bl").adds(0, 15));
        second = totalAdult = add(new TextEntry(50,""), second.pos("bl").adds(0, 9));
        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().cowsHerd.adultCows = Integer.parseInt(totalAdult.text());
            }
        }, third.pos("bl").adds(0, 5));

        prev = add(new Label("Quality constants:"), prev.pos("bl").adds(0, 25));

        prev = first = add(new Label("Milk quality:"), prev.pos("bl").adds(0, 5));
        second = milkQuality = add(new TextEntry(50,""), first.pos("ur").adds(15, 2));
        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().cowsHerd.milkq = Double.parseDouble(milkQuality.text());
            }
        }, second.pos("ur").adds(5, -2));

        prev = first = add(new Label("Milk Quantity:"), first.pos("bl").adds(0, 15));
        second = milkQuantity = add(new TextEntry(50,""), second.pos("bl").adds(0, 9));
        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().cowsHerd.milkquan = Double.parseDouble(milkQuantity.text());
            }
        }, third.pos("bl").adds(0, 5));

        prev = first = add(new Label("Meat Quality:"), first.pos("bl").adds(0, 15));
        second = meatQuality = add(new TextEntry(50,""), second.pos("bl").adds(0, 9));
        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().cowsHerd.meatq = Double.parseDouble(meatQuality.text());
            }
        }, third.pos("bl").adds(0, 5));

        prev = first = add(new Label("Meat Quantity:"), first.pos("bl").adds(0, 15));
        second = meatQuantity = add(new TextEntry(50,""), second.pos("bl").adds(0, 9));
        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().cowsHerd.meatquan = Double.parseDouble(meatQuantity.text());
            }
        }, third.pos("bl").adds(0, 5));
        pack();
    }

    @Override
    public void show() {

        if(meatQuantity!=null) {
            totalcows.settext(String.valueOf(NConfiguration.getInstance().cowsHerd.totalCows));
            totalAdult.settext(String.valueOf(NConfiguration.getInstance().cowsHerd.adultCows));
            breedingGap.settext(String.valueOf(NConfiguration.getInstance().cowsHerd.breedingGap));
            milkQuality.settext(String.valueOf(NConfiguration.getInstance().cowsHerd.milkq));
            milkQuantity.settext(String.valueOf(NConfiguration.getInstance().cowsHerd.milkquan));
            meatQuality.settext(String.valueOf(NConfiguration.getInstance().cowsHerd.meatq));
            meatQuantity.settext(String.valueOf(NConfiguration.getInstance().cowsHerd.meatquan));
        }
        super.show();
    }
}
