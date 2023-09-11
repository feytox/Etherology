package ru.feytox.etherology.magic.staff;

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

    @Override
    public String getName() {
        return name().toLowerCase();
    }
}
