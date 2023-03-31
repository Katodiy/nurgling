package haven.res.ui.tt.curio;
/* Preprocessed source code */
import haven.*;
import haven.resutil.Curiosity;

/* >tt: haven.res.ui.tt.curio.Fac */
@haven.FromResource(name = "ui/tt/curio", version = 8)
public class Fac implements ItemInfo.InfoFactory {
    public ItemInfo build(ItemInfo.Owner owner, ItemInfo.Raw raw, Object... args) {
	int exp = ((Number)args[1]).intValue();
	int mw = ((Number)args[2]).intValue();
	int enc = ((Number)args[3]).intValue();
	int time = ((Number)args[4]).intValue();
	return(new Curiosity(owner, exp, mw, enc, time));
    }
}
