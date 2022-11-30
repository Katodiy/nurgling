package nurgling.bots.tools;

import nurgling.tools.AreasID;

public class AItem {

    public AItem(AreasID area_out, AreasID barter_out, AreasID area_in, AreasID barter_in) {
        this.area_out = area_out;
        this.barter_out = barter_out;
        this.area_in = area_in;
        this.barter_in = barter_in;
    }

    public AItem() {
    }

    public AreasID area_out = AreasID.no_area;
    public AreasID barter_out = AreasID.no_area;
    public AreasID area_in = AreasID.no_area;
    public AreasID barter_in = AreasID.no_area;
}
