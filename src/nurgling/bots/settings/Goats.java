package nurgling.bots.settings;

import haven.Button;
import haven.Label;
import haven.TextEntry;
import haven.Widget;
import nurgling.NConfiguration;
import nurgling.NGob;
import nurgling.tools.AreasID;

import static haven.res.lib.itemtex.ItemTex.made_str;

public class Goats extends Settings {
    TextEntry totalGoats;
    TextEntry totalAdult;
    TextEntry breedingGap;
    TextEntry milkQuality;
    TextEntry milkQuantity;
    TextEntry woolQuality;
    TextEntry woolQuantity;
    TextEntry meatQuality;
    TextEntry meatQuantity;
    public Goats(){
        Widget first, second, third;
        prev = add(new Label("Main settings:"));

        prev = first = add(new Label("Total goats(female):"), prev.pos("bl").adds(0, 5));
        second = totalGoats= add(new TextEntry(50,""), first.pos("ur").adds(5, 2));

        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().goatsHerd.totalGoats = Integer.parseInt(totalGoats.text());
            }
        }, second.pos("ur").adds(5, -2));

        prev = first = add(new Label("Breeding Gap:"), first.pos("bl").adds(0, 15));
        second = breedingGap = add(new TextEntry(50,""), second.pos("bl").adds(0, 9));
        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().goatsHerd.breedingGap = Integer.parseInt(breedingGap.text());
            }
        }, third.pos("bl").adds(0, 5));

        prev = first = add(new Label("Total with milk:"), first.pos("bl").adds(0, 15));
        second = totalAdult = add(new TextEntry(50,""), second.pos("bl").adds(0, 9));
        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().goatsHerd.adultGoats = Integer.parseInt(totalAdult.text());
            }
        }, third.pos("bl").adds(0, 5));


        prev = add(new Label("Quality constants:"), prev.pos("bl").adds(0, 25));

        prev = first = add(new Label("Milk quality:"), prev.pos("bl").adds(0, 5));
        second = milkQuality = add(new TextEntry(50,""), first.pos("ur").adds(15, 2));
        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().goatsHerd.milkq = Double.parseDouble(milkQuality.text());
            }
        }, second.pos("ur").adds(5, -2));

        prev = first = add(new Label("Milk Quantity:"), first.pos("bl").adds(0, 15));
        second = milkQuantity = add(new TextEntry(50,""), second.pos("bl").adds(0, 9));
        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().goatsHerd.milkquan = Double.parseDouble(milkQuantity.text());
            }
        }, third.pos("bl").adds(0, 5));

        prev = first = add(new Label("Wool Quality:"), first.pos("bl").adds(0, 15));
        second = woolQuality = add(new TextEntry(50,""), second.pos("bl").adds(0, 9));
        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().goatsHerd.woolq = Double.parseDouble(woolQuality.text());
            }
        }, third.pos("bl").adds(0, 5));

        prev = first = add(new Label("Wool Quantity:"), first.pos("bl").adds(0, 15));
        second = woolQuantity = add(new TextEntry(50,""), second.pos("bl").adds(0, 9));
        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().goatsHerd.woolquan = Double.parseDouble(woolQuantity.text());
            }
        }, third.pos("bl").adds(0, 5));

        prev = first = add(new Label("Meat Quality:"), first.pos("bl").adds(0, 15));
        second = meatQuality = add(new TextEntry(50,""), second.pos("bl").adds(0, 9));
        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().goatsHerd.meatq = Double.parseDouble(meatQuality.text());
            }
        }, third.pos("bl").adds(0, 5));

        prev = first = add(new Label("Meat Quantity:"), first.pos("bl").adds(0, 15));
        second = meatQuantity = add(new TextEntry(50,""), second.pos("bl").adds(0, 9));
        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().goatsHerd.meatquan = Double.parseDouble(meatQuantity.text());
            }
        }, third.pos("bl").adds(0, 5));
        pack();
    }

    @Override
    public void show() {

        if(meatQuantity!=null) {
            totalGoats.settext(String.valueOf(NConfiguration.getInstance().goatsHerd.totalGoats));
            totalAdult.settext(String.valueOf(NConfiguration.getInstance().goatsHerd.adultGoats));
            breedingGap.settext(String.valueOf(NConfiguration.getInstance().goatsHerd.breedingGap));
            milkQuality.settext(String.valueOf(NConfiguration.getInstance().goatsHerd.milkq));
            milkQuantity.settext(String.valueOf(NConfiguration.getInstance().goatsHerd.milkquan));
            meatQuality.settext(String.valueOf(NConfiguration.getInstance().goatsHerd.meatq));
            meatQuantity.settext(String.valueOf(NConfiguration.getInstance().goatsHerd.meatquan));
            woolQuality.settext(String.valueOf(NConfiguration.getInstance().goatsHerd.woolq));
            woolQuantity.settext(String.valueOf(NConfiguration.getInstance().goatsHerd.woolquan));
        }
        super.show();
    }
}
