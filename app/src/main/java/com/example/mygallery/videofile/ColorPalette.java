package com.example.mygallery.videofile;

import androidx.core.graphics.ColorUtils;

public class ColorPalette {

    public static int getTransparentColor(int color, int alpha) {
        return ColorUtils.setAlphaComponent(color, alpha);
    }
}
