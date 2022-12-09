package nurgling.bots.settings;

import haven.Button;
import haven.Label;
import haven.TextEntry;
import haven.Widget;
import nurgling.NConfiguration;

public class Sheeps extends Settings {
    TextEntry totalsheeps;
    TextEntry totalAdult;
    TextEntry breedingGap;
    TextEntry milkQuality;
    TextEntry milkQuantity;
    TextEntry woolQuality;
    TextEntry woolQuantity;
    TextEntry meatQuality;
    TextEntry meatQuantity;
    public Sheeps(){
        Widget first, second, third;
        prev = add(new Label("Main settings:"));

        prev = first = add(new Label("Total sheeps(female):"), prev.pos("bl").adds(0, 5));
        second = totalsheeps= add(new TextEntry(50,""), first.pos("ur").adds(5, 2));

        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().sheepsHerd.totalSheeps = Integer.parseInt(totalsheeps.text());
            }
        }, second.pos("ur").adds(5, -2));

        prev = first = add(new Label("Breeding Gap:"), first.pos("bl").adds(0, 15));
        second = breedingGap = add(new TextEntry(50,""), second.pos("bl").adds(0, 9));
        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().sheepsHerd.breedingGap = Integer.parseInt(breedingGap.text());
            }
        }, third.pos("bl").adds(0, 5));

        prev = first = add(new Label("Total with milk:"), first.pos("bl").adds(0, 15));
        second = totalAdult = add(new TextEntry(50,""), second.pos("bl").adds(0, 9));
        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().sheepsHerd.adultSheeps = Integer.parseInt(totalAdult.text());
            }
        }, third.pos("bl").adds(0, 5));

        prev = add(new Label("Quality constants:"), prev.pos("bl").adds(0, 25));

        prev = first = add(new Label("Milk quality:"), prev.pos("bl").adds(0, 5));
        second = milkQuality = add(new TextEntry(50,""), first.pos("ur").adds(15, 2));
        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().sheepsHerd.milkq = Double.parseDouble(milkQuality.text());
            }
        }, second.pos("ur").adds(5, -2));

        prev = first = add(new Label("Milk Quantity:"), first.pos("bl").adds(0, 15));
        second = milkQuantity = add(new TextEntry(50,""), second.pos("bl").adds(0, 9));
        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().sheepsHerd.milkquan = Double.parseDouble(milkQuantity.text());
            }
        }, third.pos("bl").adds(0, 5));

        prev = first = add(new Label("Wool Quality:"), first.pos("bl").adds(0, 15));
        second = woolQuality = add(new TextEntry(50,""), second.pos("bl").adds(0, 9));
        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().sheepsHerd.woolq = Double.parseDouble(woolQuality.text());
            }
        }, third.pos("bl").adds(0, 5));

        prev = first = add(new Label("Wool Quantity:"), first.pos("bl").adds(0, 15));
        second = woolQuantity = add(new TextEntry(50,""), second.pos("bl").adds(0, 9));
        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().sheepsHerd.woolquan = Double.parseDouble(woolQuantity.text());
            }
        }, third.pos("bl").adds(0, 5));

        prev = first = add(new Label("Meat Quality:"), first.pos("bl").adds(0, 15));
        second = meatQuality = add(new TextEntry(50,""), second.pos("bl").adds(0, 9));
        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().sheepsHerd.meatq = Double.parseDouble(meatQuality.text());
            }
        }, third.pos("bl").adds(0, 5));

        prev = first = add(new Label("Meat Quantity:"), first.pos("bl").adds(0, 15));
        second = meatQuantity = add(new TextEntry(50,""), second.pos("bl").adds(0, 9));
        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().sheepsHerd.meatquan = Double.parseDouble(meatQuantity.text());
            }
        }, third.pos("bl").adds(0, 5));
        pack();
    }

    @Override
    public void show() {

        if(meatQuantity!=null) {
            totalsheeps.settext(String.valueOf(NConfiguration.getInstance().sheepsHerd.totalSheeps));
            totalAdult.settext(String.valueOf(NConfiguration.getInstance().sheepsHerd.adultSheeps));
            breedingGap.settext(String.valueOf(NConfiguration.getInstance().sheepsHerd.breedingGap));
            milkQuality.settext(String.valueOf(NConfiguration.getInstance().sheepsHerd.milkq));
            milkQuantity.settext(String.valueOf(NConfiguration.getInstance().sheepsHerd.milkquan));
            meatQuality.settext(String.valueOf(NConfiguration.getInstance().sheepsHerd.meatq));
            meatQuantity.settext(String.valueOf(NConfiguration.getInstance().sheepsHerd.meatquan));
            woolQuality.settext(String.valueOf(NConfiguration.getInstance().sheepsHerd.woolq));
            woolQuantity.settext(String.valueOf(NConfiguration.getInstance().sheepsHerd.woolquan));
        }
        super.show();
    }
}
