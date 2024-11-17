package ru.feytox.etherology.block.sedimentary;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.MathHelper;

public enum EssenceLevel implements StringIdentifiable {
    EMPTY,
    LOW,
    MEDIUM,
    HIGH,
    FULL;

    public boolean isPresent() {
        return !this.equals(EMPTY);
    }

    @Override
    public String asString() {
        return name().toLowerCase();
    }

    public float toFullness() {
        return ordinal() / (values().length - 1f);
    }

    public static float getFullnessDelta() {
        return 1f / (values().length - 1);
    }

    public static EssenceLevel fromFullness(float fullness) {
        var delta = getFullnessDelta();
        var index = MathHelper.floor(fullness / delta);
        return values()[index];
    }
}
