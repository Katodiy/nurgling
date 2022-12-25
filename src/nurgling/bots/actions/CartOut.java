package nurgling.bots.actions;

import haven.Gob;
import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;
import nurgling.PathFinder;
import nurgling.tools.Finder;
import nurgling.tools.NArea;

import java.util.ArrayList;

public class CartOut implements Action {
    public CartOut(NArea input_area, NArea output_area) {
        this.input_area = input_area;
        this.output_area = output_area;
    }

    @Override
    public Results run(NGameUI gui) throws InterruptedException {
        Gob cart = Finder.findObjectInArea(new NAlias("cart"),1000, input_area);
        int n = 2;
        for(int i = 0; i < 6; i++){
            n = n<<1;
            if((cart.getModelAttribute() & n) !=0){
                new PathFinder(gui,cart).run();
                NUtils.activate(cart, i+2);
                NUtils.waitEvent(()->NUtils.isPose(gui.map.player(),new NAlias("banzai")),200);
                NUtils.waitEvent(()->Finder.findLifted()!=null,200);
                Gob lifted = Finder.findLifted();
                new PlaceLifted(output_area,lifted.getHitBox(),lifted).run(gui);
            }
        }
        return new Results(Results.Types.SUCCESS);
    }

    NArea input_area;
    NArea output_area;
}
