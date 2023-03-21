package ru.feytox.etherology.util.feyapi;

import net.minecraft.util.math.MathHelper;

public class FeyColor {

    public static RGBColor getGradientColor(RGBColor startColor, RGBColor endColor, float percent) {
        int gradientLength = Math.abs(endColor.r() - startColor.r()) + Math.abs(endColor.g() - startColor.g()) + Math.abs(endColor.b() - startColor.b());
        int colorPos = MathHelper.floor((gradientLength + 1f) * percent);
        int r = getColorComponent(startColor.r(), endColor.r(), colorPos);
        int g = getColorComponent(startColor.g(), endColor.g(), colorPos);
        int b = getColorComponent(startColor.b(), endColor.b(), colorPos);
        return new RGBColor(r, g, b);
    }

    private static int getColorComponent(int startValue, int endValue, int pos) {
        if (startValue == endValue) {
            return startValue;
        }
        int delta = endValue - startValue;
        int increment = delta > 0 ? 1 : -1;
        int currentPos = 0;
        int currentValue = startValue;
        while (currentPos < pos && currentValue != endValue) {
            currentPos++;
            currentValue += increment;
        }
        return currentValue;
    }
}
