package ru.feytox.etherology.util.misc;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

public class FeyColor {

    // TODO: 08.02.2024 move to RGBColor + maybe RGBColor -> hex

    public static RGBColor lerp(RGBColor startColor, RGBColor endColor, float percent) {
        return new RGBColor(lerpComponent(startColor.r(), endColor.r(), percent), lerpComponent(startColor.g(), endColor.g(), percent), lerpComponent(startColor.b(), endColor.b(), percent));
    }

    public static RGBColor getRandomColor(RGBColor startColor, RGBColor endColor, Random random) {
        return getGradientColor(startColor, endColor, random.nextFloat());
    }

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

    private static int lerpComponent(int start, int end, float percent) {
        return Math.round(MathHelper.lerp(percent, start, end));
    }
}
