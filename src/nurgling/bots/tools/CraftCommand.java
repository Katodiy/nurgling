package nurgling.bots.tools;

import nurgling.NAlias;
import nurgling.tools.NArea;

import java.util.ArrayList;
import java.util.HashMap;

public class CraftCommand {
    public char[] command = null;
    public ArrayList<Ingredient> ingredients = new ArrayList<>();
    public HashMap<Ingredient , Integer> ing_count = new HashMap<>();
    public HashMap<Ingredient , NArea> spec_in_area = new HashMap<>();
    public String name;
    public NAlias special_command = null;
    public ArrayList<NAlias> barrels = null;
    public boolean withPepper = false;
}
