package nurgling.bots.settings;

import haven.Button;
import haven.Label;
import haven.TextEntry;
import haven.Widget;
import nurgling.NConfiguration;

public class Horses extends Settings {
    TextEntry totalHorses;
    TextEntry endurance;
    TextEntry metabolism;
    public Horses(){
        Widget first, second, third;
        prev = add(new Label("Main settings:"));

        prev = first = add(new Label("Total horses(mare):"), prev.pos("bl").adds(0, 5));
        second = totalHorses = add(new TextEntry(50,""), first.pos("ur").adds(5, 2));

        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().horsesHerd.totalMares = Integer.parseInt(totalHorses.text());
            }
        }, second.pos("ur").adds(5, -2));



        prev = first = add(new Label("Endurance:"),prev.pos("bl").adds(0, 5));
        second = endurance = add(new TextEntry(50,""), first.pos("ur").adds(15, 2));
        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().horsesHerd.endurance = Double.parseDouble(endurance.text());
            }
        }, second.pos("ur").adds(5, -2));

        prev = first = add(new Label("Metabolism:"),prev.pos("bl").adds(0, 5));
        second = metabolism = add(new TextEntry(50,""), first.pos("ur").adds(15, 2));
        third= add(new Button(50,"Set"){
            @Override
            public void click() {
                NConfiguration.getInstance().horsesHerd.metabolism = Double.parseDouble(metabolism.text());
            }
        }, second.pos("ur").adds(5, -2));
        pack();
    }

    @Override
    public void show() {

        if(metabolism!=null) {
            totalHorses.settext(String.valueOf(NConfiguration.getInstance().horsesHerd.totalMares));
            endurance.settext(String.valueOf(NConfiguration.getInstance().horsesHerd.endurance));
            metabolism.settext(String.valueOf(NConfiguration.getInstance().horsesHerd.metabolism));
        }
        super.show();
    }
}
