package nurgling.bots.actions;

import nurgling.NAlias;
import nurgling.NGameUI;
import nurgling.NUtils;

public class Craft implements Action {
    private String craft_name;
    private char[] craftCommand;
    private NAlias special = null;
    
    public Craft (
            String craft_name,
            char[] craftCommand
    ) {
        this.craft_name = craft_name;
        this.craftCommand = craftCommand;
    }
    
    public Craft (
            String craft_name,
            char[] craftCommand,
            NAlias special
    ) {
        this.craft_name = craft_name;
        this.craftCommand = craftCommand;
        this.special = special;
    }
    
    
    @Override
    public Results run ( NGameUI gui )
            throws InterruptedException {
        if(NUtils.getStamina ()<0.35)
            new Drink ( 0.9,false ).run ( gui );
        
        if ( special == null ) {
            NUtils.craft ( craftCommand, craft_name, false );
        }
        else {
            NUtils.craft ( craftCommand, special, craft_name );
        }
        return new Results ( Results.Types.SUCCESS );
    }
}
