package ru.feytox.etherology.enums;

import net.minecraft.util.math.random.Random;
import ru.feytox.etherology.util.feyapi.FeyColor;
import ru.feytox.etherology.util.feyapi.RGBColor;

public enum LightParticleType {
    SIMPLE(new RGBColor(244, 194, 133)),
    SPARK(null),
    VITAL(null), // TODO: vital
    PUSHING(RGBColor.of(0xA0FF55), RGBColor.of(0x71ED3D)),
    ATTRACT(RGBColor.of(0xCF70FF), RGBColor.of(0xCC3FFF)),
    BREWING(RGBColor.of(0xB668FF), RGBColor.of(0xEC49D9));

    private final RGBColor startColor;

    private final RGBColor endColor;

    LightParticleType(RGBColor color) {
        this(color, color);
    }

    LightParticleType(RGBColor startColor, RGBColor endColor) {
        this.startColor = startColor;
        this.endColor = endColor;
    }

    public RGBColor getColor(Random random) {
        if (startColor.equals(endColor)) return startColor;
        return FeyColor.getRandomColor(startColor, endColor, random);
    }
}
