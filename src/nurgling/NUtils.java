package nurgling;

import haven.ChatUI;

public class NUtils {
    static NGameUI gameUI;
    static NUI nui;

    static public NGameUI getGameUI() {
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

    public static boolean checkName(
            final String name,
            final String... args
    ) {
        return checkName(name, new NAlias(args));
    }

    public static boolean checkName(
            final String name,
            final NAlias regEx
    ) {
        if (regEx != null) {
            /// Проверяем имя на соответствие
            for (String key : regEx.keys) {
                if (name.contains(key)) {
                    for (String ex : regEx.exceptions) {
                        if (name.contains(ex)) {
                            return false;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

}
