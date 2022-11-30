package nurgling;

import haven.Button;
import haven.Coord;
import haven.GOut;
import haven.Widget;
import haven.Label;

import javax.swing.*;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NColorWidget extends Widget
{
    NColorButton cb;
    Label label;

    public NColorWidget(String text, String key){
        cb = new NColorButton(key,24,24);
        label = new Label(text);
        add(label, new Coord(0,4));
        add(cb, new Coord(100,0));
        pack();
    }
    public class NColorButton extends Button {
        String clr;
        JColorChooser colorChooser;
        public NColorButton(String key, int w , int h){
            super(w,"");
            sz.y = h;
            this.clr = key;
            this.colorChooser = new JColorChooser();
            final AbstractColorChooserPanel[] panels = colorChooser.getChooserPanels();
            for (final AbstractColorChooserPanel accp : panels) {
                if (!accp.getDisplayName().equals("RGB")) {
                    colorChooser.removeChooserPanel(accp);
                }
            }
            colorChooser.setPreviewPanel(new JPanel());
            colorChooser.setColor(NConfiguration.getInstance().colors.get(clr));
        }

        @Override
        public void draw(GOut g) {
            int delta = 2;
            Coord size = new Coord(sz.x-2*delta,sz.y-2*delta);
            g.chcolor(NConfiguration.getInstance().colors.get(clr));
            g.frect(new Coord(delta,  delta), size);
            g.chcolor();
            g.chcolor(Color.BLACK);
            g.frect(new Coord(0,0), new Coord(sz.x,delta));
            g.frect(new Coord(0,sz.y-delta), new Coord(sz.x,delta));
            g.frect(new Coord(0,delta), new Coord(delta,size.y));
            g.frect(new Coord(sz.x-delta,delta), new Coord(delta,size.y));
            g.chcolor();
        }

        @Override
        public void click() {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    float old = NUtils.getUI().gprefs.bghz.val;
                    NUtils.getUI().gprefs.bghz.val = NUtils.getUI().gprefs.hz.val;
                    JDialog chooser = JColorChooser.createDialog(null, "SelectColor", true, colorChooser, new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            NConfiguration.getInstance().colors.put(clr,colorChooser.getColor());
                        }
                    }, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {

                        }
                    });
                    chooser.setVisible(true);
                    NUtils.getUI().gprefs.bghz.val= old;
                }
            }).start();

        }
    }
}
