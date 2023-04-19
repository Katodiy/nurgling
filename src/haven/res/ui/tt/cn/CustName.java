package haven.res.ui.tt.cn;/* Preprocessed source code */
import haven.*;

/* >tt: CustName */
@haven.FromResource(name = "ui/tt/cn", version = 4)
public class CustName implements ItemInfo.InfoFactory {
    public ItemInfo build(ItemInfo.Owner owner, ItemInfo.Raw raw, Object... args) {
        return (new CustomName(owner, (String) args[1]));
    }
}
