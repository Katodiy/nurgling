package nurgling;

import haven.*;

public class NGameUI extends GameUI {
    NCharacterInfo charInfo;
    public NGameUI(String chrid, long plid, String genus) {
        super(chrid, plid, genus);
        charInfo = new NCharacterInfo(chrid);
        add(charInfo);
        pack();
        NUtils.setGameUI(this);
    }

    public NCharacterInfo getCharInfo() {
        return charInfo;
    }

    public void addchild(Widget child, Object... args) {
        super.addchild(child,args);
        String place = ((String) args[0]).intern();
        if (place.equals("chr") && chrwdg!=null) {
            charInfo.setCharWnd(chrwdg);
        }
    }
}
