package ru.feytox.etherology.magic.staff;

import org.apache.commons.lang3.EnumUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public enum StaffColors implements StaffPattern {
    BLACK,
    BLUE,
    BROWN,
    CYAN,
    GRAY,
    GREEN,
    LIGHT_BLUE,
    LIGHT_GRAY,
    LIME,
    MAGENTA,
    ORANGE,
    PINK,
    PURPLE,
    RED,
    WHITE,
    YELLOW;

    public static final Supplier<List<? extends StaffPattern>> COLORS = StaffPattern.memoize(values());

    @Nullable
    public static StaffColors getFromColorName(String colorName) {
        return EnumUtils.getEnumIgnoreCase(StaffColors.class, colorName, null);
    }

    @Override
    public String getName() {
        return name().toLowerCase();
    }
}
