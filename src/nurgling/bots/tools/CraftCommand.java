package nurgling.bots.tools;

import nurgling.NAlias;

import java.util.ArrayList;

public class CraftCommand {
    public char[] command = null;
    public ArrayList<Ingredient> ingredients;
    public String name;
    public NAlias special_command = null;
    public ArrayList<NAlias> barrels = null;
    public boolean withPepper = false;
}
