package com.qiuyue.goetyominous.client.rlyeh.ac;

import net.minecraft.network.chat.Style;

public final class TextThemes {

    public static final float NONE = -1.0F;

    private TextThemes() {
    }

    public static float themeOf(Style style) {
        if (ExtinctionFont.isExtinctionFont(style.getFont()) || ExtinctionFont.isAshMark(style)) {
            return RlyehShaders.EXTINCTION_THEME;
        }
        if (RlyehFont.isRlyehFont(style.getFont()) || RlyehFont.isAbyssMark(style)) {
            return RlyehShaders.RLYEH_THEME;
        }
        return NONE;
    }
}
