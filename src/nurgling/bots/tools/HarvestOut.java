package nurgling.bots.tools;

import nurgling.NAlias;
import nurgling.tools.AreasID;

public class HarvestOut {
    public NAlias items;
    public AreasID outArea;
    
    public HarvestOut(
            NAlias items,
            AreasID outArea
    ) {
        this.items = items;
        this.outArea = outArea;
    }
}
