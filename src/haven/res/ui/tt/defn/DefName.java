/* Preprocessed source code */
package haven.res.ui.tt.defn;

import haven.*;

@haven.FromResource(name = "ui/tt/defn", version = 6)
public class DefName implements ItemInfo.InfoFactory {
    public static String getname(ItemInfo.Owner owner) {
	if(owner instanceof ItemInfo.SpriteOwner) {
	    GSprite spr = ((ItemInfo.SpriteOwner)owner).sprite();
	    if(spr instanceof DynName)
		return(((DynName)spr).name());
	}
	if(!(owner instanceof ItemInfo.ResOwner))
	    return(null);
	Resource res = ((ItemInfo.ResOwner)owner).resource();
	Resource.Tooltip tt = res.layer(Resource.tooltip);
	if(tt == null)
	    throw(new RuntimeException("Item resource " + res + " is missing default tooltip"));
	return(tt.t);
    }

    public ItemInfo build(ItemInfo.Owner owner, ItemInfo.Raw raw, Object... args) {
	String nm = getname(owner);
	if(nm == null)
	    return(null);
	return(new ItemInfo.Name(owner, nm));
    }
}
