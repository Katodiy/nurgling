package nurgling;

import haven.*;

import java.awt.*;
import java.awt.image.BufferedImage;

public class NGItem extends GItem {
    public double quantity = -1;
    public double wear;
    public int d;
    public int m;
    public NGItem(Indir<Resource> res, Message sdt) {
        super(res, sdt);
    }

    public ItemInfo getInfo(Class <? extends ItemInfo> candidate){
        for(ItemInfo inf : info){
            if(inf.getClass() == candidate){
                return inf;
            }
        }
        return null;
    }


    public interface DoubleInfo extends OverlayInfo<Tex> {


        public double value();

        public default Tex overlay() {
            return(new TexI(RichText.render(String.format("$col[225,255,125]{%.2f}", value()),0).img));
        }

        public default void drawoverlay(GOut g, Tex tex) {
            g.aimage(tex, g.sz(), 1, 1);
        }

        public static BufferedImage doublerender(double value, Color col) {
            return(Utils.outline2(Text.render(Double.toString(value), col).img, Utils.contrast(col)));
        }
    }
    public static class Quantity extends ItemInfo implements DoubleInfo {
        private final double quantity;

        public Quantity(Owner owner, double quantity) {
            super(owner);
            this.quantity = quantity;
        }

        @Override
        public double value() {
            return quantity;
        }

        @Override
        public Tex overlay() {
            return DoubleInfo.super.overlay();
        }

        @Override
        public void drawoverlay(GOut g, Tex tex) {
            g.chcolor(new Color(0, 0, 0, 75));
            g.frect(g.sz().sub(tex.sz().x+2, tex.sz().y), tex.sz());
            g.chcolor();
            g.aimage(tex, g.sz(), 1, 1);
        }
    }
}
