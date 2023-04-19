package haven.res.ui.tt.cn;/* Preprocessed source code */
import haven.*;

import java.awt.*;
import java.awt.image.BufferedImage;

/* >tt: CustName */
@FromResource(name = "ui/tt/cn", version = 4)
public class CustomName extends ItemInfo.Name implements GItem.OverlayInfo<Tex> {
    public float count = -1;

    public CustomName(Owner owner, String str) {
        super(owner, str);
        if (str.contains(" kg ")) {
            count = Float.parseFloat(str.substring(0, str.indexOf(" kg ")));
        }
        else if (str.contains(" l ")) {
            count = Float.parseFloat(str.substring(0, str.indexOf(" l ")));
        }
    }


    @Override
    public Tex overlay() {
        if(count>0) {
            BufferedImage text = RichText.render(String.format("$col[65,255,115]{%.2f}", count), 0).img;
            BufferedImage bi = new BufferedImage(text.getWidth(), text.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = bi.createGraphics();
            Color rgb = new Color(0, 0, 0, 115);
            graphics.setColor(rgb);
            graphics.fillRect(0, 0, bi.getWidth(), bi.getHeight());
            graphics.drawImage(text, 0, 0, null);
            return (new TexI(bi));
        }
        return null;
    }

    @Override
    public void drawoverlay(GOut g, Tex data) {
        if (data != null) {
            g.aimage(data, new Coord(data.sz().x, g.sz().y - data.sz().y), 1, 0);

        }
    }
}
