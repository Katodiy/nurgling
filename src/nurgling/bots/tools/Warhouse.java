package nurgling.bots.tools;

import nurgling.NAlias;
import nurgling.tools.AreasID;

public class Warhouse {
    public NAlias item;
    public AreasID area = null;
    public AreasID barter = null;
    public boolean isInfo = true;
    

    
    public Warhouse(
            NAlias item,
            AreasID area,
            AreasID barter,
            boolean isInfo
    ) {
        this.item = item;
        this.area = area;
        this.barter = barter;
        this.isInfo = isInfo;
    }
    
  
}
