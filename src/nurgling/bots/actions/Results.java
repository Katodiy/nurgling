package nurgling.bots.actions;

import nurgling.NUtils;

public class Results {
    Results(Types type ) {
        if ( type != Types.SUCCESS ) {
            NUtils.getGameUI ().msg ( type.toString () );
        }
        this.type = type;
    }
    
    public enum Types {
        SUCCESS, GO_FAIL, DROP_FAIL, WAIT_FAIL, FAIL, Drink_FAIL, SELECT_FLOWER_FAIL, FILL_FAIL, OPEN_FAIL, NO_FUEL,
        FULL, NO_ITEMS, NO_BELT, NO_FREE_SPACE, NO_WORKSTATION, INGREDIENTS_NOT_FOUND, NO_PLACE, NO_CONTAINER,
        BELT_FAIL, NO_BARTER, NO_START_OBJECT, NO_BRAZIER, FIGHT, LOW_COLUMN_HP, NO_PIG, NO_TOOLS
    }
    
    public Types type;
}
