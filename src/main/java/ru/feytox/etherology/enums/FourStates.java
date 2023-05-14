package ru.feytox.etherology.enums;

public enum FourStates {
    FALSE(false, false),
    MUST_FALSE(false, true),
    TRUE(true, false),
    MUST_TRUE(true, true);

    private final boolean value;
    private final boolean modifier;

    FourStates(boolean value, boolean modifier) {
        this.value = value;
        this.modifier = modifier;
    }

    public boolean getValue() {
        return value;
    }

    public boolean getModifier() {
        return modifier;
    }
}
