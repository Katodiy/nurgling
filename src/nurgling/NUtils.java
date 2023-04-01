package nurgling;

public class NUtils {
    static NGameUI gameUI;
    static NUI nui;
    static public NGameUI getGameUI()
    {
        return gameUI;
    }

    public static void setGameUI(NGameUI gameUI) {
        NUtils.gameUI = gameUI;
    }
    public static void setUI(NUI nui) {
        NUtils.nui = nui;
    }
    public static NUI getUI() {
        return nui;
    }

    public static long getTickId() {
        return nui.tickId;
    }


}
