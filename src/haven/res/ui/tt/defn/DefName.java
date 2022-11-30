/* Preprocessed source code */
package haven.res.ui.tt.defn;

import haven.*;

@FromResource(name = "ui/tt/defn", version = 5)
public class DefName implements ItemInfo.InfoFactory {
    public ItemInfo build(ItemInfo.Owner owner, Object... args) {
	if(owner instanceof ItemInfo.SpriteOwner) {
	    GSprite spr = ((ItemInfo.SpriteOwner)owner).sprite();
	    if(spr instanceof DynName)
			return(new ItemInfo.Name(owner, ((DynName)spr).name()));
	}
	if(!(owner instanceof ItemInfo.ResOwner))
	    return(null);
	Resource res = ((ItemInfo.ResOwner)owner).resource();
	Resource.Tooltip tt = res.layer(Resource.tooltip);
	if(tt == null)
	    throw(new RuntimeException("Item resource " + res + " is missing default tooltip"));
	return(new ItemInfo.Name(owner, tt.t));
    }
}
