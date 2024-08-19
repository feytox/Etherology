package ru.feytox.etherology.block.sedimentary;

import net.minecraft.util.StringIdentifiable;

public enum EssenceLevel implements StringIdentifiable {
    EMPTY,
    LOW,
    MEDIUM,
    HIGH,
    FULL;

    public boolean isPresent() {
        return !this.equals(EMPTY);
    }

    public boolean isFull() {
        return this.equals(FULL);
    }

    @Override
    public String asString() {
        return name().toLowerCase();
    }

    public float toFullness() {
        return ordinal() / (values().length - 1f);
    }

    public static EssenceLevel fromFullness(float fullness) {
        if (fullness < 0.25f) return EMPTY;
        if (fullness < 0.50f) return LOW;
        if (fullness < 0.75f) return MEDIUM;
        if (fullness < 1.00f) return HIGH;
        return FULL;
    }
}
